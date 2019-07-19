package com.example.crud_encuesta.Componentes_MT.Intento;

import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.List;

public class ReturnView {
    private View view;
    private RadioGroup radioGroupOM;
    private EditText editText;
    private RadioGroup radioGroupVF;
    private int idPreguntaRC;
    private List<Spinner> spinner;
    private List<Integer> idPreguntaSP;

    public ReturnView() {
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public RadioGroup getRadioGroupOM() {
        return radioGroupOM;
    }

    public void setRadioGroupOM(RadioGroup radioGroupOM) {
        this.radioGroupOM = radioGroupOM;
    }

    public EditText getEditText() {
        return editText;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    public RadioGroup getRadioGroupVF() {
        return radioGroupVF;
    }

    public void setRadioGroupVF(RadioGroup radioGroupVF) {
        this.radioGroupVF = radioGroupVF;
    }

    public int getIdPreguntaRC() {
        return idPreguntaRC;
    }

    public void setIdPreguntaRC(int idPreguntaRC) {
        this.idPreguntaRC = idPreguntaRC;
    }

    public List<Integer> getIdPreguntaSP() {
        return idPreguntaSP;
    }

    public void setIdPreguntaSP(List<Integer> idPreguntaSP) {
        this.idPreguntaSP = idPreguntaSP;
    }

    public List<Spinner> getSpinner() {
        return spinner;
    }

    public void setSpinner(List<Spinner> spinner) {
        this.spinner = spinner;
    }
}
