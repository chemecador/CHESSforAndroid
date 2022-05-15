package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import servidor.Servidor;

public class DB {
    private static final Logger logger = Logger.getLogger(DB.class.getName());
    private static Connection conn;


    /**
     * Metodo que conecta con la base de datos.
     */
    public static void conectar() throws SQLException {
        String url = null;
        String user = null;
        String passw = null;

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
     * @throws SQLException
     */
    public static int registro(String user, String pass) throws SQLException {
        // conectar con la base de datos

        if (conn == null) {
            return -1;
        }
        if (conn.isClosed()) {
            return -2;
        }
        if (comprobarUnica(user))
            return 0;
        // si no ha habido errores, se crea una consulta
        String consulta = "INSERT INTO jugadores (user,pass) VALUES (?,?)";
        PreparedStatement sentencia = conn.prepareStatement(consulta);
        sentencia.setString(1, user);
        sentencia.setString(2, pass);

        // se sustituyen los datos en la consulta y se ejecuta
        int numReg = sentencia.executeUpdate();
        if (sentencia != null) {
            sentencia.close();
        }

        // se desconecta de la base de datos y se devuelve el n�mero de registros
        // afectados
        return numReg;
    }

    public static int inicioSesion(String user, String pass) {
        // conecta con la base de datos

        // realiza la consulta de la tabla actual
        String consulta = "SELECT * FROM jugadores WHERE user = ? AND pass = ?";
        PreparedStatement sentencia;
        try {
            // realiza la consulta y la ejecuta
            sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, user);
            sentencia.setString(2, pass);
            ResultSet res = sentencia.executeQuery();
            if (res.next()) {
                return res.getInt("idjugador");
            }
            return 0;
        } catch (SQLException e) {
            System.err.println("Error al consultar + " + e.toString());
        }

        // ha habido un error; desconecta y devuelve null.
        return -1;
    }

    public static int getIdFromToken(String token) {
        // conecta con la base de datos

        // realiza la consulta de la tabla actual
        String consulta = "SELECT idjugador FROM auth_tokens WHERE token = ?";
        PreparedStatement sentencia;
        try {
            // realiza la consulta y la ejecuta
            sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, token);
            ResultSet res = sentencia.executeQuery();
            if (res.next()) {
                return res.getInt("idjugador");
            }
            return 0;
        } catch (SQLException e) {
            System.err.println("Error al consultar + " + e.toString());
        }

