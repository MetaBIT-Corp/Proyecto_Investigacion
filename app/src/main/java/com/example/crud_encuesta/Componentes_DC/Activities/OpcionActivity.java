package com.example.crud_encuesta.Componentes_DC.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crud_encuesta.Componentes_DC.Adaptadores.AdaptadorOpcion;
import com.example.crud_encuesta.Componentes_DC.Adaptadores.AdaptadorOpcionRecyclerView;
import com.example.crud_encuesta.Componentes_DC.Dao.DaoOpcion;
import com.example.crud_encuesta.Componentes_DC.Objetos.Opcion;
import com.example.crud_encuesta.R;

import java.util.ArrayList;

public class OpcionActivity extends AppCompatActivity {

    private DaoOpcion dao;
    private AdaptadorOpcion adaptador;
    private AdaptadorOpcionRecyclerView adaptadorRV;
    private ArrayList<Opcion> lista_opciones;
    private Opcion opcion;
    private int id_tipo_item;

    private RecyclerView recyclerView;

    private int id_pregunta;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opcion);

        FloatingActionButton agregar = (FloatingActionButton)findViewById(R.id.btn_nuevo);
        agregar.setVisibility(View.VISIBLE);
        Button btn_change = (Button)findViewById(R.id.btn_change);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        id_pregunta = b.getInt("id_pregunta");
        id_tipo_item = b.getInt("id_tipo_item");
        int es_verdadero_falso=0;
        int es_respuesta_corta=0;
        if(id_tipo_item==2){
            es_verdadero_falso=1;
            agregar.setVisibility(View.GONE);
            btn_change.setVisibility(View.VISIBLE);
        }else if(id_tipo_item==4){
            es_respuesta_corta=1;
        }
        final int es_resp_corta_final = es_respuesta_corta;

        dao = new DaoOpcion(this, id_pregunta,es_verdadero_falso, es_respuesta_corta);
        lista_opciones = dao.verTodos();

        if (lista_opciones.size()==1){
            agregar.setVisibility(View.GONE);
        }
        /*adaptador = new AdaptadorOpcion(lista_opciones,this,dao, es_verdadero_falso,es_respuesta_corta, id_tipo_item);
        ListView list = (ListView)findViewById(R.id.lista);
        list.setAdapter(adaptador);*/

        adaptadorRV = new AdaptadorOpcionRecyclerView(lista_opciones,this,dao, es_verdadero_falso,es_respuesta_corta, id_tipo_item);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptadorRV);

        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dao.cambiar();
                adaptadorRV.notifyDataSetChanged();
                lista_opciones = dao.verTodos();
            }
        });

        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(OpcionActivity.this);
                dialog.setTitle(R.string.nueva_opc);
                dialog.setCancelable(true);
                if(es_resp_corta_final!=1) dialog.setContentView(R.layout.dialogo_opcion);
                else dialog.setContentView(R.layout.dialogo_opcion_resp_corta);
                dialog.show();

                final EditText texto_opcion = (EditText)dialog.findViewById(R.id.editt_opcion);
                final CheckBox cb_correcta = (CheckBox)dialog.findViewById(R.id.cb_correcta);


                final Button agregar = (Button)dialog.findViewById(R.id.btn_agregar);
                Button cancelar = (Button)dialog.findViewById(R.id.btn_cancelar);
                TextView texto_titulo = (TextView)dialog.findViewById(R.id.texto_titulo);
                texto_titulo.setText(R.string.agregar_opc);
                agregar.setText(R.string.btn_agregar);
                agregar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int check = 0;

                        if(es_resp_corta_final!=1){

                            if(cb_correcta.isChecked())check = 1;
                        }else{
                            cb_correcta.setVisibility(View.GONE);
                            check = 1;
                        }

                        try{

                            if(!texto_opcion.getText().toString().equals("")){

                                opcion = new Opcion(id_pregunta, texto_opcion.getText().toString(), check);

                                dao.insertar(opcion);
                                adaptadorRV.notifyDataSetChanged();
                                lista_opciones = dao.verTodos();
                                dialog.dismiss();

                                if(id_tipo_item==4){

                                    Intent in = new Intent(getApplicationContext(), OpcionActivity.class);
                                    in.putExtra("id_pregunta", id_pregunta);
                                    in.putExtra("id_tipo_item",id_tipo_item);
                                    v.getContext().startActivity(in);
                                }

                            }else{
                                Toast.makeText(v.getContext(), R.string.msg_falta_texto_opc, Toast.LENGTH_SHORT).show();
                                texto_opcion.setFocusable(true);
                            }

                        }catch (Exception e){
                            Log.d("BUSQUEDA", "CATCH");
                        }
                    }
                });

                cancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

}
