package com.example.chessforandroid.piezas;

import com.example.chessforandroid.R;

public class Peon extends Pieza {

    public Peon(int x, int y, boolean blancas) {
        this.blancas = blancas;
        this.fila = x;
        this.columna = y;
        this.tag = "PEON";
        if (blancas) {
            this.drawable = R.drawable.bpeon;
        } else {
            this.drawable = R.drawable.npeon;
        }
    }
}
