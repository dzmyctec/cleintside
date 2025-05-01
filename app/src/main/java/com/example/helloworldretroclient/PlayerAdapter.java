package com.example.helloworldretroclient;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ViewHolder> {
    private final List<Player> players;

    public PlayerAdapter(List<Player> players) {
        this.players = players;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        public ViewHolder(View view) {
            super(view);
            text = view.findViewById(android.R.id.text1);
        }
    }

    @Override
    public PlayerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlayerAdapter.ViewHolder holder, int position) {
        Player p = players.get(position);
        String playerDetails = String.format(
                "Name: %s\nPosition: %s\nAge: %d\nGoals: %d\nAssists: %d\nAppearances: %d\nNationality: %s\nTeam ID: %d",
                p.name,
                p.position,
                p.age,
                p.goals,
                p.assists,
                p.appearances,
                p.nationality,
                p.teamId
        );
        holder.text.setText(playerDetails);
    }

    @Override
    public int getItemCount() {
        return players.size();
    }
}
