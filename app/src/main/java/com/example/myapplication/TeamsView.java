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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TeamsView extends AppCompatActivity {

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

    String userId;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference favoritesRef;

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
                checkFavTeams(parsedEastTeams, jsonEastTeamsCustomAdapter);

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
                checkFavTeams(parsedWestTeams, jsonWestTeamsCustomAdapter);

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("Error loading API: " + error);
            }
        });

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

    private void checkFavTeams(ArrayList<TeamObject> teamObjectList, CustomTeamAdapter adapter) {
        System.out.println("All teams: " + teamObjectList);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        favoritesRef = database.getReference("users/" + userId + "/favorites");

        favoritesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(TeamObject teamObject: teamObjectList) {
                    if (snapshot.hasChild(teamObject.getId())) {
                        teamObject.setfavStatus("1");
                    } else {
                        teamObject.setfavStatus("0");
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The read failed: " + error);
            }
        });
    }


}