package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.chessforandroid.util.Cliente;

public class OnlineWaitingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_waiting);
        Intent intent = getIntent();
        String token = intent.getStringExtra("token");

        Cliente c = new Cliente();
        if (c.isConectado()){
            c.local(this, this, token);
        } else {
            Log.i ("************************", "error");
        }
    }
}