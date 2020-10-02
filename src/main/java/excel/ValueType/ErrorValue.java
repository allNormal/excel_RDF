package excel.ValueType;

public class ErrorValue {
    private String description;
    private String errorName;

    public ErrorValue(String description, String errorName){
        this.description = description;
        this.errorName = errorName;
    }

    public String getDescription() {
        return description;
    }

    public String getErrorName() {
        return errorName;
    }
}
