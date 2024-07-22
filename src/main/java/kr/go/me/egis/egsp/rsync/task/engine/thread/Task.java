package kr.go.me.egis.egsp.rsync.task.engine.thread;


import jakarta.annotation.Resource;
import kr.go.me.egis.egsp.rsync.biz.vo.SyncTaskMngtVO;
import kr.go.me.egis.egsp.rsync.task.engine.TaskMonitor;
import lombok.extern.slf4j.Slf4j;


import java.util.concurrent.RunnableFuture;

@Slf4j
public abstract class Task implements IdentifiableCallable<Boolean> {

    @Resource(name = "taskMonitor")
    private TaskMonitor taskMonitor;

    volatile boolean cancelled;
    private TaskData taskData;

    public void initData(SyncTaskMngtVO taskInputData){
        taskData.setTaskId(taskInputData.getTaskId());
        taskData.setTaskParameter(taskInputData.getTaskPrmtr());
        taskData.setTaskCode(taskInputData.getTaskCd());
    }

    @Override
    public synchronized Long getId(){
        return taskData.getTaskId();
    }

    public String getTaskPrmtr() {
        return taskData.getTaskParameter();
    }

    public String getTaskCd() {
        return taskData.getTaskCode();
    }

    @Override
    public RunnableFuture<Boolean> newTask() {
        return new FutureTaskWrapper<Boolean>(this) {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                Task.this.cancelTask();
                return super.cancel(mayInterruptIfRunning);
            }

            @Override
            public Long getTaskId() {
                return getId();
            }
        };
    }

    @Override
    public synchronized void cancelTask() {
        cancelled = true; // Set the cancellation flag
    }

    @Override
    public Boolean call() {
        boolean completed = false;
        try {
            if (!cancelled) {
                initFunction();
            }

            while (!cancelled && !completed) {
                completed = atomicFunction();
            }

            if (!cancelled) {
                endFunction();
            }
        } catch (Exception e) {
            completed = handleException(e);
        }

        if (completed) {
            taskMonitor.completedUpdate(getId(),getTaskCd());
        } else if (cancelled) {
            taskMonitor.cancelledUpdate(getId());
        }
        // 쓰레드풀 관리 리스트에서 해당 삭제
        taskMonitor.removeJob(this.getId());
        return true;
    }

    abstract protected void initFunction() throws Exception;

    abstract protected void endFunction() throws Exception;

    abstract protected boolean atomicFunction() throws Exception;

    private boolean handleException(Exception e) {
        log.error("Exception: {}", e.getMessage(), e); // Logging full exception can be more beneficial for tracking problems
        taskMonitor.exceptionUpdate(getId(), e.getMessage());
        return false;
    }
}