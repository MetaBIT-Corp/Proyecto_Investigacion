package com.example.crud_encuesta.Componentes_MT.Intento;

import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.crud_encuesta.R;

import java.util.ArrayList;
import java.util.List;

public class PreguntaView implements AdapterView.OnItemSelectedListener {
    private Context context;
    private List<Spinner> sp_lista = new ArrayList<>();

    public PreguntaView(Context context){
        this.context = context;
    }

    public ReturnView getVista(Pregunta preguntas) {
        ReturnView retorno = new ReturnView();
        LinearLayout ll_pregunta = new LinearLayout(context);
        ll_pregunta.setOrientation(LinearLayout.VERTICAL);

        View v = new View(context);
        v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 5));
        v.setBackgroundColor(context.getResources().getColor(R.color.primary));

        TextView txt_pregunta = new TextView(context);
        txt_pregunta.setPadding(0,   0,0,25);
        txt_pregunta.setTextColor(Color.BLACK);
        txt_pregunta.setTextSize(18);

        switch (preguntas.modalidad) {
            case 1:
                txt_pregunta.setText(preguntas.preguntaPList.get(0).pregunta);
                RadioGroup rg_pregunta = new RadioGroup(context);
                rg_pregunta.setId(preguntas.preguntaPList.get(0).id);
                for (int i = 0; i < preguntas.preguntaPList.get(0).opciones.size(); i++) {
                    RadioButton rb_pregunta = new RadioButton(context);
                    rb_pregunta.setText( preguntas.preguntaPList.get(0).opciones.get(i));
                    rb_pregunta.setId( preguntas.preguntaPList.get(0).ides.get(i));
                    rg_pregunta.addView(rb_pregunta);
                }

                if(txt_pregunta.getParent() != null) {
                    ((ViewGroup)txt_pregunta.getParent()).removeView(txt_pregunta);
                }
                ll_pregunta.addView(txt_pregunta);

                if(v.getParent() != null) {
                    ((ViewGroup)v.getParent()).removeView(v);
                }
                ll_pregunta.addView(v);

                if(rg_pregunta.getParent() != null) {
                    ((ViewGroup)rg_pregunta.getParent()).removeView(rg_pregunta);
                }
                ll_pregunta.addView(rg_pregunta);

                retorno.setRadioGroupOM(rg_pregunta);

                break;

            case 2:
                txt_pregunta.setText(preguntas.preguntaPList.get(0).pregunta);

                RadioGroup rg_pregunta_vf = new RadioGroup(context);
                rg_pregunta_vf.setId(preguntas.preguntaPList.get(0).id);
                for (int i = 0; i < preguntas.preguntaPList.get(0).opciones.size(); i++) {
                    RadioButton rb_pregunta = new RadioButton(context);
                    rb_pregunta.setText( preguntas.preguntaPList.get(0).opciones.get(i));
                    rb_pregunta.setId( preguntas.preguntaPList.get(0).ides.get(i));
                    rg_pregunta_vf.addView(rb_pregunta);

                    retorno.setRadioGroupVF(rg_pregunta_vf);

                    if(txt_pregunta.getParent() != null) {
                        ((ViewGroup)txt_pregunta.getParent()).removeView(txt_pregunta);
                    }
                    ll_pregunta.addView(txt_pregunta);

                    if(v.getParent() != null) {
                        ((ViewGroup)v.getParent()).removeView(v);
                    }
                    ll_pregunta.addView(v);

                    if(rg_pregunta_vf.getParent() != null) {
                        ((ViewGroup)rg_pregunta_vf.getParent()).removeView(rg_pregunta_vf);
                    }
                    ll_pregunta.addView(rg_pregunta_vf);

                }
                break;

            case 3:
                txt_pregunta.setText(preguntas.descripcion);

                ArrayAdapter<String> comboAdapter;
                List<String> opcionesGPO = new ArrayList<>();
                List<Integer> id_preguntas = new ArrayList<>();

                for(int i=0; i<preguntas.preguntaPList.size();i++){
                    for(int j=0; j<preguntas.preguntaPList.get(i).opciones.size(); j++){
                        opcionesGPO.add(preguntas.preguntaPList.get(i).opciones.get(j));
                        id_preguntas.add(preguntas.preguntaPList.get(i).ides.get(j));
                    }
                }

                retorno.setIdPreguntaSP(id_preguntas);

                if(txt_pregunta.getParent() != null) {
                    ((ViewGroup)txt_pregunta.getParent()).removeView(txt_pregunta);
                }
                ll_pregunta.addView(txt_pregunta);

                if(v.getParent() != null) {
                    ((ViewGroup)v.getParent()).removeView(v);
                }
                ll_pregunta.addView(v);

                for(PreguntaP p : preguntas.preguntaPList){
                    TextView txt = new TextView(context);
                    Spinner spGPO = new Spinner(context);
                    spGPO.setId(p.id);

                    spGPO.setOnItemSelectedListener(this);

                    txt.setTextSize(15);
                    txt.setTextColor(Color.BLACK);
                    txt.setText(p.pregunta);
                    txt.setPadding(0,40,0,40);

                    comboAdapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item, opcionesGPO);
                    spGPO.setAdapter(comboAdapter);

                    sp_lista.add(spGPO);


                    if(txt.getParent() != null) {
                        ((ViewGroup)txt.getParent()).removeView(txt);
                    }

                    ll_pregunta.addView(txt);

                    if(spGPO.getParent() != null) {
                        ((ViewGroup)spGPO.getParent()).removeView(spGPO);
                    }
                    ll_pregunta.addView(spGPO);
                }
                retorno.setSpinner(sp_lista);
                break;

            case 4:
                txt_pregunta.setText(preguntas.preguntaPList.get(0).pregunta);
                txt_pregunta.setPadding(0,0,0,20);

                retorno.setIdPreguntaRC(preguntas.preguntaPList.get(0).id);

                EditText et_respuesta = new EditText(context);
                et_respuesta.setId(preguntas.preguntaPList.get(0).ides.get(0));
                et_respuesta.setHint("Responda con una palabra");
                et_respuesta.setInputType(InputType.TYPE_CLASS_TEXT);

                retorno.setEditText(et_respuesta);

                if(txt_pregunta.getParent() != null) {
                    ((ViewGroup)txt_pregunta.getParent()).removeView(txt_pregunta);
                }
                ll_pregunta.addView(txt_pregunta);

                if(v.getParent() != null) {
                    ((ViewGroup)v.getParent()).removeView(v);
                }
                ll_pregunta.addView(v);

                if(et_respuesta.getParent() != null) {
                    ((ViewGroup)et_respuesta.getParent()).removeView(et_respuesta);
                }
                ll_pregunta.addView(et_respuesta);

                break;

        }

        retorno.setView(ll_pregunta);

        return retorno;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(context, "Seleccion: "+position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
