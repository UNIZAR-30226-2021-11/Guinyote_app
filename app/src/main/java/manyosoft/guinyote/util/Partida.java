package manyosoft.guinyote.util;

public class Partida {
    private Long id;
    private String nombre;
    private Integer jugadores;

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


}
