package com.java.fto.entity.EndpointEntity.Receiver;

import java.util.ArrayList;
import java.util.List;

public class WorksheetReceiver {
    private String worksheetName;
    private boolean active;
    private String columnHeader;
    private String rowHeader;
    private List<TableReceiver> table;
    private List<CheckboxItem> columns;
    private List<CheckboxItem> rows;
    private String columnsRowsFrom;

    public String getColumnHeader() {
        return columnHeader;
    }

    public void setColumnHeader(String columnHeader) {
        this.columnHeader = columnHeader;
    }

    public String getRowHeader() {
        return rowHeader;
    }

    public void setRowHeader(String rowHeader) {
        this.rowHeader = rowHeader;
    }

    public List<TableReceiver> getTable() {
        return table;
    }

    public void setTable(List<TableReceiver> table) {
        this.table = table;
    }

    public List<CheckboxItem> getColumns() {
        return columns;
    }

    public void setColumns(List<CheckboxItem> columns) {
        this.columns = columns;
    }

    public List<CheckboxItem> getRows() {
        return rows;
    }

    public void setRows(List<CheckboxItem> rows) {
        this.rows = rows;
    }

    public String getWorksheetName() {
        return worksheetName;
    }

    public void setWorksheetName(String worksheetName) {
        this.worksheetName = worksheetName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    public List<String> getColumnsValues() {
        List<String> columns =  new ArrayList<>();
        for(int i = 0; i<this.columns.size(); i++){
            columns.add(this.columns.get(i).get_value());
        }
        return columns;
    }

    public List<String> getRowsValues() {
        List<String> rows = new ArrayList<>();
        for(int i = 0;i<this.rows.size(); i++) {
            rows.add(this.rows.get(i).get_value());
        }
        return rows;
    }

    public String getColumnsRowsFrom() {
        return columnsRowsFrom;
    }

    public void setColumnsRowsFrom(String columnsRowsFrom) {
        this.columnsRowsFrom = columnsRowsFrom;
    }
}
