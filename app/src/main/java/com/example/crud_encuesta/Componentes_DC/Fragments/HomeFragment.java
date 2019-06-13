package com.example.crud_encuesta.Componentes_DC.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crud_encuesta.Componentes_AP.Activities.EvaluacionActivity;
import com.example.crud_encuesta.Componentes_AP.Activities.LoginActivity;
import com.example.crud_encuesta.Componentes_AP.Activities.PensumActivity;
import com.example.crud_encuesta.Componentes_AP.Activities.TurnoActivity;
import com.example.crud_encuesta.Componentes_AP.DAO.DAOUsuario;
import com.example.crud_encuesta.Componentes_AP.Models.Usuario;
import com.example.crud_encuesta.Componentes_DC.Activities.BottomNavActivity;
import com.example.crud_encuesta.Componentes_EL.Carrera.CarreraActivity;
import com.example.crud_encuesta.Componentes_EL.Encuesta.EncuestaActivity;
import com.example.crud_encuesta.Componentes_EL.Escuela.EscuelaActivity;
import com.example.crud_encuesta.Componentes_EL.Materia.MateriaActivity;
import com.example.crud_encuesta.Componentes_EL.Materia.MateriaUsersActivity;
import com.example.crud_encuesta.Componentes_MR.Docente.ActivityDocente;
import com.example.crud_encuesta.Componentes_MR.Estudiante.ActivityEstudiante;
import com.example.crud_encuesta.Componentes_MR.MateriaCiclo.ActivityMateriaCiclo;
import com.example.crud_encuesta.Componentes_MT.Area.AreaActivity;
import com.example.crud_encuesta.Componentes_MT.Clave.ClaveActivity;
import com.example.crud_encuesta.Componentes_MT.Intento.IntentoActivity;
import com.example.crud_encuesta.MainActivity;
import com.example.crud_encuesta.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    private ListView listView;
    private Toolbar myTopToolBar;
    private ImageView loggin;
    private ImageView imgLlenarBD;
    private ImageView imgVaciarBD;
    private DAOUsuario daoUsuario;
    private ImageView bottomNav;


    private int id = 0;
    private int rol = 0;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        daoUsuario = new DAOUsuario(getContext());


        //Recuperando datos de usuario logueado
        id = getActivity().getIntent().getExtras().getInt("id_user");
        rol = getActivity().getIntent().getExtras().getInt("rol_user");

        /*bottomNav = (ImageView)v.findViewById(R.id.bottomNav);
        bottomNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), BottomNavActivity.class);
                in.putExtra("id_user", id);
                in.putExtra("rol_user", rol);
                startActivity(in);
            }
        });*/

       /* switch (rol) {
            case 0:
                bienvenida.setText("Administrador");
                break;
            case 1:
                bienvenida.setText("Docente: " + getActivity().getIntent().getExtras().getString("username"));
                break;
            case 2:
                bienvenida.setText("Estudiante: " + getActivity().getIntent().getExtras().getString("username"));
                break;
        }*/


        ImageView materia = v.findViewById(R.id.el_btnMateria);
        ImageView carrera = v.findViewById(R.id.el_btnCarrera);
        ImageView encuesta = v.findViewById(R.id.el_btnEncuesta);
        ImageView escuela = v.findViewById(R.id.el_btnEscuela);
        ImageView pensum = v.findViewById(R.id.ap_btnPensum);
        ImageView docente = v.findViewById(R.id.btn_docente);
        ImageView alumno = v.findViewById(R.id.btn_alumno);
        ImageView materia_ciclo = v.findViewById(R.id.btn_materia_ciclo);

        CardView cardViewCarrera = v.findViewById(R.id.cardCarrera);

        //Nuevo codigo

        CardView cv_carrera = (CardView) v.findViewById(R.id.cardCarrera);
        CardView cv_escuela = (CardView) v.findViewById(R.id.cardEscuela);
        CardView cv_docente = (CardView) v.findViewById(R.id.cardDocente);
        CardView cv_alumno = (CardView) v.findViewById(R.id.cardAlumno);
        CardView cv_materiaciclo = (CardView) v.findViewById(R.id.cardMateriaCiclo);
        CardView cv_pensum = (CardView) v.findViewById(R.id.cardPensum);

        GridLayout grid_menu = (GridLayout) v.findViewById(R.id.grid_menu);

        docente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_docente(null);
            }
        });
        alumno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_estudiante(null);
            }
        });
        materia_ciclo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_materia_ciclo(null);
            }
        });
        //END

        //PARA OCULTARSELO AL ESTUDIANTE Y DOCENTE
        //CUANDO
        if (rol == 3) {
            //Estudiante
            cardViewCarrera.setVisibility(View.GONE);
        }

        if (rol != 0) {
            grid_menu.removeView(cv_carrera);
            grid_menu.removeView(cv_escuela);
            grid_menu.removeView(cv_docente);
            grid_menu.removeView(cv_alumno);
            grid_menu.removeView(cv_materiaciclo);
            grid_menu.removeView(cv_pensum);

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    GridLayout.LayoutParams.MATCH_PARENT,
                    GridLayout.LayoutParams.WRAP_CONTENT,
                    4.0f
            );

            grid_menu.setLayoutParams(param);
        }


        pensum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), PensumActivity.class);
                startActivity(i);
            }
        });

        materia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                switch (rol) {
                    //Admin
                    case 0:
                        i = new Intent(getActivity(), MateriaActivity.class);
                        i.putExtra("rol_user", rol);
                        startActivity(i);
                        break;
                    //Docente
                    case 1:
                        i = new Intent(getActivity(), MateriaUsersActivity.class);
                        i.putExtra("id_user", id);
                        i.putExtra("rol_user", rol);
                        startActivity(i);
                        break;
                    //Estudiante
                    case 2:
                        i = new Intent(getActivity(), MateriaUsersActivity.class);
                        i.putExtra("id_user", id);
                        i.putExtra("rol_user", rol);
                        startActivity(i);
                        break;


                }
            }
        });

        carrera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CarreraActivity.class);
                startActivity(i);
            }
        });

        encuesta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EncuestaActivity.class);
                i.putExtra("rol_user", rol);
                i.putExtra("id_user", id);
                startActivity(i);
            }
        });
        escuela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EscuelaActivity.class);
                startActivity(i);
            }
        });


        return v;
    }



    public void area() {
        Intent i = new Intent(getActivity(), AreaActivity.class);
        startActivity(i);
    }

    public void intento() {
        Intent i = new Intent(getActivity(), IntentoActivity.class);
        startActivity(i);
    }

    public void clave() {
        Intent i = new Intent(getActivity(), ClaveActivity.class);
        startActivity(i);
    }


    public void evaluacion() {
        Intent i = new Intent(getActivity(), EvaluacionActivity.class);
        startActivity(i);
    }

    public void turno() {
        Intent i = new Intent(getActivity(), TurnoActivity.class);
        startActivity(i);
    }

    public void activity_docente(View view) {
        Intent i = new Intent(getActivity(), ActivityDocente.class);
        startActivity(i);
    }

    public void activity_encuesta(View view) {
        /*Intent i= new Intent(this, ActivityDocente.class);
        startActivity(i);*/
    }

    public void activity_estudiante(View view) {
        Intent i = new Intent(getActivity(), ActivityEstudiante.class);
        startActivity(i);
    }

    public void activity_materia_ciclo(View view) {
        Intent i = new Intent(getActivity(), ActivityMateriaCiclo.class);
        startActivity(i);
    }

    //metodo que permite direccionar al usuario al login, si ya está logueado le dice que si en verda desea salir
    public void controlAcceso() {
        final Usuario usuario = daoUsuario.getUsuarioLogueado();
        if (usuario == null) {
            Intent i = new Intent(getActivity(), LoginActivity.class);
            startActivity(i);
        } else {
            AlertDialog.Builder delete_emergente = new AlertDialog.Builder(getContext());
            delete_emergente.setMessage(getResources().getText(R.string.ap_salir) + " " + usuario.getNOMUSUARIO() + "?");
            delete_emergente.setCancelable(true);

            //Caso positivo

            delete_emergente.setPositiveButton(getResources().getText(R.string.si), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (daoUsuario.logoutUsuario(usuario.getIDUSUARIO())) {
                        daoUsuario.logoutUsuario(usuario.getIDUSUARIO());
                        Toast.makeText(getActivity(), getResources().getText(R.string.ap_vuelve) + " " + usuario.getNOMUSUARIO(), Toast.LENGTH_LONG).show();
                        Intent i = new Intent(getActivity(), LoginActivity.class);
                        startActivity(i);
                        getActivity().finish();
                    } else {
                        Toast.makeText(getContext(), "Ups, algo falló, vuelve a intentar", Toast.LENGTH_LONG);
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

    public void onBackPressed() {
        controlAcceso();
    }


}


