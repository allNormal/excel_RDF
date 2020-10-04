import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.tdb2.TDB2Factory;

import static org.apache.jena.ontology.OntModelSpec.*;


//inspired + with help from https://github.com/jjnp/dss-ue2/blob/master/src/main/java/at/ac/tuwien/student/e01526624/backend/service/ModelManager.java
public class OntologyConverter {
    private OntModel model;
    private OntModel directInferredModel;
    private Dataset dataset;
    private final String NS = "http://www.semanticweb.org/43676/ontologies/2020/8/untitled-ontology-18#";
    private final String DATASET_LOCATION = "target/ds";
    private final String MODEL_NAME = "ExcelRDF";

    public OntologyConverter() {
        this.dataset = TDB2Factory.createDataset(DATASET_LOCATION);
        initializeTemplateModel();

    }

    public void writeDataset(){
        if(!this.dataset.isInTransaction()) this.dataset.begin(ReadWrite.WRITE);
    }

    public void closeDataset(){
        this.dataset.commit();
        this.dataset.end();
    }

    private void initializeTemplateModel(){
        if(!this.dataset.containsNamedModel(MODEL_NAME)) initializeDataSet();
        loadModels();
    }

    private void loadModels(){
        this.dataset.begin(ReadWrite.READ);
        Model temp = this.dataset.getNamedModel(MODEL_NAME);
        this.model = ModelFactory.createOntologyModel(OWL_MEM,temp);
        this.directInferredModel = ModelFactory.createOntologyModel(OWL_MEM_RULE_INF);
        this.dataset.commit();
        this.dataset.end();
    }

    private void initializeDataSet(){
        this.dataset.begin(ReadWrite.WRITE);
        OntModel temp = ModelFactory.createOntologyModel(OWL_MEM);
        temp.read("C:\\Users\\43676\\Desktop\\uni\\sepm-individual-assignment-java-template\\template\\rdfTest\\src\\main\\resources\\excel_owl\\excel_ver4_xml.owl");
        this.dataset.addNamedModel(MODEL_NAME,temp);
        this.dataset.commit();
        this.dataset.end();
    }
}
