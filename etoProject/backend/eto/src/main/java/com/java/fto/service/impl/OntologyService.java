package com.java.fto.service.impl;

import com.java.fto.entity.EndpointEntity.WorkbookEndpoint;
import com.java.fto.entity.Operator;
import com.java.fto.entity.Restriction.Restriction;
import com.java.fto.entity.SheetElement.ElementType;;
import com.java.fto.mapper.ExcelMapper.readExcel;
import com.java.fto.persistence.GraphDB.GraphDBDao;
import com.java.fto.persistence.impl.OntologyExcelDao;
import org.eclipse.rdf4j.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Service
public class OntologyService implements com.java.fto.service.OntologyService {

    @Autowired
    private OntologyExcelDao dao;
    @Autowired
    private GraphDBDao graphDBDao;
    private com.java.fto.mapper.ExcelMapper.readExcel readExcel;

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
            this.readExcel = null;
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
        this.readExcel = null;
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

    @Override
    public List<String> getAllRepository() {
        return graphDBDao.getAllRepoName();
    }

    @Override
    public void addGraphIntoRepo(String repoName) {
        try {
            this.graphDBDao.addGraphIntoRepo(this.dao.getFileName(), repoName);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void createRepoAndAddGraph(String repoName) {
        try {
            this.graphDBDao.createRepoAndAddGraphIntoIt(this.dao.getFileName(), repoName);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
