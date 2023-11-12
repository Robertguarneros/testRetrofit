package edu.upc;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class App 
{
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
    }

    public static void main( String[] args ) throws IOException
    {
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
            System.out.println("Song ID: "+song.id+"\nTitle: "+ song.title+"\nSinger: " + song.singer+"\n");
        }
    }
}
