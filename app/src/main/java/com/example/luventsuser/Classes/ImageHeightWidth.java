package com.example.luventsuser.Classes;

import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;

public class ImageHeightWidth {

    Uri mUri;

    int imageHeight,imageWidth;

    public int getImageHeight() {
        return imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public ImageHeightWidth(Uri mUri) {
        this.mUri = mUri;
        getDropboxIMGSize(mUri);
    }

    public ImageHeightWidth() {

    }

    private void getDropboxIMGSize(Uri uri){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
        imageHeight = options.outHeight;
        imageWidth = options.outWidth;

    }
}
