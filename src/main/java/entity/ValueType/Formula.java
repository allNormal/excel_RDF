package entity.ValueType;

public class Formula {
    private String formulaFunction;
    private String formulaInput;

    public Formula(String formula) {
        this.formulaFunction = formula;
    }

    public String getFormulaFunction() {
        return formulaFunction;
    }

    public String getFormulaInput() {
        return formulaInput;
    }
}
