package manyosoft.guinyote.util;

public class Partida {
    private Long id,playerId,pairId;
    private String nombre;
    private Integer jugadores,puntos;
    private String created, end;
    private boolean victoria;

    /**
     *  Resto de elementos necesarios para controlar la partida
     */

    public Partida(Long id, String name, Integer players)   {
        if(id != null && name != null && players != null)   {
            this.id = id;
            this.nombre = name;
            this.jugadores = players;

        }
    }

    public Partida(Long id, String name, Integer players, String created, String end)   {
        if(id != null && name != null && players != null)   {
            this.id = id;
            this.nombre = name;
            this.jugadores = players;
            this.created = created;
            this.end = end;
        }
    }

    public Partida(Long id, String name, Integer players, String created, String end,Integer puntos,boolean victoria)   {
        if(id != null && name != null && players != null)   {
            this.id = id;
            this.nombre = name;
            this.jugadores = players;
            this.created = created;
            this.end = end;
            this.puntos = puntos;
            this.victoria = victoria;
        }
    }

    public Partida(Long id, Long playerId, Long pairId, Integer jugadores) {
        this.id = id;
        this.playerId = playerId;
        this.pairId = pairId;
        this.jugadores = jugadores;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getJugadores() {
        return jugadores;
    }

    public void setJugadores(Integer jugadores) {
        this.jugadores = jugadores;
    }

    public String  getCreated(){return created;}

    public void setCreated(String created) {this.created = created;}

    public String getEnd() { return end;}

    public void setEnd(String end) {this.end = end;}

    public Integer getPuntos() {
        return puntos;
    }

    public void setPuntos(Integer puntos) {
        this.puntos = puntos;
    }

    public boolean getVictoria() {
        return victoria;
    }

    public void setVictoria(boolean victoria) {
        this.victoria = victoria;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Long getPairId() {
        return pairId;
    }

    public void setPairId(Long pairId) {
        this.pairId = pairId;
    }

    public boolean isVictoria() {
        return victoria;
    }
}
