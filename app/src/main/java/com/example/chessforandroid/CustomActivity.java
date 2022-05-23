package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.chessforandroid.util.Constantes;

/**
 * CustomActivity, que se encarga de personalizar las casillas si el usuario ha alcanzado el nivel 2.
 */
public class CustomActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = OnlineActivity.class.getSimpleName();

    private Spinner spinner1, spinner2;
    private Button bConfirmar, bCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);


        bConfirmar = findViewById(R.id.bConfirmarPersonalizar);
        bCancelar = findViewById(R.id.bCancelarPersonalizar);

        bConfirmar.setOnClickListener(this);
        bCancelar.setOnClickListener(this);

        // colores disponibles para seleccionar
        String[] colores = {"Por defecto", "Blanco", "Negro", "Amarillo",
                "Azul", "Rojo", "Verde", "Morado"};

        spinner1 = findViewById(R.id.colorSpinner1);
        spinner2 = findViewById(R.id.colorSpinner2);
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, colores);
        spinner1.setAdapter(adapterSpinner);
        spinner2.setAdapter(adapterSpinner);

        spinner1.setSelection(Constantes.COLOR_BLANCAS_POSICION);
        spinner2.setSelection(Constantes.COLOR_NEGRAS_POSICION);
    }

    public void recogerColorBlanco() {
        String color1 = spinner1.getSelectedItem().toString();
        switch (color1) {
            case "Blanco":
                Constantes.COLOR_BLANCAS = Color.parseColor("#FFFFFF");
                break;
            case "Negro":
                Constantes.COLOR_BLANCAS = Color.parseColor("#4F4F4F");
                break;
            case "Amarillo":
                Constantes.COLOR_BLANCAS = Color.parseColor("#FDFF5F");
                break;
            case "Azul":
                Constantes.COLOR_BLANCAS = Color.parseColor("#0080FF");
                break;
            case "Rojo":
                Constantes.COLOR_BLANCAS = Color.parseColor("#FF0000");
                break;
            case "Verde":
                Constantes.COLOR_BLANCAS = Color.parseColor("#00DB00");
                break;
            case "Morado":
                Constantes.COLOR_BLANCAS = Color.parseColor("#B355E8");
                break;
            default:
                Constantes.COLOR_BLANCAS = Constantes.BLANCAS_POR_DEFECTO;
        }
        Constantes.COLOR_BLANCAS_POSICION = spinner1.getSelectedItemPosition();
    }

    public void recogerColorNegro() {
        String color1 = spinner2.getSelectedItem().toString();
        switch (color1) {
            case "Blanco":
                Constantes.COLOR_NEGRAS = Color.parseColor("#FFFFFF");
                break;
            case "Negro":
                Constantes.COLOR_NEGRAS = Color.parseColor("#4F4F4F");
                break;
            case "Amarillo":
                Constantes.COLOR_NEGRAS = Color.parseColor("#FDFF5F");
                break;
            case "Azul":
                Constantes.COLOR_NEGRAS = Color.parseColor("#0080FF");
                break;
            case "Rojo":
                Constantes.COLOR_NEGRAS = Color.parseColor("#FF0000");
                break;
            case "Verde":
                Constantes.COLOR_NEGRAS = Color.parseColor("#00DB00");
                break;
            case "Morado":
                Constantes.COLOR_NEGRAS = Color.parseColor("#B355E8");
                break;
            default:
                Constantes.COLOR_NEGRAS = Constantes.NEGRAS_POR_DEFECTO;
        }
        Constantes.COLOR_NEGRAS_POSICION = spinner2.getSelectedItemPosition();
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.bCancelarPersonalizar) {
            finish();
        }
        if (view.getId() == R.id.bConfirmarPersonalizar) {
            recogerColorBlanco();
            recogerColorNegro();
            finish();
        }
    }
}