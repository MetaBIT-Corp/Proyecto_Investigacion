package com.example.crud_encuesta.Componentes_DC.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crud_encuesta.Componentes_DC.Adaptadores.Adaptador;
import com.example.crud_encuesta.Componentes_DC.Dao.DaoGrupoEmp;
import com.example.crud_encuesta.Componentes_DC.Objetos.GrupoEmparejamiento;
import com.example.crud_encuesta.Componentes_MT.Area.AreaActivity;
import com.example.crud_encuesta.Componentes_MT.Excel.Excel;
import com.example.crud_encuesta.R;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class GpoEmpActivity extends AppCompatActivity {

    private DaoGrupoEmp dao;
    private Adaptador adaptador;
    private ArrayList<GrupoEmparejamiento> lista_gpo_emp;
    private GrupoEmparejamiento gpo_emp;
    private int id_area;
    private int id_tipo_item;
    private int accion;

    //----------------------------------Proyecto de investigacion---------------------------------//
    private ImageButton excelUpload;
    private ImageButton excelDownload;
    private String path_excel="";
    //----------------------------------------------End-------------------------------------------//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grupo_emparejamiento);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        id_area = b.getInt("id_area");
        dao = new DaoGrupoEmp(this, id_area);
        lista_gpo_emp = dao.verTodos();
        adaptador = new Adaptador(lista_gpo_emp, this, dao);

        //Inicio
        id_tipo_item = b.getInt("id_tipo_item");
        if(id_tipo_item!=3)finish();
        accion = b.getInt("accion");

        //Fin

        FloatingActionButton agregar = (FloatingActionButton)findViewById(R.id.btn_nuevo);
        ListView list = (ListView)findViewById(R.id.lista);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(GpoEmpActivity.this);
                dialog.setTitle(R.string.nuevo_gpo_emp);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialogo_gpo_emp);
                dialog.show();

                final EditText descripcion = (EditText)dialog.findViewById(R.id.descripcion);
                Button agregar = (Button)dialog.findViewById(R.id.btn_agregar);
                Button cancelar = (Button)dialog.findViewById(R.id.btn_cancelar);
                TextView texto_titulo = (TextView)dialog.findViewById(R.id.texto_titulo);
                texto_titulo.setText(R.string.agregar_gpo_emp);
                agregar.setText(R.string.btn_agregar);
                agregar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try{

                            if(!descripcion.getText().toString().equals("")){

                                gpo_emp = new GrupoEmparejamiento(descripcion.getText().toString());
                                dao.insertar(gpo_emp);
                                lista_gpo_emp = dao.verTodos();
                                dialog.dismiss();

                                //INICIO

                                //FIN

                            }else{
                                Toast.makeText(v.getContext(), R.string.msg_falta_desc_gpo_emp, Toast.LENGTH_SHORT).show();
                                descripcion.setFocusable(true);
                            }


                        }catch (Exception e){
                            //Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT);
                        }
                    }
                });

                cancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in = new Intent(v.getContext(), AreaActivity.class);
                        startActivity(in);
                        dialog.dismiss();
                    }
                });
            }
        });

        //INICIO
        if(id_tipo_item==3){

            list.setAdapter(adaptador);
                /*if(accion == 0){

                    if(lista_gpo_emp.isEmpty()){
                        agregar.performClick();
                    }else{

                        Intent in = new Intent(this, PreguntaActivity.class);
                        gpo_emp = lista_gpo_emp.get(0);
                        in.putExtra("id_gpo_emp",gpo_emp.getId());
                        in.putExtra("desc_gpo_emp",gpo_emp.getDescripcion());
                        startActivity(in);
                    }

                }else{
                    Toast.makeText(this, "Tamanio: "+lista_gpo_emp.size(), Toast.LENGTH_SHORT).show();
                    adaptador = new Adaptador(lista_gpo_emp, this, dao);
                    ListView list = (ListView)findViewById(R.id.lista);
                    list.setAdapter(adaptador);
                }*/

        }else{
            Intent in = new Intent(this, PreguntaActivity.class);
            in.putExtra("id_gpo_emp",0);
            in.putExtra("id_tipo_item",id_tipo_item);
            in.putExtra("id_area",id_area);
            startActivity(in);
            finish();
        }
        //FIN

        //--------------------------------Proyecto de investigacion-------------------------------//
        excelDownload = findViewById(R.id.excelDownload);
        excelUpload = findViewById(R.id.excelUpload);

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
        //------------------------------------------End-------------------------------------------//
    }

    //----------------------------------Proyecto de investigacion---------------------------------//
    //Resultado de elegir el archivo desde el almacenamiento
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == RESULT_OK) {
            path_excel = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);

            Excel excel = new Excel(this);
            if(excel.leerExcelEM(path_excel, id_area)>0){
                Toast.makeText(this, "Se ha importado conéxito", Toast.LENGTH_SHORT).show();
                adaptador.notifyDataSetChanged();
                lista_gpo_emp = dao.verTodos();
            }else{
                Toast.makeText(this, "Hubo un error o el documento está vacío", Toast.LENGTH_SHORT).show();
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
                Excel excel = new Excel(GpoEmpActivity.this);
                excel.crearExcelEM(GpoEmpActivity.this, filename);
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
        new MaterialFilePicker()
                .withActivity(GpoEmpActivity.this)
                .withRequestCode(1000)
                .withFilter(Pattern.compile(".*\\.xls$")) // Filtering files and directories by file name using regexp
                // .withFilterDirectories(true) // Set directories filterable (false by default)
                .withHiddenFiles(true) // Show hidden files and folders
                .start();
    }
    //----------------------------------------------End-------------------------------------------//

}
