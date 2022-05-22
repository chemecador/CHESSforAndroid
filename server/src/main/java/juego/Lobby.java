package juego;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import servidor.Servidor;

/**
 * Metodo que se encarga de gestionar el lobby de las partidas online
 */
public class Lobby {
    private static final Logger logger = LogManager.getLogger();


    public static boolean hayRival;

    private Jugador anfitrion;
    private Jugador invitado;

    public Lobby(Jugador anfitrion) {
        this.anfitrion = anfitrion;
        hayRival = false;

        Timer t = new Timer();
        TimerTask task = new TimerTask() {
            int segundos = 0;

            @Override
            public void run() {
                // cada vez que se ejecuta la tarea (cada segundo), se suma un segundo
                segundos++;

                if (hayRival) {
                    // si hay rival antes de que acabe el tiempo, se cancela la tarea
                    this.cancel();
                }

                // si se ha alcanzado el tiempo maximo de espera
                if (segundos == Parametros.TIEMPO_ESPERA_RIVAL) {
                    try {
                        // se envia al jugador que no hay rivales disponibles
                        anfitrion.enviarString("norivales");

                    } catch (IOException e) {

                        logger.error("No se ha podido enviar al anfitrion el mensaje 'norivales'", e);
                    }
                    // ya no hay jugadores esperando partida
                    Parametros.NUM_JUGADORES = 0;

                    logger.info("El jugador {} no ha encontrado rivales", anfitrion.getUser());
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
     * @param j2 Nuevo jugador
     * @throws IOException IOException
     */
    public void setJugador(Jugador j2) throws IOException {

        this.invitado = j2;
        hayRival = true;

        logger.debug("Lobby creado con anfitrion {} e invitado {}",
                anfitrion.getUser(), invitado.getUser());

        if (anfitrion == null) {
            Parametros.NUM_JUGADORES = 0;
            invitado.enviarString("abortada");
            logger.error("Anfitrion es null, partida abortada");
        }

        if (invitado == null) {
            Parametros.NUM_JUGADORES = 0;
            anfitrion.enviarString("abortada");
            logger.error("Invitado es null, partida abortada");
        }

        try {

            anfitrion.enviarString("jugar");
        } catch (IOException e) {
            Parametros.NUM_JUGADORES = 0;
            invitado.enviarString("abortada");
            throw new IOException("Error al recibir confirmacion del anfitrion", e);
        }
        try {

            invitado.enviarString("jugar");
        } catch (IOException e) {
            anfitrion.enviarString("abortada");
            Parametros.NUM_JUGADORES = 0;
            throw new IOException("Error al recibir confirmacion del invitado", e);
        }

        new Partida(anfitrion, invitado, 0);
    }
}
