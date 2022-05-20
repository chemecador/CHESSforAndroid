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
    private final static String TAG = RankingActivity.class.getSimpleName();

    private RankingAdapter adapter;
    private ListView lvRanking;
    private ProgressBar pb;
    private TextView tvCargando, tvElo;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        lvRanking = findViewById(R.id.lvRanking);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);


        tvCargando = findViewById(R.id.txtLoadingData);
        tvElo = findViewById(R.id.txtColElo);
        pb = findViewById(R.id.progressBarRanking);

        if (prefs.getBoolean("preferencias_elo", true)) {
            tvElo.setText(R.string.elo);
            if (Cliente.isConectado()) {
                Cliente.getRanking(this, "elo");
            }
        } else {
            tvElo.setText(R.string.level);
            if (Cliente.isConectado()) {
                Cliente.getRanking(this, "nivel");
            }
        }

    }

    public void alRecibirDatos(ArrayList<RankingItem> datos) {

        tvCargando.setVisibility(View.INVISIBLE);
        pb.setVisibility(View.INVISIBLE);
        pb.setEnabled(false);
        adapter = new RankingAdapter(this, datos);
        lvRanking.setAdapter(adapter);
    }
}