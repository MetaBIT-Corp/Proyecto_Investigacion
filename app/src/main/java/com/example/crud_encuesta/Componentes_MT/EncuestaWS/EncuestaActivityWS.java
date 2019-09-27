package com.example.crud_encuesta.Componentes_MT.EncuestaWS;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.example.crud_encuesta.DatabaseAccess;
import com.example.crud_encuesta.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EncuestaActivityWS extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {
    RequestQueue requestQueue;
    JsonObjectRequest jsonObjectRequest;
    ProgressDialog progressDialog;
    List<EncuestaWS> encuestasWS = new ArrayList<>();
    List<EncuestaWS> encuestasDescargadas = new ArrayList<>();
    List<EncuestaWS> encuestas = new ArrayList<>();
    EncuestaAdapterWS encuestaAdapterWS;
    DAOEncuestaWS daoEncuestaWS;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encuesta_ws);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        listView = findViewById(R.id.ls_encuesta_ws);
        daoEncuestaWS = new DAOEncuestaWS(this);

        encuestasDescargadas = daoEncuestaWS.encuestasDescargadas();

        if(accesoInternet()){
            progress("Cargando... ");
            getEncuestasVigentes();
        }else{
            encuestaAdapterWS = new EncuestaAdapterWS(this, encuestasDescargadas, daoEncuestaWS);
            listView.setAdapter(encuestaAdapterWS);
        }
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
                encuestaWS.setLocal(false);

                encuestasWS.add(encuestaWS);

            }
        }catch(Exception e){
            e.printStackTrace();
        }

        for (EncuestaWS ews : encuestasWS){
            Boolean agregado = false;

            for (EncuestaWS elocal : encuestasDescargadas){
                if(elocal.id == ews.id){
                    encuestas.add(elocal);
                    agregado = true;
                }
            }
            if(!agregado) encuestas.add(ews);
        }

        progressDialog.cancel();
        encuestaAdapterWS = new EncuestaAdapterWS(this, encuestas, daoEncuestaWS, encuestasWS);
        listView.setAdapter(encuestaAdapterWS);
    }

    public void getEncuestasVigentes(){
        String url = "http://sigen.herokuapp.com/api/encuestas-disponibles";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        requestQueue.add(jsonObjectRequest);
    }

    public void progress(String mensaje){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(mensaje);
        progressDialog.show();
    }

    public List<EncuestaWS> encuestasDescargadas(){
        List<EncuestaWS> encuestas_local = new ArrayList<>();
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        SQLiteDatabase db = databaseAccess.open();
        String fecha = fecha_actual();

        try{
            Cursor cursor = db.rawQuery("SELECT * FROM ENCUESTA WHERE FECHA_FINAL_ENCUESTA > '"+fecha+"'", null);

            while (cursor.moveToNext()){
                EncuestaWS encuesta = new EncuestaWS();

                encuesta.setId(cursor.getInt(0));
                encuesta.setId_docente(cursor.getInt(1));
                encuesta.setTitulo_encuesta(cursor.getString(2));
                encuesta.setDescriion_encuesta(cursor.getString(3));
                encuesta.setFecha_inicio_encuesta(cursor.getString(4));
                encuesta.setFecha_final_encuesta(cursor.getString(5));
                encuesta.setLocal(true);

                encuestas_local.add(encuesta);
            }

            db.close();

        }catch (Exception  e){
            e.printStackTrace();
            db.close();
        }
        return encuestas_local;
    }

    public String fecha_actual() {
        Date date = new Date();
        DateFormat fechaHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String convertido = fechaHora.format(date);

        return convertido;
    }

    public boolean accesoInternet(){
        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");

            int val = p.waitFor();
            boolean accesible = (val == 0);
            return accesible;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
