package com.example.chessforandroid.cliente;

import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Cliente {

    private Socket conn; // socket con la conexión
    private DataInputStream in; // flujo de entrada
    private DataOutputStream out; // flujo de salida
    private final int PUERTO = 5555; // puerto que se utilizará
    private final String HOST = "192.168.1.144"; // dirección IP (local)
    private boolean conectado;

    // constructor
    public Cliente() {
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            // inicializamos el socket, dis y dos
            //conn = new Socket(HOST, PUERTO);
            conn = new Socket();
            conn.connect(new InetSocketAddress(HOST, PUERTO), 1500);
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

    public void cerrarConexion(){
        try {
            conn.close();
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public int registrarse(String user, String pass) {
        int[] ret = {-3};
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    try {
                        out.writeUTF("signup");
                        out.writeUTF(user);
                        out.writeUTF(pass);
                        ret[0] = in.readInt();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        Log.i("**", "valor final: " + ret[0]);
        while (ret[0] == -3) {
        }
        return ret[0];
    }

    public boolean iniciarSesion(String user, String pass) {
        Boolean[] res = {null};
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    try {
                        out.writeUTF("login");
                        out.writeUTF(user);
                        out.writeUTF(pass);
                        res[0] = in.readBoolean();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        while (res[0] == null) {
        }
        return res[0];
    }

    public int[] pedirDatos(String user) {
        int[] res = new int[6];
        for (int i = 0; i < res.length; i++){
            res[i]=5;
        }
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                        out.writeUTF("pedirdatos");
                        out.writeUTF(user);

                        res[0] = in.readInt();
                        res[1] = in.readInt();
                        res[2] = in.readInt();
                        res[3] = in.readInt();
                        res[4] = in.readInt();
                        res[5] = in.readInt();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        });
        thread.start();
        while (res[4] > 3) {
        }
        return res;
    }

    public boolean isConectado() {
        return conectado;
    }

    public void setConectado(boolean conectado) {
        this.conectado = conectado;
    }
}