package excel.SheetElement.BasicElement;

import excel.SheetElement.SheetElement;
import excel.Worksheet.Worksheet;


public class BasicElement extends SheetElement {

    private String title;

    public BasicElement(Worksheet worksheet, String title){
        super(worksheet);
    }

    public String title() {
        return this.title;
    }

}
