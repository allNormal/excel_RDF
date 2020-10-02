package excel.SheetElement.BasicElement;

import excel.Constraints.Constraint;
import excel.ValueType.ErrorValue;
import excel.ValueType.Formula;
import excel.ValueType.Value;
import excel.Worksheet.Worksheet;

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

    public Cell(Worksheet worksheet, String column, int row, Value value){
        super(worksheet);
        this.column = column;
        this.row = row;
        this.value = value;
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
        String pattern1 = "[[a-zA-Z]+[\\d]*]([[a-zA-Z]+[\\d]+:]+[[a-zA-Z]+[\\d]+])";
        String pattern2 = "[[[a-zA-Z]+[\\d]+]\\p{Punct}]+[[a-zA-Z]+[\\d]+]";
        boolean checkWithPattern1 = Pattern.matches(pattern1, value);
        boolean checkWithPattern2 = Pattern.matches(pattern2, value);
        try{
            if(this.value == Value.FORMULA){
                if(checkWithPattern1 || checkWithPattern2){
                    this.formulaValue = new Formula(value);
                }
                else{
                    throw new IllegalArgumentException("Value is not a type of Formula");
                }
            }
            else{
                throw new IllegalArgumentException("Cell Values is not a Formula but a " + this.value);
            }
        } catch (IllegalArgumentException err){
            System.out.println(err);
        }
    }

    public void SetErrorValue(String value, String description){
        String[] excelError = {"#DIV/0!", "#GETTING_DATA", "#N/A", "#NAME?", "#NULL!", "#NUM!", "#REF!", "#VALUE!"};
        try{
            if(this.value == Value.ERROR){
                if(Arrays.stream(excelError).anyMatch(value::equals)){
                    this.error = new ErrorValue(value, description);
                }
                else{
                    throw new IllegalArgumentException("Value is not a type of Error");
                }
            }
            else{
                throw new IllegalArgumentException("Cell Values is not an Error but a " + this.value);
            }
        } catch (IllegalArgumentException err){
            System.out.println(err);
        }
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
}
