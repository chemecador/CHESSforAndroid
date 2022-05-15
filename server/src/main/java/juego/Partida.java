package juego;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import juego.casillas.*;
import servidor.Servidor;
import db.DB;

import java.io.IOException;
import java.util.Random;

public class Partida {

    //conexiones
    private static final Logger logger = LogManager.getLogger();
    private static final int NUM_FILAS = 8;
    private static final int NUM_COLUMNAS = 8;
    private Jugador anfitrion;
    private Jugador invitado;


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

        anfitrion = j1;
        invitado = j2;

        id1 = anfitrion.getId();
        id2 = invitado.getId();



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
                    s = anfitrion.recibirString();
                    if (s.equalsIgnoreCase("tablas")) {
                        invitado.enviarString(s);
                        System.out.println(DB.getUserFromId(id1) + "(id1) ofrece tablas");
                        juez.turnoBlancas = !juez.turnoBlancas;
                    } else if (s.equalsIgnoreCase("aceptadas")) {
                        System.out.println("Tablas aceptadas");
                        invitado.enviarBool(true);
                        DB.gestionarFinal(movs, id1, id2, true);
                        DB.actualizarJugadores(id1, id2, true);
                        break;
                    } else if (s.equalsIgnoreCase("rechazadas")) {
                        System.out.println("Tablas rechazadas");
                        invitado.enviarBool(false);
                        juez.turnoBlancas = !juez.turnoBlancas;
                    } else if (s.equalsIgnoreCase("rendirse") || s.equalsIgnoreCase("abandonar")) {
                        System.out.println("Gana " + id2);
                        fin = true;
                        invitado.enviarString("rendirse");
                        DB.gestionarFinal(movs, id2, id1, false);
                        DB.actualizarJugadores(id2, id1, false);
                    } else {
                        juez.tablero = juez.stringToInt(s);
                        juez.intToCasillas(juez.tablero);
                        invitado.enviarString(s);
                        movs = anfitrion.recibirString();
                        invitado.enviarString(movs);
                        juez.turnoBlancas = !juez.turnoBlancas;
                        //comprobar jaques, etc
                        if (juez.buscarRey(juez.casillas, !j1EsBlancas) == null) {
                            System.out.println("Gana " + id1);
                            //es jaque mate
                            anfitrion.enviarBool(true);
                            invitado.enviarBool(true);
                            fin = true;
                            DB.gestionarFinal(movs, id1, id2, false);
                            DB.actualizarJugadores(id1, id2, false);
                        } else {
                            //no es jaque mate
                            anfitrion.enviarBool(false);
                            invitado.enviarBool(false);
                            juez.puedeMover = juez.puedeMover(juez.casillas, !j1EsBlancas);
                            juez.jaque = juez.comprobarJaque(juez.casillas);
                            //envio a cada jugador si es jaque, si esta ahogado y los movs
                            anfitrion.enviarBool(juez.jaque);
                            invitado.enviarBool(juez.jaque);
                            anfitrion.enviarBool(juez.puedeMover);
                            invitado.enviarBool(juez.puedeMover);
                        }
                    }
                } else {
                    //turno del jugador 2
                    s = invitado.recibirString();
                    if (s.equalsIgnoreCase("tablas")) {
                        anfitrion.enviarString(s);
                        System.out.println(DB.getUserFromId(id2) + "(id2) ofrece tablas");
                        juez.turnoBlancas = !juez.turnoBlancas;
                    } else if (s.equalsIgnoreCase("aceptadas")) {
                        System.out.println("Tablas aceptadas");
                        anfitrion.enviarBool(true);
                        DB.gestionarFinal(movs, id1, id2, true);
                        DB.actualizarJugadores(id1, id2, true);
                        break;
                    } else if (s.equalsIgnoreCase("rechazadas")) {
                        System.out.println("Tablas rechazadas");
                        anfitrion.enviarBool(false);
                        juez.turnoBlancas = !juez.turnoBlancas;
                    } else if (s.equalsIgnoreCase("rendirse") || s.equalsIgnoreCase("abandonar")) {
                        System.out.println("Gana " + id1);
                        fin = true;
                        anfitrion.enviarString("rendirse");
                        DB.gestionarFinal(movs, id1, id2, false);
                        DB.actualizarJugadores(id1, id2, false);
                    } else {
                        juez.tablero = juez.stringToInt(s);
                        juez.intToCasillas(juez.tablero);

                        anfitrion.enviarString(s);
                        movs = invitado.recibirString();
                        anfitrion.enviarString(movs);
                        juez.turnoBlancas = !juez.turnoBlancas;
                        //comprobar jaques, etc
                        if (juez.buscarRey(juez.casillas, j1EsBlancas) == null) {
                            System.out.println("Gana " + id2);
                            //es jaque mate
                            anfitrion.enviarBool(true);
                            invitado.enviarBool(true);
                            fin = true;
                            DB.gestionarFinal(movs, id2, id1, false);
                            DB.actualizarJugadores(id2, id1, false);
                        } else {
                            //no es jaque mate
                            anfitrion.enviarBool(false);
                            invitado.enviarBool(false);
                            juez.puedeMover = juez.puedeMover(juez.casillas, j1EsBlancas);
                            juez.jaque = juez.comprobarJaque(juez.casillas);
                            //envio a cada jugador si es jaque, si esta ahogado y los movs
                            anfitrion.enviarBool(juez.jaque);
                            invitado.enviarBool(juez.jaque);
                            anfitrion.enviarBool(juez.puedeMover);
                            invitado.enviarBool(juez.puedeMover);
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
            String user1 = anfitrion.getUser();
            String user2 = invitado.getUser();
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
            anfitrion.enviarString(user1);
            anfitrion.enviarString(user2);
            invitado.enviarString(user2);
            invitado.enviarString(user1);
            if (j1EsBlancas) {
                anfitrion.enviarBool(true);
                invitado.enviarBool(false);
            } else {
                invitado.enviarBool(true);
                anfitrion.enviarBool(false);
            }
            logger.info("Todo listo, ¡a jugar! {} vs {}", anfitrion.getUser(), invitado.getUser());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
