package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        Cliente cliente = new Cliente();
        lista = new ArrayList<>();
        lvRanking = findViewById(R.id.lvRanking);

        tvCargando = findViewById(R.id.txtLoadingData);
        pb = findViewById(R.id.progressBarRanking);

        if (cliente.isConectado()){
            cliente.getRanking(this);
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