package com.java.fto.entity.SheetElement;

import com.java.fto.entity.Worksheet.Worksheet;

public abstract class SheetElement {
    private Worksheet worksheet;

    public SheetElement(Worksheet worksheet){
        this.worksheet = worksheet;
    }

    public SheetElement(){}

    public Worksheet getWorksheet() {
        return worksheet;
    }

    public abstract String id();
}
