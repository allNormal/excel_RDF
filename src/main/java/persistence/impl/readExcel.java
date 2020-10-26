package persistence.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import entity.SheetElement.BasicElement.Column;
import entity.SheetElement.Charts.Chart;
import entity.SheetElement.Illustrations.Illustrations;
import entity.SheetElement.SheetElement;
import entity.SheetElement.Tables.Table;
import entity.SheetElement.Texts.Text;
import entity.ValueType.Value;
import entity.Workbook.Macro;
import entity.Workbook.Workbook;
import entity.Worksheet.Worksheet;
import org.apache.logging.log4j.core.util.FileUtils;
import org.apache.poi.poifs.macros.VBAMacroReader;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import static entity.SheetElement.ElementType.*;


public class readExcel {

    private XSSFWorkbook myWorkBook;
    private Workbook workbook;


    public readExcel(File file) throws IOException {
        readExcelConverter(file);
    }

    /**
     * convert entity.excel into Workbook Object.
     * @param file entity.excel file
     * @throws IOException if file not found.
     */
    private void readExcelConverter(File file) throws IOException {

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Finds the workbook instance for XLSX file
        myWorkBook = new XSSFWorkbook (fis);
        this.workbook = new Workbook((FileUtils.getFileExtension(file)),file.getName());

        //iterate through sheet in workbook
        for(Sheet sheet : myWorkBook){
            Worksheet worksheet = new Worksheet(sheet.getSheetName(), workbook);
            readBasicElement(sheet, worksheet);
            readTable(myWorkBook, sheet.getSheetName(), worksheet);
            readChart(myWorkBook, sheet.getSheetName(), worksheet);
            readIllustration(myWorkBook, sheet.getSheetName(), worksheet);
            readText(myWorkBook, sheet.getSheetName(), worksheet);
            this.workbook.addWorksheet(worksheet);
        }
        //check if extension = macro or not
        if(this.workbook.getExtension().equals("xlsm")) {
            readMacro(file);
        }

    }

    /**
     * read Macro and save it in workbook
     * @param file entity.excel file with macro extension
     */
    private void readMacro(File file) {
        try {
            VBAMacroReader macroReader = new VBAMacroReader(file);
            if(macroReader != null) {
                Macro m = new Macro(macroReader.toString());
                macroReader.close();
                this.workbook.setMacro(m);
            }
        } catch (IOException err) {
            System.out.println(err);
        }

    }

    /**
     * read text sheet element from entity.excel, convert it into text object and add it into worksheet object
     * @param workbook workbook target(poi)
     * @param sheetName worksheet target (poi)
     * @param worksheet add text element into worksheet object(self-made)
     */
    private void readText(XSSFWorkbook workbook, String sheetName, Worksheet worksheet) {
        List<SheetElement> texts = new ArrayList<>();
        try {
            XSSFSheet sheet = workbook.getSheet(sheetName);
            if(sheet == null) throw new FileNotFoundException("there is not sheet with name " + sheetName);
            XSSFDrawing shape = sheet.getDrawingPatriarch();

            if(shape != null) {
                List<XSSFShape> shapes = shape.getShapes();

                for(int i = 0;i<shapes.size();i++){
                    if(shapes.get(i) instanceof  XSSFSimpleShape){
                        XSSFSimpleShape temp = (XSSFSimpleShape)shapes.get(i);
                        Text text = new Text(worksheet, "Text", temp.getText());
                        texts.add(text);
                    }
                }
                worksheet.addElement(TEXT, texts);
            }
        } catch (FileNotFoundException err) {
            System.out.println(err);
        }
    }

