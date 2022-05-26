package com.example.chessforandroid;

import static com.example.chessforandroid.util.Constantes.NUM_COLUMNAS;
import static com.example.chessforandroid.util.Constantes.NUM_FILAS;

import android.annotation.SuppressLint;
import android.app.Dialog;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chessforandroid.piezas.Alfil;
import com.example.chessforandroid.piezas.Caballo;
import com.example.chessforandroid.piezas.Dama;
import com.example.chessforandroid.piezas.Peon;
import com.example.chessforandroid.piezas.Rey;
import com.example.chessforandroid.piezas.Torre;
import com.example.chessforandroid.juego.Casilla;
import com.example.chessforandroid.util.Cliente;
import com.example.chessforandroid.util.Constantes;
import com.example.chessforandroid.util.Juez;


/**
 * OnlineActivity, activity encargado de gestionar la partida online
 */
public class OnlineActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = OnlineActivity.class.getSimpleName();

    // atributos de layout
    private GridLayout oGameBoard;
    private LinearLayout oGameBoardShell;
    private Button tablas;
    public TextView tvMovs;
    private TextView tvMueven;
    public StringBuilder movs;
    public String tag;
    public String rival;
    public String yo;
    public String token;

    // atributos de tablero
    private boolean haySeleccionada;
    private boolean fin;
    private Casilla quieroMover;

    // elementos de juego
    private int nMovs;
    private int contadorTablas;
    private boolean soyBlancas;
    private boolean quieroTablas;
    private boolean miTurno;
    private Cliente cliente;
    private Juez juez;
    private int onResumes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        haySeleccionada = false;
        quieroTablas = false;
        nMovs = 0;
        contadorTablas = 0;
        onResumes = 0;
        juez = new Juez();
        Intent i = getIntent();
        token = i.getStringExtra("token");
        setContentView(R.layout.activity_friend);
        crearCasillas();

        oGameBoardShell = findViewById(R.id.shellGameBoardOnline);
        oGameBoard = findViewById(R.id.gridGameBoardOnline);
        Tablero t = new Tablero();
        t.execute();

        cliente = new Cliente(Constantes.PUERTO_PARTIDA);

    }


    @Override
    public void onClick(View view) {

        // idem que en OfflineActivity

        if (fin)
            return;

        if (quieroTablas && !miTurno) {
            Toast.makeText(this, R.string.deciding, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!miTurno && soyBlancas) {
            Toast.makeText(this, R.string.black_move, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!miTurno) {
            Toast.makeText(this, R.string.white_move, Toast.LENGTH_SHORT).show();
            return;
        }

        if (view.getId() == R.id.bTablasOnline) {
            if (contadorTablas > 2) {
                Toast.makeText(this, R.string.already_offered, Toast.LENGTH_SHORT).show();
                return;
            }

            quieroTablas = true;
            tablas.setBackgroundColor(Color.GREEN);
            Toast.makeText(this, R.string.draw_offered, Toast.LENGTH_SHORT).show();
            if (cliente.isConectado()) {
                cliente.ofrecerTablas(this, this);
                miTurno = false;
            }
            return;
        }
        if (view.getId() == R.id.bRendirseOnline) {

            if (cliente.isConectado()) {
                cliente.enviarMov(this, this, "rendirse", movs.toString());
                miTurno = false;
            }
            fin = true;
            gestionarFinal(!soyBlancas);
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
            if (casSelec.getPieza().isBlancas() != soyBlancas) {

                // si contiene una pieza pero es del rival, se notifica y no devuelve nada
                return;
            }

            // se colorea la casilla seleccionada para identificarla mejor
            casSelec.setBackgroundColor(Color.parseColor("#459CCD"));

            // se guarda la casilla en una variable
            quieroMover = casSelec;
            tag = quieroMover.getPieza().getTag();
            quieroMover.setPieza(casSelec.getPieza());
        } else {
            // si ya habia casilla seleccionada...

            // se pinta el fondo para borrar la casilla coloreada
            pintarFondo();

            // se comprueba si el movimiento es valido
            if (juez.esValido(juez.casillas, quieroMover, casSelec)) {

                // el movimiento es valido, por tanto no hay tablas
                quieroTablas = false;

                tablas.setBackgroundColor(Color.parseColor("#646464"));
                juez.captura = casSelec.getPieza() != null;
                juez.mover(quieroMover, casSelec);
                quieroMover.setBackgroundColor(Color.parseColor("#AECDDF"));
                casSelec.setBackgroundColor(Color.parseColor("#8DC5E5"));

                if (nMovs > 0 && nMovs % 3 == 0) {
                    movs.append("\n");
                    tvMovs.setText(movs);
                }
                nMovs++;

                actualizarTxt(casSelec.getFila(), casSelec.getColumna());
                juez.sTablero = juez.casillasToString();

                if (cliente.isConectado()) {
                    // se envia el movimiento al rival
                    cliente.enviarMov(this, this, juez.casillasToString(), movs.toString());
                    miTurno = false;
                    actualizarTurno();
                }
            } else {
                Toast.makeText(this, R.string.not_valid, Toast.LENGTH_SHORT).show();
            }

        }
        haySeleccionada = !haySeleccionada;
    }


    /**
     * Metodo que se ejecuta cuando el rival ha respondido a la propuesta de tablas
     *
     * @param aceptadas True (aceptadas), False (rechazadas)
     */
    public void trasOfrecerTablas(boolean aceptadas) {

        if (aceptadas) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.draw_accepted);
            builder.setPositiveButton(R.string.accept, null);
            builder.setNegativeButton(R.string.exit, (dialogInterface, i) -> finish());
            Dialog dialog = builder.create();
            dialog.show();
            tablas.setBackgroundColor(Color.GREEN);
            fin = true;

        } else {

            Toast.makeText(this, R.string.draw_declined, Toast.LENGTH_SHORT).show();
            miTurno = true;
            tablas.setBackgroundColor(Color.parseColor("#646464"));
            contadorTablas++;
        }
    }

    /**
     * Metodo que se ejecuta cuando se ha recibido movimiento
     *
     * @param objects Vector de informacion relacionada con el movimiento
     */
    public void trasRecibirMov(Object[] objects) {

        String s0 = (String) objects[0];
        if (s0 != null && s0.equalsIgnoreCase("rendirse")) {

            gestionarFinal(soyBlancas);
            fin = true;
            return;
        }
        if (s0 != null && s0.equalsIgnoreCase("tablas")) {

            propuestaTablas();
            miTurno = false;
            return;
        }
        if (s0 != null && s0.equalsIgnoreCase("tiempo")) {

            gestionarFinal(!soyBlancas);
            fin = true;
            return;
        }

        // posicion de las fichas
        juez.sTablero = s0;
        // string con los movimientos
        String s1 = (String) objects[1];
        tvMovs.setText(s1);
        // es jaque mate?
        fin = (boolean) objects[2];
        if (fin) {
            gestionarFinal(miTurno == soyBlancas);
            return;
        }
        // es jaque?
        juez.jaque = (boolean) objects[3];
        if (juez.jaque) {
            Toast.makeText(this, R.string.check, Toast.LENGTH_SHORT).show();
        }
        // puede mover?
        juez.puedeMover = (boolean) objects[4];


        juez.inTablero = juez.stringToInt(juez.sTablero);
        juez.actualizarCasillas(juez.inTablero);
        miTurno = true;
        actualizarTurno();
    }

    /**
     * Metodo que se encarga de gestionar el final de la partida y notificar el ganador
     *
     * @param ganoBlancas True (ganaron las blancas), False (ganaron las negras), Null (tablas)
     */
    public void gestionarFinal(Boolean ganoBlancas) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String ganador = null;

        if (ganoBlancas == null) {
            builder.setMessage(R.string.draw_accepted);

            builder.setPositiveButton(R.string.accept, null);
            builder.setNegativeButton(R.string.exit, (dialogInterface, i) -> finish());
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

        builder.setMessage(getString(R.string.winner_is) + " " + ganador);
        builder.setPositiveButton(R.string.accept, null);
        builder.setNegativeButton(R.string.exit, (dialogInterface, i) -> finish());
        Dialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Metodo que se encarga de gestionar una propuesta de tablas
     */
    private void propuestaTablas() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.rival_offered_draw);
        builder.setPositiveButton(R.string.accept, (dialogInterface, i) -> {
            if (cliente.isConectado()) {
                cliente.enviarMensaje("aceptadas");
                //Toast.makeText(OnlineActivity.this, "Tablas aceptadas", Toast.LENGTH_SHORT).show();
                fin = true;
                gestionarFinal(null);
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
            if (cliente.isConectado()) {
                cliente.enviarMensaje("rechazadas");
                Toast.makeText(OnlineActivity.this, R.string.draw_declined, Toast.LENGTH_SHORT).show();
            }
            esperarMov();
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Metodo que se ejecuta cuando se ha terminado de enviar el movimiento al rival
     *
     * @param objects Vector con informacion de la partida tras el movimiento
     */
    public void trasEnviarMov(Object[] objects) {

        // si el vector es nulo, la partida ha terminado
        if (objects[0] == null) {
            return;
        }
        // es jaque mate?
        fin = (boolean) objects[0];
        if (fin) {
            gestionarFinal(miTurno != soyBlancas);
            return;
        }
        // es jaque?
        juez.jaque = (boolean) objects[1];
        if (juez.jaque) {
            Toast.makeText(this, R.string.check, Toast.LENGTH_SHORT).show();
        }
        // puede mover?
        juez.puedeMover = (boolean) objects[2];

        juez.inTablero = juez.stringToInt(juez.sTablero);
        juez.actualizarCasillas(juez.inTablero);

        esperarMov();
    }

    /**
     * Metodo que espera movimiento del rival
     */
    private void esperarMov() {
        if (fin)
            return;
        if (cliente.isConectado()) {
            cliente.esperarMov(this, this);
        } else {
            Log.e(TAG, "Error al esperar movimiento");
        }
    }

    @SuppressLint("SetTextI18n")
    private void addListeners() {
        pintarFondo();
        movs = new StringBuilder();
        tvMueven = findViewById(R.id.txtMuevenOnline);
        TextView vs = findViewById(R.id.txtVs);
        tvMovs = findViewById(R.id.txtMovsOnline);
        tvMovs.setMovementMethod(new ScrollingMovementMethod());

        Object[] o = null;
        if (cliente.isConectado()) {
            o = cliente.getDatosIniciales(this, token);
        } else {
            Log.e(TAG, "Error al recibir los datos iniciales");
        }

        tablas = findViewById(R.id.bTablasOnline);
        Button rendirse = findViewById(R.id.bRendirseOnline);
        tablas.setOnClickListener(this);
        rendirse.setOnClickListener(this);
        for (int i = 0; i < NUM_FILAS; i++) {
            for (int j = 0; j < NUM_COLUMNAS; j++) {
                juez.casillas[i][j].setOnClickListener(this);
            }
        }
        assert o != null;
        soyBlancas = (boolean) o[2];
        yo = (String) o[0];
        rival = (String) o[1];
        miTurno = soyBlancas;
        if (soyBlancas) {
            vs.setText("(" + getString(R.string.white) + ") " + yo + " vs " + rival + " (" + getString(R.string.black) + ")");
            Toast.makeText(this, R.string.play_white, Toast.LENGTH_SHORT).show();
        } else {
            vs.setText("(" + getString(R.string.white) + ") " + rival + " vs " + yo + " (" + getString(R.string.black) + ")");
            Toast.makeText(this, R.string.play_black, Toast.LENGTH_SHORT).show();
            esperarMov();
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
     * Metodo que actualiza el texto con el turno
     */
    private void actualizarTurno() {
        if (soyBlancas == miTurno)
            tvMueven.setText(R.string.white_move);
        else
            tvMueven.setText(R.string.black_move);
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
                    int width = OnlineActivity.this.oGameBoardShell.getMeasuredWidth();
                    int height = OnlineActivity.this.oGameBoardShell.getMeasuredHeight();
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
                        OnlineActivity.this.oGameBoard.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        OnlineActivity.this.oGameBoard.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            };
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        onResumes++;
        if (onResumes == 2) {
            Toast.makeText(this, R.string.forfeit, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cliente.isConectado()) {
            cliente.enviarMensaje("abandonar");
            cliente.cerrarConexion();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cliente.isConectado()) {
            cliente.enviarMensaje("abandonar");
            cliente.cerrarConexion();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (cliente.isConectado()) {
            cliente.enviarMensaje("abandonar");
            cliente.cerrarConexion();
        }
    }

}