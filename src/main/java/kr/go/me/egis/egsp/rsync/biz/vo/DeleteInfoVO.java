package kr.go.me.egis.egsp.rsync.biz.vo;

import lombok.Data;

@Data
public class DeleteInfoVO {
    private String tableName;
    private String where;
}
