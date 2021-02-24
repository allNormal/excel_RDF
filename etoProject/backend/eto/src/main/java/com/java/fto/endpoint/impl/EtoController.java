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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

@RestController
@CrossOrigin
@RequestMapping("/")
public class EtoController implements EtoRestController {

    @Autowired
    private OntologyService ontologyService;
    @Autowired
    private OntologyServiceTableBased ontologyServiceTableBased;
    private final Logger log = LoggerFactory.getLogger(EtoController.class);


    @Override
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, value = "automated",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void createAuto(@ModelAttribute FileUploadEntity fileUploadEntity, ModelMap map) {
        map.addAttribute("fileUploadEntity", fileUploadEntity);
        log.info("POST request to convert excel file into automated " + fileUploadEntity.getFormat() + " received");
        try {
            File file = new File(System.getProperty("java.io.tmpdir")+"/" + fileUploadEntity.getFile().getOriginalFilename());
            if(!file.exists()) {
                fileUploadEntity.getFile().transferTo(file);
            }
            log.info("sending request to the service");
            if(fileUploadEntity.getFormat().toLowerCase().contains("table")) {
                ontologyServiceTableBased.createAuto(file.getPath());
            } else {
                ontologyService.createAuto(file.getPath());
            }
            log.info("request done, deleting temp file");
            file.deleteOnExit();

        } catch (IOException e) {
            System.out.println("exception " + e.getMessage());
        }
    }


    @Override
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, value = "custom/{formatType}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void createCustom(@RequestBody List<WorksheetReceiver> ws, @PathVariable("formatType") String formatType) {
        log.info("POST request to convert excel file into automated " + formatType + " format received");
        log.info("sending request to the service");
        if(formatType.equals("table")) {
            restrictionMapper mapper = new restrictionMapper();
            Restriction restriction = mapper.payloadToRestriction(ws, formatType);
            this.ontologyServiceTableBased.createCustom(restriction);
        } else {
            restrictionMapper mapper = new restrictionMapper();
            Restriction restriction = mapper.payloadToRestriction(ws, formatType);
            this.ontologyService.createCustom(restriction);
        }
        log.info("request done");
    }

    @Override
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, value = "custom/initialize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void initializeCustom(FileUploadEntity fileUploadEntity, ModelMap map) {
        log.info("POST request to initialize the workbook received");
        map.addAttribute("fileUploadEntity", fileUploadEntity);
        try {
            File file = new File(System.getProperty("java.io.tmpdir")+"/" + fileUploadEntity.getFile().getOriginalFilename());
            if(!file.exists()) {
                fileUploadEntity.getFile().transferTo(file);
            }
            log.info("sending request to the service");
            if(fileUploadEntity.getFormat().toLowerCase().contains("table")) {
                ontologyServiceTableBased.initializeWorkbook(file.getPath());
                //ontologyServiceTableBased.createCustom(file.getPath());
            } else {
                ontologyService.initializeWorkbook(file.getPath());
                //ontologyService.createCustom(file.getPath());
            }
            file.deleteOnExit();
            log.info("request done");
        } catch (IOException e) {
            System.out.println("exception " + e.getMessage());
        }
    }

    @Override
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, value = "custom/initialize/crheader", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void initializeRowColumnHeader(@RequestBody List<WorksheetReceiver> ws) {
        log.info("POST request to initialize the row and column header in a workbook received");
        log.info("sending request to the service");
        this.ontologyServiceTableBased.initializeColumnAndRow(ws);
        log.info("request done");
    }

    @Override
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, value = "{formatType}/workbook")
    public Collection<WorkbookEndpoint> getWorkbook(@PathVariable("formatType") String formatType) {
        log.info("GET request to retrieve workbook information received");
        List<WorkbookEndpoint> result = new ArrayList<>();
        log.info("sending request to the service");
        if(formatType.equals("table")) {
            result.add(this.ontologyServiceTableBased.getWorkbook());
        } else if(formatType.equals("file")) {
            result.add(this.ontologyService.getWorkbook());
        } else {
            log.error(formatType + " not implemented yet");
            return null;
        }
        log.info("request done");
        return result;
    }

    @Override
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, value = "{formatType}/repositories")
    public List<String> getAllRepo(@PathVariable("formatType") String formatType) {
        log.info("GET request to retrieve all available repository in graphDB received");
        log.info("sending request to the service");
        List<String> result = new ArrayList<>();
        if(formatType.toLowerCase().contains("table")) {
            result = this.ontologyServiceTableBased.getAllRepository();
        } else if(formatType.toLowerCase().contains("file")) {
            result = this.ontologyService.getAllRepository();
        } else {
            log.error(formatType + " is not yet implemented");
            return null;
        }
        log.info("request done");
        return result;
    }

    @Override
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, value = "{formatType}/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addGraphIntoRepo(@PathVariable("formatType") String formatType, @RequestBody String repoName) {
        log.info("POST request to add graph into a repository received");
        log.info("sending request to the service");
        repoName = repoName.replaceAll("\"","");
        System.out.println(formatType);
        System.out.println(repoName);
        if(formatType.toLowerCase().contains("table")) {
            this.ontologyServiceTableBased.addGraphIntoRepo(repoName);
        } else if(formatType.toLowerCase().contains("file")) {
            this.ontologyService.addGraphIntoRepo(repoName);
        }
        log.info("request done");
    }

    @Override
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, value = "{formatType}/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void createRepoAndAddGraph(@PathVariable("formatType") String formatType, @RequestBody String repoName) {
        log.info("Post request to create a new repository and add graph into it received");
        log.info("sending request to the service");
        repoName = repoName.replaceAll("\"","");
        if(formatType.toLowerCase().contains("table")) {
            this.ontologyServiceTableBased.createRepoAndAddGraph(repoName);
        } else if(formatType.toLowerCase().contains("file")) {
            this.ontologyService.createRepoAndAddGraph(repoName);
        }
        log.info("request done");
    }


}
