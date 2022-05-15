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
    private boolean anfitrionEsBlancas;
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


    private void jugar() throws IOException {
        String mensaje;
        while (!fin) {
            //turno del anfitrion, espero respuesta
            if (juez.turnoBlancas == anfitrionEsBlancas){
                mensaje = anfitrion.recibirString();
            } else {
                mensaje = invitado.recibirString();
            }
            if (mensaje.equalsIgnoreCase("tablas")) {
                ofrecerTablas(juez.turnoBlancas == anfitrionEsBlancas);
            } else if (mensaje.equalsIgnoreCase("aceptadas")) {
                aceptarTablas(juez.turnoBlancas == anfitrionEsBlancas);
                fin = true;
            } else if (mensaje.equalsIgnoreCase("rechazadas")) {
                rechazarTablas(juez.turnoBlancas == anfitrionEsBlancas);
            } else if (mensaje.equalsIgnoreCase("rendirse") || mensaje.equalsIgnoreCase("abandonar")) {
                abandonar(juez.turnoBlancas == anfitrionEsBlancas);
                fin = true;
            } else {
                logger.debug("a {} le toca mover ", juez.turnoBlancas==anfitrionEsBlancas);
                enviarMov(juez.turnoBlancas == anfitrionEsBlancas, mensaje);
                juez.turnoBlancas = !juez.turnoBlancas;
                //comprobar jaques, etc
                if (juez.buscarRey(juez.casillas, !anfitrionEsBlancas) == null) {
                    //es jaque mate
                    jaqueMate(juez.turnoBlancas == anfitrionEsBlancas);
                    fin = true;
                } else {
                    //no es jaque mate
                    noJaqueMate();
                }
            }
        }
    }

    private void noJaqueMate() throws IOException {
        anfitrion.enviarBool(false);
        invitado.enviarBool(false);
        juez.puedeMover = juez.puedeMover(juez.casillas, !anfitrionEsBlancas);
        juez.jaque = juez.comprobarJaque(juez.casillas);
        //envio a cada jugador si es jaque y si esta ahogado
        anfitrion.enviarBool(juez.jaque);
        invitado.enviarBool(juez.jaque);
        anfitrion.enviarBool(juez.puedeMover);
        invitado.enviarBool(juez.puedeMover);
    }

    private void jaqueMate(boolean esAnfitrion) throws IOException {
        anfitrion.enviarBool(true);
        invitado.enviarBool(true);
        if (esAnfitrion) {
            logger.info("El jugador {} ha ganado a {}", anfitrion.getUser(), invitado.getUser());
            DB.registrarResultado(movs, anfitrion.getId(), invitado.getId(), false);
            DB.actualizarStats(anfitrion.getId(), invitado.getId(), false);
            DB.actualizarNivel(anfitrion.getId());
        } else {
            logger.info("El jugador {} ha ganado a {}", invitado.getUser(), anfitrion.getUser());
            DB.registrarResultado(movs, invitado.getId(), anfitrion.getId(), false);
            DB.actualizarStats(invitado.getId(), anfitrion.getId(), false);
            DB.actualizarNivel(invitado.getId());
        }
    }

    private void enviarMov(boolean esAnfitrion, String mensaje) throws IOException {
        juez.tablero = juez.stringToTablero(mensaje);
        juez.intToCasillas(juez.tablero);
        if (esAnfitrion) {
            invitado.enviarString(mensaje);
            movs = anfitrion.recibirString();
            invitado.enviarString(movs);
        } else {
            anfitrion.enviarString(mensaje);
            movs = invitado.recibirString();
            anfitrion.enviarString(movs);
        }
    }

    private void abandonar(boolean esAnfitrion) throws IOException {
        if (esAnfitrion) {
            logger.info("El jugador {} ha ganado a {}", invitado.getUser(), anfitrion.getUser());
            invitado.enviarString("rendirse");
            DB.registrarResultado(movs, invitado.getId(), anfitrion.getId(), false);
            DB.actualizarStats(invitado.getId(), anfitrion.getId(), false);
            DB.actualizarNivel(invitado.getId());
        } else {
            logger.info("El jugador {} ha ganado a {}", anfitrion.getUser(), invitado.getUser());
            anfitrion.enviarString("rendirse");
            DB.registrarResultado(movs, anfitrion.getId(), invitado.getId(), false);
            DB.actualizarStats(anfitrion.getId(), invitado.getId(), false);
            DB.actualizarNivel(anfitrion.getId());
        }
    }

    private void rechazarTablas(boolean esAnfitrion) throws IOException {
        if (esAnfitrion) {
            logger.info("{} acepta las tablas", anfitrion.getUser());
            invitado.enviarBool(false);
        } else {
            logger.info("{} acepta las tablas", invitado.getUser());
            anfitrion.enviarBool(false);
        }
        juez.turnoBlancas = !juez.turnoBlancas;
    }

    private void aceptarTablas(boolean esAnfitrion) throws IOException {
        if (esAnfitrion) {
            logger.info("{} acepta las tablas", anfitrion.getUser());
            invitado.enviarBool(true);
            DB.registrarResultado(movs, anfitrion.getId(), invitado.getId(), true);
            DB.actualizarStats(anfitrion.getId(), invitado.getId(), true);
        } else {
            logger.info("{} acepta las tablas", invitado.getUser());
            anfitrion.enviarBool(true);
            DB.registrarResultado(movs, invitado.getId(), anfitrion.getId(), true);
            DB.actualizarStats(invitado.getId(), anfitrion.getId(), true);
        }
    }

    private void ofrecerTablas(boolean esAnfitrion) throws IOException {
        if (esAnfitrion) {
            invitado.enviarString("tablas");
            logger.info("{} ofrece tablas", anfitrion.getUser());
        } else {
            anfitrion.enviarString("tablas");
            logger.info("{} ofrece tablas", invitado.getUser());
        }
        juez.turnoBlancas = !juez.turnoBlancas;
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
        anfitrionEsBlancas = new Random().nextBoolean();
        if (anfitrionEsBlancas) {
            anfitrion.enviarBool(true);
            invitado.enviarBool(false);
        } else {
            invitado.enviarBool(true);
            anfitrion.enviarBool(false);
        }
    }
}
