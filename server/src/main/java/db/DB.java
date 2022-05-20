package db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class DB {
    private static final Logger logger = LogManager.getLogger();
    private static Connection conn;


    /**
     * Metodo que conecta con la base de datos.
     */
    public static void conectar() throws SQLException {
        String url;
        String user;
        String passw;

        String host = System.getenv("MYSQL_HOST");
        if (host == null) {
            url = "jdbc:mysql://localhost/chessforandroid";
            user = "chess4android";
            passw = "ttDfdqxmf3ynnSozTMYSzH7H2ncfwD";
        } else {
            user = System.getenv("MYSQL_USER");
            passw = System.getenv("MYSQL_PASSWORD");
            url = "jdbc:mysql://" + host + "/" + System.getenv("MYSQL_DB");
        }
        // MYSQL_DB
        // MYSQL_USER
        // MYSQL_PASSWORD

        conn = null;
        // conexion con localhost a trav�s de mi puerto
        conn = DriverManager.getConnection(url, user, passw);
    }

    /**
     * Metodo que desconecta de la base de datos
     */
    public static void desconectar() throws SQLException {
        conn.close();
    }

    /***
     * Metodo que gestiona el registro en la base de datos.
     *
     * @param user Nombre de usuario
     * @param pass Contrase�a (ya hasheada previamente)
     * @return int
     * @throws SQLException SQLException
     */
    public static int registro(String user, String pass) throws SQLException {
        // conectar con la base de datos

        if (conn == null || conn.isClosed()) {
            return -1;
        }

        if (comprobarUnica(user))
            return 0;
        // si no ha habido errores, se crea una consulta
        String consulta = "INSERT INTO jugadores (user,pass) VALUES (?,?)";
        PreparedStatement sentencia = conn.prepareStatement(consulta);
        sentencia.setString(1, user);
        sentencia.setString(2, pass);

        // se sustituyen los datos en la consulta y se ejecuta
        return sentencia.executeUpdate();

    }

    public static int inicioSesion(String user, String pass) throws SQLException {
        // conecta con la base de datos

        // realiza la consulta de la tabla actual
        String consulta = "SELECT * FROM jugadores WHERE user = ? AND pass = ?";
        PreparedStatement sentencia;
        // realiza la consulta y la ejecuta
        sentencia = conn.prepareStatement(consulta);
        sentencia.setString(1, user);
        sentencia.setString(2, pass);
        ResultSet res = sentencia.executeQuery();
        if (res.next()) {
            return res.getInt("idjugador");
        }
        // ha habido un error; desconecta y devuelve null.
        return -1;
    }

    public static int getIdFromToken(String token) throws SQLException {
        // conecta con la base de datos

        // realiza la consulta de la tabla actual
        String consulta = "SELECT idjugador FROM auth_tokens WHERE token = ?";
        PreparedStatement sentencia;
        // realiza la consulta y la ejecuta
        sentencia = conn.prepareStatement(consulta);
        sentencia.setString(1, token);
        ResultSet res = sentencia.executeQuery();
        if (res.next()) {
            return res.getInt("idjugador");
        }
        return -1;
    }

    public static String getUserFromId(int id) throws SQLException {
        // conecta con la base de datos

        // realiza la consulta de la tabla actual
        String consulta = "SELECT user FROM jugadores WHERE idjugador = ?";
        PreparedStatement sentencia;
        // realiza la consulta y la ejecuta
        sentencia = conn.prepareStatement(consulta);
        sentencia.setInt(1, id);
        ResultSet res = sentencia.executeQuery();
        if (res.next()) {
            return res.getString("user");
        }
        // ha habido un error; desconecta y devuelve null.
        return null;
    }

    public static int getIdFromUser(String user) throws SQLException {
        // conecta con la base de datos

        // realiza la consulta de la tabla actual
        String consulta = "SELECT idjugador FROM jugadores WHERE user = ?";
        PreparedStatement sentencia;
        // realiza la consulta y la ejecuta
        sentencia = conn.prepareStatement(consulta);
        sentencia.setString(1, user);
        ResultSet res = sentencia.executeQuery();
        if (res.next()) {
            return res.getInt("idjugador");
        }

        // ha habido un error; desconecta y devuelve null.
        return -1;
    }

    public static boolean logroCompletado(int idJugador, int idLogro) {
        String consulta;
        PreparedStatement sentencia;
        try {
            if (conn == null) {
                return false;
            }

            if (conn.isClosed()) {
                return false;
            }
            // si no ha habido errores, se crea una consulta
            consulta = "SELECT * FROM jugador_logro WHERE idjugador = ?";
            sentencia = conn.prepareStatement(consulta);
            // se sustituyen los datos en la consulta y se ejecuta
            sentencia.setInt(1, idJugador);
            ResultSet res = sentencia.executeQuery();
            while (res.next()) {
                if (res.getInt("idlogro") == idLogro) {
                    return true;
                }
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean juegoCompletado(int id) {
        String consulta;
        PreparedStatement sentencia;
        try {
            if (conn == null) {
                return false;
            }

            if (conn.isClosed()) {
                return false;
            }
            // si no ha habido errores, se crea una consulta
            consulta = "SELECT COUNT(*) FROM jugador_logro WHERE idjugador = 2;";
            sentencia = conn.prepareStatement(consulta);
            // se sustituyen los datos en la consulta y se ejecuta
            ResultSet res = sentencia.executeQuery();
            if (res.next()) {
                int numLogros = res.getInt(1);
                logger.debug("El usuario con id {} ha completado {} logros", id, numLogros);
                return numLogros == 8;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean actualizarNivel(int id) {
        String consulta;
        PreparedStatement sentencia;
        try {
            if (conn == null) {
                return false;
            }

            if (conn.isClosed()) {
                return false;
            }
            consulta = "SELECT * FROM jugadores WHERE jugadores.idjugador = ?";
            sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, id);
            ResultSet res = sentencia.executeQuery();
            int numVictorias = -1;
            int nivel = -1;
            if (res.next()) {
                numVictorias = res.getInt("victorias");
            }
            if (numVictorias % 10 != 0) {
                return true;
            }

            if (numVictorias == 10 || numVictorias == 20 || numVictorias == 30) {
                if (!DB.logroCompletado(id, numVictorias / 10)) {
                    completarLogro(id, numVictorias / 10);
                }
            }

            res = sentencia.executeQuery();
            if (res.next()) {
                nivel = res.getInt("nivel");
            }
            // si no ha habido errores, se crea una consulta
            consulta = "UPDATE jugadores SET nivel = nivel + 1 WHERE idjugador = ?";
            sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, id);
            // se sustituyen los datos en la consulta y se ejecuta
            sentencia.executeUpdate();

            if (nivel == 5) {
                if (!DB.logroCompletado(id, 6)) {
                    completarLogro(id, 6);
                }
            }
            if (nivel == 10) {
                if (!DB.logroCompletado(id, 7)) {
                    completarLogro(id, 7);
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean completarLogro(int idJugador, int idLogro) {
        String consulta;
        PreparedStatement sentencia;
        try {
            if (conn == null) {
                return false;
            }

            if (conn.isClosed()) {
                return false;
            }


            // si no ha habido errores, se crea una consulta
            consulta = "INSERT INTO jugador_logro (idjugador, idlogro) VALUES (?,?)";
            sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, idJugador);
            sentencia.setInt(2, idLogro);
            // se sustituyen los datos en la consulta y se ejecuta
            sentencia.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean actualizarStats(int id1, int id2, boolean tablas) {
        String consulta;
        PreparedStatement sentencia;
        try {
            if (conn == null) {
                return false;
            }

            if (conn.isClosed()) {
                return false;
            }

            if (tablas) {
                // si no ha habido errores, se crea una consulta
                consulta = "UPDATE jugadores SET tablas = tablas + 1, jugadas = jugadas + 1 WHERE idjugador = ?";
                sentencia = conn.prepareStatement(consulta);
                sentencia.setInt(1, id1);
                // se sustituyen los datos en la consulta y se ejecuta
                sentencia.executeUpdate();

                consulta = "UPDATE jugadores SET tablas = tablas + 1, jugadas = jugadas + 1 WHERE idjugador = ?";
            } else {
                // si no ha habido errores, se crea una consulta
                consulta = "UPDATE jugadores SET elo = elo + 10, victorias = victorias + 1, jugadas = jugadas + 1 WHERE idjugador = ?";
                sentencia = conn.prepareStatement(consulta);
                sentencia.setInt(1, id1);
                // se sustituyen los datos en la consulta y se ejecuta
                sentencia.executeUpdate();


                consulta = "UPDATE jugadores SET elo = elo - 10, derrotas = derrotas + 1, jugadas = jugadas + 1 WHERE idjugador = ?";
                // se sustituyen los datos en la consulta y se ejecuta
            }
            sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, id2);
            sentencia.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean registrarResultado(String movs, int id1, int id2, boolean tablas) {
        try {
            if (conn == null) {
                return false;
            }

            if (conn.isClosed()) {
                return false;
            }

            // si no ha habido errores, se crea una consulta
            String consulta = "INSERT INTO partidas (movimientos, idanfitrion, idinvitado, idganador, idperdedor) VALUES (?,?,?,?,?)";
            PreparedStatement sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, movs);
            sentencia.setInt(2, id1);
            sentencia.setInt(3, id2);
            if (tablas) {
                sentencia.setInt(4, 0);
                sentencia.setInt(5, 0);
            } else {
                sentencia.setInt(4, id1);
                sentencia.setInt(5, id2);
            }
            // se sustituyen los datos en la consulta y se ejecuta
            sentencia.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.error("Error al registrar el resultado", e);
        }
        return false;
    }

    public static boolean comprobarUnica(String user) throws SQLException {

        // conecta con la base de datos

        // realiza la consulta de la tabla actual
        String consulta = "SELECT * FROM jugadores WHERE user = ?";
        PreparedStatement sentencia;
        // realiza la consulta y la ejecuta
        sentencia = conn.prepareStatement(consulta);
        sentencia.setString(1, user);
        ResultSet res = sentencia.executeQuery();
        return res.next();
    }

    public static boolean existeToken(int id) throws SQLException {

        // realiza la consulta de la tabla actual
        String consulta = "SELECT token FROM auth_tokens WHERE idjugador = ?";
        PreparedStatement sentencia;
        // realiza la consulta y la ejecuta
        sentencia = conn.prepareStatement(consulta);
        sentencia.setInt(1, id);
        ResultSet res = sentencia.executeQuery();
        if (res.next()) {
            return res.getString("token").length() > 0;
        }
        return false;
    }

    public static boolean borrarToken(int id) throws SQLException {

        // realiza la consulta de la tabla actual
        String consulta = "DELETE FROM auth_tokens WHERE idjugador = ?";
        PreparedStatement sentencia;
        // realiza la consulta y la ejecuta
        sentencia = conn.prepareStatement(consulta);
        sentencia.setInt(1, id);
        sentencia.executeUpdate();
        return true;
    }

    public static Boolean[] consultarLogros(int idJugador) throws SQLException {
        Boolean[] logros = new Boolean[7];
        for (int i = 0; i < logros.length; i++) {
            logros[i] = false;
        }

        // realiza la consulta de la tabla actual
        String consulta = "SELECT * FROM jugador_logro WHERE idjugador = ?";
        PreparedStatement sentencia;
        // realiza la consulta y la ejecuta
        sentencia = conn.prepareStatement(consulta);
        sentencia.setInt(1, idJugador);
        ResultSet res = sentencia.executeQuery();
        while (res.next()) {
            if (res.getInt("idlogro") == 1) {
                logros[0] = true;
            }
            if (res.getInt("idlogro") == 2) {
                logros[1] = true;
            }
            if (res.getInt("idlogro") == 3) {
                logros[2] = true;
            }
            if (res.getInt("idlogro") == 4) {
                logros[3] = true;
            }
            if (res.getInt("idlogro") == 5) {
                logros[4] = true;
            }
            if (res.getInt("idlogro") == 6) {
                logros[5] = true;
            }
            if (res.getInt("idlogro") == 7) {
                logros[6] = true;
            }
        }
        sentencia.close();
        return logros;
    }

    public static int[] pedirDatos(int idJugador) throws SQLException {
        int[] datos = new int[6];

        // realiza la consulta de la tabla actual
        String consulta = "SELECT * FROM jugadores WHERE idjugador = ?";
        PreparedStatement sentencia;
        // realiza la consulta y la ejecuta
        sentencia = conn.prepareStatement(consulta);
        sentencia.setInt(1, idJugador);
        ResultSet res = sentencia.executeQuery();
        if (res.next()) {
            datos[0] = res.getInt("nivel");
            datos[1] = res.getInt("elo");
            datos[2] = res.getInt("jugadas");
            datos[3] = res.getInt("victorias");
            datos[4] = res.getInt("derrotas");
            datos[5] = res.getInt("tablas");
        }
        sentencia.close();
        return datos;
    }

    public static String guardarToken(String token, int id) throws SQLException {
        // conectar con la base de datos

        if (conn == null || conn.isClosed()) {
            return null;
        }

        if (existeToken(id)) {
            borrarToken(id);
        }
        // si no ha habido errores, se crea una consulta
        String consulta = "INSERT INTO auth_tokens VALUES (?,?)";
        PreparedStatement sentencia = conn.prepareStatement(consulta);
        sentencia.setInt(1, id);
        sentencia.setString(2, token);

        // se sustituyen los datos en la consulta y se ejecuta
        int numReg = sentencia.executeUpdate();

        if (numReg > 0)
            return token;
        return null;
    }

    public static boolean cambiarPass(String user, String oldPass, String newPass) throws SQLException {
        int id = inicioSesion(user, oldPass);
        if (id < 1) {
            return false;
        }

        if (conn == null) {
            return false;
        }
        if (conn.isClosed()) {
            return false;
        }

        // si no ha habido errores, se crea una consulta
        String consulta = "UPDATE jugadores SET pass = ? WHERE idjugador = ?";
        PreparedStatement sentencia = conn.prepareStatement(consulta);
        sentencia.setString(1, newPass);
        sentencia.setInt(2, id);
        // se sustituyen los datos en la consulta y se ejecuta
        sentencia.executeUpdate();
        sentencia.close();

        return true;
    }


    public static ArrayList<String> getRankingUsers() throws SQLException {
        // conecta con la base de datos

        // realiza la consulta de la tabla actual
        String consulta = "SELECT user FROM jugadores WHERE jugadas > 0 ORDER BY jugadores.elo DESC";
        PreparedStatement sentencia;
        ArrayList<String> datos = new ArrayList<>();
        // realiza la consulta y la ejecuta
        sentencia = conn.prepareStatement(consulta);
        ResultSet res = sentencia.executeQuery();

        while (res.next()) {
            datos.add(res.getString("user"));
        }
        return datos;
    }

    public static ArrayList<String> getRankingNiveles() throws SQLException {
        // conecta con la base de datos

        // realiza la consulta de la tabla actual
        String consulta = "SELECT nivel FROM jugadores WHERE jugadas > 0 ORDER BY jugadores.nivel DESC";
        PreparedStatement sentencia;
        ArrayList<String> datos = new ArrayList<>();
        // realiza la consulta y la ejecuta
        sentencia = conn.prepareStatement(consulta);
        ResultSet res = sentencia.executeQuery();

        while (res.next()) {
            datos.add(res.getString("nivel"));
        }
        return datos;
    }

    public static ArrayList<String> getRankingElos() throws SQLException {
        // conecta con la base de datos

        // realiza la consulta de la tabla actual
        String consulta = "SELECT elo FROM jugadores WHERE jugadas > 0 ORDER BY jugadores.elo DESC";
        PreparedStatement sentencia;
        ArrayList<String> datos = new ArrayList<>();
        // realiza la consulta y la ejecuta
        sentencia = conn.prepareStatement(consulta);
        ResultSet res = sentencia.executeQuery();

        while (res.next()) {
            datos.add(String.valueOf(res.getInt("elo")));
        }
        return datos;
    }


}
