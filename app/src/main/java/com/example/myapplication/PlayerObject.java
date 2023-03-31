package com.example.myapplication;

public class PlayerObject {

    String id;
    String firstname;
    String lastname;
    String teamName;

    public PlayerObject (String id, String firstname, String lastname, String teamName){
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.teamName = teamName;
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
}
