package com.example.crud_encuesta.Componentes_EL.Materia;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crud_encuesta.Componentes_EL.Carrera.Carrera;
import com.example.crud_encuesta.Componentes_EL.Escuela.Escuela;
import com.example.crud_encuesta.Componentes_EL.EstructuraTablas;
import com.example.crud_encuesta.Componentes_EL.Operaciones_CRUD;
import com.example.crud_encuesta.Componentes_EL.ModelosAdicionales.Pensum;
import com.example.crud_encuesta.Componentes_MT.finalizarIntentoWS.EvaluacionesPorSubir;
import com.example.crud_encuesta.R;
import com.example.crud_encuesta.SubMenuMateriaActivity;

import java.util.ArrayList;


public class MateriaAdapter extends BaseAdapter {

    Context context;
    ArrayList<Materia> l;
    SQLiteDatabase db;
    Activity activity;
    ArrayList<Pensum> listaPensum;
    ArrayList<Carrera> listaCarreras;
    ArrayList<Escuela> listaEscuelas;
    ArrayList<String> listPensumSpinner = new ArrayList<>();
    ArrayList<String> listCarreraSpinner = new ArrayList<>();

    int id_carrera;
    int id_pensum;
    int rol;

    public MateriaAdapter(Context c, ArrayList<Materia> lista, SQLiteDatabase db, Activity a, ArrayList<Pensum> p, ArrayList<Carrera> ca, ArrayList<Escuela> e,int rol) {
        this.context = c;
        this.l = lista;
        this.db = db;
        this.activity = a;
        this.listaPensum = p;
        this.listaCarreras = ca;
        this.listaEscuelas = e;
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

        if (rol!=0){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(context, SubMenuMateriaActivity.class);
                    i.putExtra("id_materia",getItemId(position));
                    context.startActivity(i);
                }
            });
        }

        Button btneditar = view.findViewById(R.id.btn_editar);
        Button btneliminar = view.findViewById(R.id.btn_eliminar);
        Button btninfo=view.findViewById(R.id.btn_infor);
        TextView textView = view.findViewById(R.id.txt_escuela_item);
        textView.setText(l.get(position).toString());

        btneliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int id = (int) getItemId(position);
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle(R.string.title_borrar);
                alert.setMessage(R.string.lbl_borrar);
                alert.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Operaciones_CRUD.eliminar(db, context, EstructuraTablas.MATERIA_TABLA_NAME, EstructuraTablas.COL_0_MATERIA, id);
                        l.clear();
                        setL(Operaciones_CRUD.todosMateria(db));
                    }
                });
                alert.setNegativeButton(R.string.cancelar_string, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alert.show();
            }
        });


        btneditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int id = (int) getItemId(position);
                final Materia m = getItem(position);
                final AlertDialog.Builder d = new AlertDialog.Builder(context);

                View view = inflater.inflate(R.layout.dialogo_materia, null);


                final EditText nom = view.findViewById(R.id.in_nom_mat);
                final EditText cod = view.findViewById(R.id.in_cod_materia);
                final EditText max = view.findViewById(R.id.in_max_preguntas);
                final CheckBox elec = view.findViewById(R.id.check_electiva);
                final TextView title=view.findViewById(R.id.title_dialog);

                title.setText(R.string.title_activity_act);

                /*final Spinner spinnerC = view.findViewById(R.id.spinner_lista_carrera);
                final Spinner spinnerP = view.findViewById(R.id.spinner_lista_pensum);

                listPensumSpinner = obtenerListaPensum();
                listCarreraSpinner = obtenerListaCarrera();

                ArrayAdapter adapterCa = new ArrayAdapter(context, android.R.layout.simple_list_item_1, listCarreraSpinner);
                ArrayAdapter adapterPe = new ArrayAdapter(context, android.R.layout.simple_list_item_1, listPensumSpinner);

                spinnerC.setAdapter(adapterCa);
                spinnerP.setAdapter(adapterPe);*/


                nom.setText(m.getNombre());
                cod.setText(m.getCodigo_materia());
                max.setText(m.getMaximo_preguntas()+"");
                if(m.getElectiva()==1)elec.setChecked(true);
                else elec.setChecked(false);

                /*for (int i = 0; i < listCarreraSpinner.size(); i++) {
                    if (m.getCarrera().getNombre() == listCarreraSpinner.get(i)) spinnerC.setSelection(i);
                }

                for (int i = 0; i < listPensumSpinner.size(); i++) {
                    if (m.getPensum().toString().equals(listPensumSpinner.get(i))) spinnerP.setSelection(i);
                }

                spinnerC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position != 0) id_carrera = listaCarreras.get(position - 1).getId();
                        else id_carrera = -1;
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                spinnerP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position != 0) id_pensum = listaPensum.get(position - 1).getId();
                        else id_pensum = -1;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });*/

                d.setPositiveButton(R.string.actualizar_string, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (nom.getText().toString().isEmpty() || cod.getText().toString().isEmpty() || max.getText().toString().isEmpty())
                            Toast.makeText(context, R.string.men_camp_vacios, Toast.LENGTH_SHORT).show();
                        else {
                            ContentValues contentValues = new ContentValues();
                            //contentValues.put(EstructuraTablas.COL_1_MATERIA, id_pensum);
                            //contentValues.put(EstructuraTablas.COL_2_MATERIA, id_carrera);
                            contentValues.put(EstructuraTablas.COL_3_MATERIA, cod.getText().toString());
                            contentValues.put(EstructuraTablas.COL_4_MATERIA, nom.getText().toString());

                            if (elec.isChecked()) contentValues.put(EstructuraTablas.COL_5_MATERIA, 1);
                            else contentValues.put(EstructuraTablas.COL_5_MATERIA, 0);

                            contentValues.put(EstructuraTablas.COL_6_MATERIA, max.getText().toString());

                            Operaciones_CRUD.actualizar(db, contentValues, context, EstructuraTablas.MATERIA_TABLA_NAME,EstructuraTablas.COL_0_MATERIA,id).show();
                            l = Operaciones_CRUD.todosMateria(db);
                            setL(l);
                            id_carrera = -1;
                            id_pensum = -1;
                        }
                    }
                });

                d.setNegativeButton(R.string.cancelar_string, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                d.setView(view);
                d.show();
            }
        });

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

                title.setText(R.string.title_activity_info);

                nom.setEnabled(false);
                cod.setEnabled(false);
                max.setEnabled(false);
                elec.setEnabled(false);

                nom.setText(m.getNombre());
                cod.setText(m.getCodigo_materia());
                max.setText(m.getMaximo_preguntas()+"");
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

        return view;
    }

    public ArrayList<String> obtenerListaCarrera() {
        ArrayList<String> listaca = new ArrayList<>();
        listaca.add("Seleccione");
        for (int i = 0; i < listaCarreras.size(); i++) {
            listaca.add(listaCarreras.get(i).getNombre());
        }
        return listaca;
    }

    public ArrayList<String> obtenerListaPensum() {
        ArrayList<String> listape = new ArrayList<>();
        listape.add("Seleccione");
        for (int i = 0; i < listaPensum.size(); i++) {
            listape.add(listaPensum.get(i).toString());
        }
        return listape;
    }

    public void setL(ArrayList<Materia> l) {
        this.l = l;
        notifyDataSetChanged();
    }
}
