package excel.Workbook;

import excel.Worksheet.Worksheet;

import java.util.ArrayList;
import java.util.List;

public class Workbook {
    private List<Macro> macro = new ArrayList<>();
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

    public void addMacro(String macroName, String macroDescription){
        this.macro.add(new Macro(macroName, macroDescription));
    }

    public List<Macro> getMacro() {
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
