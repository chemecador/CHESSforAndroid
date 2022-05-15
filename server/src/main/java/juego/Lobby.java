package juego;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import servidor.Servidor;

public class Lobby extends Thread {
    private static final Logger logger = LogManager.getLogger();

    private ServerSocket ss; // TODO: 😑

    public static boolean hayRival;
    private final int TIEMPO_ESPERA_RIVAL = 10;

    private Jugador anfitrion;
    private Jugador invitado;

    public Lobby(ServerSocket ss, Jugador anfitrion) {
        this.ss = ss;
        this.anfitrion = anfitrion;
        hayRival = false;

        Timer t = new Timer();
        TimerTask task = new TimerTask() {
            int segundos = 0;

            @Override
            public void run() {
                segundos++;
                if (hayRival)
                    this.cancel();
                if (segundos == TIEMPO_ESPERA_RIVAL) {
                    try {
                        anfitrion.enviarString("norivales");
                    } catch (IOException e) {
                        logger.error("No se ha podido enviar al anfitrion el mensaje 'norivales'", e);
                    }
                    Parametros.NUM_JUGADORES--;

                    logger.info("Hay {} jugadores en cola", Parametros.NUM_JUGADORES);
                    this.cancel();
                }
            }
        };
        t.scheduleAtFixedRate(task, 1000, 1000);
    }

    public void setJugador(Jugador invitado) throws IOException {
        this.invitado = invitado;
        hayRival = true;

        logger.info("Lobby creado con anfitrion {} e invitado {}",
                anfitrion.getUser(), invitado.getUser());

        try {
            anfitrion.enviarString("jugar");
            anfitrion.setSocket(ss.accept());
        } catch (IOException e) {
            invitado.enviarString("abortada");
            throw new IOException("Error al recibir confirmacion del anfitrion", e);
        }
        try {
            invitado.enviarString("jugar");
            invitado.setSocket(ss.accept());
        } catch (IOException e) {
            anfitrion.enviarString("abortada");
            throw new IOException("Error al recibir confirmacion del invitado", e);
        }

        new Partida(anfitrion, invitado);
    }
}
