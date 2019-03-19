package cc.rome753.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

/**
 * Created by chao on 19-3-15.
 */

public class SoundTexture extends TextureView implements TextureView.SurfaceTextureListener {

    public SoundTexture(Context context) {
        this(context, null);
    }

    public SoundTexture(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        gridPaint.setColor(Color.BLUE);
        gridPaint.setStyle(Paint.Style.FILL);
        gridPaint.setStrokeWidth(1);
        gridPaint.setTextSize(50);

        final HandlerThread handlerThread = new HandlerThread("drawing");
        handlerThread.start();

        handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                setPath();
                Canvas c = lockCanvas();
                if(c != null) {
                    drawBackground(c);
                    c.drawPath(path, paint);
                    unlockCanvasAndPost(c);
                    sendEmptyMessageDelayed(0, 10);
                }
            }
        };

        setSurfaceTextureListener(this);
    }

    int x = 0;
    Paint paint = new Paint();
    Path path = new Path();
    int[] list;

    Handler handler;
    Paint gridPaint = new Paint();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        if(list == null && w > 0) {
            list = new int[w];
        }
    }

    protected void setPath() {
        long time = System.currentTimeMillis();
        path.reset();
        int i = 0;
        for(int j = x + 1; j < list.length; j++) {
            path.lineTo(i++, list[j]);
        }
        for(int j = 0; j < x + 1; j++) {
            path.lineTo(i++, list[j]);
        }
        Log.e("chao", "setPath time " + (System.currentTimeMillis() - time));
    }

    public void update(int val) {
        x = (x + 1) % list.length;
        list[x] = val;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        handler.sendEmptyMessageDelayed(0, 500);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private void drawBackground(Canvas c) {
        int w = getWidth();
        c.drawColor(Color.WHITE);
        int i = 0;
        for(int tune : Tune.TUNES) {
            c.drawLine(0, tune, w, tune + 1, gridPaint);
            String name = Tune.NAMES[i];
            c.drawText(name, 0, tune, gridPaint);
            i = (i + 1) % Tune.NAMES.length;
        }
    }
}
