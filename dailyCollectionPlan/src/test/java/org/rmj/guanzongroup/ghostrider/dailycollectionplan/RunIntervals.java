package org.rmj.guanzongroup.ghostrider.dailycollectionplan;

import android.os.Handler;
import org.junit.Test;

public class RunIntervals {
    @Test
    public void TestRun(){
        Handler handler = new Handler();

        Runnable thread = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 60000);
                System.out.println("HEllo world");
            }
        };

        handler.postDelayed(thread, 60000);
    }
}
