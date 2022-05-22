package juego;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;

import servidor.Servidor;

/**
 * Lobby que gestiona una partida amistosa
 */
public class FriendLobby {
    private static final Logger logger = LogManager.getLogger();

    private Jugador anfitrion;
    private Jugador invitado;
    private int codigo;

    public FriendLobby(Jugador anfitrion, int codigo) {
        this.anfitrion = anfitrion;
        this.codigo = codigo;
        logger.debug("El anfitrion {} se encuentra esperando en el lobby", anfitrion.getUser());
    }


    /**
     * Metodo que se encarga de establecer el segundo jugador (invitado)
     * @param j2 Nuevo jugador
     * @throws IOException IOException
     */
    public void setJugador(Jugador j2) throws IOException {
        this.invitado = j2;

        logger.debug("Lobby creado con anfitrion {} e invitado {}",
                anfitrion.getUser(), invitado.getUser());

        if (anfitrion == null) {
            invitado.enviarString("abortada");
            logger.error("Anfitrion es null, partida abortada");
            Servidor.friendLobbies.remove(this);
            return;
        }
        if (invitado == null) {
            anfitrion.enviarString("abortada");
            logger.error("Invitado es null, partida abortada");
            Servidor.friendLobbies.remove(this);
            return;
        }

        try {
            anfitrion.enviarString("jugar");
            logger.debug("enviado jugar a {}", anfitrion.getUser());
        } catch (IOException e) {
            invitado.enviarString("abortada");
            throw new IOException("Error al recibir confirmacion del anfitrion", e);
        }
        try {
            invitado.enviarString("jugar");
            logger.debug("enviado jugar a {}", invitado.getUser());
        } catch (IOException e) {
            anfitrion.enviarString("abortada");
            throw new IOException("Error al recibir confirmacion del invitado", e);
        }
        new Partida(anfitrion, invitado, codigo);
    }

    public int getCodigo() {
        return this.codigo;
    }
}
