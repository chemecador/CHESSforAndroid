package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
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


public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    //layout
    private GridLayout oGameBoard;
    private LinearLayout oGameBoardShell;
    private Button tablas;
    private Button rendirse;
    public TextView tvMovs;
    private TextView mueven;
    public StringBuilder movs;

    //tablero
    public Casilla[][] casillas;
    public final int NUM_FILAS = 8;
    public final int NUM_COLUMNAS = 8;
    private boolean haySeleccionada;
    private boolean fin;
    private Casilla quieroMover;
    public static String tag;

    //elementos de juego
    private int nMovs;
    private String token;
    public String j1;
    public String j2;
    public boolean blancas;
    private Cliente c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        haySeleccionada = false;
        nMovs = 0;

        setContentView(R.layout.activity_game);
        crearCasillas();

        this.oGameBoardShell = (LinearLayout) this.findViewById(R.id.shellGameBoardLocal);
        this.oGameBoard = (GridLayout) this.findViewById(R.id.gridGameBoardLocal);
        Tablero t = new Tablero();
        t.execute();
        Intent intent = getIntent();
        token = intent.getStringExtra("token");

        Log.i("***", "comencemos, " + token);

        c = new Cliente();
        Object[] o = null;
        if (c.isConectado()){
            o = c.getDatosIniciales(this, token);
        } else {
            Log.e ("************************", "error");
        }
        Log.i ("***", "soy el jugador " + o[0] + " - juego contra " + o[1] +
                " - ¿soy blancas?" + o[2]);
    }



    @Override
    public void onClick(View view) {

        if (fin)
            return;

        if (view.getId() == R.id.bTablasLocal) {

            Toast.makeText(this, "Tablas aceptadas", Toast.LENGTH_SHORT).show();
            fin = true;
            return;
        }
        if (view.getId() == R.id.bRendirseLocal) {

            Toast.makeText(this, "Ganan las negras", Toast.LENGTH_SHORT).show();

            fin = true;
            return;
        }


        Casilla c = (Casilla) view;

        if (!haySeleccionada) {

        } else {


        }
        haySeleccionada = !haySeleccionada;
    }

    private void actualizarTxt(int fila, int col) {
        //movs = juez.actualizarTxt(juez.coorToChar(fila, col)[0], juez.coorToChar(fila, col)[1]);

        //autoscroll hacia abajo cuando sea necesario
        final int scrollAmount = tvMovs.getLayout().getLineTop
                (tvMovs.getLineCount()) - tvMovs.getHeight();
        tvMovs.scrollTo(0, Math.max(scrollAmount, 0));
        tvMovs.setText(movs + "  ");
    }


    private void addListeners() {
        pintarFondo();
        movs = new StringBuilder();
        mueven = findViewById(R.id.txtMuevenLocal);
        tvMovs = (TextView) findViewById(R.id.txtMovsLocal);
        tvMovs.setMovementMethod(new ScrollingMovementMethod());

        tablas = findViewById(R.id.bTablasLocal);
        rendirse = findViewById(R.id.bRendirseLocal);
        tablas.setOnClickListener(this);
        rendirse.setOnClickListener(this);
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
                    int width = GameActivity.this.oGameBoardShell.getMeasuredWidth();
                    int height = GameActivity.this.oGameBoardShell.getMeasuredHeight();
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
                        GameActivity.this.oGameBoard.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        GameActivity.this.oGameBoard.getViewTreeObserver().removeOnGlobalLayoutListener(this);
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
                    casillas[i][j].setBackgroundColor(Color.parseColor("#DDDDDD"));
                }
                x++;
            }
        }
    }

    public void crearCasillas() {
        casillas = new Casilla[NUM_FILAS][NUM_COLUMNAS];
        boolean cambiar = false;
        int x = 0;
        for (int i = 0; i < NUM_FILAS; i++) {
            for (int j = 0; j < NUM_COLUMNAS; j++) {

                if (x % 8 == 0) {
                    cambiar = !cambiar;
                }
                Casilla b = new Casilla(GameActivity.this, i, j);
                b.setClickable(true);
                //piezas negras
                if (x == 0 || x == 7) {
                    b.setPieza(new Torre(false));
                    b.setImageResource(b.getPieza().getDrawable());
                }
                if (x == 1 || x == 6) {
                    b.setPieza(new Caballo(false));
                    b.setImageResource(b.getPieza().getDrawable());
                }
                if (x == 2 || x == 5) {
                    b.setPieza(new Alfil(false));
                    b.setImageResource(b.getPieza().getDrawable());
                }
                if (x == 3) {
                    b.setPieza(new Dama(false));
                    b.setImageResource(b.getPieza().getDrawable());
                }
                if (x == 4) {
                    b.setPieza(new Rey(false));
                    b.setImageResource(b.getPieza().getDrawable());
                }
                if (x > 7 && x < 16) {
                    b.setPieza(new Peon(false));
                    b.setImageResource(b.getPieza().getDrawable());
                }

                //piezas blancas
                if (x == 56 || x == 63) {
                    b.setPieza(new Torre(true));
                    b.setImageResource(b.getPieza().getDrawable());
                }
                if (x == 57 || x == 62) {
                    b.setPieza(new Caballo(true));
                    b.setImageResource(b.getPieza().getDrawable());
                }
                if (x == 58 || x == 61) {
                    b.setPieza(new Alfil(true));
                    b.setImageResource(b.getPieza().getDrawable());
                }
                if (x == 59) {
                    b.setPieza(new Dama(true));
                    b.setImageResource(b.getPieza().getDrawable());
                }
                if (x == 60) {
                    b.setPieza(new Rey(true));
                    b.setImageResource(b.getPieza().getDrawable());
                }
                if (x > 47 && x < 56) {
                    b.setPieza(new Peon(true));
                    b.setImageResource(b.getPieza().getDrawable());
                }
                b.setPadding(0, 0, 0, 0);

                casillas[i][j] = b;
                x++;
            }
        }
    }
}