package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.chessforandroid.piezas.Alfil;
import com.example.chessforandroid.piezas.Caballo;
import com.example.chessforandroid.piezas.Dama;
import com.example.chessforandroid.piezas.Peon;
import com.example.chessforandroid.piezas.Rey;
import com.example.chessforandroid.piezas.Torre;


public class DosJugadoresActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int NUM_FILAS = 8;
    private static final int NUM_COLUMNAS = 8;
    private GridLayout oGameBoard;
    private LinearLayout oGameBoardShell;
    private Casilla[][] casillas;
    private boolean haySeleccionada;
    private boolean turno;
    private boolean jaque;
    private boolean fin;
    private Casilla quieroMover;
    private int nMovs;
    //private ArrayList<String> movimientosPosibles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        haySeleccionada = false;
        jaque = false;
        fin = false;
        turno = true;
        nMovs = 0;
        setContentView(R.layout.activity_dos_jugadores);

        this.casillas = new Casilla[NUM_FILAS][NUM_COLUMNAS];
        //this.movimientosPosibles = new ArrayList<>();
        crearCasillas();

        this.oGameBoardShell = (LinearLayout) this.findViewById(R.id.shellGameBoard);
        this.oGameBoard = (GridLayout) this.findViewById(R.id.gridGameBoard);
        Tablero t = new Tablero();
        t.execute();


    }


    @Override
    public void onClick(View view) {

        Casilla c = (Casilla) view;

        if (!haySeleccionada) {
            if (c.getPieza() == null)
                return;
            if (c.getPieza().isBlancas() != turno) {
                if (turno)
                    Toast.makeText(this, "Juegan las blancas", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Juegan las negras", Toast.LENGTH_SHORT).show();
                return;
            }

            c.setBackgroundColor(Color.parseColor("#36E0FA"));
            quieroMover = c;
            //this.movimientosPosibles = cargarMovimientos(casillas, quieroMover.getPieza());
        } else {
            Log.i("*******************************************************************", "empiezo MOV");
            Log.i("*******************************************************************", "empiezo MOV");
            pintarFondo();
            //intentarMover(casillas, quieroMover, c, movimientosPosibles);
            if (esLegal(casillas, quieroMover, c)) {
                mover(casillas, quieroMover, c);
                nMovs++;
                turno = !turno;
                Log.i("antes de comprobar", "hay jaque: " + jaque);
                if (comprobarJaque(casillas)) {
                    Toast.makeText(this, "JAQUE " + turno, Toast.LENGTH_SHORT).show();

                }
                Log.i("despues de comprobar", "hay jaque: " + jaque);
                if (!puedeMover(casillas, turno)){
                    fin = true;
                    Toast.makeText(this, "FINAL", Toast.LENGTH_SHORT).show();
                   // Log.i ("FINAL: " , ""+quieroMover.getPieza().getTag() + quieroMover.getFila()+quieroMover.getColumna());
                    //Casilla casRey = buscarRey(casillas, turno);
                    //casRey.setBackgroundColor(Color.RED);
                }
                Log.i("despues de mover", "hay jaque: " + jaque);
            }
            Log.i("*******************************************************************", "ACABO MOV");
            Log.i("*******************************************************************", "ACABO MOV");
        }

        haySeleccionada = !haySeleccionada;
    }

    private boolean puedeMover(Casilla[][] casillas, boolean blancas) {
        Casilla c;
        for (int i = 0; i < NUM_FILAS; i++) {
            for (int j = 0; j < NUM_COLUMNAS; j++) {
                if (casillas[i][j].getPieza() != null && casillas[i][j].getPieza().isBlancas() == blancas) {
                    c = casillas[i][j];
                    for (int x = 0; x < NUM_FILAS; x++) {
                        for (int y = 0; y < NUM_COLUMNAS; y++) {
                            if (esLegal(casillas, c, casillas[x][y])) {
                                Log.i("puedemover", "la casilla " + c.getFila() + c.getColumna()
                                        + " puede mover a " + casillas[x][y].getFila() + casillas[x][y].getColumna());
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }


    private Casilla buscarRey(Casilla[][] casillas, boolean blancas) {
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

    private boolean comprobarJaque(Casilla[][] casillas) {
        Casilla casRey = buscarRey(casillas,turno);
        for (int i = 0; i < casillas.length; i++) {
            for (int j = 0; j < casillas.length; j++) {
                if (casillas[i][j].getPieza() != null && casRey.getPieza() != null &&
                        (casRey.getPieza().isBlancas() != casillas[i][j].getPieza().isBlancas())) {
                    if (esValido(casillas, casillas[i][j], casRey)) {
                        jaque = true;
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private boolean esLegal(Casilla[][] casillas, Casilla cInicial, Casilla cFinal) {

        //Log.i("esLegal comienza:", "recibo que jaque es: " + jaque);
        this.jaque = comprobarJaque(casillas);
        //Log.i("esLegal comienza:", "lo compruebo, jaque es: " + jaque);
        if (jaque) {
            jaque = !salvaJaque(casillas, cInicial, cFinal);
//            if (jaque)
          //      Log.i("esLegal despues del mov", "" + cInicial.getPieza().getTag() + cInicial.getFila() + cInicial.getColumna() +
            //            " a " + cFinal.getFila() + cFinal.getColumna() + "salva el jaque");
            return !jaque;
        }
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

    private boolean salvaJaque(Casilla[][] casillas, Casilla cInicial, Casilla cFinal) {

        Casilla casRey = buscarRey(casillas, turno);
        Casilla[][] copia = casillas.clone();
        Casilla copiaInicial = cInicial;
        Casilla copiaFinal = cFinal;

        //      Log.i("salvajaque", "hay jaque: " + jaque);
        //     Log.i("salvajaque", "rey es " + casRey.getPieza().isBlancas());
        if (esValido(copia, copiaInicial, copiaFinal)) {
            //       Log.i("salvajaque: comiendo dama", "pieza inicial es : " + cInicial.getPieza().getTag() +
            //             cInicial.getFila() + cInicial.getColumna());
            moverFalso(copia, copiaInicial, copiaFinal);
            boolean ret = comprobarJaque(copia);
            moverFalso(copia, copiaFinal, copiaInicial);
            //copia[quieroMover.getFila()][quieroMover.getColumna()].setPieza(quieroMover.getPieza());
            //copia[quieroMover.getFila()][quieroMover.getColumna()].setImageResource(quieroMover.getPieza().getDrawable());
               Log.i("salvajaque: comiendo dama", "pieza inicial es : " + cInicial.getPieza().getTag() +
                    cInicial.getFila() + cInicial.getColumna());
            if (ret) {
                //  Log.i("salvaJaque termina:", "el mov " + cInicial.getPieza().getTag() + cInicial.getFila() + cInicial.getColumna() +
                //        " a " + cFinal.getFila() + cFinal.getColumna() + "salva el jaque");
                return false;
            }
            return true;
        }
        return false;
    }

    private void moverFalso(Casilla[][] copia, Casilla cInicial, Casilla cFinal) {
        cInicial.getPieza().setFila(cFinal.getFila());
        cInicial.getPieza().setColumna(cFinal.getColumna());
        //coronación de peones
        if (cInicial.getFila() == 1 &&
                copia[cInicial.getFila()][cInicial.getColumna()].getPieza().getTag().equalsIgnoreCase("PEON") &&
                copia[cInicial.getFila()][cInicial.getColumna()].getPieza().isBlancas()) {
            copia[cFinal.getFila()][cFinal.getColumna()].setPieza(new Dama(cFinal.getFila(), cFinal.getColumna(), true));
            copia[cFinal.getFila()][cFinal.getColumna()].setImageResource(copia[cFinal.getFila()][cFinal.getColumna()].getPieza().getDrawable());
        } else if (cInicial.getFila() == 6 &&
                copia[cInicial.getFila()][cInicial.getColumna()].getPieza().getTag().equalsIgnoreCase("PEON") &&
                !copia[cInicial.getFila()][cInicial.getColumna()].getPieza().isBlancas()) {
            copia[cFinal.getFila()][cFinal.getColumna()].setPieza(new Dama(cFinal.getFila(), cFinal.getColumna(), false));
            copia[cFinal.getFila()][cFinal.getColumna()].setImageResource(copia[cFinal.getFila()][cFinal.getColumna()].getPieza().getDrawable());
        } else {
            //no coronan
            copia[cFinal.getFila()][cFinal.getColumna()].setPieza(cInicial.getPieza());
            copia[cFinal.getFila()][cFinal.getColumna()].setImageResource(cInicial.getPieza().getDrawable());
        }
        copia[cInicial.getFila()][cInicial.getColumna()].setPieza(null);
        copia[cInicial.getFila()][cInicial.getColumna()].setImageResource(0);
    }

    private boolean esValido(Casilla[][] casillas, Casilla cInicial, Casilla cFinal) {
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
                return esValidoTorre(casillas, cInicial, cFinal);
            case "ALFIL":
                return esValidoAlfil(casillas, cInicial, cFinal);
            case "DAMA":
                return esValidoAlfil(casillas, cInicial, cFinal) || esValidoTorre(casillas, cInicial, cFinal);
        }
        return false;
    }

    private boolean esValidoRey(Casilla cInicial, Casilla cFinal) {
        return Math.abs(cInicial.getFila() - cFinal.getFila()) < 2 &&
                Math.abs(cInicial.getColumna() - cFinal.getColumna()) < 2;
    }

    private boolean esValidoCaballo(Casilla cInicial, Casilla cFinal) {
        if (Math.abs(cInicial.getFila() - cFinal.getFila()) == 2 &&
                Math.abs(cInicial.getColumna() - cFinal.getColumna()) == 1) {
            return true;
        }
        return Math.abs(cInicial.getFila() - cFinal.getFila()) == 1 &&
                Math.abs(cInicial.getColumna() - cFinal.getColumna()) == 2;
    }

    private boolean esValidoAlfil(Casilla[][] casillas, Casilla cInicial, Casilla cFinal) {
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
            if (arriba && izquierda && casillas[cInicial.getFila() - i][cInicial.getColumna() - i].getPieza() != null) {
                return false;
            }
            //si estás pasando por encima de una pieza... arriba derecha
            if (arriba && !izquierda && casillas[cInicial.getFila() - i][cInicial.getColumna() + i].getPieza() != null) {
                return false;
            }
            //si estás pasando por encima de una pieza... abajo izquierda
            if (!arriba && izquierda && casillas[cInicial.getFila() + i][cInicial.getColumna() - i].getPieza() != null) {
                return false;
            }
            //si estás pasando por encima de una pieza... abajo derecha
            if (!arriba && !izquierda && casillas[cInicial.getFila() + i][cInicial.getColumna() + i].getPieza() != null) {
                return false;
            }
        }

        return true;
    }

    private boolean esValidoTorre(Casilla[][] casillas, Casilla cInicial, Casilla cFinal) {
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
                if (arriba && casillas[cInicial.getFila() - i][cInicial.getColumna()].getPieza() != null) {
                    return false;
                }
                if (!arriba && casillas[cInicial.getFila() + i][cInicial.getColumna()].getPieza() != null) {
                    return false;
                }
            }
        }
        //movimientos horizontales
        else if (disFila < disColumna) {
            boolean izquierda = cInicial.getColumna() > cFinal.getColumna();
            for (int i = 1; i < disColumna; i++) {
                //si estás pasando por encima de una pieza...
                if (izquierda && casillas[cInicial.getFila()][cInicial.getColumna() - i].getPieza() != null) {
                    return false;
                }
                if (!izquierda && casillas[cInicial.getFila()][cInicial.getColumna() + i].getPieza() != null) {
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

    private boolean esValidoPeon(Casilla cInicial, Casilla cFinal) {

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

    private void mover(Casilla[][] casillas, Casilla cInicial, Casilla cFinal) {
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


    private void addListeners() {
        pintarFondo();
        for (int i = 0; i < NUM_FILAS; i++) {
            for (int j = 0; j < NUM_COLUMNAS; j++) {
                casillas[i][j].setOnClickListener(this);
            }
        }
    }

    public class Tablero extends AsyncTask<Void, Void, Void> {

        ViewTreeObserver.OnGlobalLayoutListener tablero;

        @Override
        protected Void doInBackground(Void... voids) {
            tablero = pintarTablero();

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            addListeners();
            oGameBoard.getViewTreeObserver().addOnGlobalLayoutListener(tablero);
        }

        ViewTreeObserver.OnGlobalLayoutListener pintarTablero() {

            return new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    int width = DosJugadoresActivity.this.oGameBoardShell.getMeasuredWidth();
                    int height = DosJugadoresActivity.this.oGameBoardShell.getMeasuredHeight();
                    double sizeA = (width / NUM_COLUMNAS);
                    double sizeB = (height / NUM_FILAS);

                    double smallestSize = Math.min(sizeA, sizeB);
                    int smallestSizeInt = (int) Math.floor(smallestSize);

                    for (int i = 0; i < NUM_FILAS; i++) {
                        for (int j = 0; j < NUM_COLUMNAS; j++) {
                            try {
                                Casilla b = casillas[i][j];
                                b.setPadding(0, 0, 0, 0);

                                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                                lp.width = smallestSizeInt;
                                lp.height = smallestSizeInt;
                                lp.leftMargin = 0;
                                lp.rightMargin = 0;
                                lp.topMargin = 0;
                                lp.bottomMargin = 0;
                                b.setLayoutParams(lp);

                                oGameBoard.addView(b);
                                oGameBoard.getLayoutParams().width = smallestSizeInt * NUM_COLUMNAS;
                                oGameBoard.getLayoutParams().height = smallestSizeInt * NUM_FILAS;

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        DosJugadoresActivity.this.oGameBoard.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        DosJugadoresActivity.this.oGameBoard.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            };
        }
    }

    private void pintarFondo() {
        boolean cambiar = false;
        int x = 0;
        for (int i = 0; i < NUM_FILAS; i++) {
            for (int j = 0; j < NUM_COLUMNAS; j++) {
                if (x % 8 == 0) {
                    cambiar = !cambiar;
                }
                if ((x % 2 == 0 && !cambiar) || x % 2 != 0 && cambiar) {
                    //casillas negras
                    casillas[i][j].setBackgroundColor(Color.parseColor("#A4552A"));
                } else {

                    //casillas blancas
                    casillas[i][j].setBackgroundColor(Color.parseColor("#F7FCFB"));
                }
                x++;
            }
        }
    }

    public void crearCasillas() {
        boolean cambiar = false;
        int x = 0;
        for (int i = 0; i < NUM_FILAS; i++) {
            for (int j = 0; j < NUM_COLUMNAS; j++) {

                if (x % 8 == 0) {
                    cambiar = !cambiar;
                }
                Casilla b = new Casilla(DosJugadoresActivity.this, i, j);
                b.setClickable(true);
                //piezas negras
                if (x == 0 || x == 7) {
                    b.setPieza(new Torre(i, j, false));
                    b.setImageResource(b.getPieza().getDrawable());
                }
                if (x == 1 || x == 6) {
                    b.setPieza(new Caballo(i, j, false));
                    b.setImageResource(b.getPieza().getDrawable());
                }
                if (x == 2 || x == 5) {
                    b.setPieza(new Alfil(i, j, false));
                    b.setImageResource(b.getPieza().getDrawable());
                }
                if (x == 3) {
                    b.setPieza(new Dama(i, j, false));
                    b.setImageResource(b.getPieza().getDrawable());
                }
                if (x == 4) {
                    b.setPieza(new Rey(i, j, false));
                    b.setImageResource(b.getPieza().getDrawable());
                }
                if (x > 7 && x < 16) {
                    b.setPieza(new Peon(i, j, false));
                    b.setImageResource(b.getPieza().getDrawable());
                }

                //piezas blancas
                if (x == 56 || x == 63) {
                    b.setPieza(new Torre(i, j, true));
                    b.setImageResource(b.getPieza().getDrawable());
                }
                if (x == 57 || x == 62) {
                    b.setPieza(new Caballo(i, j, true));
                    b.setImageResource(b.getPieza().getDrawable());
                }
                if (x == 58 || x == 61) {
                    b.setPieza(new Alfil(i, j, true));
                    b.setImageResource(b.getPieza().getDrawable());
                }
                if (x == 59) {
                    b.setPieza(new Dama(i, j, true));
                    b.setImageResource(b.getPieza().getDrawable());
                }
                if (x == 60) {
                    b.setPieza(new Rey(i, j, true));
                    b.setImageResource(b.getPieza().getDrawable());
                }
                if (x > 47 && x < 56) {
                    b.setPieza(new Peon(i, j, true));
                    b.setImageResource(b.getPieza().getDrawable());
                }
                b.setPadding(0, 0, 0, 0);

                casillas[i][j] = b;
                x++;
            }
        }
    }
    /*
    private ArrayList<String> cargarMovimientos(Casilla[][] tablero, Pieza p) {
        ArrayList<String> movs = new ArrayList<>();
        if (p.getTag().equalsIgnoreCase("PEON")) {
            movs = cargarMovsPeon(tablero, p);
        }
        if (p.getTag().equalsIgnoreCase("REY")) {
            movs = cargarMovsRey(tablero, p);
        }

        for (String s : movs) {
            Log.i("******** Movs: ", s);
        }
        return movs;
    }

    private ArrayList<String> cargarMovsRey(Casilla[][] tablero, Pieza p) {
        Log.i("******** Cargo movs: ", p.getTag() + " " + p.isBlancas() + " " + p.getFila() + " " + p.getColumna());
        //código sin optimimizar, se puede mejorar
        String s = "";
        ArrayList<String> movs = new ArrayList<>();

        if (p.getFila() == 0){
            if (tablero[p.getFila() + 1][p.getColumna()].getPieza() == null ||
                    (tablero[p.getFila() + 1][p.getColumna()].getPieza() != null &&
                            tablero[p.getFila()][p.getColumna()].getPieza().isBlancas() !=
                                    tablero[p.getFila() + 1][p.getColumna()].getPieza().isBlancas())) {
                s = String.valueOf(p.getFila() + 1);
                s += String.valueOf(p.getColumna());
                movs.add(s);
            }
            s = String.valueOf(p.getFila() + 1);
            s += String.valueOf(p.getColumna());
            movs.add(s);
        }



        if (p.getFila() > 0 && p.getColumna() > 0 && tablero[p.getFila() - 1][p.getColumna() - 1].getPieza() == null ||
                (tablero[p.getFila() - 1][p.getColumna() - 1].getPieza() != null &&
                        tablero[p.getFila()][p.getColumna()].getPieza().isBlancas() !=
                                tablero[p.getFila() - 1][p.getColumna() - 1].getPieza().isBlancas())) {
            s = String.valueOf(p.getFila() - 1);
            s += String.valueOf(p.getColumna() - 1);
            movs.add(s);
        }
        if (tablero[p.getFila() - 1][p.getColumna()].getPieza() == null ||
                (tablero[p.getFila() - 1][p.getColumna()].getPieza() != null &&
                        tablero[p.getFila()][p.getColumna()].getPieza().isBlancas() !=
                                tablero[p.getFila() - 1][p.getColumna()].getPieza().isBlancas())) {
            s = String.valueOf(p.getFila() - 1);
            s += String.valueOf(p.getColumna());
            movs.add(s);
        }
        if (tablero[p.getFila() - 1][p.getColumna() + 1].getPieza() == null ||
                (tablero[p.getFila() - 1][p.getColumna() + 1].getPieza() != null &&
                        tablero[p.getFila()][p.getColumna()].getPieza().isBlancas() !=
                                tablero[p.getFila() - 1][p.getColumna() + 1].getPieza().isBlancas())) {
            s = String.valueOf(p.getFila() - 1);
            s += String.valueOf(p.getColumna() + 1);
            movs.add(s);
        }
        if (tablero[p.getFila()][p.getColumna() - 1].getPieza() == null ||
                (tablero[p.getFila()][p.getColumna() - 1].getPieza() != null &&
                        tablero[p.getFila()][p.getColumna()].getPieza().isBlancas() !=
                                tablero[p.getFila()][p.getColumna() - 1].getPieza().isBlancas())) {
            s = String.valueOf(p.getFila());
            s += String.valueOf(p.getColumna() - 1);
            movs.add(s);
        }
        if (tablero[p.getFila()][p.getColumna() + 1].getPieza() == null ||
                (tablero[p.getFila()][p.getColumna() + 1].getPieza() != null &&
                        tablero[p.getFila()][p.getColumna()].getPieza().isBlancas() !=
                                tablero[p.getFila()][p.getColumna() + 1].getPieza().isBlancas())) {
            s = String.valueOf(p.getFila());
            s += String.valueOf(p.getColumna() + 1);
            movs.add(s);
        }
        if (tablero[p.getFila() + 1][p.getColumna()-1].getPieza() == null ||
                (tablero[p.getFila() + 1][p.getColumna()-1].getPieza() != null &&
                        tablero[p.getFila()][p.getColumna()].getPieza().isBlancas() !=
                                tablero[p.getFila() + 1][p.getColumna()-1].getPieza().isBlancas())) {
            s = String.valueOf(p.getFila() + 1);
            s += String.valueOf(p.getColumna() - 1);
            movs.add(s);
        }
        if (tablero[p.getFila() + 1][p.getColumna()].getPieza() == null ||
                (tablero[p.getFila() + 1][p.getColumna()].getPieza() != null &&
                        tablero[p.getFila()][p.getColumna()].getPieza().isBlancas() !=
                                tablero[p.getFila() + 1][p.getColumna()].getPieza().isBlancas())) {
            s = String.valueOf(p.getFila() + 1);
            s += String.valueOf(p.getColumna());
            movs.add(s);
        }
        if (tablero[p.getFila() + 1][p.getColumna()+1].getPieza() == null ||
                (tablero[p.getFila() + 1][p.getColumna()+1].getPieza() != null &&
                        tablero[p.getFila()][p.getColumna()].getPieza().isBlancas() !=
                                tablero[p.getFila() + 1][p.getColumna()+1].getPieza().isBlancas())) {
            s = String.valueOf(p.getFila() + 1);
            s += String.valueOf(p.getColumna() + 1);
            movs.add(s);
        }
        for (String st : movs){
            Log.i("******** Movs antes son: ", st);
        }
        for (String st : movs){
            if(Integer.parseInt(st.substring(0,1)) < 0 || Integer.parseInt(st.substring(0,1)) > 7 ||
                    Integer.parseInt(st.substring(1,2)) < 0 || Integer.parseInt(st.substring(1,2)) > 7){
                movs.remove(st);
            }
        }

        for (String st : movs){
            Log.i("******** Movs desp son: ", st);
        }
        return movs;
    }

    private ArrayList<String> cargarMovsPeon(Casilla[][] tablero, Pieza p) {
        //código sin optimimizar, se puede mejorar
        String s = "";
        ArrayList<String> movs = new ArrayList<>();
        if (p.isBlancas()) {
            if (p.getColumna() > 0 && tablero[p.getFila() - 1][p.getColumna() - 1].getPieza() != null &&
                    tablero[p.getFila()][p.getColumna()].getPieza().isBlancas()
                            != tablero[p.getFila() - 1][p.getColumna() - 1].getPieza().isBlancas()) {
                //y no está clavado... (FALTA)

                s = String.valueOf(p.getFila() - 1);
                s += String.valueOf(p.getColumna() - 1);
                movs.add(s);
            }
            if (p.getColumna() < 7 && tablero[p.getFila() - 1][p.getColumna() + 1].getPieza() != null &&
                    tablero[p.getFila()][p.getColumna()].getPieza().isBlancas()
                            != tablero[p.getFila() - 1][p.getColumna() + 1].getPieza().isBlancas()) {
                s = String.valueOf(p.getFila() - 1);
                s += String.valueOf(p.getColumna() + 1);
                movs.add(s);
            }
            if (tablero[p.getFila() - 1][p.getColumna()].getPieza() != null) {
                return movs;
            }
            if (p.getFila() == 6) {
                s = String.valueOf(p.getFila() - 2);
                s += String.valueOf(p.getColumna());
                movs.add(s);
            }
            s = String.valueOf(p.getFila() - 1);
            s += String.valueOf(p.getColumna());
            movs.add(s);
            return movs;
        } else {
            if (p.getColumna() > 0 && tablero[p.getFila() + 1][p.getColumna() - 1].getPieza() != null &&
                    (tablero[p.getFila()][p.getColumna()].getPieza().isBlancas()
                            != tablero[p.getFila() + 1][p.getColumna() - 1].getPieza().isBlancas())) {
                //y no está clavado... (FALTA)
                s = String.valueOf(p.getFila() + 1);
                s += String.valueOf(p.getColumna() - 1);
                movs.add(s);
            }
            if (p.getColumna() < 7 && tablero[p.getFila() + 1][p.getColumna() + 1].getPieza() != null &&
                    tablero[p.getFila()][p.getColumna()].getPieza().isBlancas()
                            != tablero[p.getFila() + 1][p.getColumna() + 1].getPieza().isBlancas()) {
                s = String.valueOf(p.getFila() + 1);
                s += String.valueOf(p.getColumna() + 1);
                movs.add(s);
            }
            if (tablero[p.getFila() + 1][p.getColumna()].getPieza() != null) {
                return movs;
            }
            if (p.getFila() == 1) {
                s = String.valueOf(p.getFila() + 2);
                s += String.valueOf(p.getColumna());
                movs.add(s);
            }
            s = String.valueOf(p.getFila() + 1);
            s += String.valueOf(p.getColumna());
            movs.add(s);
            return movs;
        }
    }*/
}