package excel.SheetElement.BasicElement;

import excel.Constraints.Constraint;

import java.util.ArrayList;
import java.util.List;

public class Column {
    private String columnID;
    private List<Cell> cell = new ArrayList<>();
    private Constraint constraint;

    public Column(String columnID) {
        this.columnID = columnID;
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
