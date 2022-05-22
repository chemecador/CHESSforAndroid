package juego.casillas;

/**
 * Clase abstracta Pieza. Contiene los elementos propios de una pieza,
 * que seran heredados por cada pieza del tablero.
 */
public abstract class Pieza {
    protected boolean blancas;
    protected String tag = "";

    public boolean isBlancas() {
        return blancas;
    }


    public void setBlancas(boolean blancas) {
        this.blancas = blancas;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
