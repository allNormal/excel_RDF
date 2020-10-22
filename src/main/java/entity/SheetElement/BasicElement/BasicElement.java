package entity.SheetElement.BasicElement;

import entity.SheetElement.SheetElement;
import entity.Worksheet.Worksheet;


public class BasicElement extends SheetElement {

    private String title;

    public BasicElement(Worksheet worksheet, String title){
        super(worksheet);
    }

    public String title() {
        return this.title;
    }

}
