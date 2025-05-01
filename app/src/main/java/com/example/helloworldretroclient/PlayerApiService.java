package com.example.helloworldretroclient;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface PlayerApiService {

    // Endpoint for fetching all players
    @GET("/api/Players")
    Call<List<Player>> getPlayers();  // This method is used to get all players.

    @GET("/api/Players/search")
    Call<List<Player>> searchPlayers(
            @Query("name") String name,
            @Query("minAge") Integer minAge,
            @Query("maxAge") Integer maxAge,
            @Query("position") String position,
            @Query("nationality") String nationality,
            @Query("minGoals") Integer minGoals,
            @Query("minAssists") Integer minAssists
    );
    
    // Overloaded method that accepts a map of query parameters
    @GET("/api/Players/search")
    Call<List<Player>> searchPlayers(@QueryMap Map<String, String> options);
    
    // Endpoint for fetching all teams
    @GET("/api/Teams")
    Call<List<Team>> getTeams();
    
    // Endpoint for creating a new team
    @POST("/api/Teams")
    Call<Team> createTeam(@Body Team team);
    
    // Endpoint for creating a new player
    @POST("/api/Players")
    Call<Player> createPlayer(@Body Player player);
}