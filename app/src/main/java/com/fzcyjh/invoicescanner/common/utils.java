package com.fzcyjh.invoicescanner.common;

import android.view.KeyEvent;

/**
 * Created by zhangyb on 2018/6/7.
 */

public class utils {
    //
    static public void threadSleep(int nMillis){
        try {
            Thread.currentThread().sleep(nMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static public  boolean isClickMediaKey(int nKeyCode){
        if(nKeyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                || nKeyCode == KeyEvent.KEYCODE_VOLUME_UP
                || (nKeyCode >= KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE && nKeyCode <= KeyEvent.KEYCODE_MEDIA_FAST_FORWARD))
            return  true;

        return  false;
    }

    static public class codeUsedTime{
        private long mTimeStart = 0;
        private long mTimeLast = 0;

        public codeUsedTime() {
            start();
        }

        public void start(){
            mTimeStart = System.currentTimeMillis();
            mTimeLast = mTimeStart;
        }

        public long tickCurrent(){
            long lNow = System.currentTimeMillis();
            long lRet = lNow - mTimeLast;
            mTimeLast = lNow;
            return lRet;
        }
    };
}
