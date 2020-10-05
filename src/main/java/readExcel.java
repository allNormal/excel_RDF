import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import excel.Workbook.Workbook;
import excel.Worksheet.Worksheet;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.poifs.macros.Module;
import org.apache.poi.poifs.macros.VBAMacroReader;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;


public class readExcel {

    private List<String> saveTitle = new ArrayList<>();
    private XSSFWorkbook myWorkBook;
    private XSSFSheet mySheet;
    private int titleIndex = 1;

    public readExcel(String filepath) {
        try {
            readExcel(filepath);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());;
        }
    }

    private void readExcel(String filePath) throws IOException {

        File myFile = new File(filePath);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(myFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Finds the workbook instance for XLSX file
        myWorkBook = new XSSFWorkbook (fis);
        //add to ontology
        Workbook workbook = new Workbook(filePath.substring(filePath.length()-4,filePath.length()),filePath);

        for(Sheet sheet : myWorkBook){
            //add to ontology
            Worksheet worksheet = new Worksheet(sheet.getSheetName(), workbook);
            readTable(myWorkBook, sheet.getSheetName());
            readChart(myWorkBook, sheet.getSheetName());
            readIllustration(myWorkBook, sheet.getSheetName());
            readText(myWorkBook, sheet.getSheetName());
            readBasicElement(sheet);
        }
        readMacro(myFile);

    }

    private void readMacro(File file) {
        try {
            VBAMacroReader macroReader = new VBAMacroReader(file);
            if(macroReader != null) {
                Map<String, String> macro = macroReader.readMacros();
                macroReader.close();
                for(Map.Entry<String, String> m : macro.entrySet()){
                    System.out.println(m.getValue());
                }
            }
        } catch (IOException err) {
            System.out.println(err);
        }

    }

    private void readText(XSSFWorkbook workbook, String sheetName) {
        int count = 0;
        try {
            XSSFSheet sheet = workbook.getSheet(sheetName);
            if(sheet == null) throw new FileNotFoundException("there is not sheet with name " + sheetName);
            XSSFDrawing shape = sheet.getDrawingPatriarch();

            if(shape != null) {
                //add to ontology
                List<XSSFShape> shapes = shape.getShapes();

                for(int i = 0;i<shapes.size();i++){
                    if(shapes.get(i) instanceof  XSSFSimpleShape){
                        count++;
                    }
                }
                System.out.println("there are " + count + " of texts");
            }
        } catch (FileNotFoundException err) {
            System.out.println(err);
        }
    }

    private void readIllustration(XSSFWorkbook workbook, String sheetName){
        int count = 0;
        try {
            XSSFSheet sheet = workbook.getSheet(sheetName);
            if(sheet == null) throw new FileNotFoundException("there is not sheet with name " + sheetName);
            XSSFDrawing chart = sheet.getDrawingPatriarch();

            if(chart != null) {
                //add to ontology
                List<XSSFShape> charts = chart.getShapes();
                for(int i = 0;i<charts.size();i++){
                    if(charts.get(i) instanceof Picture){
                        count++;
                    }
                }
            }
        } catch (FileNotFoundException err) {
            System.out.println(err);
        }
        System.out.println("there are " + count + " of pictures");
    }

    private void readChart(XSSFWorkbook workbook, String sheetName){
        try {
            XSSFSheet sheet = workbook.getSheet(sheetName);
            if(sheet == null) throw new FileNotFoundException("there is not sheet with name " + sheetName);
            XSSFDrawing chart = sheet.getDrawingPatriarch();

            if(chart != null) {
                //add to ontology
                List<XSSFChart> charts = chart.getCharts();
                System.out.println("there are " + charts.size() + " of charts");
            }
        } catch (FileNotFoundException err) {
            System.out.println(err);
        }
    }

    private void readBasicElement(Sheet sheet) {
        for(Row row : sheet){
            //add to ontology
            excel.SheetElement.BasicElement.Row row1 = new excel.SheetElement.BasicElement.Row(
                    String.valueOf(row.getRowNum()));
            for(Cell cell : row) {
                //add to ontology
                switch (cell.getCellType()){
                    case STRING: break;
                    case _NONE: break;
                    case BLANK: break;
                    case ERROR: break;
                    case BOOLEAN: break;
                    case FORMULA: break;
                    case NUMERIC: break;
                }
                //add to ontology
                XSSFComment comment = (XSSFComment)cell.getCellComment();
            }
        }
    }

    private void readTable(XSSFWorkbook workbook, String sheetName) {
        try{
            XSSFSheet sheet = workbook.getSheet(sheetName);
            if(sheet == null) throw new FileNotFoundException("there is not sheet with name " + sheetName);
            List<XSSFTable> tables = sheet.getTables();
            List<XSSFPivotTable> pivotTables = sheet.getPivotTables();
            System.out.println("there are " + tables.size() + " of tables");
            while(tables.size() != 0 && pivotTables.size()!= 0){

                tables.get(0).getEndColIndex();
                tables.get(0).getEndRowIndex();
                tables.get(0).getStartColIndex();
                tables.get(0).getStartRowIndex();
                //add tables to ontology
            }


        } catch (FileNotFoundException err) {
            System.out.println(err);
        }

    }
}
