package com.example.chessforandroid;

import android.util.Log;

import com.example.chessforandroid.Casilla;
import com.example.chessforandroid.piezas.Dama;

public class Juez {

    public static Casilla[][] casillas;
    public static final int NUM_FILAS = 8;
    public static final int NUM_COLUMNAS = 8;
    public static boolean turno;
    public static boolean jaque;
    public static boolean jaqueMate;
    public static boolean puedeMover;


    public static Casilla[][] crearCopia() {
        Casilla[][] copia = new Casilla[NUM_FILAS][NUM_COLUMNAS];
        for (int i = 0; i < copia.length; i++) {
            for (int j = 0; j < copia.length; j++) {
                copia[i][j] = casillas[i][j].clonarCasilla();
            }
        }
        return copia;
    }


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

    public static boolean esLegal(Casilla[][] copia, Casilla cInicial, Casilla cFinal) {

        //Log.i("esLegal comienza:", "recibo que jaque es: " + jaque);
        jaque = comprobarJaque(copia);
        //Log.i("esLegal comienza:", "lo compruebo, jaque es: " + jaque);
        if (jaque) {
            jaque = !salvaJaque(copia, cInicial, cFinal);
//            if (jaque)
            //      Log.i("esLegal despues del mov", "" + cInicial.getPieza().getTag() + cInicial.getFila() + cInicial.getColumna() +
            //            " a " + cFinal.getFila() + cFinal.getColumna() + "salva el jaque");
            return !jaque;
        }
        //      Log.i("esLegal va a terminar:", " compruebo si el mov " + cInicial.getPieza().getTag() + cInicial.getFila() + cInicial.getColumna() +
        //            " a " + cFinal.getFila() + cFinal.getColumna() + " es legal");
        boolean ret = esValido(copia, cInicial, cFinal);
        if (ret) {
            Log.i("esLegal termina", "jaque: " + jaque + " - devuelvo:" + ret);
            Log.i("esLegal termina:", "el mov " + cInicial.getPieza().getTag() + cInicial.getFila() + cInicial.getColumna() +
                    " a " + cFinal.getFila() + cFinal.getColumna() + "es legal");
        } else {
            //      Log.i("esLegal termina", "no lo es ---- jaque: " + jaque + " - devuelvo:" + ret);
        }
        return ret;
    }

    public static boolean esLegal(Casilla cInicial, Casilla cFinal) {

/*        this.jaque = comprobarJaque(casillas);
        if (jaque) {
            Log.i("esLegal termina", "estoy en jaque, a ver si lo apaño...");
            jaque = !salvaJaque(casillas, cInicial, cFinal);
            return !jaque;
        }*/
        //      Log.i("esLegal va a terminar:", " compruebo si el mov " + cInicial.getPieza().getTag() + cInicial.getFila() + cInicial.getColumna() +
        //            " a " + cFinal.getFila() + cFinal.getColumna() + " es legal");
        boolean ret = esValido(casillas, cInicial, cFinal);
        if (ret) {
            Log.i("esLegal termina", "jaque: " + jaque + " - devuelvo:" + ret);
            Log.i("esLegal termina:", "el mov " + cInicial.getPieza().getTag() + cInicial.getFila() + cInicial.getColumna() +
                    " a " + cFinal.getFila() + cFinal.getColumna() + "es legal");
        } else {
            //      Log.i("esLegal termina", "no lo es ---- jaque: " + jaque + " - devuelvo:" + ret);
        }
        return ret;
    }

    public static boolean salvaJaque(Casilla[][] copia, Casilla cInicial, Casilla cFinal) {

        Casilla casRey = buscarRey(casillas, turno);

        //     Log.i("salvajaque", "rey es " + casRey.getPieza().isBlancas());
        if (esValido(copia, cInicial, cFinal)) {

            boolean ret = moverFalso(copia, cInicial, cFinal);

            //Log.i("salvajaque: comiendo dama", "pieza inicial es : " + cInicial.getPieza().getTag() +
            //      cInicial.getFila() + cInicial.getColumna());
            if (ret) {
//                  Log.i("salvaJaque termina:", "el mov " + cInicial.getPieza().getTag() + cInicial.getFila() + cInicial.getColumna() +
                //                      " a " + cFinal.getFila() + cFinal.getColumna() + "salva el jaque");
                return false;
            }
            return true;
        }
        // Log.i("salvaJaque termina:", "el mov " + cInicial.getPieza().getTag() + cInicial.getFila() + cInicial.getColumna() +
        //     " a " + cFinal.getFila() + cFinal.getColumna() + " no es valido");
        return false;
    }

