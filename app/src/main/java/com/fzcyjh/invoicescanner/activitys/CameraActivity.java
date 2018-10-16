package com.fzcyjh.invoicescanner.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fzcyjh.invoicescanner.R;
import com.fzcyjh.invoicescanner.common.utils;
import com.fzcyjh.invoicescanner.manager.*;
import com.fzcyjh.invoicescanner.views.*;


public class CameraActivity extends Activity implements SurfaceHolder.Callback{
    private static final  String TAG = "CameraActivity";
    private CameraManager cameraManager;
    private boolean hasSurface;
    private String type = "null";
    private Button btn_close, btn_resacn; //light, 
    private boolean toggleLight = false;
    private Handler mHandler;
    private TextView tv_lightstate, tv_input;
    private String sdPath;
    private int times = 0;
    private Long opentime;
    private Context mContext;
    private int ACT_PREVIEW_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        opentime = System.currentTimeMillis();
        sdPath = Environment.getExternalStorageDirectory() + "/000000/";
        setContentView(R.layout.activity_camera);
        //tv_lightstate = (TextView) findViewById(R.id.tv_openlight);
        mHandler = new Handler();
        initLayoutParams();

    }

    /**
     * 重置surface宽高比例为3:4，不重置的话图形会拉伸变形
     */
    private void initLayoutParams() {
//        ErrorView = findViewById(R.id.ll_cameraerrorview);

        btn_close = (Button) findViewById(R.id.btn_close);
        //light = (Button) findViewById(R.id.light);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                onBackPressed();
            }
        });
        ((Button)findViewById(R.id.btn_setting)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
        /*
        light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long time = System.currentTimeMillis();// 摄像头 初始化 需要时间
                if (time - opentime > 1000) {
                    opentime = time;
                    if (!toggleLight) {
                        toggleLight = true;
                        tv_lightstate.setText("关闭闪关灯");
                        cameraManager.openLight();
                    } else {
                        toggleLight = false;
                        tv_lightstate.setText("打开闪关灯");
                        cameraManager.offLight();
                    }
                }
            }
        });
        */
        Button btn_TakePhoto = (Button)findViewById(R.id.btn_takephoto);
        btn_TakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnClickTakePhoto();
            }
        });

//        btn_resacn = (Button) findViewById(R.id.btn_rescan);
//        btn_resacn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                times = 0;
//                ErrorView.setVisibility(View.GONE);
//            }
//        });
//        tv_input = (TextView) findViewById(R.id.tv_inputbyself);
//        tv_input.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setResult(RESULT_CANCELED);
//                onBackPressed();
//            }
//        });
//        iv_close = (ImageView) findViewById(R.id.iv_closetips);
//        iv_close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                times = 0;
//                ErrorView.setVisibility(View.GONE);
//
//            }
//        });

    }

    void onBtnClickTakePhoto(){
        Log.d(TAG, "click btn_TakePhoto");
        cameraManager.takePicture(null, null, myjpegCallback);
        Log.d(TAG, "click btn_TakePhoto end");
    }
    /**
     * 拍照回调
     */
    // TODO:放到线程里
    Camera.PictureCallback myjpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            utils.codeUsedTime codeTime = new utils.codeUsedTime();
            Log.d(TAG, "onPictureTaken");
            // 根据拍照所得的数据创建位图
            final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
                    data.length);
            Log.d(TAG, String.format("onPictureTaken decode used:%d ms",codeTime.tickCurrent()));
            int height = bitmap.getHeight();
            int width = bitmap.getWidth();
            Log.d(TAG, "height:" + height + ",width:" + width);
            RectF rtfRoi = PreviewBorderView.calcRoiRect(width, height);
            //final Bitmap bitmap1 = Bitmap.createBitmap(bitmap, (width - height) / 2, height / 6, height, height * 2 / 3);
            Rect rtRoi = new Rect((int)rtfRoi.left, (int)rtfRoi.top, (int)rtfRoi.right, (int)rtfRoi.bottom);
            final Bitmap bitmap1 = Bitmap.createBitmap(bitmap, rtRoi.left, rtRoi.top, rtRoi.width(), rtRoi.height());
            Log.d(TAG, String.format("cut bmp used:%d ms", codeTime.tickCurrent()));

            /*
            //Log.e("TAG", "x:" + (width - height) / 2 + " y:" + height / 6 + " width:" + height + " height:" + height * 2 / 3);
            // 创建一个位于SD卡上的文件

            File path = new File(sdPath);
            if (!path.exists()) {
                path.mkdirs();
            }
            File file = new File(path, type + "_" + "test.jpg");
            //File fileOrg = new File(path, type + "_" + "org.png");
            Log.d(TAG, "filepath:" + file.getPath());
            FileOutputStream outStream = null;
            //FileOutputStream outStreamOrg = null;
            try {
                Log.d(TAG, String.format("FileOutputStream used:%d ms", codeTime.tickCurrent()));
                // 打开指定文件对应的输出流
                outStream = new FileOutputStream(file);
                //outStreamOrg = new FileOutputStream(fileOrg);
                // 把位图输出到指定文件中
                //outStreamOrg.write(data);
                bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                Log.d(TAG, String.format("FileOutputStream end used:%d ms", codeTime.tickCurrent()));
                //bitmap1.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                //outStreamOrg.flush();
                //outStreamOrg.close();
                outStream.flush();
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG, String.format("file close used:%d ms", codeTime.tickCurrent()));
            Log.d(TAG, "onPictureTaken end");
            Intent previewIntent = new Intent(mContext, PreviewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("path", file.getAbsolutePath());
            bundle.putString("type", type);
            previewIntent.putExtras(bundle);
            startActivityForResult(previewIntent, ACT_PREVIEW_IMAGE);
            */
            String strKey = "1111";
            BitmapCache.getInstance().setBitmap(strKey, bitmap1);
            Intent previewIntent = new Intent(mContext, PreviewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("bmp_key", strKey);
            previewIntent.putExtras(bundle);
            startActivityForResult(previewIntent, ACT_PREVIEW_IMAGE);
            /*
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("path", file.getAbsolutePath());
            bundle.putString("type", type);
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            */

            //CameraActivity.this.finish();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * 初始化camera
         */

        cameraManager = new CameraManager();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();

        if (hasSurface) {
            // activity在paused时但不会stopped,因此surface仍旧存在；
            // surfaceCreated()不会调用，因此在这里初始化camera
            initCamera(surfaceHolder);
        } else {
            // 重置callback，等待surfaceCreated()来初始化camera
            surfaceHolder.addCallback(this);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    /**
     * 初始camera
     *
     * @param surfaceHolder SurfaceHolder
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            return;
        }
        try {
            // 打开Camera硬件设备
            cameraManager.openDriver(surfaceHolder, this);
            // 创建一个handler来打开预览，并抛出一个运行时异常
            //cameraManager.startPreview(this);
            cameraManager.startPreview(null);
        } catch (Exception ioe) {
            Log.d("zk", ioe.toString());
        }
    }

    @Override
    protected void onPause() {
        /**
         * 停止camera，是否资源操作
         */
        cameraManager.stopPreview();
        cameraManager.closeDriver();
        if (!hasSurface) {
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(utils.isClickMediaKey(keyCode))
            return  true;

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(utils.isClickMediaKey(keyCode)){
            onBtnClickTakePhoto();
            return  true;
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ACT_PREVIEW_IMAGE && resultCode == RESULT_OK){
            setResult(RESULT_OK, data);
            this.finish();
        }
    }
}
