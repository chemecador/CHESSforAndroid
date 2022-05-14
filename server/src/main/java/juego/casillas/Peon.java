package juego.casillas;


public class Peon extends Pieza {

    public boolean pasable;
    public Peon(boolean blancas) {
        this.blancas = blancas;
        this.tag = "PEON";
        this.pasable = false;
    }
}
