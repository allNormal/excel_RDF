package service;

import entity.Operator;
import entity.SheetElement.ElementType;

import java.util.Collection;

public interface OntologyService {

    void create(String filepath);

    Collection<String> getDependency(String cellID, String worksheetName);

    Collection<String> addConstraint(ElementType type, String typeID, String worksheetName,
                                                              Operator operator, String value);

    Collection<String> getReverseDependency(String cellID, String worksheetName);

}
