package com.example.chessforandroid;

import static com.example.chessforandroid.util.Constantes.NUM_COLUMNAS;
import static com.example.chessforandroid.util.Constantes.NUM_FILAS;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
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
    private TextView tvMueven;
    private TextView vs;
    public StringBuilder movs;
    public String tag;
    public String rival;
    public String yo;

    //tablero
    private boolean haySeleccionada;
    private boolean fin;
    private Casilla quieroMover;

    //elementos de juego
    private int nMovs;
    private int contadorTablas;
    private String token;
    private boolean soyBlancas;
    private boolean quieroTablas;
    private boolean miTurno;
    private Cliente cliente;
    private Juez juez;
    private Object[] o = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        haySeleccionada = false;
        quieroTablas = false;
        nMovs = 0;
        contadorTablas = 0;
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

        if (quieroTablas && !miTurno) {
            Toast.makeText(this, "El rival está decidiendo si acepta las tablas", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!miTurno) {
            Toast.makeText(this, "Turno del rival", Toast.LENGTH_SHORT).show();
            return;
        }

        if (view.getId() == R.id.bTablasLocal) {
            if (contadorTablas > 3) {
                Toast.makeText(this, "Ya has solicitado tablas 3 veces", Toast.LENGTH_SHORT).show();
                return;
            }

            quieroTablas = true;
            tablas.setBackgroundColor(Color.GREEN);
            Toast.makeText(this, "Tablas ofrecidas al rival", Toast.LENGTH_SHORT).show();
            if (cliente.isConectado()) {
                cliente.ofrecerTablas(this, this);
                miTurno = false;
            }
            return;
        }
        if (view.getId() == R.id.bRendirseLocal) {


            if (cliente.isConectado()) {
                cliente.enviarMov(this, this, "rendirse", movs.toString());
                miTurno = false;
            }
            fin = true;
            gestionarFinal(!soyBlancas);
            return;
        }

        Casilla casSelec = (Casilla) view;

        if (!haySeleccionada) {
            if (casSelec.getPieza() == null)
                return;
            if (casSelec.getPieza().isBlancas() != soyBlancas)
                return;

            casSelec.setBackgroundColor(Color.parseColor("#36E0FA"));
            quieroMover = casSelec;
            tag = quieroMover.getPieza().getTag();
            quieroMover.setPieza(casSelec.getPieza());
        } else {

            pintarFondo();
            if (juez.esValido(juez.casillas, quieroMover, casSelec)) {
                quieroTablas = false;
                tablas.setBackgroundColor(Color.parseColor("#646464"));
                juez.captura = casSelec.getPieza() != null;
                juez.mover(quieroMover, casSelec);
                if (nMovs > 0 && nMovs % 3 == 0) {
                    movs.append("\n");
                    tvMovs.setText(movs);
                }
                nMovs++;
                actualizarTxt(casSelec.getFila(), casSelec.getColumna());
                juez.sTablero = juez.casillasToString();
                if (cliente.isConectado()) {
                    cliente.enviarMov(this, this, juez.casillasToString(), movs.toString());
                    miTurno = false;
                    actualizarTurno();
                }
            } else {
                Log.e("************************", "error");
            }

        }
        haySeleccionada = !haySeleccionada;
    }


    public void trasOfrecerTablas(boolean aceptadas) {
        if (aceptadas) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage(R.string.draw_accepted);

            builder.setPositiveButton(R.string.accept, null);
            Dialog dialog = builder.create();
            dialog.show();
            tablas.setBackgroundColor(Color.GREEN);
            fin = true;
        } else {
            Toast.makeText(this, "El rival rechaza las tablas", Toast.LENGTH_SHORT).show();
            miTurno = true;
            tablas.setBackgroundColor(Color.parseColor("#646464"));
            contadorTablas++;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("********************************************************************", "socket cerrado en onPause()");
        cliente.abandonar();
        //cliente.cerrarConexion();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("********************************************************************", "socket cerrado en onDestroy()");
        //cliente.cerrarConexion();
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.i("********************************************************************", "socket cerrado en onStop()");
        //cliente.cerrarConexion();
    }


    public void trasRecibirMov(Object[] objects) {

        String s0 = (String) objects[0];
        if (s0 != null && s0.equalsIgnoreCase("rendirse")) {

            Toast.makeText(this, "¡Has ganado por abandono del rival!", Toast.LENGTH_SHORT).show();
            gestionarFinal(soyBlancas);
            fin = true;
            return;
        }
        if (s0 != null && s0.equalsIgnoreCase("tablas")) {

            propuestaTablas();
            miTurno = false;
            return;
        }

        //tablero
        juez.sTablero = s0;
        //movs actualizados
        String s1 = (String) objects[1];
        tvMovs.setText(s1);
        //es jaque mate?
        fin = (boolean) objects[2];
        System.out.println("fin vale: " + fin);
        if (fin) {
            gestionarFinal(miTurno == soyBlancas);
            return;
        }
        //es jaque?
        juez.jaque = (boolean) objects[3];
        if (juez.jaque) {
            Toast.makeText(this, "JAQUE", Toast.LENGTH_SHORT).show();
        }
        //puede mover?
        juez.puedeMover = (boolean) objects[4];


        juez.inTablero = juez.stringToInt(juez.sTablero);
        juez.actualizarCasillas(juez.inTablero);
        miTurno = true;
        actualizarTurno();
    }

    public void gestionarFinal(Boolean ganoBlancas) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String ganador = null;
        if (ganoBlancas == null) {
            builder.setMessage(R.string.draw_accepted);

            builder.setPositiveButton(R.string.accept, null);
            Dialog dialog = builder.create();
            dialog.show();
            tablas.setBackgroundColor(Color.GREEN);
            return;
        }
        if (ganoBlancas && soyBlancas) {
            builder.setTitle(R.string.you_win);
            ganador = yo;
        }
        if (ganoBlancas && !soyBlancas) {
            builder.setTitle(R.string.you_lose);
            ganador = rival;
        }
        if (!ganoBlancas && soyBlancas) {
            builder.setTitle(R.string.you_lose);
            ganador = rival;
        }
        if (!ganoBlancas && !soyBlancas) {
            builder.setTitle(R.string.you_win);
            ganador = yo;
        }
        builder.setMessage("El ganador es " + ganador);
        builder.setPositiveButton(R.string.accept, null);
        Dialog dialog = builder.create();
        dialog.show();

    }

    private void propuestaTablas() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.draw_offered);
        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (cliente.isConectado()) {
                    cliente.enviarMensaje("aceptadas");
                    //Toast.makeText(GameActivity.this, "Tablas aceptadas", Toast.LENGTH_SHORT).show();
                    fin = true;
                    gestionarFinal(null);
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (cliente.isConectado()) {
                    cliente.enviarMensaje("rechazadas");
                    Toast.makeText(GameActivity.this, "Tablas rechazadas", Toast.LENGTH_SHORT).show();
                }
                esperarMov();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    public void trasEnviarMov(Object[] objects) {

        if (objects[0] == null) {
            return;
        }
        //es jaque mate?
        fin = (boolean) objects[0];
        System.out.println("fin vale: " + fin);
        if (fin) {
            gestionarFinal(miTurno != soyBlancas);
            return;
        }
        //es jaque?
        juez.jaque = (boolean) objects[1];
        if (juez.jaque) {
            Toast.makeText(this, "JAQUE", Toast.LENGTH_SHORT).show();
        }
        //puede mover?
        juez.puedeMover = (boolean) objects[2];

        juez.inTablero = juez.stringToInt(juez.sTablero);
        juez.actualizarCasillas(juez.inTablero);
        esperarMov();
    }


    private void esperarMov() {
        if (fin)
            return;
        if (cliente.isConectado()) {
            cliente.esperarMov(this, this);
        } else {
            Log.e("************************", "Error al esperar movimiento");
        }
    }

    private void addListeners() {
        pintarFondo();
        movs = new StringBuilder();
        tvMueven = findViewById(R.id.txtMuevenLocal);
        vs = findViewById(R.id.txtVs);
        tvMovs = (TextView) findViewById(R.id.txtMovsLocal);
        tvMovs.setMovementMethod(new ScrollingMovementMethod());

        Object[] o = null;
        if (cliente.isConectado()) {
            o = cliente.getDatosIniciales(this, token);
        } else {
            Log.e("************************", "Error al recibir los datos iniciales");
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
        yo = (String) o[0];
        rival = (String) o[1];
        miTurno = soyBlancas;
        if (soyBlancas) {
            vs.setText("(B) " + yo + " vs " + rival + " (N)");
            Toast.makeText(this, "Juegas con blancas", Toast.LENGTH_SHORT).show();
        } else {
            vs.setText("(B) " + rival + " vs " + yo + " (N)");
            Toast.makeText(this, "Juegas con negras", Toast.LENGTH_SHORT).show();
            esperarMov();
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


    private void actualizarTurno() {
        if (soyBlancas == miTurno)
            tvMueven.setText(R.string.white_move);
        else
            tvMueven.setText(R.string.black_move);
    }

    private void actualizarTxt(int fila, int col) {

        char[] cs = juez.coorToChar(fila, col);
        char c1 = cs[0];
        char c2 = cs[1];
        movs = new StringBuilder(tvMovs.getText().toString());
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

        tvMovs.setText(movs);
    }

    public void crearCasillas() {
        boolean cambiar = false;
        int x = 0;
        for (int i = 0; i < NUM_FILAS; i++) {
            for (int j = 0; j < NUM_COLUMNAS; j++) {

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
}