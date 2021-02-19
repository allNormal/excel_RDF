package com.java.fto.persistence.GraphDB;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.config.RepositoryConfigSchema;
import org.eclipse.rdf4j.repository.manager.RepositoryInfo;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryProvider;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
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

        TreeModel graph = new TreeModel();

        // Read repository configuration file
        InputStream config = new FileInputStream(FILELOCATION + fileName + ".ttl");
        RDFParser rdfParser = Rio.createParser(RDFFormat.TURTLE);
        rdfParser.setRDFHandler(new StatementCollector(graph));
        rdfParser.parse(config, RepositoryConfigSchema.NAMESPACE);
        config.close();

        // Retrieve the repository node as a resource
        Resource repositoryNode =  Models.subject(graph
                .filter(null, RDF.TYPE, RepositoryConfigSchema.REPOSITORY))
                .orElseThrow(() -> new RuntimeException(
                        "Oops, no <http://www.openrdf.org/config/repository#> subject found!"));


        // Create a repository configuration object and add it to the repositoryManager
        RepositoryConfig repositoryConfig = RepositoryConfig.create(graph, repositoryNode);
        repositoryManager.addRepositoryConfig(repositoryConfig);
    }

    @Override
    public void addGraphIntoRepo(String fileName, String repoName) throws IOException {
        Repository repository = repositoryManager.getRepository(repoName);


        Path path = Paths.get(FILELOCATION + fileName + ".ttl");
        File file =  new File(path.toString());
        RepositoryConnection repositoryConnection = repository.getConnection();
        IRI iri = repository.getValueFactory().createIRI(path.toString());
        repositoryConnection.add(file, null, RDFFormat.TURTLE, iri);
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
