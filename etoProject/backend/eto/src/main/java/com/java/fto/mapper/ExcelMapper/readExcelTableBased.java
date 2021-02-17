package com.java.fto.mapper.ExcelMapper;

import com.java.fto.entity.EndpointEntity.FileItem;
import com.java.fto.entity.EndpointEntity.Receiver.WorksheetReceiver;
import com.java.fto.entity.EndpointEntity.WorkbookEndpoint;
import com.java.fto.entity.EndpointEntity.WorkbookTable;
import com.java.fto.entity.SheetElement.BasicElement.Column;
import com.java.fto.entity.SheetElement.SheetElement;
import com.java.fto.entity.SheetElement.Tables.Table;
import com.java.fto.entity.ValueType.Formula;
import com.java.fto.entity.ValueType.FunctionType;
import com.java.fto.entity.ValueType.NestedFormula;
import com.java.fto.entity.ValueType.Value;
import com.java.fto.entity.Workbook.Workbook;
import com.java.fto.entity.Worksheet.Worksheet;
import com.java.fto.exception.IncorrectTypeException;
import com.java.fto.entity.SheetElement.ElementType;
import org.apache.logging.log4j.core.util.FileUtils;
import org.apache.poi.ss.formula.functions.T;
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

public class readExcelTableBased implements ExcelReader {

    private XSSFWorkbook myWorkBook;
    private Workbook workbook;
    private File file;
    private WorkbookEndpoint workbookEndpoint;

    public readExcelTableBased(File file) {
        this.file = file;
    }

    @Override
    public void readExcelConverter() throws IOException {
        int count = 0;

        //read each element in worksheet in a table format
        for(Sheet sheet : myWorkBook) {
                //if its not null, thats mean the row header is from another worksheet
                if(workbook.getWorksheets().get(count).getSheetName().equals(sheet.getSheetName().replaceAll(" ", ""))) {
                    if(workbook.getWorksheets().get(count).getRowHeaderFrom() != null) {
                        count++;
                        continue;
                    }
                    Table table = new Table(workbook.getWorksheets().get(count),
                            "Table_"+workbook.getWorksheets().get(count).getSheetName());
                    readTable(table, sheet, workbook.getWorksheets().get(count),
                            (WorkbookTable) workbookEndpoint.getWorksheets().get(count));
                    count++;
                }
        }

        count = 0;

        //if there is a worksheet that want to use rowheader from another worksheet
        //will be assigned here
        for(int i = 0; i<workbook.getWorksheets().size(); i++){
            if(workbook.getWorksheets().get(i).getRowHeaderFrom() == null) {
                continue;
            } else {
                Worksheet worksheetTo =  workbook.getWorksheets().get(i);
                Worksheet worksheetFrom = workbook.getWorksheets().stream()
                        .filter(worksheet -> worksheet.getSheetName().equals(worksheetTo.getRowHeaderFrom()))
                        .findAny()
                        .orElse(null);
                System.out.println(worksheetFrom.getSheetName());
                if(worksheetFrom != null) {
                    assignRowHeaderFromAnotherWorksheet(worksheetFrom, worksheetTo,
                            (WorkbookTable) workbookEndpoint.getWorksheets().get(i));
                }
            }

        }

        //assign all value to the custom worksheet;
        for(Sheet sheet : myWorkBook) {
            //if its not null, thats mean the row header is from another worksheet
            if(workbook.getWorksheets().get(count).getSheetName().equals(sheet.getSheetName().replaceAll(" ", ""))) {
                if(workbook.getWorksheets().get(count).getRowHeaderFrom() != null) {
                    Table table = (Table) workbook.getWorksheets().get(count).getSheets()
                            .get(ElementType.TABLE).get(0);

                    readTableCustom(table, sheet, workbook.getWorksheets().get(count),
                            (WorkbookTable) workbookEndpoint.getWorksheets().get(count));
                }
                count++;
            }
        }

        //assign formula dependency
        for(int i = 0; i< this.workbook.getWorksheets().size(); i++) {
            Worksheet ws = this.workbook.getWorksheets().get(i);
            List<SheetElement> table = ws.getSheets().get(ElementType.TABLE);
            for(int j = 0; j<table.size();j++) {
                Table table1 = (Table) table.get(j);
                formulaColumnDependencyCheck(table1.getColumns(), table1.getCell());
            }
        }
    }

