package com.example.chessforandroid.piezas;

import com.example.chessforandroid.R;

/**
 * Clase Alfil.
 */
public class Alfil extends Pieza {

    public Alfil(boolean blancas, boolean invertida) {
        this.blancas = blancas;
        this.tag = "ALFIL";
        this.invertida = invertida;

        if (blancas) {
            this.drawable = R.drawable.balfil;
        } else {
            if (this.invertida) {
                this.drawable = R.drawable.lnalfil;
            } else {
                this.drawable = R.drawable.nalfil;
            }
        }
    }

    @Override
    public void setNewDrawable() {
        if (this.isBlancas()){
            this.setDrawable(R.drawable.balfil);
        }
        else if (this.invertida){
            this.setDrawable(R.drawable.lnalfil);
        } else {
            this.setDrawable(R.drawable.nalfil);
        }
    }
}