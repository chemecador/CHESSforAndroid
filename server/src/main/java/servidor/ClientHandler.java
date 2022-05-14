package servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import juego.Jugador;
import juego.Lobby;
import juego.Partida;
import db.DB;
import util.Hash;

public class ClientHandler extends Thread {

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private ServerSocket ss;
    private DataOutputStream out;
    private DataInputStream in;
    private Socket socket;


    public ClientHandler(ServerSocket ss, Socket cliente) {
        this.socket = cliente;
        this.ss = ss;
        try {
            out = new DataOutputStream(cliente.getOutputStream());
            in = new DataInputStream(cliente.getInputStream());
            String peticion = in.readUTF();
            logger.log(Level.INFO, "Peticion de " + peticion + " recibida");
            peticion(peticion);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void peticion(String peticion) throws SQLException, IOException {
        switch (peticion) {
            case "signup":
                registro();
                cerrarConexion();
                break;
            case "login":
                inicioSesion();
                cerrarConexion();
                break;
            case "cambiarpass":
                out.writeBoolean(cambiarPass());
                cerrarConexion();
                break;
            case "pedirdatos":
                pedirDatos();
                cerrarConexion();
                break;
            case "online":
                jugarOnline();
                break;
            case "crearsala":
                crearSala();
                break;
            case "unirse":
                unirse();
                cerrarConexion();
                break;
            case "ranking":
                ranking();
                cerrarConexion();
                break;
        }
    }

    private void ranking() throws IOException {
        ArrayList<String> users = DB.getRankingUsers();
        ArrayList<String> elos = DB.getRankingElos();
        int numDatos = Math.min(users.size(), elos.size());
        if (users == null || elos == null){
            out.writeInt(0);
            return;
        }
        out.writeInt(numDatos);
        for (int i = 0; i < numDatos; i++){
            out.writeUTF(users.get(i));
        }
        for (int i = 0; i < numDatos; i++){
            out.writeUTF(elos.get(i));
        }
    }

    public void cerrarConexion() throws IOException {
        socket.close();
    }


    private void jugarOnline() throws IOException {
        Jugador j1 = null;
        Jugador j2 = null;
        Servidor.jugadores++;
        System.out.println("Ahora hay " + Servidor.jugadores + " jugadores ");
        if (Servidor.jugadores % 2 != 0) {
            j1 = new Jugador(socket);
            Lobby l = new Lobby(j1);
            l.start();
            Servidor.lobbies.add(l);
        } else {
            Lobby lo = Servidor.lobbies.get(Servidor.lobbies.size() - 1);
            j2 = new Jugador(socket);
            lo.setJugador(j2);
            System.out.println("listo para jugar");
            if (j1 != null && j2 != null) {
                new Partida(j1, j2);
                return;
            }
            if (j1 == null){
                //loggear
                Servidor.jugadores--;
                j2.enviarString("abortada");
            }
            if (j2 == null){
                //loggear
                Servidor.jugadores--;
                j1.enviarString("abortada");
            }
        }
    }

    private boolean crearSala() throws IOException, SQLException {
        int idAnfitrion = DB.getIdFromToken(in.readUTF());
        int codigo;
        String AB = "0123456789";
        int size = 4;
        do {
            SecureRandom rnd = new SecureRandom();
            StringBuilder sb = new StringBuilder(size);
            for (int i = 0; i < size; i++)
                sb.append(AB.charAt(rnd.nextInt(AB.length())));

            codigo = Integer.parseInt(sb.toString());
        } while (DB.existeCodigo(codigo));

        DB.crearIDAnfitiron(idAnfitrion, codigo);
        out.writeInt(codigo);


        return true;

    }

    private boolean unirse() throws IOException, SQLException {
        int idInvitado = DB.getIdFromToken(in.readUTF());
        int codigo = in.readInt();
        System.out.println("El idInv es " + idInvitado + " y el codigo " + codigo);
        if (DB.existeCodigo(codigo)) {
            System.out.println("existe");
            return DB.sumarIdInvitado(idInvitado, codigo);
        } else {

            System.out.println("no existe");
            out.writeInt(-1);
        }
        return false;
    }

    private boolean cambiarPass() {
        String user = null;
        String oldPass = null;
        String newPass = null;
        try {
            user = in.readUTF();
            oldPass = in.readUTF();
            oldPass = Hash.hashear(oldPass);
            newPass = in.readUTF();
            newPass = Hash.hashear(newPass);
            return DB.cambiarPass(user, oldPass, newPass);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Se ha producido un error al cambiar la pass.\n" +
                    "user: " + user + " - oldPass " + oldPass + " - newPass = " + newPass);
            e.printStackTrace();
        }
        return false;
    }

    private void inicioSesion() throws IOException, SQLException {
        String user, pass;
        int id;
        boolean bool;
        user = in.readUTF();
        pass = in.readUTF();
        pass = Hash.hashear(pass);
        id = DB.inicioSesion(user, pass);
        bool = id > 0;
        out.writeBoolean(bool);
        if (bool) {
            out.writeUTF(DB.guardarToken(generarToken(), id));
        }
    }

    private void registro() throws IOException, SQLException {
        String user, pass;
        user = in.readUTF();
        pass = in.readUTF();
        pass = Hash.hashear(pass);

        int res = DB.registro(user, pass);
        out.writeInt(res);

        switch (res) {
            case -2:
            case -1:
                System.err.println("Error de conexi�n con la base de datos");
                break;
            case 0:
                System.out.println("El usuario " + user + "ya existe");
                break;
            case 1:
                System.out.println("El usuario " + user + " ha sido registrado correctamente.");
                out.writeUTF(DB.guardarToken(generarToken(), DB.getIdFromUser(user)));
                break;

        }
    }


    private void pedirDatos() throws IOException {
        int[] datos = DB.pedirDatos(in.readUTF());
        out.writeInt(datos[0]);
        out.writeInt(datos[1]);
        out.writeInt(datos[2]);
        out.writeInt(datos[3]);
        out.writeInt(datos[4]);
        out.writeInt(datos[5]);
    }


    private String generarToken() {
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        int size = 10;
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }
}
