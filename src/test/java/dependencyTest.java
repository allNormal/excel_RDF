import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Test;
import persistence.impl.OntologyExcelDao;
import service.impl.OntologyService;

import java.io.IOException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class dependencyTest {

    private mapper.readExcel readExcel;

    private OntologyExcelDao ontologyExcelDao = new OntologyExcelDao();
    private OntologyService ontologyService;
    @BeforeAll
    void initFile() throws IOException {
        String testFile = "src/main/resources/OBARIS SC2 Alle_Parameter_2007-2013_Basisversion_Erosion.xlsm";
        ontologyService = new OntologyService(ontologyExcelDao);
        ontologyService.create(testFile);
    }

    @Test
    @DisplayName("not so complicated formula")
    void notComplicatedTest() {
        boolean check = true;
        String[][] cell = {
                {"CO4", "CU4", "DQ4"},
                {"A4", "D4", "F4", "G4", "K4"},
                {"E4", "F4", "O4", "U4", "V4"},
                {"AS10", "CQ10"}};
        String[] worksheet = {"Basicinfo", "Intermediate", "Local emissions", "Summed emissions"};
        String[] value = {
                "BS4BT4B4ALAM",
                "A4U4V4Y4D4E4H4I4J4K4L4N4BT4BU4W4X4Z4",
                "N4O4AR4CB4CG4AW4BS4BL4BK4BF4BG4BH4CV4",
                "F10D10AI10P10C153"};

        for(int i = 0;i<cell.length;i++){
            for(int j = 0 ; j<cell[i].length;j++){
                Collection<String> temp2 = ontologyService.getDependency(cell[i][j], worksheet[i]);
                for(String temp : temp2) {
                    String[] splitter = temp.split("_");
                    if(i == 0 && j == 1 && (temp.contains("AL") || temp.contains("AM"))) {
                        String checking = splitter[splitter.length-1];
                        if(Integer.parseInt(checking.substring(2))>1 && Integer.parseInt(checking.substring(2)) < 83) continue;
                        else check = false;
                    } else if(!value[i].contains(splitter[splitter.length-1])) {
                        System.out.println(worksheet[i]);
                        check = false;
                    }
                }
            }
        }
        assertEquals(check, true);
    }

/*
    @Test
    @DisplayName("complicated formula test")
    void complicatedTest() {
        boolean check = true;
        String[][] cell = {
                {"T4", "CU4", "DQ4"},
                {"A4", "D4", "F4", "G4", "K4"},
                {"E4", "F4", "O4", "U4", "V4"},
                {"AS10", "CQ10"}};
        String[] worksheet = {"Intermediate", "Local emissions", "Summed emissions"};
        String[] value = {
                "BS4",
                "A4U4V4Y4D4E4H4I4J4K4L4N4BT4BU4W4X4Z4",
                "N4O4AR4CB4CG4AW4BS4BL4BK4BF4BG4BH4CV4",
                "F10D10AI10P10C153"};

    }

 */
}
