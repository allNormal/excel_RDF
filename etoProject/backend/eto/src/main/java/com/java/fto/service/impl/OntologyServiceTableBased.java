package com.java.fto.service.impl;

import com.java.fto.entity.EndpointEntity.Receiver.WorksheetReceiver;
import com.java.fto.entity.EndpointEntity.WorkbookEndpoint;
import com.java.fto.entity.Operator;
import com.java.fto.entity.Restriction.Restriction;
import com.java.fto.entity.SheetElement.ElementType;
import com.java.fto.exception.IncorrectTypeException;
import com.java.fto.mapper.ExcelMapper.readExcelTableBased;
import com.java.fto.persistence.GraphDB.GraphDBDao;
import com.java.fto.persistence.impl.OntologyTableDao;
import com.java.fto.service.OntologyService;
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
public class OntologyServiceTableBased implements OntologyService {

    @Autowired
    private OntologyTableDao ontologyTableDao;
    @Autowired
    private GraphDBDao graphDBDao;
    private com.java.fto.mapper.ExcelMapper.readExcelTableBased readExcelTableBased;
    private final Logger log = LoggerFactory.getLogger(OntologyServiceTableBased.class);

    public OntologyServiceTableBased() {
    }

    @Override
    public void createAuto(String filepath) {
        try {
            log.info("request to create automated excel converter into table format received");
            File file = new File(filepath);
            this.readExcelTableBased = new readExcelTableBased(file);
            log.info("extracting the excel file and convert it into a Workboook entity");
            this.readExcelTableBased.initializeWorkbook();
            this.readExcelTableBased.readExcelConverter();
            log.info("converting Workbook entity into a RDF graph");
            this.ontologyTableDao.createAuto(this.readExcelTableBased.getWorkbook());
        } catch (IOException | IncorrectTypeException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void initializeWorkbook(String filepath) {
        log.info("request to initialize the workbook received");
        File file = new File(filepath);
        try {
            this.readExcelTableBased = new readExcelTableBased(file);
            log.info("initializing the workbook entity");
            this.readExcelTableBased.initializeWorkbook();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void initializeColumnAndRow(List<WorksheetReceiver> ws){
        log.info("request to initialize the column and row header received");
        try {
            log.info("initializing the column and row header");
            this.readExcelTableBased.initializeColumnAndRow(ws);
            log.info("convert the excel file into a Workbook entity");
            this.readExcelTableBased.readExcelConverter();
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    @Override
    public void createCustom(Restriction restriction) {
        log.info("request to convert an excel file into a custom RDF graph received");
        log.info("converting it into the RDF graph");
        try {
            this.ontologyTableDao.createCustom(this.readExcelTableBased.getWorkbook(), restriction);
        } catch (IncorrectTypeException e) {
            log.error(e.getMessage());
        }
        readExcelTableBased = null;
    }

    @Override
    public WorkbookEndpoint getWorkbook() {
        log.info("request to get workbook information received");
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
            this.graphDBDao.addGraphIntoRepo(this.ontologyTableDao.getFileName(), repoName);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void createRepoAndAddGraph(String repoName) {
        log.info("request to create a new repository and and graph into it received");
        try {
            this.graphDBDao.createRepoAndAddGraphIntoIt(this.ontologyTableDao.getFileName(), repoName);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
