package kr.go.me.egis.egsp.rsync.biz.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorVO {
    private String title;
    private LocalDateTime occurDate;
    private String message;
}
