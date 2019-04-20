package com.example.crud_encuesta.Componentes_R;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.crud_encuesta.R;

import java.util.ArrayList;

public class Operaciones_CRUD {



    public Operaciones_CRUD() {
    }

    public static final Toast insertar(SQLiteDatabase db, ContentValues c, Context context, String table_name) {
        try {
            db.insert(table_name, null, c);
            return Toast.makeText(context, "Se guardo correctamente", Toast.LENGTH_SHORT);

        } catch (Exception e) {
            return Toast.makeText(context, "Error", Toast.LENGTH_SHORT);
        }
    }

    public static void eliminar(final SQLiteDatabase db, final Context context, final String table_name, String column_name,int id) {
        final String condicion = column_name + " = ?";
        final String[] argumentos = {""+id};
        db.delete(table_name, condicion, argumentos);
        Toast.makeText(context, R.string.men_guardar, Toast.LENGTH_SHORT).show();
    }

    public static final Toast actualizar(SQLiteDatabase db, ContentValues c, Context context, String table_name,String column_name,int id) {
        try {
            final String condicion = column_name + " = ?";
            final String[] argumentos = {""+id};
            db.update(table_name,c,condicion,argumentos);
            return Toast.makeText(context, R.string.men_actualizar, Toast.LENGTH_SHORT);

        } catch (Exception e) {
            return Toast.makeText(context, "Error", Toast.LENGTH_SHORT);
        }
    }



    public static ArrayList<Escuela> todos(String table_name, SQLiteDatabase db, Context ct, Escuela e) {
        ArrayList<Escuela> lista = new ArrayList<>();
        Cursor c = db.rawQuery(" SELECT * FROM " + table_name, null);
        c.moveToFirst();
        while (c.moveToNext()) {
            e = new Escuela();
            e.setId(c.getInt(0));
            e.setNombre(c.getString(1));
            lista.add(e);
        }
        return lista;
    }
    /*public static final void todos(String table_name,SQLiteDatabase db, Carrera e){

    }*/
}
