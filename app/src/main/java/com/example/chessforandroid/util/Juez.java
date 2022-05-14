package com.example.chessforandroid.util;

import static com.example.chessforandroid.util.Constantes.NUM_COLUMNAS;
import static com.example.chessforandroid.util.Constantes.NUM_FILAS;

import com.example.chessforandroid.R;
import com.example.chessforandroid.juego.Casilla;
import com.example.chessforandroid.piezas.*;

public class Juez {

    public Casilla[][] casillas;
    public int[][] inTablero;
    public String sTablero;
    public boolean captura;
    public boolean turno;
    public boolean jaque;
    public boolean jaqueMate;
    public boolean puedeMover;

    public Juez(){
        inTablero = new int[NUM_FILAS][NUM_COLUMNAS];
        turno = true;
        casillas = new Casilla[NUM_FILAS][NUM_COLUMNAS];
        jaqueMate = false;
        jaque = false;
        puedeMover = false;
        captura = false;
    }

    public void actualizarCasillas(int[][] t){
        for (int i = 0; i < NUM_FILAS; i++) {
            for (int j = 0; j < NUM_COLUMNAS; j++) {
                switch (t[i][j]){
                    case 0:
                        casillas[i][j].setPieza(null);
                        casillas[i][j].setImageResource(0);
                        break;
                    case 1:
                        casillas[i][j].setPieza(new Rey(true));
                        casillas[i][j].setImageResource(R.drawable.brey);
                        break;
                    case -1:
                        casillas[i][j].setPieza(new Rey(false));
                        casillas[i][j].setImageResource(R.drawable.nrey);
                        break;
                    case 2:
                        casillas[i][j].setPieza(new Dama(true));
                        casillas[i][j].setImageResource(R.drawable.bdama);
                        break;
                    case -2:
                        casillas[i][j].setPieza(new Dama(false));
                        casillas[i][j].setImageResource(R.drawable.ndama);
                        break;
                    case 3:
                        casillas[i][j].setPieza(new Torre(true));
                        casillas[i][j].setImageResource(R.drawable.btorre);
                        break;
                    case -3:
                        casillas[i][j].setPieza(new Torre(false));
                        casillas[i][j].setImageResource(R.drawable.ntorre);
                        break;
                    case 4:
                        casillas[i][j].setPieza(new Alfil(true));
                        casillas[i][j].setImageResource(R.drawable.balfil);
                        break;
                    case -4:
                        casillas[i][j].setPieza(new Alfil(false));
                        casillas[i][j].setImageResource(R.drawable.nalfil);
                        break;
                    case 5:
                        casillas[i][j].setPieza(new Caballo(true));
                        casillas[i][j].setImageResource(R.drawable.bcaballo);
                        break;
                    case -5:
                        casillas[i][j].setPieza(new Caballo(false));
                        casillas[i][j].setImageResource(R.drawable.ncaballo);
                        break;
                    case 6:
                        casillas[i][j].setPieza(new Peon(true));
                        casillas[i][j].setImageResource(R.drawable.bpeon);
                        break;
                    case -6:
                        casillas[i][j].setPieza(new Peon(false));
                        casillas[i][j].setImageResource(R.drawable.npeon);
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



    /***
     * Desglose de piezas (positivo - blancas,  negativo - negras):
     * 0: Casilla Vacía
     * 1: Rey
     * 2: Dama
     * 3: Torre
     * 4: Alfil
     * 5: Caballo
     * 6: Peón
     */


    public boolean comprobarJaque(Casilla[][] copia) {
        Casilla casRey = buscarRey(copia, turno);
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

    public boolean mover(Casilla cInicial, Casilla cFinal) {

        if (!cInicial.getPieza().getTag().equalsIgnoreCase("PEON")) {
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
        }

        //si ha movido la torre o el rey, no puede enrocar
        if (cInicial.getPieza().getTag().equalsIgnoreCase("TORRE")) {
            Torre t = (Torre) cInicial.getPieza();
            t.haMovido = true;
        }

        if (cInicial.getPieza().getTag().equalsIgnoreCase("REY")) {
            Rey r = (Rey) cInicial.getPieza();
            r.haMovido = true;
        }

        //enroque corto negras
        if (cInicial.getPieza().getTag().equalsIgnoreCase("REY") &&
                cInicial.getFila() == 0 && cInicial.getColumna() == 4 &&
                cFinal.getFila() == 0 && cFinal.getColumna() == 6) {
            casillas[0][6].setPieza(casillas[0][4].getPieza());
            casillas[0][6].setImageResource(casillas[0][4].getPieza().getDrawable());

            casillas[0][5].setPieza(casillas[0][7].getPieza());
            casillas[0][5].setImageResource(casillas[0][7].getPieza().getDrawable());

            casillas[0][7].setPieza(null);
            casillas[0][7].setImageResource(0);

            casillas[0][4].setPieza(null);
            casillas[0][4].setImageResource(0);
            return true;
        }
        //enroque largo negras
        if (cInicial.getPieza().getTag().equalsIgnoreCase("REY") &&
                cInicial.getFila() == 0 && cInicial.getColumna() == 4 &&
                cFinal.getFila() == 0 && cFinal.getColumna() == 2) {
            casillas[0][2].setPieza(casillas[0][4].getPieza());
            casillas[0][2].setImageResource(casillas[0][4].getPieza().getDrawable());

            casillas[0][3].setPieza(casillas[0][7].getPieza());
            casillas[0][3].setImageResource(casillas[0][7].getPieza().getDrawable());

            casillas[0][0].setPieza(null);
            casillas[0][0].setImageResource(0);

            casillas[0][4].setPieza(null);
            casillas[0][4].setImageResource(0);
            return true;
        }

        //enroque corto blancas
        if (cInicial.getPieza().getTag().equalsIgnoreCase("REY") &&
                cInicial.getFila() == 7 && cInicial.getColumna() == 4 &&
                cFinal.getFila() == 7 && cFinal.getColumna() == 6) {
            casillas[7][6].setPieza(casillas[7][4].getPieza());
            casillas[7][6].setImageResource(casillas[7][4].getPieza().getDrawable());

            casillas[7][5].setPieza(casillas[7][7].getPieza());
            casillas[7][5].setImageResource(casillas[7][7].getPieza().getDrawable());

            casillas[7][7].setPieza(null);
            casillas[7][7].setImageResource(0);

            casillas[7][4].setPieza(null);
            casillas[7][4].setImageResource(0);
            return true;
        }

        //enroque largo blancas
        if (cInicial.getPieza().getTag().equalsIgnoreCase("REY") &&
                cInicial.getFila() == 7 && cInicial.getColumna() == 4 &&
                cFinal.getFila() == 7 && cFinal.getColumna() == 2) {
            casillas[7][2].setPieza(casillas[7][4].getPieza());
            casillas[7][2].setImageResource(casillas[7][4].getPieza().getDrawable());

            casillas[7][3].setPieza(casillas[7][7].getPieza());
            casillas[7][3].setImageResource(casillas[7][7].getPieza().getDrawable());

            casillas[7][0].setPieza(null);
            casillas[7][0].setImageResource(0);

            casillas[7][4].setPieza(null);
            casillas[7][4].setImageResource(0);
            return true;
        }

        //captura al paso blancas
        if (cInicial.getPieza().getTag().equalsIgnoreCase("PEON") &&
                cInicial.getPieza().isBlancas() && cInicial.getFila() == 3) {
            if (casillas[3][cFinal.getColumna()].getPieza() != null &&
                    !casillas[3][cFinal.getColumna()].getPieza().isBlancas() &&
                    casillas[3][cFinal.getColumna()].getPieza().getTag().equalsIgnoreCase("PEON")) {

                casillas[2][cFinal.getColumna()].setPieza(casillas[3][cInicial.getColumna()].getPieza());
                casillas[2][cFinal.getColumna()].setImageResource(casillas[3][cInicial.getColumna()].getPieza().getDrawable());

                casillas[3][cInicial.getColumna()].setPieza(null);
                casillas[3][cInicial.getColumna()].setImageResource(0);

                casillas[3][cFinal.getColumna()].setPieza(null);
                casillas[3][cFinal.getColumna()].setImageResource(0);
                return true;
            }
        }

        //captura al paso negras
        if (cInicial.getPieza().getTag().equalsIgnoreCase("PEON") &&
                !cInicial.getPieza().isBlancas() && cInicial.getFila() == 4) {
            if (casillas[4][cFinal.getColumna()].getPieza() != null &&
                    casillas[4][cFinal.getColumna()].getPieza().isBlancas() &&
                    casillas[4][cFinal.getColumna()].getPieza().getTag().equalsIgnoreCase("PEON")) {

                casillas[5][cFinal.getColumna()].setPieza(casillas[4][cInicial.getColumna()].getPieza());
                casillas[5][cFinal.getColumna()].setImageResource(casillas[4][cInicial.getColumna()].getPieza().getDrawable());

                casillas[4][cInicial.getColumna()].setPieza(null);
                casillas[4][cInicial.getColumna()].setImageResource(0);

                casillas[4][cFinal.getColumna()].setPieza(null);
                casillas[4][cFinal.getColumna()].setImageResource(0);
                return true;
            }
        }


        //coronación de peones
        if (cInicial.getFila() == 1 &&
                casillas[cInicial.getFila()][cInicial.getColumna()].getPieza().getTag().equalsIgnoreCase("PEON") &&
                casillas[cInicial.getFila()][cInicial.getColumna()].getPieza().isBlancas()) {
            casillas[cFinal.getFila()][cFinal.getColumna()].setPieza(new Dama(true));
            casillas[cFinal.getFila()][cFinal.getColumna()].setImageResource(casillas[cFinal.getFila()][cFinal.getColumna()].getPieza().getDrawable());
        } else if (cInicial.getFila() == 6 &&
                casillas[cInicial.getFila()][cInicial.getColumna()].getPieza().getTag().equalsIgnoreCase("PEON") &&
                !casillas[cInicial.getFila()][cInicial.getColumna()].getPieza().isBlancas()) {
            casillas[cFinal.getFila()][cFinal.getColumna()].setPieza(new Dama(false));
            casillas[cFinal.getFila()][cFinal.getColumna()].setImageResource(casillas[cFinal.getFila()][cFinal.getColumna()].getPieza().getDrawable());
        } else {
            //no coronan
            casillas[cFinal.getFila()][cFinal.getColumna()].setPieza(cInicial.getPieza());
            casillas[cFinal.getFila()][cFinal.getColumna()].setImageResource(cInicial.getPieza().getDrawable());
        }
        casillas[cInicial.getFila()][cInicial.getColumna()].setPieza(null);
        casillas[cInicial.getFila()][cInicial.getColumna()].setImageResource(0);
        return true;
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
            if (!turno) {
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
            if (turno) {
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
            //si estás pasando por encima de una pieza... arriba izquierda
            if (arriba && izquierda && copia[cInicial.getFila() - i][cInicial.getColumna() - i].getPieza() != null) {
                return false;
            }
            //si estás pasando por encima de una pieza... arriba derecha
            if (arriba && !izquierda && copia[cInicial.getFila() - i][cInicial.getColumna() + i].getPieza() != null) {
                return false;
            }
            //si estás pasando por encima de una pieza... abajo izquierda
            if (!arriba && izquierda && copia[cInicial.getFila() + i][cInicial.getColumna() - i].getPieza() != null) {
                return false;
            }
            //si estás pasando por encima de una pieza... abajo derecha
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
                //si estás pasando por encima de una pieza...
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
                //si estás pasando por encima de una pieza...
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
        //se cumplen todas las reglas, es válido
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

            //han movido las blancas así que ningún peón negro es capturable al paso
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
            //han movido las negras así que ningún peón blanco es capturable al paso
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

    public char[] coorToChar(int fila, int col) {
        char[] ch = new char[2];

        ch[0] = (char) ('a' + col);

        switch (fila) {
            case 0:
                ch[1] = '8';
                break;
            case 1:
                ch[1] = '7';
                break;
            case 2:
                ch[1] = '6';
                break;
            case 3:
                ch[1] = '5';
                break;
            case 4:
                ch[1] = '4';
                break;
            case 5:
                ch[1] = '3';
                break;
            case 6:
                ch[1] = '2';
                break;
            case 7:
                ch[1] = '1';
                break;
        }
        return ch;
    }
}