    public static boolean moverFalso(Casilla[][] copia, Casilla cInicial, Casilla cFinal) {

        Casilla copiaInicial = cInicial.clonarCasilla();
        Casilla copiaFinal = cFinal.clonarCasilla();
        Log.i("moverFalso", "cInicial es: " + cInicial.getPieza());//.getTag()+cInicial.getFila()+cInicial.getColumna());
        Log.i("moverFalso", "copiaInicial es: " + copiaInicial.getPieza());//.getTag()+cInicial.getFila()+cInicial.getColumna());
        Log.i("moverFalso", "muevo a: " + cFinal.getFila() + cFinal.getColumna());

        //cInicial.getPieza().setFila(cFinal.getFila());
        //cInicial.getPieza().setColumna(cFinal.getColumna());

        //coronación de peones
        if (cInicial.getFila() == 1 &&
                copia[cInicial.getFila()][cInicial.getColumna()].getPieza() != null &&
                copia[cInicial.getFila()][cInicial.getColumna()].getPieza().getTag().equalsIgnoreCase("PEON") &&
                copia[cInicial.getFila()][cInicial.getColumna()].getPieza().isBlancas()) {
            copia[cFinal.getFila()][cFinal.getColumna()].setPieza(new Dama(cFinal.getFila(), cFinal.getColumna(), true));
            copia[cFinal.getFila()][cFinal.getColumna()].setImageResource(copia[cFinal.getFila()][cFinal.getColumna()].getPieza().getDrawable());
        } else if (cInicial.getFila() == 6 &&
                copia[cInicial.getFila()][cInicial.getColumna()].getPieza() != null &&
                copia[cInicial.getFila()][cInicial.getColumna()].getPieza().getTag().equalsIgnoreCase("PEON") &&
                !copia[cInicial.getFila()][cInicial.getColumna()].getPieza().isBlancas()) {
            copia[cFinal.getFila()][cFinal.getColumna()].setPieza(new Dama(cFinal.getFila(), cFinal.getColumna(), false));
            copia[cFinal.getFila()][cFinal.getColumna()].setImageResource(casillas[cFinal.getFila()][cFinal.getColumna()].getPieza().getDrawable());
        } else {
            //no coronan
            copia[cFinal.getFila()][cFinal.getColumna()].setPieza(cInicial.getPieza());
            copia[cFinal.getFila()][cFinal.getColumna()].setImageResource(cInicial.getPieza().getDrawable());
        }
        copia[cInicial.getFila()][cInicial.getColumna()].setPieza(null);
        copia[cInicial.getFila()][cInicial.getColumna()].setImageResource(0);
        boolean ret = comprobarJaque(copia);
        copia[cFinal.getFila()][cFinal.getColumna()] = copiaInicial;
        copia[cInicial.getFila()][cInicial.getColumna()] = copiaFinal;
        Log.i("moverFalso acabo en : ", copia[cFinal.getFila()][cFinal.getColumna()].getPieza().getTag() + " en " +
                copia[cFinal.getFila()][cFinal.getColumna()].getFila() + copia[cFinal.getFila()][cFinal.getColumna()].getColumna());
        return ret;
    }

    public static void mover(Casilla cInicial, Casilla cFinal) {
        cInicial.getPieza().setFila(cFinal.getFila());
        cInicial.getPieza().setColumna(cFinal.getColumna());
        //coronación de peones
        if (cInicial.getFila() == 1 &&
                casillas[cInicial.getFila()][cInicial.getColumna()].getPieza().getTag().equalsIgnoreCase("PEON") &&
                casillas[cInicial.getFila()][cInicial.getColumna()].getPieza().isBlancas()) {
            casillas[cFinal.getFila()][cFinal.getColumna()].setPieza(new Dama(cFinal.getFila(), cFinal.getColumna(), true));
            casillas[cFinal.getFila()][cFinal.getColumna()].setImageResource(casillas[cFinal.getFila()][cFinal.getColumna()].getPieza().getDrawable());
        } else if (cInicial.getFila() == 6 &&
                casillas[cInicial.getFila()][cInicial.getColumna()].getPieza().getTag().equalsIgnoreCase("PEON") &&
                !casillas[cInicial.getFila()][cInicial.getColumna()].getPieza().isBlancas()) {
            casillas[cFinal.getFila()][cFinal.getColumna()].setPieza(new Dama(cFinal.getFila(), cFinal.getColumna(), false));
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
                                Log.i("puedemover", "la casilla " + c.getFila() + c.getColumna()
                                        + " puede mover a " + copia[x][y].getFila() + copia[x][y].getColumna());
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
            return true;
        }
        if (!cInicial.getPieza().isBlancas() && cInicial.getFila() == 1 &&
                ((cFinal.getFila() == 2 && cInicial.getColumna() == cFinal.getColumna()) ||
                        (cFinal.getFila() == 3 && cInicial.getColumna() == cFinal.getColumna() && cFinal.getPieza() == null) ||
                        (cFinal.getPieza() != null && cFinal.getFila() == 2 && (cFinal.getColumna() == cInicial.getColumna() - 1 ||
                                cFinal.getColumna() == cInicial.getColumna() + 1)))) {
            return true;
        }
        //movimientos normales y capturas
        if (cInicial.getPieza().isBlancas() && (cFinal.getFila() == cInicial.getFila() - 1) &&
                ((cInicial.getColumna() == cFinal.getColumna()) ||
                        (cInicial.getColumna() == cFinal.getColumna() - 1 && cFinal.getPieza() != null) ||
                        (cInicial.getColumna() == cFinal.getColumna() + 1 && cFinal.getPieza() != null))) {
            return true;
        }
        if (!cInicial.getPieza().isBlancas() && (cFinal.getFila() == cInicial.getFila() + 1) &&
                ((cInicial.getColumna() == cFinal.getColumna()) ||
                        (cInicial.getColumna() == cFinal.getColumna() - 1 && cFinal.getPieza() != null) ||
                        (cInicial.getColumna() == cFinal.getColumna() + 1 && cFinal.getPieza() != null))) {
            return true;
        }
        //el resto de casillas
        return false;
    }


}
