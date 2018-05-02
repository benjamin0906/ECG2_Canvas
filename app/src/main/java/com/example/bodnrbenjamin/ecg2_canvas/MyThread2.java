package com.example.bodnrbenjamin.ecg2_canvas;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by BodnárBenjamin on 2018. 04. 18..
 */




public class MyThread2 extends Thread
{
    public Handler handler;
    public Handler mainHandler;

    private boolean Header=false;
    private boolean StartSign=false;
    private int CommandSign=0;
    private boolean DSizeL=false;
    private boolean DSizeH=false;

    private int looper=0;
    private int looper2=0;
    private int DataOut[][]=new int[5][8000];
    private byte[] DataIn=new byte[9000];
    private int DSize=0;
    private int Counter11=0;
    private int GlobalCounter=0;
    private int Address=0;
    private byte[] RawData = new byte[9000];

    @Override
    public void run()
    {
        Looper.prepare();
        handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                RawData=(byte[]) msg.obj;
                for(int looper3 = 0; looper3<msg.arg1;looper3++)
                {
                    DataIn[looper3]=RawData[looper3];
                }
                /*Message m = mainHandler.obtainMessage();
                m.obj = RawData;
                m.arg1 = msg.arg1;
                mainHandler.sendMessage(m);*/
                //DataIn=(byte[]) msg.obj;
                looper=0;
                while(looper<msg.arg1)
                {
                    if(!Header) // if header is not decoded
                    {
                        if(!StartSign)
                        {
                            //if((0xFF & DataIn[looper]) == 0x53)
                                if((0xFF & msg.arg2) == 0x53)
                            {
                                StartSign=true;
                            }
                        }
                        else	//Start signal is arrived
                        {
                            if(CommandSign == 0)
                            {
                                //CommandSign = 0xFF & DataIn[looper];
                                CommandSign = 0xFF & msg.arg2;
                            }
                            else
                            {
                                if(!DSizeH)
                                {
                                    //DSize = (0xFF & (int)DataIn[looper])<<8;
                                    DSize = (0xFF & (int)msg.arg2)<<8;
                                    DSizeH = true;
                                }
                                else
                                {
                                    //DSize |= (0xFF & (int)DataIn[looper]);
                                    DSize |= (0xFF & (int)msg.arg2);
                                    Header = true;
                                }
                            }
                        }
                    }
                    else //if header is decoded
                    {
                        if(0 == (looper2 % 4))
                        {
                            //Address = 0xFF & (int)DataIn[looper];
                            Address = 0xFF & (int)msg.arg2;
                            switch (Address)
                            {
                                case 0x12:
                                    Counter11++;
                                    break;
                            }
                            GlobalCounter++;

                        }
                        else
                        {
                            switch(Address)
                            {
                                case 0x12:
                                    //DataOut[0][Counter11] |= (0xFF & (int)DataIn[looper]) << (8*(3-(looper2%4)));
                                    DataOut[0][Counter11] |= (0xFF & (int)msg.arg2) << (8*(3-(looper2%4)));
                                    //Counter11++;
                                    break;
                                default:
                                    //DataOut[0][0]=99;


                            }
                        }
                        looper2++;
                    }
                    looper++;
                }
                if(GlobalCounter == DSize && DSize !=0)
                {
                    Message m = mainHandler.obtainMessage();
                    m.obj = DataOut;
                    m.arg1 = DSize;
                    mainHandler.sendMessage(m);

                    Header=false;
                    StartSign=false;
                    CommandSign=0;
                    DSizeL=false;
                    DSizeH=false;

                    looper=0;
                    looper2=0;
                    DSize=0;
                    Counter11=0;
                    GlobalCounter=0;
                    Address=0;
                }

            }
        };
        Looper.loop();
    }
}