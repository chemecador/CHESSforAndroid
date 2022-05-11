package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chessforandroid.util.Constantes;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText user;
    private EditText pass;
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_login);


        user = findViewById(R.id.loginUser);
        user.setOnClickListener(this);

        pass = findViewById(R.id.loginPass);
        pass.setOnClickListener(this);

        Button login = findViewById(R.id.bLogin);
        login.setOnClickListener(this);

        Button signUp = findViewById(R.id.bSignup);
        signUp.setOnClickListener(this);

        Button invitado = findViewById(R.id.bInvitado);
        invitado.setOnClickListener(this);

        TextView tvChess = findViewById(R.id.txtChessLogin);
        tvChess.setOnClickListener(this);

        i = 0;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bLogin:
                if (user.getText().length() < 1 || pass.getText().length() < 1) {
                    Toast.makeText(this, "Rellena los campos de usuario y contraseña", Toast.LENGTH_SHORT).show();
                    break;
                }

                Cliente c = new Cliente();
                if (c.isConectado()) {
                    Log.i("**", "user: " + user.getText().toString());
                    c.iniciarSesion(this, user.getText().toString(), pass.getText().toString());
                } else {
                    Toast.makeText(this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bSignup:
                if (user.getText().length() < 1 || pass.getText().length() < 1) {
                    Toast.makeText(this, "Rellena los campos de usuario y contraseña", Toast.LENGTH_SHORT).show();
                    break;
                }
                c = new Cliente();
                if (c.isConectado()) {
                    c.registrarse(this, user.getText().toString(), pass.getText().toString());
                } else {
                    Toast.makeText(this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bInvitado:
                Intent mainIntentOffline = new Intent(this, MainActivity.class);
                mainIntentOffline.putExtra("user", "");
                startActivity(mainIntentOffline);
                finish();
                break;
            case R.id.txtChessLogin:
                i++;
                if (i > 7) {
                    startActivity(new Intent(this, AdvancedActivity.class));
                }
                break;
        }
    }

}