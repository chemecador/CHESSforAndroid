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

import com.example.chessforandroid.util.Cliente;

import java.util.Objects;

/**
 * LoginActivity. Activity que se encarga de gestionar el inicio de sesion de un usuario.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = LoginActivity.class.getSimpleName();

    /**
     * Nombre de usuario escrito por el jugador.
     */
    private EditText user;

    /**
     * Clave escrita por el jugador.
     */
    private EditText pass;

    /**
     * Contador de veces que se ha pulsado sobre el TextView con el nombre de la app.
     */
    private int contador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_login);


        user = findViewById(R.id.txtLoginUser);
        user.setOnClickListener(this);

        pass = findViewById(R.id.txtLoginPass);
        pass.setOnClickListener(this);

        Button login = findViewById(R.id.bLogin);
        login.setOnClickListener(this);

        Button signUp = findViewById(R.id.bSignup);
        signUp.setOnClickListener(this);

        Button invitado = findViewById(R.id.bInvitado);
        invitado.setOnClickListener(this);

        TextView tvChess = findViewById(R.id.txtChessLogin);
        tvChess.setOnClickListener(this);

        contador = 0;
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
                    Log.e(TAG, "Error al iniciar sesión");
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
                contador++;
                if (contador > 7) {
                    startActivity(new Intent(this, AdvancedActivity.class));
                }
                break;
        }
    }

}