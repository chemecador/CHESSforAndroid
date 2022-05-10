package com.example.chessforandroid;

import static com.example.chessforandroid.util.Constantes.NUM_COLUMNAS;
import static com.example.chessforandroid.util.Constantes.NUM_FILAS;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.chessforandroid.piezas.Alfil;
import com.example.chessforandroid.piezas.Caballo;
import com.example.chessforandroid.piezas.Dama;
import com.example.chessforandroid.piezas.Peon;
import com.example.chessforandroid.piezas.Rey;
import com.example.chessforandroid.piezas.Torre;


public class OfflineActivity extends AppCompatActivity implements View.OnClickListener {

    private GridLayout oGameBoard;
    private LinearLayout oGameBoardShell;
    private boolean haySeleccionada;
    private boolean fin;
    private Casilla quieroMover;
    public static String tag;
    private int nMovs;
    private Button tablas;
    private Button rendirse;
    public static TextView tvMovs;
    private TextView mueven;
    public static StringBuilder movs;
    private Juez juez;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        juez = new Juez();
        haySeleccionada = false;
        nMovs = 0;



        setContentView(R.layout.activity_offline);
        crearCasillas();

        this.oGameBoardShell = (LinearLayout) this.findViewById(R.id.shellGameBoardOffline);
        this.oGameBoard = (GridLayout) this.findViewById(R.id.gridGameBoardOffline);
        Tablero t = new Tablero();
        t.execute();


    }


    @Override
    public void onClick(View view) {

        if (fin)
            return;

        if (view.getId() == R.id.bTablasOffline) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Tablas");
            builder.setMessage("Tablas de mutuo acuerdo");
            builder.setPositiveButton(R.string.accept, null);
            Dialog dialog = builder.create();
            dialog.show();
            fin = true;
            return;
        }
        if (view.getId() == R.id.bRendirseOffline) {

            if (juez.turno) {
                Toast.makeText(this, "Ganan las negras", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Ganan las blancas", Toast.LENGTH_SHORT).show();
            }
            fin = true;
            return;
        }


        Casilla casSelec = (Casilla) view;

        if (!haySeleccionada) {
            if (casSelec.getPieza() == null)
                return;
            if (casSelec.getPieza().isBlancas() != juez.turno) {
                if (juez.turno)
                    Toast.makeText(this, "Mueven las blancas", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Mueven las negras", Toast.LENGTH_SHORT).show();
                return;
            }

            casSelec.setBackgroundColor(Color.parseColor("#36E0FA"));
            quieroMover = casSelec;
            quieroMover.setPieza(casSelec.getPieza());
        } else {
            pintarFondo();
            if (juez.esValido(juez.casillas, quieroMover, casSelec)) {
                if (juez.turno) {
                    if (nMovs % 3 == 0) {
                        movs.append("\n");
                    }
                    nMovs++;
                }
                juez.captura = casSelec.getPieza() != null;
                tag = quieroMover.getPieza().getTag();
                juez.mover(quieroMover, casSelec);
                juez.turno = !juez.turno;
                if (juez.buscarRey(juez.casillas, juez.turno) == null) {
                    if (juez.turno)
                        Toast.makeText(this, "Ganan las negras", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(this, "Ganan las blancas", Toast.LENGTH_SHORT).show();

                    fin = true;
                    return;
                }
                juez.puedeMover = juez.puedeMover(juez.casillas, juez.turno);
                juez.jaque = juez.comprobarJaque(juez.casillas);
                actualizarTxt(casSelec.getFila(), casSelec.getColumna());
                if (juez.jaque) {
                    Toast.makeText(this, "JAQUE", Toast.LENGTH_SHORT).show();
                    juez.buscarRey(juez.casillas, juez.turno).setBackgroundColor(Color.RED);
                } else {
                    if (!juez.puedeMover) {
                        Toast.makeText(this, "REY AHOGADO " + juez.turno, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            if (juez.turno)
                mueven.setText("Mueven las blancas");
            else
                mueven.setText("Mueven las negras");

        }
        haySeleccionada = !haySeleccionada;
    }

    private void actualizarTxt(int fila, int col) {

        char[] cs = juez.coorToChar(fila, col);
        char c1 = cs[0];
        char c2 = cs[1];
        movs.append("   ").append(nMovs).append(". ");
        switch (tag) {
            case "REY":
                if ((c1 == 'c' && c2 == '1') || c1 == 'c' && c2 == '8'){
                    movs.append("O-O-O");
                    return;
                }
                if ((c1 == 'g' && c2 == '1') || c1 == 'g' && c2 == '8'){
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


    private void addListeners() {
        pintarFondo();
        movs = new StringBuilder();
        mueven = findViewById(R.id.txtMuevenOffline);
        tvMovs = (TextView) findViewById(R.id.txtMovsOffline);
        tvMovs.setMovementMethod(new ScrollingMovementMethod());

        tablas = findViewById(R.id.bTablasOffline);
        rendirse = findViewById(R.id.bRendirseOffline);
        tablas.setOnClickListener(this);
        rendirse.setOnClickListener(this);
        for (int i = 0; i < NUM_FILAS; i++) {
            for (int j = 0; j < NUM_COLUMNAS; j++) {
                juez.casillas[i][j].setOnClickListener(this);
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
                    juez.casillas[i][j].setBackgroundColor(Color.parseColor("#A4552A"));
                } else {

                    //casillas blancas
                    juez.casillas[i][j].setBackgroundColor(Color.parseColor("#DDDDDD"));
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