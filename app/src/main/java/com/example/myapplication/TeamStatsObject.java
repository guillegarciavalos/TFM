package com.example.myapplication;

public class TeamStatsObject {

    String totalGames;
    String totalPoints;
    String fgm;
    String fga;
    String fgp;
    String ftm;
    String fta;
    String ftp;

    public TeamStatsObject(String totalGames, String totalPoints, String fgm,
                           String fga, String fgp, String ftm, String fta,
                           String ftp) {
        this.totalGames = totalGames;
        this.totalPoints = totalPoints;
        this.fgm = fgm;
        this.fga = fga;
        this.fgp = fgp;
        this.ftm = ftm;
        this.fta = fta;
        this.ftp = ftp;
    }


    public String getTotalGames() {
        return totalGames;
    }

    public void setTotalGames(String totalGames) {
        this.totalGames = totalGames;
    }

    public String getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(String totalPoints) {
        this.totalPoints = totalPoints;
    }

    public String getFgm() {
        return fgm;
    }

    public void setFgm(String fgm) {
        this.fgm = fgm;
    }

    public String getFga() {
        return fga;
    }

    public void setFga(String fga) {
        this.fga = fga;
    }

    public String getFgp() {
        return fgp;
    }

    public void setFgp(String fgp) {
        this.fgp = fgp;
    }

    public String getFtm() {
        return ftm;
    }

    public void setFtm(String ftm) {
        this.ftm = ftm;
    }

    public String getFta() {
        return fta;
    }

    public void setFta(String fta) {
        this.fta = fta;
    }

    public String getFtp() {
        return ftp;
    }

    public void setFtp(String ftp) {
        this.ftp = ftp;
    }


}
