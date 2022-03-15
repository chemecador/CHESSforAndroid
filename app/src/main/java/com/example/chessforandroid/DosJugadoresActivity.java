package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

public class DosJugadoresActivity extends AppCompatActivity implements View.OnClickListener{
    private ArrayList<ImageButton> casillas;
    private View a1, h8;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dos_jugadores);
        casillas = new ArrayList<>();
        a1 = findViewById(R.id.a1);
        a1.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            default:
                Toast.makeText(getApplicationContext(),
                        "Has pulsado " + view.getId(),
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }
}