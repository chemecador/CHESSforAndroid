package juego;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

import servidor.Servidor;
import db.DB;

public class Lobby extends Thread {
    private DataOutputStream out1; // flujo de salida de datos con el jugador 1 (a partir de ahora, J1)
    private DataOutputStream out2;// flujo de salida con el jugador 2 (a partir de ahora, J2)
    private DataInputStream in1; // flujo de entrada de datos con J1
    private DataInputStream in2; // flujo de entrada con J1
    private Socket[] jugadores; // lista de jugadores
    private int id1;
    private int id2;
    private boolean turno;
    public static boolean hayRival;

    public Lobby(Socket j1) {
        try {
            jugadores = new Socket[2];
            // se inicializan los flujos de entrada y salida
            in1 = new DataInputStream(j1.getInputStream());
            out1 = new DataOutputStream(j1.getOutputStream());
            hayRival = false;


            String t1 = in1.readUTF();
            id1 = DB.getIdFromToken(t1);

            // se suma el jugador al arraylist
            jugadores[0] = j1;


            Timer t = new Timer();
            TimerTask task = new TimerTask() {
                int segundos = 0;

                @Override
                public void run() {
                    segundos++;
                    if (hayRival)
                        this.cancel();
                    if (segundos == 5) {
                        try {
                            out1.writeUTF("norivales");
                            Servidor.lobbies.remove(Lobby.this);
                            Servidor.jugadores--;
                            System.out.println("Ahora hay " + Servidor.jugadores + " jugadores en cola");
                            System.out.println("Ahora hay " + Servidor.lobbies.size() + " lobbies en cola");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        this.cancel();
                    }
                }
            };
            t.scheduleAtFixedRate(task, 1000, 1000);
        } catch (SocketException se) {
            System.out.println("Conexion con el cliente cerrada.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setJugador(Socket j2) {
        try {
            in2 = new DataInputStream(j2.getInputStream());
            out2 = new DataOutputStream(j2.getOutputStream());
            jugadores[1] = j2;


            String t2 = in2.readUTF();
            id2 = DB.getIdFromToken(t2);
            hayRival = true;
            out2.writeUTF("jugar");
            out1.writeUTF("jugar");

        } catch (IOException e) {
            System.out.println("Error al introducir el segundo jugador");
            e.printStackTrace();
        }
    }
}
