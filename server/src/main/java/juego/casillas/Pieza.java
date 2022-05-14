package juego.casillas;

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
