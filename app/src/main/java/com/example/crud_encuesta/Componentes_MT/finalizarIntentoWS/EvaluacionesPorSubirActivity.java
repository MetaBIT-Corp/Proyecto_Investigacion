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
    DAOEvasPorSibir daoEvasPorSibir;
    ListView listView;
    int materia_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluaciones_por_subir);

        daoEvasPorSibir = new DAOEvasPorSibir(this);
        materia_id = getIntent().getIntExtra("materia_id", 0);

        if(materia_id==0 || materia_id==-1){
            Toast.makeText(this, "No tiene evaluaciones pendiente por subir", Toast.LENGTH_SHORT).show();
            finish();
        }

        evaluaciones = daoEvasPorSibir.getEvaluacionesPorSubir(materia_id);
        listView = findViewById(R.id.ls_evas_por_subir);

        evaAdapter = new EvaluacionesPorSubirAdapter(this, evaluaciones, daoEvasPorSibir, materia_id);
        listView.setAdapter(evaAdapter);
    }

}
