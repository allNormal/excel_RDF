package persistence.impl;

import entity.ValueType.BasicFormula;
import entity.ValueType.Formula;
import entity.ValueType.NestedFormula;
import java.util.List;

import static entity.ValueType.FunctionType.BASIC;
import static entity.ValueType.FunctionType.NESTED;


//convert a string of formula into a Formula entity
public class FormulaConverter {

    private List<String> functionList;
    private String functions;
    private Formula formula;

    public FormulaConverter(String functions) {
        this.functions = functions;
        String[] result = functions.split("\\(", 2);
        String result2 = "";
        if(result.length>1) {
            if (result[1].charAt(result[1].length() - 1) == ')') {
                result2 = result[1].substring(0, result[1].length() - 1);
            } else {
                result2 = result[1].substring(0, result[1].length());
            }
        } else if(result.length == 1) {
            result2 = result[0];
        }
        FormulaSplitter splitter =  new FormulaSplitter(result2);
        this.functionList =  splitter.getResult();
        initializeFormula();
    }

    private String checkNested(String formula) {
        if(formula.split("\\(").length > 2) {
            return "Nested";
        }
        return "Basic";
    }

    private void initializeFormula() {
        if(checkNested(functions).equals("Basic")) {
            BasicFormula formula = new BasicFormula(functions);
            formula.setFunctionType(BASIC);
            this.formula = formula;
        } else {
            NestedFormula formula = new NestedFormula(functions);
            formula.setFunctionType(NESTED);
            for(int i = 0; i<this.functionList.size(); i++) {
                System.out.println(this.functionList.get(i));
                if(checkNested(this.functionList.get(i)) == "Basic") {
                    BasicFormula formula1 = new BasicFormula(this.functionList.get(i));
                    formula1.setFunctionType(BASIC);
                    formula.add(formula1);
                } else {
                    NestedFormula formulaTemp = new NestedFormula(this.functionList.get(i));
                    formulaTemp.setFunctionType(NESTED);
                    FormulaConverter formulaConverter = new FormulaConverter(this.functionList.get(i));
                    formulaTemp.add(formulaConverter.getFormula());
                    formula.add(formulaTemp);
                }
            }
            this.formula = formula;
        }
    }

    public Formula getFormula() {
        return formula;
    }
}
