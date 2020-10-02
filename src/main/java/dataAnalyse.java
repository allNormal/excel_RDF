import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class dataAnalyse {

    private List<String> saveTitle = new ArrayList<>();
    private XSSFWorkbook myWorkBook;
    private XSSFSheet mySheet;
    private int titleIndex = 1;

    public dataAnalyse (String filepath) {
        try {
            readExcel(filepath);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());;
        }
    }

    public void checkAllColumnDataTypes() {
        for(int i = 0;i<saveTitle.size();i++){
            System.out.println(checkColumnDataTypes(saveTitle.get(i)));

        }
    }
    public String checkColumnDataTypes (String columnTitle) {
        Map<String,Integer> saveTotalDataTypes = new HashMap<>();
        DataFormatter formatter = new DataFormatter();
        int index = 0;
        //default value
        saveTotalDataTypes.put("Label",0);
        saveTotalDataTypes.put("Value",0);
        saveTotalDataTypes.put("Error",0);
        saveTotalDataTypes.put("Formula",0);

        //check which column
        for(int i = 0;i<saveTitle.size();i++){
            if(saveTitle.get(i) == columnTitle) {
                index = i;
                break;
            }
        }

        for(Row row : mySheet) {
            for(Cell cell : row) {
                if(cell.getColumnIndex() == index) {
                    // Alternatively, get the value and format it yourself
                    switch (cell.getCellType()) {
                        case STRING:
                            saveTotalDataTypes.replace("Label",saveTotalDataTypes.get("Label") + 1);
                            break;
                        case NUMERIC:
                            saveTotalDataTypes.replace("Value",saveTotalDataTypes.get("Value") + 1);
                            break;
                        case BOOLEAN:
                            saveTotalDataTypes.replace("Formula",saveTotalDataTypes.get("Formula") + 1);
                            break;
                        case FORMULA:
                            saveTotalDataTypes.replace("Formula",saveTotalDataTypes.get("Formula") + 1);
                            break;
                        case BLANK:
                            System.out.println();
                            break;
                        case ERROR:
                            saveTotalDataTypes.replace("Error",saveTotalDataTypes.get("Error") + 1);
                            break;
                        default:
                            System.out.println();
                    }
                }
            }
        }

        System.out.println("Total Datatypes for column " + columnTitle + " =");
        return ("Label = " + saveTotalDataTypes.get("Label") + "\nValue = " + saveTotalDataTypes.get("Value") +
                "\nFormula = " + saveTotalDataTypes.get("Formula") + "\nError = " + saveTotalDataTypes.get("Error"));
    }

    public String checkRawDataTypes () {
        Map<String,Integer> saveTotalDataTypes = new HashMap<>();
        int count = 1;
        //default value
        saveTotalDataTypes.put("Label",0);
        saveTotalDataTypes.put("Value",0);
        saveTotalDataTypes.put("Error",0);
        saveTotalDataTypes.put("Formula",0);

        //iterate through excel
        for(Row row : mySheet){
            if(count <= titleIndex){
                count++;
                continue;
            }
            for (Cell cell : row) {
                // Alternatively, get the value and format it yourself
                switch (cell.getCellType()) {
                    case STRING:
                        saveTotalDataTypes.replace("Label",saveTotalDataTypes.get("Label") + 1);
                        break;
                    case NUMERIC:
                        saveTotalDataTypes.replace("Value",saveTotalDataTypes.get("Value") + 1);
                        break;
                    case BOOLEAN:
                        saveTotalDataTypes.replace("Formula",saveTotalDataTypes.get("Formula") + 1);
                        break;
                    case FORMULA:
                        saveTotalDataTypes.replace("Formula",saveTotalDataTypes.get("Formula") + 1);
                        break;
                    case BLANK:
                        System.out.println();
                        break;
                    case ERROR:
                        saveTotalDataTypes.replace("Error",saveTotalDataTypes.get("Error") + 1);
                        break;
                    default:
                        System.out.println();
                }
            }
        }
        System.out.println("Total Datatypes =");
        return ("Label = " + saveTotalDataTypes.get("Label") + "\nValue = " + saveTotalDataTypes.get("Value") +
                "\nFormula = " + saveTotalDataTypes.get("Formula") + "\nError = " + saveTotalDataTypes.get("Error"));
    }

    private void readExcel(String filePath) throws IOException {

        File myFile = new File(filePath);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(myFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Finds the workbook instance for XLSX file
        myWorkBook = new XSSFWorkbook (fis);

        // Return first sheet from the XLSX workbook
        mySheet = myWorkBook.getSheetAt(0);

        for(int i = mySheet.getFirstRowNum();i<mySheet.getLastRowNum();i++){
            Row row = mySheet.getRow(i);
            if(row == null){
                titleIndex++;
                continue;
            }
            else{
                for(int j = 0;j<row.getLastCellNum();i++){
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if(cell == null) {
                        break;
                    }
                    else{
                        if(cell.getCellType() != CellType.STRING){
                            System.out.println("Are you sure to put not label datatypes as title?");
                        }
                        else{
                            saveTitle.add(cell.getStringCellValue());
                        }
                    }
                }
                break;
            }
        }
        System.out.println("title = ");
        for(int i = 0;i<saveTitle.size();i++){
            System.out.print(saveTitle.get(i)+"\t\t");
        }
        System.out.println();

    }
}
