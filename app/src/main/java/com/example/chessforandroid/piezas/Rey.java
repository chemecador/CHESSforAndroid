package com.example.chessforandroid.piezas;

import com.example.chessforandroid.R;

public class Rey extends Pieza {

    public boolean posInicial;
    public boolean enJaque;

    public Rey(int x, int y, boolean blancas) {
        this.blancas = blancas;
        this.fila = x;
        this.columna = y;
        this.tag = "REY";
        this.posInicial = true;
        this.enJaque = false;
        if (blancas) {
            this.drawable = R.drawable.brey;
        } else {
            this.drawable = R.drawable.nrey;
        }
    }
}