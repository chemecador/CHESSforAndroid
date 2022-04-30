package com.example.chessforandroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.example.chessforandroid.util.Constantes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
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
            conn.connect(new InetSocketAddress(host, puerto), 1200);
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


    public void local(Context context, String token) {
        this.context = context;
        new Local().execute(token);
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

    public class DatosIniciales extends AsyncTask<Object, Void, Object[]>{


        protected Object[] doInBackground(Object... params) {

            Object[] o = new Object[3];
            String s = "ok";
            try {
                out.writeUTF(token);

                o[0] = in.readUTF();
                o[1] = in.readUTF();
                o[2] = in.readBoolean();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return o;
        }
    }


    public class Local extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                token = strings[0];
                out.writeUTF("local");
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

            if (s != null) {
                Intent localIntent = new Intent(context, GameActivity.class);
                localIntent.putExtra("token", token);
                context.startActivity(localIntent);
            }
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

            Intent lobbyIntent = new Intent(context, LobbyActivity.class);
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
            Intent i = new Intent(context, PerfilActivity.class);
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