package com.walichin.archivoalejos.buscafichas.models;

import android.graphics.Bitmap;

/**
 * Created by bpncool on 2/23/2016.
 */
public class Image {

    private final String filename;
    private final String url_google_drive;
    private final Bitmap bitmap;

    public Image(String filename, String url_google_drive, Bitmap bitmap) {
        this.filename = filename;
        this.url_google_drive = url_google_drive;
        this.bitmap = bitmap;
    }

    public String getFileName() {
        return filename;
    }

    public String getUrlGoogleDrive() {
        return url_google_drive;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
