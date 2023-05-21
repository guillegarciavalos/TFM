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
    Map<String, Object> map = null;

    String userId;
    String teamId, teamName, teamLogo;

    //private TextView tv;

    private FirebaseAuth firebaseAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference favoritesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_teams_view);

        favTeamsGrid = findViewById(R.id.favTeamsGrid);
        ViewCompat.setNestedScrollingEnabled(favTeamsGrid,true);

        firebaseAuth = FirebaseAuth.getInstance();

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        favoritesRef = database.getReference("users/"+userId+"/favorites");

        favoritesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                TeamObject teamObject = dataSnapshot.getValue(TeamObject.class);
                System.out.println("TeamObject: "+ teamObject);

               /** favTeamsList = new ArrayList<>();
                for(DataSnapshot child : dataSnapshot.getChildren()){

//EL PROBLEMA ES QUE SE QUEDA EN ID POR ESO NO HACE EL MAPPING Y SOLO COGE UN OBJETO.
// HAY QUE HACER QUE LA DB REFERENCE ENTRE EN EL ID PARA CADA UNO PARA DE AHÍ COGER
// TODOS LOS VALORES, CREAR UN NUEVO TEAMOBJECT Y AÑADIRLO A LA LISTA PARA PASARLO AL ADAPTER

                    TeamObject teamObject = child.getValue(TeamObject.class);
                    System.out.println("TeamObject:"+ teamObject);
                    //favTeamsList.add(teamObject);
                    //teamId = teamObject.id;
                    //System.out.println("teamId: "+ teamId);
                    //teamName = teamObject.name;
                    //teamLogo = teamObject.logo;
               } */

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The read failed: " + error);
            }
        });
        //tv.setText(teamId + " " + teamName + " " + teamLogo);

        //CustomTeamAdapter jsonFavTeamsCustomAdapter = new CustomTeamAdapter(FavoriteTeamsView.this, favTeamsList);
        //favTeamsGrid.setAdapter(jsonFavTeamsCustomAdapter);
    }
}