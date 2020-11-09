package entity.ValueType;


import entity.SheetElement.BasicElement.Cell;

import java.util.ArrayList;
import java.util.List;

public class Formula {
    private String formulaFunction;
    private String stringValue;
    private Boolean booleanValue;
    private List<Cell> cellDependency = new ArrayList<>();
    private Byte errorValue;
    private float numericValue;
    private Value value;

    public Formula(String formula) {
        this.formulaFunction = formula;
    }

    public void add(Cell cell) {
        cellDependency.add(cell);
    }

    public List<Cell> getCellDependency() {
        return cellDependency;
    }

    public String getFormulaFunction() {
        return formulaFunction;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public float getNumericValue() {
        return numericValue;
    }

    public void setNumericValue(float numericValue) {
        this.numericValue = numericValue;
    }

    public void setValue(Value value){
        this.value = value;
    }

    public Value getValue() {
        return value;
    }

    public Byte getErrorValue() {
        return errorValue;
    }

    public void setErrorValue(Byte errorValue) {
        this.errorValue = errorValue;
    }
}
