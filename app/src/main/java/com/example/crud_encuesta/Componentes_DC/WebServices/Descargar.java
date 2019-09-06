package com.example.crud_encuesta.Componentes_DC.WebServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.crud_encuesta.DatabaseAccess;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Descargar {

    private Context context;
    private SQLiteDatabase cx;
    private DatabaseAccess dba;

    public Descargar(Context context){
        this.context = context;
        dba = DatabaseAccess.getInstance(context);
    }

    public void descargar_turno(int turno_id, int estudiante_id){
        Toast.makeText(context, "Descargando...", Toast.LENGTH_LONG).show();
        cx = dba.open();

        RequestQueue request;
        JsonObjectRequest jsonObjectRequest;

        request = Volley.newRequestQueue(context);
        String url = "http://192.168.0.14:8000/api/evaluacion/turno/"+turno_id+"/obtener/"+estudiante_id;


        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    //Se procede a obtener la clave y almacenarla
                    JSONObject clave = response.getJSONObject("clave");

                    ContentValues contenedor_clave = new ContentValues();
                    contenedor_clave.put("ID_CLAVE", clave.getInt("id"));
                    contenedor_clave.put("ID_TURNO", clave.getInt("turno_id"));
                    contenedor_clave.put("ID_ENCUESTA", 1/*clave.getInt("encuesta_id")*/);
                    contenedor_clave.put("NUMERO_CLAVE", clave.getInt("numero_clave"));

                    cx.insert("CLAVE",null,contenedor_clave);

                    //Se procede a obtener el intento y almacenarlo
                    JSONObject intento = response.getJSONObject("intento");

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

                        ContentValues contenedor_opcion = new ContentValues();
                        contenedor_opcion.put("ID_OPCION", opcion.getInt("id"));
                        contenedor_opcion.put("ID_PREGUNTA", opcion.getInt("pregunta_id"));
                        contenedor_opcion.put("OPCION", opcion.getString("opcion"));
                        contenedor_opcion.put("CORRECTA", opcion.getInt("correcta"));

                        cx.insert("OPCION",null,contenedor_opcion);

                    }
                    dba.close();

                    Toast.makeText(context, "Exito! Se almaceno de manera correcta la evaluación.", Toast.LENGTH_SHORT).show();

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", ""+error.getMessage());
            }
        });

        request.add(jsonObjectRequest);
    }

    public void descargar_encuesta(int encuesta_id){
        Toast.makeText(context, "Ready para descargar encuesta", Toast.LENGTH_SHORT).show();
    }

}
