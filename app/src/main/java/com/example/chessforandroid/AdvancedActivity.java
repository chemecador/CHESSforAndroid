package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.chessforandroid.util.Constantes;

public class AdvancedActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText ip;
    private EditText puerto;
    private Button cancelar;
    private Button confirmar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced);

        ip = findViewById(R.id.txtIp);
        puerto = findViewById(R.id.txtPuerto);
        cancelar = findViewById(R.id.bCancelar);
        confirmar = findViewById(R.id.bConfirmar);


        ip.setOnClickListener(this);
        puerto.setOnClickListener(this);
        cancelar.setOnClickListener(this);
        confirmar.setOnClickListener(this);

        ip.setText(Constantes.ip);
        puerto.setText(String.valueOf(Constantes.puerto));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bCancelar){
            finish();
        }
        if (view.getId() == R.id.bConfirmar){
            Constantes.ip = ip.getText().toString();
            Constantes.puerto = Integer.valueOf(puerto.getText().toString());
            finish();
        }
    }
}