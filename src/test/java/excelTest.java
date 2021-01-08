import entity.SheetElement.ElementType;
import entity.SheetElement.SheetElement;
import entity.Workbook.Workbook;
import entity.Worksheet.Worksheet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import persistence.impl.readExcel;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;


import static entity.SheetElement.ElementType.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class excelTest {

    private final readExcel[] readExcel = new readExcel[4];


    @BeforeAll
    void initFile() throws IOException {
        final String[] FILEPATH = {"src/main/resources/simple_test.xlsm",
                "src/main/resources/test.xlsx",
                "src/main/resources/Endangered_Species.xlsx",
                "src/main/resources/reptile_checklist_2020_08.xlsx"};
        File file;
        for(int i = 0; i<FILEPATH.length; i++) {
            file = new File(FILEPATH[i]);
            readExcel[i] = new readExcel(file);
        }
    }

    @Test
    @DisplayName("Testing if it read the correct number of worksheets")
    void worksheetTest(){
        int[] expected = {2, 5, 1, 1};
        int[] actual = new int[expected.length];
        for(int i = 0; i<this.readExcel.length; i++) {
            Workbook workbook1 = readExcel[i].getWorkbook();
            List<Worksheet> worksheet1 = workbook1.getWorksheets();
            actual[i] = worksheet1.size();
        }

        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("Testing if it read the correct number of workbook")
    void workbookTest(){
        for(int i = 0; i<this.readExcel.length; i++) {
            Workbook workbook1 = readExcel[i].getWorkbook();
            assertNotNull(workbook1);
        }
    }

    @Test
    @DisplayName("Testing if it read the correct number of cells")
    void cellTest() {
        int[][] expected = {{74, 46},{8188, 483, 160, 2, 16}, {416}, {0}};
        int[][] actual = new int[expected.length][];
        for(int i = 0; i<this.readExcel.length; i++) {
            Workbook workbook1 = readExcel[i].getWorkbook();
            List<Worksheet> worksheet1 = workbook1.getWorksheets();
            int[] result = new int[worksheet1.size()];
            for(int j = 0; j<worksheet1.size(); j++) {
                Map<ElementType, List<SheetElement>> sheetElement = worksheet1.get(j).getSheets();
                if(!sheetElement.containsKey(CELL)) result[j] = 0;
                else result[j] = sheetElement.get(CELL).size();
            }
            actual[i] = result;
        }
        for(int i = 0; i<expected.length; i++){
            assertArrayEquals(expected[i], actual[i]);
        }
    }

    @Test
    @DisplayName("Testing if it read the correct number of tables")
    void tableTest() {
        int[][] expected = {{1, 0},{0, 5, 0, 0, 0}, {0}, {0}};
        int[][] actual = new int[expected.length][];
        for(int i = 0; i<this.readExcel.length; i++) {
            Workbook workbook1 = readExcel[i].getWorkbook();
            List<Worksheet> worksheet1 = workbook1.getWorksheets();
            int[] result = new int[worksheet1.size()];
            for(int j = 0; j<worksheet1.size(); j++) {
                Map<ElementType, List<SheetElement>> sheetElement = worksheet1.get(j).getSheets();
                if(!sheetElement.containsKey(TABLE)) result[j] = 0;
                else result[j] = sheetElement.get(TABLE).size();
            }
            actual[i] = result;
        }
        for(int i = 0; i<expected.length; i++){
            assertArrayEquals(expected[i], actual[i]);
        }
    }

    @Test
    @DisplayName("Testing if it read the correct number of Charts")
    void chartTest() {
        int[][] expected = {{1, 0},{0, 0, 0, 5, 0}, {0}, {0}};
        int[][] actual = new int[expected.length][];
        for(int i = 0; i<this.readExcel.length; i++) {
            Workbook workbook1 = readExcel[i].getWorkbook();
            List<Worksheet> worksheet1 = workbook1.getWorksheets();
            int[] result = new int[worksheet1.size()];
            for(int j = 0; j<worksheet1.size(); j++) {
                Map<ElementType, List<SheetElement>> sheetElement = worksheet1.get(j).getSheets();
                if(!sheetElement.containsKey(CHART)) result[j] = 0;
                else result[j] = sheetElement.get(CHART).size();
            }
            actual[i] = result;
        }
        for(int i = 0; i<expected.length; i++){
            assertArrayEquals(expected[i], actual[i]);
        }
    }

    @Test
    @DisplayName("Testing if it read the correct number of Illustrations")
    void illustrationTest() {
        int[][] expected = {{1, 0},{0, 0, 12, 0, 0}, {0}, {0}};
        int[][] actual = new int[expected.length][];
        for(int i = 0; i<this.readExcel.length; i++) {
            Workbook workbook1 = readExcel[i].getWorkbook();
            List<Worksheet> worksheet1 = workbook1.getWorksheets();
            int[] result = new int[worksheet1.size()];
            for(int j = 0; j<worksheet1.size(); j++) {
                Map<ElementType, List<SheetElement>> sheetElement = worksheet1.get(j).getSheets();
                if(!sheetElement.containsKey(ILLUSTRATION)) result[j] = 0;
                else result[j] = sheetElement.get(ILLUSTRATION).size();
            }
            actual[i] = result;
        }
        for(int i = 0; i<expected.length; i++){
            assertArrayEquals(expected[i], actual[i]);
        }
    }

    @Test
    @DisplayName("Testing if it read the correct number of text box")
    void textTest() {
        int[][] expected = {{1, 0},{0, 0, 0, 0, 6}, {0}, {0}};
        int[][] actual = new int[expected.length][];
        for(int i = 0; i<this.readExcel.length; i++) {
            Workbook workbook1 = readExcel[i].getWorkbook();
            List<Worksheet> worksheet1 = workbook1.getWorksheets();
            int[] result = new int[worksheet1.size()];
            for(int j = 0; j<worksheet1.size(); j++) {
                Map<ElementType, List<SheetElement>> sheetElement = worksheet1.get(j).getSheets();
                if(!sheetElement.containsKey(TEXT)) result[j] = 0;
                else result[j] = sheetElement.get(TEXT).size();
            }
            actual[i] = result;
        }
        for(int i = 0; i<expected.length; i++){
            assertArrayEquals(expected[i], actual[i]);
        }
    }

}
