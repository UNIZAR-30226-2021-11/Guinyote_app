package manyosoft.guinyote.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.JsonToken;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import manyosoft.guinyote.R;


public class GuinyoteClienteJWT implements Serializable {
    static final String LOCALHOST = "http://10.0.2.2:9000/";
    static final String REMOTEHOST = "http://15.188.14.213:11050/";
    static final String HOST = REMOTEHOST;


    // Usuarios
    static final String CREATE_USER = "api/v1/users/";
    static final String LOGIN = "api/v1/users/login/";
    static final String DELETE_USER = "api/v1/users/";
    static final String GET_USER = "api/v1/users/";
    static final String UPDATE_USER = "api/v1/users/";

    // Partidas
    static final String GET_PUBLICAS = "api/v1/games/";
    static final String GET_USER_GAMES = "api/v1/games/user/";
    static final String CREATE_GAME = "api/v1/games/";
    static final String GET_GAME = "api/v1/games/";
    static final String GET_TORNEOS = "api/v1/games/tournament";

    // Players
    static final String JOIN_PAREJA = "api/v1/players/";

    private static GuinyoteClienteJWT instance = null;

    String token;

    protected GuinyoteClienteJWT() {
        this.token = null;
    }

    public static synchronized GuinyoteClienteJWT getInstance() {
        if (instance == null) {
            instance = new GuinyoteClienteJWT();
        }
        return instance;
    }

    public String getToken() {
        return "Bearer: " + token;
    }


    public String getTokenTesting() {
        return "Bearer: " + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTB9.OdMJ_aDX6KvAoOePuOSgkK1YhZgny9OMcTWFWYq8EzU";
    }

    public boolean loginUsuario(Context context, String username, String password) throws ExecutionException, InterruptedException {

        boolean error = true;
        final String usuarioJSON = "username";
        final String passwordJSON = "password";
        final String tokenJSON = "token";

        // Objeto JSON a pasar al backend
        JsonObject json = new JsonObject();
        json.addProperty(usuarioJSON, username);
        json.addProperty(passwordJSON, password);

        Ion.getDefault(context).getConscryptMiddleware().enable(false);
        // Envío + Callback para la respuesta
        JsonObject result = Ion.with(context)
                .load("POST", HOST + LOGIN)
                .setJsonObjectBody(json)
                .asJsonObject()
                .get();
        if (result.get(tokenJSON) != null) {//comprobar que devuelve algo, si no devuelve es que no ha encontrado el usuario en la base
            token = result.get(tokenJSON).getAsString();
            error = false;
        }
        return error;

    }

    public boolean crearUsuario(Context context,String usuario, String mail, String password) throws ExecutionException, InterruptedException {
        // Constantes para trabajar con JSON
        final String usuarioObjetoJSON = "user";
        final String usuarioJSON = "username";
        final String mailJSON = "email";
        final String locationJSON = "location";
        final String passwordJSON = "password";

        // Objeto JSON a pasar al backend
        JsonObject json = new JsonObject();
        json.addProperty(usuarioJSON, usuario);
        json.addProperty(mailJSON, mail);
        //json.addProperty(locationJSON, location);
        json.addProperty(passwordJSON, password);

        Ion.getDefault(context).getConscryptMiddleware().enable(false);
        // Envío + Callback para la respuesta
        JsonObject result = Ion.with(context)
                .load("POST", HOST + CREATE_USER)
                .setJsonObjectBody(json)
                .asJsonObject()
                .get();
        if (result.get(usuarioObjetoJSON) != null) {
            return false;
        }
        return true;
    }

    public Usuario getUsuario(Context context, String username) throws ExecutionException, InterruptedException {
        final String idUsuario = "id";
        final String victorias = "games_won";
        final String derrotas = "games_lost";
        final String usernameUsuario = "username";
        final String emailUsuario = "email";
        final String locationUsuario = "location";
        final String createdUsuario = "created_at";
        final String updatedUsuario = "updated_at";
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int colorCarta = myPreferences.getInt(username+"_colorCarta", R.drawable.reverso);
        int colorTapete = myPreferences.getInt(username+"_colorTapete",R.drawable.casino_table);


        Ion.getDefault(context).getConscryptMiddleware().enable(false);
        // Espera síncrona
        JsonObject respuesta = Ion.with(context)
                .load("GET", HOST + GET_USER + username)
                .setHeader("Authorization", getToken())  // Token de autorización
                .asJsonObject()
                .get();

        Log.d("Get usuario", "Recibe: " + respuesta.toString());

        if (respuesta.get("user") != null) {
            return new Usuario(
                    respuesta.get("user").getAsJsonObject().get(idUsuario).getAsInt(),
                    0,//respuesta.get("user").getAsJsonObject().get(victorias).getAsInt(),
                    0,//respuesta.get("user").getAsJsonObject().get(derrotas).getAsInt(),
                    respuesta.get("user").getAsJsonObject().get(usernameUsuario).getAsString(),
                    respuesta.get("user").getAsJsonObject().get(emailUsuario).getAsString(),
                    respuesta.get("user").getAsJsonObject().get(createdUsuario).getAsString(),
                    respuesta.get("user").getAsJsonObject().get(updatedUsuario).getAsString(),
                    colorCarta,
                    colorTapete);
        } else {
            return null;
        }
    }

