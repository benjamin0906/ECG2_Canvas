package com.example.bodnrbenjamin.ecg2_canvas;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.TextView;

/**
 * Created by BodnárBenjamin on 2018. 04. 17..
 * This thread is responsible for the periodical refreshing of the datas.
 * In this thread runs a counter that refreshes the datas in every tick.
 */

public class PeriodicalDataRefresherThread extends Thread {
    public Handler handler;
    public Handler mainHandler;
    private ChannelDatas datas = new ChannelDatas(500);
    private CountDownTimer DataRefreshTimer= new CountDownTimer(Long.MAX_VALUE,50) {
        @Override
        public void onTick(long l) {
                isBusy = true;
                Size=ecg.Read(RefreshedData,datas);
                if(Size[0] != 0)
                {
                    RevisionNumber++;
                }
                isBusy = false;
                Message msg = mainHandler.obtainMessage();
                msg.arg1=1;
                msg.obj = datas;
                mainHandler.sendMessage(msg);
        }

        @Override
        public void onFinish() {
            this.start();
        }
    };
    public UsbEcgHAL ecg;
    private int Size[] = new int[5];;
    public volatile int RefreshedData[][];
    private volatile int RevisionNumber=0;
    private volatile boolean isBusy=false;
    public TextView tt;

    public boolean isBusy()
    {
        return isBusy;
    }

    public int GetRevisionNumber()
    {
        return RevisionNumber;
    }

    public void StartDataRefreshTimer()
    {
        DataRefreshTimer.start();
    }
    public void StopDataRefreshTimer()
    {
        DataRefreshTimer.cancel();
    }

    @Override
    public void run()
    {
        Looper.prepare();
        handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                switch (msg.arg1)
                {
                    case 1: //start timer
                        StartDataRefreshTimer();
                        break;
                    case 2: //stop timer
                        StopDataRefreshTimer();
                        break;
                    case 3:
                        break;
                }

            }


        };
        Looper.loop();
    }
}
