package com.example.crud_encuesta.Componentes_MT.Intento;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.crud_encuesta.DatabaseAccess;
import com.example.crud_encuesta.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IntentoAdapter extends BaseAdapter implements AdapterView.OnItemSelectedListener {
    private LayoutInflater inflater;
    private Context context;
    private Activity activity;
    private Tamanio tamanio= new Tamanio();
    private List<Pregunta> preguntas = new ArrayList<>();
    private List<Integer> idPreguntaRC = new ArrayList<>();

    private List<RadioGroup> rg_lista = new ArrayList<>();
    private List<RadioButton> rb_lista = new ArrayList<>();

    private List<RadioGroup> rg_lista_vf = new ArrayList<>();
    private List<RadioGroup> rg_posicion_lista_vf = new ArrayList<>();

    private List<Spinner> sp_lista = new ArrayList<>();
    private List<Integer> idesGPO = new ArrayList<>();

    private List<EditText> et_lista = new ArrayList<>();
    private List<EditText> et_posicion_lista = new ArrayList<>();

    private int id_intento;
    private boolean sumIntento=false;

    //Datos de otros modelos
    int id_estudiante;
    int id_clave;
    int id_encuestado;
    int id_turno;

    public IntentoAdapter(List<Pregunta> preguntas, int id_estudiante, int id_clave, int id_encuestado, int id_turno, Context context, Activity activity, Tamanio tamanio) {
        this.preguntas = preguntas;
        this.id_estudiante = id_estudiante;
        this.id_clave = id_clave;
        this. id_encuestado = id_encuestado;
        this.id_turno = id_turno;
        this.context = context;
        this.activity = activity;
        this.tamanio = tamanio;

        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        id_intento = id_intento(id_estudiante);
        if(primerIntento(id_estudiante, id_turno)){
            iniciar_intento();
        }else{
            deleteRespuesta(id_intento);
            sumIntento=true;
        }
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        final View mView = inflater.inflate(R.layout.elemento_list_pregunta, null);
        TextView txt_pregrunta = mView.findViewById(R.id.txtPregunta);
        LinearLayout ll_pregunta = mView.findViewById(R.id.llPregunta);
        Button finalizar = new Button(context);
        finalizar.setText(R.string.mt_finalizar);

        switch (preguntas.get(position).modalidad) {
            case 1:
                txt_pregrunta.setText(preguntas.get(position).preguntaPList.get(0).pregunta);

                if(tamanio.getOpcion_multiple()>rg_lista.size()){
                    RadioGroup rg_pregunta = new RadioGroup(context);
                    rg_pregunta.setId(preguntas.get(position).preguntaPList.get(0).id);
                    for (int i = 0; i < preguntas.get(position).preguntaPList.get(0).opciones.size(); i++) {
                        RadioButton rb_pregunta = new RadioButton(context);
                        rb_pregunta.setText( preguntas.get(position).preguntaPList.get(0).opciones.get(i));
                        rb_pregunta.setId( preguntas.get(position).preguntaPList.get(0).ides.get(i));
                        rb_lista.add(rb_pregunta);
                        rg_pregunta.addView(rb_pregunta);
                    }

                    rg_lista.add(rg_pregunta);
                    ll_pregunta.addView(rg_pregunta);

                    et_posicion_lista.add(null);
                    rg_posicion_lista_vf.add(null);
                }else {
                    RadioGroup rg_pregunta = rg_lista.get(position);
                    if(rg_pregunta.getParent() != null) {
                        ((ViewGroup)rg_pregunta.getParent()).removeView(rg_pregunta);
                    }
                    ll_pregunta.addView(rg_pregunta);
                }
                break;

            case 2:
                txt_pregrunta.setText(preguntas.get(position).preguntaPList.get(0).pregunta);

                if(tamanio.getVerdadero_falso()>rg_lista_vf.size()){
                    RadioGroup rg_pregunta = new RadioGroup(context);
                    rg_pregunta.setId(preguntas.get(position).preguntaPList.get(0).id);
                    for (int i = 0; i < preguntas.get(position).preguntaPList.get(0).opciones.size(); i++) {
                        RadioButton rb_pregunta = new RadioButton(context);
                        rb_pregunta.setText( preguntas.get(position).preguntaPList.get(0).opciones.get(i));
                        rb_pregunta.setId( preguntas.get(position).preguntaPList.get(0).ides.get(i));
                        rg_pregunta.addView(rb_pregunta);
                    }

                    rg_lista_vf.add(rg_pregunta);
                    rg_posicion_lista_vf.add(rg_pregunta);
                    ll_pregunta.addView(rg_pregunta);

                    et_posicion_lista.add(null);
                }else {
                    RadioGroup rg_pregunta = rg_posicion_lista_vf.get(position);
                    if(rg_pregunta.getParent() != null) {
                        ((ViewGroup)rg_pregunta.getParent()).removeView(rg_pregunta);
                    }
                    ll_pregunta.addView(rg_pregunta);
                }
                break;

            case 3:
                txt_pregrunta.setText(preguntas.get(position).descripcion);

                if(tamanio.getEmparejamiento()>sp_lista.size()){
                    ArrayAdapter<String> comboAdapter;
                    List<String> opcionesGPO = new ArrayList<>();

                    for(int i=0; i<preguntas.get(position).preguntaPList.size();i++){
                        for(int j=0; j<preguntas.get(position).preguntaPList.get(i).opciones.size(); j++){
                            opcionesGPO.add(preguntas.get(position).preguntaPList.get(i).opciones.get(j));
                            idesGPO.add(preguntas.get(position).preguntaPList.get(i).ides.get(j));
                        }
                    }

                    for(PreguntaP p : preguntas.get(position).preguntaPList){
                        TextView txt = new TextView(context);
                        Spinner spGPO = new Spinner(context);
                        //spGPO.setId(p.respuesta);
                        spGPO.setId(p.id);

                        spGPO.setOnItemSelectedListener(this);

                        txt.setTextSize(15);
                        txt.setTextColor(Color.BLACK);
                        txt.setText(p.pregunta);
                        txt.setPadding(0,40,0,40);

                        comboAdapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item, opcionesGPO);
                        spGPO.setAdapter(comboAdapter);

                        sp_lista.add(spGPO);

                        ll_pregunta.addView(txt);
                        ll_pregunta.addView(spGPO);
                    }

                    et_posicion_lista.add(null);
                    rg_posicion_lista_vf.add(null);
                }else{
                    for (int i =0; i<sp_lista.size(); i++) {
                        TextView txt = new TextView(context);
                        Spinner spGPO = sp_lista.get(i);

                        txt.setTextSize(15);
                        txt.setTextColor(Color.BLACK);
                        txt.setText(preguntas.get(position).preguntaPList.get(i).pregunta);
                        txt.setPadding(0,40,0,40);

                        if(txt.getParent() != null) {
                            ((ViewGroup)txt.getParent()).removeView(txt);
                        }

                        ll_pregunta.addView(txt);

                        if(spGPO.getParent() != null) {
                            ((ViewGroup)spGPO.getParent()).removeView(spGPO);
                        }
                        ll_pregunta.addView(spGPO);
                    }
                }
                break;

            case 4:
                txt_pregrunta.setText(preguntas.get(position).preguntaPList.get(0).pregunta);
                txt_pregrunta.setPadding(0,0,0,20);

                idPreguntaRC.add(preguntas.get(position).preguntaPList.get(0).id);

                if(tamanio.getRespuesta_corta()>et_lista.size()){
                    EditText et_respuesta = new EditText(context);
                    et_respuesta.setId(preguntas.get(position).preguntaPList.get(0).ides.get(0));
                    et_respuesta.setHint("Responda con una palabra");
                    et_respuesta.setInputType(InputType.TYPE_CLASS_TEXT);

                    et_lista.add(et_respuesta);
                    et_posicion_lista.add(et_respuesta);

                    ll_pregunta.addView(et_respuesta);
                    rg_posicion_lista_vf.add(null);
                }else{
                    EditText et_respuesta = et_posicion_lista.get(position);
                    et_respuesta.setHint("Responda con una palabra");
                    et_respuesta.setInputType(5);
                    if(et_respuesta.getParent() != null) {
                        ((ViewGroup)et_respuesta.getParent()).removeView(et_respuesta);
                    }
                    ll_pregunta.addView(et_respuesta);
                }

                break;

        }

        if (position == getCount() - 1) {
            ll_pregunta.addView(finalizar);
        }


        /*mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Toast.makeText(context, "Tamaño"+String.valueOf(rb_lista.size()), Toast.LENGTH_SHORT).show();
                //Toast.makeText(context, "Tamaño"+String.valueOf(rg_lista.size()), Toast.LENGTH_SHORT).show();
                Log.d("Hey", "Aqui 1");
                for(int i=0; i<3; i++){
                    Log.d("Hey", "Aqui 2");
                    for (int j=0; j<12; j++) {
                        Log.d("Hey", "Aqui 3");
                        if(rb_lista.get(j).getId()==rg_lista.get(i).getCheckedRadioButtonId()){
                            Log.d("Hey", "Aqui 4");
                            rb_lista.get(j).setChecked(true);
                    }
                    }
                }

                return false;
            }
        });*/
        finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder emergente = new AlertDialog.Builder(context);
                emergente.setTitle(R.string.mt_finalizar);
                emergente.setCancelable(false);
                emergente.setMessage(R.string.mt_finalizar_evaluacion);
                emergente.setIcon(R.drawable.infoazul);

                emergente.setPositiveButton(R.string.mt_finalizar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        modelo_respuesta(rg_lista, sp_lista, et_lista, rg_lista_vf);
                        terminar_intento();

                        AlertDialog.Builder nota = new AlertDialog.Builder(context);
                        nota.setTitle("Evaluación finalizada");
                        nota.setCancelable(false);
                        nota.setMessage("Nota: " + calcular_nota());
                        nota.setPositiveButton(R.string.mt_aceptar, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(context, VerIntentoActivity.class);
                                i.putExtra("id_estudiante", id_estudiante);
                                i.putExtra("nota", calcular_nota());
                                context.startActivity(i);
                                activity.finish();
                            }
                        });
                        nota.show();
                    }
                });

                emergente.setNegativeButton(R.string.mt_cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                emergente.show();

            }
        });

        return mView;
    }

    @Override
    public int getCount() {
        return preguntas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void iniciar_intento() {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        SQLiteDatabase db = databaseAccess.open();

        ContentValues registro = new ContentValues();
        registro.put("id_est", id_estudiante);
        registro.put("id_clave", id_clave);
        registro.put("id", id_encuestado);
        registro.put("fecha_inicio_intento", fecha_actual());
        registro.put("numero_intento", IntentoConsultasDB.ultimo_intento(id_estudiante , db)+1);

        db.insert("intento", null, registro);
        Cursor cursor = db.rawQuery("SELECT ID_INTENTO FROM INTENTO ORDER BY ID_INTENTO DESC LIMIT 1", null);
        cursor.moveToFirst();

        id_intento = cursor.getInt(0);

    }

    public void terminar_intento() {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        SQLiteDatabase db = databaseAccess.open();

        ContentValues reg = new ContentValues();

        reg.put("fecha_final_intento", fecha_actual());
        reg.put("nota_intento", calcular_nota());

        if(sumIntento) reg.put("numero_intento", IntentoConsultasDB.ultimo_intento(id_estudiante , db)+1);

        db.update("intento", reg, "id_intento=" + id_intento, null);


    }

    public String fecha_actual() {
        Date date = new Date();
        DateFormat fechaHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String convertido = fechaHora.format(date);

        return convertido;
    }

    public void modelo_respuesta(List<RadioGroup> rg_seleccion, List<Spinner> sp_seleccion, List<EditText> et_seleccion, List<RadioGroup> rg_seleccion_vf) {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        SQLiteDatabase db = databaseAccess.open();
        ContentValues registro = new ContentValues();

        if(rg_seleccion !=null || rg_seleccion.size()>0){
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
            }
        }

        if(rg_seleccion_vf !=null || rg_seleccion_vf.size()>0){
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
            }
        }

        if(sp_seleccion !=null || sp_seleccion.size()>0){
            for (Spinner sp : sp_seleccion) {
                registro.put("id_opcion", idesGPO.get(sp.getSelectedItemPosition()));
                registro.put("id_intento", id_intento);
                registro.put("id_pregunta", sp.getId());
                db.insert("respuesta", null, registro);
            }
        }

        if(et_seleccion !=null || et_seleccion.size()>0){
            int i =0;
            for (EditText et : et_seleccion) {
                registro.put("id_opcion", et.getId());
                registro.put("id_intento", id_intento);
                registro.put("id_pregunta", idPreguntaRC.get(i));
                registro.put("texto_respuesta", et.getText().toString());
                db.insert("respuesta", null, registro);
                i++;
            }
        }

    }

    public double calcular_nota() {
        double nota = 0.0;
        int i = 0;

        while (i < preguntas.size()) {
            if(preguntas.get(i).modalidad==1){
                if (preguntas.get(i).preguntaPList.get(0).respuesta == rg_lista.get(i).getCheckedRadioButtonId()) {
                    nota += preguntas.get(i).preguntaPList.get(0).ponderacion;
                }

            }else if(preguntas.get(i).modalidad==2){
                if (preguntas.get(i).preguntaPList.get(0).respuesta == rg_posicion_lista_vf.get(i).getCheckedRadioButtonId()) {
                    nota += preguntas.get(i).preguntaPList.get(0).ponderacion;
                }
            }else if(preguntas.get(i).modalidad==3){
                for(int j=0; j<sp_lista.size();j++){
                    //int re = sp_lista.get(j).getId();
                    int re = preguntas.get(i).preguntaPList.get(j).respuesta;
                    int el = idesGPO.get(sp_lista.get(j).getSelectedItemPosition());
                    if(re==el){
                        nota +=preguntas.get(i).preguntaPList.get(j).ponderacion;
                    }
                }
            }else if(preguntas.get(i).modalidad==4){
                int id_respuesta = preguntas.get(i).preguntaPList.get(0).respuesta;
                String valor_digitado = et_posicion_lista.get(i).getText().toString().toLowerCase();
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

    public String rc_getOpcion(int id){
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        SQLiteDatabase db = databaseAccess.open();

        Cursor cursor = db.rawQuery("SELECT OPCION FROM OPCION WHERE ID_OPCION="+id, null);
        cursor.moveToFirst();

        return  cursor.getString(0);
    }

    public boolean primerIntento(int id_estudiante, int id_turno){
        boolean resultado=true;
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
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
     public int id_intento(int id_est){
         DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
         SQLiteDatabase db = databaseAccess.open();

         return IntentoConsultasDB.id_ultimo_intento(id_est, db);
     }

     public void deleteRespuesta(int id_intento){
         DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
         SQLiteDatabase db = databaseAccess.open();

         db.execSQL("DELETE FROM RESPUESTA WHERE ID_INTENTO="+id_intento);
         db.close();
     }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(context, "Seleccion: "+position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
