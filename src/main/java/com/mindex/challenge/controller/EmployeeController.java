package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    @PostMapping("/employee")
    public Employee create(@RequestBody Employee employee) {
        LOG.debug("Received employee create request for [{}]", employee);

        return employeeService.create(employee);
    }
    
    @PostMapping("/compensation")
    public Compensation create(@RequestBody Compensation compensation) {
    	LOG.debug("Received compensation create request for [{}]", compensation);
    	
    	Compensation response = new Compensation();
    	try {
    		response = employeeService.create(compensation);
    	}catch(IllegalArgumentException iae) {
    		LOG.error("Invalid compensation create for compensation [{}] - ", compensation, iae);
    		throw new RuntimeException("Invalid compensation"); 
    	}catch(Exception e) {
    		LOG.error("Error creating compensation [{}] - ", compensation, e);
    		throw new RuntimeException("Error creating compensation"); 
    	}
    	return response;
    }

    @GetMapping("/employee/{id}")
    public Employee read(@PathVariable String id) {
        LOG.debug("Received employee read request for id [{}]", id);

        return employeeService.read(id);
    }
    
    @GetMapping("/compensation/{id}")
    public Compensation readCompensation(@PathVariable String id) {
    	LOG.debug("Received compensation read request for id [{}]", id);
    	
    	return employeeService.readCompensation(id);
    }
    
    @GetMapping("/reporting-structure/{id}")
    public ReportingStructure readReportingStructure (@PathVariable String id) {
        LOG.debug("Received reporting-structure read request for id [{}]", id);

        return employeeService.readReportingStructure(id);
    }

    @PutMapping("/employee/{id}")
    public Employee update(@PathVariable String id, @RequestBody Employee employee) {
        LOG.debug("Received employee update request for id [{}] and employee [{}]", id, employee);

        employee.setEmployeeId(id);
        return employeeService.update(employee);
    }
}
