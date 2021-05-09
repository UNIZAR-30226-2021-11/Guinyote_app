package manyosoft.guinyote.util;

import android.content.Context;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import manyosoft.guinyote.R;

/**
 * Clase que representa el estado de una partida en un momento concreto
 * Es empleada para representar de una forma natural el estado de juego recibido en JSON desde
 * el BackEnd.
 */
public class EstadoPartida {

    // Estado de la partida
    private Long id, points_team_a, points_team_b, points_sing_a, points_sing_b, ronda,winner_pair;
    private boolean vueltas, arrastre,ended;
    private String singsuit,status;

    //Carta de triunfo
    private String triumph_suit;
    private Long triumph_val,triumph_points;
    private boolean triumph_playable;

    // Acciones requeridas al jugador
    private boolean tirarCarta, cantar, cambiar;
    private ArrayList<Integer> cartas;
    private ArrayList<String> cartasSuit;
    private ArrayList<Integer> cartasValue;
    private ArrayList<Boolean> puedeCarta;
    private ArrayList<Integer> cards_played_round;
    //

    public EstadoPartida(Long id, Long points_team_a, Long points_team_b, Long points_sing_a,
                         Long points_sing_b, Long ronda, boolean vueltas, boolean arrastre,
                         boolean tirarCarta, boolean cantar, boolean cambiar, Integer carta1,
                         Integer carta2, Integer carta3, Integer carta4, Integer carta5,
                         Integer carta6, ArrayList<String> cartasSuit, ArrayList<Integer> cartasValue) {
        this.id = id;
        this.points_team_a = points_team_a;
        this.points_team_b = points_team_b;
        this.points_sing_a = points_sing_a;
        this.points_sing_b = points_sing_b;
        this.ronda = ronda;
        this.vueltas = vueltas;
        this.arrastre = arrastre;
        this.tirarCarta = tirarCarta;
        this.cantar = cantar;
        this.cambiar = cambiar;
        this.cartasSuit = cartasSuit;
        this.cartasValue = cartasValue;
        this.cartas.add(carta1);
        this.cartas.add(carta2);
        this.cartas.add(carta3);
        this.cartas.add(carta4);
        this.cartas.add(carta5);
        this.cartas.add(carta6);
    }

