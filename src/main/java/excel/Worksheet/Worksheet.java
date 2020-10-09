package excel.Worksheet;

import excel.SheetElement.ElementType;
import excel.SheetElement.SheetElement;
import excel.Workbook.Workbook;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Worksheet {
    private Map<ElementType, List<SheetElement>> sheets = new HashMap<>();
    private String sheetName;
    private Workbook workbook;

    public Worksheet(String sheetName, Workbook workbook) {
        this.sheetName = sheetName;
        this.workbook = workbook;
    }

    public void addElement(ElementType type, List<SheetElement> sheetElement){
        this.sheets.put(type, sheetElement);
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    public void setWorkbook(Workbook workbook) {
        this.workbook = workbook;
    }

    public Map<ElementType, List<SheetElement>> getSheets() {
        return sheets;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
}
