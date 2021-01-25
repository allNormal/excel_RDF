package mapper;

import entity.Restriction.Restriction;

import java.io.IOException;

public interface ExcelReader {

    void readExcelConverter() throws IOException;

    void readExcelConverterWithRestriction(Restriction restriction) throws IOException;
}
