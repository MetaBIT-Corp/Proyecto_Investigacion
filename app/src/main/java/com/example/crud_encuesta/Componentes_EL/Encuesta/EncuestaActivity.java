package com.example.crud_encuesta.Componentes_EL.Encuesta;

import com.example.crud_encuesta.Componentes_MR.Docente.Docente;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.crud_encuesta.Componentes_EL.EstructuraTablas;
import com.example.crud_encuesta.Componentes_EL.Operaciones_CRUD;
import com.example.crud_encuesta.DatabaseAccess;
import com.example.crud_encuesta.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

public class EncuestaActivity extends AppCompatActivity {

    SQLiteDatabase db;
    DatabaseAccess access;
    ListView listView;
    int dia,mes,año,ho,min;
    boolean seg;

    int di,df,mi,mf,ai,af,hi,hf;
    String cadenai = null;
    String cadenaf=null;

    ArrayList<Docente>listaDocentes=new ArrayList<>();
    ArrayList<Encuesta>listaEncuesta=new ArrayList<>();
    EncuestaAdapter adapter;

    AutoCompleteTextView buscar;

    int rol;
    int iduser;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encuesta);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab= findViewById(R.id.fab);
        listView=findViewById(R.id.list_view_base);
        access=DatabaseAccess.getInstance(EncuestaActivity.this);
        db=access.open();

        rol=getIntent().getExtras().getInt("rol_user");
        iduser=getIntent().getExtras().getInt("id_user");

        listaDocentes= Operaciones_CRUD.todosDocente(db);


        LinearLayout l=findViewById(R.id.linearBusqueda);



        if (rol==0||rol==2){
            listaEncuesta=Operaciones_CRUD.todosEncuesta(db,listaDocentes);
            fab.setVisibility(View.GONE);

        }

        if (rol==1){
            l.setVisibility(View.GONE);
            listaEncuesta=Operaciones_CRUD.todosEncuesta(db,listaDocentes,iduser);
        }

        ImageView btnBuscar=findViewById(R.id.el_find);
        ImageView btnTodos=findViewById(R.id.el_all);
        //final EditText buscar=findViewById(R.id.find_nom);
        autoComplemento();

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaEncuesta=Operaciones_CRUD.todosEncuesta(db,listaDocentes,buscar.getText().toString());
                adapter.setL(listaEncuesta);
                buscar.setText("");
            }
        });
        btnTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rol==0||rol==2){
                    listaEncuesta=Operaciones_CRUD.todosEncuesta(db,listaDocentes);
                }

                if (rol==1){
                    listaEncuesta=Operaciones_CRUD.todosEncuesta(db,listaDocentes,iduser);
                }
                adapter.setL(listaEncuesta);
                buscar.setText("");
            }
        });




        adapter=new EncuestaAdapter(EncuestaActivity.this,listaEncuesta,db,this,listaDocentes,iduser,rol);

        listView.setAdapter(adapter);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder d=new AlertDialog.Builder(EncuestaActivity.this);

                View v=getLayoutInflater().inflate(R.layout.dialogo_encuesta, null);

                Button btnfi=v.findViewById(R.id.btn_fecha_inicio);
                Button btnff=v.findViewById(R.id.btn_fecha_final);
                final EditText infi=v.findViewById(R.id.in_fecha_inicial);
                final EditText inff=v.findViewById(R.id.in_fecha_final);
                final EditText nom=v.findViewById(R.id.in_nom_encuesta);
                final EditText desc=v.findViewById(R.id.in_descrip_encuesta);

                btnfi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar c=Calendar.getInstance();
                        dia=c.get(Calendar.DAY_OF_MONTH);
                        mes=c.get(Calendar.MONTH);
                        año=c.get(Calendar.YEAR);

                        ho=c.get(Calendar.HOUR_OF_DAY);
                        min=c.get(Calendar.MINUTE);
                        seg=false;

                        DatePickerDialog calendar=new DatePickerDialog(EncuestaActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                ai=year;
                                mi=month;
                                di=dayOfMonth;
                                cadenai=dayOfMonth+"/"+month+"/"+year+" ";

                                TimePickerDialog hora=new TimePickerDialog(EncuestaActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        hi=hourOfDay;
                                        infi.setText(cadenai+hourOfDay+":"+minute);
                                    }
                                },ho,min,seg);
                                hora.setTitle(R.string.men_hora_in);
                                hora.show();
                            }
                        },año,mes,dia);
                        calendar.setTitle(R.string.men_fecha_in);
                        calendar.show();
                    }
                });

                btnff.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar c=Calendar.getInstance();
                        dia=c.get(Calendar.DAY_OF_MONTH);
                        mes=c.get(Calendar.MONTH);
                        año=c.get(Calendar.YEAR);

                        DatePickerDialog calendar=new DatePickerDialog(EncuestaActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                af=year;
                                mf=month;
                                df=dayOfMonth;
                                cadenaf=dayOfMonth+"/"+month+"/"+year+" ";

                                TimePickerDialog hora=new TimePickerDialog(EncuestaActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        hf=hourOfDay;
                                        inff.setText(cadenaf+hourOfDay+":"+minute);

                                        if ((di>df&&mi>=mf)||mi>mf||ai>af||(di==df&&hi>=hf)||(hi==12&&hf>=12)){
                                            Toast.makeText(EncuestaActivity.this,R.string.men_fecha_error,Toast.LENGTH_SHORT).show();
                                            infi.setText("dd/mm/aa");
                                            inff.setText("dd/mm/aa");
                                        }
                                    }
                                },ho,min,seg);
                                hora.setTitle(R.string.men_hora_fi);
                                hora.show();

                            }
                        },año,mes,dia);
                        calendar.setTitle(R.string.men_fecha_fi);
                        calendar.show();
                    }
                });


                d.setPositiveButton(R.string.agregar_string, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(nom.getText().toString().isEmpty()|| desc.getText().toString().isEmpty() || infi.getText().toString().equals("dd/mm/aa") || inff.getText().toString().equals("dd/mm/aa"))
                            Toast.makeText(EncuestaActivity.this,R.string.men_camp_vacios,Toast.LENGTH_SHORT).show();
                        else{
                            ContentValues contentValues=new ContentValues();

                            contentValues.put(EstructuraTablas.COL_1_ENCUESTA,Operaciones_CRUD.docenteEncuesta(db,iduser));
                            contentValues.put(EstructuraTablas.COL_2_ENCUESTA,nom.getText().toString());
                            contentValues.put(EstructuraTablas.COL_3_ENCUESTA,desc.getText().toString());
                            contentValues.put(EstructuraTablas.COL_4_ENCUESTA,infi.getText().toString());
                            contentValues.put(EstructuraTablas.COL_5_ENCUESTA,inff.getText().toString());
                            Operaciones_CRUD.insertar(db,contentValues,EncuestaActivity.this,EstructuraTablas.ENCUESTA_TABLA_NAME).show();
                            if (rol==0||rol==2){
                                listaEncuesta=Operaciones_CRUD.todosEncuesta(db,listaDocentes);
                            }

                            if (rol==1){
                                listaEncuesta=Operaciones_CRUD.todosEncuesta(db,listaDocentes,iduser);
                            }
                            adapter.setL(listaEncuesta);
                            autoComplemento();
                        }
                    }
                });

                d.setNegativeButton(R.string.cancelar_string, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                d.setView(v);
                d.show();
            }
        });
    }

    public void autoComplemento(){
        Vector<String> autocomplemento = new Vector<String>();
        for(int i =0;i<=listaEncuesta.size()-1;i++){
            autocomplemento.add(listaEncuesta.get(i).getTitulo());
        }
        buscar = (AutoCompleteTextView) findViewById(R.id.auto);
        ArrayAdapter<String> adapterComplemento = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                autocomplemento);
        buscar.setAdapter(adapterComplemento);
    }

}
