package com.example.chessforandroid.util;

/**
 * Clase RankingItem. Contiene la informacion de cada fila que aparecera en el ranking
 */
public class RankingItem {
    /**
     * Posicion en la clasificacion
     */
    private int position;

    /**
     * Nombre del usuario
     */
    private String user;

    /**
     * ELO del usuario
     */
    private String elo;

    public RankingItem(){}

    public RankingItem(int position, String user, String elo) {
        this.position = position;
        this.user = user;
        this.elo = elo;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getElo() {
        return elo;
    }

    public void setElo(String elo) {
        this.elo = elo;
    }
}
