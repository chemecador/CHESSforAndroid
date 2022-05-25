package juego;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import juego.casillas.*;
import db.DB;
import servidor.Servidor;
import servidor.SocketHandler;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Random;

/**
 * Clase Partida. Se encarga de gestionar una partida online entre dos jugadores
 */
public class Partida {

    // atributos de la partida
    private static final Logger logger = LogManager.getLogger();
    private static final int NUM_FILAS = 8;
    private static final int NUM_COLUMNAS = 8;
    private Jugador anfitrion;
    private Jugador invitado;
    private int codigo;

    private boolean anfitrionEsBlancas;
    private Juez juez;
    private String movs;
    private int nMovs;


    // constructor con los dos jugadores y el codigo de la sala
    public Partida(Jugador j1, Jugador j2, int codigo) throws IOException {

        this.codigo = codigo;
        anfitrion = j1;
        invitado = j2;
        juez = new Juez();

        // se asignan los dos nuevos sockets, ya que al cambiar de activity se han perdido los antiguos
        j1.setSocket(SocketHandler.getSocket());
        j2.setSocket(SocketHandler.getSocket());

        String user1 = null;
        // es posible que el socket que se le asigne al jugador 1 no coincida con el primer socket que llega...

        try {
            // asi que se le pide a ambos jugadores que envien su nombre
            user1 = DB.getUserFromId(DB.getIdFromToken(j1.recibirString()));
        } catch (SQLException e) {
            logger.error("No he podido leer el usuario desde el token", e);
        }

        j2.recibirString();

        // si el nombre que envian no coincide con el nombre que se le habia adjudicado al principio...
        if (!user1.equalsIgnoreCase(j1.getUser())){
            // se cambian los sockets
            Socket alt = j1.getSocket();
            j1.setSocket(j2.getSocket());
            j2.setSocket(alt);
        }

        // si el codigo es 0, los rivales no son amigos, entraron buscando partida online
        if (this.codigo == 0) {
            logger.info("Comienzo partida online entre {} y {}", anfitrion.getUser(), invitado.getUser());
        } else {
            logger.info("Comienzo partida amistosa entre {} y {}", anfitrion.getUser(), invitado.getUser());
        }

        crearCasillas();
        enviarDatosIniciales();
        jugar();
    }


    /**
     * Metodo que se encarga de leer los movimientos de un jugador y enviarselos al otro, asi como
     * de hacer las comprobaciones de que esos movimientos son legales y los posibles jaques
     * @throws IOException IOException
     */
    private void jugar() throws IOException {
        String mensaje;

        while (true) {
            nMovs++;
            // si es el turno del anfitiron...
            if (juez.turnoBlancas == anfitrionEsBlancas) {
                // ... leo su string
                mensaje = anfitrion.recibirString();
            } else {
                // si no, leo el string del invitado
                mensaje = invitado.recibirString();
            }

            if (mensaje.equalsIgnoreCase("tablas")) {

                ofrecerTablas(juez.turnoBlancas == anfitrionEsBlancas);

            } else if (mensaje.equalsIgnoreCase("aceptadas")) {

                aceptarTablas(juez.turnoBlancas == anfitrionEsBlancas);
                break;

            } else if (mensaje.equalsIgnoreCase("rechazadas")) {

                rechazarTablas(juez.turnoBlancas == anfitrionEsBlancas);

            } else if (mensaje.equalsIgnoreCase("rendirse") || mensaje.equalsIgnoreCase("abandonar")) {

                abandonar(juez.turnoBlancas == anfitrionEsBlancas);
                break;

            } else {

                // se envia el movimiento al otro jugador
                enviarMov(juez.turnoBlancas == anfitrionEsBlancas, mensaje);

                // se cambia el turno
                juez.turnoBlancas = !juez.turnoBlancas;

                // si han capturado el rey del jugador al que le toca mover...
                if (juez.buscarRey(juez.casillas, juez.turnoBlancas) == null) {

                    // es jaque mate, se acaba la partida
                    jaqueMate(juez.turnoBlancas != anfitrionEsBlancas);
                    break;
                } else {
                    // no es jaque mate
                    noJaqueMate();
                }
            }
        }

        // ha acabado la partida, si era una partida amistosa, se elimina del ArrayList
        if (codigo != 0) {

            // forma abreviada recomendada por el IDE
            Servidor.friendLobbies.removeIf(fl -> fl.getCodigo() == codigo);

            logger.info("La partida con el codigo {} ha sido eliminada. Quedan {} partidas",
                    codigo, Servidor.friendLobbies.size());
        }
    }

