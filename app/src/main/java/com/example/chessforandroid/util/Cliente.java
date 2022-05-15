package com.example.chessforandroid.util;

import static com.example.chessforandroid.util.Constantes.debug;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

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
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Cliente {

    private Socket conn; // socket con la conexión
    private DataInputStream in; // flujo de entrada
    private DataOutputStream out; // flujo de salida
    private static String host = Constantes.ip; // dirección IP (local)
    private static int puerto = Constantes.puerto; // puerto que se utilizará
    private boolean conectado;
    private String user;
    private String token;
    private Context context;


    // constructor
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
            Log.i("*****", "me conecto a " + host);
            in = new DataInputStream(conn.getInputStream());
            out = new DataOutputStream(conn.getOutputStream());
            conectado = true;

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            conectado = false;
            e.printStackTrace();
        }
    }

    public void cerrarConexion() {
        try {
            conn.close();
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void registrarse(Context context, String user, String pass) {
        this.context = context;
        new Registro().execute(user, pass);
    }

    public void iniciarSesion(Context context, String user, String pass) {
        this.context = context;
        new InicioSesion().execute(user, pass);
    }

    public void cambiarPass(Context context, String user, String oldPass, String newPass) {
        this.context = context;
        new CambiarPass().execute(user, oldPass, newPass);
    }

    public void pedirDatos(Context context, String user) {
        this.context = context;
        new PedirDatos().execute(user);
    }

    public void crearSala(Context context, String token) {
        this.context = context;
        new CrearSala().execute(token);
    }

    public void unirse(Context context, String token, String codigo) {
        this.context = context;
        new Unirse().execute(token, codigo);
    }


    public void local(Context context, OnlineWaitingActivity lla, String token) {
        this.context = context;
        new Online(lla).execute(token);
    }

    public void esperarMov(Context context, OnlineActivity ga) {
        this.context = context;
        new EsperarMov(ga).execute();
    }

    public void enviarMov(Context context, OnlineActivity ga, String casillas, String movs) {
        this.context = context;
        new EnviarMov(ga).execute(casillas, movs);
    }


    public void ofrecerTablas(Context context, OnlineActivity ga) {
        this.context = context;
        new EnviarTablas(ga).execute();
    }

    public void enviarMensaje(String... s) {
        new EnviarMensaje().execute(s);
    }

    public void abandonar() {
        enviarMensaje("abandonar");
    }

    public void getRanking(RankingActivity ra, String pref) {
        new Ranking(ra).execute(pref);
    }


    public class Ranking extends AsyncTask<String, Void, ArrayList<RankingItem>> {

        private RankingActivity ra;

        public Ranking(RankingActivity ra) {
            this.ra = ra;
        }

        @Override
        protected ArrayList<RankingItem> doInBackground(String... strings) {
            ArrayList<RankingItem> datos = new ArrayList<>();
            try {
                out.writeUTF(strings[0]);
                int numUsuarios = in.readInt();
                for (int i = 0; i < numUsuarios; i++) {
                    RankingItem ri = new RankingItem();
                    ri.setPosition(i + 1);
                    ri.setUser(in.readUTF());
                    datos.add(ri);
                }
                for (int i = 0; i < numUsuarios; i++) {
                    RankingItem ri = datos.get(i);
                    ri.setElo(in.readUTF());
                }
                return datos;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<RankingItem> datos) {
            ra.alRecibirDatos(datos);
        }
    }

    public class EnviarMensaje extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                out.writeUTF(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return strings[0];
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equalsIgnoreCase("abandonar")) {
                //cerrarConexion();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class EnviarTablas extends AsyncTask<Void, Void, Boolean> {

        OnlineActivity caller;

        EnviarTablas(OnlineActivity caller) {
            this.caller = caller;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                out.writeUTF("tablas");
                return in.readBoolean();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            caller.trasOfrecerTablas(b);
        }
    }


    public class EnviarMov extends AsyncTask<String, Void, Object[]> {

        OnlineActivity caller;

        EnviarMov(OnlineActivity caller) {
            this.caller = caller;
        }

        @Override
        protected Object[] doInBackground(String... strings) {
            Object[] objects = new Object[3];
            try {
                //envío las casillas
                out.writeUTF(strings[0]);

                if (strings[0].equalsIgnoreCase("rendirse") ||
                        strings[0].equalsIgnoreCase("tablas")) {
                    return objects;
                }

                //envío movs
                out.writeUTF(strings[1]);
                //es jaque mate?
                Boolean b = in.readBoolean();
                objects[0] = b;
                if (b) {
                    return objects;
                }
                //es jaque?
                objects[1] = in.readBoolean();
                //puede mover?
                objects[2] = in.readBoolean();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return objects;
        }

        @Override
        protected void onPostExecute(Object[] objects) {
            super.onPostExecute(objects);
            caller.trasEnviarMov(objects);
        }
    }


    public class EsperarMov extends AsyncTask<Void, Void, Object[]> {

        OnlineActivity caller;

        EsperarMov(OnlineActivity caller) {
            this.caller = caller;
        }

        @Override
        protected Object[] doInBackground(Void... voids) {
            Object[] objects = new Object[5];
            try {
                //tablero
                String s = in.readUTF();
                objects[0] = s;
                if (s.equalsIgnoreCase("rendirse") ||
                        s.equalsIgnoreCase("tablas") ||
                        s.equalsIgnoreCase("tiempo")) {
                    return objects;
                }
                //movs
                objects[1] = in.readUTF();
                //es jaque mate?
                Boolean b = in.readBoolean();
                objects[2] = b;
                if (b) {
                    return objects;
                }
                //es jaque?
                objects[3] = in.readBoolean();
                //puede mover?
                objects[4] = in.readBoolean();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return objects;
        }

        @Override
        protected void onPostExecute(Object[] objects) {
            super.onPostExecute(objects);
            caller.trasRecibirMov(objects);
        }

    }


    public Object[] getDatosIniciales(Context context, String token) {
        this.context = context;
        this.token = token;
        Object[] o = null;
        try {
            o = new DatosIniciales().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return o;
    }

    public class DatosIniciales extends AsyncTask<Object, Void, Object[]> {


        protected Object[] doInBackground(Object... params) {

            Object[] o = new Object[3];
            String s = "ok";
            try {
                // recibo mi nombre
                o[0] = in.readUTF();
                // recibo el nombre del rival
                o[1] = in.readUTF();
                // recibo si es mi turno
                o[2] = in.readBoolean();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return o;
        }
    }


    public class Online extends AsyncTask<String, Void, String> {

        OnlineWaitingActivity lla;

        public Online(OnlineWaitingActivity lla) {
            this.lla = lla;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                token = strings[0];
                out.writeUTF("online");
                out.writeUTF(token);
                return in.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s == null) {
                Log.e("**********", "Clase Online recibe null en onPostExecute");
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
                Intent localIntent = new Intent(context, OnlineActivity.class);
                localIntent.putExtra("token", token);
                context.startActivity(localIntent);
            }
            lla.terminar();
            cerrarConexion();
        }
    }

    public class Unirse extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            try {
                Log.i("**", "entro en unirse. El token vale " + strings[0] +
                        " y el id " + Integer.parseInt(strings[1]));
                token = strings[0];
                out.writeUTF("unirse");
                out.writeUTF(token);
                out.writeInt(Integer.parseInt(strings[1]));
                return in.readInt();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return -3;
        }

        @Override
        protected void onPostExecute(Integer res) {
            super.onPostExecute(res);

            Toast.makeText(context, "Listo para jugar", Toast.LENGTH_SHORT).show();
            cerrarConexion();
        }
    }

    public class CrearSala extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            try {
                token = strings[0];
                out.writeUTF("crearsala");
                out.writeUTF(token);
                return in.readInt();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return -3;
        }

        @Override
        protected void onPostExecute(Integer res) {
            super.onPostExecute(res);

            Intent lobbyIntent = new Intent(context, FriendWaitingActivity.class);
            lobbyIntent.putExtra("id", res);
            lobbyIntent.putExtra("token", token);
            context.startActivity(lobbyIntent);
            cerrarConexion();
        }
    }


    public class Registro extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            try {
                user = strings[0];
                out.writeUTF("signup");
                out.writeUTF(strings[0]);
                out.writeUTF(strings[1]);
                return in.readInt();
            } catch (IOException e) {
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
                        e.printStackTrace();
                    }
                    break;
                default:
                    Log.e("**", "Error de conexión con la base de datos");
                    break;
            }
            cerrarConexion();
        }
    }

    public class CambiarPass extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                user = strings[0];
                out.writeUTF("cambiarpass");
                //envio el user
                out.writeUTF(strings[0]);
                //enviar la pass vieja
                out.writeUTF(strings[1]);
                //enviar la pass nueva
                out.writeUTF(strings[2]);
                return in.readBoolean();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (b) {
                Toast.makeText(context, "Contraseña cambiada correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Nombre de usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
            cerrarConexion();
        }
    }

    public class InicioSesion extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                user = strings[0];
                out.writeUTF("login");
                out.writeUTF(strings[0]);
                out.writeUTF(strings[1]);
                if (in.readBoolean()) {
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

    public class PedirDatos extends AsyncTask<String, Void, Integer[]> {

        @Override
        protected Integer[] doInBackground(String... strings) {

            Integer[] res = new Integer[6];
            for (int i = 0; i < res.length; i++) {
                res[i] = 0;
            }
            try {
                user = strings[0];
                out.writeUTF("pedirdatos");
                out.writeUTF(user);

                res[0] = in.readInt();
                res[1] = in.readInt();
                res[2] = in.readInt();
                res[3] = in.readInt();
                res[4] = in.readInt();
                res[5] = in.readInt();
                return res;
            } catch (IOException e) {
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
            i.putExtra("datos", datos);
            cerrarConexion();
            context.startActivity(i);
        }
    }

    public boolean isConectado() {
        return conectado;
    }
}