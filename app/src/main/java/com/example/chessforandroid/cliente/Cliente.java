package com.example.chessforandroid.cliente;

import android.os.StrictMode;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Cliente {

    private Socket conn; // socket con la conexión
    private DataInputStream in; // flujo de entrada
    private DataOutputStream out; // flujo de salida
    private final int PUERTO = 5555; // puerto que se utilizará
    private final String HOST = "192.168.1.144"; // dirección IP (local)

    // constructor
    public Cliente() {
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            // inicializamos el socket, dis y dos
            conn = new Socket(HOST, PUERTO);
            in = new DataInputStream(conn.getInputStream());
            out = new DataOutputStream(conn.getOutputStream());

        } catch (UnknownHostException e) {
            e.printStackTrace();
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
                        Log.i("****", "el usuario es " + user + " y la pass " + pass);
                        ret[0] = in.readInt();
                        Log.i("**", "valor intermedio: " + ret[0]);
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
                        Log.i("****", "el usuario es " + user + " y la pass " + pass);
                        res[0] = in.readBoolean();
                        Log.i("**", "valor intermedio: " + res[0]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        Log.i("**", "valor final: " + res[0]);
        while (res[0] == null) {
        }
        return res[0];
    }
}