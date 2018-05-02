package com.example.bodnrbenjamin.ecg2_canvas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by BodnárBenjamin on 2018. 04. 28..
 * This object is a canvas on that curves can be drawn.
 * This object can be placeable on the graphical view of the layout.
 * It is faster than the ImageView object and more simple.
 */

public class DrawView extends View {
    Paint paint = new Paint();
    public int LineNumber=0;
    private int Lines[][]= new int[4][1000];

    private void init() {
        paint.setColor(Color.BLACK);
    }

    public DrawView(Context context) {
        super(context);
        init();
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public void onDraw(Canvas canvas) {

        for(int looper=0; looper < LineNumber;looper++)
        {
            canvas.drawLine(Lines[0][looper],Lines[1][looper],Lines[2][looper],Lines[3][looper], paint);
        }
        //canvas.setBitmap(b);
    }

    public void modifyLineWithoutRefresh(int LineId, int newStartX, int newStartY, int newStopX, int newStopY)
    {
        Lines[0][LineId]=newStartX;
        Lines[1][LineId]=newStartY;
        Lines[2][LineId]=newStopX;
        Lines[3][LineId]=newStopY;
        //LineNumber=LineId;
    }

    public void refresh()
    {
        invalidate();
        requestLayout();
    }

}
