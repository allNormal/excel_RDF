package persistence.impl;

import entity.SheetElement.BasicElement.Cell;
import entity.SheetElement.Charts.Chart;
import entity.SheetElement.ElementType;
import entity.SheetElement.SheetElement;
import entity.SheetElement.Tables.Table;
import entity.SheetElement.Texts.Text;
import entity.Workbook.Workbook;
import entity.Worksheet.Worksheet;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.vocabulary.RDFS;
import java.io.*;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OntologyDao implements persistence.OntologyDao {
    private OntologyConverter converter;
    private Workbook workbook;
    private readExcel readExcel;
    private OntModel model;
    public OntologyDao(){

    }


    @Override
    public void create(String filepath) {
        File file = new File(filepath);
        try {
            System.out.println(file.getName());
            this.readExcel = new readExcel(file);
            this.workbook = this.readExcel.getWorkbook();
            this.converter = new OntologyConverter(file.getName());
            this.model = this.converter.getModel();
            convertExcelToOntology();
            saveModel(filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createRepoGraphDB(String filePath) {
        FileOutputStream out = null;
        File myFile = new File(filePath);
        try {
            out = new FileOutputStream("./OntologyOut/" + myFile.getName()+".ttl");
        } catch (IOException e) {
            System.out.println(e);
        }
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
            worksheet.addProperty(this.converter.getIsInWorkbook(), workbook);

            List<SheetElement> chart = new ArrayList<>();
            List<SheetElement> illustration = new ArrayList<>();
            List<SheetElement> table =  new ArrayList<>();
            List<SheetElement> text = new ArrayList<>();
            List<SheetElement> cell = new ArrayList<>();
            List<SheetElement> row = new ArrayList<>();
            List<SheetElement> column = new ArrayList<>();

            //initialize list with the saved data in map.
            for(Map.Entry<ElementType, List<SheetElement>> m : this.workbook.getWorksheets().get(i).getSheets().entrySet()) {
                switch (m.getKey()){
                    case TABLE:
                        table = m.getValue();
                        break;
                    case COLUMN:
                        column = m.getValue();
                        break;
                    case CELL:
                        cell = m.getValue();
                        break;
                    case ROW:
                        row = m.getValue();
                        break;
                    case CHART:
                        chart = m.getValue();
                        break;
                    case ILLUSTRATION:
                        illustration = m.getValue();
                        break;
                    case TEXT:
                        text = m.getValue();
                        break;
                    default: break;
                }
            }

            //check if worksheet contain basic + sheet element or not
            if(chart.size() != 0 || illustration.size() != 0 || table.size() != 0 || cell.size() != 0 ||
             row.size() != 0 || column.size() != 0 || text.size() != 0) {

                Individual sheetElement = this.converter.getSheetElement().createIndividual();
                worksheet.addProperty(this.converter.getHasSheetElement(), sheetElement);
                sheetElement.addProperty(this.converter.getIsPartOfWorksheet(), worksheet);
                addBasicElement(cell, worksheet, this.workbook.getWorksheets().get(i));
                addChart(chart, worksheet, this.workbook.getWorksheets().get(i));
                addTable(table, worksheet, this.workbook.getWorksheets().get(i));
                addText(text, worksheet, this.workbook.getWorksheets().get(i));
                addIllustration(illustration, worksheet, this.workbook.getWorksheets().get(i));
            }
        }
    }


    /**
     * save model into file with turtle format
     * @param filePath file model that want to be converted into file
     */
    private void saveModel(String filePath) {
        FileOutputStream out = null;
        File myFile = new File(filePath);
        try {
            out = new FileOutputStream("./OntologyOut/" + myFile.getName()+".ttl");
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
        workbook.addLiteral(this.converter.getExtension(), this.workbook.getExtension());
        workbook.addLiteral(this.converter.getFileName(), this.workbook.getFileName());
        if(this.workbook.getMacro()!=null) {
                Individual macro = this.converter.getMacro().createIndividual(this.converter.getMacro().getURI() + "_" +
                        "Macro");
                macro.addLiteral(this.converter.getMacroInput(), this.workbook.getMacro().getMacro());
                workbook.addProperty(this.converter.getHasMacro(), macro);
        }
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

    /**
     * Iterate through cell list and call addCell O(cell.size)
     * @param cell list of cell in the worksheet that already been converted from entity.excel
     * @param worksheet for isPartOfWorksheet relation so data can be tracked coming from which worksheet
     * @param ws for naming purposes(not really important can be removed i think)
     */
    private void addBasicElement(List<SheetElement> cell, Individual worksheet, Worksheet ws) {
        //System.out.println(cell.size());
        for(int i = 0;i<cell.size();i++) {
            if(cell.get(i) instanceof  Cell) {
                addCell((Cell)cell.get(i), worksheet, ws);
            }
        }

        //System.out.println("converting " + cell.size() + " cells");
    }

    /**
     * add basicelement(Row, Column, Cell) into the rdf-graph
     * @param cell cell data that already been converted from entity.excel and
     *            since row, column also saved in cell, we only need cell parameter.
     * @param worksheet for the relation isPartOfWorksheet (so data can be tracked coming from which worksheet)
     * @param ws for naming purposes(not really important can be removed i think)
     */
    private void addCell(Cell cell, Individual worksheet, Worksheet ws) {
        Individual i = this.converter.getCell().createIndividual(this.converter.getCell().getURI() +
                "_Worksheet" + ws.getSheetName() +
                "_" + cell.getCellId());
        i.addLiteral(RDFS.label, "Cell_"+cell.getCellId());
        i.addLiteral(this.converter.getCellID(), cell.getCellId());
        i.addProperty(this.converter.getIsPartOfWorksheet(), worksheet);
        Individual val;
        switch (cell.getValue()){
            case STRING:
                val = this.converter.getStringValue().createIndividual(i.getURI() + "_value" );
                val.addLiteral(RDFS.label, "StringValue");
                val.addLiteral(this.converter.getCellValue(), cell.getStringValue());
                i.addProperty(this.converter.getHasValue(),val);
                break;
            case NUMERIC:
                val = this.converter.getNumericValue().createIndividual(i.getURI() + "_value");
                val.addLiteral(RDFS.label, "NumericValue");
                val.addLiteral(this.converter.getCellValue(), cell.getNumericValue());
                i.addProperty(this.converter.getHasValue(),val);
                break;
            case FORMULA:
                val = this.converter.getFormulaValue().createIndividual(i.getURI() + "_value");
                val.addLiteral(RDFS.label, "FormulaValue");
                val.addLiteral(this.converter.getFunctionName(), cell.getFormulaValue().getFormulaFunction());
                /**
                 * add value into formula
                 */
                switch(cell.getFormulaValue().getValueType()) {
                    case BOOLEAN:
                        val.addLiteral(this.converter.getCellValue(), cell.getFormulaValue().getBooleanValue());
                        break;
                    case STRING:
                        val.addLiteral(this.converter.getCellValue(), cell.getFormulaValue().getStringValue());
                        break;
                    case NUMERIC:
                        val.addLiteral(this.converter.getCellValue(), cell.getFormulaValue().getNumericValue());
                        break;
                    case ERROR:
                        val.addLiteral(this.converter.getCellValue(), "ERROR");
                        break;
                }
                for(int k = 0; k<cell.getFormulaValue().getCellDependency().size(); k++) {
                    Individual temp = this.converter.getCell().createIndividual(this.converter.getCell().getURI() +
                            "_Worksheet" + cell.getFormulaValue().getCellDependency().get(k).getWorksheet().getSheetName() +
                            "_" + cell.getFormulaValue().getCellDependency().get(k).getCellId());
                    val.addProperty(this.converter.getHasCell(), temp);
                    temp.addProperty(this.converter.getIsUsedIn(), val);
                }
                i.addProperty(this.converter.getHasValue(),val);
                break;
            case BOOLEAN:
                val = this.converter.getBooleanValue().createIndividual(i.getURI() + "_value");
                val.addLiteral(RDFS.label, "BooleanValue");
                val.addLiteral(this.converter.getCellValue(), cell.isBooleanValue());
                i.addProperty(this.converter.getHasValue(),val);
                break;
            case ERROR:
                val = this.converter.getErrorValue().createIndividual(i.getURI() + "_value");
                val.addLiteral(RDFS.label, "ErrorValue");
                val.addLiteral(this.converter.getErrorName(), cell.getError().getErrorName());
                i.addProperty(this.converter.getHasValue(),val);
                break;
        }

        if(cell.getComment() != null) {
            Individual comment = this.converter.getComment().createIndividual(i.getURI() + "comment");
            comment.addLiteral(this.converter.getCommentsValue(), cell.getComment().getString().toString());
            comment.addLiteral(RDFS.label, cell.getCellId() + "-Comment");
            i.addProperty(this.converter.getHasComment(), comment);
        }

        Individual row = this.converter.getRow().createIndividual( this.converter.getRow().getURI() +
                "_Worksheet" + ws.getSheetName() +
                "_" + cell.getRow());
        row.addLiteral(RDFS.label, "Row_"+cell.getRow());
        row.addLiteral(this.converter.getRowID(), cell.getRow());
        row.addProperty(this.converter.getHasCell(), i);
        row.addProperty(this.converter.getIsPartOfWorksheet(), worksheet);
        i.addProperty(this.converter.getHasRow(),row);

        Individual column = this.converter.getColumn().createIndividual(this.converter.getColumn().getURI() +
                "_Worksheet" + ws.getSheetName() +
                "_" + cell.getColumn());
        column.addLiteral(RDFS.label, "Column_"+cell.getColumn());
        column.addLiteral(this.converter.getColumnID(),cell.getColumn());
        column.addProperty(this.converter.getHasCell(),i);
        column.addProperty(this.converter.getHasRow(), row);
        column.addProperty(this.converter.getIsPartOfWorksheet(), worksheet);
        i.addProperty(this.converter.getHasColumn(),column);
        row.addProperty(this.converter.getHasColumn(), column);
        worksheet.addProperty(this.converter.getHasSheetElement(), i);
        worksheet.addProperty(this.converter.getHasSheetElement(), column);
        worksheet.addProperty(this.converter.getHasSheetElement(), row);
    }

    /**
     * add chart data into the rdf-graph O(chart.size)
     * @param chart
     * @param worksheet
     * @param ws for naming purposes(not really important can be removed i think)
     */
    private void addChart(List<SheetElement> chart, Individual worksheet, Worksheet ws) {
        for(int i = 0;i<chart.size(); i++) {
            if(chart.get(i) instanceof Chart) {
                if(((Chart) chart.get(i)).getTitle() == null) {
                    Individual j = this.converter.getChart().createIndividual(this.converter.getChart().getURI() +
                            "_Worksheet" + ws.getSheetName() +
                            "_" + "Chart" + (i+1));
                    j.addProperty(this.converter.getIsPartOfWorksheet(), worksheet);
                    j.addLiteral(RDFS.label, "Chart" + i+1);
                }
                else if(((Chart) chart.get(i)).getTitle().toString().isEmpty()) {
                    Individual j = this.converter.getChart().createIndividual(this.converter.getChart().getURI() +
                            "_Worksheet" + ws.getSheetName() +
                            "_" + "Chart" + (i+1));
                    j.addProperty(this.converter.getIsPartOfWorksheet(), worksheet);
                    j.addLiteral(RDFS.label, "Chart" + i+1);
                }
                else {
                    Individual j = this.converter.getChart().createIndividual(this.converter.getChart().getURI() +
                            "_Worksheet" + ws.getSheetName() +
                            "_" + ((Chart) chart.get(i)).getTitle().toString());
                    j.addLiteral(this.converter.getChartTitle(), ((Chart) chart.get(i)).getTitle().toString());
                    j.addProperty(this.converter.getIsPartOfWorksheet(), worksheet);
                    j.addLiteral(RDFS.label, ((Chart) chart.get(i)).getTitle().toString());
                }
            }
        }
        //System.out.println("converting " + chart.size() + " chart");
    }

    /**
     * add table data into the rdf-graph O(table.size)
     * @param table table data that already been converted from entity.excel
     * @param worksheet for relation isPartOfWorksheet (so data can be tracked from which worksheet)
     * @param ws for naming purposes(not really important can be removed i think)
     */
    private void addTable(List<SheetElement> table, Individual worksheet, Worksheet ws) {
        for(int j = 0;j<table.size();j++){
            if(table.get(j) instanceof Table) {
                if(table.get(j).title() != null) {
                    addTableRelation((Table)table.get(j), ws, table.get(j).title(), worksheet);
                }
                else{
                    addTableRelation((Table)table.get(j), ws, "Table" + (j+1), worksheet);
                }
            }
        }

        //System.out.println("converting " + table.size() + " table");

    }

    private void addTableRelation (Table table, Worksheet ws, String title, Individual worksheet){
        Individual Table = this.converter.getTable().createIndividual(this.converter.getTable().getURI() +
                "_Worksheet" + ws.getSheetName() +
                "_" + title);
        Table.addLiteral(RDFS.label, title);
        Table.addLiteral(this.converter.getElementName(), title);
        Table.addLiteral(this.converter.getColStart(),  table.getColumnStart());
        Table.addLiteral(this.converter.getColEnd(),  table.getColumnEnd());
        Table.addLiteral(this.converter.getRowEnd(),  table.getRowEnd());
        Table.addLiteral(this.converter.getRowStart(), table.getRowStart());
        Table.addProperty(this.converter.getIsPartOfWorksheet(), worksheet);
        for(int i = 0;i<table.getCell().size();i++) {
            Individual cell = this.converter.getModel().getIndividual(this.converter.getCell().getURI() +
                    "_Worksheet" + ws.getSheetName() +
                    "_" + table.getCell().get(i).getCellId());
            Table.addProperty(this.converter.getHasCell(), cell);
            worksheet.removeProperty(this.converter.getHasSheetElement(),cell);
            cell.removeProperty(this.converter.getIsPartOfWorksheet(), worksheet);
        }
    }

    /**
     * add Text - Sheet element instances into rdf-graph O(text.size)
     * @param text text data that already been converted from entity.excel
     * @param worksheet worksheet to give the text class relation isPartOfWorksheet (so data can be tracked from which worksheet)
     * @param ws for naming purposes(not really important can be removed i think)
     */
    private void addText(List<SheetElement> text, Individual worksheet, Worksheet ws) {
        for(int j = 0;j<text.size();j++) {
            if(text.get(j) instanceof Text) {
                Individual i = this.converter.getText().createIndividual(this.converter.getText().getURI() +
                        "_Worksheet" + ws.getSheetName() +
                        "_" + text.get(j).title() + (j+1));
                i.addLiteral(RDFS.label, text.get(j).title() + (j+1));
                i.addLiteral(this.converter.getHasValue(), ((Text) text.get(j)).getValue());
                i.addProperty(this.converter.getIsPartOfWorksheet(), worksheet);
            }
        }
        //System.out.println("converting " + text.size() + " text");
    }

    /**
     * add Illustrations(Pictures,Icons,etc) element into rdf-graph O(illustrations.size)
     * @param illustrations illustrations data that already been converted from entity.excel.
     * @param worksheet to link isPartOfWorksheet with illustrations (so we can track which data is from which worksheet)
     * @param ws for naming purposes(not really important can be removed i think)
     */
    private void addIllustration(List<SheetElement> illustrations, Individual worksheet, Worksheet ws) {
        for(int j = 0;j<illustrations.size();j++) {
            Individual i = this.converter.getIllustration().createIndividual(this.converter.getIllustration() +
                    "_Worksheet" + ws.getSheetName() +
                    "_" + illustrations.get(j).title() + (j+1));
            i.addLiteral(RDFS.label, illustrations.get(j).title() + (j+1));
            i.addProperty(this.converter.getIsPartOfWorksheet(), worksheet);
        }
        //System.out.println("converting " + illustrations.size() +  " illustrations");
    }

}
