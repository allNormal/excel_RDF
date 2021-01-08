import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import persistence.impl.OntologyDao;
import service.impl.OntologyService;

public class Test {

    public static void main(String[] args) {

        OntologyDao abc = new OntologyDao();
        OntologyService test = new OntologyService(abc);
        test.create("src/main/resources/OBARIS SC2 Alle_Parameter_2007-2013_Basisversion_Erosion.xlsm");
    }

}
