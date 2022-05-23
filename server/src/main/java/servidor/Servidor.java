package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;

import juego.FriendLobby;
import juego.Lobby;
import db.DB;
import juego.Parametros;

/**
 * Clase Servidor.
 */
public class Servidor {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Lobby de partida online
     */
    public static Lobby lobby;

    /**
     * ArrayList de Lobbies de partidas entre amigos.
     * A diferencia de las partidas online, aqui si se debe guardar en un ArrayList para poder
     * acceder a la partida con un codigo en concreto
     */
    public static ArrayList<FriendLobby> friendLobbies;

    public Servidor() {
        ServerSocket ss = null;

        friendLobbies = new ArrayList<>();


        try {
            DB.conectar();
        } catch (SQLException e) {
            logger.fatal("Error al conectarse con la base de datos", e);
            System.exit(-1);
        }

        try {
            ss = new ServerSocket(Parametros.PUERTO);
            new SocketHandler(Parametros.PUERTO_PARTIDA);
        } catch (Exception e) {
            logger.fatal("Error al inicializar el socket de escucha en el puerto '{}'", Parametros.PUERTO, e);
            System.exit(-1);
        }
        logger.info("Servidor iniciado y listo para recibir conexiones.");

        while (true) {
            try {
                Socket cliente = ss.accept();

                // se lanza un hilo de la clase ClientHandler que gestiona la conexion de manera
                // independiente a esta clase.
                new ClientHandler(cliente).start();

            } catch (IOException e) {
                logger.error("Se ha producido un error al aceptar una conexion", e);
            }
        }
    }

    /**
     * Metodo principal.
     *
     * @param args
     */
    public static void main(String[] args) {
        Configurator.setRootLevel(Level.INFO);
        new Servidor();
    }
}