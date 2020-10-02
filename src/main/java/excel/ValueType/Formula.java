package excel.ValueType;

import java.util.ArrayList;
import java.util.List;

public class Formula {
    private String formulaFunction;
    private String formulaInput;

    public Formula(String formula) {
        String[] splitFormula = formula.split("\\(",2);
        formulaFunction = splitFormula[0];
        formulaInput = splitFormula[1].substring(0, splitFormula[1].length()-1);

    }

    public String getFormulaFunction() {
        return formulaFunction;
    }

    public String getFormulaInput() {
        return formulaInput;
    }
}
