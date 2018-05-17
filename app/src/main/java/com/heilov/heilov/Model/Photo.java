package com.heilov.heilov.Model;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by adeli on 5/16/2018.
 */

public class Photo {
    String bitmap;
    private String user;

    public Photo(String bitmap, String user) {

        this.bitmap = bitmap;
        this.user = user;
    }

    public void setBitmap(String bitmap) {
        this.bitmap = bitmap;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getBitmap() {
        return bitmap;
    }

    public String getUser() {
        return user;
    }

    public Photo() {
    }
}
