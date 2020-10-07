package excel.SheetElement.BasicElement;

import excel.Constraints.Constraint;
import excel.ValueType.ErrorValue;
import excel.ValueType.Formula;
import excel.ValueType.Value;
import excel.Worksheet.Worksheet;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.xssf.usermodel.XSSFComment;

import java.util.Arrays;
import java.util.regex.Pattern;

public class Cell extends BasicElement {
    private String cellId;
    private int row;
    private String column;
    private CellStatus status;
    private String stringValue;
    private float numericValue;
    private boolean booleanValue;
    private Formula formulaValue;
    private ErrorValue error;
    private Constraint constraint;
    private Value value;
    private XSSFComment comment;

    public Cell(Worksheet worksheet, String column, int row){
        super(worksheet);
        this.column = convertColumn(column);
        this.row = row + 1;
        this.cellId = this.column + this.row;
    }

    private String convertColumn(String column) {
        String result = "";
        int min = 65;
        int temp = Integer.parseInt(column);
        boolean check = false;

        while(temp >= 0) {
            if(temp <= 25){
                check = true;
            }
            int calc = temp % 25;
            temp = temp/25;

            char character = (char) (min + calc);
            result = result + character;
            if(check){
                break;
            }
        }
        if(column == "0") System.out.println(result);
        return result;
    }

    public void setStringValue(String value){
        try{
            if(this.value == Value.STRING){
                stringValue = value;
            }
            else{
                throw new IllegalArgumentException("Cell Values is not a String but a " + this.value);
            }
        } catch (IllegalArgumentException err){
            System.out.println(err);
        }
    }

    public void setNumericValue(float value){
        try{
            if(this.value == Value.NUMERIC){
                numericValue = value;
            }
            else{
                throw new IllegalArgumentException("Cell Values is not a Numeric but a " + this.value);
            }
        } catch (IllegalArgumentException err){
            System.out.println(err);
        }
    }

    public void setBooleanValue(boolean value){
        try{
            if(this.value == Value.BOOLEAN){
                booleanValue = value;
            }
            else{
                throw new IllegalArgumentException("Cell Values is not a Boolean but a " + this.value);
            }
        } catch (IllegalArgumentException err){
            System.out.println(err);
        }
    }

    public void setFormulaValue(String value){
        this.formulaValue = new Formula(value);
    }

    public void SetErrorValue(String value){
        this.error = new ErrorValue(value);
    }

    public String getCellId() {
        return cellId;
    }

    public int getRow() {
        return row;
    }

    public String getColumn() {
        return column;
    }

    public CellStatus getStatus() {
        return status;
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public void setComment(XSSFComment comment) {
        this.comment = comment;
    }

    public String getStringValue() {
        return stringValue;
    }

    public float getNumericValue() {
        return numericValue;
    }

    public boolean isBooleanValue() {
        return booleanValue;
    }

    public Formula getFormulaValue() {
        return formulaValue;
    }

    public ErrorValue getError() {
        return error;
    }

    public XSSFComment getComment() {
        return comment;
    }

    public Value getValue() {
        return value;
    }
}
