import persistence.impl.OntologyDao;
import service.impl.OntologyService;

public class Test {
    public static void main(String[] args) {
        OntologyDao dao = new OntologyDao();
        OntologyService o = new OntologyService(dao);
        o.create("C:\\Users\\43676\\Desktop\\uni\\sepm-individual-assignment-java-template\\template\\rdfTest\\src\\main\\resources\\simple_test.xlsm");
        OntologyService o1 = new OntologyService(dao);
        o1.create("C:\\Users\\43676\\Desktop\\uni\\sepm-individual-assignment-java-template\\template\\rdfTest\\src\\main\\resources\\OBARIS SC2 Alle_Parameter_2007-2013_Basisversion_Erosion.xlsm");

    }
}