        // ha habido un error; desconecta y devuelve null.
        return -1;
    }

    public static String getUserFromId(int id) {
        // conecta con la base de datos

        // realiza la consulta de la tabla actual
        String consulta = "SELECT user FROM jugadores WHERE idjugador = ?";
        PreparedStatement sentencia;
        try {
            // realiza la consulta y la ejecuta
            sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, id);
            ResultSet res = sentencia.executeQuery();
            if (res.next()) {
                return res.getString("user");
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar + " + e.toString());
        }

        // ha habido un error; desconecta y devuelve null.
        return null;
    }

    public static int getIdFromUser(String user) {
        // conecta con la base de datos

        // realiza la consulta de la tabla actual
        String consulta = "SELECT idjugador FROM jugadores WHERE user = ?";
        PreparedStatement sentencia;
        try {
            // realiza la consulta y la ejecuta
            sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, user);
            ResultSet res = sentencia.executeQuery();
            if (res.next()) {
                return res.getInt("idjugador");
            }
            return 0;
        } catch (SQLException e) {
            System.err.println("Error al consultar + " + e.toString());
        }

        // ha habido un error; desconecta y devuelve null.
        return -1;
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
            consulta = "SELECT victorias FROM jugadores WHERE jugadores.idjugador = ?";
            sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, id);
            ResultSet res = sentencia.executeQuery();
            int numVictorias = -1;
            while (res.next()) {
                numVictorias = res.getInt("victorias");
            }
            if (numVictorias % 10 != 0) {
                return true;
            }

            // si no ha habido errores, se crea una consulta
            consulta = "UPDATE jugadores SET nivel = nivel + 1 WHERE idjugador = ?";
            sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, id);
            // se sustituyen los datos en la consulta y se ejecuta
            sentencia.executeUpdate();

            if (numVictorias == 10 || numVictorias == 20 || numVictorias == 30) {
                consulta = "INSERT INTO jugador_logro (idjugador, idlogro) VALUES (?,?)";
                sentencia = conn.prepareStatement(consulta);
                sentencia.setInt(1, id);
                sentencia.setInt(2, numVictorias / 10);
                // se sustituyen los datos en la consulta y se ejecuta
                sentencia.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean actualizarJugadores(int id1, int id2, boolean tablas) {
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
                sentencia = conn.prepareStatement(consulta);
                sentencia.setInt(1, id2);
                sentencia.executeUpdate();
            } else {
                // si no ha habido errores, se crea una consulta
                consulta = "UPDATE jugadores SET elo = elo + 10, victorias = victorias + 1, jugadas = jugadas + 1 WHERE idjugador = ?";
                sentencia = conn.prepareStatement(consulta);
                sentencia.setInt(1, id1);
                // se sustituyen los datos en la consulta y se ejecuta
                sentencia.executeUpdate();


                consulta = "UPDATE jugadores SET elo = elo - 10, derrotas = derrotas + 1, jugadas = jugadas + 1 WHERE idjugador = ?";
                sentencia = conn.prepareStatement(consulta);
                sentencia.setInt(1, id2);
                // se sustituyen los datos en la consulta y se ejecuta
                sentencia.executeUpdate();
            }
            if (sentencia != null) {
                sentencia.close();
            }
            Servidor.jugadores = Servidor.jugadores - 2;
            System.out.println("Ahora hay " + Servidor.jugadores + " jugadores");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean gestionarFinal(String movs, int id1, int id2, boolean tablas) {
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
            int numReg = sentencia.executeUpdate();


            if (sentencia != null) {
                sentencia.close();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean comprobarUnica(String user) {

        // conecta con la base de datos

        // realiza la consulta de la tabla actual
        String consulta = "SELECT * FROM jugadores WHERE user = ?";
        PreparedStatement sentencia;
        try {
            // realiza la consulta y la ejecuta
            sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, user);
            ResultSet res = sentencia.executeQuery();
            return res.next();
        } catch (SQLException e) {
            System.err.println("Error al consultar + " + e.toString());
        }

        // ha habido un error; desconecta y devuelve null.
        return false;
    }


    public static boolean existeCodigo(int codigo) {

        // realiza la consulta de la tabla actual
        String consulta = "SELECT codigo FROM partidas WHERE codigo = ?";
        PreparedStatement sentencia;
        try {
            // realiza la consulta y la ejecuta
            sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, codigo);
            ResultSet res = sentencia.executeQuery();
            if (res.next()) {
                return res.getInt("codigo") > 0;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error al consultar + ");
            e.printStackTrace();
        }

        // ha habido un error; desconecta y devuelve null.
        return false;
    }

    public static boolean existeToken(int id) {

        // realiza la consulta de la tabla actual
        String consulta = "SELECT token FROM auth_tokens WHERE idjugador = ?";
        PreparedStatement sentencia;
        try {
            // realiza la consulta y la ejecuta
            sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, id);
            ResultSet res = sentencia.executeQuery();
            if (res.next()) {
                return res.getString("token").length() > 0;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error al consultar + " + e.toString());
        }

        // ha habido un error; desconecta y devuelve null.
        return false;
    }

    public static boolean borrarToken(int id) {

        // realiza la consulta de la tabla actual
        String consulta = "DELETE FROM auth_tokens WHERE idjugador = ?";
        PreparedStatement sentencia;
        try {
            // realiza la consulta y la ejecuta
            sentencia = conn.prepareStatement(consulta);
            sentencia.setInt(1, id);
            sentencia.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al consultar + " + e.toString());
        }

        // ha habido un error; desconecta y devuelve null.
        return false;
    }


    public static int[] pedirDatos(String user) {
        int[] datos = new int[6];

        // conecta con la base de datos

        // realiza la consulta de la tabla actual
        String consulta = "SELECT * FROM jugadores WHERE user = ?";
        PreparedStatement sentencia = null;
        try {
            // realiza la consulta y la ejecuta
            sentencia = conn.prepareStatement(consulta);
            sentencia.setString(1, user);
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

        } catch (SQLException e) {
            System.err.println("Error al consultar + " + e.toString());
        }

        // ha habido un error; desconecta y devuelve null.
        return datos;
    }

    public static String guardarToken(String token, int id) throws SQLException {
        // conectar con la base de datos

        if (conn == null) {
            return null;
        }
        if (conn.isClosed()) {
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
        if (sentencia != null) {
            sentencia.close();
        }

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
        if (sentencia != null) {
            sentencia.close();
        }

        return true;
    }

    public static boolean sumarIdInvitado(int idInvitado, int codigo) throws SQLException {
        if (conn == null || conn.isClosed()) {
            logger.log(Level.WARNING, "La conexion es null o esta cerrada");
            return false;
        }

        // si no ha habido errores, se crea una consulta
        String consulta = "UPDATE partidas SET idinvitado = ? WHERE codigo = ?";
        PreparedStatement sentencia = conn.prepareStatement(consulta);
        sentencia.setInt(1, idInvitado);
        sentencia.setInt(2, codigo);

        // se sustituyen los datos en la consulta y se ejecuta
        sentencia.executeUpdate();
        return true;
    }

    public static boolean crearIDAnfitiron(int idAnfitrion, int codigo) throws SQLException {
        // conectar con la base de datos

        if (conn == null || conn.isClosed()) {
            logger.log(Level.WARNING, "La conexion es null o esta cerrada");
            return false;
        }
        // si no ha habido errores, se crea una consulta
        String consulta = "INSERT INTO partidas (idanfitrion, codigo) VALUES (?,?)";
        PreparedStatement sentencia = conn.prepareStatement(consulta);
        sentencia.setInt(1, idAnfitrion);
        sentencia.setInt(2, codigo);

        // se sustituyen los datos en la consulta y se ejecuta
        sentencia.executeUpdate();
        return true;
    }

    public static ArrayList<String> getRankingUsers() {
        // conecta con la base de datos

        // realiza la consulta de la tabla actual
        String consulta = "SELECT user FROM jugadores WHERE jugadas > 0 ORDER BY jugadores.elo DESC";
        PreparedStatement sentencia;
        try {
            ArrayList<String> datos = new ArrayList<>();
            // realiza la consulta y la ejecuta
            sentencia = conn.prepareStatement(consulta);
            ResultSet res = sentencia.executeQuery();

            while (res.next()) {
                datos.add(res.getString("user"));
            }
            return datos;
        } catch (SQLException e) {
            System.err.println("Error al consultar + " + e.toString());
        }

        // ha habido un error; desconecta y devuelve null.
        return null;
    }

    public static ArrayList<String> getRankingNiveles() {
        // conecta con la base de datos

        // realiza la consulta de la tabla actual
        String consulta = "SELECT nivel FROM jugadores WHERE jugadas > 0 ORDER BY jugadores.nivel DESC";
        PreparedStatement sentencia;
        try {
            ArrayList<String> datos = new ArrayList<>();
            // realiza la consulta y la ejecuta
            sentencia = conn.prepareStatement(consulta);
            ResultSet res = sentencia.executeQuery();

            while (res.next()) {
                datos.add(res.getString("nivel"));
            }
            return datos;
        } catch (SQLException e) {
            System.err.println("Error al consultar + " + e.toString());
        }

        // ha habido un error; desconecta y devuelve null.
        return null;
    }

    public static ArrayList<String> getRankingElos() {
        // conecta con la base de datos

        // realiza la consulta de la tabla actual
        String consulta = "SELECT elo FROM jugadores WHERE jugadas > 0 ORDER BY jugadores.elo DESC";
        PreparedStatement sentencia;
        try {
            ArrayList<String> datos = new ArrayList<>();
            // realiza la consulta y la ejecuta
            sentencia = conn.prepareStatement(consulta);
            ResultSet res = sentencia.executeQuery();

            while (res.next()) {
                datos.add(String.valueOf(res.getInt("elo")));
            }
            return datos;
        } catch (SQLException e) {
            System.err.println("Error al consultar + " + e.toString());
        }

        // ha habido un error; desconecta y devuelve null.
        return null;
    }
}
