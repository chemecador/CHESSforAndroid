package juego;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import juego.casillas.*;
import servidor.Servidor;
import db.DB;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;


public class Partida {

    //conexiones
    private static final Logger logger = LogManager.getLogger();
    private static final int NUM_FILAS = 8;
    private static final int NUM_COLUMNAS = 8;
    private Jugador[] jugadores; //lista de jugadores


    //variables de la partida
    private static boolean haMovido;
    private int id1;
    private int id2;
    private boolean fin;
    private boolean j1EsBlancas;
    private Juez juez;
    private String movs;


    public Partida(Jugador j1, Jugador j2) {
        logger.info("Comienzo partida entre {} y {}", j1.getUser(), j2.getUser());
        fin = false;
        juez = new Juez();

        jugadores = new Jugador[2];
        jugadores[0] = j1;
        jugadores[1] = j2;

        id1 = jugadores[0].getId();
        id2 = jugadores[1].getId();



        datosIniciales();

        jugar();
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
                haMovido = false;
                if (juez.turnoBlancas == j1EsBlancas) {
                    //turno del jugador 1, espero respuesta
                    s = jugadores[0].recibirString();
                    if (s.equalsIgnoreCase("tablas")) {
                        jugadores[1].enviarString(s);
                        System.out.println(DB.getUserFromId(id1) + "(id1) ofrece tablas");
                        juez.turnoBlancas = !juez.turnoBlancas;
                    } else if (s.equalsIgnoreCase("aceptadas")) {
                        System.out.println("Tablas aceptadas");
                        jugadores[1].enviarBool(true);
                        DB.gestionarFinal(movs, id1, id2, true);
                        DB.actualizarJugadores(id1, id2, true);
                        break;
                    } else if (s.equalsIgnoreCase("rechazadas")) {
                        System.out.println("Tablas rechazadas");
                        jugadores[1].enviarBool(false);
                        juez.turnoBlancas = !juez.turnoBlancas;
                    } else if (s.equalsIgnoreCase("rendirse") || s.equalsIgnoreCase("abandonar")) {
                        System.out.println("Gana " + id2);
                        fin = true;
                        jugadores[1].enviarString("rendirse");
                        DB.gestionarFinal(movs, id2, id1, false);
                        DB.actualizarJugadores(id2, id1, false);
                    } else {
                        juez.tablero = juez.stringToInt(s);
                        juez.intToCasillas(juez.tablero);
                        jugadores[1].enviarString(s);
                        movs = jugadores[0].recibirString();
                        jugadores[1].enviarString(movs);
                        juez.turnoBlancas = !juez.turnoBlancas;
                        //comprobar jaques, etc
                        if (juez.buscarRey(juez.casillas, !j1EsBlancas) == null) {
                            System.out.println("Gana " + id1);
                            //es jaque mate
                            jugadores[0].enviarBool(true);
                            jugadores[1].enviarBool(true);
                            fin = true;
                            DB.gestionarFinal(movs, id1, id2, false);
                            DB.actualizarJugadores(id1, id2, false);
                        } else {
                            //no es jaque mate
                            jugadores[0].enviarBool(false);
                            jugadores[1].enviarBool(false);
                            juez.puedeMover = juez.puedeMover(juez.casillas, !j1EsBlancas);
                            juez.jaque = juez.comprobarJaque(juez.casillas);
                            //envio a cada jugador si es jaque, si esta ahogado y los movs
                            jugadores[0].enviarBool(juez.jaque);
                            jugadores[1].enviarBool(juez.jaque);
                            jugadores[0].enviarBool(juez.puedeMover);
                            jugadores[1].enviarBool(juez.puedeMover);
                        }

                    }
                } else {
                    //turno del jugador 2
                    s = jugadores[1].recibirString();
                    if (s.equalsIgnoreCase("tablas")) {
                        jugadores[0].enviarString(s);
                        System.out.println(DB.getUserFromId(id2) + "(id2) ofrece tablas");
                        juez.turnoBlancas = !juez.turnoBlancas;
                    } else if (s.equalsIgnoreCase("aceptadas")) {
                        System.out.println("Tablas aceptadas");
                        jugadores[0].enviarBool(true);
                        DB.gestionarFinal(movs, id1, id2, true);
                        DB.actualizarJugadores(id1, id2, true);
                        break;
                    } else if (s.equalsIgnoreCase("rechazadas")) {
                        System.out.println("Tablas rechazadas");
                        jugadores[0].enviarBool(false);
                        juez.turnoBlancas = !juez.turnoBlancas;
                    } else if (s.equalsIgnoreCase("rendirse") || s.equalsIgnoreCase("abandonar")) {
                        System.out.println("Gana " + id1);
                        fin = true;
                        jugadores[0].enviarString("rendirse");
                        DB.gestionarFinal(movs, id1, id2, false);
                        DB.actualizarJugadores(id1, id2, false);
                    } else {
                        juez.tablero = juez.stringToInt(s);
                        juez.intToCasillas(juez.tablero);

                        jugadores[0].enviarString(s);
                        movs = jugadores[1].recibirString();
                        jugadores[0].enviarString(movs);
                        juez.turnoBlancas = !juez.turnoBlancas;
                        //comprobar jaques, etc
                        if (juez.buscarRey(juez.casillas, j1EsBlancas) == null) {
                            System.out.println("Gana " + id2);
                            //es jaque mate
                            jugadores[0].enviarBool(true);
                            jugadores[1].enviarBool(true);
                            fin = true;
                            DB.gestionarFinal(movs, id2, id1, false);
                            DB.actualizarJugadores(id2, id1, false);
                        } else {
                            //no es jaque mate
                            jugadores[0].enviarBool(false);
                            jugadores[1].enviarBool(false);
                            juez.puedeMover = juez.puedeMover(juez.casillas, j1EsBlancas);
                            juez.jaque = juez.comprobarJaque(juez.casillas);
                            //envio a cada jugador si es jaque, si esta ahogado y los movs
                            jugadores[0].enviarBool(juez.jaque);
                            jugadores[1].enviarBool(juez.jaque);
                            jugadores[0].enviarBool(juez.puedeMover);
                            jugadores[1].enviarBool(juez.puedeMover);
                        }

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void datosIniciales() {

        crearCasillas();
        try {
            juez.turnoBlancas = true;
            j1EsBlancas = new Random().nextBoolean();
            String user1 = jugadores[0].getUser();
            String user2 = jugadores[1].getUser();
            if (user1 == null && user2 == null) {
                Servidor.jugadores = Servidor.jugadores - 2;
                logger.error("Partida abortada: user1: NULL, user2: NULL");
                return;
            }
            if (user1 == null) {
                Servidor.jugadores--;
                logger.error("Partida abortada: user1: {} , user2: {}" , user1, user2);
                return;
            }
            if (user2 == null) {
                Servidor.jugadores--;
                logger.error("Partida abortada: user1: {} , user2: {}" , user1, user2);
                return;
            }
            jugadores[0].enviarString(user1);
            jugadores[0].enviarString(user2);
            jugadores[1].enviarString(user2);
            jugadores[1].enviarString(user1);
            if (j1EsBlancas) {
                jugadores[0].enviarBool(true);
                jugadores[1].enviarBool(false);
            } else {
                jugadores[1].enviarBool(true);
                jugadores[0].enviarBool(false);
            }
            logger.info("Todo listo, ¡a jugar! {} vs {}", jugadores[0].getUser(), jugadores[1].getUser());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
