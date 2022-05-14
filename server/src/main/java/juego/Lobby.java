package juego;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Timer;
import java.util.TimerTask;

import servidor.Servidor;
import db.DB;

public class Lobby extends Thread {

    private ServerSocket ss;
    private Jugador[] jugadores; // lista de jugadores
    public static boolean hayRival;

    public Lobby(ServerSocket ss, Jugador j1) {

        this.ss = ss;
        jugadores = new Jugador[2];

        hayRival = false;

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
                        jugadores[0].enviarString("norivales");
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
    }

    public void setJugador(Jugador j2) {
        try {
            jugadores[1] = j2;
            hayRival = true;
            jugadores[0].enviarString("jugar");
            jugadores[1].enviarString("jugar");
            jugadores[0].setSocket(ss.accept());
            jugadores[1].setSocket(ss.accept());
            if (!jugadores[0].getSocket().isClosed() && jugadores[0].getSocket().isConnected() &&
                    !jugadores[1].getSocket().isClosed() && jugadores[1].getSocket().isConnected()) {
                System.out.println("Nueva partida comenzada entre " + jugadores[0].getUser() + " y " +
                        jugadores[1].getUser());
                new Partida(jugadores[0], jugadores[1]);
            } else {
                System.out.println("Alguien ha muerto por el camino");
            }
        } catch (IOException e) {
            System.out.println("Error al introducir el segundo jugador");
            e.printStackTrace();
        }
    }
}
