package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


import util.DB;

/**
 * Clase Servidor.
 */
public class Servidor {
    private static final Logger logger = Logger.getLogger(Servidor.class.getName());

    /**
     * ServerSocket que escucha la llegada de nuevos clientes.
     */
    private ServerSocket ss;

    /**
     *
     */
    public static int jugadores = 0;

    /**
     *
     */
    public static ArrayList<Socket> conexiones;

    /**
     *
     */
    public static ArrayList<Lobby> lobbies;

    public Servidor() {
        conexiones = new ArrayList<>();
        lobbies = new ArrayList<>();
        try {
            DB.conectar();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "No se ha podido conectar con la base de datos.");
            e.printStackTrace();
            System.exit(-1);
        }

        try {
            // se inicializa el serversocket.
            ss = new ServerSocket(5566);
            logger.log(Level.INFO, "El servidor se ha iniciado. Esperando jugadores...");


            while (true) {
                // se crea un socket que espera a que llegue un cliente.
                Socket cliente = ss.accept();
                conexiones.add(cliente);
                // se lanza un hilo de la clase ClientHandler que gestiona la conexi�n de manera
                // independiente a esta clase.
                new ClientHandler(ss, cliente).start();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al iniciar el servidor.");
            e.printStackTrace();
        } finally {
            try {
                DB.desconectar();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Metodo principal.
     *
     * @param args
     */
    public static void main(String[] args) {
        new Servidor();
    }
}