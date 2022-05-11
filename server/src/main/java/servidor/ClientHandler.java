package servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.sql.*;

public class ClientHandler extends Thread {

    private ServerSocket ss;
    private DataOutputStream out; // flujo de salida
    private DataInputStream in; // flujo de entrada
    private Socket socket; // socket con la conexi�n con el cliente
    private String s;
    static Connection conexion; // conexi�n con la base de datos

    // constructor
    public ClientHandler(ServerSocket ss, Socket cliente) {
        // se muestra por pantalla que ha llegado un nuevo cliente
        this.socket = cliente;
        this.ss = ss;
        // se inicializan los flujos de entrada y de salida, así como el centinela
        try {
            out = new DataOutputStream(cliente.getOutputStream());
            in = new DataInputStream(cliente.getInputStream());
            s = in.readUTF();
            System.out.println("Peticion de " + s + " recibida");
            if (s.equals("signup")) {
                gestionarRegistro();
                cerrarConexion();
            }
            if (s.equals("login")) {
                gestionarInicioSesion();
                cerrarConexion();
            }
            if (s.equals("cambiarpass")) {
                out.writeBoolean(gestionarCambiarPass());
                cerrarConexion();
            }
            if (s.equals("pedirdatos")) {
                gestionarPedirDatos();
                cerrarConexion();
            }
            if (s.equals("local")) {
                gestionarJugarLocal();
            }
            if (s.equals("crearsala")) {
                gestionarCrearSala();
            }
            if (s.equals("unirse")) {
                gestionarUnirse();
                cerrarConexion();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cerrarConexion() throws IOException {
        socket.close();
    }


    private void gestionarJugarLocal() throws IOException {

        System.out.println("Hay " + Servidor.jugadores + " jugadores");
        if (Servidor.jugadores % 2 == 0) {
            Local l = new Local(socket);
            l.start();
            Servidor.locales.add(l);
        } else {
            Local lo = Servidor.locales.get(Servidor.locales.size()-1);
            lo.setJugador(socket);
            new Partida(ss.accept(), ss.accept());
        }
        Servidor.jugadores++;
    }

    private boolean gestionarCrearSala() throws IOException, SQLException {
        int idAnfitrion = getIdFromToken(in.readUTF());
        int codigo;
        String AB = "0123456789";
        int size = 4;
        do {
            SecureRandom rnd = new SecureRandom();
            StringBuilder sb = new StringBuilder(size);
            for (int i = 0; i < size; i++)
                sb.append(AB.charAt(rnd.nextInt(AB.length())));

            codigo = Integer.parseInt(sb.toString());
        } while (existeCodigo(codigo));

        // conectar con la base de datos
        conectar();
        if (conexion == null) {
            return false;
        }
        if (conexion.isClosed()) {
            return false;
        }

        // si no ha habido errores, se crea una consulta
        String consulta = "INSERT INTO partidas (idanfitrion, codigo) VALUES (?,?)";
        PreparedStatement sentencia = conexion.prepareStatement(consulta);
        sentencia.setInt(1, idAnfitrion);
        sentencia.setInt(2, codigo);

        // se sustituyen los datos en la consulta y se ejecuta
        sentencia.executeUpdate();
        if (sentencia != null) {
            sentencia.close();
        }
        out.writeInt(codigo);
        desconectar();

        return true;

    }

    private boolean gestionarUnirse() throws IOException, SQLException {
        int idInvitado = getIdFromToken(in.readUTF());
        int codigo = in.readInt();
        System.out.println("El idInv es " + idInvitado + "y el codigo " + codigo);
        if (existeCodigo(codigo)) {
            System.out.println("existe");
            conectar();
            if (conexion == null) {
                return false;
            }
            if (conexion.isClosed()) {
                return false;
            }

            // si no ha habido errores, se crea una consulta
            String consulta = "UPDATE partidas SET idinvitado = ? WHERE codigo = ?";
            PreparedStatement sentencia = conexion.prepareStatement(consulta);
            sentencia.setInt(1, idInvitado);
            sentencia.setInt(2, codigo);

            // se sustituyen los datos en la consulta y se ejecuta
            sentencia.executeUpdate();
            if (sentencia != null) {
                sentencia.close();
            }

            desconectar();

            return true;
        } else {

            System.out.println("no existe");
            out.writeInt(-1);
        }
        return false;
    }

    private boolean gestionarCambiarPass() throws IOException, SQLException {
        String user, oldPass, newPass;
        int id;
        user = in.readUTF();
        oldPass = in.readUTF();
        oldPass = Hash.hashear(oldPass);
        newPass = in.readUTF();
        newPass = Hash.hashear(newPass);
        id = inicioSesionBBDD(user, oldPass);
        if (id < 1){
            return false;
        }
        conectar();
        if (conexion == null) {
            return false;
        }
        if (conexion.isClosed()) {
            return false;
        }

        // si no ha habido errores, se crea una consulta
        String consulta = "UPDATE jugadores SET pass = ? WHERE idjugador = ?";
        PreparedStatement sentencia = conexion.prepareStatement(consulta);
        sentencia.setString(1, newPass);
        sentencia.setInt(2, id);
        // se sustituyen los datos en la consulta y se ejecuta
        sentencia.executeUpdate();
        if (sentencia != null) {
            sentencia.close();
        }
        desconectar();
        return true;
    }

    private void gestionarInicioSesion() throws IOException, SQLException {
        String user, pass;
        int id;
        boolean bool;
        user = in.readUTF();
        pass = in.readUTF();
        pass = Hash.hashear(pass);
        id = inicioSesionBBDD(user, pass);
        bool = id > 0;
        out.writeBoolean(bool);
        if (bool) {
            out.writeUTF(guardarToken(id));
        }
    }

    private void gestionarRegistro() throws IOException, SQLException {
        String user, pass;
        user = in.readUTF();
        pass = in.readUTF();
        pass = Hash.hashear(pass);

        int res = registroBBDD(user, pass);
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
                out.writeUTF(guardarToken(getIdFromUser(user)));
                break;

        }
    }

    /***
     * M�todo que gestiona el registro en la base de datos.
     *
     * @param user Nombre de usuario
     * @param pass Contrase�a (ya hasheada previamente)
     * @return int
     * @throws SQLException
     */
    private int registroBBDD(String user, String pass) throws SQLException {
        // conectar con la base de datos
        conectar();
        if (conexion == null) {
            return -1;
        }
        if (conexion.isClosed()) {
            return -2;
        }
        if (comprobarUnica(user))
            return 0;
        // si no ha habido errores, se crea una consulta
        String consulta = "INSERT INTO jugadores (user,pass) VALUES (?,?)";
        PreparedStatement sentencia = conexion.prepareStatement(consulta);
        sentencia.setString(1, user);
        sentencia.setString(2, pass);

        // se sustituyen los datos en la consulta y se ejecuta
        int numReg = sentencia.executeUpdate();
        if (sentencia != null) {
            sentencia.close();
        }
        desconectar();
        // se desconecta de la base de datos y se devuelve el n�mero de registros
        // afectados
        return numReg;
    }

    private int inicioSesionBBDD(String user, String pass) {
        // conecta con la base de datos
        conectar();
        // realiza la consulta de la tabla actual
        String consulta = "SELECT * FROM jugadores WHERE user = ? AND pass = ?";
        PreparedStatement sentencia;
        try {
            // realiza la consulta y la ejecuta
            sentencia = conexion.prepareStatement(consulta);
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
        desconectar();
        // ha habido un error; desconecta y devuelve null.
        return -1;
    }

    private void gestionarPedirDatos() throws IOException {
        int[] datos = pedirDatosBBDD(in.readUTF());
        out.writeInt(datos[0]);
        out.writeInt(datos[1]);
        out.writeInt(datos[2]);
        out.writeInt(datos[3]);
        out.writeInt(datos[4]);
        out.writeInt(datos[5]);
    }

    private int[] pedirDatosBBDD(String user) {
        int[] datos = new int[6];

        // conecta con la base de datos
        conectar();
        // realiza la consulta de la tabla actual
        String consulta = "SELECT * FROM jugadores WHERE user = ?";
        PreparedStatement sentencia = null;
        try {
            // realiza la consulta y la ejecuta
            sentencia = conexion.prepareStatement(consulta);
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
        desconectar();
        // ha habido un error; desconecta y devuelve null.
        return datos;
    }

    private String guardarToken(int id) throws SQLException {
        String token = generarToken();
        // conectar con la base de datos
        conectar();
        if (conexion == null) {
            return null;
        }
        if (conexion.isClosed()) {
            return null;
        }

        if (existeToken(id)) {
            borrarToken(id);
        }
        // si no ha habido errores, se crea una consulta
        String consulta = "INSERT INTO auth_tokens VALUES (?,?)";
        PreparedStatement sentencia = conexion.prepareStatement(consulta);
        sentencia.setInt(1, id);
        sentencia.setString(2, token);

        // se sustituyen los datos en la consulta y se ejecuta
        int numReg = sentencia.executeUpdate();
        if (sentencia != null) {
            sentencia.close();
        }
        desconectar();
        if (numReg > 0)
            return token;
        return null;

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

    private boolean comprobarUnica(String user) {

        // conecta con la base de datos
        conectar();
        // realiza la consulta de la tabla actual
        String consulta = "SELECT * FROM jugadores WHERE user = ?";
        PreparedStatement sentencia;
        try {
            // realiza la consulta y la ejecuta
            sentencia = conexion.prepareStatement(consulta);
            sentencia.setString(1, user);
            ResultSet res = sentencia.executeQuery();
            return res.next();
        } catch (SQLException e) {
            System.err.println("Error al consultar + " + e.toString());
        }
        desconectar();
        // ha habido un error; desconecta y devuelve null.
        return false;
    }

    static int getIdFromToken(String token) {
        // conecta con la base de datos
        conectar();
        // realiza la consulta de la tabla actual
        String consulta = "SELECT idjugador FROM auth_tokens WHERE token = ?";
        PreparedStatement sentencia;
        try {
            // realiza la consulta y la ejecuta
            sentencia = conexion.prepareStatement(consulta);
            sentencia.setString(1, token);
            ResultSet res = sentencia.executeQuery();
            if (res.next()) {
                return res.getInt("idjugador");
            }
            return 0;
        } catch (SQLException e) {
            System.err.println("Error al consultar + " + e.toString());
        }
        desconectar();
        // ha habido un error; desconecta y devuelve null.
        return -1;
    }

    static String getUserFromId(int id) {
        // conecta con la base de datos
        conectar();
        // realiza la consulta de la tabla actual
        String consulta = "SELECT user FROM jugadores WHERE idjugador = ?";
        PreparedStatement sentencia;
        try {
            // realiza la consulta y la ejecuta
            sentencia = conexion.prepareStatement(consulta);
            sentencia.setInt(1, id);
            ResultSet res = sentencia.executeQuery();
            if (res.next()) {
                return res.getString("user");
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar + " + e.toString());
        }
        desconectar();
        // ha habido un error; desconecta y devuelve null.
        return null;
    }

    static int getIdFromUser(String user) {
        // conecta con la base de datos
        conectar();
        // realiza la consulta de la tabla actual
        String consulta = "SELECT idjugador FROM jugadores WHERE user = ?";
        PreparedStatement sentencia;
        try {
            // realiza la consulta y la ejecuta
            sentencia = conexion.prepareStatement(consulta);
            sentencia.setString(1, user);
            ResultSet res = sentencia.executeQuery();
            if (res.next()) {
                return res.getInt("idjugador");
            }
            return 0;
        } catch (SQLException e) {
            System.err.println("Error al consultar + " + e.toString());
        }
        desconectar();
        // ha habido un error; desconecta y devuelve null.
        return -1;
    }

    private boolean existeCodigo(int codigo) {
        conectar();
        // realiza la consulta de la tabla actual
        String consulta = "SELECT codigo FROM partidas WHERE codigo = ?";
        PreparedStatement sentencia;
        try {
            // realiza la consulta y la ejecuta
            sentencia = conexion.prepareStatement(consulta);
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
        desconectar();
        // ha habido un error; desconecta y devuelve null.
        return false;
    }

    private boolean existeToken(int id) {
        conectar();
        // realiza la consulta de la tabla actual
        String consulta = "SELECT token FROM auth_tokens WHERE idjugador = ?";
        PreparedStatement sentencia;
        try {
            // realiza la consulta y la ejecuta
            sentencia = conexion.prepareStatement(consulta);
            sentencia.setInt(1, id);
            ResultSet res = sentencia.executeQuery();
            if (res.next()) {
                return res.getString("token").length() > 0;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error al consultar + " + e.toString());
        }
        desconectar();
        // ha habido un error; desconecta y devuelve null.
        return false;
    }

    private boolean borrarToken(int id) {
        conectar();
        // realiza la consulta de la tabla actual
        String consulta = "DELETE FROM auth_tokens WHERE idjugador = ?";
        PreparedStatement sentencia;
        try {
            // realiza la consulta y la ejecuta
            sentencia = conexion.prepareStatement(consulta);
            sentencia.setInt(1, id);
            sentencia.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al consultar + " + e.toString());
        }
        desconectar();
        // ha habido un error; desconecta y devuelve null.
        return false;
    }

    /**
     * M�todo que conecta con la base de datos.
     */
    static void conectar() {
        String url = null;
        String user = null;
        String passw = null;

        String host = System.getenv("MYSQL_HOST");
        if (host == null) {
            url = "jdbc:mysql://localhost/chessforandroid";
            user = "chess4android";
            //prohibido mirar
            passw = "ttDfdqxmf3ynnSozTMYSzH7H2ncfwD";
        } else {

            user = System.getenv("MYSQL_USER");
            passw = System.getenv("MYSQL_PASSWORD");
            url = "jdbc:mysql://"+ host + "/" + System.getenv("MYSQL_DB");
        }
        // MYSQL_DB
        // MYSQL_USER
        // MYSQL_PASSWORD

        conexion = null;
        try {
            // conexi�n con localhost a trav�s de mi puerto
            conexion = DriverManager.getConnection(url, user, passw);
        } catch (SQLException e) {
            System.out.println("No se ha podido conectar con la base de datos. \n" + e.toString());
        }
    }

    /**
     * M�todo que desconecta de la base de datos
     */
    static void desconectar() {
        try {
            conexion.close();
        } catch (SQLException e) {
            System.out.println("No he podido desconectar de la base de datos");
            e.printStackTrace();
        }
    }

}
