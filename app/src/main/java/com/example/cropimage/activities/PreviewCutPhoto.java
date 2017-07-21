package com.example.cropimage.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.cropimage.DataSingleton;
import com.example.cropimage.R;
import com.example.cropimage.widget.CutPhotoView;
import com.example.cropimage.widget.Panel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PreviewCutPhoto extends AppCompatActivity {
    @BindView(R.id.previewcut)
    Panel previewcut;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.textview)
    TextView textView;
    Canvas canvas;
    Paint paint;
    boolean flgPathDraw = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_cut_photo);
//        setContentView(new Panel(this));

        ButterKnife.bind(this);

        int widthOfscreen = 0;
        int heightOfScreen = 0;

        DisplayMetrics dm = new DisplayMetrics();
        try {
            getWindowManager().getDefaultDisplay().getMetrics(dm);
        } catch (Exception ex) {
        }
        widthOfscreen = dm.widthPixels;
        heightOfScreen = dm.heightPixels;
        Bitmap
            bitmap2 =BitmapFactory.decodeFile(DataSingleton.getInstance().uriImage);
        Bitmap resultingImage = null;
        resultingImage = Bitmap.createBitmap(widthOfscreen,
                heightOfScreen,bitmap2.getConfig());
        Canvas canvas = new Canvas(resultingImage);
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Path path = new Path();

        for (int i = 0; i < CutPhotoView.points.size(); i++) {
            path.lineTo(CutPhotoView.points.get(i).x, CutPhotoView.points.get(i).y);
        }
        canvas.drawPath(path, paint);
        if (DataSingleton.getInstance().crop) {
            // paint.setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.DST_IN));
           paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        } else {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        }
        paint.setFilterBitmap(false);
        paint.setColor(Color.RED);
        paint.setAlpha(100);
        canvas.drawBitmap(bitmap2, 0, 0, paint);

        previewcut.setImageBitmap(resultingImage);
    
        final Bitmap finalResultingImage = resultingImage;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setText(String.valueOf(progress));
                blurfast(finalResultingImage,progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
              //  int alp =(int) (progress * 2.55f);
               // blurfast(finalResultingImage,seekBar.getProgress());
                //  previewcut.setImageBitmap(makeRadGrad());
            }
        });
    }
    static void blurfast(Bitmap bmp, int radius) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int[] pix = new int[w * h];
        bmp.getPixels(pix, 0, w, 0, 0, w, h);

        for(int r = radius; r >= 1; r /= 2) {
            for(int i = r; i < h - r; i++) {
                for(int j = r; j < w - r; j++) {
                    int tl = pix[(i - r) * w + j - r];
                    int tr = pix[(i - r) * w + j + r];
                    int tc = pix[(i - r) * w + j];
                    int bl = pix[(i + r) * w + j - r];
                    int br = pix[(i + r) * w + j + r];
                    int bc = pix[(i + r) * w + j];
                    int cl = pix[i * w + j - r];
                    int cr = pix[i * w + j + r];

                    pix[(i * w) + j] = 0xFF000000 |
                            (((tl & 0xFF) + (tr & 0xFF) + (tc & 0xFF) + (bl & 0xFF) + (br & 0xFF) + (bc & 0xFF) + (cl & 0xFF) + (cr & 0xFF)) >> 3) & 0xFF |
                            (((tl & 0xFF00) + (tr & 0xFF00) + (tc & 0xFF00) + (bl & 0xFF00) + (br & 0xFF00) + (bc & 0xFF00) + (cl & 0xFF00) + (cr & 0xFF00)) >> 3) & 0xFF00 |
                            (((tl & 0xFF0000) + (tr & 0xFF0000) + (tc & 0xFF0000) + (bl & 0xFF0000) + (br & 0xFF0000) + (bc & 0xFF0000) + (cl & 0xFF0000) + (cr & 0xFF0000)) >> 3) & 0xFF0000;
                }
            }
        }
        bmp.setPixels(pix, 0, w, 0, 0, w, h);
    }
    private Bitmap makeRadGrad() {
        RadialGradient gradient = new RadialGradient(200, 200, 200, 0xFFFFFFFF,
                0xFF000000, android.graphics.Shader.TileMode.CLAMP);
        Paint p = new Paint();
        p.setDither(true);
        p.setShader(gradient);

        Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        c.drawCircle(200, 200, 200, p);

        return bitmap;
    }
    public boolean generateBitmap() {
       /* BitmapDrawable drawable = (BitmapDrawable) previewcut.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        Canvas canvas = new Canvas(bitmap);
        previewcut.draw(canvas);
        String iamgePath =saveImage(bitmap);*/
        return true;
    }
    public  String saveImage(Bitmap bitmap) {
        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filename;
    }

    private  String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "CropImage");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/"
                +System.currentTimeMillis()+ ".jpg");

        return uriSting;
    }
    private Bitmap ShrinkBitmap(int width) throws FileNotFoundException {
        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(DataSingleton.getInstance().uriImage, bmpFactoryOptions);
        int widthRatio = (int) (float) Math.ceil(bmpFactoryOptions.outWidth / (float) width);
        bmpFactoryOptions.inSampleSize = widthRatio;
        if (bmpFactoryOptions.inSampleSize <= 0)
            bmpFactoryOptions.inSampleSize = 0;
        bmpFactoryOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(DataSingleton.getInstance().uriImage, bmpFactoryOptions);
        return bitmap;

    }
}
