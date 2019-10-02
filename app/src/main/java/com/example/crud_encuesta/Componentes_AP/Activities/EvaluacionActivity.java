package com.example.crud_encuesta.Componentes_AP.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.crud_encuesta.Componentes_AP.Adapters.AdapterEvaluacion;
import com.example.crud_encuesta.Componentes_AP.DAO.DAOEvaluacion;
import com.example.crud_encuesta.Componentes_AP.DAO.DAOTurno;
import com.example.crud_encuesta.Componentes_AP.DAO.DAOUsuario;
import com.example.crud_encuesta.Componentes_AP.Models.Evaluacion;
import com.example.crud_encuesta.Componentes_AP.Models.Turno;
import com.example.crud_encuesta.Componentes_AP.Models.Usuario;
import com.example.crud_encuesta.Componentes_MR.Estudiante.Estudiante;
import com.example.crud_encuesta.Componentes_MT.Intento.IntentoActivity;
import com.example.crud_encuesta.MainActivity;
import com.example.crud_encuesta.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Vector;

public class EvaluacionActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {
    RequestQueue requestQueue;
    JsonObjectRequest jsonObjectRequest;
    DAOTurno daoTurno;
    ProgressDialog progressDialog;

    DAOEvaluacion daoEvaluacion;
    DAOUsuario daoUsuario;
    AdapterEvaluacion adapterEvaluacion;
    ArrayList<Evaluacion> evaluaciones;
    Evaluacion evaluacion;
    int id_materia = 0;
    int id_carga_academica =0;
    int id_estudiante =0;
    Usuario usuario;

