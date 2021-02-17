package com.java.fto.persistence;

import com.java.fto.entity.Operator;
import com.java.fto.entity.Restriction.Restriction;
import com.java.fto.entity.SheetElement.ElementType;
import com.java.fto.entity.Workbook.Workbook;
import com.java.fto.exception.IncorrectTypeException;

import java.util.Collection;

public interface OntologyDao {

    void createAuto(Workbook workbook) throws IncorrectTypeException;

    void createCustom(Workbook workbook, Restriction restriction) throws IncorrectTypeException;


    Collection<String> getCellDependencies(String cellID, String worksheetName);

    Collection<String> addConstraint(ElementType type, String typeID, String worksheetName,
                                     Operator operator, String value);

    Collection<String> getReverseDependencies(String cellID, String worksheetName);

    String getFileName();
}
