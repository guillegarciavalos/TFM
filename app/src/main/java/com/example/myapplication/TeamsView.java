package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TeamsView extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    private GridView eastTeamsGrid;
    private GridView westTeamsGrid;
    private Button logout;
    private Button eastTeamsBtn;
    private Button westTeamsBtn;
    private Button favTeamsBtn;
    JSONObject resultObjectEast;
    JSONObject resultObjectWest;
    JSONArray jsonArrayEast = null;
    JSONArray jsonArrayWest = null;
    private final ArrayList<TeamObject> jsonObjectEast = new ArrayList<>();
    private final ArrayList<TeamObject> jsonObjectWest = new ArrayList<>();
    private ArrayList<TeamObject> parsedEastTeams;
    private ArrayList<TeamObject> parsedWestTeams;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_teams_view);

        eastTeamsGrid = findViewById(R.id.eastTeamsGrid);
        ViewCompat.setNestedScrollingEnabled(eastTeamsGrid,true);
        westTeamsGrid = findViewById(R.id.westTeamsGrid);
        ViewCompat.setNestedScrollingEnabled(westTeamsGrid,true);
        logout = findViewById(R.id.logout);
        firebaseAuth = FirebaseAuth.getInstance();

        eastTeamsBtn = findViewById(R.id.eastTeamsBtn);
        westTeamsBtn = findViewById(R.id.westTeamsBtn);

        favTeamsBtn = findViewById(R.id.favTeamsBtn);

        eastTeamsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eastTeamsGrid.setVisibility(View.VISIBLE);
                westTeamsGrid.setVisibility(View.INVISIBLE);
            }
        });

        westTeamsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                westTeamsGrid.setVisibility(View.VISIBLE);
                eastTeamsGrid.setVisibility(View.INVISIBLE);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Intent i = new Intent(TeamsView.this, MainActivity.class);
                startActivity(i);
            }
        });

        favTeamsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TeamsView.this, FavoriteTeamsView.class);
                startActivity(i);
            }
        });

        String eastUrl = "https://api-nba-v1.p.rapidapi.com/teams?conference=East";
        String westUrl = "https://api-nba-v1.p.rapidapi.com/teams?conference=West";

        //GET Teams from Eastern Conference
        AsyncHttpClient clientEast = new AsyncHttpClient();
        clientEast.addHeader("X-RapidAPI-Key", "4b30019b27msh7b1b5ffd53b790fp1e60bejsnb7b3a6236a8f");
        clientEast.addHeader("X-RapidAPI-Host", "api-nba-v1.p.rapidapi.com");
        clientEast.get(eastUrl, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String str = new String(responseBody);
                parsedEastTeams = returnParsedJSONObjectEast(str);
                System.out.println("parsedEastTeams: "+ parsedEastTeams);
                CustomTeamAdapter jsonEastTeamsCustomAdapter = new CustomTeamAdapter(TeamsView.this, parsedEastTeams);
                eastTeamsGrid.setAdapter(jsonEastTeamsCustomAdapter);
                eastTeamsGrid.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("Error loading API: " + error);
            }
        });

        //GET Teams from Western Conference
        AsyncHttpClient clientWest = new AsyncHttpClient();
        clientWest.addHeader("X-RapidAPI-Key", "4b30019b27msh7b1b5ffd53b790fp1e60bejsnb7b3a6236a8f");
        clientWest.addHeader("X-RapidAPI-Host", "api-nba-v1.p.rapidapi.com");
        clientWest.get(westUrl, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String str = new String(responseBody);
                System.out.println("West str: " + str);
                parsedWestTeams = returnParsedJSONObjectWest(str);
                CustomTeamAdapter jsonWestTeamsCustomAdapter = new CustomTeamAdapter(TeamsView.this, parsedWestTeams);
                westTeamsGrid.setAdapter(jsonWestTeamsCustomAdapter);
                westTeamsGrid.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("Error loading API: " + error);
            }
        });

        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private ArrayList<TeamObject> returnParsedJSONObjectEast (String result){

        TeamObject newTeamObject;

        try {
            resultObjectEast = new JSONObject(result);
            System.out.println("Preparsed JSON object " +
                    resultObjectEast);
            // set up json Array to be parsed
            jsonArrayEast = resultObjectEast.optJSONArray("response");
            System.out.println("jsonArray: " + jsonArrayEast);
        } catch (JSONException e) {
            System.out.println("Err" + e);
        }

        for (int i = 0; i < jsonArrayEast.length(); i++){
            JSONObject jsonChildNode;

            try{
                jsonChildNode = jsonArrayEast.getJSONObject(i);
                String id = jsonChildNode.getString("id");
                String name = jsonChildNode.getString("name");
                String logo = jsonChildNode.getString("logo");

                if(!logo.equals("null")){
                    newTeamObject = new TeamObject(id, name, logo);
                    jsonObjectEast.add(newTeamObject);
                }else{}
            }catch(JSONException e){
                System.out.println("Err" + e);
            }
        }
        return jsonObjectEast;

    }

    private ArrayList<TeamObject> returnParsedJSONObjectWest (String result){

        TeamObject newTeamObject;

        try {
            resultObjectWest = new JSONObject(result);
            System.out.println("Preparsed JSON object " +
                    resultObjectWest);
            // set up json Array to be parsed
            jsonArrayWest = resultObjectWest.optJSONArray("response");
            System.out.println("jsonArray: " + jsonArrayWest);
        } catch (JSONException e) {
            System.out.println("Err" + e);
        }

        for (int i = 0; i < jsonArrayWest.length(); i++){
            JSONObject jsonChildNode;

            try{
                jsonChildNode = jsonArrayWest.getJSONObject(i);
                String id = jsonChildNode.getString("id");
                String name = jsonChildNode.getString("name");
                String logo = jsonChildNode.getString("logo");

                if(!logo.equals("null")){
                    newTeamObject = new TeamObject(id, name, logo);
                    jsonObjectWest.add(newTeamObject);
                }else{}
            }catch(JSONException e){
                System.out.println("Err" + e);
            }
        }
        return jsonObjectWest;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }

    // override the onOptionsItemSelected()
    // function to implement
    // the item click listener callback
    // to open and close the navigation
    // drawer when the icon is clicked
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        System.out.println("onOptionsItemSelected: item selected");

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()){
            case R.id.nav_account:
                System.out.println("My account selected");
                return true;
            case R.id.nav_logout:
                firebaseAuth.signOut();
                Intent intent = new Intent(TeamsView.this, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.nav_teams:
                Toast.makeText(this, "Teams selected", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}