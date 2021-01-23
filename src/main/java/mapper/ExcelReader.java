package mapper;

import java.io.File;
import java.io.IOException;

public interface ExcelReader {

    void readExcelConverter(File file) throws IOException;
}
