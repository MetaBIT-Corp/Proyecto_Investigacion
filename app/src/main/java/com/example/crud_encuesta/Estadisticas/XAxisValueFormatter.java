package com.example.crud_encuesta.Estadisticas;

import android.util.Log;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class XAxisValueFormatter extends ValueFormatter {
    private String[] values;

    public XAxisValueFormatter(String[] strings) {
        this.values = strings;
    }

    @Override
    public String getFormattedValue(float value) {
        for (int i = 0; i < values.length; i++) {
            if (value == i * 2) {
                return values[i];
            }
        }
        return "";
    }
}
