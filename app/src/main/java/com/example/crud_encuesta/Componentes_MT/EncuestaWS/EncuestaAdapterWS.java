package com.example.crud_encuesta.Componentes_MT.EncuestaWS;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crud_encuesta.Componentes_DC.WebServices.Descargar;
import com.example.crud_encuesta.R;

import java.util.ArrayList;
import java.util.List;

public class EncuestaAdapterWS extends BaseAdapter implements AdapterView.OnItemSelectedListener{
    private LayoutInflater inflater = null;
    private Context context;
    private Descargar descargar_ws;

    private int pos_area;
    private List<EncuestaWS> encuestasWS= new ArrayList<>();

    public EncuestaAdapterWS(Context context, List<EncuestaWS> encuestasWS){
        this.context = context;
        this.encuestasWS = encuestasWS;

        inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View mView = inflater.inflate(R.layout.item_list_encuestas_ws, null);

        TextView txt_titulo = mView.findViewById(R.id.txt_titulo_ws);
        ImageView informacion = mView.findViewById(R.id.img_info_ws);
        final ImageView descargar = mView.findViewById(R.id.img_download_ws);

        txt_titulo.setText(encuestasWS.get(i).getTitulo_encuesta());

        informacion.setTag(i);
        descargar.setTag(i);

        informacion.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                final int pocision = Integer.parseInt(view.getTag().toString());
                info(pocision);
            }
        });

        descargar.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                final int id_encuesta_seleccion = encuestasWS.get(Integer.parseInt(view.getTag().toString())).id;
                descargar_ws = new Descargar(context);
                descargar_ws.descargar_encuesta(id_encuesta_seleccion);
                //Toast.makeText(context, "encuesta_id: "+id_encuesta_seleccion+", Este método está vacio", Toast.LENGTH_SHORT).show();

            }
        });

        return mView;
    }

    @Override
    public int getCount() {
        return encuestasWS.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void info(int posicion){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);

        mBuilder.setMessage("Descripción: "+encuestasWS.get(posicion).getDescriion_encuesta()+
                "\n\nDisponibilidad:" +
                "\nDesde "+encuestasWS.get(posicion).getFecha_inicio_encuesta()+
                "\nHasta "+encuestasWS.get(posicion).getFecha_final_encuesta());
        mBuilder.setIcon(R.drawable.infoazul);
        mBuilder.setTitle("Detalle de encuesta");

        mBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }
}
