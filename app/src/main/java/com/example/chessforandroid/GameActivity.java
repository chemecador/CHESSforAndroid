package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

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
    private TextView vs;
    public StringBuilder movs;

    //tablero
    public final int NUM_FILAS = 8;
    public final int NUM_COLUMNAS = 8;
    private boolean haySeleccionada;
    private boolean fin;
    private Casilla quieroMover;
    private int[][] tablero;

    //elementos de juego
    private int nMovs;
    private String token;
    private boolean soyBlancas;
    private boolean miTurno;
    private Cliente cliente;
    private Juez juez;
    private Object[] o = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        haySeleccionada = false;
        nMovs = 0;
        juez = new Juez();

        setContentView(R.layout.activity_game);
        crearCasillas();

        this.oGameBoardShell = (LinearLayout) this.findViewById(R.id.shellGameBoardLocal);
        this.oGameBoard = (GridLayout) this.findViewById(R.id.gridGameBoardLocal);
        Tablero t = new Tablero();
        t.execute();
        Intent intent = getIntent();
        token = intent.getStringExtra("token");

        Log.i("***", "comencemos, " + token);

        cliente = new Cliente();

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

        Casilla cas = (Casilla) view;

        if (!miTurno)
            Toast.makeText(this, tvMovs.getText().toString(), Toast.LENGTH_SHORT).show();

        if (!haySeleccionada) {
            if (cas.getPieza() == null)
                return;
            if (cas.getPieza().isBlancas() != soyBlancas)
                return;

            cas.setBackgroundColor(Color.parseColor("#36E0FA"));
            quieroMover = cas;
            quieroMover.setPieza(cas.getPieza());
        } else {

            pintarFondo();
            if (juez.esValido(juez.casillas, quieroMover, cas)) {

                juez.captura = cas.getPieza() != null;
                juez.mover(quieroMover, cas);

                if (cliente.isConectado()) {
                    o = cliente.enviarMov(this,
                            juez.casillasToString());
                } else {
                    Log.e("************************", "error");
                }

                //actualizarTxt(cas.getFila(), cas.getColumna());
                miTurno = false;
                //ESPERAR A MI TURNO
            }
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
        vs = findViewById(R.id.txtVs);
        tvMovs = (TextView) findViewById(R.id.txtMovsLocal);
        tvMovs.setMovementMethod(new ScrollingMovementMethod());

        Object[] o = null;
        if (cliente.isConectado()) {
            o = cliente.getDatosIniciales(this, token);
        } else {
            Log.e("************************", "error");
        }

        tablas = findViewById(R.id.bTablasLocal);
        rendirse = findViewById(R.id.bRendirseLocal);
        tablas.setOnClickListener(this);
        rendirse.setOnClickListener(this);
        for (int i = 0; i < NUM_FILAS; i++) {
            for (int j = 0; j < NUM_COLUMNAS; j++) {
                juez.casillas[i][j].setOnClickListener(this);
            }
        }
        soyBlancas = (boolean) o[2];
        miTurno = soyBlancas;
        if (soyBlancas)
            vs.setText("(BLANCAS) " + o[0] + " vs " + o[1] + " (NEGRAS)");
        else

            vs.setText("(BLANCAS) " + o[1] + " vs " + o[0] + " (NEGRAS)");

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
                                Casilla b = juez.casillas[i][j];
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
                    //juez.casillas negras
                    juez.casillas[i][j].setBackgroundColor(Color.parseColor("#A4552A"));
                } else {

                    //juez.casillas blancas
                    juez.casillas[i][j].setBackgroundColor(Color.parseColor("#DDDDDD"));
                }
                x++;
            }
        }
    }

    public void crearCasillas() {
        boolean cambiar = false;
        int x = 0;
        for (int i = 0; i < juez.NUM_FILAS; i++) {
            for (int j = 0; j < juez.NUM_COLUMNAS; j++) {

                if (x % 8 == 0) {
                    cambiar = !cambiar;
                }
                Casilla b = new Casilla(this, i, j);
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

                juez.casillas[i][j] = b;
                x++;
            }
        }
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

        tablero = new int[NUM_FILAS][NUM_COLUMNAS];
        for (int i = 0; i < NUM_FILAS; i++) {
            for (int j = 0; j < NUM_COLUMNAS; j++) {
                //colocar juez.casillas en blanco
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

    public static void mostrarTablero(int[][] tablero) {
        StringBuilder t = new StringBuilder();
        for (int i = 0; i < tablero.length; i++) {
            for (int j = 0; j < tablero[0].length; j++) {
                t.append(tablero[i][j]).append(" ");
            }
            t.append("\n");
        }
        Log.i("Tablero: ", "\n" + t);
    }

}