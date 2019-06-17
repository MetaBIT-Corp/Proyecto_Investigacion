package com.example.crud_encuesta.Componentes_MR.Estudiante;

import android.app.Dialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.crud_encuesta.Componentes_AP.Models.Usuario;
import com.example.crud_encuesta.Componentes_MR.Funciones;
import com.example.crud_encuesta.DatabaseAccess;
import com.example.crud_encuesta.R;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ActivityEstudiante extends AppCompatActivity {

    private DAOEstudiante dao;
    private AdaptadorEstudiante adapter;
    private ArrayList<Estudiante> lista = new ArrayList<>();
    private Estudiante estudiante;
    private Usuario usuario;
    private SQLiteDatabase db;
    private DatabaseAccess access;

    Map<String, Integer> mapIndex;
    private ListView list;

    Session session = null;
    String rec;
    String subject="Creación de Usuario Estudiante";
    String textMessage;

    AutoCompleteTextView buscar;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        ArrayList<String> fruits = new ArrayList<>();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estudiante);

        dao = new DAOEstudiante(this);
        lista = dao.verTodos();

        for (int i=0;i<lista.size();i++){
            fruits.add(lista.get(i).getCarnet());
        }

        adapter = new AdaptadorEstudiante(this,lista,dao);
        access = DatabaseAccess.getInstance(ActivityEstudiante.this);
        db = access.open();

        final ListView list = (ListView) findViewById(R.id.lista_estudiante);
        //final EditText buscar=findViewById(R.id.find_nom);

        FloatingActionButton agregar = (FloatingActionButton) findViewById(R.id.btn_nuevo_estudiante);
        ImageView btnBuscar=findViewById(R.id.el_find);
        ImageView btnTodos=findViewById(R.id.el_all);

        /*Llenando Lista de Estudiantes en DB*/
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

        getIndexList(fruits);
        displayIndex(list);

        /*Botón para Nuevo Estudiante*/
        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Declarando Dialogo para Ingreso de Nuevo Estudiante*/
                final Dialog dialogo =new Dialog(ActivityEstudiante.this);
                dialogo.setTitle("Registro de Estudiante");
                dialogo.setCancelable(true);
                dialogo.setContentView(R.layout.dialogo_estudiante);
                dialogo.show();
                dialogo.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                /*Llamando y declarando las Vistas*/
                    /*TextView, EditText, Spinners, etc...*/
                final TextView mensaje = (TextView) dialogo.findViewById(R.id.toolbar_std);
                final EditText carnet = (EditText) dialogo.findViewById(R.id.editt_carnet_estd);
                final EditText nombre = (EditText) dialogo.findViewById(R.id.editt_nombre_estd);
                final CheckBox activo = (CheckBox) dialogo.findViewById(R.id.cb_actividad_estd);
                final EditText anio_ingreso = (EditText) dialogo.findViewById(R.id.editt_anio_ingreso_estd);

                    /*Buttons*/
                Button btn_anio = (Button) dialogo.findViewById(R.id.btn_agregar_anio);
                Button guardar = (Button) dialogo.findViewById(R.id.btn_agregar_estd);
                Button cancelar = (Button) dialogo.findViewById(R.id.btn_cancelar_estd);

                mensaje.setText(R.string.est_titulo2);
                guardar.setText(R.string.btn_registrar);
                cancelar.setText(R.string.btn_cancelar);

                Funciones.setBtnAnio(dialogo, btn_anio, anio_ingreso);/*Seteo de Anio en Vista*/

                /*Guardado de Objeto Estudiante(Creación de Objeto y envio a DAO)*/
                guardar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String errores="";

                        errores += Funciones.comprobarCampo(carnet,getResources().getString(R.string.carnet_v),ActivityEstudiante.this);
                        errores += Funciones.comprobarCampo(nombre,getResources().getString(R.string.nombre_v),ActivityEstudiante.this);
                        errores += Funciones.comprobarCampo(anio_ingreso,getResources().getString(R.string.anioI_v),ActivityEstudiante.this);
                        errores += Funciones.comprobarAnio(anio_ingreso,ActivityEstudiante.this);

                        if (errores.isEmpty()){

                            int check;
                            if(activo.isChecked()){
                                check = 1;
                            }else{
                                check = 0;
                            }

                            usuario = new Usuario(
                                    carnet.getText().toString(),
                                    carnet.getText().toString(),
                                    2
                            );
                            dao.insertarUsuario(usuario);
                            usuario = dao.usuarioNombre(usuario.getNOMUSUARIO());

                            estudiante = new Estudiante(
                                    carnet.getText().toString().trim(),
                                    nombre.getText().toString(),
                                    check,
                                    anio_ingreso.getText().toString(),
                                    usuario.getIDUSUARIO());
                            dao.insertar(estudiante);

                            /*Dialogo para Aviso sobre nuevo User creado con credenciales.*/
                            /*final AlertDialog.Builder usrAlert= new AlertDialog.Builder(ActivityEstudiante.this);
                            int clave_tamanio = usuario.getCLAVE().length();
                            String astericos ="";
                            for(int i=0;i<clave_tamanio-2;i++){
                                astericos+="*";
                            }
                            String clave_formateada=(usuario.getCLAVE().substring(0,2)+astericos);
                            usrAlert.setMessage(getResources().getString(R.string.alertUser)+"\n\n"+getResources().getString(R.string.usuario)+usuario.getNOMUSUARIO()+"\n"+getResources().getString(R.string.clave)+clave_formateada);
                            usrAlert.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {}
                            });*/

                            rec = carnet.getText().toString().trim()+("@ues.edu.sv");
                            textMessage="¡Felicidades "+nombre.getText().toString()+"! Su usuario con credenciales de <b>Estudiante</b> ha sido creado exitosamente.<br><br>" +
                                    "Sus credenciales son:<br><br>" +
                                    "<b>Usuario</b>: "+carnet.getText().toString()+"<br>" +
                                    "<b>Contraseña</b>: "+carnet.getText().toString()+"<br><br>" +
                                    "Muchas gracias por utilizar la aplicación de Encuestas de la FIA/UES";

                            Properties props = new Properties();
                            props.put("mail.smtp.host", "smtp.gmail.com");
                            props.put("mail.smtp.socketFactory.port", "465");
                            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                            props.put("mail.smtp.auth", "true");
                            props.put("mail.smtp.port", "465");

                            session = Session.getDefaultInstance(props, new Authenticator() {
                                protected PasswordAuthentication getPasswordAuthentication() {
                                    return new PasswordAuthentication("remitentemoviles@gmail.com", "Moviles115");
                                }
                            });

                            RetreiveFeedTask task = new RetreiveFeedTask();
                            task.execute();

                            /*usrAlert.show();*/
                            lista = dao.verTodos();
                            adapter.notifyDataSetChanged();
                            autoComplemento();
                            dialogo.dismiss();

                        } else {
                            Toast.makeText(getApplicationContext(),errores+"\n"+getResources().getString(R.string.rellene_v), Toast.LENGTH_LONG).show();
                        }
                    }
                });

                /*Cancelación de Nuevo Estudiante*/
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

    class RetreiveFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try{
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("testfrom354@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(rec));
                message.setSubject(subject);
                message.setContent(textMessage, "text/html; charset=utf-8");
                Transport.send(message);
            } catch(MessagingException e) {
                e.printStackTrace();
            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), "¡Estudiante y usuario creados!\nCorreo Electrónico Enviado.", Toast.LENGTH_LONG).show();
        }
    }

    private void getIndexList(ArrayList<String> fruits) {
        mapIndex = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < fruits.size(); i++) {
            String fruit = fruits.get(i);
            String index = fruit.substring(0, 1).toUpperCase();

            if (mapIndex.get(index) == null)
                mapIndex.put(index, i);
        }
    }

    private void displayIndex(final ListView list) {
        LinearLayout indexLayout = (LinearLayout) findViewById(R.id.side_index);

        TextView textView;
        List<String> indexList = new ArrayList<String>(mapIndex.keySet());
        for (String index : indexList) {
            textView = (TextView) getLayoutInflater().inflate(
                    R.layout.side_index_item, null);
            textView.setText(index);
            final TextView finalTextView = textView;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    lista = dao.verBusquedaIndex(finalTextView.getText().toString());
                    if((lista != null) && (lista.size() > 0)){
                        list.setAdapter(adapter);
                    }
                }
            });
            indexLayout.addView(textView);
        }
    }
}