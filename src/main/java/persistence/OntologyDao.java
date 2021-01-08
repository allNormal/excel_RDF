package persistence;

import java.util.List;

public interface OntologyDao {

    public void create(String filepath);

    public List<String> getCellDependencies(String cell);

}
