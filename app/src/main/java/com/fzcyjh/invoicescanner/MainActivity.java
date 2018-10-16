package com.fzcyjh.invoicescanner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;

import com.fzcyjh.invoicescanner.activitys.*;
import com.fzcyjh.invoicescanner.manager.ConfigManager;
import com.fzcyjh.invoicescanner.manager.UploadImageManager;

import java.io.File;

public class MainActivity extends AppCompatActivity implements  View.OnClickListener{
    private static final  String TAG = "MainActivity";
    private static final int GETPERMISSION_SUCCESS = 1;//获取权限成功
    private static final int GETPERMISSION_FAILER = 2;//获取权限失败
    private TextView mTvText;
    private ProgressBar mPrgUpload;
    private  EditText mEdtUploadUrl;
    private int MY_SCAN_REQUEST_CODE = 100;
    private Context mContext;
    private Handler myHandler = new MyHandler();
    private String mImagePath;
    private UploadImageManager mUploadManager= new UploadImageManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mUploadManager.setHandler(myHandler);
        mUploadManager.initClient();

        ((Button)findViewById(R.id.btn_go)).setOnClickListener(this);
        ((Button)findViewById(R.id.btn_test)).setOnClickListener(this);
        mPrgUpload = (ProgressBar) findViewById(R.id.upload_progress);
        mTvText = (TextView) findViewById(R.id.tv_text);
        mEdtUploadUrl = (EditText)findViewById(R.id.edt_updateurl);
        mEdtUploadUrl.setText(ConfigManager.getString(ConfigManager.CONF_URL_UPLOAD));
        mImagePath = Environment.getExternalStorageDirectory() + "/000000/null_test.jpg";

        // 不展示主界面的东西，因为要申请权限
        LinearLayout layMain = (LinearLayout)findViewById(R.id.activity_main);
        layMain.setVisibility(View.INVISIBLE);
        onClickGo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_go:
                onClickGo();
                break;
            case R.id.btn_test:
                onClickTest();
                break;
            default:
                break;
        }
    }

    private  void onClickGo(){
        requestAllPermission();
    }
    private void testDialogBuilder(){

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        // 设置主题的构造方法
        // AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialog);
        android.view.LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_guide, null);
        builder.setView(view);
        builder.show();
    }

    private  void onClickTest(){
        //Log.d(TAG, "onClickTest");
        //Intent scanIntent = new Intent(mContext, PreviewActivity.class);
        //startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
//        testDialogBuilder();

        //onUploadImage(mImagePath);
        //openPreviewImage();
        //Intent intent = new Intent(mContext, SettingActivity.class);
        Intent intent = new Intent(mContext, UploadListActivity.class);
        startActivityForResult(intent, MY_SCAN_REQUEST_CODE);
    }

    private void onUploadImage(String strImage){
        String strUrl = mEdtUploadUrl.getText().toString();
        mUploadManager.setUploadUrl(strUrl);
        mUploadManager.setImagePath(strImage);
     //   mUploadManager.uploadImage();
    }

    private  void openPreviewImage(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.setDataAndType(Uri.fromFile(new File(mImagePath)), "image/jpeg");
        //intent.setDataAndType(Uri.fromFile(new File(strFilePath)), "image/png");
        startActivity(intent);
    }
    private void requestAllPermission() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(MainActivity.this,
                new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        myHandler.sendEmptyMessage(GETPERMISSION_SUCCESS);
                    }

                    @Override
                    public void onDenied(String permission) {
                        myHandler.sendEmptyMessage(GETPERMISSION_FAILER);
                    }
                });
    }

    //因为权限管理类无法监听系统，所以需要重写onRequestPermissionResult方法，更新权限管理类，并回调结果。这个是必须要有的。
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
        if (requestCode == MY_SCAN_REQUEST_CODE) {
            Log.d(TAG, "onActivityResult， request:" + requestCode);
            this.finish();
            /*
            if(resultCode == RESULT_OK)
                onUploadImage(mImagePath);
            else  if(resultCode == RESULT_CANCELED)
                this.finish();
             */
            //String id = data.getStringExtra("id");
            //Toast.makeText(this, id, Toast.LENGTH_LONG).show();
            /*
            if (id != null && id.length() == 18) {
                mTvText.setText(id);
            }
            */
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GETPERMISSION_SUCCESS:
                    Intent scanIntent = new Intent(mContext, CameraActivity.class);
                    startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
                    break;
                case GETPERMISSION_FAILER:
                    Toast.makeText(mContext, "此功能须获摄像头权限!", Toast.LENGTH_LONG).show();
                    MainActivity.this.finish();;
                    break;
                case UploadImageManager.UPDATE_PROCESS:
                    Bundle bundle = msg.getData();
                    int nPercent =bundle.getInt("percent", 0);
                    mPrgUpload.setProgress(nPercent);
                    break;
                case UploadImageManager.UPDATE_RESULT_FAILED:
                    String strText = "上传失败:" + msg.obj.toString();
                    mTvText.setText(strText);
                    Toast.makeText(MainActivity.this, strText, Toast.LENGTH_SHORT);
                    break;
                case UploadImageManager.UPDATE_RESULT_SUCCESS:
                    String strText2 = "上传成功:" + msg.obj.toString();
                    mTvText.setText(strText2);
                    Toast.makeText(MainActivity.this, strText2, Toast.LENGTH_SHORT);
                    break;
            }
        }
    }
}
