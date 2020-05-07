package com.xcomp.livestreamlamp.activity;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;


import com.xcomp.livestreamlamp.R;

import helpers.MqttHelper;

public class SettingActivity extends BaseActivity {

    MqttHelper mqttHelper;
    WebView webview;
    EditText tfSsid;
    EditText tfPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle("Cài đặt thiết bị");

        tfSsid = (EditText) findViewById(R.id.tfWifiName);
        tfPassword = (EditText) findViewById(R.id.tfPassword);

        webview = (WebView) findViewById(R.id.webview);
        ImageView imgBackground = (ImageView) findViewById(R.id.img_background);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        FrameLayout.LayoutParams layoutparams = (FrameLayout.LayoutParams) imgBackground.getLayoutParams();
        layoutparams.height = height;
        imgBackground.setLayoutParams(layoutparams);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        mqttHelper = new MqttHelper(getApplicationContext(), "", null);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackButtonClicked();
                break;
        }
        return true;
    }

    public void onSaveButtonClicked(View v){

        String ssid = tfSsid.getText().toString().trim();
        String password = tfPassword.getText().toString().trim();
        webview.loadUrl("http://192.168.1.100/?ssid=" + ssid + "&pass=" + password);

//        String msg = "a test message";
//        byte[] encodedPayload = new byte[0];
//        try {
//            encodedPayload = msg.getBytes("UTF-8");
//            MqttMessage message = new MqttMessage(encodedPayload);
//            message.setId(5866);
//            message.setRetained(true);
//            message.setQos(0);
//            mqttHelper.mqttAndroidClient.publish("hientest",message);
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }

    }

}
