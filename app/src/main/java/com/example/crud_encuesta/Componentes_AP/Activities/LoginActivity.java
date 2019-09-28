package com.example.crud_encuesta.Componentes_AP.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.crud_encuesta.Componentes_AP.DAO.DAOUsuario;
import com.example.crud_encuesta.Componentes_AP.Models.Usuario;
import com.example.crud_encuesta.Componentes_MR.Estudiante.Estudiante;
import com.example.crud_encuesta.Componentes_MT.EncuestaWS.EncuestaActivityWS;
import com.example.crud_encuesta.DatabaseAccess;
import com.example.crud_encuesta.MainActivity;
import com.example.crud_encuesta.R;

import org.json.JSONArray;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {
    RequestQueue requestQueue;
    JsonObjectRequest jsonObjectRequest;

    SQLiteDatabase baseDeDatos;
    TextInputLayout tvUsuario;
    TextInputLayout tvPass;
    DAOUsuario daoUsuario;
    Usuario user;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        daoUsuario = new DAOUsuario(this);

        //enlazamos con el layout
        tvUsuario = (TextInputLayout) findViewById(R.id.ap_tiluser);
        tvPass = (TextInputLayout) findViewById(R.id.ap_tilpass);
        Button login = (Button) findViewById(R.id.ap_bt_login);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //guardamos en variables los datos que ha ingresado el usuario
                final String usuario = tvUsuario.getEditText().getText().toString().trim();
                final String pass = tvPass.getEditText().getText().toString();
                Usuario usuarioLogueado = daoUsuario.getUsuarioLogueado();
                //si hay un usuario logueado en la base de datos
                if(usuarioLogueado != null){
                    //si el usuario que se ingresa es igual al que esta en la base lo deja entrar de inmediato
                    if(usuario.equals(usuarioLogueado.getNOMUSUARIO()) && pass.equals(usuarioLogueado.getCLAVE())){
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("id_user",usuarioLogueado.getIDUSUARIO());
                        intent.putExtra("rol_user",usuarioLogueado.getROL());
                        intent.putExtra("username",usuarioLogueado.getNOMUSUARIO());
                        startActivity(intent);
                        Toast.makeText(v.getContext(), getResources().getText(R.string.ap_bienvenido) + " "+usuario,Toast.LENGTH_LONG).show();
                        finish();
                    }//si el usuario que se ingresa no es igual al que está en la base, se consulta al webService
                    else{
                        //si no tiene internet le presenta un mensaje que no puede realizar la acción
                        if (!isInternetAvailable()){
                            Toast.makeText(v.getContext(), "No hay acceso a Internet.", Toast.LENGTH_LONG).show();
                        }else{
                            progress("Cargando... ");
                            accesoAWebService(usuario,pass);
                            progressDialog.cancel();
                        }

                    }
                 //si no hay usuario logueado, se consulta al webservice
                }else{
                    //si no tiene internet le presenta un mensaje que no puede realizar la acción
                    if (!isInternetAvailable()){
                        Toast.makeText(v.getContext(), "No hay acceso a Internet.", Toast.LENGTH_LONG).show();
                    }else{
                        progress("Cargando... ");
                        accesoAWebService(usuario,pass);
                        progressDialog.cancel();
                    }
                }
            }
        });


    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, "Error: " + error,Toast.LENGTH_LONG).show();

    }

    @Override
    public void onResponse(JSONObject response) {
        daoUsuario.DeleteUserAll();
        daoUsuario.DeleteSesionAll();
        daoUsuario.DeleteMateriasUser();
        daoUsuario.DeleteEstudianteAll();
        try{
            JSONObject jsonUser = response.getJSONObject("user");
            JSONObject jsonEstudiante = response.getJSONObject("estudiante");
            if(jsonUser!= null){
                Usuario user = new Usuario();
                Estudiante estudiante = new Estudiante();



                user.setIDUSUARIO(jsonUser.getInt("id"));
                user.setCLAVE(jsonUser.getString("name"));
                user.setNOMUSUARIO(jsonUser.getString("email"));
                user.setROL(jsonUser.getInt("role"));

                estudiante.setId(jsonEstudiante.getInt("id_est"));
                estudiante.setActivo(jsonEstudiante.getInt("activo"));
                estudiante.setAnio_ingreso(jsonEstudiante.getString("anio_ingreso"));
                estudiante.setCarnet(jsonEstudiante.getString("carnet"));
                estudiante.setId_usuario(jsonEstudiante.getInt("user_id"));
                estudiante.setNombre(jsonEstudiante.getString("nombre"));


                if(daoUsuario.Insertar(user)&& daoUsuario.insertar(estudiante)){
                    if(daoUsuario.loginUsuario(user.getCLAVE(),user.getNOMUSUARIO())){
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("id_user",user.getIDUSUARIO());
                        intent.putExtra("rol_user",user.getROL());
                        intent.putExtra("username",user.getNOMUSUARIO());
                        startActivity(intent);
                        Toast.makeText(this, getResources().getText(R.string.ap_bienvenido) + " "+user.getNOMUSUARIO(),Toast.LENGTH_LONG).show();
                        finish();
                    }else{
                        Toast.makeText(this, "El usuario y/o contraseña no es valido.",Toast.LENGTH_LONG).show();
                    }
                }
            }else{
                Toast.makeText(this, "El usuario no existe",Toast.LENGTH_LONG).show();
            }

        }catch(Exception e){
            Toast.makeText(this, "Error: El usuario no existe",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        progressDialog.cancel();

    }

    public void accesoAWebService(String email, String pass){
        String url = "http://sigen.herokuapp.com/api/user/acceso/"+email+"/"+pass;
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
