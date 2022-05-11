package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button login;
    private Button signUp;
    private Button invitado;
    private TextView tvChess;
    private EditText user;
    private EditText pass;
    private Cliente c;
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);


        user = (EditText) findViewById(R.id.loginUser);
        user.setOnClickListener(this);

        pass = (EditText) findViewById(R.id.loginPass);
        pass.setOnClickListener(this);

        login = findViewById(R.id.bLogin);
        login.setOnClickListener(this);

        signUp = findViewById(R.id.bSignup);
        signUp.setOnClickListener(this);

        invitado = findViewById(R.id.bInvitado);
        invitado.setOnClickListener(this);

        tvChess = findViewById(R.id.txtChessLogin);
        tvChess.setOnClickListener(this);

        i = 0;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bLogin:
                if (user.getText().length() < 1 || pass.getText().length() < 1) {
                    Toast.makeText(this, "Rellena los campos de usuario y contraseña", Toast.LENGTH_SHORT).show();
                    break;
                }

                c = new Cliente();
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