package com.example.cropimage.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.cropimage.DataSingleton;
import com.example.cropimage.R;
import com.example.cropimage.widget.CutPhotoView;

import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CutPhotoActivity extends AppCompatActivity {
    @BindView(R.id.photoimage)
    CutPhotoView cutPhotoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cut_photo);
        ButterKnife.bind(this);
        cutPhotoView.setImageBitmap(BitmapFactory.decodeFile(DataSingleton.getInstance().uriImage));
    }

    @Override
    protected void onStart() {
        super.onStart();
        cutPhotoView.resetView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.reset) {
            cutPhotoView.resetView();
        }
        if (id == R.id.crop) {
            DataSingleton.getInstance().crop = true;
            startActivity(new Intent(getApplicationContext(), PreviewCutPhoto.class));
        }
        if (id == R.id.noncrop) {
            DataSingleton.getInstance().crop = false;
            startActivity(new Intent(getApplicationContext(), PreviewCutPhoto.class));
        }
       /* if (id == R.id.undo) {
            cutPhotoView.onClickUndo();
        }*/
        return super.onOptionsItemSelected(item);
    }

    private Bitmap ShrinkBitmap(int width)throws FileNotFoundException {
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
