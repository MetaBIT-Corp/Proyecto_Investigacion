package com.example.crud_encuesta.Componentes_MR.Docente;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.crud_encuesta.Componentes_AP.Models.Usuario;
import com.example.crud_encuesta.Componentes_EL.Escuela.Escuela;
import com.example.crud_encuesta.Componentes_EL.Operaciones_CRUD;
import com.example.crud_encuesta.Componentes_MR.Funciones;
import com.example.crud_encuesta.DatabaseAccess;
import com.example.crud_encuesta.R;
import java.util.ArrayList;
import java.util.Vector;

public class ActivityDocente extends AppCompatActivity {

    private DAODocente dao;
    private AdaptadorDocente adapter;
    private ArrayList<Docente> lista;
    private Docente docente;
    private Usuario usuario;
    private SQLiteDatabase db;
    private DatabaseAccess access;
    private String tableName = "ESCUELA";
    private ArrayList<Escuela> escuelas = new ArrayList<>();
    private ArrayList<String> listaEscuelas = new ArrayList<>();
    private int id_escuela;

    AutoCompleteTextView buscar;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docente);

        dao = new DAODocente(this);
        lista = dao.verTodos();

        adapter = new AdaptadorDocente(this,lista,dao);
        access=DatabaseAccess.getInstance(ActivityDocente.this);
        db=access.open();

        escuelas=Operaciones_CRUD.todosEscuela(tableName,db); /*Lista con Objetos Escuela en DB*/
        listaEscuelas = Funciones.obtenerListaEscuelas(escuelas); /*Lista con Strings de Escuelas*/

        final ListView list = (ListView) findViewById(R.id.lista_docente);
        FloatingActionButton agregar = (FloatingActionButton) findViewById(R.id.btn_nuevo_docente);
        ImageView btnBuscar=findViewById(R.id.el_find);
        ImageView btnTodos=findViewById(R.id.el_all);
        //final EditText buscar=findViewById(R.id.find_nom);

        /*Llenando Lista de Docentes en DB*/
        if((lista != null) && (lista.size() > 0)){
            list.setAdapter(adapter);
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {}
        });

        autoComplemento();

        /*Botón de Busqueda*/
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lista = dao.verBusqueda(buscar.getText().toString());
                if((lista != null) && (lista.size() > 0)){
                    list.setAdapter(adapter);
                }
            }
        });

        /*Botón para mostrar todos*/
        btnTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lista = dao.verTodos();
                if((lista != null) && (lista.size() > 0)){
                    list.setAdapter(adapter);
                }
            }
        });


        /*Botón para Nuevo Docente*/
        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Declarando Dialogo para Ingreso de Nuevo Docente*/
                final Dialog dialogo =new Dialog(ActivityDocente.this);
                dialogo.setTitle("Registro de Docente");
                dialogo.setCancelable(true);
                dialogo.setContentView(R.layout.dialogo_docente);
                dialogo.show();
                dialogo.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                /*Llamando y declarando las Vistas*/
                    /*TextView, EditText, Spinners, etc...*/
                final Spinner sp_escuela = (Spinner) dialogo.findViewById(R.id.sp_escuela);
                final EditText carnet = (EditText) dialogo.findViewById(R.id.editt_carnet);
                final EditText anio_titulo = (EditText) dialogo.findViewById(R.id.editt_anio_titulo);
                final CheckBox activo = (CheckBox) dialogo.findViewById(R.id.cb_actividad);
                final EditText tipo_jornada = (EditText) dialogo.findViewById(R.id.editt_tipo_jornada);
                final EditText descripcion = (EditText) dialogo.findViewById(R.id.editt_descripcion);
                final EditText cargo_actual = (EditText) dialogo.findViewById(R.id.editt_cargo_actual);
                final EditText cargo_secundario = (EditText) dialogo.findViewById(R.id.editt_cargo_secundario);
                final EditText nombre = (EditText) dialogo.findViewById(R.id.editt_nombre);
                final TextView mensaje = (TextView) dialogo.findViewById(R.id.toolbar_docente);

                    /*Buttons*/
                Button btn_anio = (Button) dialogo.findViewById(R.id.btn_agregar_anio);
                Button guardar =(Button) dialogo.findViewById(R.id.btn_agregar_dcn);
                Button cancelar = (Button) dialogo.findViewById(R.id.btn_cancelar_dcn);

                mensaje.setText(R.string.dcn_titulo_registrar);
                guardar.setText(R.string.btn_registrar);

                /*Seteando Valores en Vistas*/
                    /*Obteniendo Valores de Spinner de Escuelas*/
                ArrayAdapter adapterEs = new ArrayAdapter(ActivityDocente.this, android.R.layout.simple_list_item_1, listaEscuelas);
                sp_escuela.setAdapter(adapterEs);
                sp_escuela.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position != 0){
                            id_escuela = escuelas.get(position).getId();
                        } else {
                            id_escuela = 1;
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
                    /*Fin de Spinner Materias*/

                Funciones.setBtnAnio(dialogo,btn_anio,anio_titulo); /*Seteando valor a vista de Año Título.*/

                /*Guardado de Objeto Docente (Creación de Objeto y envio a DAO).*/
                guardar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String errores="";
                        errores += Funciones.comprobarCampo(carnet,getResources().getString(R.string.carnet_v),ActivityDocente.this);
                        errores += Funciones.comprobarCampo(anio_titulo,getResources().getString(R.string.aniot_v),ActivityDocente.this);
                        errores += Funciones.comprobarAnio(anio_titulo,ActivityDocente.this);
                        errores += Funciones.comprobarCampo(tipo_jornada,getResources().getString(R.string.tipoj_v),ActivityDocente.this);
                        errores += Funciones.comprobarCampo(cargo_actual,getResources().getString(R.string.cargoa_v),ActivityDocente.this);
                        errores += Funciones.comprobarCampo(cargo_secundario,getResources().getString(R.string.cargos_v),ActivityDocente.this);
                        errores += Funciones.comprobarCampo(nombre,getResources().getString(R.string.nombre_v),ActivityDocente.this);

                        if(errores.isEmpty()) {
                            int checki;
                            if(activo.isChecked()){
                                checki = 1;
                            }else{
                                checki = 0;
                            }

                            usuario = new Usuario(
                                    carnet.getText().toString(),
                                    carnet.getText().toString(),
                                    1
                            );
                            dao.insertarUsuario(usuario);
                            usuario = dao.usuarioNombre(usuario.getNOMUSUARIO());

                            docente = new Docente(
                                    id_escuela,
                                    carnet.getText().toString().trim(),
                                    anio_titulo.getText().toString(),
                                    checki,
                                    Integer.parseInt(tipo_jornada.getText().toString()),
                                    descripcion.getText().toString(),
                                    Integer.parseInt(cargo_actual.getText().toString()),
                                    Integer.parseInt(cargo_secundario.getText().toString()),
                                    nombre.getText().toString(),
                                    usuario.getIDUSUARIO());

                            dao.insertar(docente);

                            /*Dialogo para Aviso sobre nuevo User creado con credenciales.*/
                            final AlertDialog.Builder usrAlert= new AlertDialog.Builder(ActivityDocente.this);
                            int clave_tamanio = usuario.getCLAVE().length();
                            String astericos ="";
                            for(int i=0;i<clave_tamanio-2;i++){
                                astericos+="*";
                            }
                            String clave_formateada=(usuario.getCLAVE().substring(0,2)+astericos);
                            usrAlert.setMessage("Usuario de Docente creado:\n\n"+"Usuario: "+usuario.getNOMUSUARIO()+"\nClave: "+clave_formateada);
                            usrAlert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {}
                            });
                            usrAlert.show();

                            adapter.notifyDataSetChanged();
                            lista = dao.verTodos();
                            autoComplemento();
                            dialogo.dismiss();
                        }else {
                            Toast.makeText(getApplicationContext(), errores+"\n"+getResources().getString(R.string.rellene_v), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                /*Cancelación de Nuevo Docente*/
                cancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogo.dismiss();
                    }
                });
            }
        });
    }
    public void autoComplemento(){
        Vector<String> autocomplemento = new Vector<String>();
        for(int i =0;i<=lista.size()-1;i++){
            autocomplemento.add(lista.get(i).getNombre());
            autocomplemento.add(lista.get(i).getCarnet());
        }
        buscar = (AutoCompleteTextView) findViewById(R.id.auto);
        ArrayAdapter<String> adapterComplemento = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                autocomplemento);
        buscar.setAdapter(adapterComplemento);
    }
}