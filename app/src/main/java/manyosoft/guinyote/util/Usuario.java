package manyosoft.guinyote.util;

import android.graphics.drawable.Drawable;

import com.koushikdutta.ion.builder.Builders;

import manyosoft.guinyote.R;

public class Usuario {
    private Integer id,victorias,derrotas;
    private String username, email, location, created_at, updated_at;
    private Integer colorCarta,colorTapete;

    private static Usuario instance = null;

    public static synchronized Usuario getInstance() {
        return instance;
    }

    public Usuario(Integer id, Integer victorias, Integer derrotas, String username, String email, String location, String created_at, String updated_at) {
        this.id = id;
        this.victorias = victorias;
        this.derrotas = derrotas;
        this.username = username;
        this.email = email;
        this.location = location;
        this.created_at = created_at;
        this.updated_at = updated_at;
        colorCarta = R.drawable.reverso;
        colorTapete = R.drawable.casino_table;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
