package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class DosJugadoresActivity extends AppCompatActivity {
    private static final int NUM_FILAS = 8;
    private static final int NUM_COLUMNAS = 8;
    private GridLayout oGameBoard;
    private LinearLayout oGameBoardShell;
    private ArrayList<ImageButton> casillas;
    private int[][] tablero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dos_jugadores);
        casillas = new ArrayList<>();
        tablero = new int[NUM_FILAS][NUM_COLUMNAS];
        this.oGameBoardShell = (LinearLayout) this.findViewById(R.id.shellGameBoard);
        this.oGameBoard = (GridLayout) this.findViewById(R.id.gridGameBoard);
        this.oGameBoard.getViewTreeObserver().addOnGlobalLayoutListener(this.SquareIfy());
        crearTablero();
        mostrarTablero();
    }

    ViewTreeObserver.OnGlobalLayoutListener SquareIfy() {
        return new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                int width = DosJugadoresActivity.this.oGameBoardShell.getMeasuredWidth();
                int height = DosJugadoresActivity.this.oGameBoardShell.getMeasuredHeight();
                int tileCount = NUM_COLUMNAS * NUM_FILAS;

                double sizeA = (width / NUM_COLUMNAS);
                double sizeB = (height / NUM_FILAS);

                double smallestSize = Math.min(sizeA, sizeB);
                int smallestSizeInt = (int) Math.floor(smallestSize);
                boolean cambiar = false;
                for (int x = 0; x <= tileCount - 1; x++) {
                    try {
                        if (x % 8 == 0) {
                            cambiar = !cambiar;
                        }
                        ImageButton b = new ImageButton(DosJugadoresActivity.this);
                        if ((x % 2 == 0 && !cambiar) || x % 2 != 0 && cambiar) {
                            //casillas negras
                            b.setBackgroundColor(Color.parseColor("#A4552A"));
                        } else {
                            //casillas blancas
                            b.setBackgroundColor(Color.parseColor("#F7FCFB"));
                        }
                        //piezas negras
                        if (x == 0 || x == 7) {
                            b.setImageResource(R.drawable.ntorre);
                        }
                        if (x == 1 || x == 6) {
                            b.setImageResource(R.drawable.ncaballo);
                        }
                        if (x == 2 || x == 5) {
                            b.setImageResource(R.drawable.nalfil);
                        }
                        if (x == 3) {
                            b.setImageResource(R.drawable.ndama);
                        }
                        if (x == 4) {
                            b.setImageResource(R.drawable.nrey);
                        }
                        if (x > 7 && x < 16) {
                            b.setImageResource(R.drawable.npeon);
                        }

                        //piezas blancas
                        if (x == 56 || x == 63) {
                            b.setImageResource(R.drawable.btorre);
                        }
                        if (x == 57 || x == 62) {
                            b.setImageResource(R.drawable.bcaballo);
                        }
                        if (x == 58 || x == 61) {
                            b.setImageResource(R.drawable.balfil);
                        }
                        if (x == 59) {
                            b.setImageResource(R.drawable.bdama);
                        }
                        if (x == 60) {
                            b.setImageResource(R.drawable.brey);
                        }
                        if (x > 47 && x < 56) {
                            b.setImageResource(R.drawable.bpeon);
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
                        casillas.add(b);

                        oGameBoard.addView(b);
                        oGameBoard.getLayoutParams().width = smallestSizeInt * NUM_COLUMNAS;
                        oGameBoard.getLayoutParams().height = smallestSizeInt * NUM_FILAS;
                    } catch (Exception ex) {
                        ex.printStackTrace();
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
    private void crearTablero() {
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
}