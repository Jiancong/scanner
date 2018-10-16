package com.fzcyjh.invoicescanner.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.fzcyjh.invoicescanner.MainActivity;
import com.fzcyjh.invoicescanner.R;
import com.fzcyjh.invoicescanner.manager.ConfigManager;

public class SplashActivity extends Activity {
    private final int SPLASH_DISPLAY_LENGHT = 2000; // 两秒后进入系统

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ConfigManager.getInstance().init(this);

        new android.os.Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent;
                if(ConfigManager.getBoolean(ConfigManager.CONF_GUIDE_NEVER_TIP, false))
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                else
                    intent = new Intent(SplashActivity.this, GuideActivity.class);
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }

        }, SPLASH_DISPLAY_LENGHT);
    }
}
