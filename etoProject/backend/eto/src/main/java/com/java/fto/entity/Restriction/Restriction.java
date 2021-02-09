package com.java.fto.entity.Restriction;

import com.java.fto.exception.RestrictionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Restriction {

    List<String> worksheets = new ArrayList<>();
    Map<String, List<String>> columnsInWorksheet = new HashMap<>();

    public Restriction() {
    }

    public void addWorksheet(String name) {
        worksheets.add(name);
    }

    public void addColumnsInWorksheet(String worksheetName, List<String> columnList) throws RestrictionException {
        if(columnsInWorksheet.get(worksheetName) == null) columnsInWorksheet.put(worksheetName, columnList);
        else throw new RestrictionException("you can only have 1 list of column per worksheet");
    }

    public List<String> getWorksheets() {
        return worksheets;
    }

    public void setWorksheets(List<String> worksheets) {
        this.worksheets = worksheets;
    }

    public Map<String, List<String>> getColumnsInWorksheet() {
        return columnsInWorksheet;
    }

    public void setColumnsInWorksheet(Map<String, List<String>> columnsInWorksheet) {
        this.columnsInWorksheet = columnsInWorksheet;
    }
}
