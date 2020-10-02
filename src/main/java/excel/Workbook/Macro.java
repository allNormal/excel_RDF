package excel.Workbook;

public class Macro {
    private String macroName;
    private String macroDescription;

    public Macro(String macroName, String macroDescription) {
        this.macroName = macroName;
        this.macroDescription = macroDescription;
    }

    public String getMacroName() {
        return macroName;
    }

    public void setMacroName(String macroName) {
        this.macroName = macroName;
    }

    public String getMacroDescription() {
        return macroDescription;
    }

    public void setMacroDescription(String macroDescription) {
        this.macroDescription = macroDescription;
    }
}
