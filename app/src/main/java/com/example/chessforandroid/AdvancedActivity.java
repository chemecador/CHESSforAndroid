package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chessforandroid.util.Constantes;

public class AdvancedActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText ip, puerto;
    private Button cancelar, confirmar;
    private CheckBox cbDeveloper;
    private TextView tvIp, tvPuerto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced);

        ip = findViewById(R.id.txtIp);
        puerto = findViewById(R.id.txtPuerto);
        cancelar = findViewById(R.id.bCancelar);
        confirmar = findViewById(R.id.bConfirmar);
        tvIp = findViewById(R.id.txtIPAdvanced);
        tvPuerto = findViewById(R.id.txtPuertoAdvanced);

        cbDeveloper = findViewById(R.id.cbDeveloper);


        ip.setOnClickListener(this);
        puerto.setOnClickListener(this);
        cancelar.setOnClickListener(this);
        confirmar.setOnClickListener(this);
        cbDeveloper.setOnClickListener(this);
        tvPuerto.setOnClickListener(this);
        tvIp.setOnClickListener(this);
        if (Constantes.debug) {
            cbDeveloper.setChecked(true);
        }

        ip.setText(Constantes.ip);
        puerto.setText(String.valueOf(Constantes.puerto));

        if (!cbDeveloper.isChecked()) {
            ocultar();
        }
    }

    private void ocultar() {
        ip.setVisibility(View.INVISIBLE);
        puerto.setVisibility(View.INVISIBLE);
        cancelar.setVisibility(View.INVISIBLE);
        confirmar.setVisibility(View.INVISIBLE);
        tvIp.setVisibility(View.INVISIBLE);
        tvPuerto.setVisibility(View.INVISIBLE);
    }

    private void mostrar() {
        ip.setVisibility(View.VISIBLE);
        puerto.setVisibility(View.VISIBLE);
        cancelar.setVisibility(View.VISIBLE);
        confirmar.setVisibility(View.VISIBLE);
        tvIp.setVisibility(View.VISIBLE);
        tvPuerto.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bCancelar) {
            finish();
        }
        if (view.getId() == R.id.bConfirmar) {
            Constantes.ip = ip.getText().toString();
            Constantes.puerto = Integer.parseInt(puerto.getText().toString());
            Constantes.debug = false;
            finish();
        }
        if (view.getId() == R.id.cbDeveloper) {
            if (cbDeveloper.isChecked()) {
                mostrar();
            } else {
                ocultar();
            }
        }
    }
}