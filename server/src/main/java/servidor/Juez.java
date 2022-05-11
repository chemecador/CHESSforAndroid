package servidor;

import casillas.*;

public class Juez {

    Casilla[][] casillas;
    int[][] tablero;
    final int NUM_FILAS = 8;
    final int NUM_COLUMNAS = 8;
    boolean captura;
    boolean turnoBlancas;
    boolean jaque;
    boolean jaqueMate;
    boolean puedeMover;
    int nMovs;
    StringBuilder movs;

    public Juez(){
        casillas = new Casilla[NUM_FILAS][NUM_COLUMNAS];
        tablero = new int[NUM_FILAS][NUM_COLUMNAS];
        jaqueMate = false;
        jaque = false;
        puedeMover = false;
        captura = false;
    }

    public void intToCasillas(int t[][]){
        for (int i = 0; i < NUM_FILAS; i++) {
            for (int j = 0; j < NUM_COLUMNAS; j++) {
                switch (t[i][j]){
                    case 0:
                        casillas[i][j].setPieza(null);
                        break;
                    case 1:
                        casillas[i][j].setPieza(new Rey(true));
                        break;
                    case -1:
                        casillas[i][j].setPieza(new Rey(false));
                        break;
                    case 2:
                        casillas[i][j].setPieza(new Dama(true));
                        break;
                    case -2:
                        casillas[i][j].setPieza(new Dama(false));
                        break;
                    case 3:
                        casillas[i][j].setPieza(new Torre(true));
                        break;
                    case -3:
                        casillas[i][j].setPieza(new Torre(false));
                        break;
                    case 4:
                        casillas[i][j].setPieza(new Alfil(true));
                        break;
                    case -4:
                        casillas[i][j].setPieza(new Alfil(false));
                        break;
                    case 5:
                        casillas[i][j].setPieza(new Caballo(true));
                        break;
                    case -5:
                        casillas[i][j].setPieza(new Caballo(false));
                        break;
                    case 6:
                        casillas[i][j].setPieza(new Peon(true));
                        break;
                    case -6:
                        casillas[i][j].setPieza(new Peon(false));
                        break;

                    default:
                        System.err.println("ERROR." + t[i][j]);
                }
            }
        }
    }

    public int[][] stringToInt(String s){
        int[][] t = new int[NUM_FILAS][NUM_COLUMNAS];
        int x = 0;
        for (int i = 0; i < NUM_FILAS; i++) {
            for (int j = 0; j < NUM_COLUMNAS; j++) {
                if (s.substring(x, x + 1).equals("-")) {
                    t[i][j] = Integer.parseInt(s.substring(x, x + 2));
                    x = x+2;
                } else {
                    t[i][j] = Integer.parseInt(s.substring(x, x + 1));
                    x++;
                }
            }
        }
        return t;
    }

    public String casillasToString() {
        StringBuilder sbResult = new StringBuilder();

        for (int i = 0; i < NUM_FILAS; i++) {
            for (int j = 0; j < NUM_COLUMNAS; j++) {
                if (casillas[i][j].getPieza() == null) {
                    sbResult.append(0);
                } else if (casillas[i][j].getPieza().getTag().equalsIgnoreCase("REY")) {
                    if (casillas[i][j].getPieza().isBlancas())
                        sbResult.append(1);
                    else
                        sbResult.append(-1);
                } else if (casillas[i][j].getPieza().getTag().equalsIgnoreCase("DAMA")) {
                    if (casillas[i][j].getPieza().isBlancas())
                        sbResult.append(2);
                    else
                        sbResult.append(-2);
                } else if (casillas[i][j].getPieza().getTag().equalsIgnoreCase("TORRE")) {
                    if (casillas[i][j].getPieza().isBlancas())
                        sbResult.append(3);
                    else
                        sbResult.append(-3);
                } else if (casillas[i][j].getPieza().getTag().equalsIgnoreCase("ALFIL")) {
                    if (casillas[i][j].getPieza().isBlancas())
                        sbResult.append(4);
                    else
                        sbResult.append(-4);
                } else if (casillas[i][j].getPieza().getTag().equalsIgnoreCase("CABALLO")) {
                    if (casillas[i][j].getPieza().isBlancas())
                        sbResult.append(5);
                    else
                        sbResult.append(-5);
                } else if (casillas[i][j].getPieza().getTag().equalsIgnoreCase("PEON")) {
                    if (casillas[i][j].getPieza().isBlancas())
                        sbResult.append(6);
                    else
                        sbResult.append(-6);
                }
            }
        }
        return sbResult.toString();
    }

