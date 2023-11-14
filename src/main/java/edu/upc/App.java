package edu.upc;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.http.*;

public class App {
    public static final String API_URL = "http://localhost:8080/dsaApp/";//path defined in BASE_URI on API

    //class song where a song will be stored
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

    //Interface that will use get method to get the song list. Here use the path in swagger. In this case http://localhost:8080/dsaApp/tracks. Since we have the rest just add tracks
    public interface TracksInterface {
        @GET("tracks")
        Call<List<Song>> songs();

        @GET("tracks/{id}")
        Call<Song> song1(@Path("id") String id);

        @DELETE("tracks/{id}")
        Call<Void> responseDelete(@Path("id") String id);

        @POST("tracks")
        Call<Song> createSong(@Body Song song);
    }

    public static void main(String[] args) throws IOException {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        // Create a very simple REST adapter which points the API.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create an instance of our tracks API interface.
        TracksInterface tracksInterface = retrofit.create(TracksInterface.class);

        // Create a call instance for looking up Retrofit songs.
        Call<List<Song>> call = tracksInterface.songs();

        // Fetch and print a list of the contributors to the library.
        List<Song> songs = call.execute().body();
        for (Song song : songs) {
            System.out.println("Song ID: " + song.id + "\nTitle: " + song.title + "\nSinger: " + song.singer + "\n");
        }

        //We will try to get only one song.
       /* String songId = "Z2050407322"; // Replace with the actual song ID you want to retrieve
        Call<Song> call1 = tracksInterface.song1(songId);
        Song songObtained = call1.execute().body();
        System.out.println("Getting song for ID: XRraT9\nSong ID: " + songObtained.id + "\nTitle: " + songObtained.title + "\nSinger: " + songObtained.singer + "\n");
*/
        //We will now try deleting a song
        /*String deleteID = "Z2050407322";
        Call<Void> call2 = tracksInterface.responseDelete(deleteID);
        retrofit2.Response<Void> response = call2.execute();
        if (response.code() == 201) {
            System.out.println("The song with ID: " + deleteID + " has been deleted.\n");
        } else {
            System.out.println("Failed to delete the song. HTTP Status Code: " + response.code() + "\n");
        }*/

        //We will now try to implement create a track
        Song newSongToSend = new Song("PruebaID", "PruebaTitle", "PruebaSinger");
        Call<Song> call3 = tracksInterface.createSong(newSongToSend);

        try {
            retrofit2.Response<Song> responseCreate = call3.execute();

            if (responseCreate.isSuccessful()) {
                Song newSong = responseCreate.body();
                System.out.println("New Song Created:\nSong ID: " + newSong.id + "\nTitle: " + newSong.title + "\nSinger: " + newSong.singer + "\n");
            } else {
                System.out.println("Failed to create the song. HTTP Status Code: " + responseCreate.code() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}