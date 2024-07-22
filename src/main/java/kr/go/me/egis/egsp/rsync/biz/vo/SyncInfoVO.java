package kr.go.me.egis.egsp.rsync.biz.vo;

import lombok.Data;

@Data
public class SyncInfoVO {
    private long syncId;
    private String intrfcId;
    private String sourcFileNm;
    private String tableFullNm;
    private String priorExecQuery;
}
