package com.java.fto.entity.SheetElement.BasicElement;

import com.java.fto.entity.SheetElement.SheetElement;
import com.java.fto.entity.Worksheet.Worksheet;


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
