package com.java.fto.persistence.impl;

import com.java.fto.entity.Operator;
import com.java.fto.entity.Restriction.Restriction;
import com.java.fto.entity.SheetElement.BasicElement.Cell;
import com.java.fto.entity.SheetElement.BasicElement.Column;
import com.java.fto.entity.SheetElement.BasicElement.Row;
import com.java.fto.entity.SheetElement.ElementType;
import com.java.fto.entity.SheetElement.SheetElement;
import com.java.fto.entity.SheetElement.Tables.Table;
import com.java.fto.entity.Workbook.Workbook;
import com.java.fto.entity.Worksheet.Worksheet;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.vocabulary.RDFS;
import com.java.fto.persistence.OntologyDao;
import org.springframework.stereotype.Repository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Repository
public class OntologyTableDao implements OntologyDao {

    private OntologyTableBasedConverter converter;
    private OntModel model;
    private Workbook workbook;
    private Restriction restriction;

    public OntologyTableDao() {
    }

    @Override
    public void createAuto(Workbook workbook) {
        this.workbook = workbook;
        this.converter = new OntologyTableBasedConverter(this.workbook.getFileName());
        this.model = this.converter.getModel();
        convertExcelToOntology();
        saveModel(this.workbook.getFileName());

    }

    @Override
    public void createCustom(Workbook workbook, Restriction restriction) {
        this.workbook = workbook;
        this.restriction = restriction;
        this.converter = new OntologyTableBasedConverter(this.workbook.getFileName());
        this.model = this.converter.getModel();
        convertCustomExcelToOntology(restriction);
        saveModel(this.workbook.getFileName());
    }

    @Override
    public Collection<String> getCellDependencies(String cellID, String worksheetName) {
        return null;
    }

    @Override
    public Collection<String> addConstraint(ElementType type, String typeID, String worksheetName, Operator operator, String value) {
        return null;
    }

    @Override
    public Collection<String> getReverseDependencies(String cellID, String worksheetName) {
        return null;
    }

    /**
     * add com.java.eto.entity.excel data into the rdf-graph
     */
    private void convertExcelToOntology(){
        //System.out.println("converting 1 workbook");
        Individual workbook = addWorkbook();
        //System.out.println("converting " + this.workbook.getWorksheets().size() + " worksheet");
        for(int i = 0;i<this.workbook.getWorksheets().size();i++){
            //  System.out.println("worksheet " + (i+1));
            Individual worksheet = addWorksheet(this.workbook.getWorksheets().get(i));
            workbook.addProperty(this.converter.getHasWorksheet(), worksheet);
            List<SheetElement> table =  new ArrayList<>();

            //initialize list with the saved data in map.
            for(Map.Entry<ElementType, List<SheetElement>> m : this.workbook.getWorksheets().get(i).getSheets().entrySet()) {
                switch (m.getKey()){
                    case TABLE:
                        table = m.getValue();
                        break;
                    default: break;
                }
            }

            //check if worksheet contain basic + sheet element or not
            if(table.size() != 0) {
                for(int j = 0; j<table.size(); j++) {
                    addTable((Table) table.get(j), worksheet, this.workbook.getWorksheets().get(i));
                }
            }
        }
    }

    private void convertCustomExcelToOntology(Restriction restriction) {
        //System.out.println("converting 1 workbook");
        Individual workbook = addWorkbook();
        //System.out.println("converting " + this.workbook.getWorksheets().size() + " worksheet");
        for(int i = 0;i<this.workbook.getWorksheets().size();i++) {
            if (restriction.getTablesInWorksheet().containsKey(this.workbook.getWorksheets().get(i).getSheetName())) {
                //  System.out.println("worksheet " + (i+1));
                Individual worksheet = addWorksheet(this.workbook.getWorksheets().get(i));
                workbook.addProperty(this.converter.getHasWorksheet(), worksheet);
                List<SheetElement> table =  new ArrayList<>();

                //initialize list with the saved data in map.
                for(Map.Entry<ElementType, List<SheetElement>> m : this.workbook.getWorksheets().get(i).getSheets().entrySet()) {
                    switch (m.getKey()){
                        case TABLE:
                            table = m.getValue();
                            break;
                        default: break;
                    }
                }

                //check if worksheet contain basic + sheet element or not
                if(table.size() != 0) {
                    for(int j = 0; j<table.size(); j++) {
                        addCustomTable((Table) table.get(j), worksheet, this.workbook.getWorksheets().get(i),
                                restriction.getTablesInWorksheet().get(this.workbook.getWorksheets().get(i).getSheetName()));
                    }
                }

            }
        }
    }

