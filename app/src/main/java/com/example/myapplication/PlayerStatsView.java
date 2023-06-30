package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PlayerStatsView extends AppCompatActivity {

    String playerId, firstname, lastname, teamName, teamLogo, season;
    ImageView teamLogoImg;

    JSONObject resultObject;
    JSONArray jsonArray = null;
    private List<PlayerStatsObject> jsonObject = new ArrayList<>();
    private List<PlayerStatsObject> parsedPlayerStats;

    DecimalFormat decimalFormat = new DecimalFormat("#.##");

    private TextView playerNameTv, teamTv, totalGamesTv, avgPointsTv, avgReboundsTv, avgAssistsTv, avgFoulsTv, avgStealsTv;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_stats_view);

        Intent i = getIntent();
        playerId = i.getStringExtra("playerId");
        firstname = i.getStringExtra("firstname");
        lastname = i.getStringExtra("lastname");
        teamName = i.getStringExtra("teamName");
        teamLogo = i.getStringExtra("teamLogo");
        season = i.getStringExtra("season");
        System.out.println("TeamLogo: "+ teamLogo);


        playerNameTv = findViewById(R.id.playerNameTv);
        playerNameTv.setText(firstname + " " + lastname);
        teamTv = findViewById(R.id.teamTv);
        teamTv.setText(teamName);
        totalGamesTv = findViewById(R.id.totalGamesTv);
        avgPointsTv = findViewById(R.id.avgPointsTv);
        avgReboundsTv = findViewById(R.id.avgReboundsTv);
        avgAssistsTv = findViewById(R.id.avgAssistsTv);
        avgFoulsTv = findViewById(R.id.avgFoulsTv);
        avgStealsTv = findViewById(R.id.avgStealsTv);

        teamLogoImg = findViewById(R.id.teamLogoIv);
        new DownloadImageFromInternet((ImageView) findViewById(R.id.teamLogoIv)).execute(teamLogo);

        String playerStatsUrl = "https://api-nba-v1.p.rapidapi.com/players/statistics?id="+playerId+"&season="+season;
        System.out.println("url " + playerStatsUrl);

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("X-RapidAPI-Key", "4b30019b27msh7b1b5ffd53b790fp1e60bejsnb7b3a6236a8f");
        client.addHeader("X-RapidAPI-Host", "api-nba-v1.p.rapidapi.com");
        client.get(playerStatsUrl, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String str = new String(responseBody);
                try {
                    parsedPlayerStats = returnParsedJSONObject(str);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("Error loading API: " + error);
            }
        });
    }

    private List<PlayerStatsObject> returnParsedJSONObject(String result) throws JSONException {
        PlayerStatsObject playerStatsObject;
        double pointSum = 0, totRebSum = 0, totAstSum = 0, totFoulSum = 0, totStealSum = 0;
        try {
            resultObject = new JSONObject(result);
            System.out.println("Preparsed PlayerStats JSON object " +
                    resultObject);
            // set up json Array to be parsed
            jsonArray = resultObject.optJSONArray("response");
            System.out.println("PlayerStats jsonArray: " + jsonArray);
        }catch (JSONException e){
            System.out.println("Err" + e);
        }

        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject jsonChildNode;
            try{
                jsonChildNode = jsonArray.getJSONObject(i);
                double points = jsonChildNode.getDouble("points");
                double totReb = jsonChildNode.getDouble("totReb");
                double totAst = jsonChildNode.getDouble("assists");
                double totFoul = jsonChildNode.getDouble("pFouls");
                double totSteal = jsonChildNode.getDouble("steals");
                pointSum += points;
                totRebSum += totReb;
                totAstSum += totAst;
                totFoulSum += totFoul;
                totStealSum += totSteal;

            }catch(JSONException e){
                System.out.println("Err" + e);
            }
        }

        double avgPoints = jsonArray.length() != 0 ? pointSum/jsonArray.length() : 0.0;
        double avgRebounds = jsonArray.length() != 0 ? totRebSum/jsonArray.length() : 0.0;
        double avgAssists = jsonArray.length() != 0 ? totAstSum/jsonArray.length() : 0.0;
        double avgFoul = jsonArray.length() != 0 ? totFoulSum/jsonArray.length() : 0.0;
        double avgSteal = jsonArray.length() != 0 ? totStealSum/jsonArray.length() : 0.0;

        String formattedPoints = decimalFormat.format(avgPoints);
        String formattedRebs = decimalFormat.format(avgRebounds);
        String formattedAsts = decimalFormat.format(avgAssists);
        String formattedFouls = decimalFormat.format(avgFoul);
        String formattedSteals = decimalFormat.format(avgSteal);

        totalGamesTv.setText(String.valueOf(jsonArray.length()));
        avgPointsTv.setText(formattedPoints);
        avgReboundsTv.setText(String.valueOf(formattedRebs));
        avgAssistsTv.setText(String.valueOf(formattedAsts));
        avgFoulsTv.setText(String.valueOf(formattedFouls));
        avgStealsTv.setText(String.valueOf(formattedSteals));
        playerStatsObject = new PlayerStatsObject(avgPoints, avgRebounds, avgAssists, avgFoul, avgSteal);
        jsonObject.add(playerStatsObject);
        return jsonObject;
    }
}