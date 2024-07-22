package kr.go.me.egis.egsp.rsync.biz.vo;

import lombok.Data;

import java.io.File;

@Data
public class CopyVO {
    private File copyFile;
    private long exportCount;
}
