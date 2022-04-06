package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button login;
    private Button signUp;
    private Button loginOffline;
    private EditText user;
    private EditText pass;
    private boolean isLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user = (EditText) findViewById(R.id.loginUser);
        user.setOnClickListener(this);

        pass = (EditText) findViewById(R.id.loginPass);
        pass.setOnClickListener(this);

        login = findViewById(R.id.bLogin);
        login.setOnClickListener(this);

        signUp = findViewById(R.id.bSignup);
        signUp.setOnClickListener(this);

        loginOffline = findViewById(R.id.bLoginOffline);
        loginOffline.setOnClickListener(this);
        isLogged = false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bLogin:
                Intent mainIntentLogin = new Intent(this, MainActivity.class);
                isLogged = true;
                mainIntentLogin.putExtra("isLogged", isLogged);
                startActivity(mainIntentLogin);
                finish();
                break;
            case R.id.bSignup:
                if(user.getText().length() < 1 || pass.getText().length() < 1 ){
                    Toast.makeText(this, "Rellena los campos de usuario y contraseña", Toast.LENGTH_SHORT).show();
                    break;
                }
                Intent mainIntentSignup = new Intent(this, MainActivity.class);
                isLogged = true;
                mainIntentSignup.putExtra("isLogged", isLogged);
                startActivity(mainIntentSignup);
                finish();
                break;
            case R.id.bLoginOffline:
                Intent mainIntentOffline = new Intent(this, MainActivity.class);
                mainIntentOffline.putExtra("isLogged", isLogged);
                startActivity(mainIntentOffline);
                finish();
                break;
        }
    }
}