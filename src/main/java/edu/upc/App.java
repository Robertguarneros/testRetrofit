package edu.upc;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.http.*;

public class App {
    public static final String API_URL = "http://localhost:8080/dsaApp/";

    public static class Song {
        public final String id;
        public final String title;
        public final String singer;

        public Song(String id, String title, String singer) {
            this.id = id;
            this.title = title;
            this.singer = singer;
        }
    }

    public interface TracksInterface {
        @GET("tracks")
        Call<List<Song>> songs();
    }

    public static void main(String[] args) throws IOException {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TracksInterface tracksInterface = retrofit.create(TracksInterface.class);

        Call<List<Song>> call = tracksInterface.songs();

        call.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                if (response.isSuccessful()) {
                    List<Song> songs = response.body();
                    for (Song song : songs) {
                        System.out.println("Song ID: " + song.id + "\nTitle: " + song.title + "\nSinger: " + song.singer + "\n");
                    }
                } else {
                    System.out.println("Error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }
        });
    }
}
