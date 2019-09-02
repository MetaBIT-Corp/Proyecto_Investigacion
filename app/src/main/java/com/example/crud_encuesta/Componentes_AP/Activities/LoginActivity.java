package com.example.crud_encuesta.Componentes_AP.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.crud_encuesta.Componentes_AP.DAO.DAOUsuario;
import com.example.crud_encuesta.Componentes_AP.Models.Usuario;
import com.example.crud_encuesta.Componentes_MT.EncuestaWS.EncuestaActivityWS;
import com.example.crud_encuesta.DatabaseAccess;
import com.example.crud_encuesta.MainActivity;
import com.example.crud_encuesta.R;


public class LoginActivity extends AppCompatActivity {

    SQLiteDatabase baseDeDatos;
    TextInputLayout tvUsuario;
    TextInputLayout tvPass;
    DAOUsuario daoUsuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        daoUsuario = new DAOUsuario(this);

        //enlazamos con el layout
        tvUsuario = (TextInputLayout) findViewById(R.id.ap_tiluser);
        tvPass = (TextInputLayout) findViewById(R.id.ap_tilpass);
        Button login = (Button) findViewById(R.id.ap_bt_login);



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //guardamos en variables los datos que ha ingresado el usuario
                final String usuario = tvUsuario.getEditText().getText().toString().trim();
                final String pass = tvPass.getEditText().getText().toString();

                if(daoUsuario.loginUsuario(pass,usuario)){
                    Usuario usuarioLogueado = daoUsuario.getUsuarioLogueado();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("id_user",usuarioLogueado.getIDUSUARIO());
                    intent.putExtra("rol_user",usuarioLogueado.getROL());
                    intent.putExtra("username",usuarioLogueado.getNOMUSUARIO());
                    startActivity(intent);
                    Toast.makeText(v.getContext(), getResources().getText(R.string.ap_bienvenido) + " "+usuario,Toast.LENGTH_LONG).show();
                    finish();
                }else {
                    Toast.makeText(v.getContext(),getResources().getText(R.string.ap_usuario),Toast.LENGTH_SHORT).show();
                }
                /*Intent i = new Intent(LoginActivity.this, pruebaActivity.class);
                startActivity(i);*/
            }
        });


    }

}
