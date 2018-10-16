package com.fzcyjh.invoicescanner.activitys;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fzcyjh.invoicescanner.R;
import com.fzcyjh.invoicescanner.common.utils;
import com.fzcyjh.invoicescanner.manager.BitmapCache;
import com.fzcyjh.invoicescanner.manager.ConfigManager;
import com.fzcyjh.invoicescanner.manager.UploadImageManager;

public class PreviewActivity extends Activity implements  View.OnClickListener{
    private  static final  String TAG = "PreviewActivity";
    private final int AUTO_UPLOAD_DISPLAY_TIME = 3000;

    private UploadImageManager mUploadManager= new UploadImageManager();
    private  String mImagePath = "";
    private Handler myHandler = new MyHandler();
    private ProgressDialog  mDlgProgress;
    private TextView mTVInfo;
    private  String mStrLastUploadFileName;
    private boolean mCancelUpload=false;
    private boolean mIsUploading=false;
    private Bitmap      mBmpPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        initContrls();

        mUploadManager.setHandler(myHandler);
        mUploadManager.initClient();
        //String strKey = savedInstanceState.getString("bmp_key");
        mBmpPreview = BitmapCache.getInstance().getBitmap("xxx");

        utils.codeUsedTime codeTime = new utils.codeUsedTime();
        ImageView imgView = (ImageView)findViewById(R.id.img1);
        /*
        String strFilePath = Environment.getExternalStorageDirectory() + "/000000/null_test.jpg";
        mImagePath = strFilePath;
        */
        //if(!strImagePath.isEmpty())
        //    strFilePath = strImagePath;
        Log.d(TAG, "setImageURI, begin");
        imgView.setImageBitmap(mBmpPreview);
        //imgView.setImageURI(Uri.fromFile(new File(strFilePath)));
        Log.d(TAG, String.format("setImageURI end, used:%d ms", codeTime.tickCurrent()));

        // 延迟3秒自动上传
        if(isAutoUpload()) {
            new android.os.Handler().postDelayed(new Runnable() {
                public void run() {
                    onClickBtnUpLoad();
                }
            }, AUTO_UPLOAD_DISPLAY_TIME);
        }
    }

    private void initContrls(){
        ((Button)findViewById(R.id.btn_back)).setOnClickListener(this);
        ((Button)findViewById(R.id.btn_upload)).setOnClickListener(this);
        ((Button)findViewById(R.id.btn_exit)).setOnClickListener(this);
        ((Button)findViewById(R.id.btn_setting)).setOnClickListener(this);
        mTVInfo = (TextView)findViewById(R.id.tv_info);
        mTVInfo.setText("");
    }

    private void onUploadImage(String strImage){
        if(mStrLastUploadFileName == strImage) {
            Toast.makeText(this, "文件已经被上传!", Toast.LENGTH_SHORT).show();
            return;
        }
        //EditText edtUploadUrl = (EditText)findViewById(R.id.edt_updateurl);
        String strUrl = ConfigManager.getString(ConfigManager.CONF_URL_UPLOAD);
        mUploadManager.setUploadUrl(strUrl);
        mUploadManager.setImagePath(strImage);
        mUploadManager.uploadImage("111");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(utils.isClickMediaKey(keyCode) && !mIsUploading) {
            Log.d(TAG, "onKeyDown onBackPressed");
            mCancelUpload = true;
            onBackPressed();
            if(isAutoUpload())
                Toast.makeText(this, "上传已取消！", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // 如果按了媒体键就直接返回
        if(utils.isClickMediaKey(keyCode) && !mIsUploading) {
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, String.format("onClick,id:%d", v.getId()));
        switch (v.getId()){
            case R.id.btn_back: {
                onBackPressed();
            }
                break;
            case R.id.btn_upload: {
                onClickBtnUpLoad();
            }
                break;
            case R.id.btn_exit: {
                //finishFromChild(this);
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                PreviewActivity.this.finish();
            }
                break;
            case R.id.btn_setting:{
                Intent intent = new Intent(PreviewActivity.this, SettingActivity.class);
                startActivity(intent);
            }
                break;
        }
    }

    private  void onClickBtnUpLoad(){
        Log.d(TAG,"onCtnUpLoad");
        Log.d(TAG, "onPictureTaken end");
        if(mCancelUpload || mIsUploading)
            return;

        mIsUploading = true;
        //Intent intent = new Intent();
        //Bundle bundle = new Bundle();
        //bundle.putString("path", "xxx");
        //intent.putExtras(bundle);
        onUploadImage(mImagePath);
        //setResult(Activity.RESULT_OK, intent);
        //PreviewActivity.this.finish();
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UploadImageManager.UPDATE_BEGIN:{
                    //
                    mDlgProgress = new ProgressDialog(PreviewActivity.this, android.app.AlertDialog.THEME_HOLO_LIGHT);
                    mDlgProgress.setMax(100);
                    //mDlgProgress.setTitle("正在上传");
                    mDlgProgress.setMessage("文件正在上传中,请稍后...");
                    mDlgProgress.setCancelable(false);
                    mDlgProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mDlgProgress.setIndeterminate(false);
                    mDlgProgress.show();
                    //Toast.makeText(PreviewActivity.this, "正在上传...", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case UploadImageManager.UPDATE_PROCESS: {
                    Bundle bundle = msg.getData();
                    int nPercent = bundle.getInt("percent", 0);
                    mDlgProgress.setProgress(nPercent);
                    }
                    break;
                case UploadImageManager.UPDATE_RESULT_FAILED: {
                    Toast.makeText(PreviewActivity.this, "上传失败!", Toast.LENGTH_LONG).show();
                    mDlgProgress.dismiss();
                    }
                    break;
                case UploadImageManager.UPDATE_RESULT_SUCCESS: {
                    Toast.makeText(PreviewActivity.this, "上传成功!", Toast.LENGTH_LONG).show();
                    mStrLastUploadFileName = mImagePath;
                    mDlgProgress.dismiss();
                    mIsUploading = false;
                    if(isAutoUpload())
                        onBackPressed();
                    }
                    break;
            }
        }
    }
    private boolean isAutoUpload(){
        return  ConfigManager.getBoolean(ConfigManager.CONF_SETTING_QUICKUPLOAD, false);
    }
}
