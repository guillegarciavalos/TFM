package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomFavTeamAdapter extends BaseAdapter {

    private LayoutInflater lInflater;
    private List<TeamObject> listStorage;
    private Context context;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    public CustomFavTeamAdapter(Context context,
                             List<TeamObject> customizedListView) {
        lInflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        listStorage = customizedListView;
        this.context = context;
    }

    @Override
    public int getCount() {
        return listStorage.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        TextView team;
        RelativeLayout item;

        if(view == null){
            view = lInflater.inflate(R.layout.team_list, viewGroup,false);
        }

        team = view.findViewById(R.id.name);
        team.setText(listStorage.get(i).name);

        new DownloadImageFromInternet((ImageView) view.findViewById(R.id.teamlogo)).execute(listStorage.get(i).logo);

        item = view.findViewById(R.id.teamButton);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PlayersView.class);
                String teamId = listStorage.get(i).id;
                String teamName = listStorage.get(i).name;
                String teamLogo = listStorage.get(i).logo;
                intent.putExtra("teamId", teamId);
                intent.putExtra("teamName", teamName);
                intent.putExtra("teamLogo", teamLogo);
                context.startActivity(intent);
            }
        });
        return view;
    }
}
