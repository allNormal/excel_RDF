import excel.SheetElement.BasicElement.Cell;
import excel.SheetElement.BasicElement.Column;
import excel.SheetElement.BasicElement.Row;
import excel.SheetElement.Charts.Chart;
import excel.SheetElement.Illustrations.Illustrations;
import excel.SheetElement.SheetElement;
import excel.SheetElement.Tables.Table;
import excel.SheetElement.Texts.Text;
import excel.Workbook.Workbook;
import excel.Worksheet.Worksheet;
import org.apache.jena.atlas.io.IO;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OntologyService {
    private OntologyConverter converter;
    private Workbook workbook;
    private readExcel readExcel;
    private OntModel model;

    public OntologyService (String filepath){
        File file = new File(filepath);
        try {
            System.out.println(file.getName());
            this.readExcel = new readExcel(file);
            this.workbook = this.readExcel.getWorkbook();
            this.converter = new OntologyConverter(file.getName());
            this.model = this.converter.getModel();
            convertExcelToOntology();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * add excel data into the rdf-graph
     */
    private void convertExcelToOntology(){
        System.out.println("converting 1 workbook");
        converter.writeDataset();
        Individual workbook = addWorkbook();
        System.out.println("converting " + this.workbook.getWorksheets().size() + " worksheet");
        for(int i = 0;i<this.workbook.getWorksheets().size();i++){
            System.out.println("worksheet " + (i+1));
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
            for(Map.Entry<String, List<SheetElement>> m : this.workbook.getWorksheets().get(i).getSheets().entrySet()) {
                switch (m.getKey()){
                    case "Tables" :
                        table = m.getValue();
                        break;
                    case "Column" :
                        column = m.getValue();
                        break;
                    case "Cell" :
                        cell = m.getValue();
                        break;
                    case "Row" :
                        row = m.getValue();
                        break;
                    case "Charts" :
                        chart = m.getValue();
                        break;
                    case "Illustrations" :
                        illustration = m.getValue();
                        break;
                    case "Texts" :
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
                addChart(chart, worksheet, this.workbook.getWorksheets().get(i));
                addTable(table, worksheet, this.workbook.getWorksheets().get(i));
                addText(text, worksheet, this.workbook.getWorksheets().get(i));
                addIllustration(illustration, worksheet, this.workbook.getWorksheets().get(i));
                addBasicElement(cell, worksheet, this.workbook.getWorksheets().get(i));
            }
            System.out.println("----------------------------------------------------");
        }

        this.converter.closeDataset();
    }

    /**
     * save model into file with turtle format
     * @param filePath file model that want to be converted into file
     */
    public void saveModel(String filePath) {
        this.converter.writeDataset();
        FileOutputStream out = null;
        File myFile = new File(filePath);
        if(!this.converter.getDataset().containsNamedModel(myFile.getName())) {
            System.out.println("no model from " + myFile.getName() + " have been created yet");
            return;
        }
        try {
            out = new FileOutputStream(myFile.getName());
        } catch (IOException e) {
            System.out.println(e);
        }
        //RDFDataMgr.write(out, this.converter.getDataset(), Lang.TRIG);
        this.converter.getDataset().getNamedModel(myFile.getName()).write(out,"TTL");
        //this.converter.getModel().write(out, "TTL");
        this.converter.closeDataset();
    }

    /**
     * add workbook data into the rdf-graph
     * @return workbook instance.
     */
    private Individual addWorkbook() {
        Individual i = this.converter.getWorkbook().createIndividual(this.converter.getWorkbook().getURI() + "_" +
                1);
        i.addLiteral(this.converter.getExtension(), this.workbook.getExtension());
        i.addLiteral(this.converter.getFileName(), this.workbook.getFileName());
        return i;
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
        return i;
    }

    /**
     * Iterate through cell list and call addCell O(cell.size)
     * @param cell list of cell in the worksheet that already been converted from excel
     * @param worksheet for isPartOfWorksheet relation so data can be tracked coming from which worksheet
     * @param ws for naming purposes(not really important can be removed i think)
     */
    private void addBasicElement(List<SheetElement> cell, Individual worksheet, Worksheet ws) {
        for(int i = 0;i<cell.size();i++) {
            if(cell.get(i) instanceof  Cell) {
                addCell((Cell)cell.get(i), worksheet, ws);
            }
        }

        System.out.println("converting " + cell.size() + " cells");
    }

    /**
     * add basicelement(Row, Column, Cell) into the rdf-graph
     * @param cell cell data that already been converted from excel and
     *            since row, column also saved in cell, we only need cell parameter.
     * @param worksheet for the relation isPartOfWorksheet (so data can be tracked coming from which worksheet)
     * @param ws for naming purposes(not really important can be removed i think)
     */
    private void addCell(Cell cell, Individual worksheet, Worksheet ws) {
        Individual i = this.converter.getCell().createIndividual(this.converter.getCell().getURI() +
                "_Worksheet" + ws.getSheetName() +
                "_" + cell.getCellId());
        i.addLiteral(this.converter.getCellID(), cell.getCellId());
        i.addProperty(this.converter.getIsPartOfWorksheet(), worksheet);
        Individual val;
        switch (cell.getValue()){
            case STRING:
                val = this.converter.getStringValue().createIndividual();
                val.addLiteral(this.converter.getCellValue(), cell.getStringValue());
                i.addProperty(this.converter.getHasValue(),val);
                break;
            case NUMERIC:
                val = this.converter.getNumericValue().createIndividual();
                val.addLiteral(this.converter.getCellValue(), cell.getNumericValue());
                i.addProperty(this.converter.getHasValue(),val);
                break;
            case FORMULA:
                val = this.converter.getFormulaValue().createIndividual();
                val.addLiteral(this.converter.getFunctionName(), cell.getFormulaValue().getFormulaFunction());
                i.addProperty(this.converter.getHasValue(),val);
                break;
            case BOOLEAN:
                val = this.converter.getBooleanValue().createIndividual();
                val.addLiteral(this.converter.getCellValue(), cell.isBooleanValue());
                i.addProperty(this.converter.getHasValue(),val);
                break;
            case ERROR:
                val = this.converter.getErrorValue().createIndividual();
                val.addLiteral(this.converter.getErrorName(), cell.getError().getErrorName());
                i.addProperty(this.converter.getHasValue(),val);
                break;
        }

        if(cell.getComment() != null) {
            Individual comment = this.converter.getComment().createIndividual();
            comment.addLiteral(this.converter.getCommentsValue(), cell.getComment().getString().toString());
            i.addProperty(this.converter.getHasComment(), comment);
        }

        Individual row = this.converter.getRow().createIndividual( this.converter.getRow().getURI() +
                "_Worksheet" + ws.getSheetName() +
                "_" + Integer.toString(cell.getRow()));
        row.addLiteral(this.converter.getRowID(), cell.getRow());
        row.addProperty(this.converter.getHasCell(), i);
        row.addProperty(this.converter.getIsPartOfWorksheet(), worksheet);
        i.addProperty(this.converter.getHasRow(),row);

        Individual column = this.converter.getColumn().createIndividual(this.converter.getColumn().getURI() +
                "_Worksheet" + ws.getSheetName() +
                "_" + cell.getColumn());
        column.addLiteral(this.converter.getColumnID(),cell.getColumn());
        column.addProperty(this.converter.getHasCell(),i);
        column.addProperty(this.converter.getHasRow(), row);
        column.addProperty(this.converter.getIsPartOfWorksheet(), worksheet);
        i.addProperty(this.converter.getHasColumn(),column);
        row.addProperty(this.converter.getHasColumn(), column);
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
                if(((Chart) chart.get(i)).getTitle() != null) {
                    Individual j = this.converter.getChart().createIndividual(this.converter.getChart().getURI() +
                            "_Worksheet" + ws.getSheetName() +
                            "_" + ((Chart) chart.get(i)).getTitle().toString());
                    j.addLiteral(this.converter.getChartTitle(), ((Chart) chart.get(i)).getTitle().toString());
                    j.addProperty(this.converter.getIsPartOfWorksheet(), worksheet);
                }
                else {
                    Individual j = this.converter.getChart().createIndividual(this.converter.getChart().getURI() +
                            "_Worksheet" + ws.getSheetName() +
                            "_" + "Chart" + i+1);
                    j.addProperty(this.converter.getIsPartOfWorksheet(), worksheet);
                }
            }
        }
        System.out.println("converting " + chart.size() + " chart");
    }

    /**
     * add table data into the rdf-graph O(table.size)
     * @param table table data that already been converted from excel
     * @param worksheet for relation isPartOfWorksheet (so data can be tracked from which worksheet)
     * @param ws for naming purposes(not really important can be removed i think)
     */
    private void addTable(List<SheetElement> table, Individual worksheet, Worksheet ws) {
        for(int j = 0;j<table.size();j++){
            if(table.get(j) instanceof Table) {
                Individual i = this.converter.getTable().createIndividual(this.converter.getTable().getURI() +
                        "_Worksheet" + ws.getSheetName() +
                        "_"+((Table) table.get(j)).getElementName());
                i.addLiteral(this.converter.getElementName(), ((Table) table.get(j)).getElementName());
                i.addLiteral(this.converter.getColStart(), ((Table) table.get(j)).getColumnStart());
                i.addLiteral(this.converter.getColEnd(), ((Table) table.get(j)).getColumnEnd());
                i.addLiteral(this.converter.getRowEnd(), ((Table) table.get(j)).getRowEnd());
                i.addLiteral(this.converter.getRowStart(), ((Table) table.get(j)).getRowStart());
                i.addProperty(this.converter.getIsPartOfWorksheet(), worksheet);
            }
        }

        System.out.println("converting " + table.size() + " table");

    }

    /**
     * add Text - Sheet element instances into rdf-graph O(text.size)
     * @param text text data that already been converted from excel
     * @param worksheet worksheet to give the text class relation isPartOfWorksheet (so data can be tracked from which worksheet)
     * @param ws for naming purposes(not really important can be removed i think)
     */
    private void addText(List<SheetElement> text, Individual worksheet, Worksheet ws) {
        for(int j = 0;j<text.size();j++) {
            if(text.get(j) instanceof Text) {
                Individual i = this.converter.getText().createIndividual(this.converter.getText().getURI() +
                        "_Worksheet" + ws.getSheetName() +
                        "_Text" + (j+1));
                i.addLiteral(this.converter.getHasValue(), ((Text) text.get(j)).getValue());
                i.addProperty(this.converter.getIsPartOfWorksheet(), worksheet);
            }
        }
        System.out.println("converting " + text.size() + " text");
    }

    /**
     * add Illustrations(Pictures,Icons,etc) element into rdf-graph O(illustrations.size)
     * @param illustrations illustrations data that already been converted from excel.
     * @param worksheet to link isPartOfWorksheet with illustrations (so we can track which data is from which worksheet)
     * @param ws for naming purposes(not really important can be removed i think)
     */
    private void addIllustration(List<SheetElement> illustrations, Individual worksheet, Worksheet ws) {
        for(int j = 0;j<illustrations.size();j++) {
            Individual i = this.converter.getIllustration().createIndividual(this.converter.getIllustration() +
                    "_Worksheet" + ws.getSheetName() +
                    "_Illustration" + (j+1));
            i.addProperty(this.converter.getIsPartOfWorksheet(), worksheet);
        }
        System.out.println("converting " + illustrations.size() +  " illustrations");
    }
}
