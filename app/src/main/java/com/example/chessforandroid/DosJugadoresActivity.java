package com.example.chessforandroid;

import static com.example.chessforandroid.Juez.NUM_COLUMNAS;
import static com.example.chessforandroid.Juez.NUM_FILAS;
import static com.example.chessforandroid.Juez.casillas;
import static com.example.chessforandroid.Juez.esLegal;
import static com.example.chessforandroid.Juez.jaque;
import static com.example.chessforandroid.Juez.turno;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chessforandroid.piezas.Alfil;
import com.example.chessforandroid.piezas.Caballo;
import com.example.chessforandroid.piezas.Dama;
import com.example.chessforandroid.piezas.Peon;
import com.example.chessforandroid.piezas.Rey;
import com.example.chessforandroid.piezas.Torre;


public class DosJugadoresActivity extends AppCompatActivity implements View.OnClickListener {

    private GridLayout oGameBoard;
    private LinearLayout oGameBoardShell;
    private boolean haySeleccionada;
    private boolean captura;
    private boolean fin;
    private Casilla quieroMover;
    private String tag;
    private int nMovs;
    private Button tablas;
    private Button rendirse;
    private TextView tvMovs;
    private StringBuilder movs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        haySeleccionada = false;
        captura = false;
        nMovs = 0;


        Juez.turno = true;
        Juez.casillas = new Casilla[NUM_FILAS][NUM_COLUMNAS];
        Juez.jaqueMate = false;
        Juez.jaque = false;
        Juez.puedeMover = false;
        setContentView(R.layout.activity_dos_jugadores);
        crearCasillas();

        this.oGameBoardShell = (LinearLayout) this.findViewById(R.id.shellGameBoard);
        this.oGameBoard = (GridLayout) this.findViewById(R.id.gridGameBoard);
        Tablero t = new Tablero();
        t.execute();


    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.bTablas) {

            return;
        }
        if (view.getId() == R.id.bRendirse) {

            return;
        }


        Casilla c = (Casilla) view;

        if (!haySeleccionada) {
            if (c.getPieza() == null)
                return;
            if (c.getPieza().isBlancas() != Juez.turno) {
                if (Juez.turno)
                    Toast.makeText(this, "Juegan las blancas", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Juegan las negras", Toast.LENGTH_SHORT).show();
                return;
            }

            c.setBackgroundColor(Color.parseColor("#36E0FA"));
            quieroMover = c;
            quieroMover.setPieza(c.getPieza());
        } else {
            Log.i("*******************************************************************", "empiezo MOV");
            pintarFondo();
            if (esLegal(quieroMover, c)) {
                if (turno)
                    nMovs++;
                captura = c.getPieza() != null;
                tag = quieroMover.getPieza().getTag();
                Juez.mover(quieroMover, c);
                Juez.turno = !Juez.turno;
                Juez.puedeMover = Juez.puedeMover(casillas, Juez.turno);
                Juez.jaque = Juez.comprobarJaque(casillas);
                actualizarTxt(tag, coorToChar(c.getFila(), c.getColumna())[0],
                        coorToChar(c.getFila(), c.getColumna())[1], captura);
                if (jaque) {
                    Toast.makeText(this, "JAQUE " + Juez.turno, Toast.LENGTH_SHORT).show();

                } else {
                    if (!Juez.puedeMover) {
                        Toast.makeText(this, "REY AHOGADO " + Juez.turno, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            Log.i("*******************************************************************", "ACABO MOV");
        }
        haySeleccionada = !haySeleccionada;
    }

    private char[] coorToChar(int fila, int col) {
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

    private void actualizarTxt(String tag, char c1, char c2, boolean captura) {
        movs.append("   " + nMovs + ". ");
        switch (tag) {
            case "REY":
                movs.append("R");
                break;
            case "DAMA":
                movs.append("D");
                break;
            case "ALFIL":
                movs.append("A");
                break;
            case "CABALLO":
                movs.append("C");
                break;
            case "TORRE":
                movs.append("T");
                break;
        }
        if (captura)
            movs.append("x");

        movs.append("" + c1 + c2);
        Log.i("***", "" + movs);
        if (jaque)
            movs.append("+");
        tvMovs.setText(movs + "  ");

    }


    private void addListeners() {
        pintarFondo();
        movs = new StringBuilder();
        tvMovs = (TextView) findViewById(R.id.txtMovs);
        tablas = findViewById(R.id.bTablas);
        rendirse = findViewById(R.id.bRendirse);
        tablas.setOnClickListener(this);
        rendirse.setOnClickListener(this);
        for (int i = 0; i < Juez.NUM_FILAS; i++) {
            for (int j = 0; j < Juez.NUM_COLUMNAS; j++) {
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
                    double sizeA = (width / Juez.NUM_COLUMNAS);
                    double sizeB = (height / Juez.NUM_FILAS);

                    double smallestSize = Math.min(sizeA, sizeB);
                    int smallestSizeInt = (int) Math.floor(smallestSize);

                    for (int i = 0; i < Juez.NUM_FILAS; i++) {
                        for (int j = 0; j < Juez.NUM_COLUMNAS; j++) {
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
                                oGameBoard.getLayoutParams().width = smallestSizeInt * Juez.NUM_COLUMNAS;
                                oGameBoard.getLayoutParams().height = smallestSizeInt * Juez.NUM_FILAS;

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
        for (int i = 0; i < Juez.NUM_FILAS; i++) {
            for (int j = 0; j < Juez.NUM_COLUMNAS; j++) {
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
        for (int i = 0; i < Juez.NUM_FILAS; i++) {
            for (int j = 0; j < Juez.NUM_COLUMNAS; j++) {

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
}