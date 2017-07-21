package com.example.cropimage.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class CutPhotoView extends ImageView implements View.OnTouchListener {
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
    public CutPhotoView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public CutPhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setPathEffect(new DashPathEffect(new float[] { 10, 20 }, 0));
        paint.setStrokeWidth(10);
      paint.setColor(Color.WHITE);
        this.setOnTouchListener(this);
        points = new ArrayList<Point>();
        bfirstpoint = false;
        mmatrix = new Matrix();
        dm = getResources().getDisplayMetrics();
        mScreenwidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        zoomPos = new PointF(0, 0);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        setBitmap(bm);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap != null) {
            Path path = new Path();
            boolean first = true;
            for (int i = 0; i < points.size(); i += 2) {
                Point point = points.get(i);
                if (first) {
                    first = false;
                    path.moveTo(point.x, point.y);
                } else if (i < points.size() - 1) {
                    Point next = points.get(i + 1);
                    path.quadTo(point.x, point.y, next.x, next.y);
                } else {
                    mlastpoint = points.get(i);
                    path.lineTo(point.x, point.y);

                }
            }
            canvas.drawPath(path, paint);

           // showZoom(canvas);
        }
    }

    private void showZoom(Canvas canvas)    {
        if (!zooming) {
            buildDrawingCache();
        } else {
            bitmap = getDrawingCache();
            BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            Paint paints = new Paint();
            paints.setShader(shader);
            mmatrix.reset();
            mmatrix.postScale(2f, 2f, zoomPos.x, zoomPos.y);
            paints.getShader().setLocalMatrix(mmatrix);
            RectF src = new RectF(zoomPos.x - 50, zoomPos.y - 50, zoomPos.x + 50, zoomPos.y + 50);
            RectF dst = new RectF(0, 0, 200, 200);
            mmatrix.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER);
            mmatrix.postScale(2f, 2f);
            paints.getShader().setLocalMatrix(mmatrix);
         //paints.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
            canvas.drawCircle(100, 100, 200, paints);
        }
    }

    public boolean onTouch(View view, MotionEvent event) {
        Point point = new Point();
        point.x = (int) event.getX();
        point.y = (int) event.getY();

        zoomPos.x = event.getX();
        zoomPos.y = event.getY();

        if (flgPathDraw) {
            zooming = true;
            if (bfirstpoint) {
                if (comparepoint(mfirstpoint, point)) {
                    points.add(mfirstpoint);
                } else {
                    points.add(point);
                }
            } else {
                points.add(point);
            }
            if (!(bfirstpoint)) {
                mfirstpoint = point;
                bfirstpoint = true;
            }
        }
        invalidate();
        if (event.getAction() == MotionEvent.ACTION_UP) {
           // paths.add(path);
            mlastpoint = point;
            if (flgPathDraw) {
                if (points.size() > 12) {
                    if (!comparepoint(mfirstpoint, mlastpoint)) {
                        zooming = false;
                        points.add(mfirstpoint);
                    }
                }
            }
        }
        return true;
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


    public void resetView() {
       // paths.clear();
        bfirstpoint = false;
        points.clear();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        flgPathDraw = true;
        invalidate();
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