    private void addCustomTable(Table table, Individual worksheet, Worksheet ws, List<com.java.fto.entity.Restriction.Table> tables) {
        for(int x = 0; x<tables.size(); x++) {
            Individual table1 = this.converter.getTable().createIndividual(this.converter.getWorkbook().getURI() + "_" +
                    ws.getSheetName() + "_" + tables.get(x).getName());
            table1.addLiteral(RDFS.label, tables.get(x).getName());
            table1.addLiteral(this.converter.getTableName(), tables.get(x).getName());
            for(int i = 0; i< table.getColumns().size(); i++) {
                Column column = table.getColumns().get(i);
                System.out.println(column.getColumnTitle());
                if(!tables.get(x).getColumnHeader().contains(column.getColumnTitle())){
                    System.out.println("skip");
                    continue;
                }
                Individual columnHeader = this.converter.getColumnHeader().createIndividual(this.converter.getWorkbook().getURI() + "_" +
                        ws.getSheetName() + "_" + column.getColumnTitle().replaceAll(" ","_"));
                columnHeader.addLiteral(this.converter.getColumnHeaderID(), column.getColumnID());
                columnHeader.addLiteral(this.converter.getColumnHeaderTitle(), column.getColumnTitle());
                columnHeader.addLiteral(RDFS.label, column.getColumnTitle().replaceAll(" ", "_"));
                Individual val;
                if(column.getValue() != null) {
                    switch (column.getValue()) {
                        case STRING:
                            val = this.converter.getString().createIndividual(table1.getURI() + "_" +
                                    column.getColumnTitle().replaceAll(" ","_") +"_StringValueType");
                            val.addLiteral(RDFS.label, "String");
                            columnHeader.addProperty(this.converter.getHasValueType(), val);
                            break;
                        case BOOLEAN:
                            val = this.converter.getBool().createIndividual(table1.getURI()+ "_" +
                                    column.getColumnTitle().replaceAll(" ","_") +"_BooleanValueType");
                            val.addLiteral(RDFS.label, "Boolean");
                            columnHeader.addProperty(this.converter.getHasValueType(), val);
                            break;
                        case ERROR:
                            val = this.converter.getError().createIndividual(table1.getURI()+ "_" +
                                    column.getColumnTitle().replaceAll(" ","_") +"_ErrorValueType");
                            val.addLiteral(RDFS.label, "Error");
                            columnHeader.addProperty(this.converter.getHasValueType(), val);
                            break;
                        case NUMERIC:
                            val = this.converter.getNumber().createIndividual(table1.getURI()+ "_" +
                                    column.getColumnTitle().replaceAll(" ","_") +"_NumericValueType");
                            val.addLiteral(RDFS.label, "Numeric");
                            columnHeader.addProperty(this.converter.getHasValueType(), val);
                            break;
                        case FORMULA:
                            val = this.converter.getFormula().createIndividual(table1.getURI()+ "_" +
                                    column.getColumnTitle().replaceAll(" ","_") +"_FormulaValueType");
                            val.addLiteral(RDFS.label, "Formula");
                            val.addLiteral(this.converter.getFormulaFunction(), column.getFormulaValue().getFormulaFunction());
                            for (int k = 0; k < column.getFormulaValue().getCellDependency().size(); k++) {
                                Cell cell = column.getFormulaValue().getCellDependency().get(k);
                                Individual cell1 = this.converter.getValue().createIndividual(this.converter.getWorkbook().getURI() + "_" +
                                        cell.getWorksheet().getSheetName() + "_" + cell.getTableName() + "_" +
                                        cell.getColumnTitle().replaceAll(" ", "_") + "_Value_For_Row" + cell.getRowID());
                                cell1.addProperty(this.converter.getIsUsedIn(), val);
                                switch (cell.getValue()) {
                                    case STRING:
                                        cell1.addLiteral(this.converter.getActualValue(), cell.getStringValue());
                                        break;
                                    case NUMERIC:
                                        cell1.addLiteral(this.converter.getActualValue(), cell.getNumericValue());
                                        break;
                                    case ERROR:
                                        cell1.addLiteral(this.converter.getActualValue(), cell.getError().getErrorName());
                                        break;
                                    case BOOLEAN:
                                        cell1.addLiteral(this.converter.getActualValue(), cell.isBooleanValue());
                                        break;
                                    case FORMULA:
                                        switch (cell.getFormulaValue().getValueType()) {
                                            case BOOLEAN:
                                                cell1.addLiteral(this.converter.getActualValue(), cell.getFormulaValue().getBooleanValue());
                                                break;
                                            case STRING:
                                                cell1.addLiteral(this.converter.getActualValue(), cell.getFormulaValue().getStringValue());
                                                break;
                                            case NUMERIC:
                                                cell1.addLiteral(this.converter.getActualValue(), cell.getFormulaValue().getNumericValue());
                                                break;
                                            case ERROR:
                                                cell1.addLiteral(this.converter.getActualValue(), "ERROR");
                                                break;
                                        }
                                        break;
                                }
                                val.addProperty(this.converter.getHasDependency(), cell1);
                            }
                            for (int k = 0; k < column.getFormulaValue().getColumnDependency().size(); k++) {
                                Column column1 = column.getFormulaValue().getColumnDependency().get(k);
                                Individual col = this.converter.getColumnHeader().createIndividual(this.converter.getWorkbook().getURI() + "_" +
                                        column1.getWorksheet().getSheetName()  + "_" + column1.getColumnTitle().replaceAll(" ", "_"));
                                col.addProperty(this.converter.getIsUsedIn(), val);
                                val.addProperty(this.converter.getHasDependency(), col);
                            }
                            columnHeader.addProperty(this.converter.getHasValueType(), val);
                            break;
                    }

                    table1.addProperty(this.converter.getHasColumnHeader(), columnHeader);
                }
            }
            for(int i = 0; i<table.getCell().size();i++) {
                Cell cell = table.getCell().get(i);
                if(!tables.get(x).getColumnHeader().contains(cell.getColumnTitle())) {
                    continue;
                }
                Individual cell1 = this.converter.getValue().createIndividual(table1.getURI()+"_"+
                        cell.getColumnTitle().replaceAll(" ", "_")+"_Value_For_Row"+
                        cell.getRowID().replaceAll(" ", "_"));
                cell1.addLiteral(RDFS.label, cell.getColumnTitle()+"_Value_For_Row"+cell.getRowID());
                switch (cell.getValue()) {
                    case STRING:
                        cell1.addLiteral(this.converter.getActualValue(), cell.getStringValue());
                        break;
                    case NUMERIC:
                        cell1.addLiteral(this.converter.getActualValue(), cell.getNumericValue());
                        break;
                    case ERROR:
                        cell1.addLiteral(this.converter.getActualValue(), cell.getError().getErrorName());
                        break;
                    case BOOLEAN:
                        cell1.addLiteral(this.converter.getActualValue(), cell.isBooleanValue());
                        break;
                    case FORMULA:
                        switch(cell.getFormulaValue().getValueType()) {
                            case BOOLEAN:
                                cell1.addLiteral(this.converter.getActualValue(), cell.getFormulaValue().getBooleanValue());
                                break;
                            case STRING:
                                cell1.addLiteral(this.converter.getActualValue(), cell.getFormulaValue().getStringValue());
                                break;
                            case NUMERIC:
                                cell1.addLiteral(this.converter.getActualValue(), cell.getFormulaValue().getNumericValue());
                                break;
                            case ERROR:
                                cell1.addLiteral(this.converter.getActualValue(), "ERROR");
                                break;
                        }
                        break;
                }
                Individual col = this.converter.getColumnHeader().createIndividual(this.converter.getWorkbook().getURI() + "_" +
                        ws.getSheetName() + "_" +  cell.getColumnTitle().replaceAll(" ", "_"));
                cell1.addProperty(this.converter.getValueFor(), col);
                table1.addProperty(this.converter.getHasValue(), cell1);
            }
            for(int i = 0; i<table.getRows().size();i++) {
                Row row = table.getRows().get(i);
                if(!tables.get(x).getRowHeader().contains(row.getRowTitle())) {
                    continue;
                }
                Individual rowHeader = this.converter.getRowHeader().createIndividual(this.converter.getWorkbook().getURI() + "_" +
                        ws.getSheetName() + "_"+
                        row.getRowTitle().replaceAll(" ", "_"));
                if(row.getRowTitle() != null) {
                    rowHeader.addLiteral(RDFS.label, row.getRowTitle());
                }
                Individual colHeader = this.converter.getColumnHeader().createIndividual(this.converter.getWorkbook().getURI() + "_" +
                        ws.getSheetName()  + "_" +  row.getColumnTitle().replaceAll(" ", ""));
                rowHeader.addProperty(this.converter.getHasColumnHeader(), colHeader);
                for(int j = 0; j<row.getCell().size();j++) {
                    Cell cell = table.getCell().get(j);
                    Individual cell1 = this.converter.getValue().createIndividual(table1.getURI()+"_"+
                            cell.getColumnTitle().replaceAll(" ", "_")+"_Value_For_Row"+
                            cell.getRowID().replaceAll(" ","_"));
                    rowHeader.addProperty(this.converter.getHasValue(), cell1);
                }
                rowHeader.addLiteral(this.converter.getRowHeaderID(), row.getRowId());
                if(row.getRowTitle() != null) {
                    rowHeader.addLiteral(this.converter.getRowHeaderTitle(), row.getRowTitle());
                }
                table1.addProperty(this.converter.getHasRowHeader(), rowHeader);
            }
            worksheet.addProperty(this.converter.getHasTable(), table1);
        }
    }

