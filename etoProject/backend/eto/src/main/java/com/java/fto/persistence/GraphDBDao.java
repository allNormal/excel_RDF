package com.java.fto.persistence;

import org.apache.jena.ontology.OntModel;
import org.eclipse.rdf4j.repository.Repository;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface GraphDBDao {

    void createRepoAndAddGraphIntoIt(String fileName, String repoName) throws IOException;

    void addGraphIntoRepo(String fileName, String repoName) throws IOException;

    List<String> getAllRepoName();

}
