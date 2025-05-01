package com.example.helloworldretroclient;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

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
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public class MainActivity extends AppCompatActivity {

    interface PlayerApiService {
        @GET("/api/Players/search")
        Call<List<Player>> searchPlayers(@QueryMap Map<String, String> options);
    }

    private RecyclerView recyclerView;
    private EditText inputName, inputMinAge, inputPosition;
    private PlayerApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputName = findViewById(R.id.inputName);
        inputMinAge = findViewById(R.id.inputMinAge);
        inputPosition = findViewById(R.id.inputPosition);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ca2ead.azurewebsites.net")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(PlayerApiService.class);

        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> performSearch());
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
}
