package com.example.chessforandroid.util;

public class RankingItem {
    private int position;
    private String user;
    private String elo;

    public RankingItem(int position, String user, String elo) {
        this.position = position;
        this.user = user;
        this.elo = elo;
    }

    public RankingItem() {
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
