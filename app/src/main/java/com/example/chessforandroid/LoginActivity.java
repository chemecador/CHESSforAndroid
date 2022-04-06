package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import com.example.chessforandroid.cliente.Cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

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
                Cliente c = new Cliente();
                c.hablar();
                if (user.getText().length() < 1 || pass.getText().length() < 1) {
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


    public class Cliente {
        private Socket conn; // socket con la conexión
        private DataInputStream in; // flujo de entrada
        private DataOutputStream out; // flujo de salida
        private final int PUERTO = 5555; // puerto que se utilizará
        private final String HOST = "localhost"; // dirección IP (local)

        // constructor
        public Cliente() {
             StrictMode.ThreadPolicy policy = new
             StrictMode.ThreadPolicy.Builder().permitAll().build();
             StrictMode.setThreadPolicy(policy);
            try {
                // inicializamos el socket, dis y dos
                conn = new Socket("192.168.1.144", PUERTO);
                in = new DataInputStream(conn.getInputStream());
                out = new DataOutputStream(conn.getOutputStream());

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void hablar() {

            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        try {
                            Log.i("****", "voy a enviar ping");
                            out.writeUTF("android envia ping");
                            Log.i("****", "android recibe " + in.readUTF());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }

}