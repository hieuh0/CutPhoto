package com.example.cropimage.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by duchieu on 6/6/17.
 */

public class ViewPhoto extends ImageView implements View.OnTouchListener {
    private Paint paint;
    public static List<Point> points;
    boolean flgPathDraw = true;
    Point mfirstpoint = null;
    boolean bfirstpoint = false;
    Point mlastpoint = null;
    private Matrix mmatrix;
    Context mContext;
    Bitmap bitmap;
    private float MAX_SCALE = 1.2f;
    private double halfDiagonalLength;
    private float oringinWidth = 0;
    private int mScreenwidth, mScreenHeight;
    private DisplayMetrics dm;
    private float MIN_SCALE = 0.5f;
    private PointF zoomPos;
    private boolean zooming = false;
    private int x = 0;
    private int y = 0;
    Path circlePath;
    Paint circlePaint;
    Canvas bitmapCanvas;


    private final Paint eraserPaint = new Paint();


    public ViewPhoto(Context context) {
        super(context);
        mContext = context;
        init();
    }
    public ViewPhoto(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }


    private void init() {

        setFocusable(true);
        setFocusableInTouchMode(true);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
       // paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        paint.setMaskFilter(new BlurMaskFilter(8,BlurMaskFilter.Blur.NORMAL));
        paint.setStyle(Paint.Style.STROKE);
        //paint.setPathEffect(new DashPathEffect(new float[] { 10, 20 }, 0));
        paint.setStrokeWidth(50);
      //  paint.setColor(Color.WHITE);
        this.setOnTouchListener(this);
        points = new ArrayList<Point>();
        bfirstpoint = false;
        mmatrix = new Matrix();
        dm = getResources().getDisplayMetrics();
        mScreenwidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        zoomPos = new PointF(0, 0);
        bitmapCanvas = new Canvas();
        bitmapCanvas.setBitmap(bitmap);
        bitmapCanvas.drawColor(Color.BLUE);

        // Set eraser paint properties
        eraserPaint.setAlpha(0);
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        eraserPaint.setAntiAlias(true);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        setBitmap(bm);
    }

    public void setBitmap(Bitmap bm) {

        mmatrix.reset();
        bitmap = bm;
        setDiagonalLength();
        initBitmaps();
        int w = bm.getWidth();
        int h = bm.getHeight();
        oringinWidth = w;
        float initScale = (MIN_SCALE + MAX_SCALE) / 2;
        mmatrix.postScale(initScale, initScale, w / 2, h / 2);
        mmatrix.postTranslate(mScreenwidth / 2 - w / 2, (mScreenwidth) / 2 - h / 2);
        invalidate();
    }

    private void initBitmaps() {
        if (bitmap.getWidth() >= bitmap.getHeight()) {
            float minWidth = mScreenwidth / 8;
            if (bitmap.getWidth() < minWidth) {
                MIN_SCALE = 1f;
            } else {
                MIN_SCALE = 1.0f * minWidth / bitmap.getWidth();
            }

            if (bitmap.getWidth() > mScreenwidth) {
                MAX_SCALE = 1;
            } else {
                MAX_SCALE = 1.0f * mScreenwidth / bitmap.getWidth();
            }
        } else {
            float minHeight = mScreenwidth / 8;
            if (bitmap.getHeight() < minHeight) {
                MIN_SCALE = 1f;
            } else {
                MIN_SCALE = 1.0f * minHeight / bitmap.getHeight();
            }

            if (bitmap.getHeight() > mScreenwidth) {
                MAX_SCALE = 1;
            } else {
                MAX_SCALE = 1.0f * mScreenwidth / bitmap.getHeight();
            }
        }
    }

    private void setDiagonalLength() {
        halfDiagonalLength = Math.hypot(bitmap.getWidth(), bitmap.getHeight()) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap != null) {
            bitmapCanvas.drawColor(Color.BLUE);
            bitmapCanvas.drawCircle(x, y, 10, eraserPaint);

            canvas.drawBitmap(bitmap, 0, 0, paint);
        }
    }

    public boolean onTouch(View v, MotionEvent event) {
        x = (int) event.getX();
        y = (int) event.getY();

        invalidate();
        return true;
    }
    private boolean comparepoint(Point first, Point current) {
        int left_range_x = (int) (current.x - 3);
        int left_range_y = (int) (current.y - 3);

        int right_range_x = (int) (current.x + 3);
        int right_range_y = (int) (current.y + 3);

        if ((left_range_x < first.x && first.x < right_range_x)
                && (left_range_y < first.y && first.y < right_range_y)) {
            if (points.size() < 10) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
}
