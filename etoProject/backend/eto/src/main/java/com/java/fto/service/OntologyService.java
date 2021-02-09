package com.java.fto.service;

import com.java.fto.entity.EndpointEntity.WorkbookEndpoint;
import com.java.fto.entity.EndpointEntity.WorkbookTable;
import com.java.fto.entity.Restriction.Restriction;
import com.java.fto.entity.SheetElement.ElementType;
import com.java.fto.entity.Operator;
import com.java.fto.entity.Workbook.Workbook;

import java.util.Collection;

public interface OntologyService {

    void createAuto(String filepath);

    void initializeWorkbook(String filepath);

    void createCustom(Restriction restriction);

    WorkbookEndpoint getWorkbook();

    Collection<String> getDependency(String cellID, String worksheetName);

    Collection<String> addConstraint(ElementType type, String typeID, String worksheetName,
                                                              Operator operator, String value);

    Collection<String> getReverseDependency(String cellID, String worksheetName);

}
