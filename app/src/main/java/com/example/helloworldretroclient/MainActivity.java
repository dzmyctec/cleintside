package com.example.helloworldretroclient;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText inputName, inputMinAge, inputPosition;
    private PlayerApiService apiService;
    private Button backButton;
    private TextView currentTeamName;
    private boolean isViewingPlayers = false;
    private List<Team> currentTeams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputName = findViewById(R.id.inputName);
        inputMinAge = findViewById(R.id.inputMinAge);
        inputPosition = findViewById(R.id.inputPosition);
        recyclerView = findViewById(R.id.recyclerView);
        backButton = findViewById(R.id.backButton);
        currentTeamName = findViewById(R.id.currentTeamName);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ca2ead.azurewebsites.net")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(PlayerApiService.class);

        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> performSearch());

        Button getTeamsButton = findViewById(R.id.getTeamsButton);
        getTeamsButton.setOnClickListener(v -> fetchTeams());
        
        backButton.setOnClickListener(v -> returnToTeams());
        
        // Initially hide the back button and team name
        backButton.setVisibility(View.GONE);
        currentTeamName.setVisibility(View.GONE);
    }
    
    private void returnToTeams() {
        // Return to the teams list
        if (currentTeams != null) {
            TeamAdapter adapter = new TeamAdapter(currentTeams);
            setupTeamAdapter(adapter);
            recyclerView.setAdapter(adapter);
            
            // Hide back button and team name
            backButton.setVisibility(View.GONE);
            currentTeamName.setVisibility(View.GONE);
            isViewingPlayers = false;
            
            // Show the search inputs and buttons
            inputName.setVisibility(View.VISIBLE);
            inputMinAge.setVisibility(View.VISIBLE);
            inputPosition.setVisibility(View.VISIBLE);
            findViewById(R.id.searchButton).setVisibility(View.VISIBLE);
            findViewById(R.id.getTeamsButton).setVisibility(View.VISIBLE);
        }
    }

    private void displayTeamPlayers(Team team) {
        if (team.players != null && !team.players.isEmpty()) {
            recyclerView.setAdapter(new PlayerAdapter(team.players));
            
            // Show back button and team name
            backButton.setVisibility(View.VISIBLE);
            currentTeamName.setVisibility(View.VISIBLE);
            currentTeamName.setText(team.name + " Players");
            isViewingPlayers = true;
            
            // Hide the search inputs and buttons
            inputName.setVisibility(View.GONE);
            inputMinAge.setVisibility(View.GONE);
            inputPosition.setVisibility(View.GONE);
            findViewById(R.id.searchButton).setVisibility(View.GONE);
            findViewById(R.id.getTeamsButton).setVisibility(View.GONE);
        } else {
            // Show a message that team has no players
            Toast.makeText(this, 
                "No players available for " + team.name, 
                Toast.LENGTH_SHORT).show();
        }
    }

    private void performSearch() {
        Map<String, String> params = new HashMap<>();

        if (!inputName.getText().toString().isEmpty()) {
            params.put("name", inputName.getText().toString());
        }
        if (!inputMinAge.getText().toString().isEmpty()) {
            params.put("minAge", inputMinAge.getText().toString());
        }
        if (!inputPosition.getText().toString().isEmpty()) {
            params.put("position", inputPosition.getText().toString());
        }

        apiService.searchPlayers(params).enqueue(new Callback<List<Player>>() {
            @Override
            public void onResponse(Call<List<Player>> call, Response<List<Player>> response) {
                if (response.isSuccessful()) {
                    recyclerView.setAdapter(new PlayerAdapter(response.body()));
                } else {
                    Log.e("API", "Search failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Player>> call, Throwable t) {
                Log.e("API", "Error: " + t.getMessage());
            }
        });
    }
    
    private void fetchTeams() {
        apiService.getTeams().enqueue(new Callback<List<Team>>() {
            @Override
            public void onResponse(Call<List<Team>> call, Response<List<Team>> response) {
                if (response.isSuccessful()) {
                    currentTeams = response.body();
                    TeamAdapter adapter = new TeamAdapter(currentTeams);
                    setupTeamAdapter(adapter);
                    recyclerView.setAdapter(adapter);
                } else {
                    Log.e("API", "Team fetch failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Team>> call, Throwable t) {
                Log.e("API", "Error fetching teams: " + t.getMessage());
            }
        });
    }
    
    private void setupTeamAdapter(TeamAdapter adapter) {
        adapter.setOnPlayerClickListener(team -> displayTeamPlayers(team));
    }
    
    @Override
    public void onBackPressed() {
        if (isViewingPlayers) {
            returnToTeams();
        } else {
            super.onBackPressed();
        }
    }
}

