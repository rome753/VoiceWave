package cc.rome753.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

import java.util.Random;

/**
 * Created by chao on 19-3-15.
 */

public class SoundTexture extends TextureView implements TextureView.SurfaceTextureListener {

    public SoundTexture(Context context) {
        this(context, null);
    }

    public SoundTexture(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.RED);

        final HandlerThread handlerThread = new HandlerThread("drawing");
        handlerThread.start();

        handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                drawOutside(mCanvas);
                Canvas c = lockCanvas();
                if(c != null) {
                    c.drawBitmap(mBitmap, 0, 0, null);
                    unlockCanvasAndPost(c);
                    sendEmptyMessageDelayed(0, 60);
                }
            }
        };

        setSurfaceTextureListener(this);
    }

    int x = 0;
    Paint paint = new Paint();
    int[] list;

    Canvas mCanvas;
    Bitmap mBitmap;
    Handler handler;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        if(list == null && w > 0) {
            list = new int[w];
            mBitmap = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }
    }

    protected void drawOutside(Canvas canvas) {
        long time = System.currentTimeMillis();
        canvas.drawColor(Color.WHITE);
        int i = 0;
        for(int j = x + 1; j < list.length; j++) {
            canvas.drawCircle(i++, list[j], 2, paint);
        }
        for(int j = 0; j < x + 1; j++) {
            canvas.drawCircle(i++, list[j], 2, paint);
        }
        Log.e("chao", "drawOutside time " + (System.currentTimeMillis() - time));
    }

    public void update(int val) {
        x = (x + 1) % list.length;
        list[x] = val * 5;
//        handler.sendEmptyMessage(0);
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
}
