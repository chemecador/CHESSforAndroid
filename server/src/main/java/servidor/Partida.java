package servidor;

import casillas.*;
import util.DB;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Partida {

    private static final Logger logger = Logger.getLogger(Partida.class.getName());
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
            id1 = DB.getIdFromToken(t1);
            String t2 = in2.readUTF();
            id2 = DB.getIdFromToken(t2);

            datosIniciales();
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
                        System.out.println(DB.getUserFromId(id1) + "(id1) ofrece tablas");
                        juez.turnoBlancas = !juez.turnoBlancas;
                    } else if (s.equalsIgnoreCase("aceptadas")) {
                        System.out.println("Tablas aceptadas");
                        out2.writeBoolean(true);
                        DB.gestionarFinal(movs, id1, id2, true);
                        DB.actualizarJugadores(id1, id2, true);
                        break;
                    } else if (s.equalsIgnoreCase("rechazadas")) {
                        System.out.println("Tablas rechazadas");
                        out2.writeBoolean(false);
                        juez.turnoBlancas = !juez.turnoBlancas;
                    } else if (s.equalsIgnoreCase("rendirse") || s.equalsIgnoreCase("abandonar")) {
                        System.out.println("Gana " + id2);
                        fin = true;
                        out2.writeUTF("rendirse");
                        DB.gestionarFinal(movs,id2, id1, false);
                        DB.actualizarJugadores(id2, id1, false);
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
                            DB.gestionarFinal(movs,id1, id2, false);
                            DB.actualizarJugadores(id1, id2, false);
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
                        System.out.println(DB.getUserFromId(id2) + "(id2) ofrece tablas");
                        juez.turnoBlancas = !juez.turnoBlancas;
                    } else if (s.equalsIgnoreCase("aceptadas")) {
                        System.out.println("Tablas aceptadas");
                        out1.writeBoolean(true);
                        DB.gestionarFinal(movs,id1, id2, true);
                        DB.actualizarJugadores(id1, id2, true);
                        break;
                    } else if (s.equalsIgnoreCase("rechazadas")) {
                        System.out.println("Tablas rechazadas");
                        out1.writeBoolean(false);
                        juez.turnoBlancas = !juez.turnoBlancas;
                    } else if (s.equalsIgnoreCase("rendirse") || s.equalsIgnoreCase("abandonar")) {
                        System.out.println("Gana " + id1);
                        fin = true;
                        out1.writeUTF("rendirse");
                        DB.gestionarFinal(movs,id1, id2, false);
                        DB.actualizarJugadores(id1, id2, false);
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
                            DB.gestionarFinal(movs,id2, id1, false);
                            DB.actualizarJugadores(id2, id1, false);
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
    private void datosIniciales() {

        crearCasillas();
        try {
            juez.turnoBlancas = true;
            j1EsBlancas = new Random().nextBoolean();
            String user1 = DB.getUserFromId(id1);
            String user2 = DB.getUserFromId(id2);
            if (user1 == null && user2 == null){
                Servidor.jugadores = Servidor.jugadores - 2;
                logger.log(Level.SEVERE, "Partida abordada: user1: " + user1 + ", user2: " + user2);
                return;
            }
            if (user1 == null){
                Servidor.jugadores--;
                logger.log(Level.SEVERE, "Partida abordada: user1: " + user1 + ", user2: " + user2);
                return;
            }
            if (user2 == null){
                Servidor.jugadores--;
                logger.log(Level.SEVERE, "Partida abordada: user1: " + user1 + ", user2: " + user2);
                return;
            }
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
