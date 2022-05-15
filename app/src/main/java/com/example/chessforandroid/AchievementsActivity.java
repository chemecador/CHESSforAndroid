package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class AchievementsActivity extends AppCompatActivity {
    private int victorias;
    private TextView win10, win20, win30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        Intent i = getIntent();
        victorias = Integer.parseInt(i.getStringExtra("victorias"));

        win10 = findViewById(R.id.txtWin10);
        win20 = findViewById(R.id.txtWin20);
        win30 = findViewById(R.id.txtWin30);

        String s = "";

        if (victorias >= 10){
            win10.setText(R.string.DONE);
        } else {
            s = victorias+"/10";
            win10.setText(s);
        }

        if (victorias >= 20){
            win20.setText(R.string.DONE);
        } else {
            s = victorias+"/20";
            win20.setText(s);
        }
        if (victorias >= 30){
            win30.setText(R.string.DONE);
        } else {
            s = victorias+"/30";
            win30.setText(s);
        }


    }
}