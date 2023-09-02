package com.example.exfch;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

//백그라운드 유지
public class MyService extends Service {
    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null) {
            return START_STICKY;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}