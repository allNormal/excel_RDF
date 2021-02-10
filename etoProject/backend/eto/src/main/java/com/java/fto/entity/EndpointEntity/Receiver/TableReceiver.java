package com.java.fto.entity.EndpointEntity.Receiver;

import java.util.ArrayList;
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

    public List<String> getColumnsValues() {
        List<String> columns =  new ArrayList<>();
        for(int i = 0; i<this.columns.size(); i++){
            System.out.println(this.columns.get(i).get_value());
            columns.add(this.columns.get(i).get_value());
        }
        return columns;
    }

    public List<String> getRowsValues() {
        List<String> rows = new ArrayList<>();
        for(int i = 0;i<this.rows.size(); i++) {
            System.out.println(this.rows.get(i).get_value());
            rows.add(this.rows.get(i).get_value());
        }
        return rows;
    }
}
