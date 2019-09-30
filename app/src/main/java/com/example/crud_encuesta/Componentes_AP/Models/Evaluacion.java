package com.example.crud_encuesta.Componentes_AP.Models;

public class Evaluacion {

    int id;
    int idCargaAcad;
    int duracion;
    int cantIntento;
    String nombre;
    String descripcion;

    public Evaluacion() {
    }

    //constructor con id
    public Evaluacion(int id, int idCargaAcad, int duracion, int cantIntento,
                      String nombre, String descripcion) {
        this.id = id;
        this.idCargaAcad = idCargaAcad;
        this.duracion = duracion;
        this.cantIntento = cantIntento;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    //constructor sin id
    //TODO: pendiente en ver si es necesario este metodo
    public Evaluacion(int idCargaAcad, int duracion, int cantIntento,
                      String nombre, String descripcion) {
        this.idCargaAcad = idCargaAcad;
        this.duracion = duracion;
        this.cantIntento = cantIntento;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCargaAcad() {
        return idCargaAcad;
    }

    public void setIdCargaAcad(int idCargaAcad) {
        this.idCargaAcad = idCargaAcad;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public int getCantIntento() {
        return cantIntento;
    }

    public void setCantIntento(int cantIntento) {
        this.cantIntento = cantIntento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}
