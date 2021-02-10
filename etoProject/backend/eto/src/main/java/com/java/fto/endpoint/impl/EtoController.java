package com.java.fto.endpoint.impl;

import com.java.fto.endpoint.EtoRestController;
import com.java.fto.entity.EndpointEntity.FileUploadEntity;
import com.java.fto.entity.EndpointEntity.Receiver.WorksheetReceiver;
import com.java.fto.entity.EndpointEntity.WorkbookEndpoint;
import com.java.fto.entity.EndpointEntity.Receiver.WorksheetTableReceiver;
import com.java.fto.entity.Restriction.Restriction;
import com.java.fto.entity.Workbook.Workbook;
import com.java.fto.mapper.RestrictionMapper.restrictionMapper;
import com.java.fto.service.impl.OntologyService;
import com.java.fto.service.impl.OntologyServiceTableBased;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/")
public class EtoController implements EtoRestController {

    @Autowired
    private OntologyService ontologyService;
    @Autowired
    private OntologyServiceTableBased ontologyServiceTableBased;

    @Override
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, value = "automated",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void createAuto(@ModelAttribute FileUploadEntity fileUploadEntity, ModelMap map) {
        map.addAttribute("fileUploadEntity", fileUploadEntity);
        try {
            File file = new File(System.getProperty("java.io.tmpdir")+"/" + fileUploadEntity.getFile().getOriginalFilename());
            fileUploadEntity.getFile().transferTo(file);
            System.out.println(fileUploadEntity.getFile().getOriginalFilename());

            if(fileUploadEntity.getFormat().toLowerCase().contains("table")) {
                ontologyServiceTableBased.createAuto(file.getPath());
            } else {
                ontologyService.createAuto(file.getPath());
            }
            file.delete();

        } catch (IOException e) {
            System.out.println("exception " + e.getMessage());
        }
    }


    @Override
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, value = "custom/{formatType}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void createCustom(@RequestBody List<WorksheetReceiver> ws, @PathVariable("formatType") String formatType) {
        if(formatType.equals("table")) {
            restrictionMapper mapper = new restrictionMapper();
            Restriction restriction = mapper.payloadToRestriction(ws, formatType);
            this.ontologyServiceTableBased.createCustom(restriction);
        } else {
            restrictionMapper mapper = new restrictionMapper();
            Restriction restriction = mapper.payloadToRestriction(ws, formatType);
            this.ontologyService.createCustom(restriction);
        }

    }

    @Override
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, value = "custom/initialize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void initializeCustom(FileUploadEntity fileUploadEntity, ModelMap map) {
        map.addAttribute("fileUploadEntity", fileUploadEntity);
        try {
            File file = new File(System.getProperty("java.io.tmpdir")+"/" + fileUploadEntity.getFile().getOriginalFilename());
            fileUploadEntity.getFile().transferTo(file);
            System.out.println(fileUploadEntity.getFile().getOriginalFilename());

            if(fileUploadEntity.getFormat().toLowerCase().contains("table")) {
                ontologyServiceTableBased.initializeWorkbook(file.getPath());
                //ontologyServiceTableBased.createCustom(file.getPath());
            } else {
                ontologyService.initializeWorkbook(file.getPath());
                //ontologyService.createCustom(file.getPath());
            }
        } catch (IOException e) {
            System.out.println("exception " + e.getMessage());
        }
    }

    @Override
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, value = "custom/initialize/crheader", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void initializeRowColumnHeader(@RequestBody List<WorksheetReceiver> ws) {
        this.ontologyServiceTableBased.initializeColumnAndRow(ws);
    }

    @Override
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, value = "{formatType}/workbook")
    public Collection<WorkbookEndpoint> getWorkbook(@PathVariable("formatType") String formatType) {
        System.out.println("get");
        System.out.println(formatType);
        List<WorkbookEndpoint> result = new ArrayList<>();
        if(formatType.equals("table")) {
            result.add(this.ontologyServiceTableBased.getWorkbook());
        } else if(formatType.equals("file")) {
            result.add(this.ontologyService.getWorkbook());
        } else {
            return null;
        }
        return result;
    }


}
