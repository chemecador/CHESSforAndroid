package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    }

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
                    if (c.iniciarSesion(user.getText().toString(), pass.getText().toString())) {
                        Intent mainIntentLogin = new Intent(this, MainActivity.class);
                        mainIntentLogin.putExtra("user", user.getText().toString());
                        c.cerrarConexion();
                        startActivity(mainIntentLogin);
                        finish();
                    } else {
                        Toast.makeText(this, "Nombre de usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    }
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
                    int res = c.registrarse(user.getText().toString(), pass.getText().toString());
                    Log.i("**", "res vale: " + res);
                    switch (res) {
                        case -2:
                        case -1:
                            Log.i("**", "Error de conexión con la base de datos");
                            break;
                        case 0:
                            Toast.makeText(this, "El nombre de usuario ya existe.", Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            Intent mainIntentSignup = new Intent(this, MainActivity.class);
                            mainIntentSignup.putExtra("user", user.getText().toString());
                            c.cerrarConexion();
                            startActivity(mainIntentSignup);
                            finish();
                            break;
                    }
                } else {
                    Toast.makeText(this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bLoginOffline:
                Intent mainIntentOffline = new Intent(this, MainActivity.class);
                mainIntentOffline.putExtra("user", "");
                startActivity(mainIntentOffline);
                finish();
                break;
        }
    }

}