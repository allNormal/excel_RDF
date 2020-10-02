package excel.SheetElement;

import excel.SheetElement.BasicElement.BasicElement;
import excel.SheetElement.Charts.Chart;
import excel.SheetElement.Illustrations.Illustrations;
import excel.SheetElement.Tables.Table;
import excel.SheetElement.Texts.Text;
import excel.Worksheet.Worksheet;

import java.util.ArrayList;
import java.util.List;

public class SheetElement {
    private List<BasicElement> basicElements = new ArrayList<>();
    private List<Table> tables =  new ArrayList<>();
    private List<Chart> charts = new ArrayList<>();
    private List<Illustrations> illustrations = new ArrayList<>();
    private List<Text> texts = new ArrayList<>();
    private Worksheet worksheet;

    public SheetElement(Worksheet worksheet){
        this.worksheet = worksheet;
    }
}
