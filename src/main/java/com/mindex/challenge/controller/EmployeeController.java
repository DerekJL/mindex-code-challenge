package com.mindex.challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.exception.CompensationAlreadyExistsException;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeController {
    private static final Logger LOG = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;
    
    @Autowired
    private RequestValidation requestValidation;
    
    @PostMapping("/employee")
    public Employee create(@RequestBody Employee employee) {
        LOG.debug("Received employee create request for [{}]", employee);

        return employeeService.create(employee);
    }
    
    @GetMapping("/employee/{id}")
    public Employee read(@PathVariable String id) {
        LOG.debug("Received employee read request for id [{}]", id);

        return employeeService.read(id);
    }
    
    @PutMapping("/employee/{id}")
    public Employee update(@PathVariable String id, @RequestBody Employee employee) {
        LOG.debug("Received employee update request for id [{}] and employee [{}]", id, employee);

        employee.setEmployeeId(id);
        return employeeService.update(employee);
    }
    
    @GetMapping("/compensation/{id}")
    public ResponseEntity<Object> readCompensation(@PathVariable String id) {
    	LOG.debug("Received compensation read request for id [{}]", id);
    	
    	//validate that request contains valid employeeId
    	if(!requestValidation.validateRequestGetById(id)) {
    		LOG.debug("Compensation Read request returned invalid request for id [{}]", id);
   		
    		return new ResponseEntity<>(createJsonObject("Invalid Request"), HttpStatus.BAD_REQUEST);
    	}
    	
    	Compensation compensation = null;
    	try{
    		compensation = employeeService.readCompensation(id);
    	}catch(Exception e) {
    		LOG.error("Error reading compensation with employeeId [{}] - ", id, e);
    		return new ResponseEntity<>(createJsonObject("Error reading compensation"), HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    	
		if (compensation == null) {
	        LOG.debug("Compensation read request returned NOT FOUND for id [{}]", id);
			return new ResponseEntity<>(createJsonObject("Compensation not found"), HttpStatus.NOT_FOUND);
        }
		
		LOG.debug("Compensation read request successfully returned response payload for id [{}]", id);
    	return new ResponseEntity<>(compensation, HttpStatus.OK);
    }
    
    @PostMapping("/compensation")
    public ResponseEntity<Object> create(@RequestBody Compensation compensation) {
    	LOG.debug("Received compensation create request for [{}]", compensation);
    	
    	if(!requestValidation.validateRequestCreateCompensation(compensation)){
    		LOG.debug("Compensation Create request returned invalid request for compensation [{}]", compensation);
    		return new ResponseEntity<>(createJsonObject("Invalid Request"), HttpStatus.BAD_REQUEST);
    	}
    	
    	Compensation response = new Compensation();
    	try {
    		response = employeeService.createCompensation(compensation);
    	}catch(CompensationAlreadyExistsException caee) {
    		LOG.debug("Compensation already exists for id [{}] - ", compensation.getEmployeeId(), caee);
    		return new ResponseEntity<>(createJsonObject("Compensation already exists for employeeId " + compensation.getEmployeeId()), HttpStatus.OK);
    	}catch(Exception e) {
    		LOG.error("Error creating compensation [{}] - ", compensation, e);
    		return new ResponseEntity<>(createJsonObject("Error creating compensation"), HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    	
    	if (response == null) {
        	LOG.error("Error creating compensation for id [{}]", compensation.getEmployeeId());
			return new ResponseEntity<>(createJsonObject("Error creating compensation for employeeId " + compensation.getEmployeeId()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    	
    	LOG.debug("Compensation Create request successfully returned response payload for id [{}]", compensation.getEmployeeId());
    	return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/reporting-structure/{id}")
    public ResponseEntity<Object> readReportingStructure (@PathVariable String id) {
        LOG.debug("Received reporting-structure read request for id [{}]", id);

        //validate that request contains valid employeeId
    	if(!requestValidation.validateRequestGetById(id)) {
    		LOG.debug("reporting-structure Read request returned invalid request for id [{}]", id);
    		return new ResponseEntity<>(createJsonObject("Invalid Request"), HttpStatus.BAD_REQUEST);
    	}
    	
        ReportingStructure reportingStructure = null;      
        try {
        	reportingStructure = employeeService.readReportingStructure(id);
        }catch(Exception e) {
        	LOG.error("Error reading reporting structure for id [{}] ", id, e);
        	return new ResponseEntity<>(createJsonObject("Error reading reporting structure"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        if (reportingStructure == null || reportingStructure.getEmployee() == null) {
            LOG.debug("reporting-structure Read request returned NOT FOUND for id [{}]", id);
        	return new ResponseEntity<>(createJsonObject("Reporting Structure not found"), HttpStatus.NOT_FOUND);
        }
		
        LOG.debug("reporting-structure request successfully returned response payload for id [{}]", id);
        return new ResponseEntity<>(reportingStructure, HttpStatus.OK);
    }
    
    public ObjectNode createJsonObject (String string){
   	 ObjectMapper mapper = new ObjectMapper();
   	 ObjectNode jsonObject = mapper.createObjectNode();
   	 jsonObject.put("message", string);
   	 return jsonObject;
   }
    
}
