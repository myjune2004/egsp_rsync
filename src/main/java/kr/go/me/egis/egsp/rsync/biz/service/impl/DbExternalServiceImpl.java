package kr.go.me.egis.egsp.rsync.biz.service.impl;

import jakarta.annotation.Resource;
import kr.go.me.egis.egsp.rsync.biz.dao.SqlExtDAO;
import kr.go.me.egis.egsp.rsync.biz.dao.SqlIntDAO;
import kr.go.me.egis.egsp.rsync.biz.service.DbExternalService;
import kr.go.me.egis.egsp.rsync.biz.vo.CopyVO;
import kr.go.me.egis.egsp.rsync.biz.vo.DeleteInfoVO;
import kr.go.me.egis.egsp.rsync.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;

@Slf4j
@Service("dbExternalService")
public class DbExternalServiceImpl implements DbExternalService {

    @Resource(name = "sqlExtDAO")
    private SqlExtDAO sqlExtDAO;

    @Value(value = "${sync.work-path}")
    private String syncWorkPath;

    @Value(value = "${spring.datasource-ext.url}")
    private String jdbcUrl;

    @Value(value = "${spring.username}")
    private String jdbcUsername;

    @Value(value = "${spring.password}")
    private String jdbcPassword;

    @Override
    public void truncate(String tableFullName) {
        sqlExtDAO.truncate(tableFullName);
    }

    @Override
    public void delete(DeleteInfoVO deleteInfoVO) {
        sqlExtDAO.delete(deleteInfoVO);
    }

    @Override
    public long getDataCount(String tableFullName) throws Exception {
        return sqlExtDAO.selectDataCount(tableFullName);
    }

    @Override
    public CopyVO copyOut(String tableFullName) throws Exception {
        String copyStat = prepareCopyStatement(tableFullName);
        String fileName = formattedFileName(tableFullName);
        File copyOutFile = new File(syncWorkPath, fileName);

        return copyDataOut(tableFullName, copyStat, copyOutFile);
    }

    private String prepareCopyStatement(String tableFullName) {
        String query = String.format("SELECT * FROM %s ", tableFullName);

        String copyStat =
                "COPY " +
                "(" +
                query +
                ")" +
                " TO STDOUT " +
                " WITH (" +
                "   FORMAT text" +
                "  ,DELIMITER '|'" +
                "  ,ENCODING 'UTF-8'" +
                " )";

        log.debug("Copy Statement:{}", copyStat);

        return copyStat;
    }

    private String formattedFileName(String tableFullName) {
        return String.format("%s_%s.copy", tableFullName, DateUtils.getCurrentDate_yyyyMMdd_HHmmss());
    }

    private CopyVO copyDataOut(String tableFullName, String copyStat, File copyOutFile) throws Exception {
        CopyVO copyVO;
        try (Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
             FileWriter fw = new FileWriter(copyOutFile)) {

            CopyManager copyManager = new CopyManager(conn.unwrap(BaseConnection.class));
            long exportRowCount = copyManager.copyOut(copyStat, fw);
            log.info("{} Copy out(Export) count {}", tableFullName, exportRowCount);

            copyVO = new CopyVO();
            copyVO.setExportCount(exportRowCount);
            copyVO.setCopyFile(copyOutFile);

        } catch (Exception e) {
            String errorMessage = String.format("%s copyOut Error: %s", tableFullName, e.getMessage());
            log.error(errorMessage);
            throw new Exception(errorMessage);
        }

        return copyVO;
    }

    @Override
    public void updateSyncFlag(String tableFullName) throws Exception {

    }
}
