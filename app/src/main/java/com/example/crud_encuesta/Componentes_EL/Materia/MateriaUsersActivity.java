package com.example.crud_encuesta.Componentes_EL.Materia;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.crud_encuesta.Componentes_EL.Operaciones_CRUD;
import com.example.crud_encuesta.Componentes_EL.WebService.WS_Materia;
import com.example.crud_encuesta.DatabaseAccess;
import com.example.crud_encuesta.R;

import java.util.ArrayList;

public class MateriaUsersActivity extends AppCompatActivity {
    SQLiteDatabase db;
    DatabaseAccess access;
    ContentValues contentValues;
    ListView listView;
    MateriaUserAdapter adapter;
    ArrayList<Materia> listaMateria = new ArrayList<>();
    int id;
    int rol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materia_users);

        rol=getIntent().getExtras().getInt("rol_user");
        id=getIntent().getExtras().getInt("id_user");

        WS_Materia ws_materia=new WS_Materia(7,this);


        LinearLayout l=findViewById(R.id.linearBusqueda);
        l.setVisibility(View.GONE);

        listView = findViewById(R.id.list_view_base);
        access = DatabaseAccess.getInstance(MateriaUsersActivity.this);
        db = access.open();
        listaMateria= Operaciones_CRUD.todosMateria(db,rol,id);
        adapter=new MateriaUserAdapter(MateriaUsersActivity.this,ws_materia.getMateriasWS(),db,this,id,rol);

        listView.setAdapter(adapter);
    }
}
