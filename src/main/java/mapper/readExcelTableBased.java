package mapper;

import entity.SheetElement.BasicElement.Column;
import entity.SheetElement.SheetElement;
import entity.SheetElement.Tables.Table;
import entity.ValueType.Formula;
import entity.ValueType.FunctionType;
import entity.ValueType.NestedFormula;
import entity.ValueType.Value;
import entity.Workbook.Workbook;
import entity.Worksheet.Worksheet;
import exception.IncorrectTypeException;
import org.apache.logging.log4j.core.util.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static entity.SheetElement.ElementType.*;

public class readExcelTableBased implements ExcelReader {

    private XSSFWorkbook myWorkBook;
    private Workbook workbook;

    readExcelTableBased(File file, int columnHeader, int rowHeader) {
        try {
            initializeWorkbook(file);
            for(int i = 0; i<workbook.getWorksheets().size(); i++) {
                workbook.getWorksheets().get(i).setColumnHeaderIndex(columnHeader);
                workbook.getWorksheets().get(i).setRowHeaderIndex(rowHeader);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    readExcelTableBased(File file) {
        try {
            initializeWorkbook(file);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }



    @Override
    public void readExcelConverter() throws IOException {
        for(Sheet sheet : myWorkBook) {
            for(int i = 0; i<workbook.getWorksheets().size(); i++) {
                if(workbook.getWorksheets().get(i).getSheetName().equals(sheet.getSheetName().replaceAll(" ", ""))) {
                    readTable(sheet, workbook.getWorksheets().get(i));
                }
            }
        }
    }

    public void readTable(Sheet sheet, Worksheet worksheet) {
        Table table = new Table(worksheet, "Table_" + worksheet.getSheetName());
        List<entity.SheetElement.BasicElement.Cell> cellTemp = new ArrayList<>();
        Map<String, Column> columnTemp = new HashMap<>();
        List<entity.SheetElement.BasicElement.Row> rowTemp = new ArrayList<>();
        boolean emptyCell = false;

        for(Row row : sheet){
            entity.SheetElement.BasicElement.Row row1 = new entity.SheetElement.BasicElement.Row(worksheet,
                    Integer.toString(row.getRowNum()));
            for(Cell cell : row) {
                if(cell.getColumnIndex() == worksheet.getRowHeaderIndex()) {
                    try {
                        switch (cell.getCellType()) {
                            case STRING:
                                row1.setRowTitle(cell.getRichStringCellValue().getString());
                                break;
                            default:
                                throw new IncorrectTypeException("row header must be a type of string");

                        }
                        continue;
                    } catch (IncorrectTypeException e) {
                        System.out.println(e.getMessage());
                        return;
                    }
                }
                entity.SheetElement.BasicElement.Cell cell1 = new entity.SheetElement.BasicElement.Cell(worksheet,
                        convertColumn(Integer.toString(cell.getColumnIndex())),cell.getRowIndex());
                switch (cell.getCellType()){
                    case STRING:
                        cell1.setValue(Value.STRING);
                        cell1.setStringValue(cell.getRichStringCellValue().getString());
                        break;
                    case BLANK:
                        emptyCell = true;
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
                        FormulaConverter formulaConverter =  new FormulaConverter(cell.getCellFormula());
                        Formula formula = formulaConverter.getFormula();
                        switch (cell.getCachedFormulaResultType()){
                            case BOOLEAN:
                                formula.setBooleanValue(cell.getBooleanCellValue());
                                formula.setValueType(Value.BOOLEAN);
                                break;
                            case STRING:
                                formula.setStringValue(cell.getStringCellValue());
                                formula.setValueType(Value.STRING);
                                break;
                            case NUMERIC:
                                formula.setNumericValue((float)cell.getNumericCellValue());
                                formula.setValueType(Value.NUMERIC);
                                break;
                            case ERROR:
                                formula.setErrorValue(cell.getErrorCellValue());
                                formula.setValueType(Value.ERROR);
                        }
                        cell1.setFormulaValue(formula);
                        break;
                    case NUMERIC:
                        cell1.setValue(Value.NUMERIC);
                        cell1.setNumericValue((float)cell.getNumericCellValue());
                        break;
                }
                if(emptyCell) {
                    emptyCell = false;
                    continue;
                }
                if(!columnTemp.containsKey(Integer.toString(cell.getColumnIndex()))){
                    Column column = new Column(worksheet, convertColumn(Integer.toString(cell.getColumnIndex())));
                    try {
                        if (cell1.getStringValue() == null) {
                            throw new IncorrectTypeException("column header cannot be a type other than string");
                        } else {
                            column.setColumnTitle(cell1.getStringValue());
                        }
                    } catch (IncorrectTypeException e) {
                        System.out.println(e.getMessage());
                        return;
                    }
                    columnTemp.put(convertColumn(Integer.toString(cell.getColumnIndex())), column);
                }
                else{
                    Column column = columnTemp.get(convertColumn(Integer.toString(cell.getColumnIndex())));
                    try {
                        if (column.getValue() == null) {
                            column.setValue(cell1.getValue());
                            if(cell1.getValue() == Value.FORMULA) {
                                column.setFormulaValue(cell1.getFormulaValue());
                            }
                        } else if (column.getValue() != cell1.getValue()) {
                            throw new IncorrectTypeException("not all cell in column have the same value type");
                        }
                    } catch (IncorrectTypeException e) {
                        System.out.println(e.getMessage());
                        return;
                    }
                    column.addCell(cell1);
                }
                cellTemp.add(cell1);
                row1.addCell(cell1);
            }
            rowTemp.add(row1);
        }

        table.setCell(cellTemp);
        table.setRows(rowTemp);
        //formulaCellDependencyCheck(cellTemp);
        List<Column> colTemp = new ArrayList<>();
        for(Map.Entry<String, Column> temp : columnTemp.entrySet()) {
            colTemp.add(temp.getValue());
        }
        table.setColumns(colTemp);
        List<SheetElement> tables = new ArrayList<>();
        tables.add(table);
        worksheet.addElement(TABLE, tables);
    }

    private void initializeWorkbook(File file) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }

        // Finds the workbook instance for XLSX file
        myWorkBook = new XSSFWorkbook (fis);
        this.workbook = new Workbook((FileUtils.getFileExtension(file)),file.getName().replaceAll(" ", "_"));
        //iterate through sheet in workbook
        for(Sheet sheet : myWorkBook) {
            Worksheet worksheet = new Worksheet(sheet.getSheetName().replaceAll(" ", ""), workbook);
            workbook.addWorksheet(worksheet);
        }
    }

    /**
     * add column dependency to column that have Formula value.
     * @param column list of column.
     */
    private void formulaColumnDependencyCheck(List<SheetElement> column, List<SheetElement> cells){
        for(int i = 0; i<column.size(); i++){
            Column column1 = (Column) column.get(i);
            if(column1.getValue() == Value.FORMULA) {
                if(column1.getFormulaValue().getFunctionType() == FunctionType.BASIC) {
                    addAllFormulaColumnDependency(column1.getFormulaValue().getFormulaFunction(),
                            column, cells, column1);
                } else if(column1.getFormulaValue().getFunctionType() == FunctionType.NESTED) {
                    NestedFormula nestedFormula = (NestedFormula) column1.getFormulaValue();
                    formulaDependencyCheck(nestedFormula.getFormulaList(), column, cells, column1);
                }
            }
        }
    }

    /**
     * recursive function for nested formula
     * @param formulas list of formula in a nested formula
     * @param column list of column
     * @param column1 column which the list of formula from
     */
    private void formulaDependencyCheck(List<Formula> formulas, List<SheetElement> column,
                                        List<SheetElement> cells,
                                        entity.SheetElement.BasicElement.Column column1) {
        for(int i = 0; i<formulas.size(); i++){
            if(formulas.get(i).getFunctionType() == FunctionType.BASIC) {
                addAllFormulaColumnDependency(formulas.get(i).getFormulaFunction(), column, cells, column1);
            } else if(formulas.get(i).getFunctionType() == FunctionType.NESTED) {
                NestedFormula nestedFormula = (NestedFormula) formulas.get(i);
                formulaDependencyCheck(nestedFormula.getFormulaList(),column, cells, column1);
            }
        }
    }

    /**
     * save all the columnid in form of Cell:Cell in a string of list
     * example = C1 : B4 = C,B
     * @param text cell to cell that wanted to be converted
     * @return list of cell id
     */
    public List<String> columnToColumn(String text) {
        List<String> result = new ArrayList<>();
        String[] temp = text.split(":");
        String fromColumn = temp[0].replaceAll("\\d","");
        String toColumn = temp[1].replaceAll("\\d", "");
        int Aascii = 65;
        result.add(fromColumn);
        while(!fromColumn.equals(toColumn)) {
                if(fromColumn.equals(toColumn)) {
                    break;
                }
                else {
                    if(fromColumn.length() == 1) {
                        if(fromColumn.charAt(0) != 'Z') {
                            fromColumn = ""+(char)(fromColumn.charAt(0)+1);
                        }
                        else {
                            fromColumn = ""+(char)(Aascii) + (char)(Aascii);
                        }
                    }
                    else {
                        if(fromColumn.charAt(1) != 'Z') {
                            fromColumn = ""+ fromColumn.charAt(0)+(char)(fromColumn.charAt(1)+1);
                        }
                        else {
                            if(fromColumn.charAt(0) == 'Z') {
                                throw new IndexOutOfBoundsException("too many columns");
                            }
                            fromColumn = "" + (char)(fromColumn.charAt(0)+1) + (char)(Aascii);
                        }
                    }
                }
            result.add(fromColumn);
        }
        return result;
    }

    /**
     * use regex to detect Column and Worksheet in a formula
     * @param formula formula function of a cell
     * @param columnList list of column
     * @param column column that have a Formula value.
     */
    private void addAllFormulaColumnDependency(String formula, List<SheetElement> columnList,
                                               List<SheetElement> cellList,
                                               entity.SheetElement.BasicElement.Column column) {
        formula = formula.replaceAll("'", "");
        formula = formula.replaceAll(" ","");
        formula = formula.replaceAll("\\d", "");
        String regex = "\\(|\\)|,|\\+|-|\\*|/|;";
        String[] temp = formula.split(regex);

        for(int i = 0;i<temp.length; i++) {
            if (temp[i].contains("$")) {
                addFormulaCellDependency(temp[i], cellList, column);
            } else {
                addFormulaColumnDependency(temp[i], columnList, column);
            }
        }

    }

    /**
     * save all the cellid in form of Cell:Cell in a string of list
     * example = C1 : C4 = C1,C2,C3,C4
     * @param text cell to cell that wanted to be converted
     * @return list of cell id
     */
    public List<String> cellToCell(String text) {
        List<String> result = new ArrayList<>();
        String[] temp = text.split(":");
        String fromColumn = temp[0].replaceAll("\\d","");
        String toColumn = temp[1].replaceAll("\\d", "");
        int fromRow = Integer.parseInt(temp[0].replaceAll("[a-zA-Z]*", ""));
        int tempRow = fromRow;
        int toRow = Integer.parseInt(temp[1].replaceAll("[a-zA-Z]*", ""));
        int Aascii = 65;
        result.add(fromColumn+fromRow);
        while(fromRow < toRow || !fromColumn.equals(toColumn)) {
            if(fromRow == toRow) {
                if(fromColumn.equals(toColumn)) {
                    break;
                }
                else {
                    fromRow = tempRow;
                    if(fromColumn.length() == 1) {
                        if(fromColumn.charAt(0) != 'Z') {
                            fromColumn = ""+(char)(fromColumn.charAt(0)+1);
                        }
                        else {
                            fromColumn = ""+(char)(Aascii) + (char)(Aascii);
                        }
                    }
                    else {
                        if(fromColumn.charAt(1) != 'Z') {
                            fromColumn = ""+ fromColumn.charAt(0)+(char)(fromColumn.charAt(1)+1);
                        }
                        else {
                            if(fromColumn.charAt(0) == 'Z') {
                                throw new IndexOutOfBoundsException("too many columns");
                            }
                            fromColumn = "" + (char)(fromColumn.charAt(0)+1) + (char)(Aascii);
                        }
                    }
                }
            }
            else {
                fromRow++;
            }
            result.add(fromColumn+fromRow);
        }
        return result;
    }

    /**
     * use regex to detect Cell and Worksheet in a formula
     * @param formula formula function of a column
     * @param cellList list of cell
     * @param column cell that have a Formula value.
     */
    private void addFormulaCellDependency(String formula, List<SheetElement> cellList, entity.SheetElement.BasicElement.Column column) {
        formula = formula.replaceAll("\\$", "");
        formula = formula.replaceAll("'", "");
        formula = formula.replaceAll(" ","");
        String patternCell = "[a-zA-Z]+\\d+";
        String patternCellToCell = patternCell + ":" + patternCell;
        String patternCellFromOtherSheet = "'*[a-zA-Z]+\\d*'*![a-zA-Z]+\\d+\\s*";
        String patternCellToCellFromOtherSheet = patternCellFromOtherSheet + ":" + patternCellFromOtherSheet;
        String patternCellToCellFromOtherSheet2 = patternCellFromOtherSheet + ":" + patternCell;
        String regex = "\\(|\\)|,|\\+|-|\\*|/|;";
        String[] temp = formula.split(regex);

        /*
        if(formula.contains("IF") || formula.contains("if")) {
            ifFormulaDependency(formula.replaceFirst("IF|if", ""), cellList, cell);
            return;
        }

         */
        for(int i = 0;i<temp.length; i++) {;
            if(temp[i].matches(patternCell)){
                String check = temp[i];
                entity.SheetElement.BasicElement.Cell cell1 = (entity.SheetElement.BasicElement.Cell)cellList.stream()
                        .filter(cellCheck -> check.equals(cellCheck.id()))
                        .findAny()
                        .orElse(null);
                if(cell1 == null) continue;
                else{
                    Formula basicFormula1 = column.getFormulaValue();
                    basicFormula1.addDependencies(cell1);
                }
            }
            else if(temp[i].matches(patternCellFromOtherSheet)) {
                String[] worksheetCellSplit = temp[i].split("!");
                List<Worksheet> worksheets = this.workbook.getWorksheets();
                if(worksheetCellSplit[0].toLowerCase().equals("tabelle")) {
                    addFormulaCellDependency(worksheetCellSplit[1], cellList, column);
                }
                else {
                    Worksheet worksheet = worksheets.stream()
                            .filter(worksheet1 -> worksheetCellSplit[0].equals(worksheet1.getSheetName()))
                            .findAny()
                            .orElse(null);

                    if (worksheet != null) {
                        List<SheetElement> cells = worksheet.getSheets().getOrDefault(CELL, null);
                        if (cells != null) {
                            entity.SheetElement.BasicElement.Cell cell1 = (entity.SheetElement.BasicElement.Cell) cells.stream()
                                    .filter(cellTemp -> worksheetCellSplit[1].equals(cellTemp.id()))
                                    .findAny()
                                    .orElse(null);
                            if (cell1 != null) {
                                column.getFormulaValue().addDependencies(cell1);
                            }
                        }
                    }
                }
            }
            else if(temp[i].matches(patternCellToCell)) {
                List<String> cellToCells = cellToCell(temp[i]);
                for(int l = 0; l<cellToCells.size(); l++) {
                    String check1 = cellToCells.get(l);
                    entity.SheetElement.BasicElement.Cell cell2 = (entity.SheetElement.BasicElement.Cell)cellList.stream()
                            .filter(cellCheck -> check1.equals(cellCheck.id()))
                            .findAny()
                            .orElse(null);
                    if(cell2 == null) continue;
                    else{
                        Formula basicFormula1 = column.getFormulaValue();
                        basicFormula1.addDependencies(cell2);
                    }
                }
            }
            else if(temp[i].matches(patternCellToCellFromOtherSheet)) {
                String[] worksheetCellSplit = temp[i].split(":");
                String tempSplit = worksheetCellSplit[0].split("!")[1] + ":" + worksheetCellSplit[1].split("!")[1];
                List<Worksheet> worksheets = this.workbook.getWorksheets();
                Worksheet worksheet = worksheets.stream()
                        .filter(worksheet1 -> worksheetCellSplit[0].split("!")[0].equals(worksheet1.getSheetName()))
                        .findAny()
                        .orElse(null);

                if(worksheet!= null){
                    List<String> cell2 = cellToCell(tempSplit);
                    List<SheetElement> cells = worksheet.getSheets().getOrDefault(CELL, null);
                    if(cells != null) {
                        for(int l = 0; l<cell2.size(); l++) {
                            String workSheetTemp = cell2.get(l);
                            entity.SheetElement.BasicElement.Cell cell1 = (entity.SheetElement.BasicElement.Cell) cells.stream()
                                    .filter(cellTemp -> workSheetTemp.equals(cellTemp.id()))
                                    .findAny()
                                    .orElse(null);
                            if (cell1 != null) {
                                column.getFormulaValue().addDependencies(cell1);
                            }
                        }
                    }
                }
            } else if(temp[i].matches(patternCellToCellFromOtherSheet2)) {
                String[] worksheetCellSplit = temp[i].split("!");
                List<Worksheet> worksheets = this.workbook.getWorksheets();
                Worksheet worksheet = worksheets.stream()
                        .filter(worksheet1 -> worksheetCellSplit[0].equals(worksheet1.getSheetName()))
                        .findAny()
                        .orElse(null);

                if(worksheet!= null){
                    List<String> cell2 = cellToCell(worksheetCellSplit[1]);
                    List<SheetElement> cells = worksheet.getSheets().getOrDefault(CELL, null);
                    if(cells != null) {
                        for(int l = 0; l<cell2.size(); l++) {
                            String workSheetTemp = cell2.get(l);
                            entity.SheetElement.BasicElement.Cell cell1 = (entity.SheetElement.BasicElement.Cell) cells.stream()
                                    .filter(cellTemp -> workSheetTemp.equals(cellTemp.id()))
                                    .findAny()
                                    .orElse(null);
                            if (cell1 != null) {
                                column.getFormulaValue().addDependencies(cell1);
                            }
                        }
                    }
                }
            }
        }
    }


    private void addFormulaColumnDependency(String formula, List<SheetElement> columnList, entity.SheetElement.BasicElement.Column column) {
        String patternColumn = "[a-zA-Z]+";
        String patternColumnToColumn = patternColumn + ":" + patternColumn;
        String patternColumnFromOtherSheet = "'*[a-zA-Z]+'*![a-zA-Z]+\\s*";
        String patternColumnToColumnFromOtherSheet = patternColumnFromOtherSheet + ":" + patternColumnFromOtherSheet;
        String patternColumnToColumnFromOtherSheet2 = patternColumnFromOtherSheet + ":" + patternColumn;
        if(formula.matches(patternColumn)){
            String check = formula;
            entity.SheetElement.BasicElement.Column column1 = (entity.SheetElement.BasicElement.Column)columnList.stream()
                    .filter(columnCheck -> check.equals(columnCheck.id()))
                    .findAny()
                    .orElse(null);
            if(column1 == null) return;
            else{
                Formula basicFormula1 = column.getFormulaValue();
                basicFormula1.addColumnDependencies(column1);
            }
        }
        else if(formula.matches(patternColumnFromOtherSheet)) {
            String[] worksheetCellSplit = formula.split("!");
            List<Worksheet> worksheets = this.workbook.getWorksheets();
                Worksheet worksheet = worksheets.stream()
                        .filter(worksheet1 -> worksheetCellSplit[0].equals(worksheet1.getSheetName()))
                        .findAny()
                        .orElse(null);

                if (worksheet != null) {
                    List<SheetElement> columns = worksheet.getSheets().getOrDefault(COLUMN, null);
                    if (columns != null) {
                        entity.SheetElement.BasicElement.Column column1 = (entity.SheetElement.BasicElement.Column) columns.stream()
                                .filter(columnTemp -> worksheetCellSplit[1].equals(columnTemp.id()))
                                .findAny()
                                .orElse(null);
                        if (column1 != null) {
                            column.getFormulaValue().addColumnDependencies(column1);
                        }
                    }
                }
        }
        else if(formula.matches(patternColumnToColumn)) {
            List<String> columnToColumns = columnToColumn(formula);
            for(int l = 0; l<columnToColumns.size(); l++) {
                String check1 = columnToColumns.get(l);
                entity.SheetElement.BasicElement.Column column2 = (entity.SheetElement.BasicElement.Column)columnList.stream()
                        .filter(columnCheck -> check1.equals(columnCheck.id()))
                        .findAny()
                        .orElse(null);
                if(column2 == null) return;
                else{
                    Formula basicFormula1 = column.getFormulaValue();
                    basicFormula1.addColumnDependencies(column2);
                }
            }
        }
        else if(formula.matches(patternColumnToColumnFromOtherSheet)) {
            String[] worksheetCellSplit = formula.split(":");
            String tempSplit = worksheetCellSplit[0].split("!")[1] + ":" + worksheetCellSplit[1].split("!")[1];
            List<Worksheet> worksheets = this.workbook.getWorksheets();
            Worksheet worksheet = worksheets.stream()
                    .filter(worksheet1 -> worksheetCellSplit[0].split("!")[0].equals(worksheet1.getSheetName()))
                    .findAny()
                    .orElse(null);

            if(worksheet!= null){
                List<String> column2 = columnToColumn(tempSplit);
                List<SheetElement> columns = worksheet.getSheets().getOrDefault(COLUMN, null);
                if(columns != null) {
                    for(int l = 0; l<column2.size(); l++) {
                        String workSheetTemp = column2.get(l);
                        entity.SheetElement.BasicElement.Column column1 = (entity.SheetElement.BasicElement.Column) columns.stream()
                                .filter(columnTemp -> workSheetTemp.equals(columnTemp.id()))
                                .findAny()
                                .orElse(null);
                        if (column1 != null) {
                            column.getFormulaValue().addColumnDependencies(column);
                        }
                    }
                }
            }
        } else if(formula.matches(patternColumnToColumnFromOtherSheet2)) {
            String[] worksheetColumnSplit = formula.split("!");
            List<Worksheet> worksheets = this.workbook.getWorksheets();
            Worksheet worksheet = worksheets.stream()
                    .filter(worksheet1 -> worksheetColumnSplit[0].equals(worksheet1.getSheetName()))
                    .findAny()
                    .orElse(null);

            if(worksheet!= null){
                List<String> column2 = columnToColumn(worksheetColumnSplit[1]);
                List<SheetElement> columns = worksheet.getSheets().getOrDefault(COLUMN, null);
                if(columns != null) {
                    for(int l = 0; l<column2.size(); l++) {
                        String workSheetTemp = column2.get(l);
                        entity.SheetElement.BasicElement.Column column1 = (entity.SheetElement.BasicElement.Column) columns.stream()
                                .filter(columnTemp -> workSheetTemp.equals(columnTemp.id()))
                                .findAny()
                                .orElse(null);
                        if (column1 != null) {
                            column.getFormulaValue().addColumnDependencies(column1);
                        }
                    }
                }
            }
        }
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
            if(temp >=1 && temp <=25) {
                char character = (char) (min + (temp-1));
                result = result + character;
                character = (char) (min + calc);
                result = result + character;
                break;
            } else if(temp > 25) {
                char character = (char)(min + ((temp/26) - 1));
                result = result + character;
            } else {
                char character = (char) (min + calc);
                result = result + character;
            }
            if(check){
                break;
            }
        }
        return result;
    }

    public Workbook getWorkbook() {
        return workbook;
    }
}
