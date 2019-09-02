package com.example.crud_encuesta.Componentes_MT.EncuestaWS;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.crud_encuesta.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EncuestaActivityWS extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {
    RequestQueue requestQueue;
    JsonObjectRequest jsonObjectRequest;
    ProgressDialog progressDialog;
    List<EncuestaWS> encuestasWS = new ArrayList<>();
    EncuestaAdapterWS encuestaAdapterWS;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encuesta_ws);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        listView = findViewById(R.id.ls_encuesta_ws);

        progress("Cargando... ");
        getEncuestasVigentes();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        progressDialog.cancel();
        Toast.makeText(this, "Hubo un error al intentar recuperar los datos. Prueba tu conexión a internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {

        try{
            JSONArray jsonArray = response.optJSONArray("encuestas");
            for (int i=0; i<jsonArray.length(); i++){
                EncuestaWS encuestaWS = new EncuestaWS();
                JSONObject encuesta_object = jsonArray.getJSONObject(i);

                encuestaWS.setId(encuesta_object.getInt("id"));
                encuestaWS.setId_docente(encuesta_object.getInt("id_docente"));
                encuestaWS.setTitulo_encuesta(encuesta_object.getString("titulo_encuesta"));
                encuestaWS.setDescriion_encuesta(encuesta_object.getString("descripcion_encuesta"));
                encuestaWS.setFecha_inicio_encuesta(encuesta_object.getString("fecha_inicio_encuesta"));
                encuestaWS.setFecha_final_encuesta(encuesta_object.getString("fecha_final_encuesta"));

                encuestasWS.add(encuestaWS);

            }
        }catch(Exception e){
            e.printStackTrace();
        }

        progressDialog.cancel();
        encuestaAdapterWS = new EncuestaAdapterWS(this, encuestasWS);
        listView.setAdapter(encuestaAdapterWS);
    }

    public void getEncuestasVigentes(){
        String url = "http://192.168.1.3:8000/api/encuestas-disponibles";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        requestQueue.add(jsonObjectRequest);
    }

    public void progress(String mensaje){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(mensaje);
        progressDialog.show();
    }
}
