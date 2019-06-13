package com.example.crud_encuesta.Estadisticas;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.crud_encuesta.DatabaseAccess;
import com.example.crud_encuesta.DatabaseOpenHelper;

public class Consultas {
    private static SQLiteDatabase db;
    private static DatabaseAccess helper;

    public static String[] parcialesMateria(int idMateria,Context context){
        helper= DatabaseAccess.getInstance(context);
        db=helper.open();
        String[] nomEva;
        Cursor c=db.rawQuery(
                "select evaluacion.id_evaluacion as Evaluacion,evaluacion.nombre_evaluacion\n" +
                        "from cat_mat_materia inner join materia_ciclo on cat_mat_materia.ID_CAT_MAT=materia_ciclo.ID_CAT_MAT\n" +
                        "inner join carga_academica on carga_academica.ID_MAT_CI=materia_ciclo.ID_MAT_CI\n" +
                        "inner join evaluacion on evaluacion.ID_CARG_ACA=carga_academica.ID_CARG_ACA\n" +
                        "inner join turno on turno.ID_EVALUACION=evaluacion.ID_EVALUACION\n" +
                        "inner join clave on clave.ID_TURNO=turno.ID_TURNO\n" +
                        "inner join intento on intento.ID_CLAVE=clave.ID_CLAVE where cat_mat_materia.ID_CAT_MAT="+idMateria+" group by Evaluacion",
                null
        );
        if (c.getCount()!=0){
            c.moveToFirst();

            nomEva=new String[c.getCount()];
            int conta=0;
            do{
               nomEva[conta]=c.getString(1);

               conta++;
            }while(c.moveToNext());
            db.close();
            return nomEva;
        }
        db.close();
        return null;
    }

    public static int aprobadosEvaluacion(String nomEva,Context context){
        helper= DatabaseAccess.getInstance(context);
        db=helper.open();
        Cursor c=db.rawQuery(
                "select  count(*) as Aprobados\n" +
                        "from (select cat_mat_materia.CODIGO_MAT as mat,intento.nota_intento as nota\n" +
                        "from cat_mat_materia inner join materia_ciclo on cat_mat_materia.ID_CAT_MAT=materia_ciclo.ID_CAT_MAT\n" +
                        "inner join carga_academica on carga_academica.ID_MAT_CI=materia_ciclo.ID_MAT_CI\n" +
                        "inner join evaluacion on evaluacion.ID_CARG_ACA=carga_academica.ID_CARG_ACA\n" +
                        "inner join turno on turno.ID_EVALUACION=evaluacion.ID_EVALUACION\n" +
                        "inner join clave on clave.ID_TURNO=turno.ID_TURNO\n" +
                        "inner join intento on intento.ID_CLAVE=clave.ID_CLAVE\n" +
                        "where evaluacion.NOMBRE_EVALUACION='"+nomEva+"')as consulta where consulta.nota>=5.95",
                null
        );

        if (c.getCount()!=0){
            db.close();
            c.moveToFirst();
            int val=c.getInt(0);
            return val;
        }
        db.close();
        return 2019;
    }

    public static int reprobadosEvaluacion(String nomEva,Context context){
        helper= DatabaseAccess.getInstance(context);
        db=helper.open();
        Cursor c=db.rawQuery(
                "select  count(*) as Reprobados\n" +
                        "from (select cat_mat_materia.CODIGO_MAT as mat,intento.nota_intento as nota\n" +
                        "from cat_mat_materia inner join materia_ciclo on cat_mat_materia.ID_CAT_MAT=materia_ciclo.ID_CAT_MAT\n" +
                        "inner join carga_academica on carga_academica.ID_MAT_CI=materia_ciclo.ID_MAT_CI\n" +
                        "inner join evaluacion on evaluacion.ID_CARG_ACA=carga_academica.ID_CARG_ACA\n" +
                        "inner join turno on turno.ID_EVALUACION=evaluacion.ID_EVALUACION\n" +
                        "inner join clave on clave.ID_TURNO=turno.ID_TURNO\n" +
                        "inner join intento on intento.ID_CLAVE=clave.ID_CLAVE\n" +
                        "where evaluacion.NOMBRE_EVALUACION='"+nomEva+"') as consulta where consulta.nota<5.95",
                null
        );

        if (c.getCount()!=0){
            db.close();
            c.moveToFirst();
            int val=c.getInt(0);
            return val;
        }
        db.close();
        return 2019;
    }
}
