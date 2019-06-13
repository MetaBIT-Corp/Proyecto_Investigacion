package com.example.crud_encuesta.Componentes_MT.Excel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.crud_encuesta.Componentes_DC.Objetos.Opcion;
import com.example.crud_encuesta.DatabaseAccess;

public class DAOExcel {
    Context context;
    DatabaseAccess databaseAccess;
    SQLiteDatabase db;

    public DAOExcel(Context context) {
        this.context = context;

        databaseAccess = DatabaseAccess.getInstance(context);
    }

    public int insertGPOEMP(int id_area, String descripcion){
        int id_emp=0;

        db = databaseAccess.open();
        ContentValues registro = new ContentValues();

        registro.put("id_area", id_area);
        registro.put("descripcion_grupo_emp", descripcion);

        db.insert("grupo_emparejamiento", null, registro);

        Cursor cursor = db.rawQuery("SELECT ID_GRUPO_EMP FROM GRUPO_EMPAREJAMIENTO ORDER BY ID_GRUPO_EMP DESC LIMIT 1", null);
         if(cursor.moveToFirst()){
             id_emp = cursor.getInt(0);
         }

        db.close();
        return id_emp;
    }

    public int insertPregunta(int id_grupo_emp, String pregunta){
        int id_pregunta=0;

        db = databaseAccess.open();
        ContentValues registro = new ContentValues();

        registro.put("id_grupo_emp", id_grupo_emp);
        registro.put("pregunta", pregunta);

        db.insert("PREGUNTA", null, registro);

        Cursor cursor = db.rawQuery("SELECT ID_PREGUNTA FROM PREGUNTA ORDER BY ID_GRUPO_EMP DESC LIMIT 1", null);
        if(cursor.moveToFirst()){
            id_pregunta = cursor.getInt(0);
        }

        db.close();
        return id_pregunta;
    }

    public int insertOpcion(Opcion opcion){
        int cantidad=0;
        db = databaseAccess.open();
        ContentValues registro = new ContentValues();

        registro.put("id_pregunta", opcion.getId_pregunta());
        registro.put("opcion", opcion.getOpcion());
        registro.put("correcta", opcion.getCorrecta());

        cantidad = (int)db.insert("OPCION", null, registro);
        db.close();

        return cantidad;
    }

}
