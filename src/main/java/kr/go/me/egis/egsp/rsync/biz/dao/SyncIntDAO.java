package kr.go.me.egis.egsp.rsync.biz.dao;

import kr.go.me.egis.egsp.rsync.biz.vo.LoadingInfoVO;
import kr.go.me.egis.egsp.rsync.biz.vo.SpatialInfoVO;
import kr.go.me.egis.egsp.rsync.biz.vo.SyncInfoVO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository("syncIntDAO")
public class SyncIntDAO {
    private final SqlSessionTemplate sqlSession;

    public SyncIntDAO(@Qualifier("intSqlSessionTemplate") SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    public SyncInfoVO selectSyncRequest(SyncInfoVO syncInfoVO) {
        return sqlSession.selectOne("selectSyncRequest", syncInfoVO);
    }

    public int insertSyncRequest(SyncInfoVO syncInfoVO) {
        return sqlSession.insert("insertSyncRequest", syncInfoVO);
    }

    public int updateSyncRequest(SyncInfoVO syncInfoVO) {
        return sqlSession.update("updateSyncRequest", syncInfoVO);
    }

    public int insertLoadingInfo(LoadingInfoVO loadingInfoVO){
        return sqlSession.insert("insertLoadingInfo", loadingInfoVO);
    }

    public SpatialInfoVO selectSpatialRequest(SpatialInfoVO spatialInfoVO){
        return sqlSession.selectOne("selectSpatialRequest", spatialInfoVO);
    }

    public int insertSpatialRequest(SpatialInfoVO spatialInfoVO){
        return sqlSession.insert("insertSpatialRequest", spatialInfoVO);
    }

    public int updateSpatialRequest(SpatialInfoVO spatialInfoVO){
        return sqlSession.update("updateSpatialRequest", spatialInfoVO);
    }

}
