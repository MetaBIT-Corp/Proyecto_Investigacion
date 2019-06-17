package com.example.crud_encuesta.Componentes_MT.Intento;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.crud_encuesta.DatabaseAccess;
import com.example.crud_encuesta.R;

import java.util.ArrayList;
import java.util.List;

public class IntentoActivity extends AppCompatActivity {
    private ListView listView;
    Tamanio tamanio;

    //Datos de otros modelos
    private int id_clave;
    private int id_turno;
    private int id_encuestado;
    private int id_estudiante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intento);

        id_turno = getIntent().getIntExtra("id_turno_intento", 0);
        id_encuestado = getIntent().getIntExtra("id_encuesta_intento", 0);
        id_estudiante = getIntent().getIntExtra("id_estudiante", 0);

        listView = (ListView)findViewById(R.id.lsPreguntas);
        listView.setAdapter(new IntentoAdapter(getPreguntas(), id_estudiante, id_clave, id_encuestado, id_turno,this, this, tamanio));
    }

    public List<Pregunta> getPreguntas(){
        tamanio = new Tamanio();
        List<Pregunta> preguntas = new ArrayList<>();
        List<PreguntaP> preguntaPList = new ArrayList<>();
        String descripcion="";
        String pregunta;
        float ponderacion;
        int id;
        int id_gpo;
        int respuesta=0;
        int modalidad=0;
        boolean emparejamiento=false;
        int i = 1;

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        SQLiteDatabase db = databaseAccess.open();

        id_clave = IntentoConsultasDB.getClave(id_turno, db);

        String sentencia_pregunta = "SELECT ID_PREGUNTA, ID_GRUPO_EMP, PREGUNTA FROM PREGUNTA WHERE ID_PREGUNTA IN\n" +
                "(SELECT ID_PREGUNTA FROM CLAVE_AREA_PREGUNTA WHERE ID_CLAVE_AREA IN\n" +
                "(SELECT ID_CLAVE_AREA FROM CLAVE_AREA WHERE ID_CLAVE ="+id_clave+"))";

        String sentencia_opcion = "SELECT ID_OPCION, OPCION, CORRECTA FROM OPCION WHERE ID_PREGUNTA =";

        Cursor cursor_pregunta = db.rawQuery(sentencia_pregunta, null);

        while (cursor_pregunta.moveToNext()){
            List<String> opciones = new ArrayList<>();
            List<Integer> ides = new ArrayList<>();

            if(emparejamiento){
                i++;
            }else{
                preguntaPList = new ArrayList<>();
            }

            id = cursor_pregunta.getInt(0);
            pregunta = cursor_pregunta.getString(2);
            id_gpo = cursor_pregunta.getInt(1);

            modalidad = IntentoConsultasDB.getModalidad(id, db);
            ponderacion = IntentoConsultasDB.getPonderacion(id, id_clave, id_gpo, modalidad, db);
            descripcion = IntentoConsultasDB.getDescripcion(id_gpo, db);

            aumentarTamanio(modalidad);

            Cursor cursor_opcion = db.rawQuery(sentencia_opcion+id, null);
            while (cursor_opcion.moveToNext()){
                ides.add(cursor_opcion.getInt(0));
                opciones.add(cursor_opcion.getString(1));

                if(cursor_opcion.getInt(2)==1) respuesta=cursor_opcion.getInt(0);
            }

            if(modalidad==3){
                emparejamiento=true;
                preguntaPList.add(new PreguntaP(pregunta, id, respuesta, ponderacion, ides, opciones));
                if(i==IntentoConsultasDB.getCatidadPreguntasPorGrupo(id_gpo, db)){
                    preguntas.add(new Pregunta(descripcion, modalidad, preguntaPList));
                    emparejamiento=false;
                    i = 1;
                }
            }else{
                preguntaPList.add(new PreguntaP(pregunta, id, respuesta, ponderacion, ides, opciones));
                preguntas.add(new Pregunta(descripcion, modalidad, preguntaPList));
            }
            cursor_opcion.close();
        }

        cursor_pregunta.close();

        return preguntas;
    }

    public void aumentarTamanio(int modalidad){
        switch (modalidad){
            case 1:
                tamanio.setOpcion_multiple(tamanio.getOpcion_multiple()+1);
                break;
            case 2:
                tamanio.setVerdadero_falso(tamanio.getVerdadero_falso()+1);
                break;
            case 3:
                tamanio.setEmparejamiento(tamanio.getEmparejamiento()+1);
                break;
            case 4:
                tamanio.setRespuesta_corta(tamanio.getRespuesta_corta()+1);
        }
    }

}

