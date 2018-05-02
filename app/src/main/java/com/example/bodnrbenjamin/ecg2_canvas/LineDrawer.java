package com.example.bodnrbenjamin.ecg2_canvas;

/**
 * Created by BodnárBenjamin on 2018. 02. 25..
 * This class is a complete line drawer module.
 */
import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.ImageView;

@SuppressWarnings("unused")
public class LineDrawer {
    private Bitmap bitmap;
    private ImageView mImageView;
    private int mImageViewHeigh=0;
    private int mImageViewWidth=0;
    private final int ArraySize=2000;
    private final Line[] LineArray = new Line[ArraySize];
    private final int[] FreeSpacesInLineArray = new int[ArraySize];

    /*This method sets the needed pixels of a line that in the LineArray*/

    private void setPixels3(int index)
    {
        int looper=LineArray[index].iter;
        while(looper != 0)
        {
            looper--;
            bitmap.setPixel(LineArray[index].Pixels[looper][0],LineArray[index].Pixels[looper][1], LineArray[index].LineColor);
        }
    }

    public LineDrawer(ImageView InputImageView)
    {

        mImageView = InputImageView;
        for(int looper=0;looper<ArraySize;looper++){
            FreeSpacesInLineArray[looper]=looper;
            LineArray[looper] = null;
        }

    }

    public void setBitmapSize(int BitmapHeight, int BitmatWidth)
    {
        mImageViewHeigh=BitmapHeight;
        mImageViewWidth=BitmatWidth;
        bitmap = Bitmap.createBitmap(mImageViewWidth,mImageViewHeigh,Bitmap.Config.ARGB_8888);
    }

    public void addLine(int InputStartX, int InputStartY, int InputStopX, int InputStopY, int color, int ID)
    {
        int looper = 0;
        boolean sign= true;
        while(sign && looper <ArraySize-1)
        {
            if(LineArray[looper] == null)
            {
                sign= false;
            }
            else looper++;
        }
        if(sign) return;
        LineArray[looper]= new Line(InputStartX, InputStartY, InputStopX, InputStopY, color, ID);
        setPixels3(looper);
        mImageView.setImageBitmap(bitmap);
    }

    /*This method can both the position and the color of the line modify*/
    public void modifyLine(int LineId, int newStartX, int newStartY, int newStopX, int newStopY, int newColor)
    {

        //bitmap.eraseColor(Color.TRANSPARENT);
        bitmap = Bitmap.createBitmap(mImageViewWidth,mImageViewHeigh,Bitmap.Config.ARGB_8888);
        int looper = 0;
        boolean sign = true;
        /*The following cycle looks for the desired line in the array of lines*/
        while(sign && (looper < ArraySize))
        {
            if(LineArray[looper] != null) sign= !(LineArray[looper].ID == LineId);
            looper++;
        }
        looper--;
        if(sign) return;
        LineArray[looper].modifyVariables(newStartX,newStartY,newStopX,newStopY,newColor);
        looper = ArraySize;
        do
        {
            looper--;
            if((LineArray[looper]!= null) && (LineArray[looper].ID != 0))
            {
                setPixels3(looper);
            }
        }
        while(looper != 0);

        mImageView.setImageBitmap(bitmap);
    }

    /*This method can only the pozition of the line modify*/
    public void modifyLine(int LineId, int newStartX, int newStartY, int newStopX, int newStopY)
    {
        //bitmap.eraseColor(Color.TRANSPARENT);
        bitmap = Bitmap.createBitmap(mImageViewWidth,mImageViewHeigh,Bitmap.Config.ARGB_8888);
        int looper;
        boolean sign= true;
        looper=0;
        /*The following cycle looks for the desired line in the array of lines*/
        while(sign && (looper < ArraySize))
        {
            if(LineArray[looper] != null) sign= !(LineArray[looper].ID == LineId);
            looper++;
        }
        looper--;
        if(sign) return;
        LineArray[looper].modifyVariables(newStartX,newStartY,newStopX,newStopY);
        looper = ArraySize;
        do
        {
            looper--;
            if((LineArray[looper]!= null) && (LineArray[looper].ID != 0)) setPixels3(looper);
        }
        while(looper != 0);
        mImageView.setImageBitmap(bitmap);
    }

