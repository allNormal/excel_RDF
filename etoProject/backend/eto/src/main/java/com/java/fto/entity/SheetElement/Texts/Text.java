package com.java.fto.entity.SheetElement.Texts;

import com.java.fto.entity.SheetElement.SheetElement;
import com.java.fto.entity.Worksheet.Worksheet;

public class Text extends SheetElement {
    private String name;
    private String value;

    public Text(Worksheet worksheet, String name, String value) {
        super(worksheet);
        this.name = name;
        this.value = value;
    }

    public String id() {
        return name;
    }

    public String getValue() {
        return value;
    }

}
