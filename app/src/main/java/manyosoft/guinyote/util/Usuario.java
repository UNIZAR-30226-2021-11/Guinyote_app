package manyosoft.guinyote.util;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;

import com.koushikdutta.ion.builder.Builders;

import manyosoft.guinyote.R;
import manyosoft.guinyote.ui.UserProfile;

public class Usuario {
    private Integer id,victorias,derrotas;
    private String username, email, created_at, updated_at;
    private Integer colorCarta,colorTapete;

    private static Usuario instance = null;

    public static synchronized Usuario getInstance() {
        return instance;
    }

    public Usuario(Integer id, Integer victorias, Integer derrotas, String username, String email, String created_at, String updated_at,int colorCarta,int colorTapete) {
        this.id = id;
        this.victorias = victorias;
        this.derrotas = derrotas;
        this.username = username;
        this.email = email;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.colorCarta = colorCarta;
        this.colorTapete = colorTapete;
    }

    public Usuario(Usuario u){
        instance = u;
    }

    public void logOut(){
        instance = null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public Integer getVictorias() {
        return victorias;
    }

    public void setVictorias(Integer victorias) {
        this.victorias = victorias;
    }

    public Integer getDerrotas() {
        return derrotas;
    }

    public void setDerrotas(Integer derrotas) {
        this.derrotas = derrotas;
    }

    public Integer getColorCarta() {
        return colorCarta;
    }

    public void setColorCarta(Integer colorCarta) {
        this.colorCarta = colorCarta;
    }

    public Integer getColorTapete() {
        return colorTapete;
    }

    public void setColorTapete(Integer colorTapete) {
        this.colorTapete = colorTapete;
    }
}
