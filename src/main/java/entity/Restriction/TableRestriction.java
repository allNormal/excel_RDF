package entity.Restriction;

import exception.RestrictionException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableRestriction extends Restriction{


    Map<String, Map<String, List<String>>> tablesInWorksheet = new HashMap<>();

    public TableRestriction() {
    }


    public void addTablesInWorksheet(String worksheetName, String tableName, List<String> columnList) throws RestrictionException {
        if(tablesInWorksheet.get(worksheetName) == null) {
            Map<String, List<String>> temp = new HashMap<>();
            temp.put(tableName, columnList);
            tablesInWorksheet.put(worksheetName, temp);
        } else {
            Map<String, List<String>> temp = tablesInWorksheet.get(worksheetName);
            if(temp.get(tableName) == null) temp.put(tableName, columnList);
            else {
                throw new RestrictionException("duplicate table name detected");
            }
        }
    }

    public Map<String, Map<String, List<String>>> getTablesInWorksheet() {
        return tablesInWorksheet;
    }
}
