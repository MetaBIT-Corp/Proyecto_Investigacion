package com.example.crud_encuesta.Componentes_DC.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crud_encuesta.Componentes_DC.Adaptadores.AdaptadorPregunta;
import com.example.crud_encuesta.Componentes_DC.Adaptadores.AdaptadorPreguntaRecyclerView;
import com.example.crud_encuesta.Componentes_DC.Dao.DaoPregunta;
import com.example.crud_encuesta.Componentes_DC.Objetos.Pregunta;
import com.example.crud_encuesta.Componentes_MT.Area.Area;
import com.example.crud_encuesta.Componentes_MT.Area.AreaActivity;
import com.example.crud_encuesta.R;
import com.example.crud_encuesta.SubMenuMateriaActivity;

import java.util.ArrayList;

public class PreguntaActivity extends AppCompatActivity {

    private DaoPregunta dao;
    private AdaptadorPregunta adaptador;
    private AdaptadorPreguntaRecyclerView adaptadorRV;
    private ArrayList<Pregunta> lista_preguntas;
    private Pregunta pregunta;

    private RecyclerView recyclerView;

    private int id_gpo_emp=0;
    private String desc_gpo_emp;
    private  int id_area =0;
    private int id_tipo_item;
    private ImageView btn_buscar;
    private ImageView btn_todo;
    private EditText texto_busqueda;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregunta);
        Intent i = getIntent();
        Bundle b = i.getExtras();

        id_gpo_emp = b.getInt("id_gpo_emp");
        desc_gpo_emp = b.getString("desc_gpo_emp");
        id_area = b.getInt("id_area");
        id_tipo_item = b.getInt("id_tipo_item");

        dao = new DaoPregunta(this, id_gpo_emp, id_area, id_tipo_item);

        lista_preguntas = dao.verTodos();
        //adaptador = new AdaptadorPregunta(lista_preguntas,this,dao, id_tipo_item);
        adaptadorRV = new AdaptadorPreguntaRecyclerView(lista_preguntas,this,dao, id_tipo_item);
        /*ListView list = (ListView)findViewById(R.id.lista);
        list.setAdapter(adaptador);*/
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptadorRV);

        FloatingActionButton agregar = findViewById(R.id.btn_nuevo);

        btn_buscar = (ImageView)findViewById(R.id.btn_buscar);
        btn_todo = (ImageView)findViewById(R.id.btn_todo);
        texto_busqueda = (EditText)findViewById(R.id.texto_busqueda);
       /* TextView texto_desc_emp = (TextView)findViewById(R.id.txt_desc_emp);
        texto_desc_emp.setText(desc_gpo_emp);
        Button editar_gpo_emp = (Button)findViewById(R.id.btn_editar_desc_emp);

        editar_gpo_emp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(PreguntaActivity.this, GpoEmpActivity.class);
                in.putExtra("id_gpo_emp",id_gpo_emp);
                in.putExtra("id_tipo_item",3);
                in.putExtra("accion",1);
                in.putExtra("id_area",id_area);
                startActivity(in);
            }
        });*/

        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(PreguntaActivity.this);
                dialog.setTitle(R.string.nueva_preg);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialogo_pregunta);
                dialog.show();

                final EditText texto_pregunta = (EditText)dialog.findViewById(R.id.editt_pregunta);
                Button agregar = (Button)dialog.findViewById(R.id.btn_agregar);
                Button cancelar = (Button)dialog.findViewById(R.id.btn_cancelar);
                TextView texto_titulo = (TextView)dialog.findViewById(R.id.texto_titulo);
                texto_titulo.setText(R.string.agregar_preg);
                agregar.setText(R.string.btn_agregar);
                agregar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try{
                            if(!texto_pregunta.getText().toString().equals("")){

                                pregunta = new Pregunta(id_gpo_emp, texto_pregunta.getText().toString());
                                dao.insertar(pregunta);
                                adaptadorRV.notifyDataSetChanged();
                                lista_preguntas = dao.verTodos();
                                dialog.dismiss();

                            }else{
                                Toast.makeText(v.getContext(), R.string.msg_falta_texto_preg, Toast.LENGTH_SHORT).show();
                                texto_pregunta.setFocusable(true);
                            }

                        }catch (Exception e){
                            //Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT);
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

        btn_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!texto_busqueda.getText().toString().isEmpty()){

                    lista_preguntas = dao.busqueda(texto_busqueda.getText().toString());
                    adaptador.dataChange(lista_preguntas);
                }else{
                    Toast.makeText(
                            v.getContext(),
                            R.string.msg_consulta_nula,
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });

        btn_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lista_preguntas = dao.verTodos();
                texto_busqueda.setText("");
                adaptador.dataChange(lista_preguntas);
            }
        });

    }

    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
        //do whatever you want the 'Back' button to do
        //as an example the 'Back' button is set to start a new Activity named 'NewActivity'
        /*if (id_tipo_item!=3) {
            startActivity(new Intent(PreguntaActivity.this, AreaActivity.class));
            finish();
        }else{
            startActivity(new Intent(PreguntaActivity.this, GpoEmpActivity.class));
            finish();
        }*/
    }
}
