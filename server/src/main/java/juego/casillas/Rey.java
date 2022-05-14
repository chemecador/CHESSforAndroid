package juego.casillas;


public class Rey extends Pieza {

    public boolean haMovido;
    public boolean enJaque;

    public Rey(boolean blancas) {
        this.blancas = blancas;
        this.tag = "REY";
        this.haMovido = false;
        this.enJaque = false;
    }
}