package db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;


/**
 * Clase DB. Contiene todos los metodos necesarios para interactuar con la base de datos
 */
public class DB {
    private static final Logger logger = LogManager.getLogger();
    private static Connection conn;


    /***
     * Metodo que gestiona el registro en la base de datos.
     *
     * @param user Nombre de usuario
     * @param pass Clave (ya hasheada previamente)
     * @return int
     * @throws SQLException SQLException
     */
    public static int registro(String user, String pass) throws SQLException {

        if (conn == null || conn.isClosed()) {
            return -1;
        }

        if (usuarioYaExiste(user))
            return 0;
        // se crea una consulta
        String consulta = "INSERT INTO jugadores (user,pass) VALUES (?,?)";
        PreparedStatement sentencia = conn.prepareStatement(consulta);
        sentencia.setString(1, user);
        sentencia.setString(2, pass);

        // se sustituyen los datos en la consulta y se ejecuta
        return sentencia.executeUpdate();

    }

    /***
     * Metodo que gestiona el inicio de sesion en la base de datos.
     *
     * @param user Nombre de usuario
     * @param pass Clave (ya hasheada previamente)
     * @return int
     * @throws SQLException SQLException
     */
    public static int inicioSesion(String user, String pass) throws SQLException {

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

    /**
     * Recibe un token y consulta su id
     *
     * @param token Token que recibe
     * @return Id
     * @throws SQLException SQLException
     */
    public static int getIdFromToken(String token) throws SQLException {

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

    /**
     * Recibe un id y consulta su nombre de usuario
     *
     * @param id Id que recibe
     * @return Nombre de usuario
     * @throws SQLException SQLException
     */
    public static String getUserFromId(int id) throws SQLException {

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

    /**
     * Recibe un nombre de usuario y consulta su id
     *
     * @param user Nombre de usuario que recibe
     * @return Id
     * @throws SQLException SQLException
     */
    public static int getIdFromUser(String user) throws SQLException {


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

    /**
     * Consulta si un jugador ha completado un logro
     *
     * @param idJugador Jugador a comprobar
     * @param idLogro   Logro a comprobar
     * @return True (si), False (no)
     */
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
            // se crea una consulta
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

    /**
     * Comprueba si un jugador ha completado todos los logros
     *
     * @param id Id del jugador
     * @return True (si), False (no)
     */
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
            // se crea una consulta
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


    /**
     * Metodo encargado de registrar que un jugador ha completado un logro
     *
     * @param idJugador Jugador que ha completado el logro
     * @param idLogro   Logro que ha completado el jugadopr
     * @return True (registro correcto), False (error al registrar)
     */
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


            // se crea una consulta
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


    /**
     * Metodo que se encarga de actualizar el nivel tras llegar a una nueva decena de victorias
     *
     * @param id Id del jugador
     * @return True (nivel actualizado correctamente), False (error al actualizar)
     */
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
                // no se ha llegado a una nueva decena, se termina la ejecucion del metodo
                return true;
            }

            // los tres primeros logros son ganar 10, 20 y 30 partidas
            if (numVictorias == 10 || numVictorias == 20 || numVictorias == 30) {

                // si no los ha completado el usuario, se completan ahora
                if (!DB.logroCompletado(id, numVictorias / 10)) {
                    completarLogro(id, numVictorias / 10);
                }
            }

            res = sentencia.executeQuery();

            if (res.next()) {
                nivel = res.getInt("nivel");
            }
            // se crea una consulta
            consulta = "UPDATE jugadores SET nivel = nivel + 1 WHERE idjugador = ?";
            sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, id);
            // se sustituyen los datos en la consulta y se ejecuta
            sentencia.executeUpdate();

            // si se ha alcanzado el nivel 5, se ha completado el logro 6
            if (nivel == 5) {
                if (!DB.logroCompletado(id, 6)) {
                    completarLogro(id, 6);
                }
            }

            // si se ha alcanzado el nivel 10, se ha completado el logro 7
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

    /**
     * Metodo que se encarga de actualizar las estadisticas de un jugadory67t5u
     *
     * @param id1    Id del ganador
     * @param id2    Id del perdedor
     * @param tablas booleano que indica si ha habido tablas
     * @return True (stats actualizadas correctamente), False (error al actualizar)
     */
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
                // se crea una consulta
                consulta = "UPDATE jugadores SET tablas = tablas + 1, jugadas = jugadas + 1 WHERE idjugador = ?";
                sentencia = conn.prepareStatement(consulta);
                sentencia.setInt(1, id1);
                // se sustituyen los datos en la consulta y se ejecuta
                sentencia.executeUpdate();

                consulta = "UPDATE jugadores SET tablas = tablas + 1, jugadas = jugadas + 1 WHERE idjugador = ?";
            } else {
                // se crea una consulta
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

    /**
     * Metodo que se encarga de actualizar el resultado de una partida
     *
     * @param movs   String con los movimientos del tablero
     * @param id1    Id del ganador
     * @param id2    Id del perdedor
     * @param tablas booleano que indica si ha habido tablas
     * @return True (stats actualizadas correctamente), False (error al actualizar)
     */
    public static boolean registrarResultado(String movs, int id1, int id2, boolean tablas) {
        try {
            if (conn == null) {
                return false;
            }

            if (conn.isClosed()) {
                return false;
            }

            // se crea una consulta
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

    /**
     * Metodo que comprueba si el usuario ya existre
     *
     * @param user Nombre del usuario a comprobar
     * @return True (ya existe), False (no existe)
     * @throws SQLException SQLException
     */
    public static boolean usuarioYaExiste(String user) throws SQLException {

        // realiza la consulta de la tabla actual
        String consulta = "SELECT * FROM jugadores WHERE user = ?";
        PreparedStatement sentencia;

        // realiza la consulta y la ejecuta
        sentencia = conn.prepareStatement(consulta);
        sentencia.setString(1, user);
        ResultSet res = sentencia.executeQuery();

        // si existe, res.next() devolvera true; si no, devolvera false
        return res.next();
    }

    /**
     * Metodo que comprueba si un usuario ya tiene asignado un token
     *
     * @param id Id del usuario
     * @return True (ya lo tiene asignado), False (no lo tiene)
     * @throws SQLException SQLException
     */
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

    /**
     * Metodo que elimina el token de un usuario
     *
     * @param id Id del usuario
     * @throws SQLException SQLException
     */
    public static void borrarToken(int id) throws SQLException {

        // realiza la consulta de la tabla actual
        String consulta = "DELETE FROM auth_tokens WHERE idjugador = ?";
        PreparedStatement sentencia;

        // realiza la consulta y la ejecuta
        sentencia = conn.prepareStatement(consulta);
        sentencia.setInt(1, id);
        sentencia.executeUpdate();

    }

    /**
     * Metodo que devuelve un vector con los logros completados por un jugador
     *
     * @param idJugador Id del jugador a comprobar
     * @return Vector de booleanos con un true por cada logro completado
     * @throws SQLException SQLException
     */
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

    /**
     * Metodo encargado de pedir los datos de un jugador
     *
     * @param idJugador Id del jugador
     * @return Datos de la cuenta del jugador
     * @throws SQLException SQLException
     */
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

    /**
     * Metodo encargado de guardar un token en la base de datos
     *
     * @param token Token a guardar
     * @param id    Id del jugador al que le corresponde
     * @return String con el token que ha guardado, o null si no lo ha podido guardar
     * @throws SQLException SQLException
     */
    public static String guardarToken(String token, int id) throws SQLException {

        if (conn == null || conn.isClosed()) {
            return null;
        }

        if (existeToken(id)) {
            borrarToken(id);
        }
        // se crea una consulta
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

    /**
     * Metodo encargado de cambiar la contraseña de un usuario
     *
     * @param user    Nombre de usuario
     * @param oldPass Clave antigua
     * @param newPass Clave nueva
     * @return True (clave cambiada correctamente), False (error al cambiar la clave)
     * @throws SQLException SQLException
     */
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

        // se crea una consulta
        String consulta = "UPDATE jugadores SET pass = ? WHERE idjugador = ?";
        PreparedStatement sentencia = conn.prepareStatement(consulta);
        sentencia.setString(1, newPass);
        sentencia.setInt(2, id);

        // se sustituyen los datos en la consulta y se ejecuta
        sentencia.executeUpdate();
        sentencia.close();

        return true;
    }


    /**
     * Metodo que devuelve los nombres de los usuarios ordenados por nivel
     *
     * @return ArrayList de Strings con los nombres
     * @throws SQLException SQLException
     */
    public static ArrayList<String> getRankingUsersByNivel() throws SQLException {


        // realiza la consulta de la tabla actual
        String consulta = "SELECT user FROM jugadores WHERE jugadas > 0 ORDER BY jugadores.nivel DESC";
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

    /**
     * Metodo que devuelve los nombres de los usuarios ordenados por ELO
     *
     * @return ArrayList de Strings con los nombres
     * @throws SQLException SQLException
     */
    public static ArrayList<String> getRankingUsersByELO() throws SQLException {

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

    /**
     * Metodo que devuelve el nivel de los usuarios ordenados de manera descendente
     *
     * @return ArrayList de Strings con los niveles
     * @throws SQLException SQLException
     */
    public static ArrayList<String> getRankingNiveles() throws SQLException {

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

    /**
     * Metodo que devuelve el ELO de los usuarios ordenados de manera descendente
     *
     * @return ArrayList de Strings con los ELOs
     * @throws SQLException SQLException
     */
    public static ArrayList<String> getRankingElos() throws SQLException {

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

    /**
     * Metodo que conecta con la base de datos.
     */
    public static void conectar() throws SQLException {
        String url = null;
        String user = null;
        String passw = null;

        String host = System.getenv("MYSQL_HOST");
        if (host == null) {
            FileInputStream f = null;
            try {
                f = new FileInputStream("conf/conf.properties");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Properties p = new Properties();
            try {
                p.load(f);
                url = p.getProperty("server.url");
                user = p.getProperty("server.user");
                passw = p.getProperty("server.password");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            url = "jdbc:mysql://" + host + "/" + System.getenv("MYSQL_DB");
            user = System.getenv("MYSQL_USER");
            passw = System.getenv("MYSQL_PASSWORD");
        }

        conn = null;
        // conexion con localhost a traves de mi puerto
        conn = DriverManager.getConnection(url, user, passw);
    }

    /**
     * Metodo que desconecta de la base de datos
     */
    public static void desconectar() throws SQLException {
        conn.close();
    }
}
