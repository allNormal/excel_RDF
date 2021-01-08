import org.junit.Ignore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Test;
import persistence.impl.readExcel;

import java.io.File;
import java.io.IOException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Ignore
public class dependencyTest {

    private persistence.impl.readExcel readExcel;

    @BeforeAll
    void initFile() throws IOException {
        String testFile = "src/main/resources/OBARIS SC2 Alle_Parameter_2007-2013_Basisversion_Erosion.xlsm";
        File file = new File(testFile);
        readExcel = new readExcel(file);
    }

    @Test
    @Ignore
    @DisplayName("not so complicated formula")
    void notComplicatedTest() {
        String[][] cell = {
                {"CO4", "CU4", "DQ4"},
                {"A4", "D4", "F4", "G4", "K4"},
                {"E4", "F4", "O4", "U4", "V4"},
                {"AS10", "CQ10"}};
        String[] worksheet = {"Basicinfo", "Intermediate", "Local emissions", "Summed emissions"};
        String[][] value = {
                {"BS4", "BT4","B4","AL","AM"},
                {"A4", "U4", "V4", "Y4", "D4", "E4", "H4", "I4", "J4", "K4", "L4", "N4", "BT4", "BU4"},
                {"N4", "O4", "AR4", "CB4", "CG4", "AW4", "BS4", "BL4", "BK4", "BF4", "BG4", "BH4", "CV4"},
                {"F10", "D10", "AI10", "P10", "C153"}};
    }
}
