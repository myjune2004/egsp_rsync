package kr.go.me.egis.egsp.rsync.biz.dao;

import kr.go.me.egis.egsp.rsync.biz.vo.ColumnDescVO;
import kr.go.me.egis.egsp.rsync.biz.vo.DeleteInfoVO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository("sqlIntDAO")
public class SqlIntDAO {
    private final SqlSessionTemplate sqlSession;

    public SqlIntDAO(@Qualifier("intSqlSessionTemplate") SqlSessionTemplate sqlSession) {
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

    public int selectInsert(HashMap<String, String> map){
        return sqlSession.update("selectInsert", map);
    }

    public int selectInsertColumns(HashMap<String, String> map){
        return sqlSession.update("selectInsertColumns", map);
    }

    public List<ColumnDescVO> selectColumnDescList(String tableName){
        return sqlSession.selectList("selectColumnDesc", tableName);
    }
}
