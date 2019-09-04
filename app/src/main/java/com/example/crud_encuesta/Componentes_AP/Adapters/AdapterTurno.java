package com.example.crud_encuesta.Componentes_AP.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.crud_encuesta.Componentes_AP.DAO.DAOTurno;
import com.example.crud_encuesta.Componentes_AP.DAO.DAOUsuario;
import com.example.crud_encuesta.Componentes_AP.Models.Turno;
import com.example.crud_encuesta.Componentes_AP.Models.Usuario;
import com.example.crud_encuesta.Componentes_DC.Dao.DaoOpcion;
import com.example.crud_encuesta.Componentes_MT.Clave.ClaveActivity;
import com.example.crud_encuesta.DatabaseAccess;
import com.example.crud_encuesta.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterTurno extends BaseAdapter {
    private int anio, mes, dia, hora, minuto;
    private SQLiteDatabase cx;
    private DatabaseAccess dba;

    ArrayList<Turno> turnos = new ArrayList<>();
    Turno turno;
    DAOTurno daoTurno;
    Activity activity; //la actividad donde va a mostrarse el listview
    Context context;


    //variables que nos ayudara a manejar los id de turno y de valauacion
    int idTurno = 0;
    int idEvaluacion = 0;


    //constructos del adapter
    public AdapterTurno(ArrayList<Turno> turnos, DAOTurno daoTurno, Activity activity,Context context) {
        this.turnos = turnos;
        this.daoTurno = daoTurno;
        this.activity = activity;
        this.context = context;
        this.dba = DatabaseAccess.getInstance(this.context);
        //cx = dba.open();

    }

    //getter y setter
    public int getIdTurno() {
        return idTurno;
    }

    public void setIdTurno(int idTurno) {
        this.idTurno = idTurno;
    }

    public int getIdEvaluacion() {
        return idEvaluacion;
    }

    public void setIdEvaluacion(int idEvaluacion) {
        this.idEvaluacion = idEvaluacion;
    }



    //metodos

    @Override
    public int getCount() {
        return turnos.size();
    }

    @Override
    public Turno getItem(int position) {
        return turnos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return turnos.get(position).getId();
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
        turno = turnos.get(position);

        TextView tv_item = (TextView) view.findViewById(R.id.ap_tv_item);
        ImageView editar = (ImageView) view.findViewById(R.id.ap_editar_item);
        ImageView eliminar = (ImageView) view.findViewById(R.id.ap_eliminar_item);
        ImageView info = (ImageView) view.findViewById(R.id.ap_info_item);
        ImageView turnoi = (ImageView) view.findViewById(R.id.ap_turno_item);
        ImageView descargar = (ImageView) view.findViewById(R.id.btn_descargar);

        String fechainicial = turno.getDateInicial();
        String[] pfechainicial = fechainicial.split(" ");

        String fechafinal = turno.getDateFinal();
        String[] pfechafinal = fechafinal.split(" ");

        tv_item.setText("Id: " + turno.getId() + " " + pfechainicial[0] + " - " + pfechafinal[0]);


        //ocultados de acuerdo a rol
        DAOUsuario daoUsuario = new DAOUsuario(context);
        Usuario usuario = daoUsuario.getUsuarioLogueado();
        if(usuario.getROL()== 0 || usuario.getROL()==2){
            editar.setVisibility(View.INVISIBLE);
            eliminar.setVisibility(View.INVISIBLE);
            turnoi.setVisibility(View.INVISIBLE);
            info.setVisibility(View.INVISIBLE);
        }

        //utilizamos setTag para que al presionar editar o eliminar, android sepa cuál registro queremos afectar
        editar.setTag(position);
        eliminar.setTag(position);
        info.setTag(position);
        turnoi.setTag(position);

        descargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(activity, "Descargando...", Toast.LENGTH_LONG).show();
                cx = dba.open();

                RequestQueue request;
                JsonObjectRequest jsonObjectRequest;

                request = Volley.newRequestQueue(v.getContext());
                String url = "http://192.168.0.12:8000/api/evaluacion/turno/1/obtener/1";


                jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            //Se procede a obtener la clave y almacenarla
                            JSONObject clave = response.getJSONObject("clave");

                            Log.d("Clave",""+clave.getInt("id"));

                            ContentValues contenedor_clave = new ContentValues();
                            contenedor_clave.put("ID_CLAVE", clave.getInt("id"));
                            contenedor_clave.put("ID_TURNO", clave.getInt("turno_id"));
                            contenedor_clave.put("ID_ENCUESTA", 1/*clave.getInt("encuesta_id")*/);
                            contenedor_clave.put("NUMERO_CLAVE", clave.getInt("numero_clave"));

                            cx.insert("CLAVE",null,contenedor_clave);

                            //Se procede a obtener el intento y almacenarlo
                            JSONObject intento = response.getJSONObject("intento");

                            Log.d("Intento",""+intento.getInt("id"));

                            ContentValues contenedor_intento = new ContentValues();
                            contenedor_intento.put("ID_INTENTO", intento.getInt("id"));
                            contenedor_intento.put("ID_EST", intento.getInt("estudiante_id"));
                            contenedor_intento.put("ID_CLAVE", intento.getInt("clave_id"));
                            contenedor_intento.put("ID", ""/*intento.getString("")*/);
                            contenedor_intento.put("FECHA_INICIO_INTENTO", intento.getString("fecha_inicio_intento"));

                            cx.insert("INTENTO",null,contenedor_intento);

                            //Se procede a obtener las clave_areas, recorerlas y almacenarlas
                            JSONArray clave_areas = response.getJSONArray("clave_areas");
                            for (int i = 0; i < clave_areas.length(); i++) {

                                JSONObject clave_area = clave_areas.getJSONObject(i);

                                Log.d("Clave_Area",""+clave_area.getInt("id"));

                                ContentValues contenedor_clave_area = new ContentValues();
                                contenedor_clave_area.put("ID_CLAVE_AREA", clave_area.getInt("id"));
                                contenedor_clave_area.put("ID_AREA", clave_area.getInt("area_id"));
                                contenedor_clave_area.put("ID_CLAVE", clave_area.getInt("clave_id"));
                                contenedor_clave_area.put("NUMERO_PREGUNTAS", clave_area.getInt("numero_preguntas"));
                                contenedor_clave_area.put("ALEATORIO", clave_area.getInt("aleatorio"));
                                contenedor_clave_area.put("PESO", clave_area.getInt("peso"));

                                cx.insert("CLAVE_AREA",null,contenedor_clave_area);
                            }

                            //Se procede a obtener las areas, recorerlas y almacenarlas
                            JSONArray areas = response.getJSONArray("areas");
                            for (int i = 0; i < areas.length(); i++) {

                                JSONObject area = areas.getJSONObject(i);

                                Log.d("Area",""+area.getInt("id"));

                                ContentValues contenedor_area = new ContentValues();
                                contenedor_area.put("ID_AREA", area.getInt("id"));
                                contenedor_area.put("ID_CAT_MAT", area.getInt("id_cat_mat"));
                                contenedor_area.put("ID_PDG_DCN", area.getInt("id_pdg_dcn"));
                                contenedor_area.put("ID_TIPO_ITEM", area.getInt("tipo_item_id"));
                                contenedor_area.put("TITULO", area.getString("titulo"));

                                cx.insert("AREA",null,contenedor_area);
                            }

                            //Se procede a obtener las clave_area_preguntas, recorerlas y almacenarlas
                            JSONArray clave_area_preguntas = response.getJSONArray("clave_area_preguntas");
                            for (int i = 0; i < clave_area_preguntas.length(); i++) {

                                JSONObject clave_area_pregunta = clave_area_preguntas.getJSONObject(i);

                                Log.d("Clave_Area_Pregunta",""+clave_area_pregunta.getInt("id"));

                                ContentValues contenedor_clave_area_pregunta = new ContentValues();
                                contenedor_clave_area_pregunta.put("ID_CLAVE_AREA_PRE", clave_area_pregunta.getInt("id"));
                                contenedor_clave_area_pregunta.put("ID_PREGUNTA", clave_area_pregunta.getInt("pregunta_id"));
                                contenedor_clave_area_pregunta.put("ID_CLAVE_AREA", clave_area_pregunta.getInt("clave_area_id"));

                                cx.insert("CLAVE_AREA_PREGUNTA",null,contenedor_clave_area_pregunta);

                            }

                            //Se procede a obtener los grupos de emparejamiento, recorerlos y almacenarlos
                            JSONArray grupos_emp = response.getJSONArray("grupos_emp");
                            for (int i = 0; i < grupos_emp.length(); i++) {

                                JSONObject grupo_emp = grupos_emp.getJSONObject(i);

                                Log.d("Grupo_Emp",""+grupo_emp.getInt("id"));

                                ContentValues contenedor_grupo_emp = new ContentValues();
                                contenedor_grupo_emp.put("ID_GRUPO_EMP", grupo_emp.getInt("id"));
                                contenedor_grupo_emp.put("ID_AREA", grupo_emp.getInt("area_id"));
                                contenedor_grupo_emp.put("DESCRIPCION_GRUPO_EMP", grupo_emp.getString("descripcion_grupo_emp"));

                                cx.insert("GRUPO_EMPAREJAMIENTO",null,contenedor_grupo_emp);
                            }

                            //Se procede a obtener las preguntas, recorerlas y almacenarlas
                            JSONArray preguntas = response.getJSONArray("preguntas");
                            for (int i = 0; i < preguntas.length(); i++) {

                                JSONObject pregunta = preguntas.getJSONObject(i);

                                Log.d("Preguntas",""+pregunta.getInt("id"));

                                ContentValues contenedor_pregunta = new ContentValues();
                                contenedor_pregunta.put("ID_PREGUNTA", pregunta.getInt("id"));
                                contenedor_pregunta.put("ID_GRUPO_EMP", pregunta.getInt("grupo_emparejamiento_id"));
                                contenedor_pregunta.put("PREGUNTA", pregunta.getString("pregunta"));

                                cx.insert("PREGUNTA",null,contenedor_pregunta);
                            }

                            //Se procede a obtener las opciones, recorerlas y almacenarlas
                            JSONArray opciones = response.getJSONArray("opciones");
                            for (int i = 0; i < opciones.length(); i++) {

                                JSONObject opcion = opciones.getJSONObject(i);

                                Log.d("Opciones",""+opcion.getInt("id"));

                                ContentValues contenedor_opcion = new ContentValues();
                                contenedor_opcion.put("ID_OPCION", opcion.getInt("id"));
                                contenedor_opcion.put("ID_PREGUNTA", opcion.getInt("pregunta_id"));
                                contenedor_opcion.put("OPCION", opcion.getString("opcion"));
                                contenedor_opcion.put("CORRECTA", opcion.getInt("correcta"));

                                cx.insert("OPCION",null,contenedor_opcion);

                            }
                            dba.close();
                            Log.d("Exito", "Se almaceno de manera correcta la evaluación");

                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", ""+error.toString());
                    }
                });

                request.add(jsonObjectRequest);
            }
        });

        //TODO: OnCLICKLISTENER DE OPCIONES
        //inicio de listener editar
        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogo de editar

                //recuperamos la posicion del registro
                int pos = Integer.parseInt(v.getTag().toString());

                //recuperamos el registro de la lista y seteamos el id
                turno = turnos.get(pos);
                setIdTurno(turno.getId());
                setIdEvaluacion(turno.getIdEvaluacion());

                //creamos dialogo
                final Dialog dialog = new Dialog(activity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//para quitar el titulo
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialogo_turno);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                dialog.show();

                //enlazamos views del dialogo turno
                final TextView titulo = (TextView) dialog.findViewById(R.id.ap_tv_title_dialogo_turno);
                final EditText dateinicial = (EditText) dialog.findViewById(R.id.ap_edt_dateinicial);
                final EditText datefinal = (EditText) dialog.findViewById(R.id.ap_edt_datefinal);
                final EditText timeinicial = (EditText) dialog.findViewById(R.id.ap_edt_timeinicial);
                final EditText timefinal = (EditText) dialog.findViewById(R.id.ap_edt_timefinal);
                final EditText contrasenia = (EditText) dialog.findViewById(R.id.ap_edt_pass_turno);
                final CheckBox visible = (CheckBox) dialog.findViewById(R.id.ap_cb_visible);

                Button btCrear = (Button) dialog.findViewById(R.id.d_agregar_turno);
                Button btCancelar = (Button) dialog.findViewById(R.id.d_cancelar_turno);
                ImageView imv_dateinicial = (ImageView) dialog.findViewById(R.id.ap_imv_dateinicial);
                ImageView imv_timeinicial = (ImageView) dialog.findViewById(R.id.ap_imv_timeinicial);
                ImageView imv_datefinal = (ImageView) dialog.findViewById(R.id.ap_imv_datefinal);
                ImageView imv_timefinal = (ImageView) dialog.findViewById(R.id.ap_imv_timefinal);

                //;)
                String fechainicial = turno.getDateInicial();
                String[] pfechainicial = fechainicial.split(" ");

                String fechafinal = turno.getDateFinal();
                String[] pfechafinal = fechafinal.split(" ");

                //seteamos valores de views
                btCrear.setText(v.getResources().getString(R.string.btn_guardar));
                titulo.setText(v.getResources().getString(R.string.mt_editar));
                dateinicial.setText(pfechainicial[0]);
                datefinal.setText(pfechafinal[0]);
                timeinicial.setText(pfechainicial[1]);
                timefinal.setText(pfechafinal[1]);
                contrasenia.setText(turno.getContrasenia());
                if (turno.getVisible() == 1) {
                    visible.setChecked(true);
                } else {
                    visible.setChecked(false);
                }

                //programamos metodo crear y cancelar y pickers

                imv_dateinicial.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        datePicker(dateinicial, v.getContext());
                    }
                });

                imv_timefinal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timepicker(timeinicial, v.getContext());
                    }
                });

                imv_datefinal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        datePicker(datefinal, v.getContext());
                    }
                });

                imv_timefinal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timepicker(timefinal, v.getContext());
                    }
                });


                //Inicio listener de crear
                btCrear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int ver = 0;
                        if (visible.isChecked()) {
                            ver = 1;
                        }
                        if (!dateinicial.getText().toString().isEmpty() && !datefinal.getText().toString().isEmpty()
                                && !timeinicial.getText().toString().isEmpty() && !timefinal.getText().toString().isEmpty()
                                && !contrasenia.getText().toString().isEmpty()) {
                            if(validarFecha(dateinicial.getText().toString(),
                                    timeinicial.getText().toString(),
                                    datefinal.getText().toString(),
                                    timefinal.getText().toString())) {
                                try {
                                    turno = new Turno(
                                            getIdTurno(),
                                            getIdEvaluacion(),
                                            dateinicial.getText().toString() + " " + timeinicial.getText().toString(),
                                            datefinal.getText().toString() + " " + timefinal.getText().toString(),
                                            ver,
                                            contrasenia.getText().toString()
                                    );
                                    //editamos registro
                                    daoTurno.Editar(turno);
                                    //refrescamos la lista
                                    turnos = daoTurno.verTodos(getIdEvaluacion());
                                    //como ya estamos dentro de la clase adaptador simplemente ejecutamos el metodo
                                    notifyDataSetChanged();
                                    //cerramos el dialogo
                                    dialog.dismiss();


                                } catch (Exception e) {
                                    Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(
                                        v.getContext(),
                                        v.getResources().getString(R.string.ap_fecha_no_valida),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(
                                    v.getContext(),
                                    v.getResources().getString(R.string.rellene_v),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                //Final listener de crear

                //Inicio de listener cancelar
                btCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                //final de listener cancelar
            }
        });
        //final de listener editar

        //Inicio de listener eliminar
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //dialogo de eliminar

                //recuperamos la posicion del registro
                int pos = Integer.parseInt(v.getTag().toString());

                //recuperamos el contacto de la lista y seteamos id
                turno = turnos.get(pos);
                setIdTurno(turno.getId());
                setIdEvaluacion(turno.getIdEvaluacion());

                //creamos Alertdialogo

                AlertDialog.Builder delete_emergente = new AlertDialog.Builder(activity);
                delete_emergente.setMessage(v.getResources().getString(R.string.ap_delete_turno));
                delete_emergente.setCancelable(true);

                //Caso positivo

                delete_emergente.setPositiveButton(v.getResources().getString(R.string.si), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //elimina el registro, actualiza la lista y notifica el cambio al adaptador
                        daoTurno.Eliminar(getIdTurno());
                        turnos = daoTurno.verTodos(getIdEvaluacion());
                        notifyDataSetChanged();

                    }
                });

                //Caso negativo

                delete_emergente.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // no esperamos que haga nada al cerrar, solo se cierra
                    }
                });
                delete_emergente.show(); //mostrar alerta
            }
        });
        //Final de listener eliminar

        //Inicio de listener info
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //dialogo de info
                //recuperamos la posicion del registro
                int pos = Integer.parseInt(v.getTag().toString());

                //recuperamos el contacto de la lista y seteamos el id
                turno = turnos.get(pos);
                setIdTurno(turno.getId());

                //creamos dialogo
                final Dialog dialog = new Dialog(activity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//para quitar el titulo
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.vista_turno);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                //enlazamos views del dialogo turno
                final TextView tv_dateinicial = (TextView) dialog.findViewById(R.id.ap_tv_dateinit_turno);
                final TextView tv_datefinal = (TextView) dialog.findViewById(R.id.ap_tv_datefinish_turno);
                final TextView tv_pass = (TextView) dialog.findViewById(R.id.ap_tv_pass_turno);
                final TextView tv_visible = (TextView) dialog.findViewById(R.id.ap_tv_visible_turno);

                Button btn_OK = (Button) dialog.findViewById(R.id.ap_btn_cerrar_vista_turno);

                //seteamos views con los valores que tiene el registro
                tv_dateinicial.setText(v.getResources().getString(R.string.ap_dateinit) + " : " +turno.getDateInicial());
                tv_datefinal.setText(v.getResources().getString(R.string.ap_datefinish) +" : "+ turno.getDateFinal());
                tv_pass.setText(v.getResources().getString(R.string.ap_hint_password) + " : " + turno.getContrasenia());
                if (turno.getVisible() == 1) {
                    tv_visible.setText("Visible: "+ v.getResources().getString(R.string.si));
                } else {
                    tv_visible.setText("Visible: "+ v.getResources().getString(R.string.no));
                }

                btn_OK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        turnoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //recuperamos la posicion del registro
                int pos = Integer.parseInt(v.getTag().toString());
                int id_turno = turnos.get(pos).getId();

                Intent intent = new Intent(context, ClaveActivity.class);
                //enviamos parametro
                intent.putExtra("id_turno",id_turno);
                context.startActivity(intent);
            }
        });
        //final de listener info
        return view;
    }

    public void datePicker(final EditText editText, Context context) {

        final Calendar c = Calendar.getInstance();
        dia = c.get(Calendar.DAY_OF_MONTH);
        mes = c.get(Calendar.MONTH);
        anio = c.get(Calendar.YEAR);


        //creamos instancia de clase DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editText.setText(year + "/" + (month + 1) + "/" + dayOfMonth);
                    }
                },
                anio,
                mes,
                dia
        );
        datePickerDialog.show();
    }

    public void timepicker(final EditText editText, Context context) {
        final Calendar c = Calendar.getInstance();
        hora=c.get(Calendar.HOUR_OF_DAY);
        minuto=c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        editText.setText(hourOfDay + ":" + minute);
                    }
                },
                hora,
                minuto,
                false
        );
        timePickerDialog.show();

    }

    public Boolean validarFecha(String di, String ti, String df, String tf){
        Boolean validado = false;
        int anioi,mesi,diai,horai,minutoi;
        int aniof,mesf,diaf,horaf,minutof;

        anioi = Integer.parseInt(di.split("/")[0]);
        mesi = Integer.parseInt(di.split("/")[1]);
        diai = Integer.parseInt(di.split("/")[2]);

        aniof = Integer.parseInt(df.split("/")[0]);
        mesf = Integer.parseInt(df.split("/")[1]);
        diaf = Integer.parseInt(df.split("/")[2]);

        horai = Integer.parseInt(ti.split(":")[0]);
        minutoi =  Integer.parseInt(ti.split(":")[1]);

        horaf = Integer.parseInt(tf.split(":")[0]);
        minutof =  Integer.parseInt(tf.split(":")[1]);

        if (aniof>anioi){
            validado=true;
        }
        if (aniof==anioi){
            if (mesf>mesi){
                validado = true;
            }
            if (mesf==mesi){
                if(diaf>diai){
                    validado=true;
                }
                if(diaf==diai){
                    if(horaf>horai){
                        validado = true;
                    }
                    if(horaf==horai){
                        if(minutof>=minutoi){
                            validado = true;
                        }
                    }
                }
            }
        }

        return  validado;
    }

}