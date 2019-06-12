package com.example.crud_encuesta.Componentes_DC.Adaptadores;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crud_encuesta.Componentes_DC.Activities.OpcionActivity;
import com.example.crud_encuesta.Componentes_DC.Dao.DaoPregunta;
import com.example.crud_encuesta.Componentes_DC.Objetos.Pregunta;
import com.example.crud_encuesta.R;

import java.util.ArrayList;

public class AdaptadorPreguntaRecyclerView extends RecyclerView.Adapter<AdaptadorPreguntaRecyclerView.ViewHolder> {

    private ArrayList<Pregunta> lista_preguntas = new ArrayList<>();
    private DaoPregunta dao;
    //private Pregunta pregunta;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private Activity a;
    private int id;
    private int id_tipo_item;

    public AdaptadorPreguntaRecyclerView(ArrayList<Pregunta> lista_preguntas,Activity a,DaoPregunta dao, int id_tipo_item) {
        this.lista_preguntas = lista_preguntas;
        this.dao = dao;
        this.a = a;
        this.id_tipo_item = id_tipo_item;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_pregunta,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.asignarDatos(lista_preguntas.get(i), i);
    }

    @Override
    public int getItemCount() {
        return lista_preguntas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView texto_pregunta;
        Button editar;
        Button eliminar;
        Button opcion;

        public ViewHolder(@NonNull View v) {
            super(v);
            texto_pregunta = (TextView)v.findViewById(R.id.txt_pregunta);
            editar = (Button)v.findViewById(R.id.btn_editar);
            eliminar = (Button)v.findViewById(R.id.btn_eliminar);
            opcion = (Button)v.findViewById(R.id.btn_ag_opcion);
        }

        public void asignarDatos(final Pregunta pregunta, int position) {
            texto_pregunta.setText(pregunta.getPregunta());
            /*editar.setTag(position);
            eliminar.setTag(position);
            opcion.setTag(position);*/

            opcion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*final int pos = Integer.parseInt(v.getTag().toString());
                    pregunta = lista_preguntas.get(pos);*/
                    Bundle b = new Bundle();
                    b.putInt("id_pregunta",pregunta.getId());;
                    b.putInt("id_tipo_item",id_tipo_item);
                    Intent i = new Intent(a, OpcionActivity.class);
                    i.putExtras(b);
                    a.startActivity(i);
                }
            });

            editar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //final int pos = Integer.parseInt(v.getTag().toString());
                    final Dialog dialog = new Dialog(a);
                    dialog.setTitle(R.string.edit_preg);
                    dialog.setCancelable(true);
                    dialog.setContentView(R.layout.dialogo_pregunta);
                    dialog.show();

                    Toast.makeText(v.getContext(), "AQUI LLEGA", Toast.LENGTH_SHORT).show();

                    final EditText texto_pregunta = (EditText)dialog.findViewById(R.id.editt_pregunta);
                    Button agregar = (Button)dialog.findViewById(R.id.btn_agregar);
                    Button cancelar = (Button)dialog.findViewById(R.id.btn_cancelar);
                    TextView texto_titulo = (TextView)dialog.findViewById(R.id.texto_titulo);
                    texto_titulo.setText(R.string.edit_preg);
                    agregar.setText(R.string.btn_guardar);
                    //pregunta = lista_preguntas.get(pos);
                    setId(pregunta.getId());
                    final int id_grupo_emp = pregunta.getId_grupo_emp();
                    texto_pregunta.setText(pregunta.getPregunta());

                    agregar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try{

                                if(!texto_pregunta.getText().toString().equals("")){

                                    dao.editar(new Pregunta(getId(),id_grupo_emp, texto_pregunta.getText().toString()));
                                    notifyDataSetChanged();
                                    lista_preguntas = dao.verTodos();
                                    dialog.dismiss();

                                }else{
                                    Toast.makeText(v.getContext(), R.string.msg_falta_texto_preg, Toast.LENGTH_SHORT).show();
                                    texto_pregunta.setFocusable(true);
                                }

                            }catch (Exception e){
                                //Toast.makeText(a, "Error", Toast.LENGTH_SHORT);
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

            eliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //final int pos = Integer.parseInt(v.getTag().toString());
                    //pregunta = lista_preguntas.get(pos);
                    setId(pregunta.getId());
                    int cant = dao.cantidad_eliminar_opciones(getId());
                    AlertDialog.Builder del = new AlertDialog.Builder(a);

                    String del_preg_1 = v.getResources().getString(R.string.del_preg_1);
                    String del_preg_2 = v.getResources().getString(R.string.del_preg_2);

                    del.setMessage(del_preg_1+" "+cant+" "+del_preg_2);
                    del.setCancelable(false);
                    del.setPositiveButton(R.string.positivo, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dao.eliminar(getId());
                            lista_preguntas = dao.verTodos();
                            notifyDataSetChanged();
                        }
                    });

                    del.setNegativeButton(R.string.negativo, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    del.show();
                }
            });

        }
    }
}
