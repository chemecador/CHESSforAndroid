package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chessforandroid.util.Cliente;

public class FriendActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText codigo;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);

        Button crear = findViewById(R.id.bCrearPartida);
        Button unirse = findViewById(R.id.bUnirse);
        codigo = findViewById(R.id.txtCodigo);
        Intent intent = getIntent();
        token = intent.getStringExtra("token");

        crear.setOnClickListener(this);
        unirse.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bCrearPartida:
                Cliente c = new Cliente();
                if (c.isConectado()) {
                    c.crearSala(this, token);
                } else {
                    Toast.makeText(this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.bUnirse:
                c = new Cliente();
                if (c.isConectado()) {
                    c.unirse(this, token, codigo.getText().toString());
                } else {
                    Toast.makeText(this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}