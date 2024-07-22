package kr.go.me.egis.egsp.rsync.biz.vo;

import lombok.Data;

@Data
public class InterfaceVO {
    private String intrfcId;
    private String intrfcNm;
    private String sourcSysNm;
    private String sourcSysAbrv;
    private String tgrtTableNm;
    private String loadingTy;
    private String linkTy;
    private String dataDelimiter;
    private String headerYn;
    private int columnCt;
}
