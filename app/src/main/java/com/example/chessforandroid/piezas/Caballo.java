package com.example.chessforandroid.piezas;

import com.example.chessforandroid.R;

public class Caballo extends Pieza {
    public Caballo(int x, int y, boolean blancas) {
        this.blancas = blancas;
        this.fila = x;
        this.columna = y;
        this.tag = "CABALLO";
        if (blancas) {
            this.drawable = R.drawable.bcaballo;
        } else {
            this.drawable = R.drawable.ncaballo;
        }
    }
}