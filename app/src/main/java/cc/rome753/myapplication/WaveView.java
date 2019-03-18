package cc.rome753.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Random;

/**
 * Created by chao on 19-3-15.
 */

public class WaveView extends View{

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(5);
    }

    int x = 0;
    Random random = new Random();
    Paint paint = new Paint();
    int[] list;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = getMeasuredWidth();
        if(list == null && w > 0) {
            list = new int[w];
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long time = System.currentTimeMillis();
        if(x > 0) {
            list[x] = list[x - 1] + (random.nextBoolean() ? 1 : -1);
        } else {
            list[x] = random.nextInt(100) + 50;
        }
        int i = 0;
        for(int j = x + 1; j < list.length; j++) {
            canvas.drawPoint(i++, list[j], paint);
        }
        for(int j = 0; j < x + 1; j++) {
            canvas.drawPoint(i++, list[j], paint);
        }
        x = (x + 1) % list.length;
        invalidate();
        Log.e("chao", "draw time " + (System.currentTimeMillis() - time));
    }
}
