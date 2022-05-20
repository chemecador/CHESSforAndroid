package servidor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketHandler {
    private static final Logger logger = LogManager.getLogger();

    private static ServerSocket ss;

    public SocketHandler() throws IOException {
        int port = 5567;
        ss = new ServerSocket(port);
    }

    public static Socket getSocket(){
        try {
            return ss.accept();
        } catch (IOException e) {
            logger.error("Error al recibir nuevo socket de jugador");
            e.printStackTrace();
        }
        return null;
    }
}
