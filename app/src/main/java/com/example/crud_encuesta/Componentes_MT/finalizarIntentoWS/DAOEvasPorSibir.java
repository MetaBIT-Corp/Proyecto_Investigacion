package com.example.crud_encuesta.Componentes_MT.finalizarIntentoWS;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.crud_encuesta.DatabaseAccess;

import java.util.ArrayList;
import java.util.List;

public class DAOEvasPorSibir {
    Context context;
    DatabaseAccess databaseAccess;
    SQLiteDatabase db;

    public DAOEvasPorSibir(Context context) {
        this.context = context;

        databaseAccess = DatabaseAccess.getInstance(context);
    }

    public List<EvaluacionesPorSubir> getEvaluacionesPorSubir(int materia_id){
        List<EvaluacionesPorSubir> evaluaciones = new ArrayList<>();
        Cursor cursor_intento;
        Cursor cursor_evaluacion;
        int intento_id;

        db = databaseAccess.open();

        try{
            cursor_intento = db.rawQuery("SELECT * FROM INTENTO WHERE SUBIDO=0 AND ID_CLAVE IN\n" +
                    "(SELECT ID_CLAVE FROM CLAVE WHERE ID_TURNO IN\n" +
                    "(SELECT ID_TURNO FROM TURNO WHERE ID_EVALUACION IN\n" +
                    "(SELECT ID_EVALUACION FROM EVALUACION WHERE ID_CARG_ACA IN\n" +
                    "(SELECT ID_CARG_ACA FROM CARGA_ACADEMICA WHERE ID_MAT_CI IN\n" +
                    "(SELECT ID_MAT_CI FROM MATERIA_CICLO WHERE ID_CAT_MAT = "+materia_id+")))))", null);

            while(cursor_intento.moveToNext()){
                intento_id = cursor_intento.getInt(0);

                cursor_evaluacion = db.rawQuery("SELECT NOMBRE_EVALUACION FROM EVALUACION WHERE ID_EVALUACION IN\n" +
                        "(SELECT ID_EVALUACION FROM TURNO WHERE ID_TURNO IN\n" +
                        "(SELECT ID_TURNO FROM CLAVE WHERE ID_CLAVE IN\n" +
                        "(SELECT ID_CLAVE FROM INTENTO WHERE ID_INTENTO="+intento_id+")))", null);

                cursor_evaluacion.moveToFirst();
                EvaluacionesPorSubir evaluacion = new EvaluacionesPorSubir();
                evaluacion.setIntento_id(intento_id);
                evaluacion.setNombre_evalacion(cursor_evaluacion.getString(0));

                evaluaciones.add(evaluacion);
            }
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return evaluaciones;
    }
}
