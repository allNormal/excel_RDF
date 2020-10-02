package excel.SheetElement.Charts;

import excel.SheetElement.SheetElement;
import excel.Worksheet.Worksheet;

import java.util.ArrayList;
import java.util.List;

public class Chart extends SheetElement {
    private List<String> legend = new ArrayList<>();
    private List<String> label = new ArrayList<>();
    private String title;

    public Chart(Worksheet worksheet, List<String> legend, List<String> label, String title) {
        super(worksheet);
        this.legend = legend;
        this.label = label;
        this.title = title;
    }

    public List<String> getLegend() {
        return legend;
    }

    public List<String> getLabel() {
        return label;
    }

    public String getTitle() {
        return title;
    }
}
