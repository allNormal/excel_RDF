package com.java.fto.entity.EndpointEntity;

import com.java.fto.entity.Workbook.Workbook;

import java.util.List;

public class WorkbookEndpoint {
    private String workbookName;
    private List<FileItem> worksheets;

    public WorkbookEndpoint() {
    }

    public String getWorkbookName() {
        return workbookName;
    }

    public void setWorkbookName(String workbookName) {
        this.workbookName = workbookName;
    }

    public List<FileItem> getWorksheets() {
        return worksheets;
    }

    public void setWorksheets(List<FileItem> worksheets) {
        this.worksheets = worksheets;
    }
}
