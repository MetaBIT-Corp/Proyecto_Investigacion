package com.example.crud_encuesta.Componentes_MT.finalizarIntentoWS;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.crud_encuesta.DatabaseAccess;
import com.example.crud_encuesta.R;

import java.util.ArrayList;
import java.util.List;

public class EvaluacionesPorSubirActivity extends AppCompatActivity {
    List<EvaluacionesPorSubir> evaluaciones = new ArrayList<>();
    EvaluacionesPorSubirAdapter evaAdapter;
    ListView listView;
    int materia_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluaciones_por_subir);

        materia_id = getIntent().getIntExtra("materia_id", 0);

        if(materia_id==0 || materia_id==-1){
            Toast.makeText(this, "No tiene evaluaciones pendiente por subir", Toast.LENGTH_SHORT).show();
            finish();
        }

        evaluaciones = getEvaluacionesPorSubir(materia_id);
        listView = findViewById(R.id.ls_evas_por_subir);

        evaAdapter = new EvaluacionesPorSubirAdapter(this, evaluaciones);
        listView.setAdapter(evaAdapter);
    }

    public List<EvaluacionesPorSubir> getEvaluacionesPorSubir(int materia_id){
        List<EvaluacionesPorSubir> evaluaciones = new ArrayList<>();
        Cursor cursor_intento;
        Cursor cursor_evaluacion;
        int intento_id;

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        SQLiteDatabase db = databaseAccess.open();

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
