package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Activity AboutUsActivity.
 * Muestra la informacion acerca del desarrollador y cuenta el numero
 * de toques que se ha realizado sobre el texto de la version.
 * Si llega a 7, lanza el AdvancedActivity.
 */
public class AboutUsActivity extends AppCompatActivity implements View.OnClickListener {

    private int contador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        contador = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        TextView tv = findViewById(R.id.txtVersion);
        tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.txtVersion) {
            contador++;
            if (contador > 7) {
                startActivity(new Intent(this, AdvancedActivity.class));
            }
        }
    }
}