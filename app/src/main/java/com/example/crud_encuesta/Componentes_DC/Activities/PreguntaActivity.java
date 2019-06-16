package com.example.crud_encuesta.Componentes_DC.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crud_encuesta.Componentes_DC.Adaptadores.AdaptadorPregunta;
import com.example.crud_encuesta.Componentes_DC.Adaptadores.AdaptadorPreguntaRecyclerView;
import com.example.crud_encuesta.Componentes_DC.Dao.DaoPregunta;
import com.example.crud_encuesta.Componentes_DC.Objetos.Pregunta;
import com.example.crud_encuesta.Componentes_MT.Area.Area;
import com.example.crud_encuesta.Componentes_MT.Area.AreaActivity;
import com.example.crud_encuesta.Componentes_MT.Excel.Excel;
import com.example.crud_encuesta.R;
import com.example.crud_encuesta.SubMenuMateriaActivity;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class PreguntaActivity extends AppCompatActivity {

    private DaoPregunta dao;
    private AdaptadorPregunta adaptador;
    private AdaptadorPreguntaRecyclerView adaptadorRV;
    private ArrayList<Pregunta> lista_preguntas;
    private Pregunta pregunta;

    private RecyclerView recyclerView;

    private int id_gpo_emp=0;
    private String desc_gpo_emp;
    private  int id_area =0;
    private int id_tipo_item;
    private ImageView btn_buscar;
    private ImageView btn_todo;
    private EditText texto_busqueda;

    //----------------------------------Proyecto de investigacion---------------------------------//
    private ImageButton excelUpload;
    private ImageButton excelDownload;
    private String path_excel="";
    private static final int REQUEST_CODE = 43;
    //----------------------------------------------End-------------------------------------------//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregunta);
        Intent i = getIntent();
        Bundle b = i.getExtras();

        id_gpo_emp = b.getInt("id_gpo_emp");
        desc_gpo_emp = b.getString("desc_gpo_emp");
        id_area = b.getInt("id_area");
        id_tipo_item = b.getInt("id_tipo_item");

        dao = new DaoPregunta(this, id_gpo_emp, id_area, id_tipo_item);

        lista_preguntas = dao.verTodos();
        //adaptador = new AdaptadorPregunta(lista_preguntas,this,dao, id_tipo_item);
        adaptadorRV = new AdaptadorPreguntaRecyclerView(lista_preguntas,this,dao, id_tipo_item);
        /*ListView list = (ListView)findViewById(R.id.lista);
        list.setAdapter(adaptador);*/
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptadorRV);

        FloatingActionButton agregar = findViewById(R.id.btn_nuevo);

        btn_buscar = (ImageView)findViewById(R.id.btn_buscar);
        btn_todo = (ImageView)findViewById(R.id.btn_todo);
        texto_busqueda = (EditText)findViewById(R.id.texto_busqueda);
       /* TextView texto_desc_emp = (TextView)findViewById(R.id.txt_desc_emp);
        texto_desc_emp.setText(desc_gpo_emp);
        Button editar_gpo_emp = (Button)findViewById(R.id.btn_editar_desc_emp);

        editar_gpo_emp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(PreguntaActivity.this, GpoEmpActivity.class);
                in.putExtra("id_gpo_emp",id_gpo_emp);
                in.putExtra("id_tipo_item",3);
                in.putExtra("accion",1);
                in.putExtra("id_area",id_area);
                startActivity(in);
            }
        });*/

        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(PreguntaActivity.this);
                dialog.setTitle(R.string.nueva_preg);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialogo_pregunta);
                dialog.show();

                final EditText texto_pregunta = (EditText)dialog.findViewById(R.id.editt_pregunta);
                Button agregar = (Button)dialog.findViewById(R.id.btn_agregar);
                Button cancelar = (Button)dialog.findViewById(R.id.btn_cancelar);
                TextView texto_titulo = (TextView)dialog.findViewById(R.id.texto_titulo);
                texto_titulo.setText(R.string.agregar_preg);
                agregar.setText(R.string.btn_agregar);
                agregar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try{
                            if(!texto_pregunta.getText().toString().equals("")){

                                pregunta = new Pregunta(id_gpo_emp, texto_pregunta.getText().toString());
                                dao.insertar(pregunta);
                                adaptadorRV.notifyDataSetChanged();
                                lista_preguntas = dao.verTodos();
                                dialog.dismiss();

                            }else{
                                Toast.makeText(v.getContext(), R.string.msg_falta_texto_preg, Toast.LENGTH_SHORT).show();
                                texto_pregunta.setFocusable(true);
                            }

                        }catch (Exception e){
                            //Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT);
                        }
                    }
                });

                cancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        btn_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!texto_busqueda.getText().toString().isEmpty()){

                    lista_preguntas = dao.busqueda(texto_busqueda.getText().toString());
                    adaptador.dataChange(lista_preguntas);
                }else{
                    Toast.makeText(
                            v.getContext(),
                            R.string.msg_consulta_nula,
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });

        btn_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lista_preguntas = dao.verTodos();
                texto_busqueda.setText("");
                adaptador.dataChange(lista_preguntas);
            }
        });

        //--------------------------------Proyecto de investigacion-------------------------------//
        excelDownload = findViewById(R.id.excelDownload);
        excelUpload = findViewById(R.id.excelUpload);

        //Si no viene de grupo emparejamiento poner objetos a la escucha del evento
        if(id_gpo_emp==0){
            excelUpload.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    permisoAlmacenamiento();
                    cargarExcel();
                }
            });

            excelDownload.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    permisoAlmacenamiento();
                    descargarExcel();

                }
            });
            // Si viene de grupo emparejamiento ocultar los iconos del activity pregunta
        }else {
            excelDownload.setVisibility(View.GONE);
            excelUpload.setVisibility(View.GONE);
        }
        //------------------------------------------End-------------------------------------------//

    }

    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
        //do whatever you want the 'Back' button to do
        //as an example the 'Back' button is set to start a new Activity named 'NewActivity'
        /*if (id_tipo_item!=3) {
            startActivity(new Intent(PreguntaActivity.this, AreaActivity.class));
            finish();
        }else{
            startActivity(new Intent(PreguntaActivity.this, GpoEmpActivity.class));
            finish();
        }*/
    }

    //----------------------------------Proyecto de investigacion---------------------------------//
    //Resultado de elegir el archivo desde el almacenamiento
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if(data != null){
                path_excel = data.getData().getPath();

                Excel excel = new Excel(this);
                if(excel.leerExcel(path_excel, id_area)>0){
                    Toast.makeText(this, "Se ha importado conéxito", Toast.LENGTH_SHORT).show();
                    adaptadorRV.notifyDataSetChanged();
                    lista_preguntas = dao.verTodos();
                }else{
                    Toast.makeText(this, "Hubo un error o el documento está vacío", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //Resultado de si se acepto o no el permiso de almacenamiento en el dispositivo
    @Override
    public void onRequestPermissionsResult (int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case 1001:{
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    //Consultar permiso para leer y escribir en el dispositivo
    public void permisoAlmacenamiento(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1001);
            }
        }
    }

    //Guardar excel en el dispositivo
    public void descargarExcel(){
        final EditText etFilename = new EditText(this);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);

        mBuilder.setMessage("Ingrese el nombre con el que desee guardar el archivo excel");
        mBuilder.setIcon(R.drawable.infoazul);
        mBuilder.setTitle("Excel");
        mBuilder.setView(etFilename);

        mBuilder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String filename = etFilename.getText().toString();
                Excel excel = new Excel(PreguntaActivity.this);
                excel.crearExcel(PreguntaActivity.this, filename);
            }
        });

        mBuilder.setNegativeButton(R.string.mt_cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    //Subir preguntas desde el dispositivo
    public void cargarExcel(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE);
    }
    //----------------------------------------------End-------------------------------------------//
}
