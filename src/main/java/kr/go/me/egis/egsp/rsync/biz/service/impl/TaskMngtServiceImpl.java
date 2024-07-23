package kr.go.me.egis.egsp.rsync.biz.service.impl;

import javax.annotation.Resource;
import kr.go.me.egis.egsp.rsync.biz.dao.TaskMngtDAO;
import kr.go.me.egis.egsp.rsync.biz.service.TaskMngtService;
import kr.go.me.egis.egsp.rsync.biz.vo.SyncTaskMngtVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("taskMngtService")
public class TaskMngtServiceImpl implements TaskMngtService {

    @Resource
    private TaskMngtDAO taskMngtDAO;

    @Override
    public SyncTaskMngtVO selectToRunningTaskOne() {
        return taskMngtDAO.selectToRunningTaskOne();
    }

    @Override
    public SyncTaskMngtVO selectTaskCancel(String serverId) {
        return taskMngtDAO.selectTaskCancel(serverId);
    }

    @Override
    public int updateTask(SyncTaskMngtVO taskMngtVO) {
        return taskMngtDAO.updateTask(taskMngtVO);
    }

    @Override
    public int updateTaskStart(long taskId) {
        return taskMngtDAO.updateTaskStart(taskId);
    }
}
