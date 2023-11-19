package edu.upc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.http.*;

public class App {
    public static final String API_URL = "http://localhost:8080/dsaApp/pixelRush/";

    public static class User {
        String username;
        String password;
        String mail;
        String name;
        String surname;
        String photo; //.png or .jpg <img src="photo.jpg"> (Front-end job)
        String state;
        int age;
        int pointsEarned; //We need an attribute points to buy!!!!!
        //Played Matches list
        List<Match> matchesPlayed;
        //list of owned objects;
        List<StoreObject> ownedObjects;

        public User(String username, String password, String mail, String name,
                    String surname, int age) {
            this.username = username;
            this.password = password;
            this.mail = mail;
            this.name = name;
            this.surname = surname;
            this.photo = null; //user will put a photo after the register
            this.state = null; //same as photo
            this.age = age;
            this.matchesPlayed = new ArrayList<>();//create empty lists of matches
            this.ownedObjects = new ArrayList<>();//create empty list of owned objects
            this.pointsEarned = 0;//User starts with 0 points earned
        }
    }

    public static class StoreObject {
        String objectID;
        String articleName;
        int price;
        String description;
        String articlePhoto; //same as attribute photo from User class

        //fully constructor
        public StoreObject(String objectID, String articleName, int price,
                           String description) {
            this.objectID = objectID;
            this.articleName = articleName;
            this.price = price;
            this.description = description;
            this.articlePhoto = null;
        }
    }

    public class Match {
        String username;
        int totalPoints;
        int currentLVL;
        int maxLVL;
        List<Integer> pointsObtainedPerLevel;

        //fully constructor
        public Match(String username) {
            this.username = username;
            this.totalPoints = 0;//user always starts with 0 points
            this.currentLVL = 1;//user always start at level 1
            //This value will change at the end of the project because it depends on the number od levels we make, we start with 3
            this.maxLVL = 3;
            this.pointsObtainedPerLevel = new ArrayList<>();
        }
    }


    public interface PixelRushInterface {
        @GET("getStoreSize")
        Call<JsonObject> getStoreSize();
        @GET("getNumberOfUsers")
        Call<JsonObject> getNumberOfUsers();
        @GET("{username}")
        Call<User> getUser(@Path("username") String username);
        @GET("getAllUsers")
        Call<List<User>> getAllUsers();
        @GET("getObjectListFromStore")
        Call<List<StoreObject>> getAllObjectsFromStore();
        @GET("getPlayedMatchesFromUser/{username}")
        Call<List<Match>> getPlayedMatchesFromUser(@Path("username")String username);
        @GET("getObjectInformation/{objectID}")
        Call<StoreObject> getObject(@Path("objectID")String objectID);
        @GET("getActiveMatch/{username}")
        Call<Match> getActiveMatch(@Path("username")String username);
        @POST("registerNewUser")
        Call<Void> registerUser(@Body User user);
        @POST("login")
        Call<Void> loginUser(@Body User user);
        @PUT("addItemToUser/{username}/{objectID}")
        Call<Void> addItemToUser(@Path("username")String username,@Path("objectID")String objectID);
        @PUT("createMatch/{username}")
        Call<Void> createMatch(@Path("username")String username);
        @GET("getLevelFromActiveMatch/{username}")
        Call<JsonObject> getLevelFromActiveMatch(@Path("username")String username);
        @GET("getMatchPointsFromActiveMatch/{username}")
        Call<JsonObject> getMatchPointsFromActiveMatch(@Path("username")String username);
        @PUT("nextLevel/{username}/{points}")
        Call<Void> nextLevel(@Path("username")String username,@Path("points")int points);
        @PUT("endMatch/{username}")
        Call<Void> endMatch(@Path("username")String username);
        @POST("addObjectToStore")
        Call<Void> addObjectToStore(@Body StoreObject Object);
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

        PixelRushInterface pixelRushInterface = retrofit.create(PixelRushInterface.class);

