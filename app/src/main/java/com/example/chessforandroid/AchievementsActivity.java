package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class AchievementsActivity extends AppCompatActivity {
    private String victorias;
    private TextView win10, win20, win30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        Intent i = getIntent();
        victorias = i.getStringExtra("victorias");

        win10 = findViewById(R.id.txtWin10);
        win20 = findViewById(R.id.txtWin20);
        win30 = findViewById(R.id.txtWin30);

        String s = "";

        s = victorias+"/10";
        win10.setText(s);

        s = victorias+"/20";
        win20.setText(s);

        s = victorias+"/30";
        win30.setText(s);


    }
}