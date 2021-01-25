package persistence.impl;

import entity.Operator;
import entity.SheetElement.BasicElement.Cell;
import entity.SheetElement.BasicElement.Column;
import entity.SheetElement.BasicElement.Row;
import entity.SheetElement.ElementType;
import entity.SheetElement.SheetElement;
import entity.SheetElement.Tables.Table;
import entity.Workbook.Workbook;
import entity.Worksheet.Worksheet;
import mapper.readExcelTableBased;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.vocabulary.RDFS;
import persistence.OntologyDao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class OntologyTableDao implements OntologyDao {

    private OntologyTableBasedConverter converter;
    private mapper.readExcelTableBased readExcelTableBased;
    private OntModel model;
    private Workbook workbook;
    private int colHeader;
    private int rowHeader;

    public OntologyTableDao(int colHeader, int rowHeader) {
        this.colHeader = colHeader;
        this.rowHeader = rowHeader;
    }

    @Override
    public void create(String filepath) {
        File file = new File(filepath);
        System.out.println(file.getName());
        this.readExcelTableBased = new readExcelTableBased(file,colHeader,rowHeader);
        this.workbook = this.readExcelTableBased.getWorkbook();
        this.converter = new OntologyTableBasedConverter(file.getName());
        this.model = this.converter.getModel();
        convertExcelToOntology();
        saveModel(filepath);


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
     * add entity.excel data into the rdf-graph
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
                    ws.getSheetName() + "_" + table.id() + "_" + column.getColumnTitle().replaceAll(" ","_"));
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
                            Individual cell1 = this.converter.getValue().createIndividual(table1.getURI() + "_" +
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
                            Individual col = this.converter.getColumnHeader().createIndividual(this.converter.getWorkbook().getURI() + "_" +
                                    ws.getSheetName() + "_" + table.id() + "_" + cell.getColumnTitle());
                            cell1.addProperty(this.converter.getValueFor(), col);
                            val.addProperty(this.converter.getHasDependency(), cell1);
                        }
                        for (int k = 0; k < column.getFormulaValue().getColumnDependency().size(); k++) {
                            Column column1 = column.getFormulaValue().getColumnDependency().get(k);
                            Individual col = this.converter.getColumnHeader().createIndividual(this.converter.getWorkbook().getURI() + "_" +
                                    ws.getSheetName() + "_" + table.id() + "_" + column1.getColumnTitle().replaceAll(" ", "_"));
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
                    ws.getSheetName() + "_" + table.id() + "_" +  cell.getColumnTitle().replaceAll(" ", "_"));
            cell1.addProperty(this.converter.getValueFor(), col);
            table1.addProperty(this.converter.getHasValue(), cell1);
        }

        for(int i = 0; i<table.getRows().size();i++) {
            Row row = table.getRows().get(i);
            Individual rowHeader = this.converter.getRowHeader().createIndividual(table1.getURI() + "_"+
                    row.getRowTitle().replaceAll(" ", "_"));
            if(row.getRowTitle() != null) {
                rowHeader.addLiteral(RDFS.label, row.getRowTitle());
            }
            Individual colHeader = this.converter.getColumnHeader().createIndividual(this.converter.getWorkbook().getURI() + "_" +
                    ws.getSheetName() + "_" + table.id() + "_" +  row.getColumnTitle());
            rowHeader.addProperty(this.converter.getHasColumnHeader(), colHeader);
            for(int j = 0; j<row.getCell().size();j++) {
                Cell cell = table.getCell().get(i);
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
     * @param filePath file model that want to be converted into file
     */
    private void saveModel(String filePath) {
        FileOutputStream out = null;
        File myFile = new File(filePath);
        try {
            out = new FileOutputStream("./OntologyOut/" + myFile.getName().replaceAll(" ", "_")+".ttl");
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