    private void addTable(Table table, Individual worksheet, Worksheet ws) {
        Individual table1 = this.converter.getTable().createIndividual(this.converter.getWorkbook().getURI() + "_" +
                ws.getSheetName() + "_" + table.id());
        table1.addLiteral(RDFS.label, table.id());
        //table1.addLiteral(this.converter.getRowEnd(), table.getRowEnd());
        //table1.addLiteral(this.converter.getRowStart(), table.getRowStart());
        table1.addLiteral(this.converter.getTableName(), table.id());
        for(int i = 0; i< table.getColumns().size(); i++) {
            Column column = table.getColumns().get(i);
            Individual columnHeader = this.converter.getColumnHeader().createIndividual(this.converter.getWorkbook().getURI() + "_" +
                    ws.getSheetName() + "_" + column.getColumnTitle().replaceAll(" ","_"));
            columnHeader.addLiteral(this.converter.getColumnHeaderID(), column.getColumnID());
            columnHeader.addLiteral(this.converter.getColumnHeaderTitle(), column.getColumnTitle());
            columnHeader.addLiteral(RDFS.label, column.getColumnTitle().replaceAll(" ", "_"));
            Individual val;
            if(column.getValue() != null) {
                switch (column.getValue()) {
                    case STRING:
                        val = this.converter.getString().createIndividual(table1.getURI() + "_" +
                                column.getColumnTitle().replaceAll(" ","_") +"_StringValueType");
                        val.addLiteral(RDFS.label, "String");
                        columnHeader.addProperty(this.converter.getHasValueType(), val);
                        break;
                    case BOOLEAN:
                        val = this.converter.getBool().createIndividual(table1.getURI()+ "_" +
                                column.getColumnTitle().replaceAll(" ","_") +"_BooleanValueType");
                        val.addLiteral(RDFS.label, "Boolean");
                        columnHeader.addProperty(this.converter.getHasValueType(), val);
                        break;
                    case ERROR:
                        val = this.converter.getError().createIndividual(table1.getURI()+ "_" +
                                column.getColumnTitle().replaceAll(" ","_") +"_ErrorValueType");
                        val.addLiteral(RDFS.label, "Error");
                        columnHeader.addProperty(this.converter.getHasValueType(), val);
                        break;
                    case NUMERIC:
                        val = this.converter.getNumber().createIndividual(table1.getURI()+ "_" +
                                column.getColumnTitle().replaceAll(" ","_") +"_NumericValueType");
                        val.addLiteral(RDFS.label, "Numeric");
                        columnHeader.addProperty(this.converter.getHasValueType(), val);
                        break;
                    case FORMULA:
                        val = this.converter.getFormula().createIndividual(table1.getURI()+ "_" +
                                column.getColumnTitle().replaceAll(" ","_") +"_FormulaValueType");
                        val.addLiteral(RDFS.label, "Formula");
                        val.addLiteral(this.converter.getFormulaFunction(), column.getFormulaValue().getFormulaFunction());
                        for (int k = 0; k < column.getFormulaValue().getCellDependency().size(); k++) {
                            Cell cell = column.getFormulaValue().getCellDependency().get(k);
                            Individual cell1 = this.converter.getValue().createIndividual(this.converter.getWorkbook().getURI() + "_" +
                                    cell.getWorksheet().getSheetName() + "_" + cell.getTableName() + "_" +
                                    cell.getColumnTitle().replaceAll(" ", "_") + "_Value_For_Row" + cell.getRowID());
                            cell1.addProperty(this.converter.getIsUsedIn(), val);
                            switch (cell.getValue()) {
                                case STRING:
                                    cell1.addLiteral(this.converter.getActualValue(), cell.getStringValue());
                                    break;
                                case NUMERIC:
                                    cell1.addLiteral(this.converter.getActualValue(), cell.getNumericValue());
                                    break;
                                case ERROR:
                                    cell1.addLiteral(this.converter.getActualValue(), cell.getError().getErrorName());
                                    break;
                                case BOOLEAN:
                                    cell1.addLiteral(this.converter.getActualValue(), cell.isBooleanValue());
                                    break;
                                case FORMULA:
                                    switch (cell.getFormulaValue().getValueType()) {
                                        case BOOLEAN:
                                            cell1.addLiteral(this.converter.getActualValue(), cell.getFormulaValue().getBooleanValue());
                                            break;
                                        case STRING:
                                            cell1.addLiteral(this.converter.getActualValue(), cell.getFormulaValue().getStringValue());
                                            break;
                                        case NUMERIC:
                                            cell1.addLiteral(this.converter.getActualValue(), cell.getFormulaValue().getNumericValue());
                                            break;
                                        case ERROR:
                                            cell1.addLiteral(this.converter.getActualValue(), "ERROR");
                                            break;
                                    }
                                    break;
                            }
                            val.addProperty(this.converter.getHasDependency(), cell1);
                        }
                        for (int k = 0; k < column.getFormulaValue().getColumnDependency().size(); k++) {
                            Column column1 = column.getFormulaValue().getColumnDependency().get(k);
                            Individual col = this.converter.getColumnHeader().createIndividual(this.converter.getWorkbook().getURI() + "_" +
                                    column1.getWorksheet().getSheetName()  + "_" + column1.getColumnTitle().replaceAll(" ", "_"));
                            col.addProperty(this.converter.getIsUsedIn(), val);
                            val.addProperty(this.converter.getHasDependency(), col);
                        }
                        columnHeader.addProperty(this.converter.getHasValueType(), val);
                        break;
                }

                table1.addProperty(this.converter.getHasColumnHeader(), columnHeader);
            }
        }
        for(int i = 0; i<table.getCell().size();i++) {
            Cell cell = table.getCell().get(i);
            Individual cell1 = this.converter.getValue().createIndividual(table1.getURI()+"_"+
                    cell.getColumnTitle().replaceAll(" ", "_")+"_Value_For_Row"+
                    cell.getRowID().replaceAll(" ", "_"));
            cell1.addLiteral(RDFS.label, cell.getColumnTitle()+"_Value_For_Row"+cell.getRowID());
            switch (cell.getValue()) {
                case STRING:
                    cell1.addLiteral(this.converter.getActualValue(), cell.getStringValue());
                    break;
                case NUMERIC:
                    cell1.addLiteral(this.converter.getActualValue(), cell.getNumericValue());
                    break;
                case ERROR:
                    cell1.addLiteral(this.converter.getActualValue(), cell.getError().getErrorName());
                    break;
                case BOOLEAN:
                    cell1.addLiteral(this.converter.getActualValue(), cell.isBooleanValue());
                    break;
                case FORMULA:
                    switch(cell.getFormulaValue().getValueType()) {
                        case BOOLEAN:
                            cell1.addLiteral(this.converter.getActualValue(), cell.getFormulaValue().getBooleanValue());
                            break;
                        case STRING:
                            cell1.addLiteral(this.converter.getActualValue(), cell.getFormulaValue().getStringValue());
                            break;
                        case NUMERIC:
                            cell1.addLiteral(this.converter.getActualValue(), cell.getFormulaValue().getNumericValue());
                            break;
                        case ERROR:
                            cell1.addLiteral(this.converter.getActualValue(), "ERROR");
                            break;
                    }
                    break;
            }
            Individual col = this.converter.getColumnHeader().createIndividual(this.converter.getWorkbook().getURI() + "_" +
                    ws.getSheetName() + "_" +  cell.getColumnTitle().replaceAll(" ", "_"));
            cell1.addProperty(this.converter.getValueFor(), col);
            table1.addProperty(this.converter.getHasValue(), cell1);
        }

        for(int i = 0; i<table.getRows().size();i++) {
            Row row = table.getRows().get(i);
            Individual rowHeader = this.converter.getRowHeader().createIndividual(this.converter.getWorkbook().getURI() + "_" +
                    ws.getSheetName() + "_"+
                    row.getRowTitle().replaceAll(" ", "_"));
            if(row.getRowTitle() != null) {
                rowHeader.addLiteral(RDFS.label, row.getRowTitle());
            }
            Individual colHeader = this.converter.getColumnHeader().createIndividual(this.converter.getWorkbook().getURI() + "_" +
                    ws.getSheetName()  + "_" +  row.getColumnTitle().replaceAll(" ", ""));
            rowHeader.addProperty(this.converter.getHasColumnHeader(), colHeader);
            for(int j = 0; j<row.getCell().size();j++) {
                Cell cell = table.getCell().get(j);
                Individual cell1 = this.converter.getValue().createIndividual(table1.getURI()+"_"+
                        cell.getColumnTitle().replaceAll(" ", "_")+"_Value_For_Row"+
                        cell.getRowID().replaceAll(" ","_"));
                rowHeader.addProperty(this.converter.getHasValue(), cell1);
            }
            rowHeader.addLiteral(this.converter.getRowHeaderID(), row.getRowId());
            if(row.getRowTitle() != null) {
                rowHeader.addLiteral(this.converter.getRowHeaderTitle(), row.getRowTitle());
            }
            table1.addProperty(this.converter.getHasRowHeader(), rowHeader);
        }
        worksheet.addProperty(this.converter.getHasTable(), table1);
    }

    /**
     * save model into file with turtle format
     * @param fileName file model that want to be converted into file
     */
    private void saveModel(String fileName) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream("./OntologyOut/" + fileName +".ttl");
        } catch (IOException e) {
            System.out.println(e);
        }
        this.converter.getModel().write(out, "TTL");
    }

    /**
     * add workbook data into the rdf-graph
     * @return workbook instance.
     */
    private Individual addWorkbook() {
        Individual workbook = this.converter.getWorkbook().createIndividual(this.converter.getWorkbook().getURI() + "_" +
                this.workbook.getFileName());
        workbook.addLiteral(RDFS.label, this.workbook.getFileName());
        return workbook;
    }

    /**
     * add worksheet data into the rdf-graph
     * @param worksheet for naming purposes(not really important can be removed i think)
     * @return worksheet instance.
     */
    private Individual addWorksheet(Worksheet worksheet){
        Individual i = this.converter.getWorksheet().createIndividual(this.converter.getWorksheet().getURI() + "_" +
                worksheet.getSheetName());
        i.addLiteral(this.converter.getSheetName(), worksheet.getSheetName());
        i.addLiteral(RDFS.label, worksheet.getSheetName());
        return i;
    }

}
