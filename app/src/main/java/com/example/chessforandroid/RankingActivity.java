package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chessforandroid.util.Cliente;
import com.example.chessforandroid.util.RankingItem;

import java.util.ArrayList;

public class RankingActivity extends AppCompatActivity {

    private ArrayList<RankingItem> lista;
    private RankingAdapter adapter;
    private ListView lvRanking;
    private ProgressBar pb;
    private TextView tvCargando;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        Cliente cliente = new Cliente();
        lista = new ArrayList<>();
        lvRanking = findViewById(R.id.lvRanking);
        prefs = PreferenceManager.getDefaultSharedPreferences(this); //Consigo la referencia a las preferencias para poder leerlas y escribirlas


        tvCargando = findViewById(R.id.txtLoadingData);
        pb = findViewById(R.id.progressBarRanking);

       // if (prefs.getBoolean("preferencias_elo", true)) {
            if (cliente.isConectado()) {
                cliente.getRanking(this, "elo");
            }
        //}

    }

    public void alRecibirDatos(ArrayList<RankingItem> datos) {

        tvCargando.setVisibility(View.INVISIBLE);
        pb.setVisibility(View.INVISIBLE);
        pb.setEnabled(false);
        adapter = new RankingAdapter(this, datos);
        lvRanking.setAdapter(adapter);
    }
}