package manyosoft.guinyote.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.JsonToken;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class GuinyoteClienteJWT {
    static final String HOST = "host/";

    // Usuarios
    static final String CREATE_USER = "api/v1/users/";
    static final String LOGIN = "api/v1/users/";
    static final String DELETE_USER = "api/v1/users/";
    static final String GET_USER = "api/v1/users/";
    static final String UPDATE_USER = "api/v1/users/";

    // Partidas
    static final String GET_PUBLICAS = "api/v1/games/";


    static String token = null;

    public String getToken()    {
        return token;
    }



    public void loginUsuario(Context context, String username, String password)  {
        final String usuarioJSON = "username";
        final String passwordJSON = "password";
        final String tokenJSON = "token";

        // Objeto JSON a pasar al backend
        JsonObject json = new JsonObject();
        json.addProperty(usuarioJSON, username);
        json.addProperty(passwordJSON, password);

        // Envío + Callback para la respuesta
        Ion.with(context)
                .load("POST",HOST+LOGIN)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // Guarda el token obtenido en la respuesta
                        token = result.get(tokenJSON).getAsString();
                    }
                });
    }

    public void crearUsuario(Context context, String location, String usuario, String mail, String password)  {
        // Constantes para trabajar con JSON
        final String usuarioObjetoJSON = "user";
        final String usuarioJSON = "username";
        final String mailJSON = "username";
        final String locationJSON = "location";
        final String passwordJSON = "password";

        // Objeto JSON a pasar al backend
        JsonObject json = new JsonObject();
        json.addProperty(usuarioJSON, usuario);
        json.addProperty(mailJSON, mail);
        json.addProperty(locationJSON, location);
        json.addProperty(passwordJSON, password);

        // Envío + Callback para la respuesta
        Ion.with(context)
                .load("POST", HOST+LOGIN)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if(e != null)   {
                            String mensaje = e.getMessage();
                            if(mensaje == null) mensaje = "Error desconocido en creación de usuario";
                            Log.d("Creación de usuario", mensaje);
                        }
                        else    {
                            // Ok
                            Log.d("Creación de usuario", "Ok");
                        }
                    }
                });

    }

    public Usuario getUsuario(Context context, String username) throws ExecutionException, InterruptedException {
        final String idUsuario = "id";
        final String usernameUsuario = "username";
        final String emailUsuario = "email";
        final String locationUsuario = "location";
        final String createdUsuario = "created_at";
        final String updatedUsuario = "updated_at";

        // Espera síncrona
        JsonObject respuesta = Ion.with(context)
                .load("GET",HOST+GET_USER+username)
                .setHeader("Authorization", token)  // Token de autorización
                .asJsonObject()
                .get();

        return new Usuario(
                respuesta.get(idUsuario).getAsInt(),
                respuesta.get(usernameUsuario).getAsString(),
                respuesta.get(emailUsuario).getAsString(),
                respuesta.get(locationUsuario).getAsString(),
                respuesta.get(createdUsuario).getAsString(),
                respuesta.get(updatedUsuario).getAsString() );
    }

    public void updateUsuario(Context context, Integer id, String email, String location){
        final String emailJSON = "email";
        final String locationJSON = "location";

        JsonObject json = new JsonObject();
        if(email != null)       json.addProperty(emailJSON, email);
        if(location != null)    json.addProperty(locationJSON, location);

        // Envía la petición PUT y no espera respuesta alguna
        Ion.with(context)
                .load("PUT",HOST+UPDATE_USER+id.toString())
                .setHeader("Authorization", token)  // Token de autorización
                .setJsonObjectBody(json);
    }

    public void deleteUsuario(Context context, Integer id)  {
        // Envía la petición DELETE y no espera respuesta alguna
        Ion.with(context)
                .load("DELETE",HOST+DELETE_USER+id.toString())
                .setHeader("Authorization", token);  // Token de autorización
    }

    public ArrayList<Partida> getPartidasPublicas(Context context) throws ExecutionException, InterruptedException {
        ArrayList<Partida> partidasRecuperadas = new ArrayList<Partida>();

        // Espera síncrona
        JsonObject partidasJSON = Ion.with(context)
                .load("GET",HOST+GET_PUBLICAS)
                .setHeader("Authorization", token)  // Token de autorización
                .asJsonObject()
                .get();

        JsonArray partidasJSONArray = partidasJSON.getAsJsonArray("games");
        for(JsonElement par : partidasJSONArray)  {
            JsonObject parObj = par.getAsJsonObject();
            partidasRecuperadas.add(
                    new Partida(
                            parObj.get("id").getAsLong(),
                            parObj.get("name").getAsString(),
                            parObj.get("player_count").getAsInt(),
                            parObj.get("creation_date").getAsString(),
                            parObj.get("end_date").getAsString()
                    )
            );
        }
        // Devuelve el listado de partidas publicas recuperadas del backend
        return partidasRecuperadas;
    }

    public Partida createAndJoinGame(Context context, Integer userID) throws ExecutionException, InterruptedException {
        Partida creada = null;

        // Espera síncrona
        JsonObject partidaJSON = Ion.with(context)
                .load("GET",HOST+GET_PUBLICAS)
                .setHeader("Authorization", token)  // Token de autorización
                .asJsonObject()
                .get();

        if(partidaJSON != null) {
            creada = new Partida(
                    partidaJSON.get("id").getAsLong(),
                    partidaJSON.get("name").getAsString(),
                    partidaJSON.get("player_count").getAsInt(),
                    partidaJSON.get("creation_date").getAsString(),
                    partidaJSON.get("end_date").getAsString()
            );
        }

        return creada;
    }
}