    /**
     * read illustration sheet element from entity.excel, convert it into illustration object and add it into worksheet object
     * @param workbook workbook target(poi)
     * @param sheetName worksheet target (poi)
     * @param worksheet add illustration element into worksheet object(self-made)
     */
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
                        Illustrations illustrations = new Illustrations(worksheet, "Illustration");
                        sheetElements.add(illustrations);
                    }
                }
                worksheet.addElement(ILLUSTRATION, sheetElements);
            }
        } catch (FileNotFoundException err) {
            System.out.println(err);
        }
    }

    /**
     * read chart sheet element from entity.excel, convert it into chart object and add it into worksheet object
     * @param workbook workbook target(poi)
     * @param sheetName worksheet target (poi)
     * @param worksheet add chart element into worksheet object(self-made)
     */
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
                worksheet.addElement(CHART, sheetElements);
            }
        } catch (FileNotFoundException err) {
            System.out.println(err);
        }
    }

    /**
     * read basic element from worksheet(entity.excel), convert it into basicElement object, and add it into worksheet object
     * @param sheet worksheet(entity.excel + poi)
     * @param worksheet worksheet object (self-created)
     */
    private void readBasicElement(Sheet sheet, Worksheet worksheet) {
        List<SheetElement> cellTemp = new ArrayList<>();
        Map<String, Column> columnTemp = new HashMap<>();
        List<SheetElement> rowTemp = new ArrayList<>();

        for(Row row : sheet){
            entity.SheetElement.BasicElement.Row row1 = new entity.SheetElement.BasicElement.Row(worksheet,
                    Integer.toString(row.getRowNum()));
            for(Cell cell : row) {
                entity.SheetElement.BasicElement.Cell cell1 = new entity.SheetElement.BasicElement.Cell(worksheet,
                        convertColumn(Integer.toString(cell.getColumnIndex())),cell.getRowIndex());
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
                    Column column = new Column(worksheet, convertColumn(Integer.toString(cell.getColumnIndex())));
                    column.addCell(cell1);
                    columnTemp.put(convertColumn(Integer.toString(cell.getColumnIndex())), column);
                }
                else{
                    Column column = columnTemp.get(convertColumn(Integer.toString(cell.getColumnIndex())));
                    column.addCell(cell1);
                }
                cellTemp.add(cell1);
                row1.addCell(cell1);
            }
            rowTemp.add(row1);
        }

        worksheet.addElement(ROW, rowTemp);
        worksheet.addElement(CELL, cellTemp);
        List<SheetElement> colTemp = new ArrayList<>();
        for(Map.Entry<String, Column> temp : columnTemp.entrySet()) {
            colTemp.add(temp.getValue());
        }
        worksheet.addElement(COLUMN, colTemp);
    }

    /**
     * convert column index from poi into actual index in entity.excel
     * @param column colum index from poi
     * @return actual entity.excel index
     */
    private String convertColumn(String column) {
        String result = "";
        int min = 65;
        int temp = Integer.parseInt(column);
        boolean check = false;

        while(temp >= 0) {
            if(temp <= 25){
                check = true;
            }
            int calc = temp % 26;
            temp = temp/26;

            char character = (char) (min + calc);
            result = result + character;
            if(check){
                break;
            }
        }
        if(column == "0") System.out.println(result);
        return result;
    }

    /**
     * read table sheet element from entity.excel, convert it into table object and add it into worksheet object
     * @param workbook workbook target(poi)
     * @param sheetName worksheet target (poi)
     * @param worksheet add table element into worksheet object(self-created)
     */
    private void readTable(XSSFWorkbook workbook, String sheetName, Worksheet worksheet) {
        List<SheetElement> sheetElements = new ArrayList<>();
        try{
            XSSFSheet sheet = workbook.getSheet(sheetName);
            if(sheet == null) throw new FileNotFoundException("there is not sheet with name " + sheetName);
            List<XSSFTable> tables = sheet.getTables();
            List<XSSFPivotTable> pivotTables = sheet.getPivotTables();

            for(int i = 0; i<tables.size(); i++) {
                XSSFTable t = tables.get(i);
                Table table = new Table(worksheet, convertColumn(Integer.toString(t.getEndColIndex())),
                        convertColumn(Integer.toString(t.getStartColIndex())),
                        Integer.toString(t.getStartRowIndex()+1), Integer.toString(t.getEndRowIndex()+1), t.getName());
                List<SheetElement> cell = worksheet.getSheets().get(CELL);
                for(int j = 0;j<cell.size();j++) {
                    if(cell.get(j) instanceof entity.SheetElement.BasicElement.Cell) {
                        if((((entity.SheetElement.BasicElement.Cell) cell.get(j)).getColumn().charAt(0) >= convertColumn(Integer.toString(t.getStartColIndex())).charAt(0)
                        && ((entity.SheetElement.BasicElement.Cell) cell.get(j)).getColumn().charAt(0) <= convertColumn(Integer.toString(t.getEndColIndex())).charAt(0))
                        && (((entity.SheetElement.BasicElement.Cell) cell.get(j)).getRow() >= t.getStartRowIndex()+1
                        && ((entity.SheetElement.BasicElement.Cell) cell.get(j)).getRow() <= t.getEndRowIndex()+1)) {
                            table.addCell((entity.SheetElement.BasicElement.Cell)cell.get(j));
                        }
                        else if(((entity.SheetElement.BasicElement.Cell) cell.get(j)).getColumn().charAt(0) >
                        convertColumn(Integer.toString(t.getEndColIndex())).charAt(0)){
                            break;
                        }
                    }
                }
                sheetElements.add(table);
            }
            worksheet.addElement(TABLE, sheetElements);

        } catch (FileNotFoundException err) {
            System.out.println(err);
        }

    }

    public Workbook getWorkbook() {
        return workbook;
    }
}
