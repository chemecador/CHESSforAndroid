package servidor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import juego.Parametros;

/**
 * Clase SocketHandler. Se encarga de entregar un socket a la clase que se lo pide.
 */
public class SocketHandler {
    private static final Logger logger = LogManager.getLogger();

    /**
     * ServerSocket del que se aceptaran los sockets.
     */
    private static ServerSocket ss;

    public SocketHandler(int puerto) throws IOException {
        ss = new ServerSocket(puerto);
    }

    public static Socket getSocket() {
        try {
            return ss.accept();
        } catch (IOException e) {

            logger.error("Error al recibir nuevo socket de jugador");
            e.printStackTrace();
        }
        return null;
    }
}
