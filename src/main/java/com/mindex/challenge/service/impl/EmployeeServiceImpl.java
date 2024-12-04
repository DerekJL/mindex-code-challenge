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
    	LOG.debug("Reading employee with id [{}] for reporting-structure", id);
    	Employee employee = employeeRepository.findByEmployeeId(id);
    	ReportingStructure reportingStructure = getTotalDirectReports(employee);                   
        return reportingStructure;
	}
    
    /*go through all the employee's direct and indirect reports to find the numberOfReports and 
	 * also populate all the employee objects with data which are being returned in the response*/ 
    private ReportingStructure getTotalDirectReports(Employee employee) {        
    	if (employee == null || employee.getDirectReports() == null) {
            return new ReportingStructure(employee, 0);
        }

        List<Employee> employees = new ArrayList<>();
        employees.add(employee); 

    	int numberOfReports = 0;
    	
        while (!employees.isEmpty()) {
            /* Process the first employee in the arraylist and add their direct reports to the arraylist for processing next.  
             * Update their data to be used for the response payload as we're iterating through them to count the total numberOfReports.*/
            Employee updatedEmployee = employees.remove(0); 
            
            if (updatedEmployee.getDirectReports() != null) {           	
            	List<Employee> updatedDirectReports = new ArrayList<Employee>();
            	
                for (Employee directReport : updatedEmployee.getDirectReports()) {             	
                	//Count this direct report
                	numberOfReports++; 
                    //pull data for this direct report 
                	LOG.debug("Reading directReport with id [{}] for reporting-structure", directReport.getEmployeeId());
                    directReport = employeeRepository.findByEmployeeId(directReport.getEmployeeId());
                    //add the updated direct report object to a list to use for updating this direct report in the response payload employee object
                    updatedDirectReports.add(directReport);
                    //Add this data to the arraylist for processing
                    employees.add(directReport);                 
                }
                //update this employees direct reports for the response employee object
                updatedEmployee.setDirectReports(updatedDirectReports);               
            }
        }
        return new ReportingStructure(employee, numberOfReports);
    }
    
    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

}
