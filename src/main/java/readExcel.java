import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import excel.SheetElement.BasicElement.Column;
import excel.SheetElement.Charts.Chart;
import excel.SheetElement.Illustrations.Illustrations;
import excel.SheetElement.SheetElement;
import excel.SheetElement.Tables.Table;
import excel.SheetElement.Texts.Text;
import excel.ValueType.Value;
import excel.Workbook.Workbook;
import excel.Worksheet.Worksheet;
import org.apache.jena.ontology.Individual;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.poifs.macros.Module;
import org.apache.poi.poifs.macros.VBAMacroReader;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;


public class readExcel {

    private XSSFWorkbook myWorkBook;
    private Workbook workbook;

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
        this.workbook = new Workbook(filePath.substring(filePath.length()-5,filePath.length()),filePath);


        for(Sheet sheet : myWorkBook){
            Worksheet worksheet = new Worksheet(sheet.getSheetName(), workbook);
            readTable(myWorkBook, sheet.getSheetName(), worksheet);
            readChart(myWorkBook, sheet.getSheetName(), worksheet);
            readIllustration(myWorkBook, sheet.getSheetName(), worksheet);
            readText(myWorkBook, sheet.getSheetName(), worksheet);
            readBasicElement(sheet, worksheet);
            this.workbook.addWorksheet(worksheet);
        }
        if(this.workbook.getExtension() == ".xlsm") {
            readMacro(myFile);
        }

    }

    private void readMacro(File file) {
        try {
            VBAMacroReader macroReader = new VBAMacroReader(file);
            if(macroReader != null) {
                Map<String, String> macro = macroReader.readMacros();
                macroReader.close();
                this.workbook.setMacro(macro);
            }
        } catch (IOException err) {
            System.out.println(err);
        }

    }

    private void readText(XSSFWorkbook workbook, String sheetName, Worksheet worksheet) {
        List<SheetElement> texts = new ArrayList<>();
        try {
            XSSFSheet sheet = workbook.getSheet(sheetName);
            if(sheet == null) throw new FileNotFoundException("there is not sheet with name " + sheetName);
            XSSFDrawing shape = sheet.getDrawingPatriarch();

            if(shape != null) {
                //add to ontology
                List<XSSFShape> shapes = shape.getShapes();

                for(int i = 0;i<shapes.size();i++){
                    if(shapes.get(i) instanceof  XSSFSimpleShape){
                        XSSFSimpleShape temp = (XSSFSimpleShape)shapes.get(i);
                        Text text = new Text(worksheet, temp.getShapeName(), temp.getText());
                        texts.add(text);
                    }
                }
                worksheet.addElement("Texts", texts);
            }
        } catch (FileNotFoundException err) {
            System.out.println(err);
        }
    }

    private void readIllustration(XSSFWorkbook workbook, String sheetName, Worksheet worksheet){
        List<SheetElement> sheetElements = new ArrayList<>();
        try {
            XSSFSheet sheet = workbook.getSheet(sheetName);
            if(sheet == null) throw new FileNotFoundException("there is not sheet with name " + sheetName);
            XSSFDrawing chart = sheet.getDrawingPatriarch();

            if(chart != null) {
                //add to ontology
                List<XSSFShape> charts = chart.getShapes();
                for(int i = 0;i<charts.size();i++){
                    if(charts.get(i) instanceof Picture){
                        Picture picture = (Picture)charts.get(i);
                        Illustrations illustrations = new Illustrations(worksheet);
                        sheetElements.add(illustrations);
                    }
                }
                worksheet.addElement("Illustrations", sheetElements);
            }
        } catch (FileNotFoundException err) {
            System.out.println(err);
        }
    }

    private void readChart(XSSFWorkbook workbook, String sheetName, Worksheet worksheet){
        List<SheetElement> sheetElements = new ArrayList<>();
        try {
            XSSFSheet sheet = workbook.getSheet(sheetName);
            if(sheet == null) throw new FileNotFoundException("there is not sheet with name " + sheetName);
            XSSFDrawing chart = sheet.getDrawingPatriarch();

            if(chart != null) {
                //add to ontology
                List<XSSFChart> charts = chart.getCharts();

                for(int i = 0; i<charts.size(); i++){
                    Chart chart1 = new Chart(worksheet, charts.get(i).getOrAddLegend().getEntries(),
                            charts.get(i).getTitleText());
                    sheetElements.add(chart1);
                }
                worksheet.addElement("Charts", sheetElements);
            }
        } catch (FileNotFoundException err) {
            System.out.println(err);
        }
    }

    private void readBasicElement(Sheet sheet, Worksheet worksheet) {
        List<SheetElement> cellTemp = new ArrayList<>();
        Map<String, Column> columnTemp = new HashMap<>();
        List<SheetElement> rowTemp = new ArrayList<>();

        for(Row row : sheet){
            excel.SheetElement.BasicElement.Row row1 = new excel.SheetElement.BasicElement.Row(worksheet,
                    Integer.toString(row.getRowNum()));
            for(Cell cell : row) {
                excel.SheetElement.BasicElement.Cell cell1 = new excel.SheetElement.BasicElement.Cell(worksheet,
                        Integer.toString(cell.getColumnIndex()),cell.getRowIndex());

                switch (cell.getCellType()){
                    case STRING:
                        cell1.setValue(Value.STRING);
                        cell1.setStringValue(cell.getRichStringCellValue().getString());
                        break;
                    case BLANK:
                        cell1.setValue(Value.STRING);
                        cell1.setStringValue("");
                        break;
                    case ERROR:
                        cell1.setValue(Value.ERROR);
                        cell1.SetErrorValue(String.valueOf(cell.getErrorCellValue()));
                        break;
                    case BOOLEAN:
                        cell1.setValue(Value.BOOLEAN);
                        cell1.setBooleanValue(cell.getBooleanCellValue());
                        break;
                    case FORMULA:
                        cell1.setValue(Value.FORMULA);
                        cell1.setFormulaValue(cell.getCellFormula());
                        break;
                    case NUMERIC:
                        cell1.setValue(Value.NUMERIC);
                        cell1.setNumericValue((float)cell.getNumericCellValue());
                        break;
                }

                XSSFComment comment = (XSSFComment)cell.getCellComment();
                if(comment!= null) cell1.setComment(comment);

                if(!columnTemp.containsKey(Integer.toString(cell.getColumnIndex()))){
                    Column column = new Column(worksheet, Integer.toString(cell.getColumnIndex()));
                    column.addCell(cell1);
                    columnTemp.put(Integer.toString(cell.getColumnIndex()), column);
                }
                else{
                    Column column = columnTemp.get(Integer.toString(cell.getColumnIndex()));
                    column.addCell(cell1);
                }
                cellTemp.add(cell1);
                row1.addCell(cell1);
            }
            rowTemp.add(row1);
        }

        worksheet.addElement("Row", rowTemp);
        worksheet.addElement("Cell", cellTemp);
        List<SheetElement> colTemp = new ArrayList<>();
        for(Map.Entry<String, Column> temp : columnTemp.entrySet()) {
            colTemp.add(temp.getValue());
        }
        worksheet.addElement("Column", colTemp);
    }

    private void readTable(XSSFWorkbook workbook, String sheetName, Worksheet worksheet) {
        List<SheetElement> sheetElements = new ArrayList<>();
        try{
            XSSFSheet sheet = workbook.getSheet(sheetName);
            if(sheet == null) throw new FileNotFoundException("there is not sheet with name " + sheetName);
            List<XSSFTable> tables = sheet.getTables();
            List<XSSFPivotTable> pivotTables = sheet.getPivotTables();

            for(int i = 0; i<tables.size(); i++) {
                XSSFTable t = tables.get(i);
                Table table = new Table(worksheet, Integer.toString(t.getEndColIndex()), Integer.toString(t.getStartColIndex()),
                        Integer.toString(t.getStartRowIndex()), Integer.toString(t.getEndRowIndex()), t.getName());
                sheetElements.add(table);
            }
            worksheet.addElement("Tables", sheetElements);

        } catch (FileNotFoundException err) {
            System.out.println(err);
        }

    }

    public Workbook getWorkbook() {
        return workbook;
    }
}
