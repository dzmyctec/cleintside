package com.example.helloworldretroclient;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.ViewHolder> {
    private final List<Team> teams;
    private OnPlayerClickListener playerClickListener;

    public interface OnPlayerClickListener {
        void onViewPlayersClicked(Team team);
        void onAddPlayerClicked(Team team);
    }

    public void setOnPlayerClickListener(OnPlayerClickListener listener) {
        this.playerClickListener = listener;
    }

    public TeamAdapter(List<Team> teams) {
        this.teams = teams;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, league, country, stadium, manager, foundedYear;
        Button viewPlayersButton, addPlayerButton;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.teamName);
            league = view.findViewById(R.id.teamLeague);
            country = view.findViewById(R.id.teamCountry);
            stadium = view.findViewById(R.id.teamStadium);
            manager = view.findViewById(R.id.teamManager);
            foundedYear = view.findViewById(R.id.teamFoundedYear);
            viewPlayersButton = view.findViewById(R.id.viewPlayersButton);
            addPlayerButton = view.findViewById(R.id.addPlayerButton);
        }
    }

    @Override
    public TeamAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.team_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TeamAdapter.ViewHolder holder, int position) {
        Team team = teams.get(position);
        holder.name.setText(team.name);
        holder.league.setText(team.league);
        holder.country.setText(team.country);
        holder.stadium.setText(team.stadium);
        holder.manager.setText(team.manager);
        holder.foundedYear.setText(String.valueOf(team.foundedYear));
        
        // Set button click listeners
        holder.viewPlayersButton.setOnClickListener(v -> {
            if (playerClickListener != null) {
                playerClickListener.onViewPlayersClicked(team);
            }
        });
        
        holder.addPlayerButton.setOnClickListener(v -> {
            if (playerClickListener != null) {
                playerClickListener.onAddPlayerClicked(team);
            }
        });
    }

    @Override
    public int getItemCount() {
        return teams.size();
    }
} 