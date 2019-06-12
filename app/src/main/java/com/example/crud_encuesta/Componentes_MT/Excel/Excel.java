package com.example.crud_encuesta.Componentes_MT.Excel;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.crud_encuesta.Componentes_DC.Objetos.Opcion;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Excel {
    Context context;

    public  Excel(Context context){
        this.context = context;
    }

    DAOExcel daoExcel = new DAOExcel(context);

    public boolean crearExcel(Context context, String fileName) {
        fileName=fileName+".xls";

        // Comprobar si el almacenamiento está disponible y no solo de lectura
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.w("FileUtils", "Storage not available or read only");
            return false;
        }

        boolean success = false;

        //Nuevo documento
        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        //Estilo de celda para fila de encabezado
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.LIME.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        //Hoja Preguntas
        Sheet preguntas = null;
        preguntas = wb.createSheet("Preguntas");

        // Generar columnas para la hoja Pregunta
        Row rowPregunta = preguntas.createRow(0);

        c = rowPregunta.createCell(0);
        c.setCellValue("Código Pregunta");
        c.setCellStyle(cs);

        c = rowPregunta.createCell(1);
        c.setCellValue("Pregunta");
        c.setCellStyle(cs);

        preguntas.setColumnWidth(0, (15 * 300));
        preguntas.setColumnWidth(1, (15 * 400));

        //Hoja Opciones
        Sheet opciones = null;
        opciones = wb.createSheet("Opciones");

        // Generar columnas para la hoja Opciones
        Row rowOpcion = opciones.createRow(0);

        c = rowOpcion.createCell(0);
        c.setCellValue("Código Pregunta");
        c.setCellStyle(cs);

        c = rowOpcion.createCell(1);
        c.setCellValue("Opción");
        c.setCellStyle(cs);

        c = rowOpcion.createCell(2);
        c.setCellValue("Es correcta");
        c.setCellStyle(cs);

        opciones.setColumnWidth(0, (15 * 300));
        opciones.setColumnWidth(1, (15 * 300));
        opciones.setColumnWidth(2, (15 * 200));

        // Ruta de almacenamiento del archivo
        File dir = new File("/storage/emulated/0/Download");
        //File file = new File(context.getExternalFilesDir(null), fileName);
        File file = new File(dir, fileName);
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            Toast.makeText(context, "Archivo alamacenado en "+file, Toast.LENGTH_SHORT).show();
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }

        return success;
    }

    public boolean crearExcelEM(Context context, String fileName) {
        fileName=fileName+".xls";

        // Comprobar si el almacenamiento está disponible y no solo de lectura
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.w("FileUtils", "Storage not available or read only");
            return false;
        }

        boolean success = false;

        //Nuevo documento
        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        //Estilo de celda para fila de encabezado
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.LIME.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        //Hoja Preguntas
        Sheet grupo_emparejamiento = null;
        grupo_emparejamiento = wb.createSheet("Grupo Emparejamiento");

        // Generar columnas para la hoja Grupo Emparejamiento
        Row rowPregunta = grupo_emparejamiento.createRow(0);

        c = rowPregunta.createCell(0);
        c.setCellValue("Código Grupo Emparejamiento");
        c.setCellStyle(cs);

        c = rowPregunta.createCell(1);
        c.setCellValue("Descripcion");
        c.setCellStyle(cs);

        grupo_emparejamiento.setColumnWidth(0, (15 * 500));
        grupo_emparejamiento.setColumnWidth(1, (15 * 500));

        //Hoja Preguntas
        Sheet preguntas = null;
        preguntas = wb.createSheet("Preguntas");

        // Generar columnas para la hoja Preguntas
        Row rowEmparejamiento = preguntas.createRow(0);

        c = rowEmparejamiento.createCell(0);
        c.setCellValue("Código Grupo Emparejamiento");
        c.setCellStyle(cs);

        c = rowEmparejamiento.createCell(1);
        c.setCellValue("Código Pregunta");
        c.setCellStyle(cs);

        c = rowEmparejamiento.createCell(2);
        c.setCellValue("Pregunta");
        c.setCellStyle(cs);

        preguntas.setColumnWidth(0, (15 * 500));
        preguntas.setColumnWidth(1, (15 * 300));
        preguntas.setColumnWidth(2, (15 * 400));

        //Hoja Opciones
        Sheet opciones = null;
        opciones = wb.createSheet("Opciones");

        // Generar columnas para la hoja opciones
        Row rowOpcion = opciones.createRow(0);

        c = rowOpcion.createCell(0);
        c.setCellValue("Código Pregunta");
        c.setCellStyle(cs);

        c = rowOpcion.createCell(1);
        c.setCellValue("Opción");
        c.setCellStyle(cs);

        c = rowOpcion.createCell(2);
        c.setCellValue("Es correcta");
        c.setCellStyle(cs);

        opciones.setColumnWidth(0, (15 * 300));
        opciones.setColumnWidth(1, (15 * 300));
        opciones.setColumnWidth(2, (15 * 200));

        // Ruta de almacenamiento del archivo
        File dir = new File("/storage/emulated/0/Download");
        //File file = new File(context.getExternalFilesDir(null), fileName);
        File file = new File(dir, fileName);
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            Toast.makeText(context, "Archivo alamacenado en "+file, Toast.LENGTH_SHORT).show();
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }

        return success;
    }

    public int leerExcel(String path, int id_area) {
        List<PreguntaExcel> preguntas = new ArrayList<>();
        List<OpcionExcel> opciones = new ArrayList<>();
        int cantidad=0;
        int i = 0;

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly())
        {
            Log.w("FileUtils", "Storage not available or read only");
            return 0;
        }

        try{
            // Creating Input Stream
            FileInputStream myInput = new FileInputStream(path);

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet hoja_pregunta = myWorkBook.getSheetAt(0);
            HSSFSheet hoja_opcion = myWorkBook.getSheetAt(1);

            /** We now need something to iterate through the cells.**/
            Iterator<Row> rowPregunta = hoja_pregunta.rowIterator();
            Iterator<Row> rowOpcion = hoja_opcion.rowIterator();

            while(rowPregunta.hasNext()){
                int columna=0;
                PreguntaExcel pregunta = new PreguntaExcel();
                HSSFRow myRow = (HSSFRow) rowPregunta.next();
                Iterator<Cell> cellIter = myRow.cellIterator();

                if(i>0){
                    while(cellIter.hasNext()){
                        HSSFCell myCell = (HSSFCell) cellIter.next();

                        if(columna==0)
                            pregunta.setCodigo(myCell.toString());
                        if(columna==1)
                            pregunta.setPregunta(myCell.toString());

                        columna++;
                    }
                    preguntas.add(pregunta);
                }
                i++;
            }

            i=0;
            while(rowOpcion.hasNext()){
                int columna =0;
                OpcionExcel opcion = new OpcionExcel();
                HSSFRow myRow = (HSSFRow) rowOpcion.next();
                Iterator<Cell> cellIter = myRow.cellIterator();

                if(i>0){
                    while(cellIter.hasNext()){
                        HSSFCell myCell = (HSSFCell) cellIter.next();
                        if(columna==0)
                            opcion.setCodPregunta(myCell.toString());
                        if(columna==1)
                            opcion.setOpcion(myCell.toString());
                        if(columna==2)
                            opcion.setEsCorrecta(myCell.toString());
                        columna++;
                    }
                    opciones.add(opcion);
                }
                i++;
            }

        }catch (Exception e){e.printStackTrace(); }

        if(preguntas.size()>0 && opciones.size()>0 && preguntas!=null && opciones!=null){
            try {
                i=0;
                while(i<preguntas.size()){
                    int id_emp = daoExcel.insertGPOEMP(id_area, "");
                    int id_pregunta = daoExcel.insertPregunta(id_emp, preguntas.get(i).getPregunta());

                    int j=0;
                    while (j<opciones.size()) {
                        if(preguntas.get(i).getCodigo().equals(opciones.get(j).getCodPregunta())){
                            Opcion opcion = new Opcion();
                            opcion.setId_pregunta(id_pregunta);
                            opcion.setOpcion(opciones.get(j).getOpcion());
                            if(opciones.get(j).getEsCorrecta().equals("1.0")) opcion.setCorrecta(1);
                            if(opciones.get(j).getEsCorrecta().equals("0.0")) opcion.setCorrecta(0);

                            cantidad += daoExcel.insertOpcion(opcion);
                        }
                        j++;
                    }
                    i++;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return cantidad;
    }

    public int leerExcelEM(String path, int id_area) {
        List<EmparejamientoExcel> emparejamientos = new ArrayList<>();
        List<PreguntaExcel> preguntas = new ArrayList<>();
        List<OpcionExcel> opciones = new ArrayList<>();
        int cantidad=0;
        int i = 0;

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly())
        {
            Log.w("FileUtils", "Storage not available or read only");
            return 0;
        }

        try{
            // Creating Input Stream
            FileInputStream myInput = new FileInputStream(path);

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet hoja_emparejamiento = myWorkBook.getSheetAt(0);
            HSSFSheet hoja_pregunta = myWorkBook.getSheetAt(1);
            HSSFSheet hoja_opcion = myWorkBook.getSheetAt(2);

            /** We now need something to iterate through the cells.**/
            Iterator<Row> rowEmparejamiento = hoja_emparejamiento.rowIterator();
            Iterator<Row> rowPregunta = hoja_pregunta.rowIterator();
            Iterator<Row> rowOpcion = hoja_opcion.rowIterator();

            while(rowEmparejamiento.hasNext()){
                int columna=0;
                EmparejamientoExcel emparejamiento = new EmparejamientoExcel();
                HSSFRow myRow = (HSSFRow) rowEmparejamiento.next();
                Iterator<Cell> cellIter = myRow.cellIterator();

                if(i>0){
                    while(cellIter.hasNext()){
                        HSSFCell myCell = (HSSFCell) cellIter.next();

                        if(columna==0)
                            emparejamiento.setCodigoEm(myCell.toString());
                        if(columna==1)
                            emparejamiento.setDescripcion(myCell.toString());

                        columna++;
                    }
                    emparejamientos.add(emparejamiento);
                }
                i++;
            }

            i=0;
            while(rowPregunta.hasNext()){
                int columna =0;
                PreguntaExcel pregunta = new PreguntaExcel();
                HSSFRow myRow = (HSSFRow) rowPregunta.next();
                Iterator<Cell> cellIter = myRow.cellIterator();

                if(i>0){
                    while(cellIter.hasNext()){
                        HSSFCell myCell = (HSSFCell) cellIter.next();
                        if(columna==0)
                            pregunta.setCodigoEm(myCell.toString());
                        if(columna==1)
                            pregunta.setCodigo(myCell.toString());
                        if(columna==2)
                            pregunta.setPregunta(myCell.toString());
                        columna++;
                    }
                    preguntas.add(pregunta);
                }
                i++;
            }

            i=0;
            while(rowOpcion.hasNext()){
                int columna =0;
                OpcionExcel opcion = new OpcionExcel();
                HSSFRow myRow = (HSSFRow) rowOpcion.next();
                Iterator<Cell> cellIter = myRow.cellIterator();

                if(i>0){
                    while(cellIter.hasNext()){
                        HSSFCell myCell = (HSSFCell) cellIter.next();
                        if(columna==0)
                            opcion.setCodPregunta(myCell.toString());
                        if(columna==1)
                            opcion.setOpcion(myCell.toString());
                        if(columna==2)
                            opcion.setEsCorrecta(myCell.toString());
                        columna++;
                    }
                    opciones.add(opcion);
                }
                i++;
            }

        }catch (Exception e){e.printStackTrace(); }

        if(emparejamientos.size()>0 && preguntas.size()>0 && opciones.size()>0 && emparejamientos!=null && preguntas!=null && opciones!=null){
            try {
                i=0;
                while(i<emparejamientos.size()){
                    int id_emp = daoExcel.insertGPOEMP(id_area, emparejamientos.get(i).getDescripcion());

                    int j=0;
                    while(j<preguntas.size()){
                        if(emparejamientos.get(i).getCodigoEm().equals(preguntas.get(j).getCodigoEm())){
                            int id_pregunta = daoExcel.insertPregunta(id_emp, preguntas.get(j).getPregunta());

                            int k=0;
                            while (k<opciones.size()) {
                                if(preguntas.get(j).getCodigo().equals(opciones.get(k).getCodPregunta())){
                                    System.out.println(opciones.get(k).getOpcion());
                                    Opcion opcion = new Opcion();
                                    opcion.setId_pregunta(id_pregunta);
                                    opcion.setOpcion(opciones.get(k).getOpcion());
                                    if(opciones.get(k).getEsCorrecta().equals("1.0")) opcion.setCorrecta(1);
                                    if(opciones.get(k).getEsCorrecta().equals("0.0")) opcion.setCorrecta(0);

                                    cantidad += daoExcel.insertOpcion(opcion);
                                }

                                k++;
                            }
                        }

                        j++;
                    }

                    i++;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return cantidad;
    }


    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

}
