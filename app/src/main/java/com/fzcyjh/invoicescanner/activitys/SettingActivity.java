package com.fzcyjh.invoicescanner.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.fzcyjh.invoicescanner.R;
import com.fzcyjh.invoicescanner.manager.ConfigManager;

public class SettingActivity extends AppCompatActivity{
    private static  final String TAG = "SettingActivity";

    private EditText mEDTUrlUpload;
    private CheckBox mCHKQuickUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initContrls();
    }

    private void initContrls(){
        mEDTUrlUpload = (EditText)findViewById(R.id.edt_setting_urlupload);
        mEDTUrlUpload.setText(ConfigManager.getString(ConfigManager.CONF_URL_UPLOAD));

        mCHKQuickUpload = (CheckBox)findViewById(R.id.chk_setting_quickupload);
        boolean bEnableQuickUpload = ConfigManager.getBoolean(ConfigManager.CONF_SETTING_QUICKUPLOAD, false);
        mCHKQuickUpload.setChecked(bEnableQuickUpload);

        ((Button)findViewById(R.id.btn_setting_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkVaildUISetting())
                    return;

                ConfigManager.setString(ConfigManager.CONF_URL_UPLOAD, mEDTUrlUpload.getText().toString());
                Toast.makeText(SettingActivity.this, "配置已保存!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                SettingActivity.this.finish();
            }
        });

        mCHKQuickUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigManager.setBoolean(ConfigManager.CONF_SETTING_QUICKUPLOAD, mCHKQuickUpload.isChecked());
            }
        });
    }

    private boolean checkVaildUISetting(){
        if(!mEDTUrlUpload.getText().toString().startsWith("http")) {
            Toast.makeText(SettingActivity.this, "地址不能为空!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return  true;
    }
}
