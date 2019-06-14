package com.example.crud_encuesta.Componentes_AP.Librerias;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
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
import android.widget.Toast;

import com.example.crud_encuesta.Componentes_EL.Materia.Materia;
import com.example.crud_encuesta.Componentes_EL.Materia.MateriaAdapter;
import com.example.crud_encuesta.Componentes_EL.Operaciones_CRUD;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SpeechTextToSpeech {
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    Context context;
    Activity activity;
    TextToSpeech textToSpeech;
    SpeechRecognizer speechRecognizer;


    public SpeechTextToSpeech(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;

    }

    public void inicializarTexttoSpeech() {
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (textToSpeech.getEngines().size() == 0) {
                    Toast.makeText(context,
                            "No posees la instalación necesaria",
                            Toast.LENGTH_LONG).show();
                    activity.finish();
                } else {
                    Locale locSpanish = new Locale("spa", "MEX");
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

        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
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
        Toast.makeText(context,command,Toast.LENGTH_LONG).show();

        /*
        if (command.indexOf("cuál") != -1) {
            if (command.indexOf("nombre") != -1) {
                speak("Mi nombre es Edwin");
            }
            if (command.indexOf("materia") != -1) {
                speak("Mate 3");
            }
        } else if (command.indexOf("página") != -1) {
            if (command.indexOf("web") != -1) {
                Intent intent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://eisi.fia.ues.edu.sv/"));
                activity.startActivity(intent);
            }
        }*/

    }

    public void reconocimiento() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.RECORD_AUDIO)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(activity,
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

}
