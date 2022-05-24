package com.example.chessforandroid;

import static com.example.chessforandroid.util.Constantes.NUM_COLUMNAS;
import static com.example.chessforandroid.util.Constantes.NUM_FILAS;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chessforandroid.piezas.*;
import com.example.chessforandroid.juego.Casilla;
import com.example.chessforandroid.util.Constantes;
import com.example.chessforandroid.util.Juez;


/**
 * Clase OfflineActivity. Gestiona la partida local entre dos jugadores.
 */
public class OfflineActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = OfflineActivity.class.getSimpleName();

    // atributos de layout
    private GridLayout oGameBoard;
    private LinearLayout oGameBoardShell;

    // atributos de interfaz
    public static TextView tvMovs;
    private TextView mueven;
    public static StringBuilder movs;

    // atributos de la partida
    private boolean haySeleccionada;
    private boolean fin;
    private Casilla quieroMover;
    public static String tag;
    private int nMovs;
    private Juez juez;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        juez = new Juez();
        haySeleccionada = false;
        nMovs = 0;


        setContentView(R.layout.activity_offline);
        crearCasillas();

        this.oGameBoardShell = this.findViewById(R.id.shellGameBoardOffline);
        this.oGameBoard = this.findViewById(R.id.gridGameBoardOffline);
        Tablero t = new Tablero();
        t.execute();


    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {

        if (fin)
            return;

        if (view.getId() == R.id.bTablasOffline) {

            // se lanza un AlertDialog mostrando el resultado de tablas
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.draw_accepted);
            builder.setPositiveButton(R.string.accept, null);
            builder.setNegativeButton(R.string.exit, (dialogInterface, i) -> {
                finish();
            });
            Dialog dialog = builder.create();
            dialog.show();
            fin = true;
            return;
        }

        if (view.getId() == R.id.bRendirseOffline) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // gana el jugador que no era su turno
            if (juez.turno) {
                builder.setMessage(R.string.black_win);
            } else {
                builder.setMessage(R.string.white_win);
            }
            builder.setNegativeButton(R.string.exit, (dialogInterface, i) -> {
                finish();
            });
            builder.setPositiveButton(R.string.accept, null);
            Dialog dialog = builder.create();
            dialog.show();
            fin = true;
            return;
        }

        // la casilla que se acaba de tocar
        Casilla casSelec = (Casilla) view;

        if (!haySeleccionada) {
            // si no hay ninguna casilla seleccionada...

            if (casSelec.getPieza() == null) {
                // si la casilla seleccionada no contiene ninguna pieza, no devuelve nada
                return;
            }


            if (casSelec.getPieza().isBlancas() != juez.turno) {
                // si contiene una pieza pero es del rival, se notifica y no devuelve nada
                if (juez.turno)
                    Toast.makeText(this, R.string.white_move, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, R.string.black_move, Toast.LENGTH_SHORT).show();
                return;
            }

            // se colorea la casilla seleccionada para identificarla mejor
            casSelec.setBackgroundColor(Color.parseColor("#459CCD"));

            // se guarda la casilla en una variable
            quieroMover = casSelec;
            quieroMover.setPieza(casSelec.getPieza());
        } else {
            // si ya habia casilla seleccionada...

            // se pinta el fondo para borrar la casilla coloreada
            pintarFondo();

            // se comprueba si el movimiento es valido
            if (juez.esValido(juez.casillas, quieroMover, casSelec)) {

                if (juez.turno) {
                    if (nMovs % 3 == 0) {
                        // cada 3 movimientos de los dos jugadores, se produce un salto de linea
                        movs.append("\n");
                    }
                    nMovs++;
                }

                // se guarda en una variable si se va a producir una captura
                juez.captura = casSelec.getPieza() != null;

                // se guarda el nombre de la pieza que se quiere mover
                tag = quieroMover.getPieza().getTag();

                // se realiza el movmimiento
                juez.mover(quieroMover, casSelec);

                // se iluminan las dos casillas implicadas en el movimiento para una mayor claridad
                quieroMover.setBackgroundColor(Color.parseColor("#AECDDF"));
                casSelec.setBackgroundColor(Color.parseColor("#8DC5E5"));

                // se cambia el turno
                juez.turno = !juez.turno;

                // si se han comido el rey, (jaque mate)...
                if (juez.buscarRey(juez.casillas, juez.turno) == null) {
                    // se notifica quien ha ganado

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    // gana el jugador que no era su turno
                    if (juez.turno) {
                        builder.setMessage(R.string.black_win);
                    } else {
                        builder.setMessage(R.string.white_win);
                    }
                    builder.setNegativeButton(R.string.exit, (dialogInterface, i) -> {
                        finish();
                    });
                    builder.setPositiveButton(R.string.accept, null);
                    Dialog dialog = builder.create();
                    dialog.show();
                    fin = true;
                    return;
                }

                // se guarda en una variable si hay movimiento posible
                juez.puedeMover = juez.puedeMover(juez.casillas, juez.turno);

                // se guarda en una variable si está en jaque
                juez.jaque = juez.comprobarJaque(juez.casillas);

                // se actualizan los movimientos
                actualizarTxt(casSelec.getFila(), casSelec.getColumna());

                if (juez.jaque) {
                    // si hay jaque, se ilumina la casilla y se muestra por pantalla
                    Toast.makeText(this, R.string.check, Toast.LENGTH_SHORT).show();
                    juez.buscarRey(juez.casillas, juez.turno).setBackgroundColor(Color.RED);
                } else {
                    if (!juez.puedeMover) {
                        // no hay jaque y no puede mover, rey ahogado

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle(R.string.draw);
                        builder.setMessage(R.string.stalemate);
                        builder.setPositiveButton(R.string.accept, null);
                        builder.setNegativeButton(R.string.exit, (dialogInterface, i) -> {
                            finish();
                        });
                        Dialog dialog = builder.create();
                        dialog.show();
                        fin = true;
                        return;
                    }
                }
            } else {
                Toast.makeText(this, R.string.not_valid, Toast.LENGTH_SHORT).show();
            }
            // se notifica el cambio de turno
            if (juez.turno)
                mueven.setText(R.string.white_move);
            else
                mueven.setText(R.string.black_move);

        }
        haySeleccionada = !haySeleccionada;
    }

    /**
     * Metodo que actualiza el texto con los movimientos
     *
     * @param fila Fila del movimiento
     * @param col  Columna del movimiento
     */
    private void actualizarTxt(int fila, int col) {

        char[] cs = juez.coorToChar(fila, col);
        char c1 = cs[0];
        char c2 = cs[1];
        movs.append("   ").append(nMovs).append(". ");
        switch (tag) {
            case "REY":
                if ((c1 == 'c' && c2 == '1') || c1 == 'c' && c2 == '8') {
                    movs.append("O-O-O");
                    return;
                }
                if ((c1 == 'g' && c2 == '1') || c1 == 'g' && c2 == '8') {
                    movs.append("O-O");
                    return;
                }
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
        if (juez.captura)
            movs.append("x");

        movs.append(c1).append(c2);
        if (juez.jaque)
            movs.append("+");

        //autoscroll hacia abajo cuando sea necesario
        final int scrollAmount = tvMovs.getLayout().getLineTop
                (tvMovs.getLineCount()) - tvMovs.getHeight();
        tvMovs.scrollTo(0, Math.max(scrollAmount, 0));
        tvMovs.setText(movs);
    }

    /**
     * Metodo que ajusta los listeners
     */
    private void addListeners() {
        pintarFondo();
        movs = new StringBuilder();
        mueven = findViewById(R.id.txtMuevenOffline);
        tvMovs = findViewById(R.id.txtMovsOffline);
        tvMovs.setMovementMethod(new ScrollingMovementMethod());

        Button tablas = findViewById(R.id.bTablasOffline);
        Button rendirse = findViewById(R.id.bRendirseOffline);
        tablas.setOnClickListener(this);
        rendirse.setOnClickListener(this);
        for (int i = 0; i < NUM_FILAS; i++) {
            for (int j = 0; j < NUM_COLUMNAS; j++) {
                juez.casillas[i][j].setOnClickListener(this);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
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

                @SuppressLint("ObsoleteSdkInt")
                @Override
                public void onGlobalLayout() {
                    int width = OfflineActivity.this.oGameBoardShell.getMeasuredWidth();
                    int height = OfflineActivity.this.oGameBoardShell.getMeasuredHeight();
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
                        OfflineActivity.this.oGameBoard.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        OfflineActivity.this.oGameBoard.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            };
        }
    }

    /**
     * Metodo que pinta las casillas blancas y negras
     */
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
                    juez.casillas[i][j].setBackgroundColor(Constantes.COLOR_NEGRAS);
                } else {
                    //casillas blancas
                    juez.casillas[i][j].setBackgroundColor(Constantes.COLOR_BLANCAS);
                }
                x++;
            }
        }
    }

    /**
     * Metodo que crea las casillas
     */
    public void crearCasillas() {
        boolean cambiar = false;
        int x = 0;
        for (int i = 0; i < NUM_FILAS; i++) {
            for (int j = 0; j < NUM_COLUMNAS; j++) {

                if (x % 8 == 0) {
                    cambiar = !cambiar;
                }
                Casilla b = new Casilla(OfflineActivity.this, i, j);
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
}