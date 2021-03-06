package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.chessforandroid.util.Cliente;

/**
 * OnlineWaitingActivity, envia al servidor la peticion de buscar partida online y espera al rival.
 */
public class OnlineWaitingActivity extends AppCompatActivity {
    private final static String TAG = Cliente.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_waiting);
        Intent intent = getIntent();
        String token = intent.getStringExtra("token");

        Cliente c = new Cliente();
        if (c.isConectado()) {
            c.online(this, this, token);
        } else {
            Log.i(TAG, "Error al buscar partida online");
        }
    }
}