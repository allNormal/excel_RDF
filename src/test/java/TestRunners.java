import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import persistence.impl.OntologyDao;
import service.impl.OntologyService;

public class TestRunners {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(excelTest.class);

        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }

        System.out.println(result.wasSuccessful());

        result = JUnitCore.runClasses(OntologyTest.class);

        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }

        System.out.println(result.wasSuccessful());


        /*
        OntologyDao dao = new OntologyDao();
        OntologyService service = new OntologyService(dao);
        service.create("C:\\Users\\43676\\Desktop\\uni\\sepm-individual-assignment-java-template\\template\\rdfTest\\src\\main\\resources\\simple_test.xlsm");

         */
    }
}  	