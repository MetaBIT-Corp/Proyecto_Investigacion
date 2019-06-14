package com.example.crud_encuesta.Componentes_DC.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crud_encuesta.Componentes_AP.Activities.LoginActivity;
import com.example.crud_encuesta.Componentes_AP.DAO.DAOUsuario;
import com.example.crud_encuesta.Componentes_AP.Models.Usuario;
import com.example.crud_encuesta.Componentes_DC.Fragments.UsuarioFragment;
import com.example.crud_encuesta.Componentes_DC.Fragments.EncuestaFragment;
import com.example.crud_encuesta.Componentes_DC.Fragments.HomeFragment;
import com.example.crud_encuesta.MainActivity;
import com.example.crud_encuesta.R;

public class BottomNavActivity extends AppCompatActivity {

    private FrameLayout mMainFrame;
    private HomeFragment homeFrag;
    private EncuestaFragment encuestaFrag;
    private UsuarioFragment usuarioFrag;
    private BottomNavigationView mMainNav;
    private ImageView menu;
    private ImageView loggin;
    private DAOUsuario daoUsuario;

    private int id;
    private int rol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);

        daoUsuario = new DAOUsuario(this);

        homeFrag = new HomeFragment();
        encuestaFrag = new EncuestaFragment();
        usuarioFrag = new UsuarioFragment();

        setFragment(homeFrag);

        id = getIntent().getExtras().getInt("id_user");
        rol = getIntent().getExtras().getInt("rol_user");

        mMainFrame = (FrameLayout)findViewById(R.id.main_frame);

        menu = (ImageView)findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(BottomNavActivity.this, MainActivity.class);
                in.putExtra("id_user", id);
                in.putExtra("rol_user", rol);
                startActivity(in);
            }
        });

        TextView bienvenida = findViewById(R.id.title_bienvenida);

        switch (rol) {
            case 0:
                bienvenida.setText("Administrador");
                break;
            case 1:
                bienvenida.setText("Docente: " + getIntent().getExtras().getString("username"));
                break;
            case 2:
                bienvenida.setText("Estudiante: " + getIntent().getExtras().getString("username"));
                break;
        }

        //Nuevo
        mMainNav = (BottomNavigationView)findViewById(R.id.main_nav);
        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


                switch (menuItem.getItemId()){

                    case R.id.item_home:
                        setFragment(homeFrag);
                        return true;

                    case R.id.item_encuesta:
                        setFragment(encuestaFrag);
                        return  true;

                    case R.id.item_usuario:
                        setFragment(usuarioFrag);
                        return true;

                    default:
                        return false;
                }
            }
        });

        loggin = (ImageView) findViewById(R.id.log);
        loggin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Login();
                //intento();
                //evaluacion();
                //pressed();
                controlAcceso();
            }
        });

        //Fin nuevo
    }

    private void setFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();

    }

    public void controlAcceso() {
        final Usuario usuario = daoUsuario.getUsuarioLogueado();
        if (usuario == null) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        } else {
            AlertDialog.Builder delete_emergente = new AlertDialog.Builder(this);
            delete_emergente.setMessage(getResources().getText(R.string.ap_salir) + " " + usuario.getNOMUSUARIO() + "?");
            delete_emergente.setCancelable(true);

            //Caso positivo

            delete_emergente.setPositiveButton(getResources().getText(R.string.si), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (daoUsuario.logoutUsuario(usuario.getIDUSUARIO())) {
                        daoUsuario.logoutUsuario(usuario.getIDUSUARIO());
                        Toast.makeText(getApplication(), getResources().getText(R.string.ap_vuelve) + " " + usuario.getNOMUSUARIO(), Toast.LENGTH_LONG).show();
                        Intent i = new Intent(getApplication(), LoginActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(getApplication(), "Ups, algo falló, vuelve a intentar", Toast.LENGTH_LONG);
                    }
                }
            });

            //Caso negativo

            delete_emergente.setNegativeButton(getResources().getText(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // no esperamos que haga nada al cerrar, solo se cierra
                }
            });
            delete_emergente.show(); //mostrar alerta

        }
    }


    public void onBackPressed(){
        controlAcceso();
    }
}
