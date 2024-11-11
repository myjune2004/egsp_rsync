package kr.go.me.egis.egsp.rsync.biz.dao;

import kr.go.me.egis.egsp.rsync.biz.vo.DeleteInfoVO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository("sqlExtDAO")
public class SqlExtDAO {
    private final SqlSessionTemplate sqlSession;

    public SqlExtDAO(@Qualifier("extSqlSessionTemplate") SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    public long selectDataCount(String tableName){
        return sqlSession.selectOne("selectDataCount", tableName);
    }

    public void truncate(String tableName){
        sqlSession.update("truncateTable", tableName);
    }

    public void delete(DeleteInfoVO deleteInfoVO){
        sqlSession.delete("delete", deleteInfoVO);
    }

    public void deleteSyncData(String tableName, long linkHstId){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("linkHstId", linkHstId);
        params.put("tableName", tableName);
        sqlSession.delete("deleteSyncData", params);
    }
}
