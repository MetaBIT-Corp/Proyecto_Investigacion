package com.example.crud_encuesta.Componentes_MT.Intento;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import com.example.crud_encuesta.MainActivity;
import com.example.crud_encuesta.R;

import java.util.ArrayList;
import java.util.List;

public class VerIntentoAdapter extends BaseAdapter implements AdapterView.OnItemSelectedListener{
    private LayoutInflater inflater;
    private Context context;
    private List<PreguntaRevision> preguntas = new ArrayList<>();
    private Activity activity;
    private int id_encuesta;

    private List<Integer> idesGPO = new ArrayList<>();

    public VerIntentoAdapter(List<PreguntaRevision> preguntas, int id_encuesta, Activity activity, Context context) {
        this.preguntas = preguntas;
        this.activity = activity;
        this.context = context;
        this.id_encuesta = id_encuesta;

        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        final View mView = inflater.inflate(R.layout.elemento_list_pregunta, null);
        TextView txt_pregrunta = mView.findViewById(R.id.txtPregunta);
        LinearLayout ll_pregunta = mView.findViewById(R.id.llPregunta);
        Button inicio = new Button(context);
        inicio.setText(R.string.mt_ir_inicio);

        switch (preguntas.get(position).modalidad) {
            case 1:
                txt_pregrunta.setText(preguntas.get(position).pregunta);

                RadioGroup rg_pregunta = new RadioGroup(context);
                for (int i = 0; i < preguntas.get(position).opciones.size(); i++) {
                    RadioButton rb_pregunta = new RadioButton(context);
                    rb_pregunta.setText( preguntas.get(position).opciones.get(i));
                    rb_pregunta.setId( preguntas.get(position).ides.get(i));

                    if(preguntas.get(position).ides.get(i)==preguntas.get(position).eleccion){
                        rb_pregunta.setChecked(true);
                        if(preguntas.get(position).eleccion==preguntas.get(position).respuesta){
                            rb_pregunta.setTextColor(Color.GREEN);
                        }else{
                            System.out.println("id_encuesta-------"+id_encuesta);
                            if(id_encuesta!=0){
                                rb_pregunta.setTextColor(Color.BLUE);
                            }
                            else{
                                rb_pregunta.setTextColor(Color.RED);
                            }
                        }
                    }

                    rb_pregunta.setEnabled(false);
                    rg_pregunta.addView(rb_pregunta);
                }

                ll_pregunta.addView(rg_pregunta);
                break;

            case 2:
                txt_pregrunta.setText(preguntas.get(position).pregunta);

                RadioGroup rg_pregunta_vf = new RadioGroup(context);
                for (int i = 0; i < preguntas.get(position).opciones.size(); i++) {
                    RadioButton rb_pregunta = new RadioButton(context);
                    rb_pregunta.setText( preguntas.get(position).opciones.get(i));
                    rb_pregunta.setId( preguntas.get(position).ides.get(i));

                    if(preguntas.get(position).ides.get(i)==preguntas.get(position).eleccion){
                        rb_pregunta.setChecked(true);
                        if(preguntas.get(position).eleccion==preguntas.get(position).respuesta){
                            rb_pregunta.setTextColor(Color.GREEN);
                        }else{
                            if(id_encuesta!=0) rb_pregunta.setTextColor(Color.BLUE);
                            rb_pregunta.setTextColor(Color.RED);
                        }
                    }

                    rb_pregunta.setEnabled(false);
                    rg_pregunta_vf.addView(rb_pregunta);
                }
                ll_pregunta.addView(rg_pregunta_vf);
                break;

            case 3:
                txt_pregrunta.setText(preguntas.get(position).descripcion);
                ArrayAdapter<String> comboAdapter;
                List<String> opcionesGPO = new ArrayList<>();

                for(int i=0; i<preguntas.get(position).opciones.size(); i++){
                    opcionesGPO.add(preguntas.get(position).opciones.get(i));
                    idesGPO.add(preguntas.get(position).ides.get(i));
                }

                TextView txt = new TextView(context);
                Spinner spGPO = new Spinner(context);

                spGPO.setOnItemSelectedListener(this);

                txt.setTextSize(15);
                txt.setTextColor(Color.BLACK);
                txt.setText(preguntas.get(position).pregunta);

                txt.setPadding(0,40,0,40);

                comboAdapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item, opcionesGPO);
                spGPO.setAdapter(comboAdapter);

                for (int i =0; i<idesGPO.size(); i++) {
                    if(idesGPO.get(i)==preguntas.get(position).eleccion){
                        spGPO.setSelection(i);

                        if(idesGPO.get(i)==preguntas.get(position).respuesta){
                            spGPO.setBackgroundColor(Color.GREEN);
                        }else{
                            if(id_encuesta!=0) spGPO.setBackgroundColor(Color.BLUE);
                            spGPO.setBackgroundColor(Color.RED);
                        }
                    }
                }
                spGPO.setSelection(1);

                spGPO.setEnabled(false);

                ll_pregunta.addView(txt);
                ll_pregunta.addView(spGPO);

                break;

            case 4:
                int id_respuesta = preguntas.get(position).respuesta;
                String respuesta = rc_getOpcion(id_respuesta);
                txt_pregrunta.setText(preguntas.get(position).pregunta);
                txt_pregrunta.setPadding(0,0,0,20);

                EditText et_respuesta = new EditText(context);

                et_respuesta.setText(""+preguntas.get(position).texto_eleccion);

                if(respuesta.equals(preguntas.get(position).texto_eleccion)){
                    et_respuesta.setTextColor(Color.GREEN);
                }else{
                    if(id_encuesta!=0) et_respuesta.setTextColor(Color.BLUE);
                    et_respuesta.setTextColor(Color.RED);
                }

                et_respuesta.setEnabled(false);

                ll_pregunta.addView(et_respuesta);
                break;
        }

        if (position == getCount() - 1) {
            ll_pregunta.addView(inicio);
        }

        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(context, MainActivity.class);
                //context.startActivity(i);
                activity.finish();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public String rc_getOpcion(int id){
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        SQLiteDatabase db = databaseAccess.open();

        Cursor cursor = db.rawQuery("SELECT OPCION FROM OPCION WHERE ID_OPCION="+id, null);
        cursor.moveToFirst();

        return  cursor.getString(0);
    }
}
