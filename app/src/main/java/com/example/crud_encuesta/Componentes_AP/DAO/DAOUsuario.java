package com.example.crud_encuesta.Componentes_AP.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.crud_encuesta.Componentes_AP.Models.Turno;
import com.example.crud_encuesta.Componentes_AP.Models.Usuario;
import com.example.crud_encuesta.Componentes_MR.Estudiante.Estudiante;
import com.example.crud_encuesta.DatabaseAccess;

import java.util.ArrayList;

public class DAOUsuario {
    SQLiteDatabase baseDeDatos;
    Usuario usuario;
    Context contexto;
    DatabaseAccess dba;
    String nombreBD= "proy_aplicacion";

    //constructor
    public DAOUsuario(Context context){
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




    //retorna true si se realiza el login con éxito
    public Boolean loginUsuario(String pass, String user){
        baseDeDatos = this.dba.open();
        provisional();
        Boolean login = false;
        int id_usuario;
        int id_sesion;
        Cursor cursor = baseDeDatos.rawQuery(
                "Select * FROM USUARIO WHERE NOMUSUARIO= '" + user + "' AND CLAVE = '" +pass+"'",
                null
        );
        if(cursor.moveToFirst()){
           id_usuario = cursor.getInt(0);
           Cursor cursorSesion = baseDeDatos.rawQuery(
                   "Select * FROM SESIONUSUARIO WHERE IDUSUARIO = "+ id_usuario,
                   null
           );
           if (cursorSesion.moveToFirst()){
               id_sesion = cursorSesion.getInt(0);
               ContentValues contentValues = new ContentValues();
               contentValues.put("INSESION", 1);
               baseDeDatos.update("SESIONUSUARIO",contentValues,"IDSESION =" + id_sesion,null);
               login = true;
           }else{
               ContentValues contentValues = new ContentValues();
               contentValues.put("INSESION", 1);
               contentValues.put("IDUSUARIO",id_usuario);
               baseDeDatos.insert("SESIONUSUARIO",null,contentValues);
               login = true;
           }
        }else {
            /*Toast.makeText(contexto,
                    "El usuario no existe",
                    Toast.LENGTH_SHORT).show();*/
        }
        return login;
    }

    public Boolean logoutUsuario(int id_user){
        baseDeDatos = this.dba.open();
        Boolean logout = false;
        int id_usuario;
        int id_sesion;
        Cursor cursor = baseDeDatos.rawQuery(
                "Select * FROM USUARIO WHERE IDUSUARIO = " + id_user,
                null
        );
        if(cursor.moveToFirst()){
            id_usuario = cursor.getInt(0);
            Cursor cursorSesion = baseDeDatos.rawQuery(
                    "Select * FROM SESIONUSUARIO WHERE IDUSUARIO = "+ id_usuario,
                    null
            );
            if (cursorSesion.moveToFirst()){
                id_sesion = cursorSesion.getInt(0);
                ContentValues contentValues = new ContentValues();
                contentValues.put("INSESION", 0);
                baseDeDatos.update("SESIONUSUARIO",contentValues,"IDSESION =" + id_sesion,null);
                logout = true;
            }
        }else {
            /*
            Toast.makeText(contexto,
                    "Id de usuario no valido",
                    Toast.LENGTH_SHORT).show();*/
        }
        return logout;
    }

    //provisional, este metodo lo que hace es verificar que
    //si hay un estudiante en sesion, que lo saque, antes de hacer el login
    public void provisional(){
        baseDeDatos = this.dba.open();
        Cursor cursor = baseDeDatos.rawQuery(
                "Select * FROM USUARIO",
                null
        );
        if(cursor.moveToFirst()){
            cursor.moveToFirst();
            do{
                logoutUsuario(cursor.getInt(0));
            }while (cursor.moveToNext());
        }
    }

    public Usuario getUsuarioLogueado(){
        baseDeDatos = this.dba.open();
        Usuario usuario = new Usuario();

        Cursor cursorSesion = baseDeDatos.rawQuery(
                "Select * FROM SESIONUSUARIO WHERE INSESION = "+ 1,
                null
        );
        if (cursorSesion.moveToFirst()){
            Cursor cursorUsuario = baseDeDatos.rawQuery(
                    "Select * FROM USUARIO WHERE IDUSUARIO = "+ cursorSesion.getInt(2),
                    null
            );
            if (cursorUsuario.moveToFirst()){
                usuario.setIDUSUARIO(cursorUsuario.getInt(0));
                usuario.setNOMUSUARIO(cursorUsuario.getString(1));
                usuario.setCLAVE(cursorUsuario.getString(2));
                usuario.setROL(cursorUsuario.getInt(3));
                /*
                Toast.makeText(
                        contexto,
                        "Usuario: " +usuario.getNOMUSUARIO()+" Rol: " + usuario.getROL(),
                        Toast.LENGTH_SHORT
                ).show();
                */
            }else {
                usuario = null;
            }

        }else {
            usuario= null;
        }
        return usuario;
    }

    public Boolean Insertar(Usuario usuario){
        baseDeDatos = this.dba.open();
        ContentValues contentValues = new ContentValues();
        contentValues.put("IDUSUARIO", usuario.getIDUSUARIO() );
        contentValues.put("ROL",usuario.getROL());
        contentValues.put("NOMUSUARIO",usuario.getNOMUSUARIO());
        contentValues.put("CLAVE",usuario.getCLAVE());

        return (baseDeDatos.insert("USUARIO",null,contentValues)>0);
    }
    public Boolean DeleteUserAll(){
        baseDeDatos = this.dba.open();
        return (baseDeDatos.delete("USUARIO",null,null)>0);
    }

    public Boolean DeleteEstudianteAll(){
        baseDeDatos = this.dba.open();
        return (baseDeDatos.delete("ESTUDIANTE  ",null,null)>0);
    }

    public Boolean DeleteSesionAll(){
        baseDeDatos = this.dba.open();
        return (baseDeDatos.delete("SESIONUSUARIO",null,null)>0);
    }
    public Boolean DeleteMateriasUser(){
        baseDeDatos = this.dba.open();
        return (baseDeDatos.delete("CAT_MAT_MATERIA",null,null)>0);
    }

    public boolean insertar(Estudiante estd){
        ContentValues contenedor = new ContentValues();
        contenedor.put("ID_EST", estd.getId());
        contenedor.put("CARNET", estd.getCarnet());
        contenedor.put("NOMBRE", estd.getNombre());
        contenedor.put("ACTIVO", estd.getActivo());
        contenedor.put("ANIO_INGRESO", estd.getAnio_ingreso());
        contenedor.put("IDUSUARIO",estd.getId_usuario());
        return (baseDeDatos.insert("ESTUDIANTE", null, contenedor))>0;
    }
}
