package juego.casillas;

public class Casilla {
    private int fila;
    private int columna;
    private Pieza pieza;

    public Casilla(int x, int y) {
        this.fila = x;
        this.columna= y;
        this.pieza = null;
    }

    public Casilla clonarCasilla(){
        Casilla clon = new Casilla(fila,columna);
        if (this.getPieza() != null) {
            switch (this.getPieza().getTag()) {
                case "REY":
                    clon.setPieza(new Rey(this.getPieza().isBlancas()));
                    break;
                case "DAMA":
                    clon.setPieza(new Dama(this.getPieza().isBlancas()));
                    break;
                case "ALFIL":
                    clon.setPieza(new Alfil(this.getPieza().isBlancas()));
                    break;
                case "CABALLO":
                    clon.setPieza(new Caballo(this.getPieza().isBlancas()));
                    break;
                case "TORRE":
                    clon.setPieza(new Torre(this.getPieza().isBlancas()));
                    break;
                case "PEON":
                    clon.setPieza(new Peon(this.getPieza().isBlancas()));
                    break;
            }
        }
        return clon;
    }

    public int getFila() {
        return this.fila;
    }

    public void setFila(int x) {
        this.fila = x;
    }

    public int getColumna() {
        return this.columna;
    }

    public void setColumna(int y) {
        this.columna= y;
    }

    public Pieza getPieza() {
        return pieza;
    }

    public void setPieza(Pieza pieza) {
        this.pieza = pieza;
    }
}
