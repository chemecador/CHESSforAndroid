package servidor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;

import juego.Parametros;
import juego.Jugador;
import juego.Lobby;
import db.DB;
import util.Hash;

public class ClientHandler extends Thread {


    private static final Logger logger = LogManager.getLogger();
    private ServerSocket ss;
    private DataOutputStream out;
    private DataInputStream in;
    private Socket socket;


    public ClientHandler(ServerSocket ss, Socket cliente) {
        this.socket = cliente;
        this.ss = ss;
        String peticion = "";
        try {
            out = new DataOutputStream(cliente.getOutputStream());
            in = new DataInputStream(cliente.getInputStream());
            peticion = in.readUTF();
        } catch (Exception e) {
            logger.error("Conexion interrumpida " + e.getMessage());
        }
        logger.info("Peticion '{}' recibida de {}:{}", peticion,
                socket.getInetAddress().getHostAddress(), socket.getPort());
        try {
            peticion(peticion);
        } catch (Exception e) {
            logger.error("Error al procesar la peticion ", e);
        }

    }

    private void peticion(String peticion) throws SQLException, IOException, NoSuchAlgorithmException {
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
            case "elo":
                rankingElo();
                cerrarConexion();
                break;
            case "nivel":
                rankingNivel();
                cerrarConexion();
                break;
        }
    }

    private void rankingNivel() throws IOException {
        ArrayList<String> users = DB.getRankingUsers();
        ArrayList<String> niveles = DB.getRankingNiveles();
        int numDatos = Math.min(users.size(), niveles.size());
        if (users == null || niveles == null) {
            out.writeInt(0);
            return;
        }
        out.writeInt(numDatos);
        for (int i = 0; i < numDatos; i++) {
            out.writeUTF(users.get(i));
        }
        for (int i = 0; i < numDatos; i++) {
            out.writeUTF(niveles.get(i));
        }
    }

    private void rankingElo() throws IOException {
        ArrayList<String> users = DB.getRankingUsers();
        ArrayList<String> elos = DB.getRankingElos();
        int numDatos = Math.min(users.size(), elos.size());
        if (users == null || elos == null) {
            out.writeInt(0);
            return;
        }
        out.writeInt(numDatos);
        for (int i = 0; i < numDatos; i++) {
            out.writeUTF(users.get(i));
        }
        for (int i = 0; i < numDatos; i++) {
            out.writeUTF(elos.get(i));
        }
    }

    public void cerrarConexion() throws IOException {
        socket.close();
    }


    private void jugarOnline() throws IOException {
        Parametros.NUM_JUGADORES++;
        logger.info("Ahora hay {} jugadores ", Parametros.NUM_JUGADORES);
        if (Parametros.NUM_JUGADORES == 1) {
            Jugador j1 = new Jugador(socket);
            Servidor.lobby = new Lobby(ss, j1);
            Servidor.lobby.start();
        } else if (Parametros.NUM_JUGADORES == 2) {

            Jugador j2 = new Jugador(socket);
            Servidor.lobby.setJugador(j2);
        } else {
            Jugador esp = new Jugador(socket);
            logger.info("El jugador {} ha solicitado jugar, pero esta lleno", esp.getUser());
            esp.enviarString("lleno");
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
            logger.error("Se ha producido un error al cambiar la pass.\n" +
                    "user: {} - oldPass {} - newPass = {}", user, oldPass, newPass);
            e.printStackTrace();
        }
        return false;
    }

    private void inicioSesion() throws IOException, SQLException, NoSuchAlgorithmException {
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

    private void registro() throws IOException, SQLException, NoSuchAlgorithmException {
        String user, pass;
        user = in.readUTF();
        pass = in.readUTF();
        pass = Hash.hashear(pass);

        int res = DB.registro(user, pass);
        out.writeInt(res);

        switch (res) {
            case -2:
            case -1:
                logger.error("Error de conexion con la base de datos");
                break;
            case 0:
                logger.error("El usuario {} ya existe", user);
                break;
            case 1:
                logger.info("El usuario {} ha sido registrado correctamente", user);
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
