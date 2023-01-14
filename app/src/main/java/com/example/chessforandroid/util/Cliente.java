package com.example.chessforandroid.util;

import static com.example.chessforandroid.util.Constantes.debug;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.example.chessforandroid.AchievementsActivity;
import com.example.chessforandroid.FriendWaitingActivity;
import com.example.chessforandroid.OnlineActivity;
import com.example.chessforandroid.MainActivity;
import com.example.chessforandroid.OnlineWaitingActivity;
import com.example.chessforandroid.ProfileActivity;
import com.example.chessforandroid.RankingActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Clase Cliente. Contiene los elementos para la comunicación entre Cliente y Servidor.
 */
public class Cliente {
    private final static String TAG = Cliente.class.getSimpleName();

    // elementos de conexion
    private Socket conn; // socket con la conexion
    private DataInputStream in; // flujo de entrada
    private DataOutputStream out; // flujo de salida
    private String host = Constantes.ip; // direccion IP del servidor
    private int puerto = Constantes.PUERTO; // puerto en el que escucha el servidor

    // otros atributos
    private boolean conectado;
    private String user;
    private String token;
    private Context context;

    /**
     * Constructor.
     */
    public Cliente() {

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            // inicializamos el socket, dis y dos
            conn = new Socket();
            if (debug) {
                host = Constantes.ipLocal;
            } else {
                host = Constantes.ip;
            }
            conn.connect(new InetSocketAddress(host, puerto), 1200);
            in = new DataInputStream(conn.getInputStream());
            out = new DataOutputStream(conn.getOutputStream());
            conectado = true;

        } catch (Exception e) {
            // ha saltado alguna excepcion, la conexion no se ha realizado correctamente
            conectado = false;
            e.printStackTrace();
        }
    }

    /**
     * Sobrecarga del constructor.
     *
     * @param nuevoPuerto Puerto en el que escucha las peticiones el servidor.
     */
    public Cliente(int nuevoPuerto) {
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            // inicializamos el socket, dis y dos
            conn = new Socket();
            if (debug) {
                host = Constantes.ipLocal;
            } else {
                host = Constantes.ip;
            }
            conn.connect(new InetSocketAddress(host, nuevoPuerto), 1200);
            in = new DataInputStream(conn.getInputStream());
            out = new DataOutputStream(conn.getOutputStream());
            conectado = true;

        } catch (Exception e) {
            // ha saltado alguna excepcion, la conexion no se ha realizado correctamente
            conectado = false;
            e.printStackTrace();
        }
    }

    /**
     * Metodo que se encarga de cerrar las conexiones.
     */
    public void cerrarConexion() {
        try {
            conn.close();
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo que realiza la tarea de registrarse en la aplicacion.
     *
     * @param context Contexto de la Activity
     * @param user    Nombre de usuario
     * @param pass    Clave
     */
    public void registrarse(Context context, String user, String pass) {
        this.context = context;
        new Registro().execute(user, pass);
    }


    /**
     * Metodo que realiza la tarea de iniciar sesion en la aplicacion.
     *
     * @param context Contexto de la Activity
     * @param user    Nombre de usuario
     * @param pass    Clave
     */
    public void iniciarSesion(Context context, String user, String pass) {
        this.context = context;
        new InicioSesion().execute(user, pass);
    }

    /**
     * Metodo que realiza la tarea de cambiar la contraseña en la aplicacion.
     *
     * @param context Contexto de la Activity
     * @param oldPass Clave antigua
     * @param newPass Clave nueva
     */
    public void cambiarPass(Context context, String user, String oldPass, String newPass) {
        this.context = context;
        new CambiarPass().execute(user, oldPass, newPass);
    }

    /**
     * Metodo que realiza la tarea de pedir los datos de un jugador.
     *
     * @param context Contexto de la Activity
     * @param user    Nombre de usuario
     * @param token   Token unico del usuario
     */
    public void pedirDatos(Context context, String user, String token) {
        this.context = context;
        this.user = user;
        this.token = token;
        new PedirDatos().execute(token);
    }

    /**
     * Metodo que realiza la tarea de consultar los logros de un jugador.
     *
     * @param context Contexto de la Activity
     * @param aa      Callback de la Activity que lo invoca
     * @param token   Token unico del usuario
     */
    public void consultarLogros(Context context, AchievementsActivity aa, String token) {
        this.context = context;
        this.token = token;
        new ConsultarLogros(aa).execute(token);
    }

    /**
     * Metodo que se encarga de crear sala
     *
     * @param context Contexto de la Activity
     * @param token   Token unico del usuario
     */
    public void crearSala(Context context, String token) {
        this.context = context;
        this.token = token;
        new CrearSala().execute(token);
    }

    /**
     * Metodo que se encarga de unirse a una sala
     *
     * @param context Contexto de la Activity
     * @param token   Token unico del usuario
     * @param codigo  Codigo de la sala
     */
    public void unirse(Context context, String token, String codigo) {
        this.context = context;
        this.token = token;
        new Unirse().execute(token, codigo);
    }


    /**
     * Metodo que se encarga de buscar partida online
     *
     * @param context Contexto de la Activity
     * @param lla     Callback de la Activity que lo invoca
     * @param token   Token unico del usuario
     */
    public void online(Context context, OnlineWaitingActivity lla, String token) {
        this.context = context;
        this.token = token;
        new Online(lla).execute(token);
    }

    /**
     * Metodo que se encarga de esperar el movimiento del rival
     *
     * @param context Contexto de la Activity
     * @param oa      Callback de la Activity que lo invoca
     */
    public void esperarMov(Context context, OnlineActivity oa) {
        this.context = context;
        new EsperarMov(oa).execute();
    }

    /**
     * Metodo que se encarga de enviar el movimiento al rival
     *
     * @param context  Contexto de la Activity
     * @param oa       Callback de la Activity que lo invoca
     * @param casillas Posicion de las casillas en el tablero tras el movimiento
     * @param movs     Movimientos escritos en texto
     */
    public void enviarMov(Context context, OnlineActivity oa, String casillas, String movs) {
        this.context = context;
        new EnviarMov(oa).execute(casillas, movs);
    }

    /**
     * Metodo que se encarga de ofrecer tablas al rival
     *
     * @param context Contexto de la Activity
     * @param oa      Callback de la Activity que lo invoca
     */
    public void ofrecerTablas(Context context, OnlineActivity oa) {
        this.context = context;
        new EnviarTablas(oa).execute();
    }

    /**
     * Metodo que se encarga de enviar uno o varios strings al servidor
     *
     * @param s Strings a enviar al servidor
     */
    public void enviarMensaje(String... s) {
        new EnviarMensaje().execute(s);
    }


    /**
     * Metodo que se encarga de recibir el ranking de los jugadores mejor clasificados
     *
     * @param ra   Callback de la Activity que lo invoca
     * @param pref Preferencia de ordenacion
     */
    public void getRanking(RankingActivity ra, String pref) {
        new Ranking(ra).execute(pref);
    }

    /**
     * Metodo que se encarga de esperar a su amigo
     *
     * @param context Contexto de la Activity
     * @param token   Token unico del usuario
     */
    public void esperarAmigo(Context context, String token) {
        this.context = context;
        this.token = token;
        new EsperarAmigo().execute();
    }

    /**
     * Clase bloqueante que recibe los datos iniciales
     *
     * @param context Contexto de la Activity
     * @param token   Token unico del usuario
     * @return
     */
    public Object[] getDatosIniciales(Context context, String token) {
        this.context = context;
        this.token = token;
        Object[] o = null;
        try {
            o = new DatosIniciales().execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return o;
    }


    /**
     * Clase EsperarAmigo.
     */
    public class EsperarAmigo extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... voids) {
            try {
                // espera el string de "jugar" cuando el servidor me asigne un rival
                return in.readUTF();
            } catch (IOException e) {
                Toast.makeText(context, "Error al esperar amigo", Toast.LENGTH_SHORT).show();
                ((Activity) context).finish();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equalsIgnoreCase("jugar")) {
                // se ha encontrado rival y se lanza el activity para iniciar partida
                Intent localIntent = new Intent(context, OnlineActivity.class);
                localIntent.putExtra("token", token);
                context.startActivity(localIntent);
            } else if (s.equalsIgnoreCase("norivales")) {
                Toast.makeText(context, "Tiempo de espera agotado.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Error inesperado", Toast.LENGTH_SHORT).show();
            }
            ((Activity) context).finish();
        }
    }

    /**
     * Clase Ranking.
     */
    public class Ranking extends AsyncTask<String, Void, ArrayList<RankingItem>> {

        /**
         * Callback para poder mandarle el ranking a esa Activity
         */
        private RankingActivity ra;

        public Ranking(RankingActivity ra) {
            this.ra = ra;
        }

        @Override
        protected ArrayList<RankingItem> doInBackground(String... strings) {

            // ArrayList de los RankingItem (posicion del jugador, nombre y nivel/elo)
            ArrayList<RankingItem> datos = new ArrayList<>();
            try {
                // se envia la preferencia de ordenacion (por elo o por nivel)
                out.writeUTF(strings[0]);

                // numero de usuarios que habra en el ArrayList
                int numUsuarios = in.readInt();

                // se establece la posicion y el nombre de usuario en el ArrayList
                for (int i = 0; i < numUsuarios; i++) {
                    RankingItem ri = new RankingItem();
                    ri.setPosition(i + 1);
                    ri.setUser(in.readUTF());
                    datos.add(ri);
                }

                // se establece el elo en el ArrayList
                for (int i = 0; i < numUsuarios; i++) {
                    RankingItem ri = datos.get(i);
                    ri.setElo(in.readUTF());
                }

                return datos;

            } catch (IOException e) {

                Toast.makeText(ra, "Error al mostrar el ranking", Toast.LENGTH_SHORT).show();
                ra.finish();
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<RankingItem> datos) {

            // se usa el callback para devolver el ArrayList al Activity que lo necesitaba
            ra.alRecibirDatos(datos);
        }
    }

    /**
     * Clase EnviarMensaje.
     */
    public class EnviarMensaje extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            try {

                // se envia el mensaje que se recibe como parametro
                out.writeUTF(strings[0]);

            } catch (IOException e) {
                Log.e(TAG, "Error al enviar el mensaje al servidor");
                e.printStackTrace();
            }

            return null;
        }

    }

    /**
     * Clase EnviarTablas.
     */
    public class EnviarTablas extends AsyncTask<Void, Void, Boolean> {

        /**
         * Callback para poder mandarle el ranking a esa Activity
         */
        OnlineActivity caller;

        EnviarTablas(OnlineActivity caller) {
            this.caller = caller;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // se envia el mensaje que indica que el usuario quiere tablas
                out.writeUTF("tablas");

                // true (tablas aceptadas), false (tablas rechazadas)
                return in.readBoolean();

            } catch (IOException e) {
                Toast.makeText(caller, "Error al ofrecer tablas", Toast.LENGTH_SHORT).show();
                caller.finish();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);

            // se notifica de la respuesta del rival a la Activity correspondiente
            caller.trasOfrecerTablas(b);
        }
    }

    /**
     * Clase EnviarMov.
     */
    public class EnviarMov extends AsyncTask<String, Void, Object[]> {

        /**
         * Callback para poder mandarle el ranking a esa Activity
         */
        OnlineActivity caller;

        EnviarMov(OnlineActivity caller) {
            this.caller = caller;
        }

        @Override
        protected Object[] doInBackground(String... strings) {
            Object[] objects = new Object[3];
            try {
                // se envia la posicion de las casillas, la voluntad de rendirse o pedir tablas
                out.writeUTF(strings[0]);

                // si el mensaje era rendirse o tablas, termina

                if (strings[0].equalsIgnoreCase("rendirse") ||
                        strings[0].equalsIgnoreCase("tablas")) {
                    return objects;
                }

                // se envian los movimientos
                out.writeUTF(strings[1]);

                // es jaque mate?
                boolean b = in.readBoolean();
                objects[0] = b;
                if (b) {
                    // si es jaque mate, termina
                    return objects;
                }
                // es jaque?
                objects[1] = in.readBoolean();
                // puede mover?
                objects[2] = in.readBoolean();

            } catch (IOException e) {
                Log.e(TAG, "Error al enviar el movimiento");
                e.printStackTrace();
            }

            return objects;
        }

        @Override
        protected void onPostExecute(Object[] objects) {
            super.onPostExecute(objects);

            // se envia el array con la informacion actualizada a la Activity que la invoca
            caller.trasEnviarMov(objects);
        }
    }

    /**
     * Clase EsperarMov.
     */
    public class EsperarMov extends AsyncTask<Void, Void, Object[]> {

        /**
         * Callback para poder mandarle el ranking a esa Activity
         */
        OnlineActivity caller;

        EsperarMov(OnlineActivity caller) {
            this.caller = caller;
        }

        @Override
        protected Object[] doInBackground(Void... voids) {
            Object[] objects = new Object[5];
            try {
                // posicion de las piezas, voluntad de rendirse o peticion de tablas
                String s = in.readUTF();
                objects[0] = s;

                if (s.equalsIgnoreCase("rendirse") ||
                        s.equalsIgnoreCase("tablas")) {

                    // si el rival se quiere rendir u ofrece tablas, termina
                    return objects;
                }

                // string con los movimientos
                objects[1] = in.readUTF();
                // es jaque mate?
                boolean b = in.readBoolean();
                objects[2] = b;

                if (b) {
                    return objects;
                }

                // es jaque?
                objects[3] = in.readBoolean();
                // puede mover?
                objects[4] = in.readBoolean();
            } catch (IOException e) {
                Toast.makeText(caller, "Error de conexión", Toast.LENGTH_SHORT).show();
                ((Activity) context).finish();
                e.printStackTrace();
            }

            return objects;
        }

        @Override
        protected void onPostExecute(Object[] objects) {
            super.onPostExecute(objects);

            // se envia el array con la informacion actualizada a la Activity que la invoca
            caller.trasRecibirMov(objects);
        }

    }

    /**
     * Clase DatosIniciales.
     */
    public class DatosIniciales extends AsyncTask<Object, Void, Object[]> {


        protected Object[] doInBackground(Object... params) {

            Object[] o = new Object[3];
            try {

                // se envia el token para identificarse
                out.writeUTF(token);

                // recibo mi nombre
                o[0] = in.readUTF();

                // recibo el nombre del rival
                o[1] = in.readUTF();

                // recibo si es mi turno
                o[2] = in.readBoolean();
            } catch (IOException e) {
                Toast.makeText(context, "Error al iniciar la partida", Toast.LENGTH_SHORT).show();
                ((Activity) context).finish();
                e.printStackTrace();
            }
            return o;
        }
    }

    /**
     * Clase Online.
     */
    public class Online extends AsyncTask<String, Void, String> {

        OnlineWaitingActivity lla;

        public Online(OnlineWaitingActivity lla) {
            this.lla = lla;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                // se recibe el token como parametro
                token = strings[0];

                // se envia la voluntad de jugar online
                out.writeUTF("online");

                // se envia el token de identificacion
                out.writeUTF(token);

                //respuesta del servidor
                return in.readUTF();

            } catch (IOException e) {
                Toast.makeText(lla, "Error al buscar partida", Toast.LENGTH_SHORT).show();
                ((Activity) context).finish();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s == null) {
                Log.e(TAG, "La clase Online ha devuelto null");
            }
            if (s.equalsIgnoreCase("norivales")) {
                Toast.makeText(lla, "No se han encontrado rivales", Toast.LENGTH_SHORT).show();
            }
            if (s.equalsIgnoreCase("lleno")) {
                Toast.makeText(lla, "El servidor está lleno", Toast.LENGTH_SHORT).show();
            }
            if (s.equalsIgnoreCase("abortada")) {
                Toast.makeText(lla, "Error al comenzar la partida. Vuelve a intentarlo",
                        Toast.LENGTH_SHORT).show();
            }
            if (s.equalsIgnoreCase("jugar")) {
                Intent onlineIntent = new Intent(context, OnlineActivity.class);
                onlineIntent.putExtra("token", token);
                context.startActivity(onlineIntent);
            }
            lla.finish();
            cerrarConexion();
        }
    }

    /**
     * Clase Unirse.
     */
    public class Unirse extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            try {
                // se recibe el token como parametro
                token = strings[0];

                // se envia al servidor la solicitud de unirse a una partida existente
                out.writeUTF("unirse");

                // se envia el token identificativo
                out.writeUTF(token);
                // se envia el codigo de la sala, recibido como parametro en forma de String
                out.writeInt(Integer.parseInt(strings[1]));

                // se recibe la respuesta del servidor
                return in.readInt();
            } catch (IOException e) {
                Toast.makeText(context, "Error al unirse a la partida", Toast.LENGTH_SHORT).show();
                ((Activity) context).finish();
                e.printStackTrace();
            }
            return -3;
        }

        @Override
        protected void onPostExecute(Integer res) {
            super.onPostExecute(res);
            if (res == 1) {

                // este usuario ya se encuentra listo para comenzar la partida
                try {

                    String jugar = in.readUTF();

                    if (jugar.equalsIgnoreCase("jugar")) {

                        // cuando llega el mensaje, se lanza el Activity y comienza la partida
                        Intent localIntent = new Intent(context, OnlineActivity.class);
                        localIntent.putExtra("token", token);
                        context.startActivity(localIntent);
                    }
                } catch (IOException e) {
                    Toast.makeText(context, "Error al iniciar la partida", Toast.LENGTH_SHORT).show();
                    ((Activity) context).finish();
                    e.printStackTrace();
                }
            } else if (res == -1) {
                Toast.makeText(context, "Código incorrecto", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
            cerrarConexion();
        }
    }

    /**
     * Clase CrearSala.
     */
    public class CrearSala extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            try {
                // se recibe el string como parametro
                token = strings[0];

                // se envia al servidor la peticion de crear sala
                out.writeUTF("crearsala");

                // se manda el token como identificacion
                out.writeUTF(token);

                // se recibe como respuesta el codigo de la sala
                return in.readInt();
            } catch (IOException e) {
                Toast.makeText(context, "Error al crear sala", Toast.LENGTH_SHORT).show();
                ((Activity) context).finish();
                e.printStackTrace();
            }
            return -3;
        }

        @Override
        protected void onPostExecute(Integer codigo) {
            super.onPostExecute(codigo);
            Log.i(TAG, "El codigo recibido es " + codigo);
            // se lanza el activity encargado de esperar al rival con el codigo de la sala
            Intent lobbyIntent = new Intent(context, FriendWaitingActivity.class);
            lobbyIntent.putExtra("id", codigo);
            lobbyIntent.putExtra("token", token);
            context.startActivity(lobbyIntent);
            cerrarConexion();
        }
    }

    /**
     * Clase Registro.
     */
    public class Registro extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            try {
                // se recibe el nombre de usuario como parametro
                user = strings[0];

                // se envia al servidor la peticion de registro
                out.writeUTF("registro");

                // se envia el usuario
                out.writeUTF(strings[0]);

                // se envia la contraseña
                out.writeUTF(strings[1]);

                // se recibe la respuesta del servidor
                return in.readInt();

            } catch (IOException e) {
                Toast.makeText(context, "Error al registrarse", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return -3;
        }

        @Override
        protected void onPostExecute(Integer res) {
            super.onPostExecute(res);
            switch (res) {

                case 0:
                    Toast.makeText(context, "El nombre de usuario ya existe.", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    try {
                        Intent mainIntentSignup = new Intent(context, MainActivity.class);
                        mainIntentSignup.putExtra("user", user);
                        mainIntentSignup.putExtra("token", in.readUTF());
                        context.startActivity(mainIntentSignup);
                    } catch (IOException e) {
                        Toast.makeText(context, "Error al registrarse", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    break;
                default:
                    Log.e(TAG, "Error de conexión con la base de datos");
                    break;
            }
            cerrarConexion();
        }
    }

    /**
     * Clase CambiarPass.
     */
    public class CambiarPass extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            try {

                // se recibe el nombre de usuario como parametro
                user = strings[0];

                // se envia al servidor la peticion de cambiar la clave
                out.writeUTF("cambiarpass");

                // se envia el usuario
                out.writeUTF(strings[0]);

                // se envia la clave antigua
                out.writeUTF(strings[1]);

                // se envia la clave nueva
                out.writeUTF(strings[2]);

                // true (cambio correcto), false (cambio incorrecto)
                return in.readBoolean();
            } catch (IOException e) {
                Toast.makeText(context, "Error al cambiar la contraseña", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean exitoso) {
            super.onPostExecute(exitoso);

            if (exitoso) {
                Toast.makeText(context, "Contraseña cambiada correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Nombre de usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
            cerrarConexion();
        }
    }

    /**
     * Clase InicioSesion.
     */
    public class InicioSesion extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {

                // se recibe el nombre de usuario como parametro
                user = strings[0];

                // se envia al servidor la peticion de iniciar sesion
                out.writeUTF("login");

                // se envia el usuario
                out.writeUTF(strings[0]);

                // se envia la clave
                out.writeUTF(strings[1]);

                // si el inicio de sesion ha sido exitoso...
                if (in.readBoolean()) {
                    // ...se recibe el token
                    return in.readUTF();
                }
                return null;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null) {
                Intent mainIntentSignup = new Intent(context, MainActivity.class);
                mainIntentSignup.putExtra("user", user);
                mainIntentSignup.putExtra("token", s);
                cerrarConexion();
                context.startActivity(mainIntentSignup);
            } else {
                Toast.makeText(context, "Nombre de usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
            cerrarConexion();
        }
    }

    /**
     * Clase ConsultarLogros.
     */
    public class ConsultarLogros extends AsyncTask<String, Void, Boolean[]> {

        AchievementsActivity aa;

        public ConsultarLogros(AchievementsActivity aa) {
            this.aa = aa;
        }

        @Override
        protected Boolean[] doInBackground(String... strings) {

            Boolean[] logros = new Boolean[7];
            try {

                // se recibe el usuario como parametro
                user = strings[0];

                // se envia al servidor la peticion de consultar los logros
                out.writeUTF("consultarlogros");

                // se envia al servidor el token identificativo
                out.writeUTF(token);

                // se comprueba uno por uno si se ha completdo cada logro
                logros[0] = in.readBoolean();
                logros[1] = in.readBoolean();
                logros[2] = in.readBoolean();
                logros[3] = in.readBoolean();
                logros[4] = in.readBoolean();
                logros[5] = in.readBoolean();
                logros[6] = in.readBoolean();

                // se devuelve el resultado
                return logros;

            } catch (IOException e) {
                Toast.makeText(context, "Error al consultar los datos", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean[] logros) {
            super.onPostExecute(logros);

            // se devuelve al Activity la informacion acerca de los logros
            aa.alTerminarConsulta(logros);
        }
    }

    /**
     * Clase PedirDatos.
     */
    public class PedirDatos extends AsyncTask<String, Void, Integer[]> {

        @Override
        protected Integer[] doInBackground(String... strings) {

            Integer[] res = new Integer[6];
            for (int i = 0; i < res.length; i++) {
                res[i] = 0;
            }
            try {
                token = strings[0];
                out.writeUTF("pedirdatos");
                out.writeUTF(token);

                // nivel
                res[0] = in.readInt();

                // elo
                res[1] = in.readInt();

                // partidas jugadas
                res[2] = in.readInt();

                // numero de victorias
                res[3] = in.readInt();

                // numero de derrotas
                res[4] = in.readInt();

                //numero de tablas
                res[5] = in.readInt();
                return res;
            } catch (IOException e) {
                Toast.makeText(context, "Error al consultar los datos", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer[] res) {
            super.onPostExecute(res);
            int[] datos = new int[res.length];
            for (int i = 0; i < res.length; i++) {
                datos[i] = res[i];
            }
            Intent i = new Intent(context, ProfileActivity.class);
            i.putExtra("user", user);
            i.putExtra("token", token);
            i.putExtra("datos", datos);
            cerrarConexion();
            context.startActivity(i);
        }
    }

    /**
     * Metodo que devuelve un booleano que representa si la conexion ha sido correcta o no
     *
     * @return True (correcta), False (incorrecta)
     */
    public boolean isConectado() {
        return conectado;
    }
}