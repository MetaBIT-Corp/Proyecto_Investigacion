package com.example.crud_encuesta.Componentes_DC.Fragments;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crud_encuesta.DatabaseAccess;
import com.example.crud_encuesta.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UsuarioFragment extends Fragment {

    private int id = 0;
    private int rol = 0;
    private TextView txt_perfil;
    private TextView txt_usermane;
    private TextView txt_nombre;
    private SQLiteDatabase cx;
    private DatabaseAccess dba;

    public UsuarioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_usuario, container, false);

        txt_perfil = (TextView)v.findViewById(R.id.txt_perfil);
        txt_usermane = (TextView)v.findViewById(R.id.txt_usermane);
        txt_nombre = (TextView)v.findViewById(R.id.txt_nombre);
        dba = DatabaseAccess.getInstance(getContext());
        cx = dba.open();

        id = getActivity().getIntent().getExtras().getInt("id_user");
        rol = getActivity().getIntent().getExtras().getInt("rol_user");

        switch (rol) {
            case 0:
                txt_perfil.setText("Perfil: Administrador");
                txt_usermane.setText("Username: admin");
                break;
            case 1:
                txt_perfil.setText("Perfil: Docente");
                txt_usermane.setText("Username: " + getActivity().getIntent().getExtras().getString("username"));
                txt_nombre.setText("Nombre: " + getNombre(0));
                break;
            case 2:
                txt_perfil.setText("Perfil: Estudiante");
                txt_usermane.setText("Username: "+ getActivity().getIntent().getExtras().getString("username"));
                txt_nombre.setText("Nombre: " + getNombre(1));
                break;
        }

        return v;
    }

    private String getNombre(int i) {
        Cursor cursor;

        if (i == 0){
            cursor = cx.rawQuery("SELECT NOMBRE FROM ESTUDIANTE WHERE IDUSUARIO="+id, null);
        }else{
            cursor = cx.rawQuery("SELECT NOMBRE_DOCENTE FROM PDG_DCN_DOCENTE WHERE IDUSUARIO="+id, null);
        }

        if(cursor.moveToFirst()){
            return cursor.getString(0);
        }else{
            return "";
        }
        //Toast.makeText(getContext(), ""+ id, Toast.LENGTH_SHORT).show();
    }
}
