package kr.go.me.egis.egsp.rsync.biz.dao;

import kr.go.me.egis.egsp.rsync.biz.vo.SyncTaskMngtVO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository("taskMngtDAO")
public class TaskMngtDAO {

    private final SqlSessionTemplate sqlSession;

    public TaskMngtDAO(@Qualifier("intSqlSessionTemplate") SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    public int insertTask(SyncTaskMngtVO taskMngtVO){
        return sqlSession.insert("insertTask", taskMngtVO);
    }

    public SyncTaskMngtVO selectToRunningTaskOne(){
        return sqlSession.selectOne("selectToRunningTaskOne");
    }

    public SyncTaskMngtVO selectTaskCancel(String serverId){
        return sqlSession.selectOne("selectTaskCancel", serverId);
    }

    public int updateTask(SyncTaskMngtVO taskMngtVO){
        return sqlSession.update("updateTask", taskMngtVO);
    }

    public int updateTaskStart(long taskId){
        return sqlSession.update("updateTaskStart", taskId);
    }

}
