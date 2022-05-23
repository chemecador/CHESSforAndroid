package juego;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Timer;
import java.util.TimerTask;

import servidor.Servidor;

/**
 * Lobby que gestiona una partida amistosa
 */
public class FriendLobby {
    private static final Logger logger = LogManager.getLogger();

    private Jugador anfitrion;
    private Jugador invitado;
    public static boolean hayRival;
    private int codigo;

    public FriendLobby(Jugador anfitrion, int codigo) {
        this.anfitrion = anfitrion;
        this.codigo = codigo;
        logger.debug("El anfitrion {} se encuentra esperando en el lobby", anfitrion.getUser());

        hayRival = false;

        Timer t = new Timer();
        TimerTask task = new TimerTask() {
            int segundos = 0;

            @Override
            public void run() {
                // cada vez que se ejecuta la tarea (cada segundo), se suma un segundo
                segundos++;

                if (hayRival) {
                    logger.debug("El jugador {} ya tiene rival: {}. Se elimina la espera",
                            anfitrion.getUser(), invitado.getUser());
                    // si hay rival antes de que acabe el tiempo, se cancela la tarea
                    this.cancel();
                }

                // si se ha alcanzado el tiempo maximo de espera
                if (segundos == Parametros.TIEMPO_ESPERA_AMIGO) {
                    // se elimina el lobby del servidor
                    Servidor.friendLobbies.remove(FriendLobby.this);

                    logger.info("El jugador {} no ha encontrado rivales", anfitrion.getUser());

                    try {
                        // se envia al jugador que no hay rivales disponibles
                        anfitrion.enviarString("norivales");

                    } catch (IOException e) {
                        logger.error("No se ha podido enviar al anfitrion el mensaje 'norivales'", e);
                    }
                    logger.info("Ahora hay {} lobbies en total", Servidor.friendLobbies.size());
                    // se cancela la tarea
                    this.cancel();
                }
            }
        };
        // ejecuta la tarea cada segundo
        t.scheduleAtFixedRate(task, 1000, 1000);

    }


    /**
     * Metodo que se encarga de establecer el segundo jugador (invitado)
     *
     * @param j2 Nuevo jugador
     * @throws IOException IOException
     */
    public void setJugador(Jugador j2) throws IOException {
        this.invitado = j2;
        hayRival = true;

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
