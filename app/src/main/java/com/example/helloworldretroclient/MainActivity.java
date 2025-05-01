package com.example.helloworldretroclient;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Interceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements TeamAdapter.OnPlayerClickListener {

    // UI Elements
    private RecyclerView recyclerView;
    private EditText inputName, inputMinAge, inputPosition;
    private Button backButton, createTeamButton;
    private TextView currentTeamName;
    private View mainLayout, createTeamLayout, createPlayerLayout;
    private FrameLayout formContainer;
    
    // API
    private PlayerApiService apiService;
    
    // State variables
    private boolean isViewingPlayers = false;
    private boolean isCreatingTeam = false;
    private boolean isCreatingPlayer = false;
    private List<Team> currentTeams;
    private Team selectedTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupViews();
        setupRetrofit();
        setupListeners();
    }
    
    private void setupViews() {
        // Find views in main layout
        mainLayout = findViewById(R.id.mainLayout);
        formContainer = findViewById(R.id.formContainer);
        inputName = findViewById(R.id.inputName);
        inputMinAge = findViewById(R.id.inputMinAge);
        inputPosition = findViewById(R.id.inputPosition);
        recyclerView = findViewById(R.id.recyclerView);
        backButton = findViewById(R.id.backButton);
        currentTeamName = findViewById(R.id.currentTeamName);
        createTeamButton = findViewById(R.id.createTeamButton);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Initially hide back button and team name
        backButton.setVisibility(View.GONE);
        currentTeamName.setVisibility(View.GONE);
        
        // Inflate layouts but don't attach to parent yet
        createTeamLayout = getLayoutInflater().inflate(R.layout.create_team_form, null, false);
        createPlayerLayout = getLayoutInflater().inflate(R.layout.create_player_form, null, false);
    }
    
    private void setupRetrofit() {
        // Create OkHttpClient with authentication interceptor
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    
                    // Add the API key to the request headers
                    // Using the primary key from your subscription
                    Request request = original.newBuilder()
                            .header("Ocp-Apim-Subscription-Key", "251856aed0a24c91bf1656361e464b69")
                            .header("Authorization", "Bearer 251856aed0a24c91bf1656361e464b69")
                            .header("Content-Type", "application/json")
                            .header("Accept", "application/json")
                            .method(original.method(), original.body())
                            .build();
                    
                    // Log request for debugging
                    Log.d("API_REQUEST", "URL: " + request.url());
                    Log.d("API_REQUEST", "Headers: " + request.headers());
                            
                    // Execute the request
                    okhttp3.Response response = chain.proceed(request);
                    
                    // Log the response for debugging
                    Log.d("API_RESPONSE", "Code: " + response.code());
                    Log.d("API_RESPONSE", "Message: " + response.message());
                    
                    return response;
                })
                .build();

        // Create Retrofit instance with the custom OkHttpClient
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ca2ead.azurewebsites.net")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        apiService = retrofit.create(PlayerApiService.class);
    }
    
    private void setupListeners() {
        // Main screen buttons
        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> performSearch());

        Button getTeamsButton = findViewById(R.id.getTeamsButton);
        getTeamsButton.setOnClickListener(v -> fetchTeams());
        
        createTeamButton.setOnClickListener(v -> showCreateTeamForm());
        
        backButton.setOnClickListener(v -> returnToTeams());
        
        // Create team form buttons
        Button saveTeamButton = createTeamLayout.findViewById(R.id.saveTeamButton);
        saveTeamButton.setOnClickListener(v -> saveNewTeam());
        
        Button cancelTeamButton = createTeamLayout.findViewById(R.id.cancelTeamButton);
        cancelTeamButton.setOnClickListener(v -> cancelTeamCreation());
        
        // Create player form buttons
        Button savePlayerButton = createPlayerLayout.findViewById(R.id.savePlayerButton);
        savePlayerButton.setOnClickListener(v -> saveNewPlayer());
        
        Button cancelPlayerButton = createPlayerLayout.findViewById(R.id.cancelPlayerButton);
        cancelPlayerButton.setOnClickListener(v -> cancelPlayerCreation());
    }
    
    private void showCreateTeamForm() {
        // Set flag
        isCreatingTeam = true;
        
        // Add create team form to the form container
        formContainer.removeAllViews(); // Clear any existing views
        if (createTeamLayout.getParent() != null) {
            ((ViewGroup) createTeamLayout.getParent()).removeView(createTeamLayout);
        }
        formContainer.addView(createTeamLayout);
        
        // Show the form container (which overlays the main content)
        formContainer.setVisibility(View.VISIBLE);
    }
    
    private void showCreatePlayerForm(Team team) {
        // Set state
        isCreatingPlayer = true;
        selectedTeam = team;
        
        // Update team name in the form
        TextView playerTeamName = createPlayerLayout.findViewById(R.id.playerTeamName);
        playerTeamName.setText(getString(R.string.player_team_name, team.name));
        
        // Add create player form to the form container
        formContainer.removeAllViews(); // Clear any existing views
        if (createPlayerLayout.getParent() != null) {
            ((ViewGroup) createPlayerLayout.getParent()).removeView(createPlayerLayout);
        }
        formContainer.addView(createPlayerLayout);
        
        // Show the form container (which overlays the main content)
        formContainer.setVisibility(View.VISIBLE);
    }
    
    private void saveNewTeam() {
        // Get data from form
        EditText teamNameInput = createTeamLayout.findViewById(R.id.teamNameInput);
        EditText teamLeagueInput = createTeamLayout.findViewById(R.id.teamLeagueInput);
        EditText teamCountryInput = createTeamLayout.findViewById(R.id.teamCountryInput);
        EditText teamFoundedYearInput = createTeamLayout.findViewById(R.id.teamFoundedYearInput);
        EditText teamStadiumInput = createTeamLayout.findViewById(R.id.teamStadiumInput);
        EditText teamManagerInput = createTeamLayout.findViewById(R.id.teamManagerInput);
        
        // Validate inputs
        String name = teamNameInput.getText().toString().trim();
        String league = teamLeagueInput.getText().toString().trim();
        String country = teamCountryInput.getText().toString().trim();
        String stadiumName = teamStadiumInput.getText().toString().trim();
        String manager = teamManagerInput.getText().toString().trim();
        
        if (name.isEmpty() || name.length() < 2) {
            Toast.makeText(this, R.string.team_name_validation, Toast.LENGTH_SHORT).show();
            return;
        }
        
        int foundedYear;
        try {
            foundedYear = Integer.parseInt(teamFoundedYearInput.getText().toString().trim());
            if (foundedYear < 1800 || foundedYear > 2024) {
                Toast.makeText(this, R.string.team_year_validation, Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.valid_year_validation, Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Create team object
        Team newTeam = new Team();
        newTeam.name = name;
        newTeam.league = league;
        newTeam.country = country;
        newTeam.foundedYear = foundedYear;
        newTeam.stadium = stadiumName;
        newTeam.manager = manager;
        newTeam.players = new ArrayList<>();
        
        // Call API to create team
        apiService.createTeam(newTeam).enqueue(new Callback<Team>() {
            @Override
            public void onResponse(Call<Team> call, Response<Team> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, R.string.team_created, Toast.LENGTH_SHORT).show();
                    
                    // Hide form and show main layout
                    cancelTeamCreation();
                    
                    // Refresh team list
                    fetchTeams();
                } else {
                    Toast.makeText(MainActivity.this, 
                        getString(R.string.team_creation_failed, response.message()), 
                        Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Team> call, Throwable t) {
                Toast.makeText(MainActivity.this, 
                    getString(R.string.error_message, t.getMessage()), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void cancelTeamCreation() {
        // Hide the form container
        formContainer.setVisibility(View.GONE);
        formContainer.removeAllViews();
        
        // Reset state
        isCreatingTeam = false;
    }
    
    private void saveNewPlayer() {
        if (selectedTeam == null) {
            Toast.makeText(this, R.string.no_team_selected, Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Get data from form
        EditText playerNameInput = createPlayerLayout.findViewById(R.id.playerNameInput);
        EditText playerAgeInput = createPlayerLayout.findViewById(R.id.playerAgeInput);
        EditText playerPositionInput = createPlayerLayout.findViewById(R.id.playerPositionInput);
        EditText playerGoalsInput = createPlayerLayout.findViewById(R.id.playerGoalsInput);
        EditText playerAssistsInput = createPlayerLayout.findViewById(R.id.playerAssistsInput);
        EditText playerAppearancesInput = createPlayerLayout.findViewById(R.id.playerAppearancesInput);
        EditText playerNationalityInput = createPlayerLayout.findViewById(R.id.playerNationalityInput);
        
        // Validate inputs
        String name = playerNameInput.getText().toString().trim();
        String position = playerPositionInput.getText().toString().trim();
        String nationality = playerNationalityInput.getText().toString().trim();
        
        if (name.isEmpty() || name.length() < 2) {
            Toast.makeText(this, R.string.player_name_validation, Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Parse numeric inputs
        int age, goals, assists, appearances;
        try {
            age = Integer.parseInt(playerAgeInput.getText().toString().trim());
            if (age < 16 || age > 50) {
                Toast.makeText(this, R.string.player_age_validation, Toast.LENGTH_SHORT).show();
                return;
            }
            
            goals = playerGoalsInput.getText().toString().isEmpty() ? 0 : 
                Integer.parseInt(playerGoalsInput.getText().toString().trim());
            assists = playerAssistsInput.getText().toString().isEmpty() ? 0 : 
                Integer.parseInt(playerAssistsInput.getText().toString().trim());
            appearances = playerAppearancesInput.getText().toString().isEmpty() ? 0 : 
                Integer.parseInt(playerAppearancesInput.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.player_number_validation, Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Create player object
        Player newPlayer = new Player();
        newPlayer.name = name;
        newPlayer.age = age;
        newPlayer.position = position;
        newPlayer.goals = goals;
        newPlayer.assists = assists;
        newPlayer.appearances = appearances;
        newPlayer.nationality = nationality;
        newPlayer.teamId = selectedTeam.teamId;
        
        // Call API to create player
        apiService.createPlayer(newPlayer).enqueue(new Callback<Player>() {
            @Override
            public void onResponse(Call<Player> call, Response<Player> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, R.string.player_added, Toast.LENGTH_SHORT).show();
                    
                    // Hide form and show player list
                    cancelPlayerCreation();
                    
                    // Refresh to show updated player list
                    fetchTeams();
                    
                    // Navigate back to the team players view
                    for (Team team : currentTeams) {
                        if (team.teamId == selectedTeam.teamId) {
                            displayTeamPlayers(team);
                            break;
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, 
                        getString(R.string.player_add_failed, response.message()), 
                        Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Player> call, Throwable t) {
                Toast.makeText(MainActivity.this, 
                    getString(R.string.error_message, t.getMessage()), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void cancelPlayerCreation() {
        // Hide the form container
        formContainer.setVisibility(View.GONE);
        formContainer.removeAllViews();
        
        // Reset state
        isCreatingPlayer = false;
        
        // Navigate back to the team view if needed
        if (isViewingPlayers && selectedTeam != null) {
            displayTeamPlayers(selectedTeam);
        }
    }
    
    private void setMainContentVisibility(int visibility) {
        // This method is now only used for toggling visibility of elements within the main content
        // Forms are handled using the form container overlay
        findViewById(R.id.inputName).setVisibility(visibility);
        findViewById(R.id.inputMinAge).setVisibility(visibility);
        findViewById(R.id.inputPosition).setVisibility(visibility);
        findViewById(R.id.searchButton).setVisibility(visibility);
        findViewById(R.id.getTeamsButton).setVisibility(visibility);
        findViewById(R.id.createTeamButton).setVisibility(visibility);
        
        if (isViewingPlayers) {
            backButton.setVisibility(visibility);
            currentTeamName.setVisibility(visibility);
        }
        
        recyclerView.setVisibility(visibility);
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
            
            // Hide search results header
            findViewById(R.id.searchResultsHeader).setVisibility(View.GONE);
            
            // Show the search inputs and buttons
            inputName.setVisibility(View.VISIBLE);
            inputMinAge.setVisibility(View.VISIBLE);
            inputPosition.setVisibility(View.VISIBLE);
            findViewById(R.id.searchButton).setVisibility(View.VISIBLE);
            findViewById(R.id.getTeamsButton).setVisibility(View.VISIBLE);
            findViewById(R.id.createTeamButton).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onViewPlayersClicked(Team team) {
        displayTeamPlayers(team);
    }
    
    @Override
    public void onAddPlayerClicked(Team team) {
        selectedTeam = team;
        showCreatePlayerForm(team);
    }

    private void displayTeamPlayers(Team team) {
        selectedTeam = team;
        
        // Hide search results header
        findViewById(R.id.searchResultsHeader).setVisibility(View.GONE);
        
        if (team.players != null && !team.players.isEmpty()) {
            recyclerView.setAdapter(new PlayerAdapter(team.players));
            
            // Show back button and team name
            backButton.setVisibility(View.VISIBLE);
            currentTeamName.setVisibility(View.VISIBLE);
            currentTeamName.setText(getString(R.string.team_players, team.name));
            isViewingPlayers = true;
            
            // Hide the search inputs and buttons
            inputName.setVisibility(View.GONE);
            inputMinAge.setVisibility(View.GONE);
            inputPosition.setVisibility(View.GONE);
            findViewById(R.id.searchButton).setVisibility(View.GONE);
            findViewById(R.id.getTeamsButton).setVisibility(View.GONE);
            findViewById(R.id.createTeamButton).setVisibility(View.GONE);
        } else {
            // Show a message that team has no players
            Toast.makeText(this, 
                getString(R.string.no_players, team.name), 
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

        // Show the search results header
        TextView searchResultsHeader = findViewById(R.id.searchResultsHeader);
        searchResultsHeader.setVisibility(View.VISIBLE);

        apiService.searchPlayers(params).enqueue(new Callback<List<Player>>() {
            @Override
            public void onResponse(Call<List<Player>> call, Response<List<Player>> response) {
                if (response.isSuccessful()) {
                    recyclerView.setAdapter(new PlayerAdapter(response.body()));
                } else {
                    Log.e("API", "Search failed: " + response.message());
                    Toast.makeText(MainActivity.this, 
                        getString(R.string.search_failed, response.message()), 
                        Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Player>> call, Throwable t) {
                Log.e("API", "Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, 
                    getString(R.string.error_message, t.getMessage()), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void fetchTeams() {
        // Hide search results header
        findViewById(R.id.searchResultsHeader).setVisibility(View.GONE);
        
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
                    Toast.makeText(MainActivity.this, 
                        getString(R.string.team_fetch_failed, response.message()), 
                        Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Team>> call, Throwable t) {
                Log.e("API", "Error fetching teams: " + t.getMessage());
                Toast.makeText(MainActivity.this, 
                    getString(R.string.error_message, t.getMessage()), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setupTeamAdapter(TeamAdapter adapter) {
        adapter.setOnPlayerClickListener(this);
    }
    
    @Override
    public void onBackPressed() {
        if (isCreatingTeam) {
            cancelTeamCreation();
        } else if (isCreatingPlayer) {
            cancelPlayerCreation();
        } else if (isViewingPlayers) {
            returnToTeams();
        } else {
            super.onBackPressed();
        }
    }
}

