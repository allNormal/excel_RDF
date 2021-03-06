package com.java.fto.entity.SheetElement.BasicElement;

import com.java.fto.entity.Constraints.Constraint;
import com.java.fto.entity.Worksheet.Worksheet;

import java.util.ArrayList;
import java.util.List;

public class Row extends BasicElement{
    private String rowId;
    private String rowTitle;
    private String columnTitle;
    private List<Cell> cell = new ArrayList<>();
    private Constraint constraint;

    public Row(Worksheet worksheet, String rowId) {
        super(worksheet, rowId);
        this.rowId = rowId + 1;
    }

    public void addCell(Cell cell){
        this.cell.add(cell);
    }

    public String getRowId() {
        return rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    public List<Cell> getCell() {
        return cell;
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public void setConstraint(Constraint constraint) {
        this.constraint = constraint;
    }

    public String getRowTitle() {
        return rowTitle;
    }


    public void setRowTitle(String rowTitle) {
        this.rowTitle = rowTitle;
    }

    public String getColumnTitle() {
        return columnTitle;
    }

    public void setColumnTitle(String columnTitle) {
        this.columnTitle = columnTitle;
    }
}
