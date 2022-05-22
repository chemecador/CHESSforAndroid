package juego;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

/**
 * Clase Jugador.
 */
public class Jugador {

    // atributos de jugador
    private String user;
    private int id;
    // atributos  para interaccionar con el
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public Jugador(Socket socket) throws IOException, SQLException {
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.id = db.DB.getIdFromToken(recibirString());
    }

    public Jugador(Socket socket, int id) throws IOException {
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.id = id;
    }


    public String getUser() {
        if (this.user == null) {
            try {
                this.user = db.DB.getUserFromId(this.id);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket soc) throws IOException {
        this.socket = soc;
        this.in = new DataInputStream(soc.getInputStream());
        this.out = new DataOutputStream(soc.getOutputStream());
    }

    public void enviarString(String s) throws IOException {
        out.writeUTF(s);
    }

    public void enviarInt(int x) throws IOException {
        out.writeInt(x);
    }

    public void enviarBool(boolean b) throws IOException {
        out.writeBoolean(b);
    }

    public String recibirString() throws IOException {
        return in.readUTF();
    }

    public int recibirInt() throws IOException {
        return in.readInt();
    }

    public boolean recibirBool() throws IOException {
        return in.readBoolean();
    }
}
