package service.impl;

import entity.Operator;
import entity.SheetElement.ElementType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.impl.OntologyExcelDao;

import java.lang.invoke.MethodHandles;
import java.util.Collection;

public class OntologyService implements service.OntologyService {
    private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final OntologyExcelDao dao;

    public OntologyService(OntologyExcelDao dao) {
        this.dao = dao;
    }

    @Override
    public void create(String filepath) {
        LOGGER.trace("Created a new Ontology for entity.excel file in ({})", filepath);
        dao.create(filepath);
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
