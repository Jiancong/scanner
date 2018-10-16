package com.fzcyjh.invoicescanner.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fzcyjh.invoicescanner.R;

public class UploadListActivity extends AppCompatActivity {
    private  ListView    mLstUploader;
    private  String strDatas[] = new String[100];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadlist);
        initContrls();
    }

    private void initContrls(){
        mLstUploader = (ListView)findViewById(R.id.lst_uploadlist);


        for (int i = 0; i < 100; i ++)
            strDatas[i] = Integer.toString(i);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strDatas);
        mLstUploader.setAdapter(adapter);
    }
}
