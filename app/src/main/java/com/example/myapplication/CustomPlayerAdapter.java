package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class CustomPlayerAdapter extends BaseAdapter {

    private LayoutInflater lInflater;
    private List<PlayerObject> listStorage;
    private Context context;

    private Button teamStatsBtn;

    public CustomPlayerAdapter(Context context,
                               List<PlayerObject> customizedListView) {
        lInflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        listStorage = customizedListView;
        this.context = context;
    }

    @Override
    public int getCount() {return listStorage.size();}

    @Override
    public Object getItem(int i) {return i;}

    @Override
    public long getItemId(int i) {return i;}

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        TextView playerName;
        RelativeLayout item;

        if(view == null){
            view = lInflater.inflate(R.layout.players_list, viewGroup,false);
        }

        item = view.findViewById(R.id.playerButton);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlayerStatsView.class);
                String idPName = listStorage.get(i).id;
                String firstname = listStorage.get(i).firstname;
                String lastname = listStorage.get(i).lastname;
                String teamName = listStorage.get(i).teamName;
                intent.putExtra("playerId", idPName);
                intent.putExtra("firstname", firstname);
                intent.putExtra("lastname", lastname);
                intent.putExtra("teamName", teamName);
                context.startActivity(intent);
            }
        });

        playerName = view.findViewById(R.id.playerName);
        playerName.setText(listStorage.get(i).firstname + " " + listStorage.get(i).lastname);

        return view;
    }
}
