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

import juego.FriendLobby;
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

        try {
            out = new DataOutputStream(cliente.getOutputStream());
            in = new DataInputStream(cliente.getInputStream());
        } catch (IOException e) {
            logger.error("Conexion interrumpida " + e.getMessage());
            return;
        }
        String peticion="";
        while (!peticion.equalsIgnoreCase("salir")) {
            try {
                peticion = in.readUTF();
                logger.info("Peticion '{}' recibida de {}:{}", peticion,
                        socket.getInetAddress().getHostAddress(), socket.getPort());

                procesarPeticion(peticion);
            } catch (Exception e) {
                logger.error("Error al procesar la peticion ", e);
            }
        }

    }

    private void procesarPeticion(String peticion) throws Exception {
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
            case "consultarlogros":
                consultarLogros();
                cerrarConexion();
                break;
        }
    }

    private void consultarLogros() throws IOException, SQLException {
        //se recibe un token, el token se transforma a id y se envia como parametro
        Boolean[] datos = DB.consultarLogros(DB.getIdFromToken(in.readUTF()));
        out.writeBoolean(datos[0]);
        out.writeBoolean(datos[1]);
        out.writeBoolean(datos[2]);
        out.writeBoolean(datos[3]);
        out.writeBoolean(datos[4]);
        out.writeBoolean(datos[5]);
        out.writeBoolean(datos[6]);
    }

    private void rankingNivel() throws IOException, SQLException {
        ArrayList<String> users = DB.getRankingUsers();
        ArrayList<String> niveles = DB.getRankingNiveles();

        if (users == null || niveles == null) {
            out.writeInt(0);
            return;
        }

        int numDatos = Math.min(users.size(), niveles.size());
        out.writeInt(numDatos);

        for (int i = 0; i < numDatos; i++) {
            out.writeUTF(users.get(i));
        }
        for (int i = 0; i < numDatos; i++) {
            out.writeUTF(niveles.get(i));
        }
    }

    private void rankingElo() throws IOException, SQLException {
        ArrayList<String> users = DB.getRankingUsers();
        ArrayList<String> elos = DB.getRankingElos();

        if (users == null || elos == null) {
            out.writeInt(0);
            return;
        }

        int numDatos = Math.min(users.size(), elos.size());
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

    private void jugarOnline() throws IOException, SQLException {
        Parametros.NUM_JUGADORES++;
        logger.debug("Ahora hay {} jugadores ", Parametros.NUM_JUGADORES);

        if (Parametros.NUM_JUGADORES == 1) {
            Jugador j1 = new Jugador(socket);
            Servidor.lobby = new Lobby(ss, j1);
        } else if (Parametros.NUM_JUGADORES == 2) {
            Jugador j2 = new Jugador(socket);
            Servidor.lobby.setJugador(j2);
            Parametros.NUM_JUGADORES = 0;
        } else {
            Jugador esp = new Jugador(socket);
            logger.error("El jugador {} ha solicitado jugar, pero hay {} jugadores", esp.getUser(), Parametros.NUM_JUGADORES);
            esp.enviarString("lleno");
        }
    }

    private boolean crearSala() throws IOException, SQLException {
        int codigo;
        String caracteres = "0123456789";
        int size = 4;
        Jugador anfitrion = new Jugador(socket);
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; i++)
            sb.append(caracteres.charAt(rnd.nextInt(caracteres.length())));

        codigo = Integer.parseInt(sb.toString());
        logger.debug("Jugador {} ha creado la sala {}", anfitrion.getUser(), codigo);
        out.writeInt(codigo);
        FriendLobby fl = new FriendLobby(ss, anfitrion.getId(), codigo);
        Servidor.friendLobbies.add(fl);
        return true;
    }

    private boolean unirse() throws IOException, SQLException {
        Jugador invitado = new Jugador(socket);
        int codigo = in.readInt();

        for (FriendLobby fl : Servidor.friendLobbies) {
            if (fl.getCodigo() == codigo) {
                logger.debug("El usuario {} se ha unido a la sala {}", invitado.getUser(), codigo);
                invitado.enviarInt(1);
                fl.setJugador(invitado);
                return true;
            }
        }

        logger.debug("El usuario {} ha intentado unirse a la sala {}, que no existe", invitado.getUser(), codigo);
        invitado.enviarInt(-1);
        return false;
    }

    private boolean cambiarPass() throws Exception {

        String user = in.readUTF();
        String oldPass = in.readUTF();
        String newPass = in.readUTF();


        oldPass = Hash.hashear(oldPass);
        newPass = Hash.hashear(newPass);

        try {
            return DB.cambiarPass(user, oldPass, newPass);
        } catch (SQLException e) {
            throw new Exception("Error al cambiar la pass", e);
        }
    }

    private void inicioSesion() throws IOException, SQLException, NoSuchAlgorithmException {
        String user, pass;

        user = in.readUTF();
        pass = in.readUTF();
        pass = Hash.hashear(pass);

        int id = DB.inicioSesion(user, pass);
        boolean bool = id > 0;
        out.writeBoolean(bool);
        if (bool) {
            String token = DB.guardarToken(generarToken(), id);
            if (token == null)
                token = "null";
            out.writeUTF(token);
        }
    }

    private void registro() throws Exception {
        String user, pass;
        user = in.readUTF();
        pass = in.readUTF();
        pass = Hash.hashear(pass);

        int res = DB.registro(user, pass);
        out.writeInt(res);

        switch (res) {
            case -1:
                throw new Exception("Error de conexion con la base de datos");
            case 0:
                logger.info("El usuario {} ya existe", user);
                break;
            case 1:
                logger.info("El usuario {} ha sido registrado correctamente", user);

                String token = DB.guardarToken(generarToken(), DB.getIdFromUser(user));
                if (token == null) {
                    token = "null";
                }
                out.writeUTF(token);
                break;
        }
    }

    private void pedirDatos() throws IOException, SQLException {
        //se recibe un token, el token se transforma a id y se envia como parametro
        int[] datos = DB.pedirDatos(DB.getIdFromToken(in.readUTF()));
        out.writeInt(datos[0]);
        out.writeInt(datos[1]);
        out.writeInt(datos[2]);
        out.writeInt(datos[3]);
        out.writeInt(datos[4]);
        out.writeInt(datos[5]);
    }

    private String generarToken() {
        String caracteres = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        int size = 10;

        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; i++)
            sb.append(caracteres.charAt(rnd.nextInt(caracteres.length())));
        return sb.toString();
    }
}
