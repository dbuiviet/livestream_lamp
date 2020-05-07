package com.xcomp.livestreamlamp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.xcomp.livestreamlamp.R;

public class BaseActivity extends AppCompatActivity {

    //this view must be initialize in child activity before used.
    public ViewGroup rootViewToShowLoadingIndicator;

    private View loadingIndicator;
    private TextView currentLoadingTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.loadingIndicator = LayoutInflater.from(this).inflate(R.layout._loading_indicator_view, null);
        this.currentLoadingTask = this.loadingIndicator.findViewById(R.id.tv_current_task);

        Fresco.initialize(this);

    }

    public void onBackButtonClicked() {
        this.finish();
    }

    public void showLoadingIndicator(String loadingTask) {
        this.currentLoadingTask.setText(loadingTask);
        this.rootViewToShowLoadingIndicator.removeView(this.loadingIndicator);
        this.rootViewToShowLoadingIndicator.addView(this.loadingIndicator);
    }

    public void hideLoadingIndicator() {
        this.rootViewToShowLoadingIndicator.removeView(this.loadingIndicator);
    }
}
