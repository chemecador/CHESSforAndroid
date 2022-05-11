package servidor;

import casillas.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import static servidor.ClientHandler.*;

public class Partida {

    private static final int NUM_FILAS = 8;
    private static final int NUM_COLUMNAS = 8;
    private DataOutputStream out1; //flujo de salida de datos con el jugador 1 (a partir de ahora, J1)
    private DataOutputStream out2;//flujo de salida con el jugador 2 (a partir de ahora, J2)
    private DataInputStream in1; //flujo de entrada de datos con J1
    private DataInputStream in2; //flujo de entrada con J2
    private Socket[] jugadores; //lista de jugadores


    private int id1;
    private int id2;
    private boolean fin;
    private boolean j1EsBlancas;
    private Juez juez;
    private String movs;


    public Partida(Socket j1, Socket j2) {
        try {
            fin = false;
            juez = new Juez();
            //se inicializa el serversocket y el arraylist de jugadores
            jugadores = new Socket[2];

            //se inicializan los flujos de entrada y salida
            in1 = new DataInputStream(j1.getInputStream());
            out1 = new DataOutputStream(j1.getOutputStream());

            //se suma el jugador al arraylist
            jugadores[0] = j1;

            //lo mismo
            in2 = new DataInputStream(j2.getInputStream());
            out2 = new DataOutputStream(j2.getOutputStream());
            jugadores[1] = j2;

            String t1 = in1.readUTF();
            id1 = ClientHandler.getIdFromToken(t1);
            String t2 = in2.readUTF();
            id2 = ClientHandler.getIdFromToken(t2);

            datosIniciales();
            System.out.println("entro en jugar");
            jugar();

        } catch (SocketException se) {
            se.printStackTrace();
            System.err.println("Conexion con el cliente cerrada.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void crearCasillas() {
        juez.tablero = new int[NUM_FILAS][NUM_COLUMNAS];
        juez.casillas = new Casilla[NUM_FILAS][NUM_COLUMNAS];
        for (int i = 0; i < NUM_FILAS; i++) {
            for (int j = 0; j < NUM_COLUMNAS; j++) {
                juez.casillas[i][j] = new Casilla(i, j);
            }
        }
    }


    private void jugar() {
        String s;
        try {
            while (!fin) {
                if (juez.turnoBlancas == j1EsBlancas) {
                    //juegan las blancas
                    s = in1.readUTF();
                    if (s.equalsIgnoreCase("tablas")) {
                        out2.writeUTF(s);
                        System.out.println(ClientHandler.getUserFromId(id1) + "(id1) ofrece tablas");
                        juez.turnoBlancas = !juez.turnoBlancas;
                    } else if (s.equalsIgnoreCase("aceptadas")) {
                        System.out.println("Tablas aceptadas");
                        out2.writeBoolean(true);
                        gestionarFinal(id1, id2, true);
                        actualizarJugadores(id1, id2, true);
                        break;
                    } else if (s.equalsIgnoreCase("rechazadas")) {
                        System.out.println("Tablas rechazadas");
                        out2.writeBoolean(false);
                    } else if (s.equalsIgnoreCase("rendirse") || s.equalsIgnoreCase("abandonar")) {
                        fin = true;
                        System.out.println("Gana " + id2);
                        out2.writeUTF("rendirse");
                        gestionarFinal(id2, id1, false);
                        actualizarJugadores(id2, id1, false);
                    } else {
                        juez.tablero = juez.stringToInt(s);
                        juez.intToCasillas(juez.tablero);
                        out2.writeUTF(s);
                        movs = in1.readUTF();
                        out2.writeUTF(movs);
                        juez.turnoBlancas = !juez.turnoBlancas;
                        //comprobar jaques, etc
                        if (juez.buscarRey(juez.casillas, !j1EsBlancas) == null){
                            System.out.println("Gana " + id1);
                            //es jaque mate
                            out1.writeBoolean(true);
                            out2.writeBoolean(true);
                            fin = true;
                            gestionarFinal(id1, id2, false);
                            actualizarJugadores(id1, id2, false);
                        } else {
                            //no es jaque mate
                            out1.writeBoolean(false);
                            out2.writeBoolean(false);
                            juez.puedeMover = juez.puedeMover(juez.casillas, !j1EsBlancas);
                            juez.jaque = juez.comprobarJaque(juez.casillas);
                            //envio a cada jugador si es jaque, si esta ahogado y los movs
                            out1.writeBoolean(juez.jaque);
                            out2.writeBoolean(juez.jaque);
                            out1.writeBoolean(juez.puedeMover);
                            out2.writeBoolean(juez.puedeMover);
                        }

                    }
                } else {
                    s = in2.readUTF();
                    if (s.equalsIgnoreCase("tablas")) {
                        out1.writeUTF(s);
                        System.out.println(ClientHandler.getUserFromId(id2) + "(id2) ofrece tablas");
                        juez.turnoBlancas = !juez.turnoBlancas;
                    } else if (s.equalsIgnoreCase("aceptadas")) {
                        System.out.println("Tablas aceptadas");
                        out1.writeBoolean(true);
                        gestionarFinal(id1, id2, true);
                        actualizarJugadores(id1, id2, true);
                        break;
                    } else if (s.equalsIgnoreCase("rechazadas")) {
                        System.out.println("Tablas rechazadas");
                        out1.writeBoolean(false);
                    } else if (s.equalsIgnoreCase("rendirse") || s.equalsIgnoreCase("abandonar")) {
                        System.out.println("Gana " + id1);
                        fin = true;
                        gestionarFinal(id1, id2, false);
                        actualizarJugadores(id1, id2, false);
                        out1.writeUTF(s);
                        movs = in2.readUTF();
                        out1.writeUTF(movs);
                    } else {
                        juez.tablero = juez.stringToInt(s);
                        juez.intToCasillas(juez.tablero);

                        out1.writeUTF(s);
                        movs = in2.readUTF();
                        out1.writeUTF(movs);
                        juez.turnoBlancas = !juez.turnoBlancas;
                        //comprobar jaques, etc
                        if (juez.buscarRey(juez.casillas, j1EsBlancas) == null) {
                            System.out.println("Gana " + id2);
                            //es jaque mate
                            out1.writeBoolean(true);
                            out2.writeBoolean(true);
                            fin = true;
                            gestionarFinal(id2, id1, false);
                            actualizarJugadores(id2, id1, false);
                        } else {
                            //no es jaque mate
                            out1.writeBoolean(false);
                            out2.writeBoolean(false);
                            juez.puedeMover = juez.puedeMover(juez.casillas, j1EsBlancas);
                            juez.jaque = juez.comprobarJaque(juez.casillas);
                            //envio a cada jugador si es jaque, si esta ahogado y los movs
                            out1.writeBoolean(juez.jaque);
                            out2.writeBoolean(juez.jaque);
                            out1.writeBoolean(juez.puedeMover);
                            out2.writeBoolean(juez.puedeMover);
                        }

                    }
                }
            }
        } catch (IOException e) {
            /*try {
                this.jugadores[0].close();
                this.jugadores[1].close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }*/
            e.printStackTrace();
        }
    }

    private boolean actualizarJugadores(int id1, int id2, boolean tablas) {
        String consulta;
        PreparedStatement sentencia;
        try {
            conectar();
            if (conexion == null) {
                return false;
            }

            if (conexion.isClosed()) {
                return false;
            }

            if (tablas) {
                // si no ha habido errores, se crea una consulta
                consulta = "UPDATE jugadores SET tablas = tablas + 1, jugadas = jugadas + 1 WHERE idjugador = ?";
                sentencia = conexion.prepareStatement(consulta);
                sentencia.setInt(1, id1);
                // se sustituyen los datos en la consulta y se ejecuta
                sentencia.executeUpdate();

                consulta = "UPDATE jugadores SET tablas = tablas + 1, jugadas = jugadas + 1 WHERE idjugador = ?";
                sentencia = conexion.prepareStatement(consulta);
                sentencia.setInt(1, id2);
                sentencia.executeUpdate();
            } else {
                // si no ha habido errores, se crea una consulta
                consulta = "UPDATE jugadores SET elo = elo + 10, victorias = victorias + 1, jugadas = jugadas + 1 WHERE idjugador = ?";
                sentencia = conexion.prepareStatement(consulta);
                sentencia.setInt(1, id1);
                // se sustituyen los datos en la consulta y se ejecuta
                sentencia.executeUpdate();


                consulta = "UPDATE jugadores SET elo = elo - 10, derrotas = derrotas + 1, jugadas = jugadas + 1 WHERE idjugador = ?";
                sentencia = conexion.prepareStatement(consulta);
                sentencia.setInt(1, id2);
                // se sustituyen los datos en la consulta y se ejecuta
                sentencia.executeUpdate();
            }
            if (sentencia != null) {
                sentencia.close();
            }
            desconectar();
            jugadores[0].close();
            jugadores[1].close();
            Servidor.jugadores = Servidor.jugadores - 2;
            System.out.println("sockets cerrados");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean gestionarFinal(int id1, int id2, boolean tablas) {
        try {
            conectar();
            if (conexion == null) {
                return false;
            }

            if (conexion.isClosed()) {
                return false;
            }

            // si no ha habido errores, se crea una consulta
            String consulta = "INSERT INTO partidas (movimientos, idanfitrion, idinvitado, ganador, perdedor) VALUES (?,?,?,?,?)";
            PreparedStatement sentencia = conexion.prepareStatement(consulta);
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
            desconectar();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }

    private void datosIniciales() {

        crearCasillas();
        try {
            juez.turnoBlancas = true;
            j1EsBlancas = new Random().nextBoolean();
            String user1 = ClientHandler.getUserFromId(id1);
            System.out.println("user1 es :" + user1);
            String user2 = ClientHandler.getUserFromId(id2);
            System.out.println("user2 es : " + user2);
            out1.writeUTF(user1);
            out1.writeUTF(user2);
            out2.writeUTF(user2);
            out2.writeUTF(user1);
            if (j1EsBlancas) {
                out1.writeBoolean(true);
                out2.writeBoolean(false);
            } else {
                out2.writeBoolean(true);
                out1.writeBoolean(false);
            }
            System.out.println("Partida entre " + user1 + " ( " + j1EsBlancas + ") y " + user2 + " ( " + !j1EsBlancas + ") comenzada");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
