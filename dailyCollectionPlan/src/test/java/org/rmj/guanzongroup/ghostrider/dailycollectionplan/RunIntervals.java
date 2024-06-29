package org.rmj.guanzongroup.ghostrider.dailycollectionplan;

import org.junit.Test;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RunIntervals {
    @Test
    public void TestRun(){
        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

        // This schedule a runnable task every 2 minutes
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                while (true){
                    System.out.println("UMAAWIT MULA KUSINA, HANGGANG SA SALA");
                }
            }
        }, 0, 2, TimeUnit.SECONDS);
    }
}
