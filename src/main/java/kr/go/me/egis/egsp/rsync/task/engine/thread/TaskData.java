package kr.go.me.egis.egsp.rsync.task.engine.thread;

import lombok.Data;

@Data
public class TaskData {
    private long taskId;
    private String taskParameter;
    private String taskCode;
}
