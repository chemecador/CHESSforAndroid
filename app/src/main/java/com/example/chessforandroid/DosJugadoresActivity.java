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
import com.example.chessforandroid.piezas.Pieza;
import com.example.chessforandroid.piezas.Rey;
import com.example.chessforandroid.piezas.Torre;

import java.util.ArrayList;


public class DosJugadoresActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int NUM_FILAS = 8;
    private static final int NUM_COLUMNAS = 8;
    private GridLayout oGameBoard;
    private LinearLayout oGameBoardShell;
    private Casilla[][] casillas;
    private int[][] tablero;
    private boolean haySeleccionada;
    private Casilla cInicial;
    private ArrayList<String> movimientosPosibles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        haySeleccionada = false;
        setContentView(R.layout.activity_dos_jugadores);

        this.casillas = new Casilla[NUM_FILAS][NUM_COLUMNAS];
        this.movimientosPosibles = new ArrayList<>();
        crearCasillas();

        this.tablero = new int[NUM_FILAS][NUM_COLUMNAS];
        this.oGameBoardShell = (LinearLayout) this.findViewById(R.id.shellGameBoard);
        this.oGameBoard = (GridLayout) this.findViewById(R.id.gridGameBoard);
        Tablero t = new Tablero();
        t.execute();


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

    @Override
    public void onClick(View view) {

        Casilla c = (Casilla) view;

        if (!haySeleccionada) {
            if (c.getPieza() == null)
                return;
            c.setBackgroundColor(Color.parseColor("#36E0FA"));
            cInicial = c;
            this.movimientosPosibles = cargarMovimientos(casillas, cInicial.getPieza());
        } else {
            pintarFondo();
            intentarMover(casillas, cInicial, c, movimientosPosibles);
        }

        haySeleccionada = !haySeleccionada;
    }

    private void intentarMover(Casilla[][] casillas, Casilla cInicial, Casilla cFinal, ArrayList<String> movimientosPosibles) {
        String coorFin = "" + cFinal.getFila() + cFinal.getColumna();
        for (String s : movimientosPosibles) {
            Log.i("********************** Movs posibles: ", s);
        }
        Log.i("**********", "quiero mover a " + coorFin);
        if (movimientosPosibles.contains(coorFin)) {
            Log.i("**********************", "entro");
            Log.i("********************** quiero coronar: ", casillas[cInicial.getFila()][cInicial.getColumna()].getPieza().getTag() + " " +
                    casillas[cInicial.getFila()][cInicial.getColumna()].getPieza().isBlancas() + " " +
                    casillas[cInicial.getFila()][cInicial.getColumna()].getPieza().getFila() + " " +
                    casillas[cInicial.getFila()][cInicial.getColumna()].getPieza().getColumna());

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
    }

    private ArrayList<String> cargarMovimientos(Casilla[][] tablero, Pieza p) {
        ArrayList<String> movs = new ArrayList<>();
        if (p.getTag().equalsIgnoreCase("PEON")) {
            movs = cargarMovsPeon(tablero, p);
        }

        for (String s : movs) {
            Log.i("********************** Movs: ", s);
        }
        return movs;
    }

    private ArrayList<String> cargarMovsPeon(Casilla[][] tablero, Pieza p) {


        Log.i("********************** Cargo movs: ", p.getTag() + " " + p.isBlancas() + " " + p.getFila() + " " + p.getColumna());
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
    }

    private void addListeners() {
        pintarFondo();
        int p = 0;
        for (int i = 0; i < NUM_FILAS; i++) {
            for (int j = 0; j < NUM_COLUMNAS; j++) {
                casillas[i][j].setOnClickListener(this);
                p++;
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
            ViewTreeObserver.OnGlobalLayoutListener tablero =
                    new ViewTreeObserver.OnGlobalLayoutListener() {

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

            return tablero;
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

    private void mostrarTablero() {
        String t = "";
        for (int i = 0; i < NUM_FILAS; i++) {
            for (int j = 0; j < NUM_COLUMNAS; j++) {
                t += tablero[i][j] + " ";
            }
            t += "\n";
        }
        Log.i("Tablero: ", "\n" + t);
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
    private void escribirTablero() {
        for (int i = 0; i < NUM_FILAS; i++) {
            for (int j = 0; j < NUM_COLUMNAS; j++) {
                //colocar casillas en blanco
                tablero[i][j] = 0;
                //colocar peones negros
                if (i == 1) {
                    tablero[i][j] = -6;
                }
                //colocar peones blancos
                if (i == 6) {
                    tablero[i][j] = 6;
                }
            }
        }
        //colocar piezas negras
        tablero[0][0] = -3;
        tablero[0][7] = -3;
        tablero[0][1] = -5;
        tablero[0][6] = -5;
        tablero[0][2] = -4;
        tablero[0][5] = -4;
        tablero[0][3] = -2;
        tablero[0][4] = -1;

        //colocar piezas blancas
        tablero[7][0] = 3;
        tablero[7][7] = 3;
        tablero[7][1] = 5;
        tablero[7][6] = 5;
        tablero[7][2] = 4;
        tablero[7][5] = 4;
        tablero[7][3] = 2;
        tablero[7][4] = 1;
    }
}