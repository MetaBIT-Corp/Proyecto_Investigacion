package com.example.crud_encuesta.Componentes_MR.Estudiante;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.crud_encuesta.Componentes_AP.Models.Usuario;
import com.example.crud_encuesta.DatabaseAccess;
import java.util.ArrayList;

public class DAOEstudiante {

    private SQLiteDatabase cx;
    private ArrayList<Estudiante> lista = new ArrayList<>();
    private Estudiante estudiante;
    private Usuario usuario;
    private Context ct;

    public DAOEstudiante(Context ct){
        this.ct = ct;
        DatabaseAccess dba = DatabaseAccess.getInstance(ct);
        cx = dba.open();
    }

    public boolean insertarUsuario(Usuario usuario){
        ContentValues contenedor = new ContentValues();
        contenedor.put("NOMUSUARIO",usuario.getNOMUSUARIO());
        contenedor.put("CLAVE",usuario.getCLAVE());
        contenedor.put("ROL",usuario.getROL());
        return (cx.insert("USUARIO", null,contenedor))>0;
    }

    public boolean insertar(Estudiante estd){
        ContentValues contenedor = new ContentValues();
        contenedor.put("CARNET", estd.getCarnet());
        contenedor.put("NOMBRE", estd.getNombre());
        contenedor.put("ACTIVO", estd.getActivo());
        contenedor.put("ANIO_INGRESO", estd.getAnio_ingreso());
        contenedor.put("IDUSUARIO",estd.getId_usuario());
        return (cx.insert("ESTUDIANTE", null, contenedor))>0;
    }

    public boolean eliminar(int id){
        return (cx.delete("ESTUDIANTE", "ID_EST="+id, null))>0;
    }

    public boolean editarUsuario(Usuario usuario){
        ContentValues contenedor = new ContentValues();
        contenedor.put("IDUSUARIO",usuario.getIDUSUARIO());
        contenedor.put("NOMUSUARIO",usuario.getNOMUSUARIO());
        contenedor.put("CLAVE",usuario.getCLAVE());
        contenedor.put("ROL",usuario.getROL());
        return (cx.update("USUARIO", contenedor, "IDUSUARIO="+usuario.getIDUSUARIO(),null))>0;
    }

    public boolean editar(Estudiante estd){
        ContentValues contenedor = new ContentValues();
        contenedor.put("ID_EST", estd.getId());
        contenedor.put("CARNET", estd.getCarnet());
        contenedor.put("NOMBRE", estd.getNombre());
        contenedor.put("ACTIVO", estd.getActivo());
        contenedor.put("ANIO_INGRESO", estd.getAnio_ingreso());
        return (cx.update("ESTUDIANTE", contenedor, "ID_EST="+estd.getId(), null))>0;
    }

    public ArrayList<Estudiante> verTodos(){
        lista.clear();
        Cursor cursor = cx.rawQuery("SELECT * FROM ESTUDIANTE ORDER BY CARNET COLLATE NOCASE ASC ",null);

        if(cursor != null && cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                lista.add(new Estudiante(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getInt(5)));
            }while (cursor.moveToNext());
        }
        return lista;
    }

    public ArrayList<Estudiante> verBusqueda(String parametro){
        lista.clear();
        Cursor cursor = cx.rawQuery("SELECT * FROM ESTUDIANTE WHERE NOMBRE LIKE '%"+parametro+"%' OR CARNET LIKE'%"+parametro+"%'",null);
        if (cursor != null && cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                lista.add(new Estudiante(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getInt(5)));
            }while(cursor.moveToNext());
        }
        return lista;
    }

    public ArrayList<Estudiante> verBusquedaIndex(String parametro){
        lista.clear();
        Cursor cursor = cx.rawQuery("SELECT * FROM ESTUDIANTE WHERE CARNET LIKE '"+parametro+"%'",null);
        if (cursor != null && cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                lista.add(new Estudiante(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getInt(5)));
            }while(cursor.moveToNext());
        }
        return lista;
    }

    public Estudiante verUno(int id){
        Cursor cursor = cx.rawQuery("SELECT * FROM ESTUDIANTE", null);
        cursor.moveToPosition(id);
        estudiante = new Estudiante(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3),
                cursor.getString(4),
                cursor.getInt(5));
        return estudiante;
    }

    public Usuario usuarioNombre(String nombre){
        Cursor cursor = cx.rawQuery("SELECT * FROM USUARIO WHERE NOMUSUARIO = '" +nombre+"'", null);
        cursor.moveToPosition(0);
        usuario = new Usuario(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3));
        return usuario;
    }
}