package com.example.crud_encuesta.Componentes_MT.Intento;

import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.List;

public class ModalidadPregunta {
    private RadioGroup opcion_multiple;
    private RadioGroup verdadero_falso;
    private Spinner emparejamiento;
    private EditText respuesta_corta;
    private int id_emparejamiento;
    private int id_respuesta_corta;

    public ModalidadPregunta() {
    }

    public RadioGroup getOpcion_multiple() {
        return opcion_multiple;
    }

    public void setOpcion_multiple(RadioGroup opcion_multiple) {
        this.opcion_multiple = opcion_multiple;
    }

    public RadioGroup getVerdadero_falso() {
        return verdadero_falso;
    }

    public void setVerdadero_falso(RadioGroup verdadero_falso) {
        this.verdadero_falso = verdadero_falso;
    }

    public Spinner getEmparejamiento() {
        return emparejamiento;
    }

    public void setEmparejamiento(Spinner emparejamiento) {
        this.emparejamiento = emparejamiento;
    }

    public EditText getRespuesta_corta() {
        return respuesta_corta;
    }

    public void setRespuesta_corta(EditText respuesta_corta) {
        this.respuesta_corta = respuesta_corta;
    }

    public int getId_emparejamiento() {
        return id_emparejamiento;
    }

    public void setId_emparejamiento(int id_emparejamiento) {
        this.id_emparejamiento = id_emparejamiento;
    }

    public int getId_respuesta_corta() {
        return id_respuesta_corta;
    }

    public void setId_respuesta_corta(int id_respuesta_corta) {
        this.id_respuesta_corta = id_respuesta_corta;
    }
}
