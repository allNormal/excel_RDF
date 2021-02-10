package com.java.fto.entity.EndpointEntity.Receiver;

import java.util.List;

public class TableReceiver {
    private List<CheckboxItem> columns;
    private List<CheckboxItem> rows;
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
