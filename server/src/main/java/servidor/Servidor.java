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

/**
 * Clase Servidor.
 */
public class Servidor {
    private static final Logger logger = LogManager.getLogger();
    /**
     * TODO: Comentario
     */
    public static ArrayList<Socket> conexiones;

    public static Lobby lobby;
    public static ArrayList<FriendLobby> friendLobbies;

    public Servidor() {
        ServerSocket ss = null;
        int port = 5566;

        conexiones = new ArrayList<>();
        friendLobbies = new ArrayList<>();


        try {
            DB.conectar();
        } catch (SQLException e) {
            logger.fatal("Error al conectarse con la base de datos", e);
            System.exit(-1);
        }

        try {
            ss = new ServerSocket(port);
        } catch (Exception e) {
            logger.fatal("Error al inicializar el socket de escucha en el puerto '{}'", port, e);
            System.exit(-1);
        }
        logger.info("Servidor iniciado y listo para recibir conexiones.");

        while (true) {
            try {
                Socket cliente = ss.accept();
                logger.debug("Nuevo cliente recibido: {}:{}",
                        cliente.getInetAddress().getHostAddress(), cliente.getPort());

                // TODO: comentario
                conexiones.add(cliente);

                // se lanza un hilo de la clase ClientHandler que gestiona la conexion de manera
                // independiente a esta clase.
                new ClientHandler(cliente).start();

            } catch (IOException e) {
                logger.error("Se ha producido un error al aceptar una conexion", e);
            }
        }
        /**
         * TODO: cerrar la conexion con la bbdd
         *
         * try {
         *             DB.desconectar();
         *         } catch (SQLException e) {
         *             logger.fatal("Error al cerrar la conexion con la base de datos", e);
         *         }
         */

    }


    /**
     * Metodo principal.
     *
     * @param args
     */
    public static void main(String[] args) {
        Configurator.setRootLevel(Level.DEBUG);
        new Servidor();
    }
}