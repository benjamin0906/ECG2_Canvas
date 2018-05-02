package com.example.bodnrbenjamin.ecg2_canvas;

/**
 * Created by BodnárBenjamin on 2018. 02. 28..
 */

public class Line
{
    protected int LineColor;
    protected int ID;
    protected int[][] Pixels = new int[300][2];
    protected int iter;
    protected Line(int InputStartX, int InputStartY, int InputStopX, int InputStopY, int color, int ID)
    {
        this.ID=ID;
        modifyVariables(InputStartX,InputStartY,InputStopX,InputStopY,color);
    }
    private static int RoundToInt(float num)
    {
        return Math.round(num);
        //MainActivity.Count_RoundToInt++;
        /*int ret = (int) num;
        if(num>=0)
        {
            num+=0.5;
            //if((num - (float) ret) >= 0.5) ret++;
        }
        else
        {
            num-=0.5;
            //if((num - (float) ret) <= -0.5) ret--;
        }
        //return ret;
        return (int)num;//*/
    }

    private void SetUpPixelArray(int StartX, int StartY, int StopX, int StopY)
    {
        int dX;
        int dY;
        int XOffs;
        int YOffs;

        if(StopX<StartX)
        {
            dX=StartX-StopX;
            XOffs = StopX;
        }
        else
        {
            dX=StopX-StartX;
            XOffs = StartX;
        }
        if(StopY<StartY)
        {
            dY=StartY-StopY;
            YOffs = StopY;
        }
        else
        {
            dY = StopY-StartY;
            YOffs = StartY;
        }
        float m=(float)(dY)/dX;
        if(dX>dY)
        {
            iter=dX;
            //Pixels = new int[iter+1][2];
            int looper=0;
            do
            {
                Pixels[looper][0]=XOffs+looper;
                Pixels[looper][1]=RoundToInt(m*((float)looper)+(float)YOffs);
                looper++;
            }
            while(looper<iter);
        }
        else
        {
            iter=dY;
            //Pixels = new int[iter+1][2];
            int looper=0;
            do
            {
                Pixels[looper][1]=YOffs+looper;
                Pixels[looper][0]=RoundToInt(  (float)(looper)/m+(float)XOffs);
                looper++;
            }
            while(looper<iter);
        }
    }
    public void modifyVariables(int InputStartX, int InputStartY, int InputStopX, int InputStopY,int color)
    {
        LineColor = color;
        SetUpPixelArray(InputStartX,InputStartY,InputStopX,InputStopY);
    }
    public void modifyVariables(int InputStartX, int InputStartY, int InputStopX, int InputStopY)
    {
        SetUpPixelArray(InputStartX,InputStartY,InputStopX,InputStopY);
    }
    public void modifyVariables(int color)
    {
        LineColor = color;
    }
}
