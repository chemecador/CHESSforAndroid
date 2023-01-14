package com.example.chessforandroid.piezas;

import com.example.chessforandroid.R;

public class Torre extends Pieza {

    /**
     * Si se ha movido esa torre, ya no se puede enrocar hacia ese lado.
     */
    public boolean haMovido;

    public Torre(boolean blancas, boolean invertida) {

        this.blancas = blancas;
        this.tag = "TORRE";
        this.haMovido = false;
        this.invertida = invertida;

        if (blancas) {
            this.drawable = R.drawable.btorre;
        } else {
            if (invertida) {
                this.drawable = R.drawable.lntorre;
            } else {
                this.drawable = R.drawable.ntorre;
            }
        }
    }

    @Override
    public void setNewDrawable() {
        if (this.isBlancas()){
            this.setDrawable(R.drawable.btorre);
        }
        else if (this.invertida){
            this.setDrawable(R.drawable.lntorre);
        } else {
            this.setDrawable(R.drawable.ntorre);
        }
    }
}