package com.example.crud_encuesta.Componentes_AP.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.crud_encuesta.Componentes_AP.Models.Evaluacion;
import com.example.crud_encuesta.DatabaseAccess;

import java.util.ArrayList;

public class DAOEvaluacion {

    SQLiteDatabase baseDeDatos;
    ArrayList<Evaluacion> evaluaciones = new ArrayList<>();
    Evaluacion evaluacion;
    Context contexto;
    DatabaseAccess dba;
    String nombreBD= "proy_aplicacion";

    //constructor
    public DAOEvaluacion(Context context){
        this.contexto = context;

        this.dba = DatabaseAccess.getInstance(context);
        baseDeDatos = this.dba.open();

        /*
         *Abrir base de datos
         * DatabaseAccess dba = DatabaseAccess.getInstance(context);
         * baseDeDatos = dba.open();
         *
         * */
    }


    public Boolean Insertar(Evaluacion evaluacion){
        baseDeDatos = this.dba.open();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID_CARG_ACA", evaluacion.getIdCargaAcad() );
        contentValues.put("DURACION",evaluacion.getDuracion());
        contentValues.put("INTENTO",evaluacion.getCantIntento());
        contentValues.put("NOMBRE_EVALUACION",evaluacion.getNombre());
        contentValues.put("DESCRIPCION_EVALUACION",evaluacion.getDescripcion());

        return (baseDeDatos.insert("EVALUACION",null,contentValues)>0);
    }

    public Boolean InsertarWS(Evaluacion evaluacion){
        baseDeDatos = this.dba.open();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID_EVALUACION", evaluacion.getId() );
        contentValues.put("ID_CARG_ACA", evaluacion.getIdCargaAcad() );
        contentValues.put("DURACION",evaluacion.getDuracion());
        contentValues.put("INTENTO",evaluacion.getCantIntento());
        contentValues.put("NOMBRE_EVALUACION",evaluacion.getNombre());
        contentValues.put("DESCRIPCION_EVALUACION",evaluacion.getDescripcion());

        return (baseDeDatos.insert("EVALUACION",null,contentValues)>0);
    }

    public Boolean Eliminar(Integer id){
        baseDeDatos = this.dba.open();
        return (baseDeDatos.delete("EVALUACION","ID_EVALUACION="+id,null)>0);
    }

    public Boolean EliminarSegunCarga(Integer id_carga_academica){
        baseDeDatos = this.dba.open();
        return (baseDeDatos.delete("EVALUACION","ID_CARG_ACA="+id_carga_academica,null)>0);
    }

    public Boolean Editar(Evaluacion evaluacion){
        baseDeDatos = this.dba.open();
        ContentValues contentValues = new ContentValues();

        contentValues.put("DURACION",evaluacion.getDuracion());
        contentValues.put("INTENTO",evaluacion.getCantIntento());
        contentValues.put("NOMBRE_EVALUACION",evaluacion.getNombre());
        contentValues.put("DESCRIPCION_EVALUACION",evaluacion.getDescripcion());

        return (baseDeDatos.update("EVALUACION",contentValues,"ID_EVALUACION =" + evaluacion.getId(),null)>0);
    }

    public ArrayList<Evaluacion> verTodos(int id_carga_academica){
        baseDeDatos = this.dba.open();
        evaluaciones.clear(); //limpiamos lista del adapter
        Cursor cursor  = baseDeDatos.rawQuery(
                "Select * FROM EVALUACION WHERE ID_CARG_ACA ="+id_carga_academica,
                null);
        if(cursor.moveToFirst()){
            cursor.moveToFirst();
            do{
                evaluaciones.add(new Evaluacion(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getString(5)
                ));

            }while (cursor.moveToNext());
        }
        return evaluaciones;
    }

    //retorna la lista pero solo con el elemento buscado
    public ArrayList<Evaluacion> verUno(String nombre, int id_carga_academica){
        baseDeDatos = this.dba.open();
        evaluaciones.clear();
        Cursor cursor  = baseDeDatos.rawQuery(
                "Select * FROM EVALUACION WHERE NOMBRE_EVALUACION LIKE '%" + nombre + "%' AND ID_CARG_ACA="+id_carga_academica  ,
                null);
        if(cursor.moveToFirst()){
            cursor.moveToFirst();
            do {
                evaluaciones.add(new Evaluacion(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getString(5)
                ));
            }while (cursor.moveToNext());
        }
        return evaluaciones;
    }

