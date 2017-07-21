package com.example.cropimage.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by duchieu on 6/6/17.
 */

public class Panel extends ImageView implements View.OnTouchListener {
    Canvas mcanvas = new Canvas();
    Bitmap bm;
    Paint paint = new Paint();
    Path path = new Path();
    private float MAX_SCALE = 1.2f;
    private double halfDiagonalLength;
    private float oringinWidth = 0;
    private int mScreenwidth, mScreenHeight;
    private DisplayMetrics dm;
    private float MIN_SCALE = 0.5f;
    private Matrix mmatrix;
    public Panel(Context context) {
        super(context);
    init();
    }

    public Panel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init(){
        paint.setAlpha(0);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(50);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);
        mmatrix = new Matrix();
        dm = getResources().getDisplayMetrics();
        mScreenwidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(bm != null){
           mcanvas.drawPath(path,paint);

            mcanvas.setBitmap(bm);
           canvas.drawBitmap(bm,0,0,null);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        Log.d("TAG",x+"-"+y);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x,y);
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x,y);
                break;
        }
        invalidate();
        return true;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        setBitmap(bm);
    }

    public void setBitmap(Bitmap b) {
        mmatrix.reset();
         bm = b;
        setDiagonalLength();
        initBitmaps();
        int w = b.getWidth();
        int h = b.getHeight();
        oringinWidth = w;
        float initScale = (MIN_SCALE + MAX_SCALE) / 2;
        mmatrix.postScale(initScale, initScale, w / 2, h / 2);
        mmatrix.postTranslate(mScreenwidth / 2 - w / 2, (mScreenwidth) / 2 - h / 2);
        invalidate();
    }

    private void initBitmaps() {
        if (bm.getWidth() >= bm.getHeight()) {
            float minWidth = mScreenwidth / 8;
            if (bm.getWidth() < minWidth) {
                MIN_SCALE = 1f;
            } else {
                MIN_SCALE = 1.0f * minWidth / bm.getWidth();
            }

            if (bm.getWidth() > mScreenwidth) {
                MAX_SCALE = 1;
            } else {
                MAX_SCALE = 1.0f * mScreenwidth / bm.getWidth();
            }
        } else {
            float minHeight = mScreenwidth / 8;
            if (bm.getHeight() < minHeight) {
                MIN_SCALE = 1f;
            } else {
                MIN_SCALE = 1.0f * minHeight / bm.getHeight();
            }

            if (bm.getHeight() > mScreenwidth) {
                MAX_SCALE = 1;
            } else {
                MAX_SCALE = 1.0f * mScreenwidth / bm.getHeight();
            }
        }
    }

    private void setDiagonalLength() {
        halfDiagonalLength = Math.hypot(bm.getWidth(), bm.getHeight()) / 2;
    }
}
