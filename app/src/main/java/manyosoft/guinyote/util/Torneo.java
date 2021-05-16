package manyosoft.guinyote.util;

public class Torneo {
    private Long id;
    private String nombre;
    private String created, end;

    public Torneo(Long id, String nombre, String created, String end) {
        this.id = id;
        this.nombre = nombre;
        this.created = created;
        this.end = end;
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

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
