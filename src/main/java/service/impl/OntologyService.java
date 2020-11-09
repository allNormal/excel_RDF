package service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.impl.OntologyDao;

import java.lang.invoke.MethodHandles;

public class OntologyService implements service.OntologyService {
    private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final OntologyDao dao;

    public OntologyService(OntologyDao dao) {
        this.dao = dao;
    }

    @Override
    public void create(String filepath) {
        LOGGER.trace("Created a new Ontology for entity.excel file in ({})", filepath);
        dao.create(filepath);
    }

}
