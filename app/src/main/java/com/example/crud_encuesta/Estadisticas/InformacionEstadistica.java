package com.example.crud_encuesta.Estadisticas;

import androidx.annotation.NonNull;

public class InformacionEstadistica {
    private String nomEva;
    private String cantidaEvaluados;
    private String reprobados;
    private String aprobados;
    private String cantidadInscritos;
    private String mayorNota;

    public InformacionEstadistica(String nomEva, String cantidaEvaluados, String reprobados, String aprobados, String cantidadInscritos,String mayorNota) {
        this.nomEva = nomEva;
        this.cantidaEvaluados = cantidaEvaluados;
        this.reprobados = reprobados;
        this.aprobados = aprobados;
        this.cantidadInscritos = cantidadInscritos;
        this.mayorNota=mayorNota;
    }

    public String getNomEva() {
        return nomEva;
    }

    public void setNomEva(String nomEva) {
        this.nomEva = nomEva;
    }

    public String getCantidaEvaluados() {
        return cantidaEvaluados;
    }

    public void setCantidaEvaluados(String cantidaEvaluados) {
        this.cantidaEvaluados = cantidaEvaluados;
    }

    public String getReprobados() {
        return reprobados;
    }

    public void setReprobados(String reprobados) {
        this.reprobados = reprobados;
    }

    public String getAprobados() {
        return aprobados;
    }

    public void setAprobados(String aprobados) {
        this.aprobados = aprobados;
    }

    public String getCantidadInscritos() {
        return cantidadInscritos;
    }

    public void setCantidadInscritos(String cantidadInscritos) {
        this.cantidadInscritos = cantidadInscritos;
    }

    public String getMayorNota() {
        return mayorNota;
    }

    public void setMayorNota(String mayorNota) {
        this.mayorNota = mayorNota;
    }

    @NonNull
    @Override
    public String toString() {
        return "Evaluacion:"+nomEva+"\nAlumnos Evaluados: "+cantidaEvaluados+" de "+cantidadInscritos+"\nAprobados: "+aprobados+
                "\nReprobados: "+reprobados+"\nMayor Nota: "+mayorNota+"";
    }
}
