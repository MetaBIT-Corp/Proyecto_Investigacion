package com.example.crud_encuesta.Componentes_EL.Materia;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.crud_encuesta.Componentes_AP.Librerias.SpeechTextToSpeech;
import com.example.crud_encuesta.Componentes_DC.Activities.GpoEmpActivity;
import com.example.crud_encuesta.Componentes_DC.Activities.PreguntaActivity;
import com.example.crud_encuesta.Componentes_EL.Carrera.Carrera;
import com.example.crud_encuesta.Componentes_EL.Escuela.Escuela;
import com.example.crud_encuesta.Componentes_EL.EstructuraTablas;
import com.example.crud_encuesta.Componentes_EL.Operaciones_CRUD;
import com.example.crud_encuesta.Componentes_EL.ModelosAdicionales.Pensum;
import com.example.crud_encuesta.Componentes_MT.Area.AreaActivity;
import com.example.crud_encuesta.DatabaseAccess;
import com.example.crud_encuesta.MainActivity;
import com.example.crud_encuesta.R;
import com.example.crud_encuesta.SubMenuMateriaActivity;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MateriaActivity extends AppCompatActivity {

    /*
    Speech
     */
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    TextToSpeech textToSpeech;
    SpeechRecognizer speechRecognizer;
    /*
    FIN Speech
     */

    SQLiteDatabase db;
    DatabaseAccess access;
    ContentValues contentValues;
    ListView listView;
    MateriaAdapter adapter;
    ArrayList<Pensum> listaPensum = new ArrayList<>();
    ArrayList<Carrera> listaCarreras = new ArrayList<>();
    ArrayList<Escuela> listaEscuelas = new ArrayList<>();
    ArrayList<String> listPensumSpinner = new ArrayList<>();
    ArrayList<String> listCarreraSpinner = new ArrayList<>();
    ArrayList<Materia> listaMateria = new ArrayList<>();


    int id_carrera;
    int id_pensum;
    int rol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materia);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        rol=getIntent().getExtras().getInt("rol_user");

        ImageView btnBuscar=findViewById(R.id.el_find);
        ImageView btnTodos=findViewById(R.id.el_all);
        final EditText buscar=findViewById(R.id.find_nom);
        buscar.setHint(R.string.el_buscarMat);

        listView = findViewById(R.id.list_view_base);
        access = DatabaseAccess.getInstance(MateriaActivity.this);
        db = access.open();



        //listaEscuelas = Operaciones_CRUD.todosEscuela(EstructuraTablas.ESCUELA_TABLA_NAME, db);
        //listaCarreras = Operaciones_CRUD.todosCarrera(db, listaEscuelas);
        //listaPensum = Operaciones_CRUD.todosPensum(db);
        listaMateria = Operaciones_CRUD.todosMateria(db);

        adapter = new MateriaAdapter(MateriaActivity.this, listaMateria, db, this, listaPensum, listaCarreras,listaEscuelas,rol);

        listView.setAdapter(adapter);


        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaMateria=Operaciones_CRUD.todosMateria(db,buscar.getText().toString());
                adapter.setL(listaMateria);
                buscar.setText("");
            }
        });
        btnTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaMateria=Operaciones_CRUD.todosMateria(db);
                adapter.setL(listaMateria);
                buscar.setText("");
            }
        });

        /*listPensumSpinner = obtenerListaPensum();
        listCarreraSpinner = obtenerListaCarrera();*/

        /*
        SPEECH
         */
        com.getbase.floatingactionbutton.FloatingActionButton fab_speech = findViewById(R.id.fab_speech);
        fab_speech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reconocimiento();
            }
        });

        /*
        FINAL SPEECH
         */



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder d = new AlertDialog.Builder(MateriaActivity.this);
                View v = getLayoutInflater().inflate(R.layout.dialogo_materia, null);

                /*final Spinner spinnerC = v.findViewById(R.id.spinner_lista_carrera);
                final Spinner spinnerP = v.findViewById(R.id.spinner_lista_pensum);

                ArrayAdapter adapterCa = new ArrayAdapter(MateriaActivity.this, android.R.layout.simple_list_item_1, listCarreraSpinner);
                ArrayAdapter adapterPe = new ArrayAdapter(MateriaActivity.this, android.R.layout.simple_list_item_1, listPensumSpinner);

                spinnerC.setAdapter(adapterCa);
                spinnerP.setAdapter(adapterPe);*/
                final EditText nom = v.findViewById(R.id.in_nom_mat);
                final EditText cod = v.findViewById(R.id.in_cod_materia);
                final EditText max = v.findViewById(R.id.in_max_preguntas);
                final CheckBox elec = v.findViewById(R.id.check_electiva);
