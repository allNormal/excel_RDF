import entity.Operator;
import entity.SheetElement.ElementType;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import persistence.impl.OntologyDao;
import service.impl.OntologyService;

import java.util.Collection;
import java.util.List;

public class Test {

    public static void main(String[] args) {

        OntologyDao abc = new OntologyDao();
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
         //test.create("src/main/resources/OBARIS SC2 Alle_Parameter_2007-2013_Basisversion_Erosion.xlsm");
    }

}
