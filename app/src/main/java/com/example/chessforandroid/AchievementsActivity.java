package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chessforandroid.util.Cliente;

public class AchievementsActivity extends AppCompatActivity {
    private final static String TAG = AchievementsActivity.class.getSimpleName();
    private String token;
    private TextView cantidadLogros, win10, win20, win30,
            movs10, movs40, level5, level10, gameOver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        Intent i = getIntent();
        token = i.getStringExtra("token");

        cantidadLogros = findViewById(R.id.txtCantidadLogros);
        win10 = findViewById(R.id.txtWin10);
        win20 = findViewById(R.id.txtWin20);
        win30 = findViewById(R.id.txtWin30);
        movs10 = findViewById(R.id.txt10Movs);
        movs40 = findViewById(R.id.txt40Movs);
        level5 = findViewById(R.id.txtLevel5);
        level10 = findViewById(R.id.txtLevel10);
        gameOver = findViewById(R.id.txtGameOver);
        gameOver.setVisibility(View.INVISIBLE);

        if (Cliente.isConectado()) {
            Cliente.consultarLogros(this, this, token);
        } else {
            Toast.makeText(this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
        }


    }
    public void alTerminarConsulta(Boolean[] logros){
        int nLogros = 0;

        if (logros[0]){
            win10.setText(R.string.DONE);
            nLogros++;
        } else {
            win10.setText(R.string.NOT_DONE);
        }
        if (logros[1]){
            win20.setText(R.string.DONE);
            nLogros++;
        } else {
            win20.setText(R.string.NOT_DONE);
        }
        if (logros[2]){
            win30.setText(R.string.DONE);
            nLogros++;
        } else {
            win30.setText(R.string.NOT_DONE);
        }
        if (logros[3]){
            movs10.setText(R.string.DONE);
            nLogros++;
        } else {
            movs10.setText(R.string.NOT_DONE);
        }
        if (logros[4]){
            movs40.setText(R.string.DONE);
            nLogros++;
        } else {
            movs40.setText(R.string.NOT_DONE);
        }
        if (logros[5]){
            level5.setText(R.string.DONE);
            nLogros++;
        } else {
            level5.setText(R.string.NOT_DONE);
        }
        if (logros[6]){
            level10.setText(R.string.DONE);
            nLogros++;
        } else {
            level10.setText(R.string.NOT_DONE);
        }
        cantidadLogros.setText(nLogros+"/7");
        for (Boolean b : logros){
            if (!b)
                return;
        }
        gameOver.setVisibility(View.VISIBLE);
    }
}