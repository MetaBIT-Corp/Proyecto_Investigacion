package com.example.crud_encuesta.Componentes_EL.Materia;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crud_encuesta.Componentes_MT.finalizarIntentoWS.EvaluacionesPorSubirActivity;
import com.example.crud_encuesta.R;
import com.example.crud_encuesta.SubMenuMateriaActivity;

import java.util.ArrayList;

public class MateriaUserAdapter extends BaseAdapter {

    Context context;
    ArrayList<Materia> l;
    SQLiteDatabase db;
    Activity activity;
    int idUser;
    int rol;

    public MateriaUserAdapter(Context context, ArrayList<Materia> l, SQLiteDatabase db, Activity activity,int user,int rol) {
        this.context = context;
        this.l = l;
        this.db = db;
        this.activity = activity;
        this.idUser=user;
        this.rol=rol;
    }

    @Override
    public int getCount() {
        return l.size();
    }

    @Override
    public Materia getItem(int position) {
        return l.get(position);
    }

    @Override
    public long getItemId(int position) {
        return l.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_escuela, null);
        }

        //Envia ID de materia para el submenu de materia
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, SubMenuMateriaActivity.class);
                i.putExtra("id_materia",(int)getItemId(position));
                i.putExtra("nom_materia",getItem(position).getNombre());
                i.putExtra("id_user",idUser);
                i.putExtra("rol_user",rol);
                context.startActivity(i);
                //activity.finish();
            }
        });

        Button btneditar = view.findViewById(R.id.btn_editar);
        Button btneliminar = view.findViewById(R.id.btn_eliminar);
        Button btninfo=view.findViewById(R.id.btn_infor);
        Button btnSync=view.findViewById(R.id.btn_sync);
        final TextView textView = view.findViewById(R.id.txt_escuela_item);

        btnSync.setVisibility(View.VISIBLE);
        textView.setText(l.get(position).toString());

        btneditar.setVisibility(View.GONE);
        btneliminar.setVisibility(View.GONE);

        btninfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Materia m = getItem(position);
                final AlertDialog.Builder d = new AlertDialog.Builder(context);

                View view = inflater.inflate(R.layout.dialogo_materia, null);

                final EditText nom = view.findViewById(R.id.in_nom_mat);
                final EditText cod = view.findViewById(R.id.in_cod_materia);
                final EditText max = view.findViewById(R.id.in_max_preguntas);
                final CheckBox elec = view.findViewById(R.id.check_electiva);
                final TextView title=view.findViewById(R.id.title_dialog);
                TextView t=view.findViewById(R.id.textView5);


                title.setText(R.string.title_activity_info);

                nom.setEnabled(false);
                cod.setEnabled(false);
                t.setVisibility(View.GONE);
                max.setVisibility(View.GONE);
                elec.setEnabled(false);

                nom.setText(m.getNombre());
                cod.setText(m.getCodigo_materia());
                if(m.getElectiva()==1)elec.setChecked(true);
                else elec.setChecked(false);


                d.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                d.setView(view);
                d.show();
            }
            });

        btnSync.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                final int id = (int) getItemId(position);
                Intent i = new Intent(context, EvaluacionesPorSubirActivity.class);
                i.putExtra("materia_id", id);
                context.startActivity(i);
            }
        });

        return view;
    }
}
