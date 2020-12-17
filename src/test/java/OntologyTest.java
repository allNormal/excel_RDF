import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import persistence.impl.OntologyDao;
import service.impl.OntologyService;

import java.io.File;

import static org.apache.jena.ontology.OntModelSpec.OWL_MEM;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OntologyTest {

    private final String NS = "http://www.semanticweb.org/GregorKaefer/ontologies/2020/8/excelOntology#";
    private OntModel[] ontModels = new OntModel[4];

    @BeforeAll
    void initall() {
        final String[] FILEPATH = {"src/main/resources/simple_test.xlsm",
                "src/main/resources/test.xlsx",
                "src/main/resources/Endangered_Species.xlsx",
                "src/main/resources/reptile_checklist_2020_08.xlsx"};

        OntologyDao dao = new OntologyDao();
        OntologyService ontologyService = new OntologyService(dao);
        for(int i = 0; i<FILEPATH.length; i++) {
            ontologyService.create(FILEPATH[i]);
            this.ontModels[i] = ModelFactory.createOntologyModel(OWL_MEM);
            this.ontModels[i].read("OntologyOut\\"+ new File(FILEPATH[i]).getName()+".ttl");
        }

    }

    @Test
    @DisplayName("Testing if converting the correct number of workbook to rdf")
    void ontologyWorkbookTest(){
        OntClass workbook;
        for(int i = 0; i<ontModels.length; i++){
            workbook = ontModels[i].getOntClass(NS + "Workbook");
            assertNotNull(workbook);
        }
    }

    @Test
    @DisplayName("Testing if converting the correct number of cell to rdf")
    void ontologyCellTest() {
        OntClass cell;
        OntClass worksheet;
        DatatypeProperty isPartOfWorksheet;
        int[] expected = {80, 8426, 416, 0};
        int[] actual = new int[expected.length];
        int count = 0;

        for(int i = 0; i<ontModels.length; i++) {
            cell = ontModels[i].getOntClass(NS + "cell");
            worksheet = ontModels[i].getOntClass(NS + "Worksheet");
            isPartOfWorksheet = ontModels[i].getDatatypeProperty(NS + "isPartOfWorksheet");

            ExtendedIterator iterWorksheet = worksheet.listInstances();
            while (iterWorksheet.hasNext()) {
                ExtendedIterator iterCell =  cell.listInstances();
                Individual worksheetIndividual = (Individual) iterWorksheet.next();
                while(iterCell.hasNext()) {
                    Individual cellIndividual = (Individual)iterCell.next();

                    if(cellIndividual.hasProperty(isPartOfWorksheet, worksheetIndividual)){
                        count++;
                    }
                }
            }
            actual[i] = count;
            count = 0;
        }
        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("Testing if converting the correct number of table to rdf")
    void ontologyTableTest() {
        OntClass table;
        OntClass worksheet;
        DatatypeProperty isPartOfWorksheet;
        int[] expected = {1, 5, 0, 0};
        int[] actual = new int[expected.length];
        int count = 0;

        for(int i = 0; i<ontModels.length; i++) {
            table = ontModels[i].getOntClass(NS + "Table");
            worksheet = ontModels[i].getOntClass(NS + "Worksheet");
            isPartOfWorksheet = ontModels[i].getDatatypeProperty(NS + "isPartOfWorksheet");

            ExtendedIterator iterWorksheet = worksheet.listInstances();
            while (iterWorksheet.hasNext()) {
                ExtendedIterator iterTable =  table.listInstances();
                Individual worksheetIndividual = (Individual) iterWorksheet.next();
                while(iterTable.hasNext()) {
                    Individual cellIndividual = (Individual)iterTable.next();

                    if(cellIndividual.hasProperty(isPartOfWorksheet, worksheetIndividual)){
                        count++;
                    }
                }
            }
            actual[i] = count;
            count = 0;
        }
        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("Testing if converting the correct number of Charts to rdf")
    void ontologyChartTest() {
        OntClass chart;
        OntClass worksheet;
        DatatypeProperty isPartOfWorksheet;
        int[] expected = {1, 5, 0, 0};
        int[] actual = new int[expected.length];
        int count = 0;

        for(int i = 0; i<ontModels.length; i++) {
            chart = ontModels[i].getOntClass(NS + "Chart");
            worksheet = ontModels[i].getOntClass(NS + "Worksheet");
            isPartOfWorksheet = ontModels[i].getDatatypeProperty(NS + "isPartOfWorksheet");

            ExtendedIterator iterWorksheet = worksheet.listInstances();
            while (iterWorksheet.hasNext()) {
                ExtendedIterator iterChart =  chart.listInstances();
                Individual worksheetIndividual = (Individual) iterWorksheet.next();
                while(iterChart.hasNext()) {
                    Individual cellIndividual = (Individual)iterChart.next();

                    if(cellIndividual.hasProperty(isPartOfWorksheet, worksheetIndividual)){
                        count++;
                    }
                }
            }
            actual[i] = count;
            count = 0;
        }
        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("Testing if converting the correct number of illustration to rdf")
    void ontologyIllustrationTest() {
        OntClass illustration;
        OntClass worksheet;
        DatatypeProperty isPartOfWorksheet;
        int[] expected = {1, 12, 0, 0};
        int[] actual = new int[expected.length];
        int count = 0;

        for(int i = 0; i<ontModels.length; i++) {
            illustration = ontModels[i].getOntClass(NS + "Illustration");
            worksheet = ontModels[i].getOntClass(NS + "Worksheet");
            isPartOfWorksheet = ontModels[i].getDatatypeProperty(NS + "isPartOfWorksheet");

            ExtendedIterator iterWorksheet = worksheet.listInstances();
            while (iterWorksheet.hasNext()) {
                ExtendedIterator iterIllustration =  illustration.listInstances();
                Individual worksheetIndividual = (Individual) iterWorksheet.next();
                while(iterIllustration.hasNext()) {
                    Individual cellIndividual = (Individual)iterIllustration.next();

                    if(cellIndividual.hasProperty(isPartOfWorksheet, worksheetIndividual)){
                        count++;
                    }
                }
            }
            actual[i] = count;
            count = 0;
        }
        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("Testing if converting the correct number of text to rdf")
    void ontologyTextTest() {
        OntClass text;
        OntClass worksheet;
        DatatypeProperty isPartOfWorksheet;
        int[] expected = {1, 6, 0, 0};
        int[] actual = new int[expected.length];
        int count = 0;

        for(int i = 0; i<ontModels.length; i++) {
            text = ontModels[i].getOntClass(NS + "Text");
            worksheet = ontModels[i].getOntClass(NS + "Worksheet");
            isPartOfWorksheet = ontModels[i].getDatatypeProperty(NS + "isPartOfWorksheet");

            ExtendedIterator iterWorksheet = worksheet.listInstances();
            while (iterWorksheet.hasNext()) {
                ExtendedIterator iterText =  text.listInstances();
                Individual worksheetIndividual = (Individual) iterWorksheet.next();
                while(iterText.hasNext()) {
                    Individual cellIndividual = (Individual)iterText.next();

                    if(cellIndividual.hasProperty(isPartOfWorksheet, worksheetIndividual)){
                        count++;
                    }
                }
            }
            actual[i] = count;
            count = 0;
        }
        assertArrayEquals(expected, actual);
    }
}
