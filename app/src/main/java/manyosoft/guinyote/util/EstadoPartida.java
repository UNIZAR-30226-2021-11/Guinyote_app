package manyosoft.guinyote.util;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.gson.GsonParser;

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
    private Long id, points_team_a, points_team_b, points_sing_a, points_sing_b, ronda;
    private boolean vueltas, arrastre;

    // Acciones requeridas al jugador
    private boolean tirarCarta, cantar, cambiar;
    private ArrayList<Integer> cartas;
    private ArrayList<Boolean> puedeCarta;

    //

    public EstadoPartida(Long id, Long points_team_a, Long points_team_b, Long points_sing_a,
                         Long points_sing_b, Long ronda, boolean vueltas, boolean arrastre,
                         boolean tirarCarta, boolean cantar, boolean cambiar, Integer carta1,
                         Integer carta2, Integer carta3, Integer carta4, Integer carta5,
                         Integer carta6) {
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
        this.cartas.add(carta1);
        this.cartas.add(carta2);
        this.cartas.add(carta3);
        this.cartas.add(carta4);
        this.cartas.add(carta5);
        this.cartas.add(carta6);
    }

    // TODO Recoger el estado de la partida a partir del JSON que el backend envia
    public EstadoPartida(String json, Long idPlayer, Context context)   {
        JSONObject root = null;
        try {
            root = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(root != null)  {
            try {
                points_team_a = root.getLong("points_team_a");
                points_team_b = root.getLong("points_team_b");
                points_sing_a = root.getLong("points_sing_a");
                points_sing_a = root.getLong("points_sing_b");
                ronda = root.getLong("round");
                vueltas = root.getBoolean("vueltas");
                arrastre = root.getBoolean("arrastre");
                JSONArray jugadores = root.getJSONArray("players");
                JSONObject jugador;
                boolean encontrado = false;
                int i = 0;
                while (!encontrado){
                    jugador = (JSONObject) jugadores.get(i);
                    if(jugador.getLong("id") == idPlayer){
                        encontrado = true;
                        id = idPlayer;
                        tirarCarta = jugador.getBoolean("can_play");
                        cantar = jugador.getBoolean("can_sing");
                        cambiar = jugador.getBoolean("can_change");
                        for(int j = 0; j<6; j++){
                            JSONObject carta = (JSONObject) jugador.getJSONArray("cards").get(i);
                            if(carta != null){
                                int valorCarta = carta.getInt("val");
                                String paloCarta = carta.getString("suit");
                                String nombreCarta = paloCarta + "_" + valorCarta+".png";
                                int id = context.getResources().getIdentifier(nombreCarta,"drawable",context.getPackageName());
                                cartas.add(id);
                                puedeCarta.add(carta.getBoolean("playable"));
                            }else{
                                cartas.add(null);
                                puedeCarta.add(false);
                            }
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
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
        cartas.add(indice-1,carta);
    }

    public boolean isPuedeCarta(int indice){
        return puedeCarta.get(indice-1);
    }

    public void setPuedeCarta(int indice, boolean puede) {
        puedeCarta.add(indice-1,puede);
    }
}
