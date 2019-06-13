package com.example.crud_encuesta.Componentes_DC.Adaptadores;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crud_encuesta.Componentes_DC.Dao.DaoOpcion;
import com.example.crud_encuesta.Componentes_DC.Objetos.Opcion;
import com.example.crud_encuesta.R;

import java.util.ArrayList;

public class AdaptadorOpcionRecyclerView extends RecyclerView.Adapter<AdaptadorOpcionRecyclerView.ViewHolder> {

    private ArrayList<Opcion> lista_opciones;
    private DaoOpcion dao;
    private Activity a;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
    private int es_verdadero_falso;
    private int es_respuesta_corta;
    private int id_tipo_item;

    public AdaptadorOpcionRecyclerView(ArrayList<Opcion> lista_opciones, Activity a, DaoOpcion dao, int es_verdadero_falso, int es_respuesta_corta, int id_tipo_item) {
        this.lista_opciones = lista_opciones;
        this.dao = dao;
        this.id_tipo_item = id_tipo_item;
        this.a = a;
        this.es_verdadero_falso = es_verdadero_falso;
        this.es_respuesta_corta = es_respuesta_corta;
    }

    @NonNull
    @Override
    public AdaptadorOpcionRecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_opcion,null,false);
        return new AdaptadorOpcionRecyclerView.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorOpcionRecyclerView.ViewHolder viewHolder, int i) {
        viewHolder.asignarDatos(lista_opciones.get(i), i);
    }

    @Override
    public int getItemCount() {
        return lista_opciones.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

            TextView texto_opcion;
            CheckBox cb_correcta;
            Button editar;
            Button eliminar;

            public ViewHolder(@NonNull View v) {
                super(v);
                texto_opcion = (TextView)v.findViewById(R.id.txt_opcion);
                cb_correcta = (CheckBox)v.findViewById(R.id.cb_correcta);
                editar = (Button)v.findViewById(R.id.btn_editar);
                eliminar = (Button)v.findViewById(R.id.btn_eliminar);

                if(id_tipo_item==4)eliminar.setVisibility(View.GONE);
            }

            public void asignarDatos(final Opcion opcion, int i) {
                texto_opcion.setText(opcion.getOpcion());

                if(opcion.getCorrecta() == 1){
                    cb_correcta.setChecked(true);
                }else{
                    cb_correcta.setChecked(false);
                }
                if(es_verdadero_falso==1){
                    editar.setVisibility(View.GONE);
                    eliminar.setVisibility(View.GONE);
                }else{

                    final int es_resp_corta_final=es_respuesta_corta;

                    editar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final Dialog dialog = new Dialog(a);
                            dialog.setTitle(R.string.edit_opc);
                            dialog.setCancelable(true);

                            if(es_resp_corta_final!=1) dialog.setContentView(R.layout.dialogo_opcion);
                            else dialog.setContentView(R.layout.dialogo_opcion_resp_corta);

                            dialog.show();

                            final EditText texto_opcion = (EditText)dialog.findViewById(R.id.editt_opcion);
                            final CheckBox cb_correcta = (CheckBox)dialog.findViewById(R.id.cb_correcta);
                            Button agregar = (Button)dialog.findViewById(R.id.btn_agregar);
                            Button cancelar = (Button)dialog.findViewById(R.id.btn_cancelar);
                            TextView texto_titulo = (TextView)dialog.findViewById(R.id.texto_titulo);
                            texto_titulo.setText(R.string.edit_opc);
                            agregar.setText(R.string.btn_guardar);

                            setId(opcion.getId());
                            final int id_pregunta = opcion.getId_pregunta();
                            texto_opcion.setText(opcion.getOpcion());

                            try{
                                if(opcion.getCorrecta() == 1){
                                    cb_correcta.setChecked(true);
                                }else{
                                    cb_correcta.setChecked(false);
                                }
                            }catch (Exception e){

                            }

                            agregar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try{

                                        int check = 0;

                                        try{
                                            if(cb_correcta.isChecked())check = 1;
                                        }catch (Exception e){

                                        }
                                        if(!texto_opcion.getText().toString().equals("")){

                                            dao.editar(new Opcion(getId(),id_pregunta,texto_opcion.getText().toString(),check));
                                            notifyDataSetChanged();
                                            lista_opciones = dao.verTodos();
                                            dialog.dismiss();

                                        }else{
                                            Toast.makeText(v.getContext(), R.string.msg_falta_texto_opc, Toast.LENGTH_SHORT).show();
                                            texto_opcion.setFocusable(true);
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

                            setId(opcion.getId());
                            AlertDialog.Builder del = new AlertDialog.Builder(a);
                            del.setMessage(R.string.del_opc);
                            del.setCancelable(false);
                            del.setPositiveButton(R.string.positivo, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dao.eliminar(getId());
                                    lista_opciones = dao.verTodos();
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
}
