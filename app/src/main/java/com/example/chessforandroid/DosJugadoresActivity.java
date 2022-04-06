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
    private int[][] tablero;
    private boolean piezaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        piezaSeleccionada = false;
        setContentView(R.layout.activity_dos_jugadores);
        Tablero t = new Tablero();
        t.execute();
        this.casillas = new Casilla[NUM_FILAS][NUM_COLUMNAS];
        this.tablero = new int[NUM_FILAS][NUM_COLUMNAS];
        this.oGameBoardShell = (LinearLayout) this.findViewById(R.id.shellGameBoard);
        this.oGameBoard = (GridLayout) this.findViewById(R.id.gridGameBoard);
        this.oGameBoard.getViewTreeObserver().addOnGlobalLayoutListener(t.pintarTablero());

    }


    @Override
    public void onClick(View view) {

        Casilla c = (Casilla) view;

        piezaSeleccionada = !piezaSeleccionada;

        if(piezaSeleccionada){
            Toast.makeText(this, "Está seleccionada la pieza:\n" +
                    "Fila " + c.getFila() + " Columna " + c.getColumna(), Toast.LENGTH_SHORT).show();
            c.setBackgroundColor(Color.parseColor("#36E0FA"));

        }
        else{
            pintarFondo();
            Toast.makeText(this, "No hay pieza seleccionada", Toast.LENGTH_SHORT).show();
        }
    }

    private void addListeners() {
        pintarFondo();
        Log.i("**", "Entro al listener");
        int p = 0;
        for (int i = 0; i < NUM_FILAS; i++) {
            for (int j = 0; j < NUM_COLUMNAS; j++) {
                casillas[i][j].setOnClickListener(this);
                Log.i("**", "Casilla numero " + p);
                p++;
            }
        }
    }

    public class Tablero extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            pintarTablero();
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            addListeners();
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
                    boolean cambiar = false;
                    int x = 0;
                    for (int i = 0; i < NUM_FILAS; i++) {
                        for (int j = 0; j < NUM_COLUMNAS; j++) {
                            try {
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

                                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                                lp.width = smallestSizeInt;
                                lp.height = smallestSizeInt;
                                lp.leftMargin = 0;
                                lp.rightMargin = 0;
                                lp.topMargin = 0;
                                lp.bottomMargin = 0;
                                b.setLayoutParams(lp);
                                casillas[i][j] = b;
                                oGameBoard.addView(b);
                                oGameBoard.getLayoutParams().width = smallestSizeInt * NUM_COLUMNAS;
                                oGameBoard.getLayoutParams().height = smallestSizeInt * NUM_FILAS;
                                x++;
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