package excel.SheetElement;

import excel.Worksheet.Worksheet;

public class SheetElement {
    private Worksheet worksheet;

    public SheetElement(Worksheet worksheet){
        this.worksheet = worksheet;
    }

    public SheetElement(){}

    public Worksheet getWorksheet() {
        return worksheet;
    }
}
