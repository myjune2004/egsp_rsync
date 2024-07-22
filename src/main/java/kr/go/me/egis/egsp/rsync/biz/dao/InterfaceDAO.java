package kr.go.me.egis.egsp.rsync.biz.dao;

import kr.go.me.egis.egsp.rsync.biz.vo.InterfaceVO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository("interfaceDAO")
public class InterfaceDAO {
    private final SqlSessionTemplate sqlSession;

    public InterfaceDAO(@Qualifier("intSqlSessionTemplate") SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    public InterfaceVO getInterface(String  intrfcId) {
        return sqlSession.selectOne("selectInterface", intrfcId);
    }
}
