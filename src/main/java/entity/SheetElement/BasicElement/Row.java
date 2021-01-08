package entity.SheetElement.BasicElement;

import entity.Constraints.Constraint;
import entity.Worksheet.Worksheet;

import java.util.ArrayList;
import java.util.List;

public class Row extends BasicElement{
    private String rowId;
    private List<Cell> cell = new ArrayList<>();
    private Constraint constraint;

    public Row(Worksheet worksheet, String rowId) {
        super(worksheet, rowId);
        this.rowId = rowId + 1;
    }

    public void addCell(Cell cell){
        this.cell.add(cell);
    }

    public String getRowId() {
        return rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    public List<Cell> getCell() {
        return cell;
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public void setConstraint(Constraint constraint) {
        this.constraint = constraint;
    }
}