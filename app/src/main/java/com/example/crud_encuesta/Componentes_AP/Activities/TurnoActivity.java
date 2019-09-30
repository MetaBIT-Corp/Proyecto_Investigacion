package com.example.crud_encuesta.Componentes_AP.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.crud_encuesta.Componentes_AP.Adapters.AdapterEvaluacion;
import com.example.crud_encuesta.Componentes_AP.Adapters.AdapterTurno;
import com.example.crud_encuesta.Componentes_AP.DAO.DAOEvaluacion;
import com.example.crud_encuesta.Componentes_AP.DAO.DAOTurno;
import com.example.crud_encuesta.Componentes_AP.DAO.DAOUsuario;
import com.example.crud_encuesta.Componentes_AP.Models.Evaluacion;
import com.example.crud_encuesta.Componentes_AP.Models.Turno;
import com.example.crud_encuesta.Componentes_AP.Models.Usuario;
import com.example.crud_encuesta.Componentes_MT.Intento.IntentoActivity;
import com.example.crud_encuesta.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TurnoActivity extends AppCompatActivity {

    DAOTurno daoTurno;
    DAOUsuario daoUsuario;
    Usuario usuario;
    AdapterTurno adapterTurno;
    ArrayList<Turno> turnos;
    Turno turno;
    private int anio, mes, dia, hora, minuto;
    int id_evaluacion;
    int id_estudiante;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turno);

        //recuperamos el id de la evaluación que nos mandan
        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null){
            id_evaluacion = bundle.getInt("id_evaluacion");
            id_estudiante = bundle.getInt("id_estudiante");
        }

        daoUsuario = new DAOUsuario(this);
        usuario = daoUsuario.getUsuarioLogueado();

        daoTurno = new DAOTurno(this);
        turnos = daoTurno.verTodos(id_evaluacion);
        adapterTurno = new AdapterTurno(turnos,daoTurno,this, this);


        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.ap_fab_agregar_turno);
        ImageView buscar = (ImageView) findViewById(R.id.ap_imgv_buscar_turno);
        ImageView all = (ImageView) findViewById(R.id.ap_imgv_all_turno);
        final EditText edt_buscar = (EditText) findViewById(R.id.ap_edt_buscar_turno);

        if(usuario.getROL()==0 || usuario.getROL()==2){
            add.setVisibility(View.INVISIBLE);
        }

        ListView listView = (ListView) findViewById(R.id.lista_turno);
        listView.setAdapter(adapterTurno);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                if(usuario.getROL()==2) {
                    LayoutInflater inflater = (LayoutInflater) getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    turno = turnos.get(position);
                    final int id_turno_intento = turno.getId();

                    //creamos alert dialog
                    AlertDialog.Builder pass_emergente = new AlertDialog.Builder(TurnoActivity.this);
                    pass_emergente.setCancelable(true);
                    View v_pass = inflater.inflate(R.layout.contrasenia_layout, null);


                    //enlazamos views del dialogo
                    final TextView tv_dateinicial = (TextView) v_pass.findViewById(R.id.ap_tv_indicaciones);
                    final EditText pass_turno = (EditText) v_pass.findViewById(R.id.ap_edt_pass_intento);

                    pass_emergente.setPositiveButton("Ingresar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!pass_turno.getText().toString().isEmpty()) {
                                if ( pass_turno.getText().toString().equals(turno.getContrasenia())) {
                                    Intent intent = new Intent(view.getContext(), IntentoActivity.class);
                                    intent.putExtra("id_turno_intento", id_turno_intento);
                                    intent.putExtra("id_estudiante",id_estudiante);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(TurnoActivity.this,
                                            getResources().getText(R.string.ap_contrasenia_no_valida),
                                            Toast.LENGTH_SHORT).show();
                                    pass_turno.setText("");
                                }
                            } else {
                                Toast.makeText(TurnoActivity.this,
                                        getResources().getText(R.string.ap_llena_los_campos),
                                        Toast.LENGTH_SHORT).show();
                                pass_turno.setText("");
                            }
                        }
                    });

                    pass_emergente.setNegativeButton(getResources().getText(R.string.ap_cancelar), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // no esperamos que haga nada al cerrar, solo se cierra
                        }
                    });
                    pass_emergente.setView(v_pass);
                    pass_emergente.show();

                }
            }
        });

        //TODO: Listeners

        //Inicio listener de agregar
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //dialogo de crear

                final Dialog dialog = new Dialog(TurnoActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//para quitar el titulo
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialogo_turno);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                dialog.show();

                //enlazamos views del dialogo turno
                final TextView titulo = (TextView) dialog.findViewById(R.id.ap_tv_title_dialogo_turno);
                final EditText dateinicial = (EditText) dialog.findViewById(R.id.ap_edt_dateinicial);
                final EditText datefinal = (EditText) dialog.findViewById(R.id.ap_edt_datefinal);
                final EditText timeinicial = (EditText) dialog.findViewById(R.id.ap_edt_timeinicial);
                final EditText timefinal = (EditText) dialog.findViewById(R.id.ap_edt_timefinal);
                final EditText contrasenia = (EditText) dialog.findViewById(R.id.ap_edt_pass_turno);
                final CheckBox visible =(CheckBox) dialog.findViewById(R.id.ap_cb_visible);

                Button btCrear = (Button) dialog.findViewById(R.id.d_agregar_turno);
                Button btCancelar = (Button) dialog.findViewById(R.id.d_cancelar_turno);
                ImageView imv_dateinicial = (ImageView) dialog.findViewById(R.id.ap_imv_dateinicial);
                ImageView imv_timeinicial = (ImageView) dialog.findViewById(R.id.ap_imv_timeinicial);
                ImageView imv_datefinal = (ImageView) dialog.findViewById(R.id.ap_imv_datefinal);
                ImageView imv_timefinal = (ImageView) dialog.findViewById(R.id.ap_imv_timefinal);

                //fecha y hora actual

                Date date = new Date();
                DateFormat formatoHora = new SimpleDateFormat("HH:mm");
                DateFormat formatoFecha = new SimpleDateFormat("yyyy/MM/dd");

                dateinicial.setText(""+formatoFecha.format(date));
                datefinal.setText(""+formatoFecha.format(date));
                timeinicial.setText(""+formatoHora.format(date));
                timefinal.setText(""+formatoHora.format(date));

                //programamos metodo crear y cancelar y pickers

                imv_dateinicial.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        datePicker(dateinicial,v.getContext());
                    }
                });

                imv_timeinicial.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timepicker(timeinicial,v.getContext());
                    }
                });

                imv_datefinal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        datePicker(datefinal,v.getContext());
                    }
                });

                imv_timefinal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timepicker(timefinal,v.getContext());
                    }
                });


                //programamos botones de crear-guardar y cancelar
                btCrear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int ver = 0;
                        if (visible.isChecked()) {
                            ver = 1;
                        }

                        if (!dateinicial.getText().toString().isEmpty() && !datefinal.getText().toString().isEmpty()
                                && !timeinicial.getText().toString().isEmpty() && !timefinal.getText().toString().isEmpty()
                                && !contrasenia.getText().toString().isEmpty()) {
                            if(validarFecha(dateinicial.getText().toString(),
                                    timeinicial.getText().toString(),
                                    datefinal.getText().toString(),
                                    timefinal.getText().toString())){
                                try {
                                    turno = new Turno(
                                            id_evaluacion,
                                            dateinicial.getText().toString() + " " + timeinicial.getText().toString(),
                                            datefinal.getText().toString() + " " + timefinal.getText().toString(),
                                            ver,
                                            contrasenia.getText().toString()
                                    );
                                    //creamos registro
                                    daoTurno.Insertar(turno);
                                    //refrescamos la lista
                                    turnos = daoTurno.verTodos(id_evaluacion);
                                    //como ya estamos dentro de la clase adaptador simplemente ejecutamos el metodo
                                    adapterTurno.notifyDataSetChanged();
                                    //cerramos el dialogo
                                    dialog.dismiss();


                                } catch (Exception e) {
                                    Toast.makeText(getApplication(), "Error", Toast.LENGTH_SHORT).show();
                                }

                            }else {
                                Toast.makeText(
                                        v.getContext(),
                                        getResources().getText(R.string.ap_fecha_no_valida),
                                        Toast.LENGTH_SHORT).show();
                            }


                        } else {
                            Toast.makeText(
                                    v.getContext(),
                                    getResources().getText(R.string.ap_llena_los_campos),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                btCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });
        //Final de listener de agregar

        //Inicio de listener de buscar
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edt_buscar.getText().toString().isEmpty()){
                    turnos = daoTurno.verUno(Integer.parseInt(edt_buscar.getText().toString()),id_evaluacion);
                    adapterTurno.notifyDataSetChanged();
                }else{
                    Toast.makeText(
                            v.getContext(),
                            getResources().getText(R.string.ap_llena_los_campos),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
        //Final de listener de buscar

        //Inicio de listener all
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnos = daoTurno.verTodos(id_evaluacion);
                adapterTurno.notifyDataSetChanged();
            }
        });
        //Final de listener all
    }

    public void datePicker(final EditText editText, Context context){
        final Calendar c = Calendar.getInstance();
        dia = c.get(Calendar.DAY_OF_MONTH);
        mes = c.get(Calendar.MONTH);
        anio = c.get(Calendar.YEAR);

        //creamos instancia de clase DatePickerDialog
        DatePickerDialog datePickerDialog =new DatePickerDialog(
                context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        editText.setText(year+"/"+(month+1)+"/"+dayOfMonth);
                    }
                },
                anio,
                mes,
                dia
        );
        datePickerDialog.show();
    }

    public void timepicker(final EditText editText,Context context){
        final Calendar c = Calendar.getInstance();
        hora=c.get(Calendar.HOUR_OF_DAY);
        minuto=c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        editText.setText(hourOfDay+":"+minute);
                    }
                },
                hora,
                minuto,
                false
        );
        timePickerDialog.show();

    }

    public Boolean validarFecha(String di, String ti, String df, String tf){
        Boolean validado = false;
        int anioi,mesi,diai,horai,minutoi;
        int aniof,mesf,diaf,horaf,minutof;

        anioi = Integer.parseInt(di.split("/")[0]);
        mesi = Integer.parseInt(di.split("/")[1]);
        diai = Integer.parseInt(di.split("/")[2]);

        aniof = Integer.parseInt(df.split("/")[0]);
        mesf = Integer.parseInt(df.split("/")[1]);
        diaf = Integer.parseInt(df.split("/")[2]);

        horai = Integer.parseInt(ti.split(":")[0]);
        minutoi =  Integer.parseInt(ti.split(":")[1]);

        horaf = Integer.parseInt(tf.split(":")[0]);
        minutof =  Integer.parseInt(tf.split(":")[1]);

        if (aniof>anioi){
            validado=true;
        }
        if (aniof==anioi){
            if (mesf>mesi){
                validado = true;
            }
            if (mesf==mesi){
                if(diaf>diai){
                    validado=true;
                }
                if(diaf==diai){
                    if(horaf>horai){
                        validado = true;
                    }
                    if(horaf==horai){
                        if(minutof>=minutoi){
                            validado = true;
                        }
                    }
                }
            }
        }

      return  validado;
    }
}
