package com.example.crud_encuesta.Componentes_AP.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crud_encuesta.Componentes_AP.DAO.DAOEvaluacion;
import com.example.crud_encuesta.Componentes_AP.DAO.DAOUsuario;
import com.example.crud_encuesta.Componentes_AP.Models.Evaluacion;
import com.example.crud_encuesta.Componentes_AP.Models.Usuario;
import com.example.crud_encuesta.R;

import java.util.ArrayList;

public class AdapterEvaluacion extends BaseAdapter {

    ArrayList<Evaluacion> evaluaciones = new ArrayList<>();
    Evaluacion evaluacion;
    DAOEvaluacion daoEvaluacion;
    Activity activity; //la actividad donde va a mostrarse el listview
    Context context;


    //variables que nos ayudara a manejar los id de evaluacion y carga academica
    int idEvaluacion = 0;
    int idCarga = 0;

    //constructos del adapter

    public AdapterEvaluacion(ArrayList<Evaluacion> evaluaciones, DAOEvaluacion daoEvaluacion, Activity activity,Context context) {
        this.evaluaciones = evaluaciones;
        this.daoEvaluacion = daoEvaluacion;
        this.activity = activity;
        this.context = context;
    }


    //setter y getter de los id


    public int getIdEvaluacion() {
        return idEvaluacion;
    }

    public void setIdEvaluacion(int idEvaluacion) {
        this.idEvaluacion = idEvaluacion;
    }

    public int getIdCarga() {
        return idCarga;
    }

    public void setIdCarga(int idCarga) {
        this.idCarga = idCarga;
    }


    //metodos

    @Override
    public int getCount() {
        return evaluaciones.size();
    }

    @Override
    public Evaluacion getItem(int position) {
        return evaluaciones.get(position);
    }

