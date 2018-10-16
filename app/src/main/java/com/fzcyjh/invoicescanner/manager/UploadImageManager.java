package com.fzcyjh.invoicescanner.manager;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.fzcyjh.invoicescanner.common.utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.github.lizhangqu.coreprogress.ProgressHelper;
import io.github.lizhangqu.coreprogress.ProgressUIListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zhangyb on 2018/6/4.
 */

public class UploadImageManager {
    private static final String TAG = "UploadImageManager";
    private OkHttpClient mOkHttpClient;
    private Handler mHandler;
    private String mStrImangePath;
    private String mStrUploadUrl;

    public static final  int UPDATE_BEGIN = 2000;
    public static final  int UPDATE_PROCESS = 2001;
    public static final  int UPDATE_RESULT_SUCCESS = 2002;
    public static final  int UPDATE_RESULT_FAILED = 2003;

    public void setHandler(Handler handler){
        mHandler = handler;
    }

    public void initClient() {
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1000, TimeUnit.MINUTES)
                .readTimeout(1000, TimeUnit.MINUTES)
                .writeTimeout(1000, TimeUnit.MINUTES)
                .build();
    }

    public void setImagePath(String strImagePath){
        mStrImangePath = strImagePath;
    }
    public void setUploadUrl(String strUploadUrl) {
        Log.d(TAG, "setUploadUrl:" + strUploadUrl);
        mStrUploadUrl = strUploadUrl;
    }

    public void uploadImageFile() {
        Log.d(TAG, "uploadImage：" + mStrImangePath + ", URL:" + mStrUploadUrl);
        if (!TextUtils.isEmpty(mStrImangePath)) {
            File file = new File(mStrImangePath);
            uploadImage(RequestBody.create(MediaType.parse("image/jpeg"), file));
        }
    }

    public void uploadImage(String strCacheKey) {
        Bitmap bmp = BitmapCache.getInstance().getBitmap(strCacheKey);
        if (bmp != null) {
            uploadImage(RequestBody.create(MediaType.parse("image/jpeg"), BitmapCache.bitmap2Bytes(bmp)));
        }
    }

    private void uploadImage(RequestBody fileForm) {
        if (fileForm != null) {
            //构造上传请求，类似web表单
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    //.addPart(Headers.of("Content-Disposition", "form-data; name=\"client_ver\""), RequestBody.create(null, "1.0"))
                    //.addPart(Headers.of("Content-Disposition", "form-data; name=\"client_type\""), RequestBody.create(null, "android"))
                    .addFormDataPart("client_ver", "1.0")
                    .addFormDataPart("client_type", "android")
                    //.addPart(Headers.of("Content-Disposition", "form-data; name=\"action\""), RequestBody.create(null, "idcard"))
                    //.addPart(Headers.of("Content-Disposition", "form-data; name=\"img\"; myfile=\"idcardFront_user.jpg\""), RequestBody.create(MediaType.parse("image/jpeg"), file))
                    .addFormDataPart("file", "test.jpg", fileForm)
                    .build();
            //这个是ui线程回调，可直接操作UI
            RequestBody progressRequestBody = ProgressHelper.withProgress(requestBody, new ProgressUIListener() {
                @Override
                public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                    Log.e("TAG", "numBytes:" + numBytes);
                    Log.e("TAG", "totalBytes" + totalBytes);
                    Log.e("TAG", percent * 100 + " % done ");
                    Log.e("TAG", "done:" + (percent >= 1.0));
                    Log.e("TAG", "================================");
                    Message obtain = Message.obtain();
                    obtain.what = UPDATE_PROCESS;
                    Bundle data = new Bundle();
                    data.putInt("percent", (int)(percent * 100));
                    obtain.setData(data);
                    mHandler.sendMessage(obtain);
                    //ui层回调
                    //mProgressBar.setProgress((int) (100 * percent));
                }
            });
            //进行包装，使其支持进度回调
            final Request request = new Request.Builder()
                    //.header("Host", "ocr.ccyunmai.com:8080")
                    //.header("Origin", "http://ocr.ccyunmai.com:8080")
                    //.header("Referer", "http://ocr.ccyunmai.com:8080/idcard/")
                    //.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2398.0 Safari/537.36")
                    .url(mStrUploadUrl)
                    .post(progressRequestBody)
                    .build();

            Message obtain = Message.obtain();
            obtain.what = UPDATE_BEGIN;
            mHandler.sendMessage(obtain);

            //开始请求
            mOkHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, e.toString());
                    Message obtain = Message.obtain();
                    obtain.what = UPDATE_RESULT_FAILED;
                    Bundle data = new Bundle();
                    data.putString("html", e.toString());
                    obtain.setData(data);
                    obtain.obj = e.toString();
                    mHandler.sendMessage(obtain);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    Log.d(TAG, "onResponse:" + response.toString());
                    Message obtain = Message.obtain();
                    obtain.what = (response.code() == 200 ? UPDATE_RESULT_SUCCESS : UPDATE_RESULT_FAILED);

                    Bundle data = new Bundle();
                    data.putString("html", result);
                    obtain.setData(data);
                    obtain.obj = result;
                    mHandler.sendMessage(obtain);
                    //Toast.makeText(PreviewActivity.this, result, Toast.LENGTH_LONG).show();
                    /*
                    Document parse = Jsoup.parse(result);
                    Elements select = parse.select("div#ocrresult");
                    Log.e("TAG", "select：" + select.text());
                    Message obtain = Message.obtain();
                    obtain.what = UPDATE_TEXTVIEW;
                    obtain.obj = select.text();
                    mHandler.sendMessage(obtain);
                    */
                }
            });
        }
    }

}
