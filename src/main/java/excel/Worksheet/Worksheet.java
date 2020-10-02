package excel.Worksheet;

import excel.SheetElement.SheetElement;

import java.util.ArrayList;
import java.util.List;

public class Worksheet {
    private List<SheetElement> sheets = new ArrayList<>();
    private String sheetName;

    public Worksheet(String sheetName) {
        this.sheetName = sheetName;
    }

    public void addElement(SheetElement sheetElement){
        this.sheets.add(sheetElement);
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
