package kr.go.me.egis.egsp.rsync.biz.vo;

import lombok.Data;

@Data
public class SpatialInfoVO {
    private long spatialId;
    private String intrfcId;
    private String sourcFileNm;
    private String tableFullNm;
}
