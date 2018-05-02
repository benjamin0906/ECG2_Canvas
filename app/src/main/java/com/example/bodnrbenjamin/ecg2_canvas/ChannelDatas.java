package com.example.bodnrbenjamin.ecg2_canvas;

/**
 * Created by BodnárBenjamin on 2018. 05. 02..
 */

public class ChannelDatas {
    public int Channel1Data[];
    public int Channel2Data[];
    public int Channel3Data[];
    public int Channel4Data[];
    public int Channel5Data[];

    public int Channel1Size;
    public int Channel2Size;
    public int Channel3Size;
    public int Channel4Size;
    public int Channel5Size;

    public ChannelDatas(int size)
    {
        Channel1Data = new int[size];
        Channel2Data = new int[size];
        Channel3Data = new int[size];
        Channel4Data = new int[size];
        Channel5Data = new int[size];

        Channel1Size = 0;
        Channel2Size = 0;
        Channel3Size = 0;
        Channel4Size = 0;
        Channel5Size = 0;
    }
}
