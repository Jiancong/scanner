package com.fzcyjh.invoicescanner.manager;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

/**
 * Created by zhangyb on 2018/6/12.
 */

public class BitmapCache {
    private Bitmap mCache;
    static private BitmapCache mThis;

    static public BitmapCache getInstance(){
        if(mThis == null) {
            mThis = new BitmapCache();
        }

        return mThis;
    }

    public  void    setBitmap(String strKey, Bitmap bmp){
        mCache = bmp;
    }

    public Bitmap   getBitmap(String strKey){
        return  mCache;
    }

    static public     byte[]    bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

}