    /**
     * Metodo que se encarga de gestionar lo que pasa cuando no hay jaque mate
     * @throws IOException IOException
     */
    private void noJaqueMate() throws IOException {

        // se envia a cada jugador que no es jaque mate
        anfitrion.enviarBool(false);
        invitado.enviarBool(false);

        juez.puedeMover = juez.puedeMover(juez.casillas, juez.turnoBlancas == anfitrionEsBlancas);
        juez.jaque = juez.comprobarJaque(juez.casillas);

        // se envia a cada jugador si es jaque y si puede mover
        anfitrion.enviarBool(juez.jaque);
        invitado.enviarBool(juez.jaque);
        anfitrion.enviarBool(juez.puedeMover);
        invitado.enviarBool(juez.puedeMover);
    }
    /**
     * Metodo que se encarga de gestionar lo que pasa cuando hay jaque mate
     * @param esAnfitrion True si es anfitrion, False si no lo es
     * @throws IOException IOException
     */
    private void jaqueMate(boolean esAnfitrion) throws IOException {

        // se envia a cada jugador que no es jaque mate
        anfitrion.enviarBool(true);
        invitado.enviarBool(true);

        if (esAnfitrion) {

            logger.info("El jugador {} ha ganado a {} por jaque mate en {} movimientos",
                    anfitrion.getUser(), invitado.getUser(), nMovs);

            DB.registrarResultado(movs, anfitrion.getId(), invitado.getId(), false);
            DB.actualizarStats(anfitrion.getId(), invitado.getId(), false);
            DB.actualizarNivel(anfitrion.getId());

            // si ha ganado en menos de 10 movimmientos, ha completado el logro numero 4
            if (nMovs < 10) {
                // si no estaba ya completado, se registra
                if (!DB.logroCompletado(anfitrion.getId(), 4)) {
                    DB.completarLogro(anfitrion.getId(), 4);
                }
            }
            // si ha ganado en mas de 40 movimmientos, ha completado el logro numero 5
            if (nMovs > 40) {
                if (!DB.logroCompletado(anfitrion.getId(), 5)) {
                    DB.completarLogro(anfitrion.getId(), 5);
                }
            }

        } else {

            logger.info("El jugador {} ha ganado a {} por jaque mate en {} movimientos",
                    invitado.getUser(), anfitrion.getUser(), nMovs);

            DB.registrarResultado(movs, invitado.getId(), anfitrion.getId(), false);
            DB.actualizarStats(invitado.getId(), anfitrion.getId(), false);
            DB.actualizarNivel(invitado.getId());

            if (nMovs < 10) {
                if (!DB.logroCompletado(invitado.getId(), 4)) {
                    DB.completarLogro(invitado.getId(), 4);
                }
            }
            if (nMovs > 40) {
                if (!DB.logroCompletado(invitado.getId(), 5)) {
                    DB.completarLogro(invitado.getId(), 5);
                }
            }
        }
    }

    /**
     * Metodo que se encarga de enviar el movimiento de un jugador a su rival
     * @param esAnfitrion True si es anfitrion, False si no lo es
     * @param mensaje Mensaje a enviar
     * @throws IOException IOException
     */
    private void enviarMov(boolean esAnfitrion, String mensaje) throws IOException {

        juez.tablero = juez.stringToInt(mensaje);
        juez.actualizarCasillas(juez.tablero);

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

    /**
     * Metodo que se encarga de gestionar el abandono de uno de los jugadores
     * @param esAnfitrion True si es anfitrion, False si no lo es
     * @throws IOException IOException
     */
    private void abandonar(boolean esAnfitrion) throws IOException {

        if (esAnfitrion) {

            logger.info("El jugador {} ha ganado a {} por abandono del rival tras {} movimientos",
                    invitado.getUser(), anfitrion.getUser(), nMovs);

            invitado.enviarString("rendirse");

            DB.registrarResultado(movs, invitado.getId(), anfitrion.getId(), false);
            DB.actualizarStats(invitado.getId(), anfitrion.getId(), false);
            DB.actualizarNivel(invitado.getId());

            if (nMovs > 40) {
                if (!DB.logroCompletado(invitado.getId(), 5)) {
                    DB.completarLogro(invitado.getId(), 5);
                }
            }

        } else {

            logger.info("El jugador {} ha ganado a {} por abandono del rival tras {} movimientos",
                    anfitrion.getUser(), invitado.getUser(), nMovs);

            anfitrion.enviarString("rendirse");

            DB.registrarResultado(movs, anfitrion.getId(), invitado.getId(), false);
            DB.actualizarStats(anfitrion.getId(), invitado.getId(), false);
            DB.actualizarNivel(anfitrion.getId());

            if (nMovs > 40) {
                if (!DB.logroCompletado(anfitrion.getId(), 5)) {
                    DB.completarLogro(anfitrion.getId(), 5);
                }
            }
        }
    }

    /**
     * Metodo que se encarga de rechazar las tablas
     * @param esAnfitrion True si es anfitrion, False si no lo es
     * @throws IOException IOException
     */
    private void rechazarTablas(boolean esAnfitrion) throws IOException {
        if (esAnfitrion) {
            logger.info("{} rechaza las tablas", anfitrion.getUser());
            invitado.enviarBool(false);
        } else {
            logger.info("{} rechaza las tablas", invitado.getUser());
            anfitrion.enviarBool(false);
        }
        juez.turnoBlancas = !juez.turnoBlancas;
    }

    /**
     * Metodo que se encarga de aceptar las tablas
     * @param esAnfitrion True si es anfitrion, False si no lo es
     * @throws IOException IOException
     */
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

    /**
     * Metodo que se encarga de ofrecer tablas
     * @param esAnfitrion True si es anfitrion, False si no lo es
     * @throws IOException IOException
     */
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


    /**
     * Metodo que se encarga de crear las casillas
     */
    private void crearCasillas() {
        juez.tablero = new int[NUM_FILAS][NUM_COLUMNAS];
        juez.casillas = new Casilla[NUM_FILAS][NUM_COLUMNAS];

        for (int i = 0; i < NUM_FILAS; i++) {
            for (int j = 0; j < NUM_COLUMNAS; j++) {
                juez.casillas[i][j] = new Casilla(i, j);
            }
        }
    }

    /**
     * Metodo que se encarga de enviar los datos iniciales
     * @throws IOException IOException
     */
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
