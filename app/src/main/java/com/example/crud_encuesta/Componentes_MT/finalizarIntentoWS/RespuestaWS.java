package com.example.crud_encuesta.Componentes_MT.finalizarIntentoWS;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RespuestaWS {
    Context context;
    int opcion_id;
    int pregunta_id;
    int intento_id;
    int total_preguntas;
    String texto_respuesta ="";
    int es_encuesta;
    int es_rc;
    private String URLline = "http://sigen.herokuapp.com/api/finalizar-intento";

    public RespuestaWS(Context context, int opcion_id, int pregunta_id, int intento_id, int total_preguntas, String texto_respuesta, int es_encuesta, int es_rc){
        this.context = context;
        this.opcion_id = opcion_id;
        this.pregunta_id = pregunta_id;
        this.intento_id = intento_id;
        this.total_preguntas = total_preguntas;
        this.texto_respuesta = texto_respuesta;
        this.es_encuesta = es_encuesta;
        this.es_rc = es_rc;

        respuesta();
    }

    private void respuesta(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLline,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(context,"Se insertó con éxito",Toast.LENGTH_LONG).show();
                        System.out.println(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Hubo un error :(",Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("pregunta_id", String.valueOf(pregunta_id));
                params.put("opcion_id", String.valueOf(opcion_id));
                params.put("intento_id", String.valueOf(intento_id));
                params.put("texto_respuesta",texto_respuesta);
                params.put("total_preguntas", String.valueOf(total_preguntas));
                params.put("es_encuesta", String.valueOf(es_encuesta));
                params.put("es_rc", String.valueOf(es_rc));

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}


