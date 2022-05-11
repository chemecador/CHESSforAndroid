package casillas;

public class Torre extends Pieza {

    public boolean haMovido;

    public Torre(boolean blancas) {
        this.blancas = blancas;
        this.tag = "TORRE";
        this.haMovido = false;
    }
}