package com.example.crud_encuesta.Componentes_DC.Fragments;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.crud_encuesta.Componentes_AP.DAO.DAOUsuario;
import com.example.crud_encuesta.DatabaseAccess;
import com.example.crud_encuesta.R;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class UsuarioFragment extends Fragment {

    private int id = 0;
    private int rol = 0;
    private TextView txt_perfil;
    private TextView txt_usermane;
    private TextView txt_nombre;
    private TextView txt_anio_ingreso;
    private TextView txt_anio_titulo;
    private SQLiteDatabase cx;
    private DatabaseAccess dba;
    private LinearLayout lay_nombre;
    private LinearLayout lay_anio_ingreso;
    private LinearLayout lay_anio_titulo;
    private GridLayout grid;


    VideoView video;
    MediaController mediacontrol;


    public UsuarioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_usuario, container, false);

        video= (VideoView) v.findViewById(R.id.video);

        Uri uri = Uri.parse("https://instagram.fsal3-1.fna.fbcdn.net/vp/6882fd9ffd20fe2b06212aea8e81e61a/5D0A633E/t50.2886-16/64668640_370729323576264_6329386514329985304_n.mp4?_nc_ht=instagram.fsal3-1.fna.fbcdn.net");
        video.setMediaController((new MediaController(v.getContext())));
        video.setVideoURI(uri);
        video.requestFocus();
        video.start();


        txt_perfil = (TextView)v.findViewById(R.id.txt_perfil);
        txt_usermane = (TextView)v.findViewById(R.id.txt_usermane);
        txt_nombre = (TextView)v.findViewById(R.id.txt_nombre);
        txt_anio_ingreso = (TextView)v.findViewById(R.id.txt_anio_ingreso);
        txt_anio_titulo = (TextView)v.findViewById(R.id.txt_anio_titulo);
        lay_nombre = (LinearLayout)v.findViewById(R.id.lay_nombre);
        lay_anio_ingreso = (LinearLayout)v.findViewById(R.id.lay_anio_ingreso);
        lay_anio_titulo = (LinearLayout)v.findViewById(R.id.lay_anio_titulo);
        grid = (GridLayout)v.findViewById(R.id.grid);

        dba = DatabaseAccess.getInstance(getContext());
        cx = dba.open();

        id = getActivity().getIntent().getExtras().getInt("id_user");
        rol = getActivity().getIntent().getExtras().getInt("rol_user");

        switch (rol) {
            case 0:
                txt_perfil.setText("Administrador");
                txt_usermane.setText("admin");
                grid.removeView(lay_nombre);
                grid.removeView(lay_anio_ingreso);
                grid.removeView(lay_anio_titulo);
                break;
            case 1:
                txt_perfil.setText("Docente");
                txt_usermane.setText("" + getActivity().getIntent().getExtras().getString("username"));
                txt_nombre.setText("" + getNombre(1));
                txt_anio_titulo.setText("" + getAnio(1));
                grid.removeView(lay_anio_ingreso);
                break;
            case 2:
                txt_perfil.setText("Estudiante");
                txt_usermane.setText(""+ getActivity().getIntent().getExtras().getString("username"));
                txt_nombre.setText("" + getNombre(0));
                txt_anio_ingreso.setText("" + getAnio(0));
                grid.removeView(lay_anio_titulo);
                break;
        }

        return v;
    }

    private String getAnio(int i) {

        Cursor cursor;

        if (i == 0){
            cursor = cx.rawQuery("SELECT ANIO_INGRESO FROM ESTUDIANTE WHERE IDUSUARIO="+id, null);
        }else{
            cursor = cx.rawQuery("SELECT ANIO_TITULO FROM PDG_DCN_DOCENTE WHERE IDUSUARIO="+id, null);
        }

        if(cursor.moveToFirst()){
            return cursor.getString(0);
        }else{
            return "";
        }

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
