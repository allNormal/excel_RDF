package com.java.fto.mapper.ExcelMapper;

import com.java.fto.entity.Restriction.Restriction;

import java.io.IOException;

public interface ExcelReader {

    void readExcelConverter() throws IOException;

    //void readExcelConverterWithRestriction(Restriction restriction) throws IOException;
}
