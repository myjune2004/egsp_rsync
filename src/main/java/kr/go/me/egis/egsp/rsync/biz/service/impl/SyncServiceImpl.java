package kr.go.me.egis.egsp.rsync.biz.service.impl;

import com.google.gson.Gson;
import jakarta.annotation.Resource;
import kr.go.me.egis.egsp.rsync.biz.dao.InterfaceDAO;
import kr.go.me.egis.egsp.rsync.biz.dao.SyncExtDAO;
import kr.go.me.egis.egsp.rsync.biz.dao.SyncIntDAO;
import kr.go.me.egis.egsp.rsync.biz.dao.TaskMngtDAO;
import kr.go.me.egis.egsp.rsync.biz.service.SyncService;
import kr.go.me.egis.egsp.rsync.biz.vo.*;
import kr.go.me.egis.egsp.rsync.utils.DateUtils;

public class SyncServiceImpl implements SyncService {

    @Resource(name = "syncExtDAO")
    private SyncExtDAO syncExtDAO;

    @Resource(name = "syncIntDAO")
    private SyncIntDAO syncIntDAO;

    @Resource(name = "taskMngtDAO")
    private TaskMngtDAO taskMngtDAO;

    @Resource(name = "interfaceDAO")
    private InterfaceDAO interfaceDAO;



    @Override
    public void setReverseSyncTask(String serverId) {
        ReverseSyncInfoVO syncInfoVO = syncExtDAO.selectOneSyncRequest();
        if(syncInfoVO == null){ //Nothing to do for task
            return;
        }

        String taskCd = "TSK100"; //Reverse Sync Task
        String taskSttus = "TST010"; //실행요청
        String taskCmpltYn = "N";
        Gson gson = new Gson();
        String taskPrmtr = gson.toJson(syncInfoVO);

        SyncTaskMngtVO taskMngtVO = new SyncTaskMngtVO();
        taskMngtVO.setTaskCd(taskCd);
        taskMngtVO.setTaskSttus(taskSttus);
        taskMngtVO.setTaskCmpltYn(taskCmpltYn);
        taskMngtVO.setTaskPrmtr(taskPrmtr);
        taskMngtVO.setExecSrvrId(serverId);

        try{
            taskMngtDAO.insertTask(taskMngtVO);
            setReverseSyncJobStart(syncInfoVO.getSyncId());
        }catch (Exception e){
            setReverseSyncJobError(syncInfoVO.getSyncId());
        }
    }

    @Override
    public int insertReverseSyncRequest(ReverseSyncInfoVO reverseSyncInfoVO) {
        return 0;
    }

    @Override
    public int insertSyncRequest(SyncInfoVO syncInfoVO) {
        SyncInfoVO checkVo = syncIntDAO.selectSyncRequest(syncInfoVO);
        if(checkVo == null)
            return syncIntDAO.insertSyncRequest(syncInfoVO);
        else
            return syncIntDAO.updateSyncRequest(syncInfoVO);
    }

    @Override
    public void setReverseSyncJobStart(long syncId) {

    }

    @Override
    public void setReverseSyncJobError(long syncId) {

    }

    @Override
    public void updateReverseSyncInfo(ReverseSyncInfoVO syncInfoVO) {

    }

    @Override
    public InterfaceVO selectInterface(String intrfcId) {
        return interfaceDAO.getInterface(intrfcId);
    }

    @Override
    public long insertLoadingInfo(ReverseSyncInfoVO reverseSyncInfoVO, InterfaceVO interfaceVO, String syncKey) {
        String envrTable = "envr." + reverseSyncInfoVO.getTableNm();
        LoadingInfoVO loadingInfoVO = new LoadingInfoVO();
        loadingInfoVO.setColcId(reverseSyncInfoVO.getSyncId());
        loadingInfoVO.setIntrfcId(reverseSyncInfoVO.getIntrfcId());
        loadingInfoVO.setRecpFile(syncKey);
        loadingInfoVO.setRecpFileSize(0);
        loadingInfoVO.setRecpFileLineCt(reverseSyncInfoVO.getExtLinkRegCt());
        loadingInfoVO.setRecpFileSkipCt(reverseSyncInfoVO.getExtLinkCopyoutCt() - reverseSyncInfoVO.getIntLinkCopyinCt());
        loadingInfoVO.setDefineColumnCt(interfaceVO.getColumnCt());
        loadingInfoVO.setDefineHeaderYn(interfaceVO.getHeaderYn());
        loadingInfoVO.setLoadingTableNm(envrTable);
        loadingInfoVO.setLoadingTy(interfaceVO.getLoadingTy());
        loadingInfoVO.setLoadingSttus("END");
        loadingInfoVO.setLoadingBfRowCt(reverseSyncInfoVO.getIntEnvrBfRowCt());
        loadingInfoVO.setLoadingAfRowCt(reverseSyncInfoVO.getIntEnvrAfRowCt());
        loadingInfoVO.setLoadingDt(DateUtils.getCurrentTimestamp());
        syncIntDAO.insertLoadingInfo(loadingInfoVO);
        return loadingInfoVO.getLoadingId();
    }

    @Override
    public int insertSpatialRequest(String interfaceId, String envrTable, String syncKey) {
        SpatialInfoVO spatialInfoVO = new SpatialInfoVO();
        spatialInfoVO.setIntrfcId(interfaceId);
        spatialInfoVO.setSourcFileNm(syncKey);
        spatialInfoVO.setTableFullNm(envrTable);

        SpatialInfoVO checkVo = syncIntDAO.selectSpatialRequest(spatialInfoVO);
        if(checkVo == null){
            return syncIntDAO.insertSpatialRequest(spatialInfoVO);
        }else{
            spatialInfoVO.setSpatialId(checkVo.getSpatialId());
            return syncIntDAO.updateSpatialRequest(spatialInfoVO);
        }
    }


}
