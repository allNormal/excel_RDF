package com.java.fto.entity.EndpointEntity;

import java.util.List;

public class WorkbookExcel extends FileItem {

    private List<String> tables;
    private List<String> charts;
    private List<String> illustrations;

    public WorkbookExcel() {
    }


    public void setTables(List<String> tables) {
        this.tables = tables;
    }

    public void setCharts(List<String> charts) {
        this.charts = charts;
    }

    public void setIllustrations(List<String> illustrations) {
        this.illustrations = illustrations;
    }

    public List<String> getTables() {
        return tables;
    }

    public List<String> getCharts() {
        return charts;
    }

    public List<String> getIllustrations() {
        return illustrations;
    }



    @Override
    public List<String> getColumns() {
        return super.getColumns();
    }

    @Override
    public void setColumns(List<String> columns) {
        super.setColumns(columns);
    }

    @Override
    public List<String> getRows() {
        return super.getRows();
    }

    @Override
    public void setRows(List<String> rows) {
        super.setRows(rows);
    }
}
