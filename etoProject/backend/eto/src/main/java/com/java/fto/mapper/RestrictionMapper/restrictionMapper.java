package com.java.fto.mapper.RestrictionMapper;

import com.java.fto.entity.EndpointEntity.Receiver.TableReceiver;
import com.java.fto.entity.EndpointEntity.Receiver.WorksheetReceiver;
import com.java.fto.entity.Restriction.Excel;
import com.java.fto.entity.Restriction.Restriction;
import com.java.fto.entity.Restriction.Table;
import com.java.fto.exception.RestrictionException;

import java.util.ArrayList;
import java.util.List;

public class restrictionMapper implements restrictionMap {

    public restrictionMapper() {
    }

    @Override
    public Restriction payloadToRestriction(List<WorksheetReceiver> ws, String type) {

        if(type.equals("table")) {
            Restriction restriction = new Restriction();
            for (int i = 0; i < ws.size(); i++) {
                List<TableReceiver> tables = ws.get(i).getTable();
                List<Table> tables1 = new ArrayList<>();
                for(int j = 0 ; j<tables.size(); j++) {
                    Table table = new Table();
                    table.setName(tables.get(j).getName());
                    table.setColumnHeader(tables.get(j).getColumnsValues());
                    table.setRowHeader(tables.get(j).getRowsValues());
                    tables1.add(table);
                }
                try {
                    restriction.addTablesInWorksheet(ws.get(i).getWorksheetName(), tables1);
                } catch (RestrictionException e) {
                    System.out.println(e.getMessage());
                }
            }
            return restriction;
        } else {
            Restriction restriction = new Restriction();

            for(int i = 0; i< ws.size(); i++) {
                WorksheetReceiver ws1 = ws.get(i);
                Excel excel = new Excel();
                excel.setRows(ws1.getRowsValues());
                excel.setColumns(ws1.getColumnsValues());
                try {
                    restriction.addExcelRestrictions(ws1.getWorksheetName(), excel);
                } catch (RestrictionException e) {
                    System.out.println(e.getMessage());
                }
            }
            return restriction;
        }
    }

}
