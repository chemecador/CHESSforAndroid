package juego;

import juego.casillas.*;

/**
 * Clase Juez. Encargada de comprobar la validez de cada movimiento
 */
public class Juez {

    // atributos
    Casilla[][] casillas;
    int[][] tablero;
    final int NUM_FILAS = 8;
    final int NUM_COLUMNAS = 8;
    boolean captura;
    boolean turnoBlancas;
    boolean jaque;
    boolean jaqueMate;
    boolean puedeMover;

    public Juez(){
        casillas = new Casilla[NUM_FILAS][NUM_COLUMNAS];
        tablero = new int[NUM_FILAS][NUM_COLUMNAS];
        jaqueMate = false;
        jaque = false;
        puedeMover = false;
        captura = false;
    }

    /**
     * Metodo que actualiza la matriz de casillas
     * @param t Matriz de enteros a convertir en matriz de casillas
     */
    public void actualizarCasillas(int[][] t){
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
    
    /**
     * Metodo que recibe el tablero en forma de string y lo convierte a matriz de enteros
     * @param s String con la posicion de las fichas
     * @return Matriz de enteros con la posicion de las fichas
     */
    public int[][] stringToInt(String s){
        int[][] t = new int[NUM_FILAS][NUM_COLUMNAS];
        int x = 0;
        for (int i = 0; i < NUM_FILAS; i++) {
            for (int j = 0; j < NUM_COLUMNAS; j++) {
                if (s.charAt(x) == '-') {
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
    
    /**
     * Metodo que comprueba si hay jaque
     * @param copia Matriz de casillas donde comprobar si hay jaque
     * @return True (hay jaque), False (no hay jaque)
     */
    public boolean comprobarJaque(Casilla[][] copia) {
        Casilla casRey = buscarRey(copia, turnoBlancas);
        for (Casilla[] fila : copia) {
            for (int j = 0; j < copia.length; j++) {
                if (fila[j].getPieza() != null) {
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
    
    /**
     * Metodo que comprueba si un movimiento es valido
     * @param copia Matriz de casillas donde comprobar el movimiento
     * @param cInicial Casilla inicial que contiene la pieza a mover
     * @param cFinal Casilla final donde se quiere mover la pieza
     * @return True (movimiento valido), False (movimiento no valido)
     */
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

    /**
     * Metodo que busca el rey en el tablero
     * @param casillas Matriz de casillas donde buscar el rey
     * @param blancas True si es el rey de las blancas, False si es el de las negras
     * @return Casilla en la que se encuentra el rey, o null si no se encuentra en el tablero
     */
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
    
    /**
     * Metodo que comprueba si un jugador puede mover alguna ficha
     * @param copia Tablero sobre el que comprobar si hay algun movimiento posible
     * @param blancas True si el jugador es blancas, False si es negras
     * @return True si hay algun movimiento, False si no
     */
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

    /**
     * Metodo que comprueba si el movimiento del rey es legal
     * @param cInicial Casilla inicial que contiene la pieza a mover
     * @param cFinal Casilla final donde se quiere mover la pieza
     * @return True si es legal, False si no lo es
     */
    public boolean esValidoRey(Casilla cInicial, Casilla cFinal) {

        Rey rey = (Rey) cInicial.getPieza();
        if (!rey.haMovido && !jaque) {
            if (!turnoBlancas) {
                // enroque largo negras
                if (casillas[0][0].getPieza() != null &&
                        casillas[0][0].getPieza().getTag().equalsIgnoreCase("TORRE") &&
                        cFinal.getFila() == 0 && cFinal.getColumna() == 2 &&
                        casillas[0][1].getPieza() == null && casillas[0][2].getPieza() == null &&
                        casillas[0][3].getPieza() == null) {
                    return true;
                }
                // enroque corto negras
                if (casillas[0][7].getPieza() != null &&
                        casillas[0][7].getPieza().getTag().equalsIgnoreCase("TORRE") &&
                        cFinal.getFila() == 0 && cFinal.getColumna() == 6 &&
                        casillas[0][6].getPieza() == null && casillas[0][5].getPieza() == null) {
                    return true;
                }
            }
            if (turnoBlancas) {
                // enroque largo blancas
                if (casillas[7][0].getPieza() != null &&
                        casillas[7][0].getPieza().getTag().equalsIgnoreCase("TORRE") &&
                        cFinal.getFila() == 7 && cFinal.getColumna() == 2 &&
                        casillas[7][1].getPieza() == null && casillas[7][2].getPieza() == null &&
                        casillas[7][3].getPieza() == null) {
                    return true;
                }
                // enroque corto blancas
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
    
    /**
     * Metodo que comprueba si el movimiento del caballo es legal
     * @param cInicial Casilla inicial que contiene la pieza a mover
     * @param cFinal Casilla final donde se quiere mover la pieza
     * @return True si es legal, False si no lo es
     */
    public boolean esValidoCaballo(Casilla cInicial, Casilla cFinal) {
        if (Math.abs(cInicial.getFila() - cFinal.getFila()) == 2 &&
                Math.abs(cInicial.getColumna() - cFinal.getColumna()) == 1) {
            return true;
        }
        return Math.abs(cInicial.getFila() - cFinal.getFila()) == 1 &&
                Math.abs(cInicial.getColumna() - cFinal.getColumna()) == 2;
    }

    /**
     * Metodo que comprueba si el movimiento del alfil es legal
     * @param copia Tablero donde comprobar el movimiento del alfil
     * @param cInicial Casilla inicial que contiene la pieza a mover
     * @param cFinal Casilla final donde se quiere mover la pieza
     * @return True si es legal, False si no lo es
     */
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
            
            // si estas pasando por encima de una pieza... arriba izquierda
            if (arriba && izquierda && copia[cInicial.getFila() - i][cInicial.getColumna() - i].getPieza() != null) {
                return false;
            }
            
            // si estas pasando por encima de una pieza... arriba derecha
            if (arriba && !izquierda && copia[cInicial.getFila() - i][cInicial.getColumna() + i].getPieza() != null) {
                return false;
            }
            // si estas pasando por encima de una pieza... abajo izquierda
            if (!arriba && izquierda && copia[cInicial.getFila() + i][cInicial.getColumna() - i].getPieza() != null) {
                return false;
            }
            // si estas pasando por encima de una pieza... abajo derecha
            if (!arriba && !izquierda && copia[cInicial.getFila() + i][cInicial.getColumna() + i].getPieza() != null) {
                return false;
            }
        }

        return true;
    }

    /**
     * Metodo que comprueba si el movimiento de la torre es legal
     * @param copia Tablero donde comprobar el movimiento del alfil
     * @param cInicial Casilla inicial que contiene la pieza a mover
     * @param cFinal Casilla final donde se quiere mover la pieza
     * @return True si es legal, False si no lo es
     */
    public boolean esValidoTorre(Casilla[][] copia, Casilla cInicial, Casilla cFinal) {
        if (cInicial.getFila() != cFinal.getFila() && cInicial.getColumna() != cFinal.getColumna()) {
            return false;
        }
        int disFila = Math.abs(cInicial.getFila() - cFinal.getFila());
        int disColumna = Math.abs(cInicial.getColumna() - cFinal.getColumna());
        // movimientos verticales
        if (disFila > disColumna) {
            boolean arriba = cInicial.getFila() > cFinal.getFila();
            for (int i = 1; i < disFila; i++) {
                // si estas pasando por encima de una pieza...
                if (arriba && copia[cInicial.getFila() - i][cInicial.getColumna()].getPieza() != null) {
                    return false;
                }
                if (!arriba && copia[cInicial.getFila() + i][cInicial.getColumna()].getPieza() != null) {
                    return false;
                }
            }
        }
        // movimientos horizontales
        else if (disFila < disColumna) {
            boolean izquierda = cInicial.getColumna() > cFinal.getColumna();
            for (int i = 1; i < disColumna; i++) {
                // si estas pasando por encima de una pieza...
                if (izquierda && copia[cInicial.getFila()][cInicial.getColumna() - i].getPieza() != null) {
                    return false;
                }
                if (!izquierda && copia[cInicial.getFila()][cInicial.getColumna() + i].getPieza() != null) {
                    return false;
                }
            }
        } else {
            // no hay movimiento
            return false;
        }
        // se cumplen todas las reglas, es valido
        return true;
    }

    /**
     * Metodo que comprueba si el movimiento de un peon es legal
     * @param cInicial Casilla inicial que contiene la pieza a mover
     * @param cFinal Casilla final donde se quiere mover la pieza
     * @return True si es legal, False si no lo es
     */
    public boolean esValidoPeon(Casilla cInicial, Casilla cFinal) {

        // choque de peones
        if (cInicial.getPieza().isBlancas() && cFinal.getFila() == cInicial.getFila() - 1 &&
                cFinal.getColumna() == cInicial.getColumna() && cFinal.getPieza() != null) {
            return false;
        }
        if (!cInicial.getPieza().isBlancas() && cFinal.getFila() == cInicial.getFila() + 1 &&
                cFinal.getColumna() == cInicial.getColumna() && cFinal.getPieza() != null) {
            return false;
        }
        // movimientos iniciales del peon
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
        // movimientos normales y capturas
        if (cInicial.getPieza().isBlancas() && (cFinal.getFila() == cInicial.getFila() - 1) &&
                ((cInicial.getColumna() == cFinal.getColumna()) ||
                        (cInicial.getColumna() == cFinal.getColumna() - 1 && cFinal.getPieza() != null) ||
                        (cInicial.getColumna() == cFinal.getColumna() + 1 && cFinal.getPieza() != null))) {

            // han movido las blancas asi que ningun peon negro es capturable al paso
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
            // han movido las negras asi que ningun peon blanco es capturable al paso
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
        // captura al paso

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


        // el resto de juego.casillas
        return false;
    }
}