    public void updateUsuario(Context context, Integer id, String email) {
        final String emailJSON = "email";
        final String locationJSON = "location";

        Ion.getDefault(context).getConscryptMiddleware().enable(false);
        JsonObject json = new JsonObject();
        if (email != null) json.addProperty(emailJSON, email);
        //if (location != null) json.addProperty(locationJSON, location);

        // Envía la petición PUT y no espera respuesta alguna
        Ion.with(context)
                .load("PUT", HOST + UPDATE_USER + id.toString())
                .setHeader("Authorization", getToken())  // Token de autorización
                .setJsonObjectBody(json);
    }

    public void deleteUsuario(Context context, Integer id) {
        // Envía la petición DELETE y no espera respuesta alguna
        try{
            Ion.getDefault(context).getConscryptMiddleware().enable(false);
            Ion.with(context)
                    .load("DELETE", HOST + DELETE_USER + id)
                    .setHeader("Authorization", getToken())
                    .asJsonObject()
                    .get();  // Token de autorización
        }catch(Exception e){
            Log.d("Usuario Borrado",e.getMessage());
        }
    }

    public ArrayList<Partida> getPartidasPublicas(Context context) throws ExecutionException, InterruptedException {
        ArrayList<Partida> partidasRecuperadas = new ArrayList<Partida>();

        Ion.getDefault(context).getConscryptMiddleware().enable(false);
        // Espera síncrona
        JsonObject partidasJSON = Ion.with(context)
                .load("GET", HOST + GET_PUBLICAS)
                .setHeader("Authorization", getToken())  // Token de autorización
                .asJsonObject()
                .get();

        Log.d("Mensaje Partidas Recibido", partidasJSON.toString());

        if (partidasJSON.get("games") != null && partidasJSON.get("games").isJsonNull()) return partidasRecuperadas;
        else {
            JsonArray partidasJSONArray = partidasJSON.getAsJsonArray("games");
            for (JsonElement par : partidasJSONArray) {
                JsonObject parObj = par.getAsJsonObject();
                partidasRecuperadas.add(
                        new Partida(
                                parObj.get("id").getAsLong(),
                                parObj.get("name").getAsString(),
                                parObj.get("players_count").getAsInt(),
                                parObj.get("creation_date").getAsString(),
                                parObj.get("end_date").getAsString()
                        )
                );
            }
            // Devuelve el listado de partidas publicas recuperadas del backend
            return partidasRecuperadas;
        }
    }

    public Partida createAndJoinGame(Context context, Integer userID,String nombre,boolean publica) throws ExecutionException, InterruptedException {
        Partida creada = null;

        Ion.getDefault(context).getConscryptMiddleware().enable(false);
        JsonObject json = new JsonObject();
        if (nombre != null) json.addProperty("name", nombre);
        json.addProperty("public", publica);

        // Espera síncrona
        JsonObject partidaJSON = Ion.with(context)
                .load("POST", HOST + CREATE_GAME+userID)
                .setHeader("Authorization", getToken())  // Token de autorización
                .setJsonObjectBody(json)
                .asJsonObject()
                .get();

        Log.d("CreateJoin", "Recibido: " + partidaJSON.toString());

        if (partidaJSON.get("game") != null) {
            creada = new Partida(
                    partidaJSON.getAsJsonObject("game").get("id").getAsLong(),
                    partidaJSON.getAsJsonObject("game").get("name").getAsString(),
                    partidaJSON.getAsJsonObject("game").get("players_count").getAsInt(),
                    partidaJSON.getAsJsonObject("game").get("creation_date").getAsString(),
                    partidaJSON.getAsJsonObject("game").get("end_date").getAsString(),
                    partidaJSON.getAsJsonObject("game").get("my_player_id").getAsLong(),
                    partidaJSON.getAsJsonObject("game").get("my_pair_id").getAsLong()
            );
        }

        return creada;
    }

