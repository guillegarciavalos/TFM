package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class FavoriteTeamsView extends AppCompatActivity {

    private GridView favTeamsGrid;

    private ArrayList<TeamObject> favTeamsList;

    String userId, teamId, teamName, teamLogo;

    private FirebaseAuth firebaseAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference favoritesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_teams_view);

        favTeamsList = new ArrayList<>();

        favTeamsGrid = findViewById(R.id.favTeamsGrid);
        ViewCompat.setNestedScrollingEnabled(favTeamsGrid,true);

        CustomFavTeamAdapter jsonFavTeamsCustomAdapter = new CustomFavTeamAdapter(FavoriteTeamsView.this, favTeamsList);
        favTeamsGrid.setAdapter(jsonFavTeamsCustomAdapter);

        firebaseAuth = FirebaseAuth.getInstance();

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        favoritesRef = database.getReference("users/"+userId+"/favorites");

        favoritesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                System.out.println("Entra en onDataChange");
                favTeamsList.clear();
                for(DataSnapshot child : dataSnapshot.getChildren()){

                    TeamObject teamObject = child.getValue(TeamObject.class);
                    favTeamsList.add(teamObject);
                    teamId = teamObject.id;
                    teamName = teamObject.name;
                    teamLogo = teamObject.logo;
               }
                jsonFavTeamsCustomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The read failed: " + error);
            }
        });
    }
}