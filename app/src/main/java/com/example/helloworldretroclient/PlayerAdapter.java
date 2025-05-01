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
        TextView name, position, age, goals, assists, nationality;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            position = view.findViewById(R.id.position);
            age = view.findViewById(R.id.age);
            goals = view.findViewById(R.id.goals);
            assists = view.findViewById(R.id.assists);
            nationality = view.findViewById(R.id.nationality);
        }
    }

    @Override
    public PlayerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.player_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlayerAdapter.ViewHolder holder, int position) {
        Player p = players.get(position);
        holder.name.setText(p.name);
        holder.position.setText(p.position);
        holder.age.setText(String.valueOf(p.age));
        holder.goals.setText(String.valueOf(p.goals));
        holder.assists.setText(String.valueOf(p.assists));
        holder.nationality.setText(p.nationality);
    }

    @Override
    public int getItemCount() {
        return players.size();
    }
}
