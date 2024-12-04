package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Reading employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
	public ReportingStructure readReportingStructure(String id) {
    	LOG.debug("Reading employee with id [{}]", id);
    	Employee employee = employeeRepository.findByEmployeeId(id);
    	 
        //query to fully populate the employee objects in the directReports list, as the database only has employeeId for them
        List<Employee> updatedDirectReports = new ArrayList<Employee>();
    	for (int i = 0 ; i < employee.getDirectReports().size(); i++) {
        	Employee directReport = employeeRepository.findByEmployeeId(employee.getDirectReports().get(i).getEmployeeId());
        	updatedDirectReports.add(directReport);
        }
    	employee.setDirectReports(updatedDirectReports);
    	
    	//go through all the employee's direct reports to find the numberOfReports       
    	int numberOfReports = getTotalDirectReports(employee);       
        ReportingStructure reportingStructure = new ReportingStructure(employee, numberOfReports);
             
        return reportingStructure;
	}
    
    private int getTotalDirectReports(Employee employee) {
        if (employee == null || employee.getDirectReports() == null) {
            return 0;
        }
        
        // Count direct and indirect reports
        int reportsCount = 0;
        for (Employee directReport : employee.getDirectReports()) {
            reportsCount++;
            
            //query for data from indirect reports, as direct report data was already queried and will skip this if block
            directReport = employeeRepository.findByEmployeeId(directReport.getEmployeeId());
            

            // Recursively count the direct and indirect reports of the current employee
            int indirectReports = getTotalDirectReports(directReport);
            reportsCount += indirectReports;
        }

        return reportsCount;
    }
    
    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

}
