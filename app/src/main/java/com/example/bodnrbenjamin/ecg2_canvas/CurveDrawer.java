package com.example.bodnrbenjamin.ecg2_canvas;

import android.widget.TextView;

/**
 * Created by BodnárBenjamin on 2018. 04. 15..
 * This object is a complete object that can draw the given raw values from the ECG.
 */

public class CurveDrawer {
    private int DrawViewHeight;
    private int DrawViewWidth;
    private DrawView Lines2;
    private int LineID;
    private int LineIterator;
    private float MaxValue=(float)0;
    private float MinValue=(float)0;
    private TextView t;
    float VoltagePrev=0;
    private static final float ScaleFactor = (float)4*(float)1.8/(float)1.4/(float)16777216;
    private float Mul1=0;
    private int PrevSampleLooper=0;
    float CurrentValue=0;
    float PreMul=1;
    float PostMul=1;
    int OrdinataPrev=0;
    float tVoltageMax;
    float tVoltageMin;

    public CurveDrawer(TextView t,DrawView d)
    {
        this.t = t;
        LineIterator = 0;
        LineID = 1;

        Lines2 = d;

        DrawViewHeight = Lines2.getLayoutParams().height;
        DrawViewWidth = Lines2.getLayoutParams().width;

        LineID = DrawViewWidth;
        Lines2.LineNumber = DrawViewWidth;
    }
    private float EcgDataToFloat(int data)
    {
        int sData;
        if(0 != (data&0x800000)) sData=data|0xff000000;
        else sData=data;
        return (float) sData*ScaleFactor;
    }
    private int DataToOrdinata(float Voltage)
    {
        int ret;
        if(Voltage<=MaxValue && Voltage>=MinValue)
        {
            ret=(int) (Mul1*((MaxValue-Voltage)));

        }
        else if(Voltage > MaxValue) ret=0;
        else ret=DrawViewHeight-1;
        if(ret >= DrawViewHeight) ret= DrawViewHeight-1;
        return ret;
    }

    public void DrawDatas(int Data[],int Sizes)
    {
        Mul1 =(float) DrawViewHeight/(MaxValue-MinValue);
        int looper;
        VoltagePrev = EcgDataToFloat(Data[0]);

        int Ordinate;
        int SampleNumber = 1000;
        float Rate = (float)DrawViewWidth/(float) SampleNumber;
        int looper2=0;
        float Voltage;

        float Volt=VoltagePrev;
        if(Rate < 1) //össze kell préselni
        {
            float tRate = 1/Rate;

            for(looper=0;looper < Sizes;looper++)
            {
                PrevSampleLooper++;
                if((tRate*(float)(LineIterator+1)) <= (float)PrevSampleLooper)
                {
                    PostMul = (float)PrevSampleLooper - (tRate*(float)(LineIterator+1));
                    PreMul = 1-PostMul;

                    Voltage = EcgDataToFloat(Data[looper]);
                    CurrentValue += Voltage*PreMul;
                    CurrentValue /= tRate;
                    tVoltageMax = Math.max(tVoltageMax,CurrentValue);
                    tVoltageMin = Math.min(tVoltageMin,CurrentValue);
                    Ordinate = DataToOrdinata(CurrentValue);

                    Lines2.modifyLineWithoutRefresh(LineIterator, LineIterator, OrdinataPrev, LineIterator+1, Ordinate);

                    VoltagePrev = CurrentValue;
                    OrdinataPrev = Ordinate;
                    LineIterator++;

                    if (LineIterator == LineID)
                    {
                        LineIterator = 0;
                        MaxValue = tVoltageMax;
                        MinValue = tVoltageMin;
                        tVoltageMax = CurrentValue;
                        tVoltageMin = CurrentValue;
                        PrevSampleLooper = 0;
                        CurrentValue = 0;
                    }
                    CurrentValue = Voltage*PostMul;
                }
                else
                {
                    Voltage = EcgDataToFloat(Data[looper]);
                    CurrentValue += Voltage;
                }
            }
        }
        else //szét kell húzni
        {
            int offset;
            if(Sizes>SampleNumber)
            {
                offset=Sizes-SampleNumber;
            }
            else offset=0;

            for (looper = 1; looper < DrawViewWidth - 1 && looper2<Sizes; looper++)
            {
                if ((Rate * (float) (looper2 + 1)) < (float) (looper + 1))
                {
                    float PostM = ((float) (looper + 1)) - Rate * ((float) (looper2 + 1));//hiányzó az egészhez
                    float PreM = 1-PostM;//tizedes rész
                    CurrentValue = Volt * PreM;
                    looper2++;
                    Volt = EcgDataToFloat(Data[looper2+offset]);
                    CurrentValue += Volt * PostM;
                }
                else
                {
                    CurrentValue = Volt;
                }
                //if (VoltagePrev > tVoltageMax) tVoltageMax = VoltagePrev; TODO: it is written by me but same as below
                //if (VoltagePrev < tVoltageMin) tVoltageMin = VoltagePrev; TODO: it is written by me but same as below
                tVoltageMax = Math.max(tVoltageMax,VoltagePrev);
                tVoltageMin = Math.min(tVoltageMin,VoltagePrev);
                Ordinate = DataToOrdinata(CurrentValue);

                Lines2.modifyLineWithoutRefresh(LineIterator, LineIterator - 1, OrdinataPrev, LineIterator, Ordinate);

                VoltagePrev = CurrentValue;
                OrdinataPrev = Ordinate;
                LineIterator++;
                if (LineIterator == LineID - 1) LineIterator = 0;
            }
            MaxValue = tVoltageMax;
            MinValue = tVoltageMin;
        }
        Lines2.refresh();

    }

