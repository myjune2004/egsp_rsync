package kr.go.me.egis.egsp.rsync.biz.vo;

import lombok.Data;

@Data
public class ReverseSyncInfoVO {
    private long syncId;
    private String intrfcId;
    private String bakFileNm;
    private String tableNm;
    private String priorExecQuery;
    private String syncSttus;
    private long extLinkRegCt;
    private long extLinkCopyoutCt;
    private long intLinkCopyinCt;
    private long intEnvrBfRowCt;
    private long intEnvrAfRowCt;
    private String syncBginDt;
    private String syncEndDt;
    private String syncInfo;
}
