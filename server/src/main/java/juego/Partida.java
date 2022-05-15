package juego;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import juego.casillas.*;
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
    private boolean fin;
    private boolean j1EsBlancas;
    private Juez juez;
    private String movs;


    public Partida(Jugador j1, Jugador j2) throws IOException {
        anfitrion = j1;
        invitado = j2;
        fin = false;
        juez = new Juez();

        logger.info("Comienzo partida entre {} y {}", anfitrion.getUser(), invitado.getUser());

        crearCasillas();
        enviarDatosIniciales();
        jugar();
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
                        logger.info(DB.getUserFromId(anfitrion.getId()) + "(anfitrion.getId()) ofrece tablas");
                        juez.turnoBlancas = !juez.turnoBlancas;
                    } else if (s.equalsIgnoreCase("aceptadas")) {
                        logger.info("Tablas aceptadas");
                        invitado.enviarBool(true);
                        DB.gestionarFinal(movs, anfitrion.getId(), invitado.getId(), true);
                        DB.actualizarJugadores(anfitrion.getId(), invitado.getId(), true);
                        break;
                    } else if (s.equalsIgnoreCase("rechazadas")) {
                        logger.info("Tablas rechazadas");
                        invitado.enviarBool(false);
                        juez.turnoBlancas = !juez.turnoBlancas;
                    } else if (s.equalsIgnoreCase("rendirse") || s.equalsIgnoreCase("abandonar")) {
                        logger.info("El jugador {} ha ganado a {}", invitado.getUser(), anfitrion.getUser());
                        fin = true;
                        invitado.enviarString("rendirse");
                        DB.gestionarFinal(movs, invitado.getId(), anfitrion.getId(), false);
                        DB.actualizarJugadores(invitado.getId(), anfitrion.getId(), false);
                        DB.actualizarNivel(invitado.getId());
                    } else {
                        juez.tablero = juez.stringToInt(s);
                        juez.intToCasillas(juez.tablero);
                        invitado.enviarString(s);
                        movs = anfitrion.recibirString();
                        invitado.enviarString(movs);
                        juez.turnoBlancas = !juez.turnoBlancas;
                        //comprobar jaques, etc
                        if (juez.buscarRey(juez.casillas, !j1EsBlancas) == null) {
                            logger.info("El jugador {} ha ganado a {}", anfitrion.getUser(), invitado.getUser());
                            //es jaque mate
                            anfitrion.enviarBool(true);
                            invitado.enviarBool(true);
                            fin = true;
                            DB.gestionarFinal(movs, anfitrion.getId(), invitado.getId(), false);
                            DB.actualizarJugadores(anfitrion.getId(), invitado.getId(), false);
                            DB.actualizarNivel(anfitrion.getId());
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
                        logger.info(DB.getUserFromId(invitado.getId()) + "(invitado.getId()) ofrece tablas");
                        juez.turnoBlancas = !juez.turnoBlancas;
                    } else if (s.equalsIgnoreCase("aceptadas")) {
                        logger.info("Tablas aceptadas");
                        anfitrion.enviarBool(true);
                        DB.gestionarFinal(movs, anfitrion.getId(), invitado.getId(), true);
                        DB.actualizarJugadores(anfitrion.getId(), invitado.getId(), true);
                        break;
                    } else if (s.equalsIgnoreCase("rechazadas")) {
                        logger.info("Tablas rechazadas");
                        anfitrion.enviarBool(false);
                        juez.turnoBlancas = !juez.turnoBlancas;
                    } else if (s.equalsIgnoreCase("rendirse") || s.equalsIgnoreCase("abandonar")) {
                        logger.info("El jugador {} ha ganado a {}", anfitrion.getUser(), invitado.getUser());
                        fin = true;
                        anfitrion.enviarString("rendirse");
                        DB.gestionarFinal(movs, anfitrion.getId(), invitado.getId(), false);
                        DB.actualizarJugadores(anfitrion.getId(), invitado.getId(), false);
                        DB.actualizarNivel(anfitrion.getId());
                    } else {
                        juez.tablero = juez.stringToInt(s);
                        juez.intToCasillas(juez.tablero);

                        anfitrion.enviarString(s);
                        movs = invitado.recibirString();
                        anfitrion.enviarString(movs);
                        juez.turnoBlancas = !juez.turnoBlancas;
                        //comprobar jaques, etc
                        if (juez.buscarRey(juez.casillas, j1EsBlancas) == null) {
                            logger.info("El jugador {} ha ganado a {}", invitado.getUser(), anfitrion.getUser());
                            //es jaque mate
                            anfitrion.enviarBool(true);
                            invitado.enviarBool(true);
                            fin = true;
                            DB.gestionarFinal(movs, invitado.getId(), anfitrion.getId(), false);
                            DB.actualizarJugadores(invitado.getId(), anfitrion.getId(), false);
                            DB.actualizarNivel(invitado.getId());
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


    private void crearCasillas() {
        juez.tablero = new int[NUM_FILAS][NUM_COLUMNAS];
        juez.casillas = new Casilla[NUM_FILAS][NUM_COLUMNAS];
        for (int i = 0; i < NUM_FILAS; i++) {
            for (int j = 0; j < NUM_COLUMNAS; j++) {
                juez.casillas[i][j] = new Casilla(i, j);
            }
        }
    }

    private void enviarDatosIniciales() throws IOException {

        anfitrion.enviarString(anfitrion.getUser());
        anfitrion.enviarString(invitado.getUser());
        invitado.enviarString(invitado.getUser());
        invitado.enviarString(anfitrion.getUser());

        juez.turnoBlancas = true;
        j1EsBlancas = new Random().nextBoolean();
        if (j1EsBlancas) {
            anfitrion.enviarBool(true);
            invitado.enviarBool(false);
        } else {
            invitado.enviarBool(true);
            anfitrion.enviarBool(false);
        }
    }
}
