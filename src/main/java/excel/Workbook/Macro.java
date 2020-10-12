package excel.Workbook;

import java.util.List;
import java.util.Map;

public class Macro {
    private String module;
    private String  macro;

    public Macro(String macro) {
        this.macro = macro;
    }

    public String getModule() {
        return module;
    }

    public String getMacro() {
        return macro;
    }
}
