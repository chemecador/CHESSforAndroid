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

/**
 * RankingActivity. Activity que se encarga de solicitar al servidor y mostrar el ranking
 */
public class RankingActivity extends AppCompatActivity {

    private RankingAdapter adapter;
    private ListView lvRanking;
    private ProgressBar pb;
    private TextView tvCargando, tvElo;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        Cliente cliente = new Cliente();
        lvRanking = findViewById(R.id.lvRanking);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);


        tvCargando = findViewById(R.id.txtLoadingData);
        tvElo = findViewById(R.id.txtColElo);
        pb = findViewById(R.id.progressBarRanking);

        if (prefs.getBoolean("preferencias_elo", true)) {
            tvElo.setText(R.string.elo);
            if (cliente.isConectado()) {
                cliente.getRanking(this, "elo");
            }
        } else {
            tvElo.setText(R.string.level);
            if (cliente.isConectado()) {
                cliente.getRanking(this, "nivel");
            }
        }

    }

    /**
     * Metodo que se invoca cuando se ha terminado de recibir la clasificacion
     * @param datos ArrayList con los datos de los usuarios
     */
    public void alRecibirDatos(ArrayList<RankingItem> datos) {

        tvCargando.setVisibility(View.INVISIBLE);
        pb.setVisibility(View.INVISIBLE);
        pb.setEnabled(false);
        adapter = new RankingAdapter(this, datos);
        lvRanking.setAdapter(adapter);
    }
}