    @Override
    public long getItemId(int position) {
        return evaluaciones.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //retorna el view de una fila x del listview
        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.item_formato, null); //inflamos la fila con el item
        }

        //vinculamos el arraylist de contactos con el listview
        evaluacion = evaluaciones.get(position);


        TextView tv_item = (TextView) view.findViewById(R.id.ap_tv_item);
        ImageView editar = (ImageView) view.findViewById(R.id.ap_editar_item);
        ImageView eliminar = (ImageView) view.findViewById(R.id.ap_eliminar_item);
        ImageView info = (ImageView) view.findViewById(R.id.ap_info_item);
        ImageView turnoi = (ImageView) view.findViewById(R.id.ap_turno_item);
        ImageView descargar = (ImageView) view.findViewById(R.id.btn_descargar);
        descargar.setVisibility(View.INVISIBLE);
        tv_item.setText(evaluacion.getNombre());

        //ocultados de acuerdo a rol
        DAOUsuario daoUsuario = new DAOUsuario(context);
        Usuario usuario = daoUsuario.getUsuarioLogueado();
        if(usuario.getROL()== 0 || usuario.getROL()==2){
            editar.setVisibility(View.INVISIBLE);
            eliminar.setVisibility(View.INVISIBLE);
            turnoi.setVisibility(View.INVISIBLE);
        }

        //utilizamos setTag para que al presionar editar o eliminar, android sepa cuál registro queremos afectar
        editar.setTag(position);
        eliminar.setTag(position);
        info.setTag(position);
        turnoi.setVisibility(View.INVISIBLE);

        //TODO: OnCLICKLISTENER DE OPCIONES DE USUARIOS

        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //dialogo de editar

                //recuperamos la posicion del registro
                int pos = Integer.parseInt(v.getTag().toString());

                //recuperamos el contacto de la lista y seteamos el id
                evaluacion = evaluaciones.get(pos);
                setIdEvaluacion(evaluacion.getId());
                setIdCarga(evaluacion.getIdCargaAcad());

                //creamos dialogo
                final Dialog dialog = new Dialog(activity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//para quitar el titulo
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialogo_evaluacion);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                //enlazamos edittext del dialogo
                final TextView titulo = (TextView) dialog.findViewById(R.id.ap_tv_title_dialogo_eva);
                final EditText nombre = (EditText) dialog.findViewById(R.id.ap_edt_nombre_eva);
                final EditText duracion = (EditText) dialog.findViewById(R.id.ap_edt_duracion_eva);
                final EditText intento = (EditText) dialog.findViewById(R.id.ap_edt_num_intento_eva);
                final EditText desc = (EditText) dialog.findViewById(R.id.ap_edt_desc_eva);
                //final CheckBox retroceso = (CheckBox) dialog.findViewById(R.id.ap_cb_retroceder);

                Button btCrear = (Button) dialog.findViewById(R.id.d_agregar_eva);
                Button btCancelar = (Button) dialog.findViewById(R.id.d_cancelar_eva);

                btCrear.setText(v.getResources().getString(R.string.btn_guardar));
                titulo.setText(v.getResources().getString(R.string.mt_editar));

                //seteamos ediitext y checkbox con los valores que tiene el contacto
                nombre.setText(evaluacion.getNombre());
                duracion.setText("" + evaluacion.getDuracion());
                intento.setText("" + evaluacion.getCantIntento());
                desc.setText(evaluacion.getDescripcion());
                /*if (evaluacion.getRetroceder() == 1) {
                    retroceso.setChecked(true);
                } else {
                    retroceso.setChecked(false);
                }*/

                //programamos botones de crear-guardar y cancelar

                btCrear.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        /*int retro = 0;
                        if (retroceso.isChecked()) {
                            retro = 1;
                        }*/
                        if (!duracion.getText().toString().isEmpty() && !intento.getText().toString().isEmpty()
                                && !nombre.getText().toString().isEmpty()) {
                            try {
                                evaluacion = new Evaluacion(
                                        getIdEvaluacion(),
                                        getIdCarga(),
                                        Integer.parseInt(duracion.getText().toString()),
                                        Integer.parseInt(intento.getText().toString()),
                                        nombre.getText().toString(),
                                        desc.getText().toString()
                                );
                                //editamos registro
                                daoEvaluacion.Editar(evaluacion);
                                //refrescamos la lista
                                evaluaciones = daoEvaluacion.verTodos(getIdCarga());
                                //como ya estamos dentro de la clase adaptador simplemente ejecutamos el metodo
                                notifyDataSetChanged();
                                //cerramos el dialogo
                                dialog.dismiss();


                            } catch (Exception e) {
                                Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(
                                    v.getContext(),
                                    v.getResources().getString(R.string.ap_debes01),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                //final de guardar

                btCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                //final de cancelar
            }
        });
        //final de editar

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogo de eliminar

                //recuperamos la posicion del registro
                int pos = Integer.parseInt(v.getTag().toString());

                //recuperamos el contacto de la lista y seteamos id
                evaluacion = evaluaciones.get(pos);
                setIdEvaluacion(evaluacion.getId());
                setIdCarga(evaluacion.getIdCargaAcad());

                //creamos Alertdialogo

                AlertDialog.Builder delete_emergente = new AlertDialog.Builder(activity);
                delete_emergente.setMessage(v.getResources().getString(R.string.ap_delete_evaluación));
                delete_emergente.setCancelable(true);

                //Caso positivo

                delete_emergente.setPositiveButton(v.getResources().getString(R.string.si), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //elimina el registro, actualiza la lista y notifica el cambio al adaptador
                        daoEvaluacion.Eliminar(getIdEvaluacion());
                        evaluaciones = daoEvaluacion.verTodos(getIdCarga());
                        notifyDataSetChanged();

                    }
                });

                //Caso negativo

                delete_emergente.setNegativeButton(v.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // no esperamos que haga nada al cerrar, solo se cierra
                    }
                });
                delete_emergente.show(); //mostrar alerta


            }
        });
        //final de eliminar

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogo de info

                //recuperamos la posicion del registro
                int pos = Integer.parseInt(v.getTag().toString());

                //recuperamos el contacto de la lista y seteamos el id
                evaluacion = evaluaciones.get(pos);
                setIdEvaluacion(evaluacion.getId());

                //creamos dialogo
                final Dialog dialog = new Dialog(activity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//para quitar el titulo
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.vista_evaluacion);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                //enlazamos edittext del dialogo
                final TextView titulo = (TextView) dialog.findViewById(R.id.ap_tv_titulovista_eva);
                final TextView nombre = (TextView) dialog.findViewById(R.id.ap_tv_nombre_eva);
                final TextView duracion = (TextView) dialog.findViewById(R.id.ap_tv_duracion_eva);
                final TextView intento = (TextView) dialog.findViewById(R.id.ap_tv_num_intentos);
                final TextView desc = (TextView) dialog.findViewById(R.id.ap_tv_desc_eva);
                //final TextView retroceso = (TextView) dialog.findViewById(R.id.ap_tv_retroceder);

                Button btOk = (Button) dialog.findViewById(R.id.ap_btn_cerrar_vista_eva);

                //seteamos ediitext y checkbox con los valores que tiene el contacto
                nombre.setText(v.getResources().getString(R.string.ap_nombre_eva)+ " : " + evaluacion.getNombre());
                duracion.setText(v.getResources().getString(R.string.ap_duracion_eva) +" : "+ evaluacion.getDuracion());
                intento.setText(  v.getResources().getString(R.string.ap_num_intento_eva) + " : " + evaluacion.getCantIntento());
                desc.setText(v.getResources().getString(R.string.ap_desc_eva) + " : "+evaluacion.getDescripcion());
                /*if (evaluacion.getRetroceder() == 1) {
                    retroceso.setText("Se puede retroceder: SI");
                } else {
                    retroceso.setText("Se puede retroceder: NO");
                }*/

                btOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
        return view;
    }
}
