package servidor;

import casillas.Casilla;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Clase Servidor
 */
public class Servidor {

    private ServerSocket ss; //ServerSocket que escucha la llegada de nuevos clientes
    public static int jugadores = 0;
    public static ArrayList<Socket> conexiones;
    public static ArrayList<Local> locales;

    //constructor
    public Servidor() {
        try {
            //se inicializa el serversocket
            ss = new ServerSocket(5566);
            conexiones = new ArrayList<>();
            locales = new ArrayList<>();
            System.out.println("El servidor se ha iniciado. Esperando jugadores...");
            while (true) {
                //se crea un socket que espera a que llegue un cliente
                Socket cliente = ss.accept();
                conexiones.add(cliente);
                //se lanza un hilo de la clase ClientHandler que gestiona la conexión de manera independiente a esta clase
                new ClientHandler(ss, cliente).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void cerrarConexiones() {
        for (Socket soc : conexiones) {
            try {
                soc.close();
            } catch (IOException e) {
                System.err.println("Error al cerrar la conexión");
                e.printStackTrace();
            }
        }
    }

    /**
     * Método principal
     *
     * @param args
     */
    public static void main(String[] args) {

        new Servidor();
    }

}