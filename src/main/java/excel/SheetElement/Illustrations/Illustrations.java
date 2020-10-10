package excel.SheetElement.Illustrations;

import excel.SheetElement.SheetElement;
import excel.Worksheet.Worksheet;

public class Illustrations extends SheetElement {

    private String title;

    public Illustrations(Worksheet worksheet){
        super(worksheet);
    }

    public String title() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