    public Partida getGameById(Context context, Long id) throws ExecutionException, InterruptedException {
        Partida recuperada = null;

        Ion.getDefault(context).getConscryptMiddleware().enable(false);
        // Espera síncrona
        JsonObject partidaJSON = Ion.with(context)
                .load("GET", HOST + CREATE_GAME+id)
                .setHeader("Authorization", getToken())  // Token de autorización
                .asJsonObject()
                .get();

        if (partidaJSON.get("game") != null) {
            Usuario user = Usuario.getInstance();
            int numPlayers = 0;
            Long jugadorID = Long.valueOf(-1);
            Long parejaID = Long.valueOf(-1);
            JsonArray parejas = partidaJSON.getAsJsonObject("game").getAsJsonArray("pairs");
            for(JsonElement pareja : parejas){
                if(pareja.getAsJsonObject().get("users")!=null && !pareja.getAsJsonObject().get("users").isJsonNull()){
                    JsonObject parejaObject = pareja.getAsJsonObject();
                    for(JsonElement jugador : parejaObject.getAsJsonArray("users")){
                        JsonObject jugadorObject = jugador.getAsJsonObject();
                        if(jugadorObject.get("id").getAsLong() == user.getId()){
                            jugadorID = jugadorObject.get("id").getAsLong();
                            parejaID = parejaObject.get("id").getAsLong();
                        }
                        numPlayers++;
                    }
                }
            }
            recuperada = new Partida(
                    id,
                    jugadorID,
                    parejaID,
                    numPlayers
            );
        }

        return recuperada;
    }

    public ArrayList<Partida> getPartidasByUser(Context context, int userId) throws ExecutionException, InterruptedException {
        ArrayList<Partida> partidasRecuperadas = new ArrayList<Partida>();

        Ion.getDefault(context).getConscryptMiddleware().enable(false);
        // Espera síncrona
        JsonObject partidasJSON = Ion.with(context)
                .load("GET", HOST + GET_USER_GAMES + userId)
                .setHeader("Authorization", getToken())  // Token de autorización
                .asJsonObject()
                .get();

        Log.d("Partidas Usuario", partidasJSON.toString());

        if (partidasJSON.get("games") != null && !partidasJSON.get("games").isJsonNull()) {
            JsonArray partidasJSONArray = partidasJSON.getAsJsonArray("games");
            for (JsonElement par : partidasJSONArray) {
                JsonObject parObj = par.getAsJsonObject();
                boolean winned;
                if(parObj.get("winned") != null){
                    winned = true;
                }else{
                    winned = false;
                }
                int puntos;
                if(parObj.get("points") != null){
                    puntos = parObj.get("points").getAsInt();
                }else{
                    puntos = 0;
                }

                partidasRecuperadas.add(
                        new Partida(
                                parObj.get("id").getAsLong(),
                                parObj.get("name").getAsString(),
                                parObj.get("players_count").getAsInt(),
                                parObj.get("creation_date").getAsString(),
                                parObj.get("end_date").getAsString(),
                                puntos,
                                winned
                        )

                );
            }
            // Devuelve el listado de partidas publicas recuperadas del backend
        }
        return partidasRecuperadas;
    }

