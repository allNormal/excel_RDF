package entity.SheetElement.Texts;

import entity.SheetElement.SheetElement;
import entity.Worksheet.Worksheet;

public class Text extends SheetElement {
    private String name;
    private String value;

    public Text(Worksheet worksheet, String name, String value) {
        super(worksheet);
        this.name = name;
        this.value = value;
    }

    public String title() {
        return name;
    }

    public String getValue() {
        return value;
    }

}
