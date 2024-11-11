package kr.go.me.egis.egsp.rsync.task;

import com.google.gson.Gson;
import javax.annotation.Resource;
import kr.go.me.egis.egsp.rsync.biz.service.DbExternalService;
import kr.go.me.egis.egsp.rsync.biz.service.DbInternalService;
import kr.go.me.egis.egsp.rsync.biz.service.SyncService;
import kr.go.me.egis.egsp.rsync.biz.vo.*;
import kr.go.me.egis.egsp.rsync.task.engine.thread.Task;

import kr.go.me.egis.egsp.rsync.utils.DateUtils;
import kr.go.me.egis.egsp.rsync.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Component
@Scope("prototype")
@Slf4j
public class SyncTask extends Task {
    @Resource
    private DbInternalService dbInternalService;

    @Resource
    private DbExternalService dbExternalService;

    @Resource
    private SyncService syncService;

    private boolean errorOccur = false;
    private final List<ErrorVO> errorList = new LinkedList<>();
    private ReverseSyncInfoVO reverseSyncInfoVO;
    private InterfaceVO interfaceVO;
    private long syncId;
    private long linkHstId;

    private String interfaceId;
    private String tblName;
    private String extLinkTblName;
    private String linkTblName;
    private String envrTblName;
    private String reversePriorExecQuery;
    private String onConflictQuery;
    private String priorExecQuery;
    private String syncKey;

    @Override
    protected void initFunction() throws Exception {
        String json = getTaskPrmtr();
        try{
            Gson gson = new Gson();
            this.reverseSyncInfoVO = gson.fromJson(json, ReverseSyncInfoVO.class);
            this.syncId = reverseSyncInfoVO.getSyncId();
            this.linkHstId = reverseSyncInfoVO.getLinkHstId();
            this.interfaceId = this.reverseSyncInfoVO.getIntrfcId();
            this.tblName = this.reverseSyncInfoVO.getTableNm();
            if(!this.tblName.contains("_api")){
                this.tblName = this.tblName + "_api";
            }
            this.extLinkTblName = "extlink." + this.tblName;
            this.linkTblName = "link." + this.tblName;
            this.envrTblName = "envr." + this.tblName;
            this.reversePriorExecQuery = this.reverseSyncInfoVO.getPriorExecQuery();
            this.onConflictQuery = this.reverseSyncInfoVO.getOnConflictQuery();
            this.interfaceVO = syncService.selectInterface(interfaceId);
            if(this.interfaceVO == null){
                throw new Exception("인터페이스 없음:" + interfaceId);
            }
            this.syncKey = syncId + "_" + linkHstId;
            this.priorExecQuery = null;
        }catch (Exception e){
            String errorMessage = "태스크 실행 파라미터 정보 없음, " + e.getMessage();
            setError("SyncTask.initFunction", errorMessage);
        }
    }

    @Override
    protected void endFunction() throws Exception {
        //오류정보 정리
        String err = getError();
        if(!StringUtils.isNullOrEmpty(err)){
            reverseSyncInfoVO.setSyncInfo(err);
        }
        syncService.updateReverseSyncInfo(reverseSyncInfoVO);

        if(errorOccur){
            throw new Exception(err);
        }
    }