/*
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

                d.setPositiveButton(R.string.agregar_string, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (nom.getText().toString().isEmpty() || cod.getText().toString().isEmpty() || max.getText().toString().isEmpty())
                            Toast.makeText(MateriaActivity.this, R.string.men_camp_vacios, Toast.LENGTH_SHORT).show();
                        else {
                            contentValues = new ContentValues();
                            //contentValues.put(EstructuraTablas.COL_1_MATERIA, id_pensum);
                            //contentValues.put(EstructuraTablas.COL_2_MATERIA, id_carrera);
                            contentValues.put(EstructuraTablas.COL_3_MATERIA, cod.getText().toString());
                            contentValues.put(EstructuraTablas.COL_4_MATERIA, nom.getText().toString());

                            if (elec.isChecked()) contentValues.put(EstructuraTablas.COL_5_MATERIA, 1);
                            else contentValues.put(EstructuraTablas.COL_5_MATERIA, 0);

                            contentValues.put(EstructuraTablas.COL_6_MATERIA, max.getText().toString());

                            Operaciones_CRUD.insertar(db, contentValues, MateriaActivity.this, EstructuraTablas.MATERIA_TABLA_NAME).show();
                            listaMateria = Operaciones_CRUD.todosMateria(db);
                            adapter.setL(listaMateria);
                            //id_carrera = -1;
                            //id_pensum = -1;
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


    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }

    /*
    SPEECH
     */

    public void inicializarTexttoSpeech() {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (textToSpeech.getEngines().size() == 0) {
                    Toast.makeText(MateriaActivity.this,
                            "No posees la instalación necesaria",
                            Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Locale locSpanish = new Locale("spa", "US");
                    textToSpeech.setLanguage(locSpanish);
                    speak("Iniciando");
                }
            }
        });
    }

    public void speak(String s) {
        if (Build.VERSION.SDK_INT >= 21) {
            textToSpeech.speak(
                    s,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
            );
        } else {
            textToSpeech.speak(
                    s,
                    TextToSpeech.QUEUE_FLUSH,
                    null
            );
        }
    }
    public void ttsShutdown() {
        textToSpeech.shutdown();
    }

    public void inicializarSpeech() {

        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int error) {

                }

                @Override
                public void onResults(Bundle results) {
                    List<String> resultado = results.getStringArrayList(
                            SpeechRecognizer.RESULTS_RECOGNITION
                    );

                    processResult(resultado.get(0));
                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });
        }
    }

    public void processResult(String command) {
        command = command.toLowerCase();
        String where2 = "%' OR " +EstructuraTablas.COL_4_MATERIA+ " LIKE '%";
        command = command+"%'";
        String[] parametros = command.split(" ");
        listaMateria=Operaciones_CRUD.todosMateriaSpeech(db,parametros);
        adapter.setL(listaMateria);

        if(listaMateria.size()==0){
            speak("No se encontró ninguna coincidencia");
            Toast.makeText(
                    this,
                    "No se encontró ninguna coincidencia"
                    ,Toast.LENGTH_LONG).show();
        }else {
            speak("Se han encontrado " +listaMateria.size()+ "registros");
            Toast.makeText(
                    this,
                    "Se han encontrado " +listaMateria.size()+ "registros"
                    ,Toast.LENGTH_LONG).show();
        }
    }

    public void reconocimiento() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MateriaActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            speechRecognizer.startListening(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ttsShutdown();
    }

    @Override
    protected void onResume() {
        super.onResume();
        inicializarTexttoSpeech();
        inicializarSpeech();
    }
    /*
    FIN SPEECH
     */


}
