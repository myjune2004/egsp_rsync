package kr.go.me.egis.egsp.rsync.biz.service;

import kr.go.me.egis.egsp.rsync.biz.vo.SyncTaskMngtVO;

public interface TaskMngtService {
    SyncTaskMngtVO selectToRunningTaskOne();

    SyncTaskMngtVO selectTaskCancel(String serverId);

    int updateTask(SyncTaskMngtVO taskMngtVO);

    int updateTaskStart(long taskId);
}
