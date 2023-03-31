package com.example.myapplication;

public class TeamObject {

    String id;
    String name;
    String logo;
    String favStatus = "0";

    public TeamObject(String id, String name, String logo) {
        this.id = id;
        this.name = name;
        this.logo = logo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getfavStatus() {return favStatus;}

    public void setfavStatus(String favorite) {this.favStatus = favorite;}
}
