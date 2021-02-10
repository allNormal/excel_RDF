package com.java.fto.entity.EndpointEntity.Receiver;

import java.util.List;

public class WorksheetExcelReceiver extends WorksheetReceiver {
    private List<CheckboxItem> columns;
    private List<CheckboxItem> rows;

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
}
