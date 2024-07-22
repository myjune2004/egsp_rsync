package kr.go.me.egis.egsp.rsync.task.engine.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;

/**
 * Custom Callable Interface with Id.
 *
 * @author umermansoor
 */
public interface IdentifiableCallable<T> extends Callable<T> {
    Long getId();

    void cancelTask(); // Method for supporting non-standard cancellation

    RunnableFuture<T> newTask();
}
