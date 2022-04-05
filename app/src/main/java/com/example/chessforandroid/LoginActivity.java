package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button login;
    private Button loginOffline;
    private boolean isLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = findViewById(R.id.bLogin);
        login.setOnClickListener(this);

        loginOffline = findViewById(R.id.bLoginOffline);
        loginOffline.setOnClickListener(this);
        isLogged = false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bLogin:
                Intent mainIntent = new Intent(this, MainActivity.class);
                isLogged = true;
                mainIntent.putExtra("isLogged", isLogged);
                startActivity(mainIntent);
                break;
            case R.id.bLoginOffline:
                Intent mainIntentOffline = new Intent(this, MainActivity.class);
                mainIntentOffline.putExtra("isLogged", isLogged);
                startActivity(mainIntentOffline);
                break;
        }
    }
}