    public ArrayList<String> getJugadores(Context context, Long idPartida) throws ExecutionException, InterruptedException {
        ArrayList<String> jugadores = new ArrayList<>(Collections.nCopies(6, null));

        Ion.getDefault(context).getConscryptMiddleware().enable(false);

        JsonObject game = Ion.with(context)
                .load("GET",HOST+GET_PUBLICAS+idPartida.toString())
                .setHeader("Authorization", getToken())  // Token de autorización
                .asJsonObject()
                .get();
        Log.d("getJugadores JSON received",game.toString());

        game = game.get("game").getAsJsonObject();
        Log.d("getJugadores JSON game",game.toString());


        JsonArray parejasJsonArray = game.getAsJsonArray("pairs");
        Log.d("getJugadores JSON pair array", parejasJsonArray.toString());

        //JsonElement jugador11 = null, jugador12 = null, jugador21 = null, jugador22 = null;
        //ArrayList<JsonElement> jugadoresElem = new ArrayList<>(Collections.nCopies(4, null));
        int i = 0, j;
        for (JsonElement parejaElem : parejasJsonArray)
        {
            JsonObject pareja = parejaElem.getAsJsonObject();
            Log.d("getJugadores JSON pareja",pareja.toString());
            j = 0;
            if(pareja.get("users") != null && !pareja.get("users").isJsonNull()) {
                JsonArray jugadoresArray = pareja.getAsJsonArray("users");
                if (jugadoresArray != null) {
                    for (JsonElement jugadorElem : jugadoresArray) {
                        JsonObject jugador = jugadorElem.getAsJsonObject();
                        Log.d("getJugadores JSON pareja:jugador", jugador.toString());
                        Log.d("Index", ((Integer) (j + i * 2)).toString());
                        jugadores.set(j + i * 2, jugador.get("username").getAsString());
                        j++;
                    }
                }
            }
            jugadores.set(4+i,((Long)pareja.get("id").getAsLong()).toString());
            i++;
        }
        /*
        JsonObject pareja1 = parejasJsonArray.get(0).getAsJsonObject(), pareja2 = parejasJsonArray.get(0).getAsJsonObject();
        Log.d("getJugadores JSON parejas","1: "+pareja1.toString()+", 2: "+pareja2.toString());
        JsonArray jugadoresPareja1 = pareja1.getAsJsonArray("users");
        JsonArray jugadoresPareja2 = pareja2.getAsJsonArray("users");

        JsonElement jugador11, jugador12, jugador21, jugador22;
        try {
            jugador11 = jugadoresPareja1.get(0);
        } catch (IndexOutOfBoundsException e)   {
            jugador11 = null;
        }
        try {
            jugador12 = jugadoresPareja1.get(1);
        } catch (IndexOutOfBoundsException e)   {
            jugador12 = null;
        }
        try {
            jugador21 = jugadoresPareja2.get(0);
        } catch (IndexOutOfBoundsException e)   {
            jugador21 = null;
        }
        try {
            jugador22 = jugadoresPareja2.get(1);
        } catch (IndexOutOfBoundsException e)   {
            jugador22 = null;
        }


        if(jugadoresElem.get(0) == null) jugadores.set(0, null);
        else                  jugadores.set(0, jugadoresElem.get(0).getAsJsonObject().get("username").getAsString());
        if(jugadoresElem.get(1) == null) jugadores.set(1, null);
        else                  jugadores.set(1, jugadoresElem.get(1).getAsJsonObject().get("username").getAsString());
        if(jugadoresElem.get(2) == null) jugadores.set(2, null);
        else                  jugadores.set(2, jugadoresElem.get(2).getAsJsonObject().get("username").getAsString());
        if(jugadoresElem.get(3) == null) jugadores.set(3, null);
        else                  jugadores.set(3, jugadoresElem.get(3).getAsJsonObject().get("username").getAsString());

        if(pareja1 == null) jugadores.set(4, null);
        else                jugadores.set(4, ((Long)pareja1.get("id").getAsLong()).toString());
        if(pareja2 == null) jugadores.set(5, null);
        else                jugadores.set(5, ((Long)pareja2.get("id").getAsLong()).toString());
*/
        return jugadores;
    }


    public Long joinGame(Context context, Long idJugador, Long idPareja)  {
        // Objeto JSON a pasar al backend
        JsonObject json = new JsonObject();
        json.addProperty("user_id", idJugador);
        json.addProperty("pair_id", idPareja);

        Log.d("Envio join pareja", json.toString());
        // Retorna el id del jugador (-1 si hay algun problema)
        Long id = null;
        try {
            Ion.getDefault(context).getConscryptMiddleware().enable(false);
            // Espera síncrona
            JsonObject respuesta = Ion.with(context)
                    .load("POST",HOST+JOIN_PAREJA)
                    .setHeader("Authorization", getToken())  // Token de autorización
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .get();

            Log.d("Respuesta join pareja", respuesta.toString());
            id = respuesta.get("player").getAsJsonObject().get("id").getAsLong();
        } catch (Exception e)   {
            id = -1L;
            Log.d("Respuesta join pareja", "fallo");
        }

        return id;
    }
//TODO implementar la conexion con api
    public ArrayList<Partida> getTournamentGames(Context context) throws ExecutionException, InterruptedException {
        ArrayList<Partida> partidasRecuperadas = new ArrayList<Partida>();

        // Espera síncrona
        JsonObject partidasJSON = Ion.with(context)
                .load("GET", HOST + GET_TORNEOS)
                .setHeader("Authorization", getToken())  // Token de autorización
                .asJsonObject()
                .get();

        Log.d("Mensaje Partidas Recibido", partidasJSON.toString());

        if (partidasJSON.get("games")==null && partidasJSON.get("games").isJsonNull()) return partidasRecuperadas;
        else {
            JsonArray partidasJSONArray = partidasJSON.getAsJsonArray("games");
            for (JsonElement par : partidasJSONArray) {
                JsonObject parObj = par.getAsJsonObject();
                partidasRecuperadas.add(
                        new Partida(
                                parObj.get("id").getAsLong(),
                                parObj.get("name").getAsString(),
                                parObj.get("players_count").getAsInt(),
                                parObj.get("creation_date").getAsString(),
                                parObj.get("end_date").getAsString()
                        )
                );
            }
            // Devuelve el listado de partidas publicas recuperadas del backend
            return partidasRecuperadas;
        }
    }
}
