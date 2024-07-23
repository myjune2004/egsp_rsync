package kr.go.me.egis.egsp.rsync.biz.service.impl;

import javax.annotation.Resource;
import kr.go.me.egis.egsp.rsync.biz.dao.SqlIntDAO;
import kr.go.me.egis.egsp.rsync.biz.service.DbInternalService;
import kr.go.me.egis.egsp.rsync.biz.vo.ColumnDescVO;
import kr.go.me.egis.egsp.rsync.biz.vo.DeleteInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service("dbInternalService")
public class DbInternalServiceImpl implements DbInternalService {

    @Resource(name = "sqlIntDAO")
    private SqlIntDAO sqlIntDAO;

    @Value(value = "${spring.datasource-int.url}")
    private String jdbcUrl;

    @Value(value = "${spring.username}")
    private String jdbcUsername;

    @Value(value = "${spring.password}")
    private String jdbcPassword;

    @Override
    public void truncate(String tableFullName) {
        sqlIntDAO.truncate(tableFullName);
    }

    @Override
    public void delete(DeleteInfoVO deleteInfoVO) {
        sqlIntDAO.delete(deleteInfoVO);
    }

    @Override
    public long getDataCount(String tableFullName) throws Exception {
        return sqlIntDAO.selectDataCount(tableFullName);
    }

    @Override
    public long copyIn(String tableFullName, File copyFile) throws Exception {
        String copyStat = prepareCopyStatement(tableFullName);

        log.debug("Copy Statement:{}", copyStat);
        long importRowCount;
        try(Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
            Reader in = new BufferedReader(new FileReader(copyFile))){

            CopyManager copyManager = new CopyManager(conn.unwrap(BaseConnection.class));
            importRowCount = copyManager.copyIn(copyStat, in);
            log.info("{} Copy in(Import) count {}", tableFullName, importRowCount);

        }catch (Exception e){
            String errorMessage = String.format("%s copyIn Error: %s", tableFullName, e.getMessage());
            log.error(errorMessage);
            throw new Exception(errorMessage);
        }

        //Delete File
        FileUtils.forceDelete(copyFile);
        log.debug("{} is delete!!!", copyFile);

        return importRowCount;
    }

    private String prepareCopyStatement(String tableFullName) {
        return  "COPY " +
                tableFullName +
                " FROM STDIN" +
                " WITH (" +
                "   FORMAT text" +
                "  ,DELIMITER '|'" +
                "  ,ENCODING 'UTF-8'" +
                " )";
    }

    @Override
    public List<ColumnDescVO> getColumnDescList(String tableName) {
        return sqlIntDAO.selectColumnDescList(tableName);
    }

    @Override
    public int selectInsert(String linkTableName, String envrTableName) {
        HashMap<String, String> map = new HashMap<>();
        map.put("envrTableName", envrTableName);
        map.put("linkTableName", linkTableName);
        return sqlIntDAO.selectInsert(map);
    }

    @Override
    public int selectInsertColumns(String linkTableName, String envrTableName, String columns) {
        HashMap<String, String> map = new HashMap<>();
        map.put("envrTableName", envrTableName);
        map.put("linkTableName", linkTableName);
        map.put("columns", columns);
        return sqlIntDAO.selectInsertColumns(map);
    }
}