    /**
     * True 리턴시 Atomic 종료
     * False 리턴시 Atomic 재실행 --> 상태를 이어 받을 수 있어야 한다.
     * 1. 외부망 데이터 수(Count)를 검증한다.
     * 2. 외부망 extlink 데이터를 읽는다.
     * 3.
     */
    @Override
    protected boolean atomicFunction() throws Exception {
        try {
            String beginStr = DateUtils.getCurrentTimestamp();
            reverseSyncInfoVO.setSyncBginDt(beginStr);

            //1. 외부 테이블 Copy 명령어로 파일 생성
            File copyFile = null;
            try {
                CopyVO copyVO = dbExternalService.copyOutWithLinkHstId(extLinkTblName, linkHstId);
                copyFile = copyVO.getCopyFile();
                long extLinkCopyoutCount = copyVO.getExportCount();
                reverseSyncInfoVO.setExtLinkCopyoutCt(extLinkCopyoutCount);
            }catch (Exception e){
                String eMsg = "File creation error! Ext. [extlink]Table by CopyOut: " + e.getMessage();
                throw new Exception(eMsg, e);
            }

            //2. 내부 extlink 스키마 테이블 삭제
            try{
                dbInternalService.truncate(extLinkTblName);
            }catch (Exception e){
                String eMsg = "Delete Int. [extlink]Table: " + e.getMessage();
                throw new Exception(eMsg, e);
            }

            //3. 내부 extlink 스키마 테이블 동기화
            try{
                long intLinkCopyinCount = dbInternalService.copyIn(extLinkTblName, copyFile);
                reverseSyncInfoVO.setIntLinkCopyinCt(intLinkCopyinCount);
            }catch (Exception e){
                String eMsg = "Data insert to Int. [extlink]Table by CopyIn: " + e.getMessage();
                throw new Exception(eMsg, e);
            }

            //4. 내부 envr 스키마에 데이터 저장
            try{
                String loadingTy = interfaceVO.getLoadingTy();
                long intEnvrBeforeRowCount = dbInternalService.getDataCount(envrTblName);
                reverseSyncInfoVO.setIntEnvrBfRowCt(intEnvrBeforeRowCount);

                switch (loadingTy.toUpperCase()){
                    case "DELINS":
                        //truncate
                        dbInternalService.truncate(envrTblName);
                        dbInternalService.selectInsert(extLinkTblName, envrTblName, onConflictQuery);
                        break;
                    case "DELAPD": //아직 특별히 조건을 처리해야하는 경우는 없다.
                    default:
                        String columns = getColumnsWithoutEgspLinkId();
                        dbInternalService.selectInsertColumns(extLinkTblName, envrTblName, columns, onConflictQuery);
                }
                long intEnvrAfterRowCount = dbInternalService.getDataCount(envrTblName);
                reverseSyncInfoVO.setIntEnvrAfRowCt(intEnvrAfterRowCount);
            }catch (Exception e){
                String eMsg = "Data insert to Int. [envr]Table by SelectInsert(Columns): " + e.getMessage();
                throw new Exception(eMsg, e);
            }

            //5. 내/외부 extlink 스키마 테이블 삭제
            try{
                dbInternalService.truncate(extLinkTblName);
                dbExternalService.deleteSyncData(extLinkTblName, reverseSyncInfoVO.getLinkHstId());
            }catch (Exception e){
                String eMsg = "Truncate|Delete Int.,Ext. [extlink]Table: " + e.getMessage();
                throw new Exception(eMsg, e);
            }

            //6. 동기화 정보 추가
            try {
                SyncInfoVO syncInfoVO = new SyncInfoVO();
                syncInfoVO.setIntrfcId(interfaceId);
                syncInfoVO.setSourcFileNm(syncKey);
                syncInfoVO.setTableFullNm(envrTblName);
                String loadingTy = interfaceVO.getLoadingTy();
                if(loadingTy.equalsIgnoreCase("DELINS")){
                    priorExecQuery = "truncate";
                }
                if(priorExecQuery != null)
                    syncInfoVO.setPriorExecQuery(priorExecQuery);

                syncService.insertSyncRequest(syncInfoVO);
            }catch (Exception e){
                String eMsg = "Insert Sync Request [Int-->Ext]: " + e.getMessage();
                throw new Exception(eMsg, e);
            }

            //7. 적재정보 추가
            try {
                syncService.insertLoadingInfo(reverseSyncInfoVO, interfaceVO, syncKey);
            } catch (Exception e) {
                String eMsg = "Insert Loading Info: " + e.getMessage();
                throw new Exception(eMsg, e);
            }

            //8. 공간화정보 추가
            try {
                syncService.insertSpatialRequest(interfaceId, envrTblName, syncKey);
            } catch (Exception e) {
                String eMsg = "Insert Spatialization Request: " + e.getMessage();
                throw new Exception(eMsg, e);
            }

            String endStr = DateUtils.getCurrentTimestamp();
            reverseSyncInfoVO.setSyncEndDt(endStr);
            reverseSyncInfoVO.setSyncSttus("END");
        } catch (Exception e) {
            String errorMessage = String.format("%s.%s ReverseSync Fail!, Err:%s",
                    interfaceId, tblName, e.getMessage());
            setError("SyncTask.atomicFunction", errorMessage);
            reverseSyncInfoVO.setSyncSttus("ERR");
        }
        syncService.updateReverseSyncStatus(reverseSyncInfoVO);
        //[업무가 완료되면 [true]를 리턴한다.
        return true;
    }

    private void setError(String title, String errorMessage){
        this.errorOccur = true;
        ErrorVO errorVO = new ErrorVO();
        errorVO.setTitle(title);
        errorVO.setOccurDate(LocalDateTime.now());
        errorVO.setMessage(errorMessage);
        this.errorList.add(errorVO);
    }

    private String getError(){
        StringBuilder error = new StringBuilder();
        for (ErrorVO errorVO : errorList) {
            String tempError = String.format("%s:%s", errorVO.getTitle(), errorVO.getMessage());
            error.append(tempError).append(",");
        }
        if(error.toString().endsWith(",")){
            return error.substring(0, error.toString().length() - 1);
        }else{
            return error.toString();
        }
    }

    private String getColumnsWithoutEgspLinkId(){
        List<ColumnDescVO> columnDescVOList = dbInternalService.getColumnDescList(extLinkTblName);
        List<String> columns = new LinkedList<>();
        for (ColumnDescVO columnDescVO : columnDescVOList) {
            String columnName = columnDescVO.getColumnName();
            if (columnName.contains("egsp_link_id"))
                continue;
            log.debug("Column Name:{}", columnName);
            columns.add(columnDescVO.getColumnName());
        }
        StringBuilder columnStatement = new StringBuilder();
        for (int i = 0; i < columns.size(); i++) {
            if (i > 0) {
                columnStatement.append(',');
            }
            columnStatement.append(columns.get(i));
        }

        return columnStatement.toString();
    }
}
