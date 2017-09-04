package com.github.guhaibin.utils.concurrency;

import com.github.guhaibin.api.push.PushResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class StatusCountDownLatch {

    private static final Logger LOG = LoggerFactory.getLogger(StatusCountDownLatch.class);

    private static final Executor executor = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("update countdown latch thread");
            return t;
        }
    });
    private static final ConcurrentHashMap<String, StatusCountDownLatch> map = new ConcurrentHashMap<>();

    private String sessionId;
    private int result;
    private int success = 0;
    private int num;
    private CountDownLatch latch;

    public StatusCountDownLatch(String sessionId, int num){
        this.sessionId = sessionId;
        this.num = num;
        this.latch = new CountDownLatch(num);
    }

    public void await(){
        try{
            map.put(sessionId, this);
            latch.await();
        }catch (InterruptedException e){
            LOG.error("await interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    public boolean await(long timeout){
        try{
            map.put(sessionId, this);
            return latch.await(timeout, TimeUnit.SECONDS);
        }catch (InterruptedException e){
            LOG.error("await interrupted", e);
            Thread.currentThread().interrupt();
        }
        return false;
    }

    public void countDown(){
        latch.countDown();
    }

    public static void countDown(String sessionId, boolean success){
        executor.execute(() -> {
            StatusCountDownLatch latch = map.get(sessionId);
            if (latch != null){
                if (success){
                    latch.setSuccess();
                }
                if (latch.getCount() == 0){
                    map.remove(sessionId);
                }
                latch.countDown();
            }else {
                LOG.warn("can not find latch for session {}", sessionId);
            }
        });
    }

    public static void countDown(String sessionId, int result){
        executor.execute(() -> {
            StatusCountDownLatch latch = map.get(sessionId);
            if (latch != null){
                latch.setResult(result);
                if (result == PushResult.SUCCESS){
                    latch.setSuccess();
                }
                if (latch.getCount() == 0){
                    map.remove(sessionId);
                }
                latch.countDown();
            }else {
                LOG.warn("can not find latch for session {}", sessionId);
            }
        });
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public long getCount(){
        return latch.getCount();
    }

    public void setSuccess(){
        this.success++;
    }

    public boolean isSuccess(){
        return this.success == this.num;
    }
}
