package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PlayersView extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    private GridView players;
    private TextView teamNameTv;
    private ImageView teamLogoIv;
    JSONObject resultObject;
    JSONArray jsonArray = null;
    private List<PlayerObject> jsonObject = new ArrayList<>();
    private List<PlayerObject> parsedPlayers;
    private Button teamStatsBtn;
    Spinner spinner;

    RelativeLayout item;

    String teamId, teamName, teamLogo, season;
    String idPName, firstname, lastname, playersUrl;

    int updates = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players_view);

        players = findViewById(R.id.players);
        teamNameTv = findViewById(R.id.teamName);

        Intent intent = getIntent();
        teamId = intent.getStringExtra("teamId");
        teamName = intent.getStringExtra("teamName");
        teamLogo = intent.getStringExtra("teamLogo");

        spinner = findViewById(R.id.seasonSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.seasons, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemSelected = parent.getItemAtPosition(position).toString();
                String season = itemSelected.replaceAll("[^0-9]", "");
                getData(season);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                season = "2021";
            }
        });


        teamStatsBtn = findViewById(R.id.teamStatsBtn);
        teamStatsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String selectedSeason = spinner.getSelectedItem().toString();
                String seasonForTeamStats = selectedSeason.replaceAll("[^0-9]", "");

                Intent intent = new Intent(PlayersView.this, TeamStatsView.class);
                intent.putExtra("teamId", teamId);
                intent.putExtra("teamName", teamName);
                intent.putExtra("teamLogo", teamLogo);
                intent.putExtra("season", seasonForTeamStats);
                startActivity(intent);
            }
        });

        teamNameTv.setText(teamName);
        new DownloadImageFromInternet((ImageView) findViewById(R.id.teamLogo)).execute(teamLogo);

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

    private List<PlayerObject> returnParsedJSONObject (String result, String season){

        PlayerObject newPlayerObject;

        try {
            resultObject = new JSONObject(result);
            System.out.println("Preparsed Players JSON object " +
                    resultObject);
            // set up json Array to be parsed
            jsonArray = resultObject.optJSONArray("response");
            System.out.println("Players jsonArray: " + jsonArray);
        } catch (JSONException e) {
            System.out.println("Err" + e);
        }

        for (int i = 0; i < jsonArray.length(); i++){
            JSONObject jsonChildNode;

            try{
                jsonChildNode = jsonArray.getJSONObject(i);
                idPName = jsonChildNode.getString("id");
                firstname = jsonChildNode.getString("firstname");
                lastname = jsonChildNode.getString("lastname");

                newPlayerObject = new PlayerObject(idPName, firstname, lastname, teamName, teamLogo, season);
                jsonObject.add(newPlayerObject);
            }catch(JSONException e){
                System.out.println("Err" + e);
            }
        }
        return jsonObject;
    }

    private void getData(String season){
        updates++;
        System.out.println("updates: "+ updates);
        playersUrl = "https://api-nba-v1.p.rapidapi.com/players?team="+teamId+"&season="+season;
        System.out.println("url " + playersUrl);

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("X-RapidAPI-Key", "4b30019b27msh7b1b5ffd53b790fp1e60bejsnb7b3a6236a8f");
        client.addHeader("X-RapidAPI-Host", "api-nba-v1.p.rapidapi.com");
        client.get(playersUrl, new AsyncHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String str = new String(responseBody);
                if(updates > 1){
                    parsedPlayers.clear();
                    updatePlayerData(parsedPlayers);
                }else{
                   //Do nothing
                }
                parsedPlayers = returnParsedJSONObject(str, season);
                CustomPlayerAdapter jsonPlayersCustomAdapter = new CustomPlayerAdapter(PlayersView.this, parsedPlayers);
                players.setAdapter(jsonPlayersCustomAdapter);
                jsonPlayersCustomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("Error loading API: " + error);
            }
        });

    }

    private void updatePlayerData(List<PlayerObject> playerObjectList) {
        CustomPlayerAdapter adapter = (CustomPlayerAdapter) players.getAdapter();
        if (adapter != null) {
            adapter.updateData(playerObjectList);
        }
    }

    // override the onOptionsItemSelected()
    // function to implement
    // the item click listener callback
    // to open and close the navigation
    // drawer when the icon is clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}