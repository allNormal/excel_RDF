import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        //OntologyService o = new OntologyService("C:\\Users\\43676\\Desktop\\uni\\sepm-individual-assignment-java-template\\template\\rdfTest\\src\\main\\resources\\simple_test.xlsm");
        OntologyService o1 = new OntologyService("C:\\Users\\43676\\Desktop\\uni\\sepm-individual-assignment-java-template\\template\\rdfTest\\src\\main\\resources\\OBARIS SC2 Alle_Parameter_2007-2013_Basisversion_Erosion(1).xlsm");
        o1.saveModel("C:\\Users\\43676\\Desktop\\uni\\sepm-individual-assignment-java-template\\template\\rdfTest\\src\\main\\resources\\OBARIS SC2 Alle_Parameter_2007-2013_Basisversion_Erosion(1).xlsm");
        //o.saveModel("C:\\Users\\43676\\Desktop\\uni\\sepm-individual-assignment-java-template\\template\\rdfTest\\src\\main\\resources\\simple_test.xlsm");
    }
}
