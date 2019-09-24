package com.example.crud_encuesta.Componentes_EL.WebService;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.crud_encuesta.Componentes_EL.Materia.Materia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WS_Materia implements Response.Listener<JSONArray>,Response.ErrorListener {

    private String url;
    private Context context;
    private JsonArrayRequest jsonArrayRequest;
    private RequestQueue requestQueue;
    private ArrayList<Materia> materias;

    public WS_Materia(int id, Context context) {
        //Inicializando valores necesarios para el WS
        this.url = "http://sigen.herokuapp.com/api/materias/estudiante/"+id;
        this.context=context;
        this.requestQueue= Volley.newRequestQueue(context);
        this.materias=new ArrayList<>();
        this.jsonArrayRequest=new JsonArrayRequest(Request.Method.GET,url,null,this,this);
    }

    public ArrayList<Materia> getMateriasWS(){
        requestQueue.add(jsonArrayRequest);
        return materias;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(context,"Error: "+error.toString(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONArray response) {
        Materia materia;
        try {
            for (int i=0;i<response.length();i++){
                JSONObject jsonObject=null;
                    jsonObject=response.getJSONObject(i);
                    materia=new Materia();
                    materia.setId(jsonObject.getInt("id_cat_mat"));
                    materia.setCodigo_materia(jsonObject.optString("codigo_mat"));
                    materia.setNombre(jsonObject.optString("nombre_mar"));
                    materia.setElectiva(jsonObject.optInt("es_electiva"));
                    materia.setMaximo_preguntas(jsonObject.optInt("maximo_cant_preguntas"));
                    materias.add(materia);
            }
        } catch (JSONException e) {
            Log.d("Error", e.toString()+"PUTO");
        }
    }
}
