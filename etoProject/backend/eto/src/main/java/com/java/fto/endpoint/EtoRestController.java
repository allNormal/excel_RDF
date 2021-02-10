package com.java.fto.endpoint;

import com.java.fto.entity.EndpointEntity.FileUploadEntity;
import com.java.fto.entity.EndpointEntity.Receiver.WorksheetReceiver;
import com.java.fto.entity.EndpointEntity.WorkbookEndpoint;
import com.java.fto.entity.EndpointEntity.Receiver.WorksheetTableReceiver;
import org.springframework.ui.ModelMap;

import java.util.Collection;
import java.util.List;

public interface EtoRestController {

    void createAuto(FileUploadEntity fileUploadEntity, ModelMap map);

    void createCustom(List<WorksheetReceiver> ws, String formatType);

    void initializeCustom(FileUploadEntity fileUploadEntity, ModelMap map);

    void initializeRowColumnHeader(List<WorksheetReceiver> ws);

    Collection<WorkbookEndpoint> getWorkbook(String formatType);

}
