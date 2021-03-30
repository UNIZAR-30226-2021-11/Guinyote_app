package manyosoft.guinyote.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.*;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;


public class GuinyoteClienteJWT {
    static final String HOST = "host/";
    static final String CREATE_USER = "api/v1/users/";
    static final String LOGIN = "api/v1/users/";
    static final String DELETE_USER = "api/v1/users/";

    static String token = null;

    public String getToken()    {
        return token;
    }

    /** Manda una peticion al servidor para crear un usuario
     *
      * @param apodo
     * @param usuario
     * @param mail
     * @param password
     * @return
     */
    public boolean crearUsuario(String apodo, String usuario, String mail, String password)  {
        // Constantes para trabajar con JSON
        final String usuarioObjetoJSON = "user";
        final String usuarioJSON = "username";
        final String mailJSON = "username";
        final String apodoJSON = "location";        // TODO location no es necesario, hace falta apodo
        final String passwordJSON = "password";
        //
        boolean ok = false;

        try {
            URL url = new URL(HOST+CREATE_USER);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept","application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put(usuarioJSON, usuario);
            jsonParam.put(mailJSON, mail);
            jsonParam.put(apodoJSON, apodo);
            jsonParam.put(passwordJSON, password);


            Log.i("JSON Enviado", jsonParam.toString());
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            os.writeBytes(jsonParam.toString());

            os.flush();
            os.close();

            DataInputStream is = new DataInputStream(conn.getInputStream());
            String jsonResponseString = is.readUTF();
            is.close();
            Log.i("JSON Recibido", jsonResponseString);

            JSONObject jsonResponse = new JSONObject(jsonResponseString);

            conn.disconnect();

            // GUARDAR USUARIO EN LOCAL
            if(usuario == jsonResponse.getJSONObject(usuarioObjetoJSON).getString("username")) ok = true;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return ok;
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
                .load(HOST+LOGIN)
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

    public void crearUsuario(Context context, String apodo, String usuario, String mail, String password)  {
        // Constantes para trabajar con JSON
        final String usuarioObjetoJSON = "user";
        final String usuarioJSON = "username";
        final String mailJSON = "username";
        final String apodoJSON = "location";        // TODO location no es necesario, hace falta apodo
        final String passwordJSON = "password";

        // Objeto JSON a pasar al backend
        JsonObject json = new JsonObject();
        json.addProperty(usuarioJSON, usuario);
        json.addProperty(mailJSON, mail);
        json.addProperty(apodoJSON, apodo);
        json.addProperty(passwordJSON, password);

        // Envío + Callback para la respuesta
        Ion.with(context)
                .load(HOST+LOGIN)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if(e != null)   {
                            // Do smt ?
                        }
                        else    {
                            // Hacer algo si es necesario, en principio no
                        }
                    }
                });

    }
}
