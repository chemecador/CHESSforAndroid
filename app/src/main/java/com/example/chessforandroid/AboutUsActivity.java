package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class AboutUsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv;
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        i = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        tv = findViewById(R.id.txtVersion);
        tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.txtVersion) {
            i++;
            if (i > 7) {
                startActivity(new Intent(this, AdvancedActivity.class));
            }
        }
    }
}