        // Method to get store size
        Call<JsonObject> callGetStoreSize = pixelRushInterface.getStoreSize();
        callGetStoreSize.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonResponse = response.body();
                    if (jsonResponse != null) {
                        int storeSize = jsonResponse.get("Number of Items on the store").getAsInt();
                        System.out.println("Store size: " + storeSize);
                    } else {
                        System.out.println("Response body is null");
                    }
                } else {
                    System.out.println("Error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }
        });
        // Method to get number of users
        Call<JsonObject> callGetNumberOfUsers = pixelRushInterface.getNumberOfUsers();
        callGetNumberOfUsers.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonResponse = response.body();
                    if (jsonResponse != null) {
                        int numOfUsers = jsonResponse.get("number of users").getAsInt();
                        System.out.println("Number Of Users: " + numOfUsers);
                    } else {
                        System.out.println("Response body is null");
                    }
                } else {
                    System.out.println("Error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }
        });
        //Method to obtain one user by username
        String getThisUsername = "robertoguarneros11";//Enter ID here
        Call<User> callGetUsername = pixelRushInterface.getUser(getThisUsername);
        callGetUsername.enqueue(new Callback<User>(){
            @Override
            public void onResponse(Call<User> call,Response<User> response){
                if(response.isSuccessful()){
                    User usernameFound = response.body();
                    System.out.println("User found:\nUsername: " + usernameFound.username + "\nName: " + usernameFound.name + "\nMail: " + usernameFound.mail + "\n");

                }else {
                    System.out.println("Error: " + response.code() + " " + response.message());
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }
        });
        //Method to get all users
        Call<List<User>> callGetAllUsers = pixelRushInterface.getAllUsers();
        callGetAllUsers.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    List<User> users = response.body();
                    for (User user : users) {
                        System.out.println("Name: " + user.username + "\nSurname: " + user.surname + "\nusername: " + user.age+ "\n");
                    }
                } else {
                    System.out.println("Error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }
        });
        //Method to get all objects available in the store
        Call<List<StoreObject>> callGetAllStoreObjects = pixelRushInterface.getAllObjectsFromStore();

        callGetAllStoreObjects.enqueue(new Callback<List<StoreObject>>() {
            @Override
            public void onResponse(Call<List<StoreObject>> call, Response<List<StoreObject>> response) {
                if (response.isSuccessful()) {
                    List<StoreObject> objects = response.body();
                    for (StoreObject object : objects) {
                        System.out.println("Article Name: " + object.articleName + "\nObject ID: " + object.objectID + "\nDescription: " + object.description + "\n");
                    }
                } else {
                    System.out.println("Error: " + response.code() + " " + response.message());
                }
            }
            @Override
            public void onFailure(Call<List<StoreObject>> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }
        });
        //Method to get all played matches from user
        String getMatchesForThisUser = "robertoguarneros11";//Enter ID here
        Call<List<Match>> callGetPlayedMatchesFromUser = pixelRushInterface.getPlayedMatchesFromUser(getMatchesForThisUser);
        callGetPlayedMatchesFromUser.enqueue(new Callback<List<Match>>() {
            @Override
            public void onResponse(Call<List<Match>> call, Response<List<Match>> response) {
                if (response.isSuccessful()) {
                    List<Match> matches = response.body();
                    for (Match match : matches) {
                        System.out.println("Match: \nUsername: " + match.username + "\nCurrent Level: " + match.currentLVL + "\nTotal Points: " + match.totalPoints + "\n");
                    }
                } else {
                    System.out.println("Error: " + response.code() + " " + response.message());
                }
            }
            @Override
            public void onFailure(Call<List<Match>> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }
        });
        //Method to obtain object by ID
        String getThisObject = "123";//Enter ID here
        Call<StoreObject> callGetObject = pixelRushInterface.getObject(getThisObject);
        callGetObject.enqueue(new Callback<StoreObject>(){
            @Override
            public void onResponse(Call<StoreObject> call,Response<StoreObject> response){
                if(response.isSuccessful()){
                    StoreObject objectFound = response.body();
                    System.out.println("Object found:\nArticle Name: " + objectFound.articleName + "\nObject ID: " + objectFound.objectID + "\nDescription: " + objectFound.description + "\n");

                }else {
                    System.out.println("Error: " + response.code() + " " + response.message());
                }
            }
            @Override
            public void onFailure(Call<StoreObject> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }
        });
        //Method to obtain current match of username
        String getMatchForThisUser = "titi";//Enter ID here
        Call<Match> callGetActiveMatch = pixelRushInterface.getActiveMatch(getMatchForThisUser);
        callGetActiveMatch.enqueue(new Callback<Match>(){
            @Override
            public void onResponse(Call<Match> call,Response<Match> response){
                if(response.isSuccessful()){
                    Match match = response.body();
                    System.out.println("Current Match: \nUsername: " + match.username + "\nCurrent Level: " + match.currentLVL + "\nTotal Points: " + match.totalPoints + "\n");

                }else {
                    System.out.println("Error: " + response.code() + " " + response.message());
                }
            }
            @Override
            public void onFailure(Call<Match> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }
        });
        //Register
        User registerUser = new User("PruebaUsername","password","mail","name","surname",40);
        Call<Void> callRegister = pixelRushInterface.registerUser(registerUser);
        callRegister.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    System.out.println("User registered successfully");
                } else {
                    System.out.println("Error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println("Error: "+t.getMessage());
            }
        });
        //Login
        User login = new User("robertoguarneros11","123","Roberto","Guarneros","roberto@gmail.com",22);
        Call<Void> callLogin = pixelRushInterface.loginUser(login);
        callLogin.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    System.out.println("User login successful");
                } else {
                    System.out.println("Error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println("Error: "+t.getMessage());
            }
        });
        //Add item to this user
        String addToThisUser="robertoguarneros11";
        String addThisObjectID="123";
        Call<Void> callAddItemToUser = pixelRushInterface.addItemToUser(addToThisUser,addThisObjectID);
        callAddItemToUser.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    System.out.println("Item added successfully");
                } else {
                    System.out.println("Error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println("Error: "+t.getMessage());
            }
        });
        //Create Match
        String createMatchFor="robertoguarneros11";
        Call<Void> callCreateMatch = pixelRushInterface.createMatch(createMatchFor);
        callCreateMatch.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    System.out.println("Match Created successfully");
                } else {
                    System.out.println("Error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println("Error: "+t.getMessage());
            }
        });
        // Method to get level from active match
        String getLevelFor="titi";
        Call<JsonObject> callGetLevelFromActiveMatch = pixelRushInterface.getLevelFromActiveMatch(getLevelFor);
        callGetLevelFromActiveMatch.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonResponse = response.body();
                    if (jsonResponse != null) {
                        int level = jsonResponse.get("level").getAsInt();
                        System.out.println("Current level for user: " +getLevelFor+" is " +level);
                    } else {
                        System.out.println("Response body is null");
                    }
                } else {
                    System.out.println("Error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }
        });
        // Method to get points from active match
        String getPointsFor="titi";
        Call<JsonObject> callGetPointsFromActiveMatch = pixelRushInterface.getMatchPointsFromActiveMatch(getPointsFor);
        callGetPointsFromActiveMatch.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonResponse = response.body();
                    if (jsonResponse != null) {
                        int points = jsonResponse.get("matchPoints").getAsInt();
                        System.out.println("Current points for user: " +getPointsFor+" is " +points);
                    } else {
                        System.out.println("Response body is null");
                    }
                } else {
                    System.out.println("Error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }
        });
        // Method to change level for active match
        String changeLevelFor="titi";
        int points=100;
        Call<Void> callChangeLevel = pixelRushInterface.nextLevel(changeLevelFor, points);
        callChangeLevel.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    System.out.println("\t\n" +
                            "Level changed successfully");
                } else {
                    System.out.println("Error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println("Error: "+t.getMessage());
            }
        });
        // Method to change end active match
        String endMatch="titi";
        Call<Void> callEndMatch = pixelRushInterface.endMatch(endMatch);
        callEndMatch.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    System.out.println("\t\n"+ "Match ended successfully");
                } else {
                    System.out.println("Error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println("Error: "+t.getMessage());
            }
        });
        //Method add object to store
        StoreObject newObject = new StoreObject("333","pruebaNombre",50,"description here");
        Call<Void> callAddObjectToStore = pixelRushInterface.addObjectToStore(newObject);
        callAddObjectToStore.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    System.out.println("New object added successfully");
                } else {
                    System.out.println("Error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println("Error: "+t.getMessage());
            }
        });
    }
}
