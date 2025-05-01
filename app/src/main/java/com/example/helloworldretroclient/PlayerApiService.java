package com.example.helloworldretroclient;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

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
}