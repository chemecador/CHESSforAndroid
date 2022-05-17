package juego;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Timer;
import java.util.TimerTask;

public class FriendLobby extends Thread {
    private static final Logger logger = LogManager.getLogger();

    private ServerSocket ss;

    public static boolean hayRival;

    private Jugador anfitrion;
    private Jugador invitado;

    public FriendLobby(ServerSocket ss, int idAnfitrion) throws IOException {
        this.ss = ss;
        this.anfitrion = new Jugador(ss.accept(), idAnfitrion);

        logger.info("El anfitrion {} se encuentra esperando en el lobby", anfitrion.getUser());
    }

    public void setJugador(Jugador invitado) throws IOException {
        this.invitado = invitado;

        logger.info("Lobby creado con anfitrion {} e invitado {}",
                anfitrion.getUser(), invitado.getUser());

        if (anfitrion == null) {
            Parametros.NUM_AMIGOS = 0;
            invitado.enviarString("abortada");
            logger.error("Anfitrion es null, partida abortada");
        }

        try {
            anfitrion.enviarString("jugar");
            anfitrion.setSocket(ss.accept());
            logger.debug("enviado jugar a {}", anfitrion.getUser());
        } catch (IOException e) {
            Parametros.NUM_AMIGOS = 0;
            invitado.enviarString("abortada");
            throw new IOException("Error al recibir confirmacion del anfitrion", e);
        }
        try {
            invitado.enviarString("jugar");
            invitado.setSocket(ss.accept());
            logger.debug("enviado jugar a {}", invitado.getUser());
        } catch (IOException e) {
            anfitrion.enviarString("abortada");
            Parametros.NUM_AMIGOS = 0;
            throw new IOException("Error al recibir confirmacion del invitado", e);
        }

        new Partida(anfitrion, invitado);
    }
}
