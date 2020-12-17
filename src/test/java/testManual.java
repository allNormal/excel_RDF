import persistence.impl.OntologyDao;
import service.impl.OntologyService;

public class testManual {
    public static void main(String[] args) {
        OntologyDao dao = new OntologyDao();
        OntologyService service = new OntologyService(dao);
        service.create("src/main/resources/simple_test.xlsm");
    }
}
