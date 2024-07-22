package kr.go.me.egis.egsp.rsync.biz.dao;

import kr.go.me.egis.egsp.rsync.biz.vo.DeleteInfoVO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

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
}
