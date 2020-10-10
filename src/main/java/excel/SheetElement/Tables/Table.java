package excel.SheetElement.Tables;

import excel.SheetElement.SheetElement;
import excel.Worksheet.Worksheet;

import java.util.ArrayList;
import java.util.List;

public class Table extends SheetElement {
    private String rowStart;
    private String rowEnd;
    private String columnStart;
    private String columnEnd;
    private List<String> header = new ArrayList<>();
    private String elementName;

    public Table(Worksheet worksheet, String columnEnd, String columnStart, String rowStart, String rowEnd
            , String elementName) {
        super(worksheet);
        this.columnEnd = columnEnd;
        this.columnStart = columnStart;
        this.rowEnd = rowEnd;
        this.rowStart = rowStart;
        this.elementName = elementName;
    }

    public void addHeader(String header){
        this.header.add(header);
    }

    public String getRowStart() {
        return rowStart;
    }

    public void setRowStart(String rowStart) {
        this.rowStart = rowStart;
    }

    public String getRowEnd() {
        return rowEnd;
    }

    public void setRowEnd(String rowEnd) {
        this.rowEnd = rowEnd;
    }

    public String getColumnStart() {
        return columnStart;
    }

    public void setColumnStart(String columnStart) {
        this.columnStart = columnStart;
    }

    public String getColumnEnd() {
        return columnEnd;
    }

    public void setColumnEnd(String columnEnd) {
        this.columnEnd = columnEnd;
    }

    public List<String> getHeader() {
        return header;
    }

    public String title() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }
}
