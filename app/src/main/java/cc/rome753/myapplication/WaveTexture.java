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

public class WaveTexture extends TextureView implements TextureView.SurfaceTextureListener {

    public WaveTexture(Context context) {
        this(context, null);
    }

    public WaveTexture(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.RED);

        HandlerThread handlerThread = new HandlerThread("drawing");
        handlerThread.start();

        handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                drawOutside(mCanvas);
                Canvas c = lockCanvas();
                if(c != null) {
                    c.drawBitmap(mBitmap, 0, 0, null);
                    unlockCanvasAndPost(c);
                    sendEmptyMessageDelayed(0, 10);
                }
            }
        };

        setSurfaceTextureListener(this);
    }

    int x = 0;
    Random random = new Random();
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
        if(x > 0) {
            list[x] = list[x - 1] + (random.nextBoolean() ? 1 : -1);
        } else {
            list[x] = random.nextInt(100) + 50;
        }
        int i = 0;
        for(int j = x + 1; j < list.length; j++) {
            canvas.drawCircle(i++, list[j], 2, paint);
        }
        for(int j = 0; j < x + 1; j++) {
            canvas.drawCircle(i++, list[j], 2, paint);
        }
        x = (x + 1) % list.length;
        Log.e("chao", "drawOutside time " + (System.currentTimeMillis() - time));
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        handler.sendEmptyMessage(0);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        handler.removeMessages(0);
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
