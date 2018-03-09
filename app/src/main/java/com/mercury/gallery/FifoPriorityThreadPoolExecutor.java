package com.mercury.gallery;

import android.os.Process;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wang.zhonghao
 * @date 2018/3/9
 * @descript
 */

public class FifoPriorityThreadPoolExecutor extends ThreadPoolExecutor {

    private static final String TAG = "ThreadPoolExecutor";
    private final AtomicInteger ordering = new AtomicInteger();
    private final UncaughtThrowableStrategy mUncaughtThrowableStrategy;

    public enum UncaughtThrowableStrategy{

        IGNORE,

        LOG{
            @Override
            protected void handle(Throwable t) {
                if (Log.isLoggable(TAG, Log.ERROR)) {
                    Log.e(TAG, "Request threw uncaught throwable", t);
                }
            }
        },

        THROW{
            @Override
            protected void handle(Throwable t) {
                super.handle(t);
                throw new RuntimeException(t);
            }
        };

        protected void handle(Throwable t){

        }
    }

    public FifoPriorityThreadPoolExecutor(int poolSize) {
        this(poolSize, UncaughtThrowableStrategy.LOG);
    }

    public FifoPriorityThreadPoolExecutor(int poolSize, UncaughtThrowableStrategy
            uncaughtThrowableStrategy) {
        this(poolSize, poolSize, 0, TimeUnit.MILLISECONDS, new DefaultThreadFactory(),
                uncaughtThrowableStrategy);
    }

    public FifoPriorityThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long
            keepAliveTime, TimeUnit unit, ThreadFactory threadFactory,UncaughtThrowableStrategy uncaughtThrowableStrategy) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new
                PriorityBlockingQueue<Runnable>(), threadFactory);
        this.mUncaughtThrowableStrategy = uncaughtThrowableStrategy;

    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t == null && r instanceof Future<?>) {
            Future<?> future = (Future<?>) r;
            if (future.isDone() && !future.isCancelled()) {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class DefaultThreadFactory implements ThreadFactory {

        int threadNum = 0;

        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread result = new Thread(r, "fifo-pool-thread-" + threadNum){
                @Override
                public void run() {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    super.run();
                }
            };
            threadNum++;
            Log.i(TAG, result.getName());
            return result;
        }
    }

}
