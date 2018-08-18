package tz.co.wadau.bibleafrikaans.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;


public class LinedEditText extends AppCompatEditText {
    private Rect mRect;
    private Paint mPaint;
    private String horizontalLineColor = "#5e6163";

    // we need this constructor for LayoutInflater
    public LinedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        mRect = new Rect();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.parseColor(horizontalLineColor));
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int height = getHeight();
        int line_height = getLineHeight();

        int count = height / line_height;

        if (getLineCount() > count)
            count = getLineCount();//for long text with scrolling

        int baseline = getLineBounds(0, mRect) + 15;//first line

        for (int i = 0; i < count; i++) {

            canvas.drawLine(mRect.left, baseline, mRect.right, baseline, mPaint);
            baseline += getLineHeight();//next line
        }

        super.onDraw(canvas);
    }

}