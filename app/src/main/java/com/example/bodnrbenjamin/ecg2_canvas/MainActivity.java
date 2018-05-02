package com.example.bodnrbenjamin.ecg2_canvas;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.nio.IntBuffer;

public class MainActivity extends AppCompatActivity {
    private static Button button;
    private Button button2;
    private static TextView textView;
    private TextView textView2;
    private TextView textView3;
    public static TextView textView4;

    private static final String s = "com.example.bodnrbenjamin.ecg1";
    private ImageView mImageView;
    private final float[] sinus = new float[100];
    private int lineID=1;
    private LineDrawer line1;
    private int y;
    private CountDownTimer countDownTimer;
    private boolean isConnected=false;
    private UsbEcgHAL ecg;
    private int[][] data = new int[5][2200];
    private CountDownTimer DataRefreshTimer;
    private int looper=0;
    private int Size[];
    public int[][] RefreshedData;
    public int DataRevision=0;
    public CurveDrawer Drawer;
    private Thread thread;
    private Message msg;
    private MyThread mt;
    private Handler hand;
    private Runnable run;
    private int valami=1;
    DrawView d;

    private final CircularBuffer CBuffer=new CircularBuffer(8);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);

        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);

        d = (DrawView) findViewById(R.id.drawview);
        d.setBackgroundColor(Color.GREEN);

        RefreshedData = new int[5][2200];
        Size=new int[5];


        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        d.getLayoutParams().width = dm.widthPixels*5/6;
        d.requestLayout();


        int mul=30;
        for(int looper=0;looper<sinus.length;looper++)
        {
            sinus[looper] = (float) Math.sin(Math.PI /(sinus.length/2)*looper)*mul+mul+10;
        }

        Drawer = new CurveDrawer(textView4,d);
        y=0;
        ecg=new UsbEcgHAL(this,s,0x2405,0xB);
        ecg.setTextView(textView2);
        ecg.t2 = textView4;
        ecg.draw=Drawer;
    }

    private int HexStringByteArrayToInt(byte array[])
    {
        int ret=0;
        int temp=0;
        for(int looper = 0; looper < array.length; looper++)
        {
            if((array[looper] >= 0x30) && (array[looper] <=0x39))
            {
                temp=array[looper]-0x30;
            }
            else if((array[looper] >= 0x41) && (array[looper] <=0x46))
            {
                temp=array[looper]-55;
            }
            else if((array[looper] >= 0x61) && (array[looper] <=0x66))
            {
                temp=array[looper]-87;
            }
            ret+=temp<<(looper*4);
            //ret+=temp;
        }
        return ret;
    }
    private int HexCharToInt(byte data)
    {
        int ret=0;
        if(data >= 0x30 && data <=0x39)
        {
            ret=data-0x30;
        }
        else if(data >= 0x41 && data <=0x46)
        {
            ret=data-55;
        }
        else if(data >= 0x61 && data <=0x66)
        {
            ret=data-87;
        }
        return ret;
    }


    public void usingCountDownTimer()
    {
        countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long l)
            {

            }

            @Override
            public void onFinish() {
                start();
            }
        }.start();
    }
    public void button2OnClick(View v)
    {
        //mt = new MyThread();
    }
    public void StartDataRefreshTimer()
    {
        DataRefreshTimer = new CountDownTimer(Long.MAX_VALUE,70) {
            @Override
            public void onTick(long l) {
                looper++;
                //Size=ecg.Read(RefreshedData);
                textView4.setText(Integer.toString(looper));
                if(Size[0] != 0)
                {

                    DataRevision++;
                    textView3.setText(Integer.toString(Size[0]));
                    //textView3.setText(Integer.toString(looper)+" "+Integer.toString(Size[0]));
                    long t=System.currentTimeMillis();
                    //Drawer.DrawDatas(RefreshedData,Size);
                    textView.setText(Long.toString(System.currentTimeMillis()-t));
                }
                else
                {
                    textView3.setText(Integer.toString(looper)+" ERROR");
                }

                //t.append(Integer.toString(looper));
                //t.append(" ");
            }

            @Override
            public void onFinish() {
                this.start();
            }
        }.start();
    }
    public void buttonOnClick(View v)
    {

        if(!isConnected)
        {
            isConnected=true;
            isConnected=ecg.Initialize();//TODO
            if(isConnected)
            {
                button.setText("DISCONNECT");
                //StartDataRefreshTimer();//TODO
            }

            isConnected = true;
        }
        else
        {

            //Size=ecg.Read(RefreshedData);
            ecg.StartDataReadThread();

            /*for(int looper=0;looper<3;looper++)
            {
                textView3.append(Integer.toHexString(RefreshedData[0][looper]));
            }*/
            //Drawer.DrawDatas(RefreshedData,Size);
            //d.DrawLine();
            //textView.setText(Integer.toString(d.getWidth()));
            //Drawer.test();
            /*textView4.setText("");
            for(int looper=0;looper<10;looper++)
            {
                textView4.append(Integer.toHexString(RefreshedData[0][looper])+" ");
            }*/
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int ret;
        switch (resultCode)
        {
            case Activity.RESULT_OK:
                ret=data.getIntExtra("DriverOpenedStatus",-1);
                if(ret == 0) isConnected=true;
                else isConnected=false;
                break;
            case Activity.RESULT_CANCELED:
                isConnected=false;
                break;
            case Activity.RESULT_FIRST_USER:
                ret=data.getIntExtra("DriverOpenedStatus",-1);
                if(ret == 0) isConnected=true;
                else isConnected=false;
                break;
        }
        if(isConnected)
        {
            String button1DisconnectText = "Disconnect";
            button.setText(button1DisconnectText);
        }
        else
        {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MainActivity.this,"2")
                    .setSmallIcon(android.R.drawable.stat_notify_error)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setContentTitle("Not connected to port MAIN")
                    .setContentText("You are not connected to any port");
            notificationBuilder.setDefaults(
                    Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
            notificationManager.notify(1, notificationBuilder.build());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        ecg.close();
    }
}


