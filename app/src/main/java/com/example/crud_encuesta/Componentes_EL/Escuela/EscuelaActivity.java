package com.example.crud_encuesta.Componentes_EL.Escuela;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.crud_encuesta.Componentes_EL.EstructuraTablas;
import com.example.crud_encuesta.Componentes_EL.ModelosAdicionales.Facultad;
import com.example.crud_encuesta.Componentes_EL.Operaciones_CRUD;
import com.example.crud_encuesta.DatabaseAccess;
import com.example.crud_encuesta.R;

import java.util.ArrayList;
import java.util.Vector;

public class EscuelaActivity extends AppCompatActivity {

    SQLiteDatabase db;
    DatabaseAccess access;
    ContentValues contentValues;
    ListView listView;
    ArrayList <Escuela> listaEscuela= new ArrayList<>();
    EscuelaAdapter adapter;
    ArrayList<Facultad> facultades= new ArrayList<>();
    ArrayList<String> listafacultades;
    int id_facu;

    AutoCompleteTextView buscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escuela);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        access=DatabaseAccess.getInstance(EscuelaActivity.this);
        db=access.open();
        listView=findViewById(R.id.list_view_base);

        facultades= Operaciones_CRUD.todosFacultad(db);
        listaEscuela=Operaciones_CRUD.todosEscuela(EstructuraTablas.ESCUELA_TABLA_NAME,db);

        adapter= new EscuelaAdapter(EscuelaActivity.this,listaEscuela,db,this,facultades);
        listafacultades=obtenerListaFacultad();
        listView.setAdapter(adapter);

        ImageView btnBuscar=findViewById(R.id.el_find);
        ImageView btnTodos=findViewById(R.id.el_all);
        //final EditText buscar=findViewById(R.id.find_nom);

        autoComplemento();

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaEscuela=Operaciones_CRUD.todosEscuela(EstructuraTablas.ESCUELA_TABLA_NAME,db,buscar.getText().toString());
                adapter.setL(listaEscuela);
                buscar.setText("");
            }
        });
        btnTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaEscuela=Operaciones_CRUD.todosEscuela(EstructuraTablas.ESCUELA_TABLA_NAME,db);
                adapter.setL(listaEscuela);
                buscar.setText("");
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder d=new AlertDialog.Builder(EscuelaActivity.this);

                View v=getLayoutInflater().inflate(R.layout.dialogo_escuela, null);

                final EditText nom=v.findViewById(R.id.in_nom_escuela);
                final EditText cod=v.findViewById(R.id.in_cod_escuela);
                final Spinner facu=v.findViewById(R.id.spinner_lista_facultad);
                ArrayAdapter adapterFa = new ArrayAdapter(EscuelaActivity.this, android.R.layout.simple_list_item_1, listafacultades);
                facu.setAdapter(adapterFa);

                facu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position != 0) id_facu = facultades.get(position - 1).getId();
                        else id_facu= -1;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                d.setPositiveButton(R.string.agregar_string, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (nom.getText().toString().isEmpty()||id_facu==-1)Toast.makeText(EscuelaActivity.this,R.string.men_camp_vacios,Toast.LENGTH_SHORT).show();
                        else{
                            db=access.open();
                            contentValues=new ContentValues();
                            contentValues.put(EstructuraTablas.COL_2,nom.getText().toString());
                            contentValues.put(EstructuraTablas.COL_1,id_facu);
                            contentValues.put(EstructuraTablas.COL_3,cod.getText().toString());
                            Operaciones_CRUD.insertar(db,contentValues,EscuelaActivity.this, EstructuraTablas.ESCUELA_TABLA_NAME).show();
                            db=access.openRead();
                            listaEscuela=Operaciones_CRUD.todosEscuela(EstructuraTablas.ESCUELA_TABLA_NAME,db);
                            adapter.setL(listaEscuela);
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
    public ArrayList<String> obtenerListaFacultad() {
        ArrayList<String> listaca = new ArrayList<>();
        listaca.add("Seleccione");
        for (int i = 0; i < facultades.size(); i++) {
            listaca.add(facultades.get(i).toString());
        }
        return listaca;
    }

    public void autoComplemento(){
        Vector<String> autocomplemento = new Vector<String>();
        for(int i =0;i<=listaEscuela.size()-1;i++){
            autocomplemento.add(listaEscuela.get(i).getNombre());
        }
        buscar = (AutoCompleteTextView) findViewById(R.id.auto);
        ArrayAdapter<String> adapterComplemento = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                autocomplemento);
        buscar.setAdapter(adapterComplemento);
    }
}