    /*public void DrawDatas(int Data[][],int Sizes[])
    {
        Mul1 =(float) DrawViewHeight/(MaxValue-MinValue);
        int looper;
        VoltagePrev = EcgDataToFloat(Data[0][0]);

        float tVoltageMax = VoltagePrev;
        float tVoltageMin = VoltagePrev;
        int OrdinataPrev;
        int Ordinata;
        int SampleNumber = 1000;
        float Rate = (float)DrawViewWidth/(float) SampleNumber;
        t.setText(Float.toString(Rate)+" ");
        int looper2=0;
        OrdinataPrev = DataToOrdinata(VoltagePrev);
        float Voltage=0;

        float Volt=VoltagePrev;
        if(Rate < 1) //össze kell préselni
        {
            float tRate = 1/Rate;
            float PreMul=1;
            float PostMul=1;
            int looper3=0;
            for(looper=0;looper < Sizes[0];looper++,PrevSampleLooper++)
            {
                if((int)(tRate*(float)(LineIterator+1)) > PrevSampleLooper)
                {
                    PreMul = (tRate*(float)(looper2+1)) - ((float) (int)(tRate*(float)(looper2+1)));
                    PostMul= 1-PreMul;
                    Voltage = EcgDataToFloat(Data[0][looper3]);
                    CurrentValue += Voltage*PreMul;
                    CurrentValue /= tRate;
                    tVoltageMax = Math.max(tVoltageMax,VoltagePrev);
                    tVoltageMin = Math.min(tVoltageMin,VoltagePrev);
                    Ordinata = DataToOrdinata(CurrentValue);

                    t.append("Cr:"+Float.toString(CurrentValue));
                    t.append(" o:"+Integer.toString(Ordinata));

                    Lines2.modifyLineWithoutRefresh(LineIterator, LineIterator - 1, OrdinataPrev, LineIterator, Ordinata);

                    VoltagePrev = CurrentValue;
                    OrdinataPrev = Ordinata;
                    LineIterator++;
                    if (LineIterator == LineID - 1) LineIterator = 0;
                    CurrentValue = Voltage*PostMul;
                }
                else
                {
                    CurrentValue += EcgDataToFloat(Data[0][looper3]);
                }
            }
            /*for(looper2 = 0; looper2 < (int)((float)Sizes[0] * Rate); looper2++, PrevLineLooper++)
            {
                for(looper = 0; looper<tRate;looper++,looper3++, PrevSampleLooper++)
                {
                    CurrentValue = 0;
                    if((int)(tRate*(float)(PrevLineLooper+1)) < PrevSampleLooper)
                    {
                        PreMul = (tRate*(float)(looper2+1)) - ((float) (int)(tRate*(float)(looper2+1)));
                        PostMul= 1-PreMul;
                        CurrentValue += EcgDataToFloat(Data[0][looper3])*PreMul;
                    }
                    else
                    {
                        CurrentValue += EcgDataToFloat(Data[0][looper3])*PostMul;
                        PostMul = 1;
                    }
                    CurrentValue/=tRate;
                }
                tVoltageMax = Math.max(tVoltageMax,VoltagePrev);
                tVoltageMin = Math.min(tVoltageMin,VoltagePrev);
                Ordinata = DataToOrdinata(CurrentValue);

                t.append("Cr:"+Float.toString(CurrentValue));
                t.append("0o:"+Integer.toString(Ordinata));

                Lines2.modifyLineWithoutRefresh(LineIterator, LineIterator - 1, OrdinataPrev, LineIterator, Ordinata);

                VoltagePrev = CurrentValue;
                OrdinataPrev = Ordinata;
                LineIterator++;
                if (LineIterator == LineID - 1) LineIterator = 0;
            }


        }
        else //szét kell húzni
        {
            int offset;
            if(Sizes[0]>SampleNumber)
            {
                offset=Sizes[0]-SampleNumber;
            }
            else offset=0;

            for (looper = 1; looper < DrawViewWidth - 1 && looper2<Sizes[0]; looper++)
            {
                if ((Rate * (float) (looper2 + 1)) < (float) (looper + 1))
                {
                    float PostM = ((float) (looper + 1)) - Rate * ((float) (looper2 + 1));//hiányzó az egészhez
                    float PreM = 1-PostM;//tizedes rész
                    CurrentValue = Volt * PreM;
                    looper2++;
                    Volt = EcgDataToFloat(Data[0][looper2+offset]);
                    CurrentValue += Volt * PostM;
                }
                else
                {
                    CurrentValue = Volt;
                }
                //if (VoltagePrev > tVoltageMax) tVoltageMax = VoltagePrev; TODO: it is written by me but same as below
                //if (VoltagePrev < tVoltageMin) tVoltageMin = VoltagePrev; TODO: it is written by me but same as below
                tVoltageMax = Math.max(tVoltageMax,VoltagePrev);
                tVoltageMin = Math.min(tVoltageMin,VoltagePrev);
                Ordinata = DataToOrdinata(CurrentValue);

                Lines2.modifyLineWithoutRefresh(LineIterator, LineIterator - 1, OrdinataPrev, LineIterator, Ordinata);

                VoltagePrev = CurrentValue;
                OrdinataPrev = Ordinata;
                LineIterator++;
                if (LineIterator == LineID - 1) LineIterator = 0;
            }
        }
        Lines2.refresh();
        MaxValue = tVoltageMax;
        MinValue = tVoltageMin;
    }*/
}
