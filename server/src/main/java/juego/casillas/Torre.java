package juego.casillas;

public class Torre extends Pieza {

    /**
     * Si se ha movido esa torre, ya no se puede enrocar hacia ese lado.
     */
    public boolean haMovido;

    public Torre(boolean blancas) {
        this.blancas = blancas;
        this.tag = "TORRE";
        this.haMovido = false;
    }
}