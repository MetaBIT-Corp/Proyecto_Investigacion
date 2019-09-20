package com.example.crud_encuesta.Componentes_MT.Intento;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crud_encuesta.Componentes_MT.finalizarIntentoWS.RespuestaWS;
import com.example.crud_encuesta.DatabaseAccess;
import com.example.crud_encuesta.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IntentoActivity extends AppCompatActivity {
    Tamanio tamanio;

    private List<RadioGroup> rg_lista = new ArrayList<>();
    private List<RadioGroup> rg_lista_vf = new ArrayList<>();
    private List<EditText> et_lista = new ArrayList<>();
    private List<Integer> idPreguntaRC = new ArrayList<>();

    //Datos de otros modelos
    private int id_clave;
    private int id_turno;
    private int id_encuesta;
    private int id_estudiante;
    int indice =0;
    boolean acceso_internet;
    List<Pregunta> preguntas = new ArrayList<>();
    List<ModalidadPregunta> modalidadPreguntas = new ArrayList<>();
    LinearLayout ll_principal;
    LinearLayout ll;
    int id_intento;
    private boolean sumIntento=false;
    private CountDownTimer countDownTimer;
    private TextView txtTimer;
    private ArrayList<ArrayList<Spinner>> preguntasSP = new ArrayList<ArrayList<Spinner>>();
    private ArrayList<ArrayList<Integer>> idsSp = new ArrayList<ArrayList<Integer>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intento);
        txtTimer = findViewById(R.id.txtTitle);


        id_turno = getIntent().getIntExtra("id_turno_intento", 0);
        id_encuesta = getIntent().getIntExtra("id_encuesta", 0);
        id_estudiante = getIntent().getIntExtra("id_estudiante", 0);
        ll_principal = findViewById(R.id.llPrincipal);

        preguntas= getPreguntas();

        id_intento = id_intento(id_estudiante);
        if(primerIntento(id_estudiante, id_turno)){
            iniciar_intento();
        }else{
            deleteRespuesta(id_intento);
            sumIntento=true;
        }

        Button finalizar = new Button(this);
        finalizar.setText("Finalizar");

        /*--------------------- Estilos ------------------------*/
        finalizar.setTextSize(20);
        finalizar.setTextColor(Color.WHITE);
        finalizar.setBackground(this.getResources().getDrawable(R.drawable.estilo_boton_intento));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 20, 0, 100);
        /*--------------------- End Estilos ------------------------*/

        //Titulo de IntentoActivity
        if(id_encuesta!=0){
            txtTimer.setText(getFechaEncuesta(id_encuesta));
        }else{
            cuentaRegresiva(getDuracionTurno(id_turno));
        }

        //final PreguntaView preguntaView = new PreguntaView(this);
        while(indice<preguntas.size()){
            PreguntaView preguntaView = new PreguntaView(this);
            ReturnView retorno = preguntaView.getVista(preguntas.get(indice));
            ModalidadPregunta modalidadPregunta = new ModalidadPregunta();

            List<Spinner> sp_lista2 = new ArrayList<>();
            List<Integer> id_lista = new ArrayList<>();

            ll = (LinearLayout) retorno.getView();

            ll.setPadding(30,30,30,30);
            ll.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ll.setBackground(this.getResources().getDrawable(R.drawable.estilo_layout_intento));

            ll_principal.addView(ll, layoutParams);

            if(preguntas.get(indice).modalidad==1){
                modalidadPregunta.setOpcion_multiple(retorno.getRadioGroupOM());
                rg_lista.add(retorno.getRadioGroupOM());
            }
            if(preguntas.get(indice).modalidad==2){
                modalidadPregunta.setVerdadero_falso(retorno.getRadioGroupVF());
                rg_lista_vf.add(retorno.getRadioGroupVF());
            }
            if(preguntas.get(indice).modalidad==3){
                sp_lista2 = retorno.getSpinner();
                id_lista = retorno.getIdPreguntaSP();
            }
            if(preguntas.get(indice).modalidad==4){
                modalidadPregunta.setRespuesta_corta(retorno.getEditText());
                et_lista.add(retorno.getEditText());
                idPreguntaRC.add(retorno.getIdPreguntaRC());
            }

            modalidadPreguntas.add(modalidadPregunta);
            preguntasSP.add((ArrayList<Spinner>) sp_lista2);
            idsSp.add((ArrayList<Integer>) id_lista);

            if(indice==preguntas.size()-1) ll_principal.addView(finalizar);
            indice++;
        }

        finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizar();
            }
        });//2358911

    }

    @Override
    public void onBackPressed(){
        finalizar();
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

        if(id_turno!=0) id_clave = IntentoConsultasDB.getClave(id_turno, db);
        if(id_encuesta!=0) id_clave = IntentoConsultasDB.getClaveEncuesta(id_encuesta, db);

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
            ponderacion = IntentoConsultasDB.getPonderacion(id, id_clave, modalidad, db);
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

    public String fecha_actual() {
        Date date = new Date();
        DateFormat fechaHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String convertido = fechaHora.format(date);

        return convertido;
    }

    public int id_intento(int id_est){
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        SQLiteDatabase db = databaseAccess.open();

        return IntentoConsultasDB.id_ultimo_intento(id_est, db);
    }

    public String rc_getOpcion(int id){
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        SQLiteDatabase db = databaseAccess.open();

        Cursor cursor = db.rawQuery("SELECT OPCION FROM OPCION WHERE ID_OPCION="+id, null);
        cursor.moveToFirst();

        return  cursor.getString(0);
    }

    public void iniciar_intento() {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        SQLiteDatabase db = databaseAccess.open();

        try{
            ContentValues registro = new ContentValues();
            registro.put("id_est", id_estudiante);
            registro.put("id_clave", id_clave);
            registro.put("id_encuestado", id_encuesta);
            registro.put("fecha_inicio_intento", fecha_actual());
            registro.put("numero_intento", IntentoConsultasDB.ultimo_intento(id_estudiante , db)+1);

            db.insert("intento", null, registro);
            Cursor cursor = db.rawQuery("SELECT ID_INTENTO FROM INTENTO ORDER BY ID_INTENTO DESC LIMIT 1", null);
            cursor.moveToFirst();

            id_intento = cursor.getInt(0);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void modelo_respuesta(List<RadioGroup> rg_seleccion, ArrayList<ArrayList<Spinner>> sp_seleccion_a, List<EditText> et_seleccion, List<RadioGroup> rg_seleccion_vf) {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        SQLiteDatabase db = databaseAccess.open();
        ContentValues registro = new ContentValues();
        int total_preguntas = 0;

        if(rg_seleccion !=null || rg_seleccion.size()>0){
            total_preguntas += rg_seleccion.size();
            for (RadioGroup rg : rg_seleccion) {
                int id_seleccion;

                if(rg.getCheckedRadioButtonId() != -1){
                    id_seleccion = rg.getCheckedRadioButtonId();
                }else{
                    id_seleccion = 0;
                }

                registro.put("id_opcion", id_seleccion);
                registro.put("id_intento", id_intento);
                registro.put("id_pregunta", rg.getId());
                db.insert("respuesta", null, registro);

                if(acceso_internet){
                    RespuestaWS respuestaWS = new RespuestaWS(this, id_seleccion, rg.getId(), id_intento, total_preguntas, "");
                }
            }
        }

        if(rg_seleccion_vf !=null || rg_seleccion_vf.size()>0){
            total_preguntas += rg_seleccion_vf.size();
            for (RadioGroup rg : rg_seleccion_vf) {
                int id_seleccion;

                if(rg.getCheckedRadioButtonId() != -1){
                    id_seleccion = rg.getCheckedRadioButtonId();
                }else{
                    id_seleccion = 0;
                }

                registro.put("id_opcion", id_seleccion);
                registro.put("id_intento", id_intento);
                registro.put("id_pregunta", rg.getId());
                db.insert("respuesta", null, registro);

                if(acceso_internet) {
                    RespuestaWS respuestaWS = new RespuestaWS(this, id_seleccion, rg.getId(), id_intento, total_preguntas, "");
                }
            }
        }

        if(sp_seleccion_a !=null || sp_seleccion_a.size()>0){
            int i=0;
            for (ArrayList<Spinner> sp_a : sp_seleccion_a) {
                //total_preguntas += sp_seleccion_a.size();

                for(Spinner sp: sp_a){
                    total_preguntas++;
                    registro.put("id_opcion", idsSp.get(i).get(sp.getSelectedItemPosition()));
                    registro.put("id_intento", id_intento);
                    registro.put("id_pregunta", sp.getId());
                    db.insert("respuesta", null, registro);

                    if(acceso_internet){
                        RespuestaWS respuestaWS = new RespuestaWS(this, idsSp.get(i).get(sp.getSelectedItemPosition()), sp.getId(), id_intento, total_preguntas, "");
                    }
                }
                    i++;
            }
        }

        if(et_seleccion !=null || et_seleccion.size()>0){
            int i =0;
            total_preguntas += et_seleccion.size();
            for (EditText et : et_seleccion) {
                registro.put("id_opcion", et.getId());
                registro.put("id_intento", id_intento);
                registro.put("id_pregunta", idPreguntaRC.get(i));
                registro.put("texto_respuesta", et.getText().toString());
                db.insert("respuesta", null, registro);

                if(acceso_internet){
                    RespuestaWS respuestaWS = new RespuestaWS(this, et.getId(), idPreguntaRC.get(i), id_intento, total_preguntas, et.getText().toString());
                }
                i++;
            }
        }

    }

    public void terminar_intento() {
        acceso_internet = accesoInternet();
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        SQLiteDatabase db = databaseAccess.open();

        ContentValues reg = new ContentValues();

        reg.put("fecha_final_intento", fecha_actual());
        reg.put("nota_intento", calcular_nota());

        if(sumIntento) reg.put("numero_intento", IntentoConsultasDB.ultimo_intento(id_estudiante , db)+1);

        if(accesoInternet()){
            reg.put("subido", 1);
            Toast.makeText(this, "La evaluación fue subida con éxito", Toast.LENGTH_SHORT).show();
        }else{
            reg.put("subido", 0);
            Toast.makeText(this, "No tienes conexión a internet para subir la evaluación, intenta nuevamente cuanto tengas conexión a internet", Toast.LENGTH_LONG).show();
            /*AlertDialog.Builder mensaje = new AlertDialog.Builder(IntentoActivity.this);
            mensaje.setTitle("Advertencia");
            //mensaje.setIcon(R.drawable.warning);
            mensaje.setMessage("No tienes conexión a internet para subir la evaluación, intenta nuevamente cuanto tengas conexión a internet");
            mensaje.setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            mensaje.show();*/
        }

        db.update("intento", reg, "id_intento=" + id_intento, null);


    }

    public double calcular_nota() {
        double nota = 0.0;
        int i = 0;

        while (i < preguntas.size()) {

            if(preguntas.get(i).modalidad==1){
                if (preguntas.get(i).preguntaPList.get(0).respuesta == modalidadPreguntas.get(i).getOpcion_multiple().getCheckedRadioButtonId()) {
                    nota += preguntas.get(i).preguntaPList.get(0).ponderacion;
                }

            }else if(preguntas.get(i).modalidad==2){
                if (preguntas.get(i).preguntaPList.get(0).respuesta == modalidadPreguntas.get(i).getVerdadero_falso().getCheckedRadioButtonId()) {
                    nota += preguntas.get(i).preguntaPList.get(0).ponderacion;
                }
            }else if(preguntas.get(i).modalidad==3){
                    for (int k=0; k<preguntasSP.get(i).size(); k++){
                        int re = preguntas.get(i).preguntaPList.get(k).respuesta;
                        int el = idsSp.get(i).get(preguntasSP.get(i).get(k).getSelectedItemPosition());
                        if(re==el){
                            nota +=preguntas.get(i).preguntaPList.get(k).ponderacion;
                        }
                    }
            }else if(preguntas.get(i).modalidad==4){
                int id_respuesta = preguntas.get(i).preguntaPList.get(0).respuesta;
                String valor_digitado = modalidadPreguntas.get(i).getRespuesta_corta().getText().toString().toLowerCase();
                String respuesta = rc_getOpcion(id_respuesta).toLowerCase();
                if(respuesta.equals(valor_digitado)){
                    nota += preguntas.get(i).preguntaPList.get(0).ponderacion;
                }
            }
            i++;

        }

        nota = new BigDecimal(nota).setScale(2, RoundingMode.HALF_EVEN).doubleValue();

        return nota;
    }

    public boolean primerIntento(int id_estudiante, int id_turno){
        boolean resultado=true;
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        SQLiteDatabase db = databaseAccess.open();
        try{
            Cursor cursor = db.rawQuery("SELECT * FROM INTENTO WHERE ID_EST="+id_estudiante+" AND ID_CLAVE IN\n" +
                    "(SELECT ID_CLAVE FROM CLAVE WHERE ID_TURNO = "+id_turno+")", null);

            if(cursor.getCount()>0){
                resultado = false;
            }else{
                resultado = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();

        return  resultado;
    }

    public void deleteRespuesta(int id_intento){
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        SQLiteDatabase db = databaseAccess.open();

        ContentValues contentValues = new ContentValues();
        contentValues.put("fecha_inicio_intento", fecha_actual());

        db.execSQL("DELETE FROM RESPUESTA WHERE ID_INTENTO="+id_intento);
        db.update("intento",contentValues, "id_intento="+id_intento, null);

        db.close();
    }

    public void cuentaRegresiva(long duracion){
        countDownTimer = new CountDownTimer(duracion, 1000) {
            @Override
            public void onTick(long l) {
                int seconds = (int) ((l / 1000) % 60);
                int minutes = (int) ((l / (60 * 1000)) % 60);
                int hours = (int) (l / (60 * 60 * 1000));


                txtTimer.setText("Tiempo restante : " +  String.format("%02d:%02d:%02d", hours, minutes, seconds));
                txtTimer.setTypeface(null, Typeface.BOLD);

                if(l<60000) txtTimer.setTextColor(Color.rgb(170, 0, 0));
            }

            @Override
            public void onFinish() {
                modelo_respuesta(rg_lista, preguntasSP, et_lista, rg_lista_vf);
                terminar_intento();

                AlertDialog.Builder nota = new AlertDialog.Builder(IntentoActivity.this);
                if(id_encuesta==0){
                    nota.setTitle("Tiempo finalizado");
                    nota.setCancelable(false);
                    nota.setMessage("Nota: " + calcular_nota());
                    nota.setPositiveButton(R.string.mt_aceptar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(IntentoActivity.this, VerIntentoActivity.class);
                            i.putExtra("id_estudiante", id_estudiante);
                            i.putExtra("nota", calcular_nota());
                            startActivity(i);
                            finish();
                        }
                    });
                    nota.show();
                }
            }
        };
        countDownTimer.start();
    }

    public long getDuracionTurno(int id_turno){
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        SQLiteDatabase db = databaseAccess.open();
        long duracion=0;

        try{
            Cursor cursor = db.rawQuery("SELECT DURACION FROM EVALUACION WHERE ID_EVALUACION IN\n" +
                    "(SELECT ID_EVALUACION FROM TURNO WHERE ID_TURNO ="+id_turno+")", null);
            if(cursor.moveToFirst()){
                duracion=cursor.getLong(0) * 60 * 1000;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return  duracion;
    }

    public void finalizar(){
        AlertDialog.Builder emergente = new AlertDialog.Builder(this);
        emergente.setTitle(R.string.mt_finalizar);
        emergente.setCancelable(false);
        if(id_encuesta==0){
            emergente.setMessage(R.string.mt_finalizar_evaluacion);
        }else{
            emergente.setMessage("¿Desea finalizar la encuesta?");
        }

        emergente.setIcon(R.drawable.infoazul);

        emergente.setPositiveButton(R.string.mt_finalizar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                terminar_intento();
                modelo_respuesta(rg_lista, preguntasSP, et_lista, rg_lista_vf);
                if(id_encuesta==0) countDownTimer.cancel();

                AlertDialog.Builder nota = new AlertDialog.Builder(IntentoActivity.this);
                if(id_encuesta==0){
                    nota.setTitle("Evaluación finalizada");
                    nota.setCancelable(false);
                    nota.setMessage("Nota: " + calcular_nota());
                    nota.setPositiveButton(R.string.mt_aceptar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(IntentoActivity.this, VerIntentoActivity.class);
                            i.putExtra("id_estudiante", id_estudiante);
                            i.putExtra("nota", calcular_nota());
                            startActivity(i);
                            finish();
                        }
                    });
                    nota.show();
                }else{
                    //nota.setTitle("Gracias por participar");
                    nota.setCancelable(false);
                    nota.setMessage("Gracias por participar, sus respuestas fueron almacenadas");
                    nota.setPositiveButton(R.string.mt_aceptar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(IntentoActivity.this, VerIntentoActivity.class);
                            i.putExtra("id_estudiante", id_estudiante);
                            i.putExtra("id_encuesta", id_encuesta);
                            startActivity(i);
                            finish();
                        }
                    });
                    nota.show();
                }
            }
        });

        emergente.setNegativeButton(R.string.mt_cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        emergente.show();
    }

    public String getFechaEncuesta(int id_encuesta){
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        SQLiteDatabase db = databaseAccess.open();
        String duracion="";

        try{
            Cursor cursor = db.rawQuery("SELECT * FROM ENCUESTA WHERE ID_ENCUESTA="+id_encuesta, null);
            if(cursor.moveToFirst()){
                duracion = "Disponibilidad: "+cursor.getString(4)+" - "+cursor.getString(5);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return duracion;
    }

    public boolean accesoInternet(){
        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");

            int val = p.waitFor();
            boolean accesible = (val == 0);
            return accesible;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}


