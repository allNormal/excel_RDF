package persistence;

import entity.Operator;
import entity.SheetElement.ElementType;

import java.util.Collection;

public interface OntologyDao {

    public void create(String filepath);

    public Collection<String> getCellDependencies(String cellID, String worksheetName);

    public Collection<String> addConstraint(ElementType type, String typeID, String worksheetName,
                                            Operator operator, String value);

}
