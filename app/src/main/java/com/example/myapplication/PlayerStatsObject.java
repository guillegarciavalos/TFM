package com.example.myapplication;

public class PlayerStatsObject {

    double points, totReb, ast, fouls, steals;

    public PlayerStatsObject(double points, double totReb, double ast, double fouls, double steals){
        this.points = points;
        this.totReb = totReb;
        this.ast = ast;
        this.fouls = fouls;
        this.steals = steals;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public double getTotReb() {return totReb;}

    public void setTotReb(double totReb) {this.totReb = totReb;}

    public double getAst() {return ast;}

    public void setAst(double ast) {this.ast = ast;}

    public double getFouls() {return fouls;}

    public void setFouls(double fouls) {this.fouls = fouls;}

    public double getSteals() {return steals;}

    public void setSteals(double steals) {this.steals = steals;}
}
