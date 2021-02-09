package com.java.fto.entity.EndpointEntity;

public class WorksheetEndpoint {
    private String worksheetName;
    private String columnHeader;
    private String rowHeader;
    private boolean active;

    public String getWorksheetName() {
        return worksheetName;
    }

    public void setWorksheetName(String worksheetName) {
        this.worksheetName = worksheetName;
    }

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
