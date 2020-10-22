package entity.SheetElement.Illustrations;

import entity.SheetElement.SheetElement;
import entity.Worksheet.Worksheet;

public class Illustrations extends SheetElement {

    private String title;

    public Illustrations(Worksheet worksheet, String title){
        super(worksheet);
        this.title = title;
    }

    public String title() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
