package juego.casillas;

/**
 * Clase Peon.
 */
public class Peon extends Pieza {

    /**
     * Booleano que almacena si el peon puede recibir la captura al paso
     */
    public boolean pasable;

    public Peon(boolean blancas) {
        this.blancas = blancas;
        this.tag = "PEON";
        this.pasable = false;
    }
}
