package juego.casillas;

/**
 * Clase Rey.
 */
public class Rey extends Pieza {

    /**
     * Si se ha movido el rey, ya no puede enrocar.
     */
    public boolean haMovido;

    public Rey(boolean blancas) {
        this.blancas = blancas;
        this.tag = "REY";
        this.haMovido = false;
    }
}