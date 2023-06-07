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

public class TeamStatsView extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    DecimalFormat decimalFormat = new DecimalFormat("#.##");

    String teamId, teamLogo, teamName, season;
    ImageView teamLogoImg;
    TextView teamNameTv;
    TextView totalGames, totalPoints, ppgTv, fgTv, fgpTv, ftTv, ftpTV;

    JSONObject resultObject;
    JSONArray jsonArray = null;
    private List<TeamStatsObject> jsonObject = new ArrayList<>();
    private List<TeamStatsObject> parsedTeamStats;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_stats_view);

        Intent intent = getIntent();
        teamId = intent.getStringExtra("teamId");
        teamName = intent.getStringExtra("teamName");
        teamLogo = intent.getStringExtra("teamLogo");
        season = intent.getStringExtra("season");

        teamLogoImg = findViewById(R.id.teamLogoIv);
        teamNameTv = findViewById(R.id.teamNameTv);
        totalGames = findViewById(R.id.totalGames);
        totalPoints = findViewById(R.id.totalPoints);
        ppgTv = findViewById(R.id.ppg);
        fgTv = findViewById(R.id.fg);
        fgpTv = findViewById(R.id.fgp);
        ftTv = findViewById(R.id.ft);
        ftpTV = findViewById(R.id.ftp);

        new DownloadImageFromInternet((ImageView) findViewById(R.id.teamLogoIv)).execute(teamLogo);
        teamNameTv.setText(teamName);

        String teamStatsUrl = "https://api-nba-v1.p.rapidapi.com/teams/statistics?id="+teamId+"&season="+season;
        System.out.println("url " + teamStatsUrl);

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("X-RapidAPI-Key", "4b30019b27msh7b1b5ffd53b790fp1e60bejsnb7b3a6236a8f");
        client.addHeader("X-RapidAPI-Host", "api-nba-v1.p.rapidapi.com");
        client.get(teamStatsUrl, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String str = new String(responseBody);
                try {
                    parsedTeamStats = returnParsedJSONObject(str);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                Double ppg = Double.parseDouble(parsedTeamStats.get(0).totalPoints.toString()) /Double.parseDouble(parsedTeamStats.get(0).totalGames.toString());
                String formattedPpg = decimalFormat.format(ppg);
                Double roundedPpg = Double.parseDouble(formattedPpg);

                totalGames.setText(parsedTeamStats.get(0).totalGames.toString());
                totalPoints.setText(parsedTeamStats.get(0).totalPoints.toString());
                ppgTv.setText(formattedPpg);
                fgTv.setText( parsedTeamStats.get(0).fgm.toString() +"/"+ parsedTeamStats.get(0).fga.toString());
                fgpTv.setText(parsedTeamStats.get(0).fgp.toString() +"%");
                ftTv.setText(parsedTeamStats.get(0).ftm.toString() +"/"+ parsedTeamStats.get(0).fta.toString() );
                ftpTV.setText(parsedTeamStats.get(0).ftp.toString() +"%");
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

    private List<TeamStatsObject> returnParsedJSONObject (String result) throws JSONException {

        TeamStatsObject teamStatsObject;

        try {
            resultObject = new JSONObject(result);
            System.out.println("Preparsed TeamStats JSON object " +
                    resultObject);
            // set up json Array to be parsed
            jsonArray = resultObject.optJSONArray("response");
            System.out.println("TeamStats jsonArray: " + jsonArray);
        }catch (JSONException e){
            System.out.println("Err" + e);
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonChildNode;
            try{
                jsonChildNode = jsonArray.getJSONObject(i);
                String totalGames = jsonChildNode.getString("games");
                String totalPoints = jsonChildNode.getString("points");
                String fgm = jsonChildNode.getString("fgm");
                String fga = jsonChildNode.getString("fga");
                String fgp = jsonChildNode.getString("fgp");
                String ftm = jsonChildNode.getString("ftm");
                String fta = jsonChildNode.getString("fta");
                String ftp = jsonChildNode.getString("ftp");

                teamStatsObject = new TeamStatsObject(totalGames, totalPoints, fgm,
                        fga, fgp, ftm, fta, ftp);
                jsonObject.add(teamStatsObject);
            }catch(JSONException e){
                System.out.println("Err" + e);
            }
        }
        return jsonObject;
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
