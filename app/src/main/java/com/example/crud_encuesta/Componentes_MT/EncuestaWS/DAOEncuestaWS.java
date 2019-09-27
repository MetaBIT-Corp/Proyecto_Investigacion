package com.example.crud_encuesta.Componentes_MT.EncuestaWS;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.crud_encuesta.DatabaseAccess;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DAOEncuestaWS {
    Context context;
    DatabaseAccess databaseAccess;
    SQLiteDatabase db;

    public DAOEncuestaWS(Context context) {
        this.context = context;

        databaseAccess = DatabaseAccess.getInstance(context);
    }

    public List<EncuestaWS> encuestasDescargadas(){
        List<EncuestaWS> encuestas_local = new ArrayList<>();
        String fecha = fecha_actual();
        db = databaseAccess.open();

        try{
            Cursor cursor = db.rawQuery("SELECT * FROM ENCUESTA WHERE FECHA_FINAL_ENCUESTA > '"+fecha+"'", null);

            while (cursor.moveToNext()){
                EncuestaWS encuesta = new EncuestaWS();

                encuesta.setId(cursor.getInt(0));
                encuesta.setId_docente(cursor.getInt(1));
                encuesta.setTitulo_encuesta(cursor.getString(2));
                encuesta.setDescriion_encuesta(cursor.getString(3));
                encuesta.setFecha_inicio_encuesta(cursor.getString(4));
                encuesta.setFecha_final_encuesta(cursor.getString(5));
                encuesta.setLocal(true);

                encuestas_local.add(encuesta);
            }

            //db.close();

        }catch (Exception  e){
            e.printStackTrace();
            //db.close();
        }
        return encuestas_local;
    }

    public String fecha_actual() {
        Date date = new Date();
        DateFormat fechaHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String convertido = fechaHora.format(date);

        return convertido;
    }

    public List<EncuestaWS> encuestasMostrar(List<EncuestaWS> encuestasWS){
        List<EncuestaWS> encuestas = new ArrayList<>();
        for (EncuestaWS ews : encuestasWS){
            Boolean agregado = false;

            for (EncuestaWS elocal : encuestasDescargadas()){
                if(elocal.id == ews.id){
                    encuestas.add(elocal);
                    agregado = true;
                }
            }
            if(!agregado) encuestas.add(ews);
        }

        return encuestas;
    }
}
