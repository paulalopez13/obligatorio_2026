package uy.edu.um.doors.entities;

import uy.edu.um.tad.list.MyList;

public class Proceso implements Comparable<Proceso> {

    private String pid;
    private String nombre;
    private Usuario usuario;
    private int prioridad;
    private String estado; //Puede ser NEW, PENDING, RUNNING o FINISHED. Como eso solo se edita desde el codigo (no el usuario) no hay problemas con mayusculas y minisculas
    private MyList<Evento> eventos;


    public Proceso(String pid, String nombre, int prioridad, Usuario usuario, String estado, MyList<Evento> eventos) {
        this.pid = pid;
        this.nombre = nombre;
        this.prioridad = prioridad;
        this.usuario = usuario;
        this.estado = estado;
        this.eventos = eventos;
    }


    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public MyList<Evento> getEventos() {
        return eventos;
    }

    public void setEventos(MyList<Evento> eventos) {
        this.eventos = eventos;
    }


    @Override
    public int compareTo(Proceso otro){
        return Integer.compare(this.prioridad, otro.prioridad); //devuelve negativo si this<otro, 0 si song iguales, positivo si this>otro
    }

}
