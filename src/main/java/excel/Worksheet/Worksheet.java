package excel.Worksheet;

import excel.SheetElement.SheetElement;
import excel.Workbook.Workbook;

import java.util.ArrayList;
import java.util.List;

public class Worksheet {
    private List<SheetElement> sheets = new ArrayList<>();
    private String sheetName;
    private Workbook workbook;

    public Worksheet(String sheetName, Workbook workbook) {
        this.sheetName = sheetName;
        this.workbook = workbook;
    }

    public void addElement(SheetElement sheetElement){
        this.sheets.add(sheetElement);
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    public void setWorkbook(Workbook workbook) {
        this.workbook = workbook;
    }

    public List<SheetElement> getSheets() {
        return sheets;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
}
