package com.example.crud_encuesta.Estadisticas;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crud_encuesta.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class EstadisticaActivity extends AppCompatActivity {
    BarChart grafico;
    int idMat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadistica);
        TextView tx=findViewById(R.id.txEstadistica);

        grafico = findViewById(R.id.barras);
        idMat=getIntent().getExtras().getInt("id_materia");

        //Configuraciones previas
        grafico.setPinchZoom(false);//No permitir zoom con gesto
        grafico.setDoubleTapToZoomEnabled(false);//No permitir zoom con doble touch
        grafico.setDrawGridBackground(false);//No mostrara grid de cada eje
        grafico.setDragEnabled(true);
        grafico.setScaleEnabled(false);
        grafico.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Toast.makeText(EstadisticaActivity.this,e.getData()+"",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        String[] nomEvaluaciones = Consultas.parcialesMateria(idMat,this);
        if (nomEvaluaciones!=null){
            float[][] cantidad = new float[2][nomEvaluaciones.length];

            //Llenando todos los aprobados de cada parcial
            for (int c = 0; c < cantidad[0].length; c++) {
                cantidad[0][c] = Consultas.aprobadosEvaluacion(nomEvaluaciones[c],this);
            }

            //Llenando todos los reprobados de cada parcial
            for (int c = 0; c < cantidad[0].length; c++) {
                cantidad[1][c] = Consultas.reprobadosEvaluacion(nomEvaluaciones[c],this);
            }

            ArrayList<IBarDataSet> barDataSets = barDataSets(cantidad);
            //Creando objeto BarData y asignandolo al grafico
            BarData barData = new BarData(barDataSets);
            barData.setBarWidth(0.8f);
            grafico.setData(barData);

            //Leyenda
            Legend legend = grafico.getLegend();

            //Agregando leyenda personalizada
            ArrayList<LegendEntry> leyendas = new ArrayList<>();
            LegendEntry l1 = new LegendEntry("Aprobado", Legend.LegendForm.CIRCLE, 10f, 0.0f, null, ColorTemplate.COLORFUL_COLORS[0]);
            LegendEntry l2 = new LegendEntry("Reprobado", Legend.LegendForm.CIRCLE, 10f, 0.0f, null, ColorTemplate.COLORFUL_COLORS[1]);
            leyendas.add(l1);
            leyendas.add(l2);

            //Setiando la leyenda si se comenta queda la leyenda del dataset
            legend.setCustom(leyendas);
            legend.setXOffset(70);
            legend.setYOffset(10);


            //Quitando la descripcion
            Description d = grafico.getDescription();
            d.setText("");


            //Configuraciones del Eje X
            XAxis xAxis = grafico.getXAxis();
            xAxis.setValueFormatter(new XAxisValueFormatter(nomEvaluaciones));
            xAxis.setPosition(XAxis.XAxisPosition.TOP);
            xAxis.setLabelCount(nomEvaluaciones.length);
            xAxis.setCenterAxisLabels(true);
            xAxis.setGranularity(1);
            xAxis.setGranularityEnabled(true);
            xAxis.setAxisMinimum(0);
            xAxis.setAxisMaximum(nomEvaluaciones.length*2);


            //Ocualtando YAxisRigth
            YAxis yAxis = grafico.getAxisRight();
            yAxis.setEnabled(false);
            yAxis = grafico.getAxisRight();
            yAxis.setAxisMaximum(10);
            yAxis.setAxisMinimum(0);


            //Para actualizacion
            grafico.setFitBars(true);//Centra las barras
            grafico.animateXY(2000, 2000);
            grafico.invalidate();


        }else {
            grafico.setVisibility(View.GONE);
            tx.setText(R.string.estadistica);
        }


    }


    private ArrayList<IBarDataSet> barDataSets(float[][] valores) {
        ArrayList<BarEntry> barEntries;
        ArrayList<IBarDataSet> barDataSets = new ArrayList<>();
        /*Se recorre la matriz de valores segun parciales. La matriz luce asi
                P1  P2  P3
         Aprob  x   x   x
         Reprob x   x   x

         */
        float conta = 0.5f;
        for (int c = 0; c < valores[0].length; c++) {
            barEntries = new ArrayList<>();
            BarEntry bar;
            for (int f = 0; f < valores.length; f++) {
                //Genera Barras de Aprobados y Reprobados
                if (f%2==0){
                    bar = new BarEntry(conta, valores[f][c],"Aprobados: "+valores[f][c]);
                }else{
                    bar = new BarEntry(conta, valores[f][c],"Reprobados: "+valores[f][c]);
                }

                barEntries.add(bar);
                conta++;
            }
            BarDataSet barDataSet = new BarDataSet(barEntries, c + "");
            barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            barDataSets.add(barDataSet);

        }
        return barDataSets;
    }
}