    AutoCompleteTextView edt_buscar;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluacion);

        //recuperamos el id de la evaluación que nos mandan
        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null){
            id_materia = bundle.getInt("id_materia");
        }

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        daoTurno = new DAOTurno(this);
        daoUsuario = new DAOUsuario(this);
        daoEvaluacion = new DAOEvaluacion(this);

        usuario = daoUsuario.getUsuarioLogueado();

        if(usuario.getROL()==1){
            id_carga_academica = daoEvaluacion.getCargaAcademicaDocente(usuario.getIDUSUARIO(),id_materia);
        }
        if(usuario.getROL()==2){
            id_carga_academica = daoEvaluacion.getCargaAcademicaEstudiante(usuario.getIDUSUARIO(),id_materia);
            id_estudiante= daoEvaluacion.getIdEstudiante(usuario.getIDUSUARIO(),id_materia);
        }
        if(id_carga_academica==0){
            Toast.makeText(this,getResources().getText(R.string.ap_no_posee_carga),Toast.LENGTH_LONG).show();
        }

        /*
        *
        * LOGICA DE WEBSERVICES
        *
        * */
        //Si hay internet se va a realizar la consulta al ws
        if(isInternetAvailable()){
            progress("Cargando...");
            obtenerEvalucionesTurnos(id_carga_academica);
        }

        /*
         *
         * LOGICA DE WEBSERVICES
         *
         * */
        evaluaciones = daoEvaluacion.verTodos(id_carga_academica);
        adapterEvaluacion = new AdapterEvaluacion(evaluaciones,daoEvaluacion,this,this);

        //ImageView agregar = (ImageView) findViewById(R.id.ap_imgv_agregar_evaluacion);
        ImageView buscar = (ImageView) findViewById(R.id.ap_imgv_buscar_evaluacion);
        ImageView all = (ImageView) findViewById(R.id.ap_imgv_all_evaluacion);
        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.ap_fab_agregar_evaluacion);
        //final EditText edt_buscar = (EditText) findViewById(R.id.ap_edt_buscar_evaluacion);

        if(usuario.getROL()==0 || usuario.getROL()==2){
            add.setVisibility(View.INVISIBLE);
        }

        ListView listView = (ListView) findViewById(R.id.lista_evaluacion);
        listView.setAdapter(adapterEvaluacion);
        autoComplemento();

        //acceso a evaluación
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int id_evaluacion = evaluaciones.get(position).getId();

                Intent i = new Intent(view.getContext(), TurnoActivity.class);
                //enviamos parametro
                Bundle bundle = new Bundle();
                bundle.putInt("id_evaluacion",id_evaluacion);
                bundle.putInt("id_estudiante",id_estudiante);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
        //final de listener de listview

        //listener de agregar
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //dialogo de crear

                final Dialog dialog = new Dialog(EvaluacionActivity.this);
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

                btCrear.setText(getResources().getText(R.string.ap_agregar));
                titulo.setText(getResources().getText(R.string.ap_nuevo));

                //programamos botones de crear-guardar y cancelar

                btCrear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*int retro=0;
                        if (retroceso.isChecked()){
                            retro=1;
                        }*/
                        if(!duracion.getText().toString().isEmpty()&&!intento.getText().toString().isEmpty()
                        &&!nombre.getText().toString().isEmpty()) {

                            try {
                                evaluacion = new Evaluacion(
                                        id_carga_academica,
                                        Integer.parseInt(duracion.getText().toString()),
                                        Integer.parseInt(intento.getText().toString()),
                                        nombre.getText().toString(),
                                        desc.getText().toString()
                                );
                                //editamos registro
                                daoEvaluacion.Insertar(evaluacion);
                                //refrescamos la lista
                                evaluaciones = daoEvaluacion.verTodos(id_carga_academica);
                                //ejecutamos el metodo
                                adapterEvaluacion.notifyDataSetChanged();
                                //actualizamos autocompletado
                                autoComplemento();
                                //cerramos el dialogo
                                dialog.dismiss();

                            } catch (Exception e) {
                                Toast.makeText(getApplication(), getResources().getText(R.string.ap_error), Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(
                                    v.getContext(),
                                    getResources().getText(R.string.ap_debes_de_llenar),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                //final de agregar

                btCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                //final de cancelar

            }
        });
        //final de listener de agregar

        //listener de buscar
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edt_buscar.getText().toString().isEmpty()){
                    evaluaciones = daoEvaluacion.verUno(edt_buscar.getText().toString(),id_carga_academica);
                    adapterEvaluacion.notifyDataSetChanged();
                }else{
                    Toast.makeText(
                            v.getContext(),
                            getResources().getText(R.string.ap_debes_ingresar),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
        //final de listener de buscar


        //listener de all
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evaluaciones = daoEvaluacion.verTodos(id_carga_academica);
                adapterEvaluacion.notifyDataSetChanged();
            }
        });
        //final de listener de all
    }
    public void autoComplemento(){
        Vector<String> autocomplemento = new Vector<String>();
        for(int i =0;i<=evaluaciones.size()-1;i++){
            autocomplemento.add(evaluaciones.get(i).getNombre());
        }
        edt_buscar = (AutoCompleteTextView) findViewById(R.id.auto);
        ArrayAdapter<String> adapterComplemento = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                autocomplemento);
        edt_buscar.setAdapter(adapterComplemento);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, "Error: " + error,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        try{
            JSONArray jsonEvaluaciones = response.getJSONArray("evaluaciones");
            JSONArray jsonTurnos = response.getJSONArray("turnos");
            if(jsonEvaluaciones!= null && jsonTurnos != null){
            //elimina las evaluaciones y por medio de triggers se eliminan los turnos vinculados
            daoEvaluacion.EliminarSegunCarga(id_carga_academica);
            for (int i = 0; i < jsonEvaluaciones.length(); i++) {

                JSONObject evaluacion = jsonEvaluaciones.getJSONObject(i);
                Evaluacion newEvaluacion = new Evaluacion();
                newEvaluacion.setId(evaluacion.getInt("id"));
                newEvaluacion.setCantIntento(evaluacion.getInt("intentos"));
                newEvaluacion.setDescripcion(evaluacion.getString("descripcion_evaluacion"));
                newEvaluacion.setDuracion(evaluacion.getInt("duracion"));
                newEvaluacion.setNombre(evaluacion.getString("nombre_evaluacion"));
                newEvaluacion.setIdCargaAcad(evaluacion.getInt("id_carga"));

                daoEvaluacion.InsertarWS(newEvaluacion);
            }

            for (int j = 0; j < jsonTurnos.length(); j++) {

                JSONObject turno= jsonTurnos.getJSONObject(j);
                Turno newTurno = new Turno();
                newTurno.setId(turno.getInt("id"));
                newTurno.setContrasenia(turno.getString("contraseña"));
                newTurno.setIdEvaluacion(turno.getInt("evaluacion_id"));
                newTurno.setDateInicial(turno.getString("fecha_inicio_turno").replace("-","/"));
                newTurno.setDateFinal(turno.getString("fecha_final_turno").replace("-","/"));

                daoTurno.InsertarWS(newTurno);
            }
                //refrescamos la lista
                evaluaciones = daoEvaluacion.verTodos(id_carga_academica);
                //ejecutamos el metodo
                adapterEvaluacion.notifyDataSetChanged();
                progressDialog.cancel();

            }else{
                Toast.makeText(this, "NO hay evaluaciones disponibles",Toast.LENGTH_LONG).show();
            }

        }catch(Exception e){
            Toast.makeText(this, "Error: por favor vuelve a ingresar",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public boolean isInternetAvailable() {
        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            return p.waitFor() == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public void obtenerEvalucionesTurnos(int id_carga_academica) {
        String url = "http://sigen.herokuapp.com/api/evaluaciones_m/turnos_m/"+id_carga_academica;
        jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                this,
                this);
        requestQueue.add(jsonObjectRequest);
    }

    public void progress(String mensaje){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(mensaje);
        progressDialog.show();
    }
}