    private void assignRowHeaderFromAnotherWorksheet(Worksheet worksheetFrom, Worksheet worksheetTo,
                                                     WorkbookTable workbookTable) {
        List<com.java.fto.entity.SheetElement.BasicElement.Row> rows = new ArrayList<>();
        System.out.println(worksheetFrom.getSheetName());
        System.out.println(worksheetFrom.getSheets().get(ElementType.TABLE).size());
        Table table =  (Table) worksheetFrom.getSheets().get(ElementType.TABLE).get(0);
        Table table1 = new Table(worksheetTo,
                "Table_" + worksheetTo.getSheetName());
        List<String> rowTemp =  new ArrayList<>();

        for(int i = 0; i<table.getRows().size(); i++) {
            com.java.fto.entity.SheetElement.BasicElement.Row row = new com.java.fto.entity.SheetElement.BasicElement.Row(
                    worksheetFrom, table.getRows().get(i).getRowTitle()
            );
            row.setColumnTitle(table.getRows().get(i).getColumnTitle());
            row.setRowTitle(table.getRows().get(i).getRowTitle());
            rows.add(row);
            System.out.println(row);
            rowTemp.add(row.getRowTitle());
        }
        table1.setRows(rows);
        List<SheetElement> sheetElements = new ArrayList<>();
        sheetElements.add(table1);
        worksheetTo.addElement(ElementType.TABLE, sheetElements);
        workbookTable.setRows(rowTemp);
    }
/*
    @Override
    public void readExcelConverterWithRestriction(Restriction restriction) throws IOException {
        TableRestriction tableRestriction = (TableRestriction) restriction;

        for(int i = 0;i<tableRestriction.getWorksheets().size(); i++) {
            for(int j = 0; j<workbook.getWorksheets().size(); j++) {
                if(workbook.getWorksheets().get(i).getSheetName().equals(tableRestriction.getWorksheets().get(i))) {
                    workbook.getWorksheets().remove(j);
                    break;
                }
            }
        }

        if(tableRestriction.getColumnsInWorksheet() != null || tableRestriction.getTablesInWorksheet()!= null) {
            for (Sheet sheet : myWorkBook) {
                for (int i = 0; i < workbook.getWorksheets().size(); i++) {
                    if (workbook.getWorksheets().get(i).getSheetName().equals(sheet.getSheetName().replaceAll(" ", ""))) {
                        if (tableRestriction.getColumnsInWorksheet()!= null &&
                                tableRestriction.getColumnsInWorksheet().containsKey(sheet.getSheetName())) {
                            Table table = new Table(workbook.getWorksheets().get(i), "Table_" + workbook.getWorksheets().get(i).getSheetName());
                            readTable(table, sheet, workbook.getWorksheets().get(i), tableRestriction.getColumnsInWorksheet().get(sheet.getSheetName()));
                        } else if (tableRestriction.getTablesInWorksheet()!= null &&
                                tableRestriction.getTablesInWorksheet().containsKey(sheet.getSheetName())) {
                            Map<String, List<String>> temp = tableRestriction.getTablesInWorksheet().get(sheet.getSheetName());
                            for(Map.Entry<String, List<String>> tables : temp.entrySet()) {
                                Table table = new Table(workbook.getWorksheets().get(i), tables.getKey());
                                readTable(table, sheet, workbook.getWorksheets().get(i), tables.getValue());
                            }
                        } else {
                            Table table = new Table(workbook.getWorksheets().get(i),
                                    "Table_"+workbook.getWorksheets().get(i).getSheetName());
                            readTable(table, sheet, workbook.getWorksheets().get(i), null);
                        }
                    }
                }
            }
        }
    }


 */
    private void readTable(Table table, Sheet sheet, Worksheet worksheet, WorkbookTable workbookTable) {
        List<com.java.fto.entity.SheetElement.BasicElement.Cell> cellTemp = new ArrayList<>();
        Map<String, Column> columnTemp = new HashMap<>();
        List<com.java.fto.entity.SheetElement.BasicElement.Row> rowTemp = new ArrayList<>();
        List<String> columnHeader = new ArrayList<>();
        List<String> rowHeader = new ArrayList<>();
        boolean emptyCell = false;
        for(Row row : sheet){
            System.out.println(worksheet.getSheetName() + " row " + row.getRowNum());
            com.java.fto.entity.SheetElement.BasicElement.Row row1 = new com.java.fto.entity.SheetElement.BasicElement.Row(worksheet,
                    Integer.toString(row.getRowNum()));
            for(Cell cell : row) {
                if(row1.getRowTitle() == null &&
                        (cell.getColumnIndex() == worksheet.getRowHeaderIndex() || worksheet.getRowHeaderIndex() == -1)) {
                    if(cell.getRowIndex() == worksheet.getColumnHeaderIndex()){
                        row1.setRowTitle("ColumnHeader");
                        rowHeader.add(row1.getRowTitle());
                    } else if(worksheet.getColumnHeaderIndex() == -1) {
                        worksheet.setColumnHeaderIndex(cell.getColumnIndex());
                        worksheet.setRowHeaderIndex(cell.getRowIndex());
                        row1.setRowTitle("ColumnHeader");
                        rowHeader.add(row1.getRowTitle());
                    } else {
                        try {
                            switch (cell.getCellType()) {
                                case STRING:
                                    row1.setRowTitle(cell.getRichStringCellValue().getString());
                                    rowHeader.add(row1.getRowTitle());
                                    break;
                                case FORMULA:
                                    switch (cell.getCachedFormulaResultType()){
                                        case STRING:
                                            row1.setRowTitle(cell.getStringCellValue());
                                            rowHeader.add(row1.getRowTitle());
                                            break;
                                        default:
                                            break;
                                            //throw new IncorrectTypeException(worksheet.getSheetName() + " row header "+ row.getRowNum() + " must be a type of string");
                                    }
                                    break;
                                case BLANK:
                                    continue;
                                case NUMERIC:
                                    row1.setRowTitle(cell.getNumericCellValue() + "");
                                    rowHeader.add(row1.getRowTitle());
                                    break;
                                default:
                                    throw new IncorrectTypeException(worksheet.getSheetName() + " row header "+ row.getRowNum() + " must be a type of string");
                            }
                        } catch (IncorrectTypeException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
                com.java.fto.entity.SheetElement.BasicElement.Cell cell1 = new com.java.fto.entity.SheetElement.BasicElement.Cell(worksheet,
                        convertColumn(Integer.toString(cell.getColumnIndex())),cell.getRowIndex());
                cell1.setRowID(row1.getRowId());
                if(row1.getRowTitle() == null && cell.getColumnIndex() > worksheet.getRowHeaderIndex()) {
                    row1.setRowTitle("NO_TITLE_ROW_" + cell.getRowIndex());
                    rowHeader.add(row1.getRowTitle());
                }
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
                cell1.setTableName(table.id());
                if(!columnTemp.containsKey(convertColumn(Integer.toString(cell.getColumnIndex())))){
                    if(cell.getRowIndex() >= worksheet.getColumnHeaderIndex()) {
                        Column column = new Column(worksheet, convertColumn(Integer.toString(cell.getColumnIndex())));
                            if (cell1.getStringValue() == null) {
                                column.setColumnTitle("No_Title_Column_" + convertColumn(Integer.toString(cell.getColumnIndex())));
                                if(row1.getColumnTitle() == null && worksheet.getRowHeaderIndex() == cell.getColumnIndex()) {
                                    row1.setColumnTitle("No_Title_Column_" + convertColumn(Integer.toString(cell.getColumnIndex())));
                                }
                            } else {
                                column.setColumnTitle(cell1.getStringValue());
                                if(row1.getColumnTitle() == null && worksheet.getRowHeaderIndex() == cell.getColumnIndex()) {
                                    row1.setColumnTitle(cell1.getStringValue());
                                }
                            }
                            columnHeader.add(column.getColumnTitle());
                        columnTemp.put(convertColumn(Integer.toString(cell.getColumnIndex())), column);
                    }
                    continue;
                }
                else{
                    Column column = columnTemp.get(convertColumn(Integer.toString(cell.getColumnIndex())));
                    if (column.getValue() == null) {
                        column.setValue(cell1.getValue());
                        if(cell1.getValue() == Value.FORMULA) {
                            column.setFormulaValue(cell1.getFormulaValue());
                        }
                    } else if(column.getValue() != cell1.getValue()) {
                        cell1.setSameValueAsColumnHeader(false);
                    }
                    cell1.setColumnTitle(column.getColumnTitle());
                    if(row1.getColumnTitle() == null && worksheet.getRowHeaderIndex() == cell.getColumnIndex()) {
                        row1.setColumnTitle(cell1.getStringValue());
                    }
                    column.addCell(cell1);
                }
                cellTemp.add(cell1);
                row1.addCell(cell1);
            }
            rowTemp.add(row1);
        }

        for(int i = 1; i<rowTemp.size(); i++){
            rowTemp.get(i).setColumnTitle(rowTemp.get(0).getColumnTitle());
        }

        table.setCell(cellTemp);
        table.setRows(rowTemp);
        //formulaCellDependencyCheck(cellTemp);
        List<Column> colTemp = new ArrayList<>();
        workbookTable.setColumns(columnHeader);
        workbookTable.setRows(rowHeader);
        for(Map.Entry<String, Column> temp : columnTemp.entrySet()) {
            colTemp.add(temp.getValue());
        }
        table.setColumns(colTemp);
        if(worksheet.getSheets().containsKey(ElementType.TABLE)) {
            List<SheetElement> temp = worksheet.getSheets().get(ElementType.TABLE);
            temp.add(table);
            worksheet.getSheets().replace(ElementType.TABLE, temp);
        } else {
            List<SheetElement> tables = new ArrayList<>();
            tables.add(table);
            worksheet.addElement(ElementType.TABLE, tables);
        }
    }

    private void readTableCustom(Table table, Sheet sheet, Worksheet worksheet, WorkbookTable workbookTable) {
        List<com.java.fto.entity.SheetElement.BasicElement.Cell> cellTemp = new ArrayList<>();
        Map<String, Column> columnTemp = new HashMap<>();
        List<com.java.fto.entity.SheetElement.BasicElement.Row> rowTemp = table.getRows();
        List<String> columnHeader = new ArrayList<>();
        List<String> rowHeader = new ArrayList<>();
        int count = 0;
        boolean emptyCell = false;
        for(Row row : sheet){
            com.java.fto.entity.SheetElement.BasicElement.Row row1;
            if(count > rowTemp.size()-1 ) {
                row1 = new com.java.fto.entity.SheetElement.BasicElement.Row(worksheet,
                        Integer.toString(row.getRowNum()));
            } else {
                row1 = rowTemp.get(count);
                rowHeader.add(row1.getRowTitle());
            }
            for(Cell cell : row) {
                if(row1.getRowTitle() == null &&
                        (cell.getColumnIndex() == worksheet.getRowHeaderIndex() || worksheet.getRowHeaderIndex() == -1)
                        && count > rowTemp.size()-1) {
                    if(cell.getRowIndex() == worksheet.getColumnHeaderIndex()){
                        row1.setRowTitle("ColumnHeader");
                        rowHeader.add(row1.getRowTitle());
                    } else if(worksheet.getColumnHeaderIndex() == -1) {
                        worksheet.setColumnHeaderIndex(cell.getColumnIndex());
                        worksheet.setRowHeaderIndex(cell.getRowIndex());
                        row1.setRowTitle("ColumnHeader");
                        rowHeader.add(row1.getRowTitle());
                    } else {
                        try {
                            switch (cell.getCellType()) {
                                case STRING:
                                    row1.setRowTitle(cell.getRichStringCellValue().getString());
                                    rowHeader.add(row1.getRowTitle());
                                    break;
                                case FORMULA:
                                    switch (cell.getCachedFormulaResultType()){
                                        case STRING:
                                            row1.setRowTitle(cell.getStringCellValue());
                                            rowHeader.add(row1.getRowTitle());
                                            break;
                                        default:
                                            break;
                                            //throw new IncorrectTypeException(worksheet.getSheetName() + " row header "+ row.getRowNum() + " must be a type of string");
                                    }
                                    break;
                                case BLANK:
                                    continue;
                                case NUMERIC:
                                    row1.setRowTitle(cell.getNumericCellValue() + "");
                                    rowHeader.add(row1.getRowTitle());
                                    break;
                                default:
                                    throw new IncorrectTypeException(worksheet.getSheetName() + " row header "+ row.getRowNum() + " must be a type of string");
                            }
                        } catch (IncorrectTypeException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
                com.java.fto.entity.SheetElement.BasicElement.Cell cell1 = new com.java.fto.entity.SheetElement.BasicElement.Cell(worksheet,
                        convertColumn(Integer.toString(cell.getColumnIndex())),cell.getRowIndex());
                cell1.setRowID(row1.getRowId());
                if(row1.getRowTitle() == null && cell.getColumnIndex() > worksheet.getRowHeaderIndex()) {
                    row1.setRowTitle("NO_TITLE_ROW_" + cell.getRowIndex());
                    rowHeader.add(row1.getRowTitle());
                }
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
                cell1.setTableName(table.id());
                if(!columnTemp.containsKey(convertColumn(Integer.toString(cell.getColumnIndex())))){
                    if(cell.getRowIndex() >= worksheet.getColumnHeaderIndex()) {
                        Column column = new Column(worksheet, convertColumn(Integer.toString(cell.getColumnIndex())));
                        if (cell1.getStringValue() == null) {
                            column.setColumnTitle("No_Title_Column_" + convertColumn(Integer.toString(cell.getColumnIndex())));
                            if(row1.getColumnTitle() == null && worksheet.getRowHeaderIndex() == cell.getColumnIndex()) {
                                row1.setColumnTitle("No_Title_Column_" + convertColumn(Integer.toString(cell.getColumnIndex())));
                            }
                        } else {
                            column.setColumnTitle(cell1.getStringValue());
                            if(row1.getColumnTitle() == null && worksheet.getRowHeaderIndex() == cell.getColumnIndex()) {
                                row1.setColumnTitle(cell1.getStringValue());
                            }
                        }
                        columnHeader.add(column.getColumnTitle());
                        columnTemp.put(convertColumn(Integer.toString(cell.getColumnIndex())), column);
                    }
                    continue;
                }
                else{
                    Column column = columnTemp.get(convertColumn(Integer.toString(cell.getColumnIndex())));
                    if (column.getValue() == null) {
                        column.setValue(cell1.getValue());
                        if(cell1.getValue() == Value.FORMULA) {
                            column.setFormulaValue(cell1.getFormulaValue());
                        }
                    } else if(column.getValue() != cell1.getValue()) {
                        cell1.setSameValueAsColumnHeader(false);
                    }
                    cell1.setColumnTitle(column.getColumnTitle());
                    if(row1.getColumnTitle() == null && worksheet.getRowHeaderIndex() == cell.getColumnIndex()) {
                        row1.setColumnTitle(cell1.getStringValue());
                    }
                    column.addCell(cell1);
                }
                cellTemp.add(cell1);
                row1.addCell(cell1);
            }
            if(count > rowTemp.size()-1) {
                rowTemp.add(row1);
            }
            count++;
        }

        for(int i = 1; i<rowTemp.size(); i++){
            rowTemp.get(i).setColumnTitle(rowTemp.get(0).getColumnTitle());
        }

        table.setCell(cellTemp);
        //formulaCellDependencyCheck(cellTemp);
        List<Column> colTemp = new ArrayList<>();
        workbookTable.setColumns(columnHeader);
        workbookTable.setRows(rowHeader);
        for(Map.Entry<String, Column> temp : columnTemp.entrySet()) {
            colTemp.add(temp.getValue());
        }
        table.setColumns(colTemp);
        if(worksheet.getSheets().containsKey(ElementType.TABLE)) {
            List<SheetElement> temp = worksheet.getSheets().get(ElementType.TABLE);
            temp.add(table);
            worksheet.getSheets().replace(ElementType.TABLE, temp);
        } else {
            List<SheetElement> tables = new ArrayList<>();
            tables.add(table);
            worksheet.addElement(ElementType.TABLE, tables);
        }
    }

    public void initializeWorkbook() throws IOException {
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
        this.workbookEndpoint = new WorkbookEndpoint();
        this.workbookEndpoint.setWorkbookName(this.workbook.getFileName());
        List<FileItem> worksheets = new ArrayList<>();
        for(Sheet sheet : myWorkBook) {
            Worksheet worksheet = new Worksheet(sheet.getSheetName().replaceAll(" ", ""), workbook);
            workbook.addWorksheet(worksheet);
            WorkbookTable workbookTable = new WorkbookTable();
            workbookTable.setWorksheetName(worksheet.getSheetName());
            worksheets.add(workbookTable);
        }
        workbookEndpoint.setWorksheets(worksheets);
    }

    public void initializeColumnAndRow(List<WorksheetReceiver> ws) {
        for(int i = 0; i<ws.size(); i++){
            WorksheetReceiver sheet = ws.get(i);
            Worksheet temp = workbook.getWorksheets().stream()
                    .filter(worksheet -> sheet.getWorksheetName().equals(worksheet.getSheetName()))
                    .findAny()
                    .orElse(null);
            if(temp != null) {
                if(sheet.getColumnsRowsFrom() != null) {
                    temp.setRowHeaderFrom(sheet.getColumnsRowsFrom());
                    temp.setColumnHeaderIndex(Integer.parseInt(sheet.getColumnHeader()));
                    temp.setRowHeaderIndex(-1);
                    continue;
                }
                temp.setColumnHeaderIndex(Integer.parseInt(sheet.getColumnHeader()));
                temp.setRowHeaderIndex(Integer.parseInt(sheet.getRowHeader()));
            }
        }
    }

    /**
     * add column dependency to column that have Formula value.
     * @param column list of column.
     */
    private void formulaColumnDependencyCheck(List<Column> column, List<com.java.fto.entity.SheetElement.BasicElement.Cell> cells){
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
    private void formulaDependencyCheck(List<Formula> formulas, List<Column> column,
                                        List<com.java.fto.entity.SheetElement.BasicElement.Cell> cells,
                                        Column column1) {
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
    private void addAllFormulaColumnDependency(String formula, List<Column> columnList,
                                               List<com.java.fto.entity.SheetElement.BasicElement.Cell> cellList,
                                               Column column) {
        formula = formula.replaceAll("'", "");
        formula = formula.replaceAll(" ","");
        String regex = "\\(|\\)|,|\\+|-|\\*|/|;";
        String[] temp = formula.split(regex);

        for(int i = 0;i<temp.length; i++) {
            System.out.println(temp[i]);
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
    private void addFormulaCellDependency(String formula, List<com.java.fto.entity.SheetElement.BasicElement.Cell> cellList, Column column) {
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
        for(int i = 0;i<temp.length; i++) {
            if(temp[i].matches(patternCell)){
                String check = temp[i];
                com.java.fto.entity.SheetElement.BasicElement.Cell cell1 = (com.java.fto.entity.SheetElement.BasicElement.Cell)cellList.stream()
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
                        List<SheetElement> cells = worksheet.getSheets().getOrDefault(ElementType.CELL, null);
                        if (cells != null) {
                            com.java.fto.entity.SheetElement.BasicElement.Cell cell1 = (com.java.fto.entity.SheetElement.BasicElement.Cell) cells.stream()
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
                    com.java.fto.entity.SheetElement.BasicElement.Cell cell2 = (com.java.fto.entity.SheetElement.BasicElement.Cell)cellList.stream()
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
                    List<SheetElement> cells = worksheet.getSheets().getOrDefault(ElementType.CELL, null);
                    if(cells != null) {
                        for(int l = 0; l<cell2.size(); l++) {
                            String workSheetTemp = cell2.get(l);
                            com.java.fto.entity.SheetElement.BasicElement.Cell cell1 = (com.java.fto.entity.SheetElement.BasicElement.Cell) cells.stream()
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
                    List<SheetElement> cells = worksheet.getSheets().getOrDefault(ElementType.CELL, null);
                    if(cells != null) {
                        for(int l = 0; l<cell2.size(); l++) {
                            String workSheetTemp = cell2.get(l);
                            com.java.fto.entity.SheetElement.BasicElement.Cell cell1 = (com.java.fto.entity.SheetElement.BasicElement.Cell) cells.stream()
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


    private void addFormulaColumnDependency(String formula, List<Column> columnList, Column column) {
        String patternColumn = "[a-zA-Z]+\\d+";
        String patternColumnToColumn = patternColumn + ":" + patternColumn;
        String patternColumnFromOtherSheet = "'*[a-zA-Z]+\\d*'*![a-zA-Z]+\\d+\\s*";
        String patternColumnToColumnFromOtherSheet = patternColumnFromOtherSheet + ":" + patternColumnFromOtherSheet;
        String patternColumnToColumnFromOtherSheet2 = patternColumnFromOtherSheet + ":" + patternColumn;
        if(formula.matches(patternColumn)){
            String check = formula.replaceAll("\\d", "");
            Column column1 = columnList.stream()
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
                    List<SheetElement> tables = worksheet.getSheets().getOrDefault(ElementType.TABLE, null);
                    for(int l = 0; l<tables.size(); l++) {
                        Table table = (Table) tables.get(l);
                        List<Column> columns = table.getColumns();
                        if (columns != null) {
                            Column column1 = (Column) columns.stream()
                                    .filter(columnTemp -> worksheetCellSplit[1].replaceAll("\\d", "").equals(columnTemp.id()))
                                    .findAny()
                                    .orElse(null);
                            if (column1 != null) {
                                column.getFormulaValue().addColumnDependencies(column1);
                                break;
                            }
                        }
                    }
                }
        }
        else if(formula.matches(patternColumnToColumn)) {
            List<String> columnToColumns = columnToColumn(formula.replaceAll("\\d", ""));
            for(int l = 0; l<columnToColumns.size(); l++) {
                String check1 = columnToColumns.get(l);
                Column column2 = columnList.stream()
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
                tempSplit = tempSplit.replaceAll("\\d", "");
                List<String> column2 = columnToColumn(tempSplit);
                List<SheetElement> tables = worksheet.getSheets().getOrDefault(ElementType.TABLE, null);
                if(tables != null) {
                    for(int l = 0; l<column2.size(); l++) {
                        String workSheetTemp = column2.get(l);
                        for(int p = 0; p<tables.size();p++) {
                            Table table = (Table) tables.get(p);
                            List<Column> columns = table.getColumns();
                            Column column1 = (Column) columns.stream()
                                    .filter(columnTemp -> workSheetTemp.equals(columnTemp.id()))
                                    .findAny()
                                    .orElse(null);
                            if (column1 != null) {
                                column.getFormulaValue().addColumnDependencies(column);
                                break;
                            }
                        }
                    }
                }
            }
        } else if(formula.matches(patternColumnToColumnFromOtherSheet2)) {
            System.out.println("im hereee");
            String[] worksheetColumnSplit = formula.split("!");
            List<Worksheet> worksheets = this.workbook.getWorksheets();
            Worksheet worksheet = worksheets.stream()
                    .filter(worksheet1 -> worksheetColumnSplit[0].equals(worksheet1.getSheetName()))
                    .findAny()
                    .orElse(null);

            if(worksheet!= null){
                List<String> column2 = columnToColumn(worksheetColumnSplit[1].replaceAll("\\d", ""));
                List<SheetElement> tables = worksheet.getSheets().getOrDefault(ElementType.TABLE, null);
                if(tables != null) {
                    for(int l = 0; l<column2.size(); l++) {
                        String workSheetTemp = column2.get(l);
                        for(int p = 0; p<tables.size(); p++) {
                            Table table = (Table) tables.get(p);
                            List<Column> columns = table.getColumns();
                            Column column1 = (Column) columns.stream()
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
    }

    /**
     * convert column index from poi into actual index in com.java.eto.entity.excel
     * @param column colum index from poi
     * @return actual com.java.eto.entity.excel index
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

    public WorkbookEndpoint getWorkbookEndpoint() {
        return workbookEndpoint;
    }
}
