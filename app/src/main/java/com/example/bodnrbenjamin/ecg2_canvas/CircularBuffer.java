package com.example.bodnrbenjamin.ecg2_canvas;

/**
 * Created by BodnárBenjamin on 2018. 03. 28..
 * This class is creates and handles a circular buffer with the added size.
 */

public class CircularBuffer {
    private final int Buffersize;
    private final int[] Buffer;
    private int WriteIterator=0;
    private int ReadIterator=0;
    private final boolean[] BufferElementSign;
    private int Size=0;
    protected CircularBuffer(int size)
    {
        Buffersize=size;
        Buffer=new int[size];
        BufferElementSign = new boolean[Buffersize];
        for(int looper=0; looper < Buffersize;looper++)
        {
            Buffer[looper]=0;
            BufferElementSign[looper]=false;
        }
    }
    public void push(int data)
    {
        if(WriteIterator != Buffersize)
        {
            Buffer[WriteIterator]=data;

             if(BufferElementSign[WriteIterator])
             {
                 ReadIterator=WriteIterator+1;
             }
            WriteIterator++;
            //else BufferElementSign[0]=false;
            BufferElementSign[WriteIterator-1]=true;
        }
        else
        {
            Buffer[0]=data;

            if(BufferElementSign[0])
            {
                ReadIterator=1;
            }
            WriteIterator = 1;
            BufferElementSign[0]=true;

        }
        if(Size != Buffersize) Size++;
    }
    public int pull()
    {
        int ret;
        if(ReadIterator!=Buffersize)
        {
            ret = Buffer[ReadIterator];
            BufferElementSign[ReadIterator]=false;
            ReadIterator++;
        }
        else
        {
            ret=Buffer[0];
            BufferElementSign[0]=false;
            ReadIterator=1;
        }
        if(Size > 0) Size--;
        return ret;
    }
    public boolean isReadRelevant()
    {
        boolean ret;
        if(ReadIterator!=Buffersize)
        {
            ret = BufferElementSign[ReadIterator];
        }
        else
        {
            ret = BufferElementSign[0];
        }
        return ret;
    }
    public int UnreadSize()
    {
        return Size;
    }
}
