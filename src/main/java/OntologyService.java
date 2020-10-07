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
    private OntModel modeldirectInf;

    public OntologyService (String filepath){
        this.readExcel = new readExcel(filepath);
        this.workbook = readExcel.getWorkbook();
        this.converter = new OntologyConverter();
        this.model = converter.getModel();
        this.modeldirectInf = converter.getDirectInferredModel();
        convertExcelToOntology();
        //saveToFile();
    }


    public void convertExcelToOntology(){
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

        FileOutputStream out = null;
        try {
            out = new FileOutputStream("excel_xml.owl");
        } catch (IOException e) {
            System.out.println(e);
        }
        RDFDataMgr.write(out, this.converter.getDataset(), Lang.TRIG);
        this.converter.closeDataset();
    }

    private Individual addWorkbook() {
        Individual i = this.converter.getWorkbook().createIndividual(this.workbook.getFileName());
        i.addLiteral(this.converter.getExtension(), this.workbook.getExtension());
        i.addLiteral(this.converter.getFileName(), this.workbook.getFileName());
        return i;
    }

    private Individual addWorksheet(Worksheet worksheet){
        Individual i = this.converter.getWorksheet().createIndividual(worksheet.getSheetName());
        i.addLiteral(this.converter.getSheetName(), worksheet.getSheetName());
        return i;
    }

    private void addBasicElement(List<SheetElement> cell, Individual worksheet, Worksheet ws) {
        for(int i = 0;i<cell.size();i++) {
            if(cell.get(i) instanceof  Cell) {
                addCell((Cell)cell.get(i), worksheet, ws);
            }
        }

        System.out.println("converting " + cell.size() + " cells");
    }

    private void addCell(Cell cell, Individual worksheet, Worksheet ws) {
        Individual i = this.converter.getCell().createIndividual("Worksheet " + ws.getSheetName()+
                " - " + cell.getCellId());
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

        Individual row = this.converter.getRow().createIndividual("Worksheet " + ws.getSheetName() +
                " - " + Integer.toString(cell.getRow()));
        row.addLiteral(this.converter.getRowID(), cell.getRow());
        row.addProperty(this.converter.getHasCell(), i);
        row.addProperty(this.converter.getIsPartOfWorksheet(), worksheet);
        i.addProperty(this.converter.getHasRow(),row);

        Individual column = this.converter.getColumn().createIndividual("Worksheet " + ws.getSheetName() +
                " - " + cell.getColumn());
        column.addLiteral(this.converter.getColumnID(),cell.getColumn());
        column.addProperty(this.converter.getHasCell(),i);
        column.addProperty(this.converter.getHasRow(), row);
        column.addProperty(this.converter.getIsPartOfWorksheet(), worksheet);
        i.addProperty(this.converter.getHasColumn(),column);
        row.addProperty(this.converter.getHasColumn(), column);
    }

    private void addChart(List<SheetElement> chart, Individual worksheet, Worksheet ws) {
        for(int i = 0;i<chart.size(); i++) {
            if(chart.get(i) instanceof Chart) {
                Individual j = this.converter.getChart().createIndividual("Worksheet " + ws.getSheetName() +
                        " - " + ((Chart) chart.get(i)).getTitle().toString());
                if(((Chart) chart.get(i)).getTitle() != null) {
                    j.addLiteral(this.converter.getChartTitle(), ((Chart) chart.get(i)).getTitle().toString());
                    j.addProperty(this.converter.getIsPartOfWorksheet(), worksheet);
                }
            }
        }
        System.out.println("converting " + chart.size() + " chart");
    }

    private void addTable(List<SheetElement> table, Individual worksheet, Worksheet ws) {
        for(int j = 0;j<table.size();j++){
            if(table.get(j) instanceof Table) {
                Individual i = this.converter.getTable().createIndividual("Worksheet " + ws.getSheetName() +
                        " - "+((Table) table.get(j)).getElementName());
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

    private void addText(List<SheetElement> text, Individual worksheet, Worksheet ws) {
        for(int j = 0;j<text.size();j++) {
            if(text.get(j) instanceof Text) {
                Individual i = this.converter.getText().createIndividual("Worksheet " + ws.getSheetName() +
                        " - Text " + j);
                i.addLiteral(this.converter.getHasValue(), ((Text) text.get(j)).getValue());
                i.addProperty(this.converter.getIsPartOfWorksheet(), worksheet);
            }
        }
        System.out.println("converting " + text.size() + " text");
    }

    private void addIllustration(List<SheetElement> illustrations, Individual worksheet, Worksheet ws) {
        for(int j = 0;j<illustrations.size();j++) {
            Individual i = this.converter.getIllustration().createIndividual("Worksheet " + ws.getSheetName() +
                    " - Illustration " + j);
            i.addProperty(this.converter.getIsPartOfWorksheet(), worksheet);
        }
        System.out.println("converting " + illustrations.size() +  " illustrations");
    }
}
