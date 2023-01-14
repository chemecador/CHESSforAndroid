package com.example.chessforandroid.piezas;

import com.example.chessforandroid.R;

/**
 * Clase Rey.
 */
public class Rey extends Pieza {

    /**
     * Si se ha movido el rey, ya no puede enrocar.
     */
    public boolean haMovido;

    public Rey(boolean blancas, boolean invertida) {

        this.blancas = blancas;
        this.tag = "REY";
        this.haMovido = false;
        this.invertida = invertida;

        if (blancas) {
            this.drawable = R.drawable.brey;
        } else {
            if (invertida) {
                this.drawable = R.drawable.lnrey;
            } else {
                this.drawable = R.drawable.nrey;
            }
        }
    }

    @Override
    public void setNewDrawable() {
        if (this.isBlancas()){
            this.setDrawable(R.drawable.brey);
        }
        else if (this.invertida){
            this.setDrawable(R.drawable.lnrey);
        } else {
            this.setDrawable(R.drawable.nrey);
        }
    }
}