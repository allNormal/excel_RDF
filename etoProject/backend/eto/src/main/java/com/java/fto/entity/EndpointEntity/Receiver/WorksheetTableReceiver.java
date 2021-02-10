package com.java.fto.entity.EndpointEntity.Receiver;

import java.util.List;

public class WorksheetTableReceiver extends WorksheetReceiver {
    private String columnHeader;
    private String rowHeader;
    private List<TableReceiver> table;

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
}
