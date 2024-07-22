package kr.go.me.egis.egsp.rsync.biz.service;

import kr.go.me.egis.egsp.rsync.biz.vo.ColumnDescVO;
import kr.go.me.egis.egsp.rsync.biz.vo.DeleteInfoVO;

import java.io.File;
import java.util.List;

public interface DbInternalService {

    void truncate(String tableFullName);

    void delete(DeleteInfoVO deleteInfoVO);

    long getDataCount(String tableFullName) throws Exception;

    long copyIn(String tableFullName, File copyFile) throws Exception;


    /**
     * 테이블의 컬럼정보를 조회한다.
     */
    List<ColumnDescVO> getColumnDescList(String tableName);

    /**
     * insert into envr.t select * from link.t;
     */
    int selectInsert(String linkTableName, String envrTableName);

    /**
     * insert into envr.t select columns from link.t;
     */
    int selectInsertColumns(String linkTableName, String envrTableName, String columns);
}
