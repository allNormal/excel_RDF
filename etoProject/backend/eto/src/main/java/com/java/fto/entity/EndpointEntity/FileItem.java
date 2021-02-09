package com.java.fto.entity.EndpointEntity;

import java.util.List;

public class FileItem {

    private List<String> columns;
    private List<String> rows;
    private String worksheetName;

    public FileItem() {
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<String> getRows() {
        return rows;
    }

    public void setRows(List<String> rows) {
        this.rows = rows;
    }

    public String getWorksheetName() {
        return worksheetName;
    }

    public void setWorksheetName(String worksheetName) {
        this.worksheetName = worksheetName;
    }
}