    public boolean comprobarJaque(Casilla[][] copia) {
        Casilla casRey = buscarRey(copia, turnoBlancas);
        for (Casilla[] fila : copia) {
            for (int j = 0; j < copia.length; j++) {
                if (fila[j].getPieza() != null) {
                    assert casRey != null;
                    if (casRey.getPieza().isBlancas() != fila[j].getPieza().isBlancas()) {
                        if (esValido(copia, fila[j], casRey)) {
                            jaque = true;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean esValido(Casilla[][] copia, Casilla cInicial, Casilla cFinal) {
        if (cInicial.getPieza() == null || (cFinal.getPieza() != null &&
                cInicial.getPieza().isBlancas() == cFinal.getPieza().isBlancas())) {
            return false;
        }
        String tag = cInicial.getPieza().getTag();
        switch (tag) {
            case "PEON":
                return esValidoPeon(cInicial, cFinal);
            case "REY":
                return esValidoRey(cInicial, cFinal);
            case "CABALLO":
                return esValidoCaballo(cInicial, cFinal);
            case "TORRE":
                return esValidoTorre(copia, cInicial, cFinal);
            case "ALFIL":
                return esValidoAlfil(copia, cInicial, cFinal);
            case "DAMA":
                return esValidoAlfil(copia, cInicial, cFinal) || esValidoTorre(copia, cInicial, cFinal);
        }
        return false;
    }


    public Casilla buscarRey(Casilla[][] casillas, boolean blancas) {
        for (int i = 0; i < NUM_FILAS; i++) {
            for (int j = 0; j < NUM_COLUMNAS; j++) {
                if (casillas[i][j].getPieza() != null &&
                        casillas[i][j].getPieza().getTag().equalsIgnoreCase("REY") &&
                        casillas[i][j].getPieza().isBlancas() == blancas) {
                    return casillas[i][j];

                }
            }
        }
        return null;
    }

    public boolean puedeMover(Casilla[][] copia, boolean blancas) {
        Casilla c, copy;
        for (int i = 0; i < NUM_FILAS; i++) {
            for (int j = 0; j < NUM_COLUMNAS; j++) {
                if (copia[i][j].getPieza() != null && copia[i][j].getPieza().isBlancas() == blancas) {
                    c = copia[i][j];
                    copy = c.clonarCasilla();
                    for (int x = 0; x < NUM_FILAS; x++) {
                        for (int y = 0; y < NUM_COLUMNAS; y++) {
                            if (esValido(copia, copy, copia[x][y])) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean esValidoRey(Casilla cInicial, Casilla cFinal) {

        Rey rey = (Rey) cInicial.getPieza();
        if (!rey.haMovido && !jaque) {
            if (!turnoBlancas) {
                //enroque largo negras
                if (casillas[0][0].getPieza() != null &&
                        casillas[0][0].getPieza().getTag().equalsIgnoreCase("TORRE") &&
                        cFinal.getFila() == 0 && cFinal.getColumna() == 2 &&
                        casillas[0][1].getPieza() == null && casillas[0][2].getPieza() == null &&
                        casillas[0][3].getPieza() == null) {
                    return true;
                }
                //enroque corto negras
                if (casillas[0][7].getPieza() != null &&
                        casillas[0][7].getPieza().getTag().equalsIgnoreCase("TORRE") &&
                        cFinal.getFila() == 0 && cFinal.getColumna() == 6 &&
                        casillas[0][6].getPieza() == null && casillas[0][5].getPieza() == null) {
                    return true;
                }
            }
            if (turnoBlancas) {
                //enroque largo blancas
                if (casillas[7][0].getPieza() != null &&
                        casillas[7][0].getPieza().getTag().equalsIgnoreCase("TORRE") &&
                        cFinal.getFila() == 7 && cFinal.getColumna() == 2 &&
                        casillas[7][1].getPieza() == null && casillas[7][2].getPieza() == null &&
                        casillas[7][3].getPieza() == null) {
                    return true;
                }
                //enroque corto blancas
                if (casillas[7][7].getPieza() != null &&
                        casillas[7][7].getPieza().getTag().equalsIgnoreCase("TORRE") &&
                        cFinal.getFila() == 7 && cFinal.getColumna() == 6 &&
                        casillas[7][6].getPieza() == null && casillas[7][5].getPieza() == null) {
                    return true;
                }
            }
        }

        return Math.abs(cInicial.getFila() - cFinal.getFila()) < 2 &&
                Math.abs(cInicial.getColumna() - cFinal.getColumna()) < 2;
    }

    public boolean esValidoCaballo(Casilla cInicial, Casilla cFinal) {
        if (Math.abs(cInicial.getFila() - cFinal.getFila()) == 2 &&
                Math.abs(cInicial.getColumna() - cFinal.getColumna()) == 1) {
            return true;
        }
        return Math.abs(cInicial.getFila() - cFinal.getFila()) == 1 &&
                Math.abs(cInicial.getColumna() - cFinal.getColumna()) == 2;
    }

    public boolean esValidoAlfil(Casilla[][] copia, Casilla cInicial, Casilla cFinal) {
        if (cInicial.getFila() == cFinal.getFila() || cInicial.getColumna() == cFinal.getColumna()) {
            return false;
        }
        if (Math.abs(cInicial.getFila() - cFinal.getFila()) != Math.abs(cInicial.getColumna() - cFinal.getColumna())) {
            return false;
        }
        int dis = Math.abs(cInicial.getFila() - cFinal.getFila());
        boolean arriba = cInicial.getFila() > cFinal.getFila();
        boolean izquierda = cInicial.getColumna() > cFinal.getColumna();

        for (int i = 1; i < dis; i++) {
            //si est�s pasando por encima de una pieza... arriba izquierda
            if (arriba && izquierda && copia[cInicial.getFila() - i][cInicial.getColumna() - i].getPieza() != null) {
                return false;
            }
            //si est�s pasando por encima de una pieza... arriba derecha
            if (arriba && !izquierda && copia[cInicial.getFila() - i][cInicial.getColumna() + i].getPieza() != null) {
                return false;
            }
            //si est�s pasando por encima de una pieza... abajo izquierda
            if (!arriba && izquierda && copia[cInicial.getFila() + i][cInicial.getColumna() - i].getPieza() != null) {
                return false;
            }
            //si est�s pasando por encima de una pieza... abajo derecha
            if (!arriba && !izquierda && copia[cInicial.getFila() + i][cInicial.getColumna() + i].getPieza() != null) {
                return false;
            }
        }

        return true;
    }

    public boolean esValidoTorre(Casilla[][] copia, Casilla cInicial, Casilla cFinal) {
        if (cInicial.getFila() != cFinal.getFila() && cInicial.getColumna() != cFinal.getColumna()) {
            return false;
        }
        int disFila = Math.abs(cInicial.getFila() - cFinal.getFila());
        int disColumna = Math.abs(cInicial.getColumna() - cFinal.getColumna());
        //movimientos verticales
        if (disFila > disColumna) {
            boolean arriba = cInicial.getFila() > cFinal.getFila();
            for (int i = 1; i < disFila; i++) {
                //si est�s pasando por encima de una pieza...
                if (arriba && copia[cInicial.getFila() - i][cInicial.getColumna()].getPieza() != null) {
                    return false;
                }
                if (!arriba && copia[cInicial.getFila() + i][cInicial.getColumna()].getPieza() != null) {
                    return false;
                }
            }
        }
        //movimientos horizontales
        else if (disFila < disColumna) {
            boolean izquierda = cInicial.getColumna() > cFinal.getColumna();
            for (int i = 1; i < disColumna; i++) {
                //si est�s pasando por encima de una pieza...
                if (izquierda && copia[cInicial.getFila()][cInicial.getColumna() - i].getPieza() != null) {
                    return false;
                }
                if (!izquierda && copia[cInicial.getFila()][cInicial.getColumna() + i].getPieza() != null) {
                    return false;
                }
            }
        } else {
            //no hay movimiento
            return false;
        }
        //se cumplen todas las reglas, es v�lido
        return true;
    }

    public boolean esValidoPeon(Casilla cInicial, Casilla cFinal) {

        //choque de peones
        if (cInicial.getPieza().isBlancas() && cFinal.getFila() == cInicial.getFila() - 1 &&
                cFinal.getColumna() == cInicial.getColumna() && cFinal.getPieza() != null) {
            return false;
        }
        if (!cInicial.getPieza().isBlancas() && cFinal.getFila() == cInicial.getFila() + 1 &&
                cFinal.getColumna() == cInicial.getColumna() && cFinal.getPieza() != null) {
            return false;
        }
        //movimientos iniciales del peon
        if (cInicial.getPieza().isBlancas() && cInicial.getFila() == 6 &&
                ((cFinal.getFila() == 5 && cInicial.getColumna() == cFinal.getColumna()) ||
                        (cFinal.getFila() == 4 && cInicial.getColumna() == cFinal.getColumna() && cFinal.getPieza() == null) ||
                        (cFinal.getPieza() != null && cFinal.getFila() == 5 && (cFinal.getColumna() == cInicial.getColumna() - 1 ||
                                cFinal.getColumna() == cInicial.getColumna() + 1)))) {
            if (cFinal.getFila() == 4) {
                Peon p = (Peon) cInicial.getPieza();
                p.pasable = true;
            }
            return true;
        }
        if (!cInicial.getPieza().isBlancas() && cInicial.getFila() == 1 &&
                ((cFinal.getFila() == 2 && cInicial.getColumna() == cFinal.getColumna()) ||
                        (cFinal.getFila() == 3 && cInicial.getColumna() == cFinal.getColumna() && cFinal.getPieza() == null) ||
                        (cFinal.getPieza() != null && cFinal.getFila() == 2 && (cFinal.getColumna() == cInicial.getColumna() - 1 ||
                                cFinal.getColumna() == cInicial.getColumna() + 1)))) {
            if (cFinal.getFila() == 3) {
                Peon p = (Peon) cInicial.getPieza();
                p.pasable = true;
            }
            return true;
        }
        //movimientos normales y capturas
        if (cInicial.getPieza().isBlancas() && (cFinal.getFila() == cInicial.getFila() - 1) &&
                ((cInicial.getColumna() == cFinal.getColumna()) ||
                        (cInicial.getColumna() == cFinal.getColumna() - 1 && cFinal.getPieza() != null) ||
                        (cInicial.getColumna() == cFinal.getColumna() + 1 && cFinal.getPieza() != null))) {

            //han movido las blancas as� que ning�n pe�n negro es capturable al paso
            for (int i = 0; i < NUM_FILAS; i++) {
                if (casillas[3][i].getPieza() != null &&
                        casillas[3][i].getPieza().getTag().equalsIgnoreCase("PEON")) {
                    Peon p = (Peon) casillas[3][i].getPieza();
                    p.pasable = false;
                }
                if (casillas[4][i].getPieza() != null &&
                        casillas[4][i].getPieza().getTag().equalsIgnoreCase("PEON")) {
                    Peon p = (Peon) casillas[4][i].getPieza();
                    p.pasable = false;
                }
            }
            return true;
        }
        if (!cInicial.getPieza().isBlancas() && (cFinal.getFila() == cInicial.getFila() + 1) &&
                ((cInicial.getColumna() == cFinal.getColumna()) ||
                        (cInicial.getColumna() == cFinal.getColumna() - 1 && cFinal.getPieza() != null) ||
                        (cInicial.getColumna() == cFinal.getColumna() + 1 && cFinal.getPieza() != null))) {
            //han movido las negras as� que ning�n pe�n blanco es capturable al paso
            for (int i = 0; i < NUM_FILAS; i++) {
                if (casillas[3][i].getPieza() != null &&
                        casillas[3][i].getPieza().getTag().equalsIgnoreCase("PEON")) {
                    Peon p = (Peon) casillas[3][i].getPieza();
                    p.pasable = false;
                }
                if (casillas[4][i].getPieza() != null &&
                        casillas[4][i].getPieza().getTag().equalsIgnoreCase("PEON")) {
                    Peon p = (Peon) casillas[4][i].getPieza();
                    p.pasable = false;
                }
            }
            return true;
        }
        //captura al paso

        if (cInicial.getPieza().isBlancas() && cInicial.getFila() == 3 &&
                (cFinal.getColumna() == cInicial.getColumna() + 1 ||
                        (cFinal.getColumna() == cInicial.getColumna() - 1))) {
            if (casillas[3][cFinal.getColumna()].getPieza() != null &&
                    !casillas[3][cFinal.getColumna()].getPieza().isBlancas() &&
                    casillas[3][cFinal.getColumna()].getPieza().getTag().equalsIgnoreCase("PEON")) {
                Peon p = (Peon) casillas[3][cFinal.getColumna()].getPieza();
                return p.pasable;
            }
        }

        if (!cInicial.getPieza().isBlancas() && cInicial.getFila() == 4 &&
                (cFinal.getColumna() == cInicial.getColumna() + 1 ||
                        (cFinal.getColumna() == cInicial.getColumna() - 1))) {
            if (casillas[4][cFinal.getColumna()].getPieza() != null &&
                    casillas[4][cFinal.getColumna()].getPieza().isBlancas() &&
                    casillas[4][cFinal.getColumna()].getPieza().getTag().equalsIgnoreCase("PEON")) {
                Peon p = (Peon) casillas[4][cFinal.getColumna()].getPieza();
                return p.pasable;
            }
        }


        //el resto de casillas
        return false;
    }
}