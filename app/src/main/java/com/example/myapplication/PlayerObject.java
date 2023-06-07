package com.example.myapplication;

public class PlayerObject {

    String id,firstname,lastname, teamName, teamLogo, season ;

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public PlayerObject (String id, String firstname, String lastname, String teamName, String teamLogo, String season){
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.teamName = teamName;
        this.teamLogo = teamLogo;
        this.season = season;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getTeamName() {return teamName;}

    public void setTeamName(String teamName) {this.teamName = teamName;}

    public String getTeamLogo() {return teamLogo;}

    public void setTeamLogo(String teamLogo) {this.teamLogo = teamLogo;}
}
