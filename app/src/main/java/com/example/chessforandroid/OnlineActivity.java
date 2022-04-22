package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class OnlineActivity extends AppCompatActivity implements View.OnClickListener {

    private Button crear;
    private Button unirse;
    private EditText codigo;
    private Cliente c;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);

        crear = findViewById(R.id.bCrearPartida);
        unirse = findViewById(R.id.bUnirse);
        codigo = findViewById(R.id.txtCodigo);
        Intent intent = getIntent();
        token = intent.getStringExtra("token");

        crear.setOnClickListener(this);
        unirse.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bCrearPartida:
                c = new Cliente();
                if (c.isConectado()) {
                } else {
                    Toast.makeText(this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.bUnirse:
                break;
        }
    }
}