    /*This method can only the color of line modify*/
    public void modifyLine(int LineId, int newColor)
    {
        bitmap = Bitmap.createBitmap(mImageViewWidth,mImageViewHeigh,Bitmap.Config.ARGB_8888);
        int looper;
        boolean sign= true;
        looper = 0;
        /*The following cycle looks for the desired line in the array of lines*/
        while(sign && (looper < ArraySize))
        {
            if(LineArray[looper] != null) sign= !(LineArray[looper].ID == LineId);
            looper++;
        }
        looper--;
        if(sign) return;
        LineArray[looper].modifyVariables(newColor);

        looper = ArraySize;
        do
        {
            looper--;
            if((LineArray[looper]!= null) && (LineArray[looper].ID != 0)) setPixels3(looper);
        }
        while(looper != 0);
        mImageView.setImageBitmap(bitmap);
        //mImageView.set
    }

    /*This method deletes the line from the array of lines*/
    public void deleteLine(int LineId)
    {
        bitmap = Bitmap.createBitmap(mImageViewWidth,mImageViewHeigh,Bitmap.Config.ARGB_8888);
        int looper = 0;
        boolean sign = true;
        /*The following cycle looks for the desired line in the array of lines*/
        while(sign && (looper < ArraySize))
        {
            if(LineArray[looper] != null) sign= !(LineArray[looper].ID == LineId);
            looper++;
        }
        looper--;
        if(sign) return;
        LineArray[looper]=null;

        for(looper=0;looper<ArraySize;looper++) // the whole array of lines will be checked
        {
            /* If the actual line object is not null, it can be check the ID, and if it is correct we can set the pixels */
            if((LineArray[looper]!= null) && (LineArray[looper].ID != 0)) setPixels3(looper);
        }
        mImageView.setImageBitmap(bitmap);
    }
    public void refresh()
    {
        //bitmap = Bitmap.createBitmap(mImageViewWidth,mImageViewHeigh,Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.YELLOW);
        int looper = ArraySize;
        do
        {
            looper--;
            if((LineArray[looper]!= null) && (LineArray[looper].ID != 0)) setPixels3(looper);
        }
        while(looper != 0);
        mImageView.setImageBitmap(bitmap);
    }
    /*This method can both the position and the color of the line modify*/
    public void modifyLineWithoutRefresh(int LineId, int newStartX, int newStartY, int newStopX, int newStopY, int newColor)
    {
        int looper = 0;
        boolean sign = true;
        /*The following cycle looks for the desired line in the array of lines*/
        while(sign && (looper < ArraySize))
        {
            if(LineArray[looper] != null) sign= !(LineArray[looper].ID == LineId);
            looper++;
        }
        looper--;
        if(sign) return;
        LineArray[looper].modifyVariables(newStartX,newStartY,newStopX,newStopY,newColor);
    }

    /*This method can only the pozition of the line modify*/
    public void modifyLineWithoutRefresh(int LineId, int newStartX, int newStartY, int newStopX, int newStopY)
    {
        int looper;
        boolean sign= true;
        looper=0;
        /*The following cycle looks for the desired line in the array of lines*/
        while(sign && (looper < ArraySize))
        {
            if(LineArray[looper] != null) sign= !(LineArray[looper].ID == LineId);
            looper++;
        }
        looper--;
        if(sign) return;
        LineArray[looper].modifyVariables(newStartX,newStartY,newStopX,newStopY);
    }

    /*This method can only the color of line modify*/
    public void modifyLineWithoutRefresh(int LineId, int newColor)
    {
        int looper;
        boolean sign= true;
        looper = 0;
        /*The following cycle looks for the desired line in the array of lines*/
        while(sign && (looper < ArraySize))
        {
            if(LineArray[looper] != null) sign= !(LineArray[looper].ID == LineId);
            looper++;
        }
        looper--;
        if(sign) return;
        LineArray[looper].modifyVariables(newColor);
    }

    /*This method deletes the line from the array of lines*/
    public void deleteLineWithoutRefresh(int LineId)
    {
        int looper = 0;
        boolean sign = true;
        /*The following cycle looks for the desired line in the array of lines*/
        while(sign && (looper < ArraySize))
        {
            if(LineArray[looper] != null) sign= !(LineArray[looper].ID == LineId);
            looper++;
        }
        looper--;
        if(sign) return;
        LineArray[looper]=null;
    }
}


