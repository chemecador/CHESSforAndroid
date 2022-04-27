package com.example.chessforandroid;

import android.util.Log;

import com.example.chessforandroid.piezas.Dama;
import com.example.chessforandroid.piezas.Peon;
import com.example.chessforandroid.piezas.Rey;
import com.example.chessforandroid.piezas.Torre;

public class Juez {

    public static Casilla[][] casillas;
    public static final int NUM_FILAS = 8;
    public static final int NUM_COLUMNAS = 8;
    public static boolean turno;
    public static boolean jaque;
    public static boolean jaqueMate;
    public static boolean puedeMover;

    public static boolean comprobarJaque(Casilla[][] copia) {
        Casilla casRey = buscarRey(copia, turno);
        for (int i = 0; i < copia.length; i++) {
            for (int j = 0; j < copia.length; j++) {
                if (copia[i][j].getPieza() != null &&
                        (casRey.getPieza().isBlancas() != copia[i][j].getPieza().isBlancas())) {
                    if (esValido(copia, copia[i][j], casRey)) {
                        jaque = true;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void mover(Casilla cInicial, Casilla cFinal) {

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
            return;
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
            return;
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
            return;
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
            return;
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
                return;
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
                return;
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
    }

    public static boolean esValido(Casilla[][] copia, Casilla cInicial, Casilla cFinal) {
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


    public static Casilla buscarRey(Casilla[][] casillas, boolean blancas) {
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

    public static boolean puedeMover(Casilla[][] copia, boolean blancas) {
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

    public static boolean esValidoRey(Casilla cInicial, Casilla cFinal) {

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

    public static boolean esValidoCaballo(Casilla cInicial, Casilla cFinal) {
        if (Math.abs(cInicial.getFila() - cFinal.getFila()) == 2 &&
                Math.abs(cInicial.getColumna() - cFinal.getColumna()) == 1) {
            return true;
        }
        return Math.abs(cInicial.getFila() - cFinal.getFila()) == 1 &&
                Math.abs(cInicial.getColumna() - cFinal.getColumna()) == 2;
    }

    public static boolean esValidoAlfil(Casilla[][] copia, Casilla cInicial, Casilla cFinal) {
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

    public static boolean esValidoTorre(Casilla[][] copia, Casilla cInicial, Casilla cFinal) {
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

    public static boolean esValidoPeon(Casilla cInicial, Casilla cFinal) {

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
                if (p.pasable)
                    Log.i("**", "es pasable");
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
                if (p.pasable)
                    Log.i("**", "es pasable");
                return p.pasable;
            }
        }


        //el resto de casillas
        return false;
    }


}
