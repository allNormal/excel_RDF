package persistence;

public interface OntologyDao {

    public void create(String filepath);

    public void openGraphDb(String filepath);
}
