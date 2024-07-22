package kr.go.me.egis.egsp.rsync.biz.service;

import kr.go.me.egis.egsp.rsync.biz.vo.CopyVO;
import kr.go.me.egis.egsp.rsync.biz.vo.DeleteInfoVO;

public interface DbExternalService {

    void truncate(String tableFullName);

    void delete(DeleteInfoVO deleteInfoVO);

    long getDataCount(String tableFullName) throws Exception;

    CopyVO copyOut(String tableFullName) throws Exception;

    void updateSyncFlag(String tableFullName) throws Exception;
}