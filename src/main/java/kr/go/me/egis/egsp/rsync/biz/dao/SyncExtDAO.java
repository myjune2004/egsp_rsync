package kr.go.me.egis.egsp.rsync.biz.dao;

import kr.go.me.egis.egsp.rsync.biz.vo.ReverseSyncInfoVO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository("syncExtDAO")
public class SyncExtDAO {
    private final SqlSessionTemplate sqlSession;

    public SyncExtDAO(@Qualifier("extSqlSessionTemplate") SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    public ReverseSyncInfoVO selectOneSyncRequest(){
        return sqlSession.selectOne("selectOneSyncRequest");
    }

    public int updateSyncRequestSttus(ReverseSyncInfoVO reverseSyncInfoVO){
        return sqlSession.update("updateSyncRequestSttus", reverseSyncInfoVO);
    }


}
