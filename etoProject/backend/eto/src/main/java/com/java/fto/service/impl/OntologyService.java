package com.java.fto.service.impl;

import com.java.fto.endpoint.impl.EtoController;
import com.java.fto.entity.EndpointEntity.WorkbookEndpoint;
import com.java.fto.entity.Operator;
import com.java.fto.entity.Restriction.Restriction;
import com.java.fto.entity.SheetElement.ElementType;;
import com.java.fto.mapper.ExcelMapper.readExcel;
import com.java.fto.persistence.GraphDB.GraphDBDao;
import com.java.fto.persistence.impl.OntologyExcelDao;
import org.eclipse.rdf4j.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger log = LoggerFactory.getLogger(OntologyService.class);


    public OntologyService() {

    }

    @Override
    public void createAuto(String filepath) {
        log.info("request to create automated excel converter into file format received");
        File file = new File(filepath);
        try {
            this.readExcel = new readExcel(file);
            log.info("extracting the excel file and convert it into a Workboook entity");
            this.readExcel.readExcelConverter();
            log.info("converting Workbook entity into a RDF graph");
            dao.createAuto(this.readExcel.getWorkbook());
            this.readExcel = null;
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void initializeWorkbook(String filepath) {
        log.info("request to initialize the workbook received");
        File file = new File(filepath);
        try {
            this.readExcel = new readExcel(file);
            log.info("initializing the workbook entity");
            this.readExcel.readExcelConverter();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void createCustom(Restriction restriction) {
        log.info("request to convert an excel file into a custom RDF graph received");
        log.info("converting it into the RDF graph");
        this.dao.createCustom(this.readExcel.getWorkbook(), restriction);
        this.readExcel = null;
    }

    @Override
    public WorkbookEndpoint getWorkbook() {
        log.info("request to get workbook information received");

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
        log.info("request to get all the repository available in graphDB received");
        log.info("sending all the repository available in graphDB");
        return graphDBDao.getAllRepoName();
    }

    @Override
    public void addGraphIntoRepo(String repoName) {
        log.info("request to add graph into an existing repository received");
        try {
            this.graphDBDao.addGraphIntoRepo(this.dao.getFileName(), repoName);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void createRepoAndAddGraph(String repoName) {
        log.info("request to create a new repository and and graph into it received");
        try {
            this.graphDBDao.createRepoAndAddGraphIntoIt(this.dao.getFileName(), repoName);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
