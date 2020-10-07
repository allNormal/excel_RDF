package excel.SheetElement.BasicElement;

import excel.Constraints.Constraint;
import excel.Worksheet.Worksheet;

import java.util.ArrayList;
import java.util.List;

public class Column extends BasicElement {
    private String columnID;
    private List<Cell> cell = new ArrayList<>();
    private Constraint constraint;

    public Column(Worksheet worksheet, String columnID) {
        super(worksheet);
        this.columnID = convertColumn(columnID);
    }

    private String convertColumn(String column) {
        String result = "";
        int min = 65;
        int temp = Integer.parseInt(column);
        boolean check = false;

        while(temp >= 0) {
            if(temp <= 25) check = true;
            int calc = temp % 25;
            temp = temp/25;

            char character = (char) (min + calc);
            result = result + character;
            if(check) break;
        }
        return result;
    }

    public void addCell(Cell cell){
        this.cell.add(cell);
    }

    public void setConstraint(Constraint constraint){
        this.constraint = constraint;
    }

    public String getColumnID() {
        return columnID;
    }

    public List<Cell> getCell() {
        return cell;
    }

    public Constraint getConstraint() {
        return constraint;
    }
}
