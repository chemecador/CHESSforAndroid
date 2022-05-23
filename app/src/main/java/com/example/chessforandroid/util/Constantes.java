package com.example.chessforandroid.util;

import android.graphics.Color;

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
     * Modo debug para ejecutar el servidor en local
     */
    public static boolean debug = false;

    /**
     * Color de las casillas blancas por defecto
     */
    public static final int BLANCAS_POR_DEFECTO = Color.parseColor("#DDDDDD");

    /**
     * Color de las casillas negras por defecto
     */
    public static final int NEGRAS_POR_DEFECTO = Color.parseColor("#A4552A");

    /**
     * Color de las casillas blancas elegido por el usuario
     */
    public static int COLOR_BLANCAS = BLANCAS_POR_DEFECTO;

    /**
     * Color de las casillas negras elegido por el usuario
     */
    public static int COLOR_NEGRAS = NEGRAS_POR_DEFECTO;
    /**
     * Posicion en el combobox del color de las casillas blancas elegido por el usuario
     */
    public static int COLOR_BLANCAS_POSICION = 0;

    /**
     * Posicion en el combobox del color de las casillas negras elegido por el usuario
     */
    public static int COLOR_NEGRAS_POSICION = 0;
}
