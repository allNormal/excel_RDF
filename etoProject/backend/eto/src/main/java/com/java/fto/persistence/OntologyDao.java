package com.java.fto.persistence;

import com.java.fto.entity.Operator;
import com.java.fto.entity.Restriction.Restriction;
import com.java.fto.entity.SheetElement.ElementType;
import com.java.fto.entity.Workbook.Workbook;

import java.util.Collection;

public interface OntologyDao {

    void createAuto(Workbook workbook);

    void createCustom(Workbook workbook, Restriction restriction);


    Collection<String> getCellDependencies(String cellID, String worksheetName);

    Collection<String> addConstraint(ElementType type, String typeID, String worksheetName,
                                     Operator operator, String value);

    Collection<String> getReverseDependencies(String cellID, String worksheetName);
}
