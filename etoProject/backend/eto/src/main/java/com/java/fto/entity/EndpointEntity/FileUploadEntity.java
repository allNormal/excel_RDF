package com.java.fto.entity.EndpointEntity;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadEntity {
    private String format;
    private MultipartFile file;
    private String columnHeader;
    private String rowHeader;

    public String getColumnHeader() {
        return columnHeader;
    }

    public void setColumnHeader(String columnHeader) {
        this.columnHeader = columnHeader;
    }

    public String getRowHeader() {
        return rowHeader;
    }

    public void setRowHeader(String rowHeader) {
        this.rowHeader = rowHeader;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
