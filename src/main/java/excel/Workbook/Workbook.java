package excel.Workbook;

import excel.Worksheet.Worksheet;
import org.apache.poi.ss.usermodel.Cell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Workbook {
    private Macro macro;
    private List<Worksheet> worksheets = new ArrayList<>();
    private String extension;
    private String fileName;

    public Workbook(String extension, String fileName) {
        this.extension = extension;
        this.fileName = fileName;
    }

    public void addWorksheet(Worksheet worksheet){
        this.worksheets.add(worksheet);
    }

    public void setMacro(Macro macro) {
        this.macro = macro;
    }

    public Macro getMacro() {
        return macro;
    }

    public List<Worksheet> getWorksheets() {
        return worksheets;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
