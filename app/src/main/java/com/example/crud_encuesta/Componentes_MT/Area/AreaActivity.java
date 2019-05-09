package com.example.crud_encuesta.Componentes_MT.Area;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.crud_encuesta.DatabaseAccess;
import com.example.crud_encuesta.R;

import java.util.ArrayList;
import java.util.List;

public class AreaActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    DAOArea daoArea;
    List<Area> areas = new ArrayList<>();
    AreaAdapter areaAdapter;

    private Spinner spItems;
    private ArrayAdapter<String> comboAdapter;
    private List <String> items;
    private int[] iconos={R.drawable.edit1, R.drawable.ic_delete};

    private ImageView add;
    private EditText mArea;
    private ListView listView;

    //Datos de otros modelos
    private int seleccion_item=0;
    private int id_cat_mat = 1;
    private int id_pdg_dcn = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area);

        daoArea = new DAOArea(this);
        areas = daoArea.getAreas();
        areaAdapter = new AreaAdapter(this, areas, daoArea, iconos);

        cargarItems();
        listView = (ListView)findViewById(R.id.list_areas);
        listView.setAdapter(areaAdapter);
        add = (ImageView)findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregar_area();
            }
        });

    }

    public void cargarItems(){
        spItems = (Spinner) findViewById(R.id.items);
        spItems.setOnItemSelectedListener(this);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        SQLiteDatabase db = databaseAccess.open();
        //SQLiteDatabase db = databaseAccess.database();

        items = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT nombre_tipo_item FROM tipo_item", null);

        while (cursor.moveToNext()) {
            items.add(cursor.getString(0));
        }
        cursor.close();

        databaseAccess.close();

        comboAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, items);
        spItems.setAdapter(comboAdapter);
    }

    public void agregar_area(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(AreaActivity.this);
        final View mView = getLayoutInflater().inflate(R.layout.dialogo_area, null);

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mArea = (EditText)mView.findViewById(R.id.etArea);

                String titulo_area = mArea.getText().toString();

                if(!titulo_area.isEmpty()){
                    Area area = new Area(titulo_area, id_cat_mat, id_pdg_dcn, seleccion_item);
                    daoArea.insertar(area);
                    areas = daoArea.getAreas();
                    areaAdapter.notifyDataSetChanged();
                    Toast.makeText(AreaActivity.this, "Area agregada con éxito", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(AreaActivity.this, "Ingrese el título del área", Toast.LENGTH_SHORT).show();
                }
                refresh();
            }
        });

        mBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.items:

                //Toast.makeText(this, "Modalidad: " + valorItem[position], Toast.LENGTH_SHORT).show();
                seleccion_item = position+1;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void refresh(){
        listView.setAdapter(new AreaAdapter(this, areas, daoArea, iconos));
    }



}