    public EstadoPartida(String json, Long idPlayer, Context context)   {
        JsonObject root = null;
        root = new Gson().fromJson(json, JsonObject.class);
        if(root != null)  {
            cartas = new ArrayList<>();
            cartasSuit = new ArrayList<>();
            cartasValue = new ArrayList<>();
            puedeCarta = new ArrayList<>();
            //Obtencion de valores de la partida
            status = root.get("status").getAsString();
            root = root.get("game_state").getAsJsonObject();
            points_team_a = root.get("points_team_a").getAsLong();
            points_team_b = root.get("points_team_b").getAsLong();
            points_sing_a = root.get("points_sing_a").getAsLong();
            points_sing_a = root.get("points_sing_b").getAsLong();
            ronda = root.get("current_round").getAsLong();
            vueltas = root.get("vueltas").getAsBoolean();
            arrastre = root.get("arrastre").getAsBoolean();
            ended = root.get("ended").getAsBoolean();
            winner_pair = root.get("winner_pair").getAsLong();
            if(!root.get("cards_played_round").isJsonNull()) {
                JsonArray cards_played_json = root.get("cards_played_round").getAsJsonArray();
                cards_played_round = new ArrayList<Integer>();
                for(JsonElement card_played : cards_played_json){
                    JsonObject card_played_object = card_played.getAsJsonObject();
                    int valorCarta = card_played_object.get("val").getAsInt();
                    String paloCarta = card_played_object.get("suit").getAsString();
                    String nombreCarta = paloCarta + "_" + valorCarta;
                    int id = context.getResources().getIdentifier(nombreCarta,"drawable",context.getPackageName());
                    cards_played_round.add(id);
                }
            }else{
                cards_played_round = null;
            }

            //Obtencion valores del triunfo
            triumph_suit = root.get("triumph_card").getAsJsonObject().get("suit").getAsString();
            triumph_val =  root.get("triumph_card").getAsJsonObject().get("val").getAsLong();
            triumph_points =  root.get("triumph_card").getAsJsonObject().get("points").getAsLong();
            triumph_playable =  root.get("triumph_card").getAsJsonObject().get("playable").getAsBoolean();

            //Obtencion de las cartas
            JsonArray jugadores = root.get("players").getAsJsonObject().get("players").getAsJsonArray();
            JsonObject jugador;
            boolean encontrado = false;
            int i = 0;
            while (!encontrado){
                jugador = jugadores.get(i).getAsJsonObject();
                if(jugador.get("id").getAsInt() == idPlayer){
                    encontrado = true;
                    id = idPlayer;
                    tirarCarta = jugador.get("can_play").getAsBoolean();
                    cantar = jugador.get("can_sing").getAsBoolean();
                    singsuit = jugador.get("sing_suit").getAsString();
                    cambiar = jugador.get("can_change").getAsBoolean();
                    for(int j = 0; j<6; j++){
                        JsonObject carta = (JsonObject) jugador.get("cards").getAsJsonArray().get(j);
                        if(carta != null){
                           int valorCarta = carta.get("val").getAsInt();
                            String paloCarta = carta.get("suit").getAsString();
                            String nombreCarta = paloCarta + "_" + valorCarta;
                            int id = context.getResources().getIdentifier(nombreCarta,"drawable",context.getPackageName());
                            cartas.add(id);
                            cartasSuit.add(paloCarta);
                            cartasValue.add(valorCarta);
                            puedeCarta.add(carta.get("playable").getAsBoolean());
                        }else{
                            cartas.add(null);
                            puedeCarta.add(false);
                        }
                    }
                }else{
                    i++;
                }
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPoints_team_a() {
        return points_team_a;
    }

    public void setPoints_team_a(Long points_team_a) {
        this.points_team_a = points_team_a;
    }

    public Long getPoints_team_b() {
        return points_team_b;
    }

    public void setPoints_team_b(Long points_team_b) {
        this.points_team_b = points_team_b;
    }

    public Long getPoints_sing_a() {
        return points_sing_a;
    }

    public void setPoints_sing_a(Long points_sing_a) {
        this.points_sing_a = points_sing_a;
    }

    public Long getPoints_sing_b() {
        return points_sing_b;
    }

    public void setPoints_sing_b(Long points_sing_b) {
        this.points_sing_b = points_sing_b;
    }

    public Long getRonda() {
        return ronda;
    }

    public void setRonda(Long ronda) {
        this.ronda = ronda;
    }

    public boolean isVueltas() {
        return vueltas;
    }

    public void setVueltas(boolean vueltas) {
        this.vueltas = vueltas;
    }

    public boolean isArrastre() {
        return arrastre;
    }

    public void setArrastre(boolean arrastre) {
        this.arrastre = arrastre;
    }

    public boolean isTirarCarta() {
        return tirarCarta;
    }

    public void setTirarCarta(boolean tirarCarta) {
        this.tirarCarta = tirarCarta;
    }

    public boolean isCantar() {
        return cantar;
    }

    public void setCantar(boolean cantar) {
        this.cantar = cantar;
    }

    public boolean isCambiar() {
        return cambiar;
    }

    public void setCambiar(boolean cambiar) {
        this.cambiar = cambiar;
    }

    public Integer getCarta(int indice){
        return cartas.get(indice-1);
    }

    public void setCarta(int indice, Integer carta){
        cartas.set(indice-1,carta);
    }

    public String getCartaSuit(int indice){
        return cartasSuit.get(indice-1);
    }

    public void setCartaSuit(int indice, String carta){
        cartasSuit.set(indice-1, carta);
    }

    public Integer getCartaVal(int indice){
        return cartasValue.get(indice-1);
    }

    public void setCartaVal(int indice, Integer carta){
        cartasValue.set(indice-1,carta);
    }

    public boolean isPuedeCarta(int indice){
        return puedeCarta.get(indice-1);
    }

    public void setPuedeCarta(int indice, boolean puede) {
        puedeCarta.add(indice-1,puede);
    }

    public String getSingsuit() {
        return singsuit;
    }

    public void setSingsuit(String singsuit) {
        this.singsuit = singsuit;
    }

    public Long getWinner_pair() {
        return winner_pair;
    }

    public void setWinner_pair(Long winner_pair) {
        this.winner_pair = winner_pair;
    }

    public ArrayList<Integer> getCards_played_round() {
        return cards_played_round;
    }

    public void setCards_played_round(ArrayList<Integer> cards_played_round) {
        this.cards_played_round = cards_played_round;
    }

    public boolean isEnded() {
        return ended;
    }

    public void setEnded(boolean ended) {
        this.ended = ended;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTriumph_suit() {
        return triumph_suit;
    }

    public void setTriumph_suit(String triumph_suit) {
        this.triumph_suit = triumph_suit;
    }

    public Long getTriumph_val() {
        return triumph_val;
    }

    public void setTriumph_val(Long triumph_val) {
        this.triumph_val = triumph_val;
    }

    public Long getTriumph_points() {
        return triumph_points;
    }

    public void setTriumph_points(Long triumph_points) {
        this.triumph_points = triumph_points;
    }

    public boolean isTriumph_playable() {
        return triumph_playable;
    }

    public void setTriumph_playable(boolean triumph_playable) {
        this.triumph_playable = triumph_playable;
    }
}


