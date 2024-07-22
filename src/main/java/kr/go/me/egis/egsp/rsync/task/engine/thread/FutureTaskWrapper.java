package kr.go.me.egis.egsp.rsync.task.engine.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * A Custom Wrapper around FutureTask for returning Task Id.
 * @author umermansoor
 */
public abstract class FutureTaskWrapper<T> extends FutureTask<T>
{
    public FutureTaskWrapper(Callable<T> c) {
        super(c);
    }

    public abstract Long getTaskId();

}