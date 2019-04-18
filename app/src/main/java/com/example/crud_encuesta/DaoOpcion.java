package com.example.crud_encuesta;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DaoOpcion {

    private SQLiteDatabase cx;
    private ArrayList<Opcion> lista_opciones=new ArrayList<>();
    private Opcion opcion;
    private Context ct;
    private String nombreBD = "proy_aplicacion.db";
    private int id_pregunta;

    public DaoOpcion(Context ct, int id_pregunta){
        this.ct = ct;
        this.id_pregunta = id_pregunta;
        DatabaseAccess dba = DatabaseAccess.getInstance(ct);
        cx = dba.open();

    }

    public boolean insertar(Opcion opcion){

        ContentValues contenedor = new ContentValues();
        contenedor.put("ID_PREGUNTA ",opcion.getId_pregunta());
        contenedor.put("OPCION",opcion.getOpcion());
        contenedor.put("CORRECTA",opcion.getCorrecta());
        return (cx.insert("OPCION",null,contenedor)>0);
    }

    public boolean eliminar(int id){

        return (cx.delete("OPCION","ID_OPCION="+id, null)>0);
    }

    public boolean editar(Opcion opcion){
        ContentValues contenedor = new ContentValues();
        contenedor.put("OPCION",opcion.getOpcion());
        contenedor.put("CORRECTA",opcion.getCorrecta());
        return (cx.update("OPCION",contenedor,"ID_OPCION="+opcion.getId(), null)>0);
    }

    public ArrayList<Opcion> verTodos(){
        lista_opciones.clear();

        try{

            Cursor cursor = cx.rawQuery("SELECT * FROM OPCION WHERE ID_PREGUNTA="+id_pregunta, null);
            cursor.moveToFirst();

            do {
                lista_opciones.add(new Opcion(cursor.getInt(cursor.getColumnIndex("ID_OPCION")), cursor.getInt(cursor.getColumnIndex("ID_PREGUNTA")),cursor.getString(cursor.getColumnIndex("OPCION")),cursor.getInt(cursor.getColumnIndex("CORRECTA"))));
            }while (cursor.moveToNext());

        }catch (Exception e){

        }

        return lista_opciones;
    }

    public Opcion verUno(int position){

        Cursor cursor = cx.rawQuery("SELECT * FROM OPCION", null);
        cursor.moveToPosition(position);
        opcion = new Opcion(cursor.getInt(cursor.getColumnIndex("ID_OPCION")), cursor.getInt(cursor.getColumnIndex("ID_PREGUNTA")),cursor.getString(cursor.getColumnIndex("OPCION")),cursor.getInt(cursor.getColumnIndex("CORRECTA")));
        return opcion;
    }
}
