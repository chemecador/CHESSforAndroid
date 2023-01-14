package com.example.chessforandroid.piezas;

import com.example.chessforandroid.R;

/**
 * Clase Peon.
 */
public class Peon extends Pieza {

    /**
     * Booleano que almacena si el peon puede recibir la captura al paso
     */
    public boolean pasable;

    public Peon(boolean blancas, boolean invertida) {

        this.blancas = blancas;
        this.tag = "PEON";
        this.pasable = false;
        this.invertida = invertida;
        if (blancas) {
            this.drawable = R.drawable.bpeon;
        } else {
            if (invertida) {
                this.drawable = R.drawable.lnpeon;
            } else {
                this.drawable = R.drawable.npeon;
            }
        }
    }

    @Override
    public void setNewDrawable() {
        if (this.isBlancas()){
            this.setDrawable(R.drawable.bpeon);
        }
        else if (this.invertida){
            this.setDrawable(R.drawable.lnpeon);
        } else {
            this.setDrawable(R.drawable.npeon);
        }
    }
}