    public int getCargaAcademicaEstudiante(int id_usuario, int id_materia){
        baseDeDatos = this.dba.open();
        int carga=0;
        Cursor cursor  = baseDeDatos.rawQuery(
                "SELECT\n" +
                        "CARGA_ACADEMICA.ID_CARG_ACA,\n" +
                        "CAT_MAT_MATERIA.NOMBRE_MAR\n" +
                        "FROM ESTUDIANTE\n" +
                        "INNER JOIN DETALLEINSCEST ON\n" +
                        "ESTUDIANTE.ID_EST=DETALLEINSCEST.ID_EST\n" +
                        "INNER JOIN CARGA_ACADEMICA ON\n" +
                        "DETALLEINSCEST.ID_CARG_ACA=CARGA_ACADEMICA.ID_CARG_ACA\n" +
                        "INNER JOIN MATERIA_CICLO ON\n" +
                        "MATERIA_CICLO.ID_MAT_CI=CARGA_ACADEMICA.ID_MAT_CI\n" +
                        "INNER JOIN CAT_MAT_MATERIA ON\n" +
                        "CAT_MAT_MATERIA.ID_CAT_MAT=MATERIA_CICLO.ID_CAT_MAT\n" +
                        "WHERE ESTUDIANTE.IDUSUARIO=" + id_usuario+
                        " AND CAT_MAT_MATERIA.ID_CAT_MAT =" + id_materia,
                null);
        if(cursor.moveToFirst()){
            cursor.moveToFirst();
            do {
                carga = cursor.getInt(0);
            }while (cursor.moveToNext());
        }
        return carga;
    }

    public int getIdEstudiante(int id_usuario, int id_materia){
        baseDeDatos = this.dba.open();
        int id=0;
        Cursor cursor  = baseDeDatos.rawQuery(
                "SELECT\n" +
                        "ESTUDIANTE.ID_EST\n" +
                        "FROM ESTUDIANTE\n" +
                        "INNER JOIN DETALLEINSCEST ON\n" +
                        "ESTUDIANTE.ID_EST=DETALLEINSCEST.ID_EST\n" +
                        "INNER JOIN CARGA_ACADEMICA ON\n" +
                        "DETALLEINSCEST.ID_CARG_ACA=CARGA_ACADEMICA.ID_CARG_ACA\n" +
                        "INNER JOIN MATERIA_CICLO ON\n" +
                        "MATERIA_CICLO.ID_MAT_CI=CARGA_ACADEMICA.ID_MAT_CI\n" +
                        "INNER JOIN CAT_MAT_MATERIA ON\n" +
                        "CAT_MAT_MATERIA.ID_CAT_MAT=MATERIA_CICLO.ID_CAT_MAT\n" +
                        "WHERE ESTUDIANTE.IDUSUARIO=" + id_usuario+
                        " AND CAT_MAT_MATERIA.ID_CAT_MAT =" + id_materia,
                null);
        if(cursor.moveToFirst()){
            cursor.moveToFirst();
            do {
                id = cursor.getInt(0);
            }while (cursor.moveToNext());
        }
        return id;
    }

    public int getCargaAcademicaDocente(int id_usuario, int id_materia){
        baseDeDatos = this.dba.open();
        int carga=0;
        Cursor cursor  = baseDeDatos.rawQuery(
                "SELECT\n" +
                        "CARGA_ACADEMICA.ID_CARG_ACA,\n" +
                        "CAT_MAT_MATERIA.NOMBRE_MAR\n" +
                        "FROM\n" +
                        "PDG_DCN_DOCENTE\n" +
                        "INNER JOIN\n" +
                        "CARGA_ACADEMICA ON\n" +
                        "PDG_DCN_DOCENTE.ID_PDG_DCN=CARGA_ACADEMICA.ID_PDG_DCN\n" +
                        "INNER JOIN\n" +
                        "MATERIA_CICLO ON\n" +
                        "CARGA_ACADEMICA.ID_MAT_CI=MATERIA_CICLO.ID_MAT_CI\n" +
                        "INNER JOIN\n" +
                        "CAT_MAT_MATERIA ON\n" +
                        "MATERIA_CICLO.ID_CAT_MAT=CAT_MAT_MATERIA.ID_CAT_MAT\n" +
                        "WHERE PDG_DCN_DOCENTE.IDUSUARIO=" +id_usuario+
                        " AND CAT_MAT_MATERIA.ID_CAT_MAT= " + id_materia,
                null);
        if(cursor.moveToFirst()){
            cursor.moveToFirst();
            do {
                carga = cursor.getInt(0);
            }while (cursor.moveToNext());
        }
        return carga;
    }


}
