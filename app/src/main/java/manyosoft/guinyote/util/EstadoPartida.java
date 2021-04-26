package manyosoft.guinyote.util;

import com.google.gson.JsonObject;
import com.koushikdutta.ion.gson.GsonParser;

import org.json.JSONException;
import org.json.JSONObject;

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
    private Integer carta1, carta2, carta3, carta4, carta5, carta6;
    private boolean puedeCarta1, puedeCarta2, puedeCarta3, puedeCarta4, puedeCarta5, puedeCarta6;

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
        this.carta1 = carta1;
        this.carta2 = carta2;
        this.carta3 = carta3;
        this.carta4 = carta4;
        this.carta5 = carta5;
        this.carta6 = carta6;
    }

    // TODO Recoger el estado de la partida a partir del JSON que el backend envia
    public EstadoPartida(String json, Long idPlayer)   {
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

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        /**
         * DE LAS CARTAS SE TIENE QUE GUARDAR SU ID EN DRAWABLES
         */



        /**
         * INCOMPLETO....
         */
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

    public Integer getCarta1() {
        return carta1;
    }

    public void setCarta1(Integer carta1) {
        this.carta1 = carta1;
    }

    public Integer getCarta2() {
        return carta2;
    }

    public void setCarta2(Integer carta2) {
        this.carta2 = carta2;
    }

    public Integer getCarta3() {
        return carta3;
    }

    public void setCarta3(Integer carta3) {
        this.carta3 = carta3;
    }

    public Integer getCarta4() {
        return carta4;
    }

    public void setCarta4(Integer carta4) {
        this.carta4 = carta4;
    }

    public Integer getCarta5() {
        return carta5;
    }

    public void setCarta5(Integer carta5) {
        this.carta5 = carta5;
    }

    public Integer getCarta6() {
        return carta6;
    }

    public void setCarta6(Integer carta6) {
        this.carta6 = carta6;
    }

    public boolean isPuedeCarta1() {
        return puedeCarta1;
    }

    public void setPuedeCarta1(boolean puedeCarta1) {
        this.puedeCarta1 = puedeCarta1;
    }

    public boolean isPuedeCarta2() {
        return puedeCarta2;
    }

    public void setPuedeCarta2(boolean puedeCarta2) {
        this.puedeCarta2 = puedeCarta2;
    }

    public boolean isPuedeCarta3() {
        return puedeCarta3;
    }

    public void setPuedeCarta3(boolean puedeCarta3) {
        this.puedeCarta3 = puedeCarta3;
    }

    public boolean isPuedeCarta4() {
        return puedeCarta4;
    }

    public void setPuedeCarta4(boolean puedeCarta4) {
        this.puedeCarta4 = puedeCarta4;
    }

    public boolean isPuedeCarta5() {
        return puedeCarta5;
    }

    public void setPuedeCarta5(boolean puedeCarta5) {
        this.puedeCarta5 = puedeCarta5;
    }

    public boolean isPuedeCarta6() {
        return puedeCarta6;
    }

    public void setPuedeCarta6(boolean puedeCarta6) {
        this.puedeCarta6 = puedeCarta6;
    }
}
