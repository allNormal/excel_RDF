package com.java.fto.service.impl;

import com.java.fto.entity.EndpointEntity.WorkbookEndpoint;
import com.java.fto.entity.EndpointEntity.WorkbookExcel;
import com.java.fto.entity.EndpointEntity.WorkbookTable;
import com.java.fto.entity.Operator;
import com.java.fto.entity.Restriction.Restriction;
import com.java.fto.entity.SheetElement.ElementType;;
import com.java.fto.entity.Workbook.Workbook;
import com.java.fto.mapper.readExcel;
import com.java.fto.persistence.impl.OntologyExcelDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

@Service
public class OntologyService implements com.java.fto.service.OntologyService {

    @Autowired
    private OntologyExcelDao dao;
    private com.java.fto.mapper.readExcel readExcel;

    public OntologyService() {

    }

    @Override
    public void createAuto(String filepath) {
        File file = new File(filepath);
        try {
            this.readExcel = new readExcel(file);
            this.readExcel.readExcelConverter();
            dao.createAuto(this.readExcel.getWorkbook());
            System.out.println("done");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void initializeWorkbook(String filepath) {
        File file = new File(filepath);
        try {
            this.readExcel = new readExcel(file);
            this.readExcel.readExcelConverter();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void createCustom(Restriction restriction) {

        this.dao.createCustom(this.readExcel.getWorkbook(), restriction);
    }

    @Override
    public WorkbookEndpoint getWorkbook() {
        return this.readExcel.getWorkbookEndpoint();
    }

    @Override
    public Collection<String> getDependency(String cellID, String worksheetName) {
        return dao.getCellDependencies(cellID, worksheetName);
    }

    @Override
    public Collection<String> addConstraint(ElementType type, String typeID, String worksheetName, Operator operator, String value) {

        return dao.addConstraint(type, typeID, worksheetName, operator, value);
    }

    @Override
    public Collection<String> getReverseDependency(String cellID, String worksheetName) {
        return dao.getReverseDependencies(cellID, worksheetName);
    }

}
