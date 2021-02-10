package com.java.fto.entity.Restriction;

import java.util.List;

public class Table {
    private String name;
    private List<String> columnHeader;
    private List<String> rowHeader;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getColumnHeader() {
        return columnHeader;
    }

    public void setColumnHeader(List<String> columnHeader) {
        this.columnHeader = columnHeader;
    }

    public List<String> getRowHeader() {
        return rowHeader;
    }

    public void setRowHeader(List<String> rowHeader) {
        this.rowHeader = rowHeader;
    }
}
