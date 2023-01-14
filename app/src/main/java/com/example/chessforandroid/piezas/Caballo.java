package com.example.chessforandroid.piezas;

import com.example.chessforandroid.R;

/**
 * Clase Caballo.
 */
public class Caballo extends Pieza {

    public Caballo(boolean blancas, boolean invertida) {

        this.blancas = blancas;
        this.tag = "CABALLO";
        this.invertida = invertida;

        if (blancas) {
            this.drawable = R.drawable.bcaballo;
        } else {
            if (invertida) {
                this.drawable = R.drawable.lncaballo;
            } else {
                this.drawable = R.drawable.ncaballo;
            }
        }
    }

    @Override
    public void setNewDrawable() {
        if (this.isBlancas()){
            this.setDrawable(R.drawable.bcaballo);
        }
        else if (this.invertida){
            this.setDrawable(R.drawable.lncaballo);
        } else {
            this.setDrawable(R.drawable.ncaballo);
        }
    }
}