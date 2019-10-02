package com.example.crud_encuesta.Componentes_EL.Materia;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.crud_encuesta.Componentes_EL.EstructuraTablas;
import com.example.crud_encuesta.Componentes_EL.Operaciones_CRUD;
import com.example.crud_encuesta.DatabaseAccess;
import com.example.crud_encuesta.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MateriaUsersActivity extends AppCompatActivity implements Response.Listener<JSONArray>,Response.ErrorListener{
    SQLiteDatabase db;
    DatabaseAccess access;
    ContentValues contentValues;
    ListView listView;
    MateriaUserAdapter adapter;
    ArrayList<Materia> listaMateria = new ArrayList<>();
    String url;

    private JsonArrayRequest jsonArrayRequest;
    private RequestQueue requestQueue;
    ProgressDialog progressDialog;
    int id;
    int rol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        access = DatabaseAccess.getInstance(MateriaUsersActivity.this);
        db = access.open();
        requestQueue= Volley.newRequestQueue(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materia_users);
        rol=getIntent().getExtras().getInt("rol_user");
        id=getIntent().getExtras().getInt("id_user");

        LinearLayout l=findViewById(R.id.linearBusqueda);
        l.setVisibility(View.GONE);

        listView = findViewById(R.id.list_view_base);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando Materias...");
        progressDialog.show();

        //Se verifica si hay conexion a internet
        if (! isInternetAvailable()){
            listaMateria=Operaciones_CRUD.todosMateria(db,rol,id);
            if(listaMateria.size()==0){
                Toast.makeText(MateriaUsersActivity.this,"Necesitas internet para acceder a tur materias por primera vez.",Toast.LENGTH_LONG).show();
            }
            adapter=new MateriaUserAdapter(MateriaUsersActivity.this,listaMateria,db,this,id,rol);
            listView.setAdapter(adapter);
            progressDialog.cancel();
        }else{
            //Llamada al WS
            //Ahorita se ha dejado 7 porque ese es el id del usuario jose
            getMateriasWS(id);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        //Toast.makeText(MateriaUsersActivity.this,"Error: "+error.toString(),Toast.LENGTH_SHORT).show();
        Log.d("ERROR",error.toString());
        progressDialog.cancel();
    }

    @Override
    public void onResponse(JSONArray response) {
        Materia materia;
        try {
            db.delete(EstructuraTablas.MATERIA_TABLA_NAME,null,null);
            db.delete("carga_academica",null,null);
            db.delete("materia_ciclo",null,null);
            db.delete("detalleinscest",null,null);
            for (int i=0;i<response.length();i++){
                JSONObject jsonObject;
                jsonObject=response.getJSONObject(i);

                //Insertando datos en la tabla Materias
                ContentValues c=new ContentValues();
                c.put(EstructuraTablas.COL_3_MATERIA, jsonObject.optString("codigo_mat"));
                c.put(EstructuraTablas.COL_4_MATERIA, jsonObject.optString("nombre_mar"));
                c.put(EstructuraTablas.COL_5_MATERIA, jsonObject.optInt("es_electiva"));
                c.put(EstructuraTablas.COL_6_MATERIA, jsonObject.optInt("maximo_cant_preguntas"));
                Operaciones_CRUD.insertar(db,c,MateriaUsersActivity.this, EstructuraTablas.MATERIA_TABLA_NAME);

                //Actualizando id de cada materia para que quede como el del servidor
                String sql="UPDATE "+EstructuraTablas.MATERIA_TABLA_NAME+" SET "+EstructuraTablas.COL_0_MATERIA+"="
                        +jsonObject.optInt("id_cat_mat")+" WHERE "+
                        EstructuraTablas.COL_3_MATERIA+"='"+jsonObject.optString("codigo_mat")+"'";
                db.execSQL(sql);

                //Agregando materia ciclo y ciclo
                c=new ContentValues();
                c.put("id_cat_mat",jsonObject.optInt("id_cat_mat"));
                c.put("ciclo",jsonObject.optInt("num_ciclo"));
                c.put("anio",jsonObject.optInt("anio"));
                Operaciones_CRUD.insertar(db,c,this,"materia_ciclo");

                sql="UPDATE materia_ciclo set id_mat_ci="+jsonObject.optString("id_mat_ci")+
                        " where id_cat_mat="+jsonObject.optString("id_cat_mat");
                db.execSQL(sql);

                //Agregando Carga academica
                c=new ContentValues();
                c.put("id_mat_ci",jsonObject.optInt("id_mat_ci"));
                c.put("id_pdg_dcn",jsonObject.getInt("id_pdg_dcn"));
                c.put("id_grup_carg",jsonObject.optInt("id_grup_carg"));
                Operaciones_CRUD.insertar(db,c,this,"carga_academica");

                sql="UPDATE carga_academica set id_carg_aca="+jsonObject.optString("id_carg_aca")+
                        " where id_mat_ci="+jsonObject.optString("id_mat_ci");
                db.execSQL(sql);

                //Agregando detalle de incripcion de estudiante
                c=new ContentValues();
                c.put("id_est",jsonObject.optInt("id_est"));
                c.put("id_carg_aca",jsonObject.optInt("id_carg_aca"));
                Operaciones_CRUD.insertar(db,c,this,"detalleinscest");

                //sql="UPDATE detalleinscest set id_det_insc="+jsonObject.optString("id_insc_est")+ " where id_est="+jsonObject.optString("id_est");
                //db.execSQL(sql);
            }
        } catch (JSONException e) {
            Log.d("Error", e.toString());
        }

        listaMateria=Operaciones_CRUD.todosMateria(db,rol,id);
        adapter=new MateriaUserAdapter(MateriaUsersActivity.this,listaMateria,db,this,id,rol);
        listView.setAdapter(adapter);
        progressDialog.cancel();
    }

    public void getMateriasWS(int id){
        url = "https://sigen.herokuapp.com/api/materias/estudiante/"+id;
        jsonArrayRequest=new JsonArrayRequest(Request.Method.GET,url,null,this,this);
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(8000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonArrayRequest);
    }

    //Funcion para verificar si hay conexion a internet
    public boolean isInternetAvailable() {
        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            return p.waitFor() == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
