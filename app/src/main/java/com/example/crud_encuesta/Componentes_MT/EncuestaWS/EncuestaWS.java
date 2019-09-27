package com.example.crud_encuesta.Componentes_MT.EncuestaWS;

public class EncuestaWS {
    int id;
    int id_docente;
    String titulo_encuesta;
    String descriion_encuesta;
    String fecha_inicio_encuesta;
    String fecha_final_encuesta;
    Boolean local;

    public EncuestaWS(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_docente() {
        return id_docente;
    }

    public void setId_docente(int id_docente) {
        this.id_docente = id_docente;
    }

    public String getTitulo_encuesta() {
        return titulo_encuesta;
    }

    public void setTitulo_encuesta(String titulo_encuesta) {
        this.titulo_encuesta = titulo_encuesta;
    }

    public String getFecha_inicio_encuesta() {
        return fecha_inicio_encuesta;
    }

    public void setFecha_inicio_encuesta(String fecha_inicio_encuesta) {
        this.fecha_inicio_encuesta = fecha_inicio_encuesta;
    }

    public String getFecha_final_encuesta() {
        return fecha_final_encuesta;
    }

    public void setFecha_final_encuesta(String fecha_final_encuesta) {
        this.fecha_final_encuesta = fecha_final_encuesta;
    }

    public String getDescriion_encuesta() {
        return descriion_encuesta;
    }

    public void setDescriion_encuesta(String descriion_encuesta) {
        this.descriion_encuesta = descriion_encuesta;
    }

    public Boolean getLocal() {
        return local;
    }

    public void setLocal(Boolean local) {
        this.local = local;
    }
}
