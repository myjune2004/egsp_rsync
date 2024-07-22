package kr.go.me.egis.egsp.rsync.biz.vo;

import lombok.Data;

@Data
public class ColumnDescVO {
    private String columnName;
    private String dataType;
    private String notNull;
}