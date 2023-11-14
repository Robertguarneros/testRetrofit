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
        @GET("tracks/{id}")
        Call<Song> songById(@Path("id") String id);
        @DELETE("tracks/{id}")
        Call<Void> deleteSong(@Path("id") String id);
        @POST("tracks")
        Call<Song> createSong(@Body Song song);
        @PUT("tracks")
        Call<Void> updateSong(@Body Song song);
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

        //Method to get all tracks
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

        //Method to obtain one song by ID.
        /*String getThisSong = "h2953926787240";//Enter ID here
        Call<Song> call1 = tracksInterface.songById(getThisSong);
        call1.enqueue(new Callback<Song>(){
            @Override
            public void onResponse(Call<Song> call1,Response<Song> response){
                if(response.isSuccessful()){
                    Song songFound = response.body();
                    System.out.println("Se ha encontrado la siguiente cancion:\nSong ID: " + songFound.id + "\nTitle: " + songFound.title + "\nSinger: " + songFound.singer + "\n");

                }else {
                    System.out.println("Error: " + response.code() + " " + response.message());
                }
            }
            @Override
            public void onFailure(Call<Song> call1, Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }
        });*/

        //Method to delete a song
        /*String deleteThisSong = "h2953926787240";
        Call<Void> call2 = tracksInterface.deleteSong(deleteThisSong);
        call2.enqueue(new Callback<Void>(){
            @Override
            public void onResponse(Call<Void>call2, Response<Void>response){
                if(response.isSuccessful()){
                    System.out.println("The song with ID: " + deleteThisSong + " has been deleted.\n");
                } else{
                    System.out.println("Error: " + response.code() + " " + response.message());
                }
            }
            @Override
            public void onFailure(Call<Void>call2,Throwable t){
                System.out.println("Error: "+t.getMessage());
            }
        });*/

        //We will now try to implement create a track
        Song createSong = new Song("PruebaID", "PruebaTitle", "PruebaSinger");
        Call<Song> call3 = tracksInterface.createSong(createSong);
        call3.enqueue(new Callback<Song>() {
            @Override
            public void onResponse(Call<Song> call3, Response<Song> response) {
                if (response.isSuccessful()) {
                    Song newSong = response.body();
                    System.out.println("New Song Created:\nSong ID: " + newSong.id + "\nTitle: " + newSong.title + "\nSinger: " + newSong.singer + "\n");
                } else {
                    System.out.println("Error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Song> call3, Throwable t) {
                System.out.println("Error: "+t.getMessage());
            }
        });

        //Method to update a song by ID.
        Song updateSong = new Song("PruebaID","cambioTitle","cambioSinger");
        Call<Void> call4 = tracksInterface.updateSong(updateSong);
        call4.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call4, Response<Void> response) {
                if (response.isSuccessful()) {
                    System.out.println("The song with ID: " + updateSong.id + " has been updated.\n");
                } else {
                    System.out.println("Error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call4, Throwable t) {
                System.out.println("Error: "+t.getMessage());
            }
        });
    }
}
