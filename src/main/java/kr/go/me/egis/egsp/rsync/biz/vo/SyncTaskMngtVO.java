package kr.go.me.egis.egsp.rsync.biz.vo;

import lombok.Data;

@Data
public class SyncTaskMngtVO {
    private long taskId;
    private String taskCd;
    private String taskPrmtr;
    private String taskSttus;
    private String taskCmpltYn;
    private String execSrvrId;
    private String beginDt;
    private String endDt;
    private String deleYn;
    private String deleDt;
    private String deleUserId;
}
