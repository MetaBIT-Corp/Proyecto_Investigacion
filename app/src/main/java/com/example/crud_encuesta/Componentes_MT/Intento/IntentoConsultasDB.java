package com.example.crud_encuesta.Componentes_MT.Intento;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class IntentoConsultasDB {

    public static String getDescripcion(int id_gpo_emp, SQLiteDatabase db){

        String descripcion;
        String sentencia;

        sentencia="SELECT DESCRIPCION_GRUPO_EMP FROM GRUPO_EMPAREJAMIENTO WHERE ID_GRUPO_EMP="+id_gpo_emp;

        Cursor cursor = db.rawQuery(sentencia,null);
        cursor.moveToFirst();

        if(cursor != null){
            descripcion = cursor.getString(0);
        }else{
            descripcion="";
        }
        return descripcion;
    }

    public static int getCatidadPreguntasPorGrupo(int id_gpo_emp, SQLiteDatabase db){
        String sentencia;

        sentencia="SELECT ID_PREGUNTA FROM PREGUNTA WHERE ID_GRUPO_EMP="+id_gpo_emp;

        Cursor cursor = db.rawQuery(sentencia,null);

        return cursor.getCount();
    }

    public static float getPonderacion(int id_pregunta, int id_clave, int modalidad, SQLiteDatabase db){

        float valor_pregunta;
        String sentencia;
        int peso;
        int cantidad;

        sentencia="SELECT NUMERO_PREGUNTAS, PESO FROM CLAVE_AREA WHERE ID_CLAVE = "+id_clave+" AND ID_CLAVE_AREA IN\n" +
                "(SELECT ID_CLAVE_AREA FROM CLAVE_AREA_PREGUNTA WHERE ID_PREGUNTA="+id_pregunta+")";

        Cursor cursor = db.rawQuery(sentencia,null);
        cursor.moveToFirst();

        Cursor cursor_emp = db.rawQuery(
                "SELECT * FROM PREGUNTA WHERE ID_GRUPO_EMP IN\n" +
                        "(SELECT ID_GRUPO_EMP FROM GRUPO_EMPAREJAMIENTO WHERE ID_AREA IN\n" +
                        "(SELECT ID_AREA FROM CLAVE_AREA WHERE ID_CLAVE="+id_clave+" AND ID_CLAVE_AREA IN\n" +
                        "(SELECT ID_CLAVE_AREA FROM CLAVE_AREA_PREGUNTA WHERE ID_PREGUNTA="+id_pregunta+")))", null);

        peso = cursor.getInt(1);

        if(modalidad==3){
            cantidad = cursor_emp.getCount();
        }else{
            cantidad = cursor.getInt(0);
        }

        float peso_f = (float)peso;
        float cantidad_f = (float)cantidad;

        valor_pregunta = (peso_f/cantidad_f)/10;
        return valor_pregunta;
    }

    public static int getModalidad(int id_pregunta, SQLiteDatabase db){
        int id_modalidad;
        String sentencia;

        sentencia="SELECT ID_TIPO_ITEM FROM AREA WHERE ID_AREA IN\n" +
                "(SELECT ID_AREA FROM CLAVE_AREA WHERE ID_CLAVE_AREA IN\n" +
                "(SELECT ID_CLAVE_AREA FROM CLAVE_AREA_PREGUNTA WHERE ID_PREGUNTA="+id_pregunta+"))";

        Cursor cursor = db.rawQuery(sentencia,null);
        cursor.moveToFirst();

        id_modalidad= cursor.getInt(0);
        return id_modalidad;
    }

    public static int ultimo_intento(int id_usuario, SQLiteDatabase db) {
        int numero_intento = 0;

        Cursor cursor = db.rawQuery("SELECT NUMERO_INTENTO FROM INTENTO WHERE ID_EST="+id_usuario+" ORDER BY ID_INTENTO DESC LIMIT 1", null);
        cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            numero_intento = cursor.getInt(0);
        }

        return numero_intento;
    }

    public static int getClaveUltimoIntento(int ultimo_intento, SQLiteDatabase db){

        Cursor cursor = db.rawQuery("SELECT ID_CLAVE FROM INTENTO WHERE NUMERO_INTENTO ="+ultimo_intento, null);
        cursor.moveToFirst();

        return cursor.getInt(0);
    }

    public static int id_ultimo_intento(int id_usuario, int id_encuestado, SQLiteDatabase db) {
        int id_numero_intento = 0;

        try{
            Cursor cursor = db.rawQuery("SELECT ID_INTENTO FROM INTENTO WHERE ID_EST="+id_usuario+
                    " OR ID_ENCUESTADO ="+id_encuestado+" ORDER BY ID_INTENTO DESC LIMIT 1", null);

            cursor.moveToFirst();

            if (cursor.getCount() > 0) {
                id_numero_intento = cursor.getInt(0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return id_numero_intento;
    }

    public static int getClave(int id_turno, SQLiteDatabase db){
        List<Integer> claves = new ArrayList<>();
        int id_clave=0;

        try {
            Cursor cursor = db.rawQuery("SELECT ID_CLAVE FROM CLAVE WHERE ID_TURNO ="+id_turno, null);
            while (cursor.moveToNext()){
                claves.add(cursor.getInt(0));
            }
        } catch (Exception e) {
            Log.d("Database", "Ocurrio un error");
        }

        if(claves.size()>0){
            int generado = (int) (Math.random() * claves.size());
            id_clave = claves.get(generado);
        }

        return id_clave;
    }

    public static int getClaveEncuesta(int id_encuesta, SQLiteDatabase db){
        List<Integer> claves = new ArrayList<>();
        int id_clave=0;
        try {
            Cursor cursor = db.rawQuery("SELECT ID_CLAVE FROM CLAVE WHERE ID_ENCUESTA ="+id_encuesta, null);
            while (cursor.moveToNext()){
                claves.add(cursor.getInt(0));
            }
        } catch (Exception e) {
            Log.d("Database", "Ocurrio un error");
        }

        if(claves.size()>0){
            int generado = (int) (Math.random() * claves.size());
            id_clave = claves.get(generado);
        }

        return id_clave;
    }

}
