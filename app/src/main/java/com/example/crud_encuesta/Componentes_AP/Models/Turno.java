package com.example.crud_encuesta.Componentes_AP.Models;

public class Turno {

    int id;
    int idEvaluacion;
    String dateInicial;
    String dateFinal;
    int visible;
    String contrasenia;

    public Turno() {
    }

    //constructor con id: este constructor nos servirá en el momento de editar un registro
    public Turno(int id, int idEvaluacion, String dateInicial,
                 String dateFinal, int visible, String contrasenia) {
        this.id = id;
        this.idEvaluacion = idEvaluacion;
        this.dateInicial = dateInicial;
        this.dateFinal = dateFinal;
        this.visible = visible;
        this.contrasenia = contrasenia;
    }

    //constructor sin id: este constructor nos servirá para poder crear registros, dado que el id por ser autoincremental se lo dará la base de datos
    public Turno(int idEvaluacion, String dateInicial, String dateFinal, int visible, String contrasenia) {
        this.idEvaluacion = idEvaluacion;
        this.dateInicial = dateInicial;
        this.dateFinal = dateFinal;
        this.visible = visible;
        this.contrasenia = contrasenia;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdEvaluacion() {
        return idEvaluacion;
    }

    public void setIdEvaluacion(int idEvaluacion) {
        this.idEvaluacion = idEvaluacion;
    }

    public String getDateInicial() {
        return dateInicial;
    }

    public void setDateInicial(String dateInicial) {
        this.dateInicial = dateInicial;
    }

    public String getDateFinal() {
        return dateFinal;
    }

    public void setDateFinal(String dateFinal) {
        this.dateFinal = dateFinal;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }
}
