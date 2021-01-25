import entity.Operator;
import entity.SheetElement.ElementType;
import persistence.impl.OntologyExcelDao;
import persistence.impl.OntologyTableDao;
import service.impl.OntologyService;

import java.util.Collection;

public class Test {

    public static void main(String[] args) {

        OntologyTableDao abc = new OntologyTableDao(0,0);
        OntologyService test = new OntologyService(abc);

        test.create("src/main/resources/test_table.xlsm");
        /*
        OntologyExcelDao abc = new OntologyExcelDao();
        OntologyService test = new OntologyService(abc);

        test.create("src/main/resources/simple_test.xlsm");
        Collection<String> testCheckDependency = test.getDependency("F4", "Sheet1");
        System.out.println("Dependency...");
        for(String temp : testCheckDependency) {
            System.out.println(temp);
        }
        Collection<String> testAddConstraint = test.addConstraint(ElementType.COLUMN,"A", "Sheet1", Operator.NE, "Bread");
        System.out.println("Constraint...");
        for (String temp : testAddConstraint) {
            System.out.println(temp);
        }
        System.out.println("ReverseDependency...");
        Collection<String> testCheckReverseDependency = test.getReverseDependency("B2", "Sheet1");
        for(String temp : testCheckReverseDependency) {
            System.out.println(temp);
        }

        */
        /*
        test.create("src/main/resources/OBARIS SC2 Alle_Parameter_2007-2013_Basisversion_Erosion.xlsm");
        Collection<String> testCheck = test.getDependency("F4", "Intermediate");
        System.out.println("Dependency");
        for(String temp : testCheck) {
            System.out.println(temp);
        }

         */
    }

}
