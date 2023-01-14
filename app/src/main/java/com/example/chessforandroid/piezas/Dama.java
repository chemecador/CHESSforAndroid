package com.example.chessforandroid.piezas;

import com.example.chessforandroid.R;

/**
 * Clase Dama.
 */
public class Dama extends Pieza {

    public Dama(boolean blancas, boolean invertida) {

        this.blancas = blancas;
        this.tag = "DAMA";
        this.invertida = invertida;
        if (blancas) {
            this.drawable = R.drawable.bdama;
        } else {
            if (invertida) {
                this.drawable = R.drawable.lndama;
            } else {
                this.drawable = R.drawable.ndama;
            }
        }
    }

    @Override
    public void setNewDrawable() {
        if (this.isBlancas()){
            this.setDrawable(R.drawable.bdama);
        }
        else if (this.invertida){
            this.setDrawable(R.drawable.lndama);
        } else {
            this.setDrawable(R.drawable.ndama);
        }
    }
}
