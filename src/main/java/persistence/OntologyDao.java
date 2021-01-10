package persistence;

import entity.Operator;
import entity.SheetElement.ElementType;

import java.util.Collection;

public interface OntologyDao {

    void create(String filepath);

    Collection<String> getCellDependencies(String cellID, String worksheetName);

    Collection<String> addConstraint(ElementType type, String typeID, String worksheetName,
                                            Operator operator, String value);

    Collection<String> getReverseDependencies(String cellID, String worksheetName);
}
