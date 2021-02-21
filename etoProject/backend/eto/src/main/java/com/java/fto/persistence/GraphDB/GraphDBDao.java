package com.java.fto.persistence.GraphDB;


import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.config.AbstractRepositoryImplConfig;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.config.RepositoryImplConfig;
import org.eclipse.rdf4j.repository.manager.RepositoryInfo;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryProvider;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Repository
public class GraphDBDao implements com.java.fto.persistence.GraphDBDao {

    private RepositoryManager repositoryManager;
    private final String FILELOCATION = "./OntologyOut/";

    public GraphDBDao() {
        repositoryManager = RepositoryProvider.getRepositoryManager("http://localhost:7200");
        repositoryManager.init();
    }

    @Override
    public void createRepoAndAddGraphIntoIt(String fileName, String repoName) throws IOException {

        RepositoryImplConfig repositoryImplConfig = new AbstractRepositoryImplConfig();
        RepositoryConfig repositoryConfig = new RepositoryConfig(repoName, repositoryImplConfig);
        repositoryManager.addRepositoryConfig(repositoryConfig);
        Repository repository = repositoryManager.getRepository(repoName);
        Path path = Paths.get(FILELOCATION + fileName + ".ttl");
        File file =  new File(path.toString());
        RepositoryConnection repositoryConnection = repository.getConnection();
        repositoryConnection.begin();
        repositoryConnection.add(file, null, RDFFormat.TURTLE);
        repositoryConnection.commit();
        repositoryConnection.close();
    }

    @Override
    public void addGraphIntoRepo(String fileName, String repoName) throws IOException {
        Repository repository = repositoryManager.getRepository(repoName);
        Path path = Paths.get(FILELOCATION + fileName + ".ttl");
        File file =  new File(path.toString());
        RepositoryConnection repositoryConnection = repository.getConnection();
        repositoryConnection.begin();
        repositoryConnection.add(file, null, RDFFormat.TURTLE);
        repositoryConnection.commit();
        repositoryConnection.close();
    }

    @Override
    public List<String> getAllRepoName() {
        List<String> result = new ArrayList<>();
        for(RepositoryInfo repo : repositoryManager.getAllRepositoryInfos()) {
          result.add(repo.getId());
        }
        return result;
    }
}
