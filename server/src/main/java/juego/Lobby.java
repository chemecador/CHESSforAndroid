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
                        logger.error("No se ha podido enviar al anfitrion el mensaje \"norivales\"", e);
                        //TODO: CERRAR LA CONEXION ??
                    }
                    Servidor.lobbies.remove(Lobby.this);
                    Servidor.jugadores--;

                    logger.info("Hay {} jugadores en cola", Servidor.jugadores);
                    logger.info("Hay {} lobbies en cola", Servidor.lobbies.size());
                    this.cancel();
                }
            }
        };
        t.scheduleAtFixedRate(task, 1000, 1000);
    }

    public void setJugador(Jugador invitado) {
        this.invitado = invitado;
        hayRival = true;

        try {
            anfitrion.enviarString("jugar");
            anfitrion.setSocket(ss.accept());

            invitado.enviarString("jugar");
            invitado.setSocket(ss.accept());
        } catch (IOException e) {
            logger.error("Error al recibir confirmacion de alguno de los jugadores", e); // TODO: o el primero? mejorar mensaje de error,
        }

        if (!anfitrion.getSocket().isClosed() && anfitrion.getSocket().isConnected()
                && !invitado.getSocket().isClosed() && invitado.getSocket().isConnected()) {
            logger.info("Nueva partida comenzada entre {} y {}", anfitrion.getUser(), invitado.getUser());
            new Partida(anfitrion, invitado);
        } else {
            logger.error("Alguien ha muerto por el camino"); // TODO: Comentario un poco KEKW, ERROR? WARNING? Que hacer
                                                            // ahora?
        }
    }
}
