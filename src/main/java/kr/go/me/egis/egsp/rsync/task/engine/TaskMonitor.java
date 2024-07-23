package kr.go.me.egis.egsp.rsync.task.engine;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import kr.go.me.egis.egsp.rsync.biz.service.SyncService;
import kr.go.me.egis.egsp.rsync.biz.service.TaskMngtService;
import kr.go.me.egis.egsp.rsync.biz.vo.SyncTaskMngtVO;
import kr.go.me.egis.egsp.rsync.provider.ApplicationContextProvider;
import kr.go.me.egis.egsp.rsync.task.SyncTask;
import kr.go.me.egis.egsp.rsync.task.engine.thread.CustomFutureReturningExecutor;
import kr.go.me.egis.egsp.rsync.task.engine.thread.FutureTaskWrapper;
import kr.go.me.egis.egsp.rsync.task.engine.thread.Task;
import kr.go.me.egis.egsp.rsync.utils.DateUtils;
import kr.go.me.egis.egsp.rsync.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service("taskMonitor")
public class TaskMonitor {

    @Resource(name = "taskMngtService")
    private TaskMngtService taskMngtService;

    @Resource(name = "syncService")
    private SyncService syncService;

    /** Task execute server id */
    private String serverId;

    /** Concurrent thread core pool size(String) */
    @Value(value = "${gisen.task.thread-count:1}")
    private String thread;

    /** Concurrent thread core pool size(Number) */
    private int coreThreadCount;

    private boolean isInit = false;

    /** Concurrent thread service */
    private ExecutorService exec;

    /** Concurrent thread service map */
    private final ConcurrentHashMap<Long, FutureTaskWrapper<Boolean>> jobList = new ConcurrentHashMap<>();

    @PostConstruct
    private void init(){
        String hostName;
        try{
            InetAddress localhost = InetAddress.getLocalHost();
            hostName = localhost.getHostName();
            if(StringUtils.nullToEmpty(hostName).equals("")){
                hostName = "JohnDoe";
            }
        }catch (UnknownHostException e){
            hostName = "JaneDoe";
        }
        serverId = hostName;
        log.debug("gisen task server id:{}", serverId);

        coreThreadCount = Integer.parseInt(thread);
        exec = new CustomFutureReturningExecutor(coreThreadCount, coreThreadCount, Long.MAX_VALUE, TimeUnit.DAYS, new LinkedBlockingQueue<>());

        isInit = true;
    }

    //Delay unit: Milliseconds
    @Scheduled(fixedDelayString = "${gisen.task.run-check}", initialDelay = 60000)
    private void runCheck(){
        log.debug("{}: Run Checking", serverId);

        //region Task Request
        try{
            syncService.setReverseSyncTask(serverId);
        }catch (Exception e){
            log.error(e.getMessage());
            //TODO: Insert Task 오류 처리 -> 어떤 오류가 나는지 확인이 필요하다.
        }
        //endregion

        if(isInit && jobList.size() < coreThreadCount){
            SyncTaskMngtVO taskData = taskMngtService.selectToRunningTaskOne();
            if(taskData != null && taskData.getTaskId() > 0){
                taskMngtService.updateTaskStart(taskData.getTaskId());
                Task task = null;

                if ("TSK090".equals(taskData.getTaskCd())) { //Sync Task
                    task = ApplicationContextProvider.getApplicationContext().getBean(SyncTask.class);
                }

                if (task != null) {
                    task.initData(taskData);
                    jobList.put(taskData.getTaskId(), (FutureTaskWrapper<Boolean>) exec.submit(task));
                }
            }
        }
    }

    //Delay unit: Milliseconds
    @Scheduled(fixedDelayString = "${gisen.task.stop-check}", initialDelay = 100000)
    private void stopCheck() {
        log.debug("{}: Stop Checking", serverId);
        if(isInit){
            SyncTaskMngtVO taskData = taskMngtService.selectTaskCancel(serverId);
            if(taskData != null){
                long taskId = taskData.getTaskId();
                taskData.setTaskSttus("TST060");
                taskData.setEndDt(DateUtils.getCurrentTimestamp());
                taskMngtService.updateTask(taskData);
                if(!jobList.containsKey(taskId)){
                    cancelledUpdate(taskId);
                } else {
                    jobList.get(taskId).cancel(true);
                }
            }
        }
    }

    public void cancelledUpdate(Long id){
        log.debug("Task {} is canceled", id);
        SyncTaskMngtVO taskMngtVO = new SyncTaskMngtVO();
        taskMngtVO.setTaskId(id);
        taskMngtVO.setTaskSttus("TST070");
        taskMngtVO.setEndDt(DateUtils.getCurrentTimestamp());
        taskMngtService.updateTask(taskMngtVO);
    }

    public void completedUpdate(Long id, String taskCode) {
        log.debug("Task {} is complete, Task code: " + taskCode, id);
        SyncTaskMngtVO taskMngtVO = new SyncTaskMngtVO();
        taskMngtVO.setTaskId(id);
        taskMngtVO.setTaskSttus("TST040");
        taskMngtVO.setTaskCmpltYn("Y");
        taskMngtVO.setEndDt(DateUtils.getCurrentTimestamp());
        taskMngtService.updateTask(taskMngtVO);
    }

    public void exceptionUpdate(Long id, String message) {
        log.error("Task {} has got error:" + message, id);
        SyncTaskMngtVO taskMngtVO = new SyncTaskMngtVO();
        taskMngtVO.setTaskId(id);
        taskMngtVO.setTaskSttus("TST080");
        taskMngtVO.setEndDt(DateUtils.getCurrentTimestamp());
        taskMngtService.updateTask(taskMngtVO);
    }

    public void removeJob(Long id) {
        if (jobList.get(id) != null) {
            jobList.remove(id);
        }
    }

    public void stopAll(){
        if (!jobList.isEmpty()) {
            for (Long key : jobList.keySet()) {
                jobList.get(key).cancel(true);
            }
        }
        exec.shutdown();
    }


}
