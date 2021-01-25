package entity.SheetElement.BasicElement;

import entity.SheetElement.SheetElement;
import entity.Worksheet.Worksheet;


public class BasicElement extends SheetElement {

    private String id;

    public BasicElement(Worksheet worksheet, String id){
        super(worksheet);
        this.id = id;
    }

    public String id() {
        return this.id;
    }

}
