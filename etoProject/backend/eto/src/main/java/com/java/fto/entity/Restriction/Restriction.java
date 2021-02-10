package com.java.fto.entity.Restriction;

import com.java.fto.exception.RestrictionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Restriction {


    private Map<String, List<Table>> tablesInWorksheet = new HashMap<>();
    private Map<String, Excel> excelRestrictions = new HashMap<>();

    public Restriction() {
    }

    public void addTablesInWorksheet(String worksheetName, List<Table> table) throws RestrictionException{
        if(!tablesInWorksheet.containsKey(worksheetName)) {
            tablesInWorksheet.put(worksheetName, table);
        } else {
            throw new RestrictionException("duplicate worksheetName");
        }
    }

    public void addExcelRestrictions(String worksheetName, Excel excels) throws RestrictionException {
        if(!excelRestrictions.containsKey(worksheetName)){
            excelRestrictions.put(worksheetName, excels);
        } else {
            throw new RestrictionException("duplicate worksheetName");
        }
    }

    public Map<String, List<Table>> getTablesInWorksheet() {
        return tablesInWorksheet;
    }

    public Map<String, Excel> getExcelRestrictions() {
        return excelRestrictions;
    }
}
