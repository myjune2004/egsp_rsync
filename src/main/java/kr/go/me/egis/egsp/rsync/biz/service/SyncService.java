package kr.go.me.egis.egsp.rsync.biz.service;

import kr.go.me.egis.egsp.rsync.biz.vo.*;

public interface SyncService {
    void setReverseSyncTask(String serverId);

    int insertReverseSyncRequest(ReverseSyncInfoVO reverseSyncInfoVO);

    int insertSyncRequest(SyncInfoVO syncInfoVO);

    void setReverseSyncJobStart(long syncId);

    void setReverseSyncJobError(long syncId);

    void updateReverseSyncInfo(ReverseSyncInfoVO syncInfoVO);

    InterfaceVO selectInterface(String intrfcId);

    long insertLoadingInfo(ReverseSyncInfoVO reverseSyncInfoVO, InterfaceVO interfaceVO, String syncKey);

    int insertSpatialRequest(String interfaceId, String envrTable, String syncKey);

}
