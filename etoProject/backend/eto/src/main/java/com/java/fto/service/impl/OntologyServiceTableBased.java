package com.java.fto.service.impl;

import com.java.fto.entity.EndpointEntity.Receiver.WorksheetReceiver;
import com.java.fto.entity.EndpointEntity.WorkbookEndpoint;
import com.java.fto.entity.Operator;
import com.java.fto.entity.Restriction.Restriction;
import com.java.fto.entity.SheetElement.ElementType;
import com.java.fto.mapper.ExcelMapper.readExcelTableBased;
import com.java.fto.persistence.impl.OntologyTableDao;
import com.java.fto.service.OntologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Service
public class OntologyServiceTableBased implements OntologyService {

    @Autowired
    private OntologyTableDao ontologyTableDao;
    private com.java.fto.mapper.ExcelMapper.readExcelTableBased readExcelTableBased;

    public OntologyServiceTableBased() {
    }

    @Override
    public void createAuto(String filepath) {
        try {
            File file = new File(filepath);
            this.readExcelTableBased = new readExcelTableBased(file);
            this.readExcelTableBased.initializeWorkbook();
            this.readExcelTableBased.readExcelConverter();
            this.ontologyTableDao.createAuto(this.readExcelTableBased.getWorkbook());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void initializeWorkbook(String filepath) {
        File file = new File(filepath);
        try {
            this.readExcelTableBased = new readExcelTableBased(file);
            this.readExcelTableBased.initializeWorkbook();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void initializeColumnAndRow(List<WorksheetReceiver> ws){
        try {
            this.readExcelTableBased.initializeColumnAndRow(ws);
            this.readExcelTableBased.readExcelConverter();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void createCustom(Restriction restriction) {
        this.ontologyTableDao.createCustom(this.readExcelTableBased.getWorkbook(), restriction);
        readExcelTableBased = null;
    }

    @Override
    public WorkbookEndpoint getWorkbook() {
        return this.readExcelTableBased.getWorkbookEndpoint();
    }

    @Override
    public Collection<String> getDependency(String cellID, String worksheetName) {
        return null;
    }

    @Override
    public Collection<String> addConstraint(ElementType type, String typeID, String worksheetName, Operator operator, String value) {
        return null;
    }

    @Override
    public Collection<String> getReverseDependency(String cellID, String worksheetName) {
        return null;
    }
}
