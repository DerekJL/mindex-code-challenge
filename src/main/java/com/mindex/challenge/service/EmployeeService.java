package com.mindex.challenge.service;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.exception.CompensationAlreadyExistsException;

public interface EmployeeService {
    Employee create(Employee employee);
    Compensation createCompensation (Compensation compensation) throws CompensationAlreadyExistsException;
    Employee read(String id);
    Compensation readCompensation(String id);
    ReportingStructure readReportingStructure(String id);
    Employee update(Employee employee);
}
