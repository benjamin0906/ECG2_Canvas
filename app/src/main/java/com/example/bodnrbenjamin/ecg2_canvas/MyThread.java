package com.example.bodnrbenjamin.ecg2_canvas;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by BodnárBenjamin on 2018. 04. 17..
 */

public class MyThread extends Thread {
    public Handler handler;
    public Handler mainHandler;

    private boolean Header=false;
    private boolean StartSign=false;
    private boolean DSizeH=false;
    private int CommandSign=0;
    private int DSize=0;
    private int GlobalCounter11=-1;
    private int Address=0;
    private int looper=0;
    private int DataOut[][];
    private int DataIn[][];
    public CircularBuffer Buff;
    private int GlobalLooper=0;
    private Message m;
    private int tempArray[];
    private int UnReadSize=0;

    public volatile boolean Working=false;
    public volatile boolean Working2=false;

    public MyThread(CircularBuffer b)
    {
        this.Buff=b;
        DataOut = new int[5][2000];
        tempArray = new int[8000];
    }

    @Override
    public void run()
    {
        Looper.prepare();
        handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                Header = false;
                StartSign = false;
                DSizeH = false;
                CommandSign = 0;
                GlobalCounter11 = -1;
                Address = 0;
                GlobalLooper = 0;
                DSize = 0;
                looper = 0;
                long t=0;
                long t2=0;
                int looper2=0;
                Working=false;
                Working2=false;

                while (GlobalLooper < DSize || !Header)
                {
                    if(Working == false && UnReadSize >=60){
                        t=System.currentTimeMillis();
                        Working=true;
                    }
                    synchronized (Buff)
                    {
                        UnReadSize = Buff.UnreadSize();
                        for (int looper=0; looper<UnReadSize; looper++) tempArray[looper]=Buff.pull();
                    }
                    if(Working2 == false && UnReadSize >=60){
                        t2=System.currentTimeMillis();
                        Working2=true;
                    }
                    looper2=0;

                    while (UnReadSize > looper2)
                    {
                        if (!Header)
                        {
                            if (!StartSign)
                            {
                                if (tempArray[looper2] == 0x53) StartSign = true;
                            }
                            else
                            {
                                if (CommandSign == 0) CommandSign = tempArray[looper2];
                                else
                                {
                                    if (!DSizeH)
                                    {
                                        DSize = (tempArray[looper2]) << 8;
                                        DSizeH = true;
                                    }
                                    else
                                    {
                                        DSize += tempArray[looper2];
                                        Header = true;
                                    }
                                }
                            }
                        }
                        else
                        {
                            if ((looper % 4) == 0)
                            {
                                Address = tempArray[looper2];
                                GlobalLooper++;
                                switch (Address)
                                {
                                    case 0x11:
                                        GlobalCounter11++;
                                        DataOut[0][GlobalCounter11]=0;
                                        break;
                                }
                            }
                            else
                            {
                                switch (Address)
                                {
                                    case 0x11:
                                        DataOut[0][GlobalCounter11] |= (tempArray[looper2]) << (8 * (3 - (looper % 4)));
                                        break;
                                    default:
                                }
                            }
                            looper++;
                        }
                        looper2++;
                    }

                    if(GlobalLooper >= DSize && DSize !=0)
                    {
                        m = mainHandler.obtainMessage();
                        m.obj = DataOut;
                        //m.arg1 = DSize;
                        m.arg1=(int)(t2-t);
                        //m.arg2=(int)System.currentTimeMillis();
                        //m.arg1=Buff.UnreadSize();
                        mainHandler.sendMessage(m);
                    }
                }
            }


        };
        Looper.loop();
    }
}
