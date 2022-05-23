package com.example.chessforandroid.util;

public class Constantes {
    /**
     * Número de filas del tablero
     */
    public static final int NUM_FILAS = 8;
    /**
     * Número de columnas del tablero
     */
    public static final int NUM_COLUMNAS = 8;
    /**
     * IP a la que se conecta el servidor
     */
    public static String ip = "chess-game-api.paesa.es";
    /**
     * IP local del servidor (mi ordenador)
     */
    public static String ipLocal = "192.168.1.144";

    /**
     * Puerto en el que escucha
     */
    public static int PUERTO = 5566;

    /**
     * Puerto en el que escucha para gestionar una partida
     */
    public static int PUERTO_PARTIDA = 5567;

    /**
     * modo debug para ejecutar el servidor en local
     */
    public static boolean debug = false;
}
