package com.java.fto.entity.SheetElement.Charts;

import com.java.fto.entity.SheetElement.SheetElement;
import com.java.fto.entity.Worksheet.Worksheet;
import org.apache.poi.xddf.usermodel.chart.XDDFLegendEntry;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import java.util.ArrayList;
import java.util.List;

public class Chart extends SheetElement {
    private List<XDDFLegendEntry> legend = new ArrayList<>();
    private XSSFRichTextString title;

    public Chart(Worksheet worksheet, List<XDDFLegendEntry> legend, XSSFRichTextString title) {
        super(worksheet);
        this.legend = legend;
        this.title = title;
    }

    public List<XDDFLegendEntry> getLegend() {
        return legend;
    }


    public String id(){
        return this.title.getString();
    }

    public XSSFRichTextString getTitle() {
        return title;
    }
}
