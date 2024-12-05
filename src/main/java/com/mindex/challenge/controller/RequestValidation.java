package com.mindex.challenge.controller;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.mindex.challenge.data.Compensation;

@Component
public class RequestValidation {

	 public boolean validateRequestGetById(String id) {
	    	
	    	//check that id exists and is actually a UUID
	    	if (id == null || id.isBlank()) {
	    		return false;
	    	}
	    	
	    	if(!isStringUUID(id)) {
	    		return false;
	    	}
	    	
	    	return true;
	    }
	    
	 
	    private boolean isStringUUID (String id) {
	    	
	    	//check that string is valid UUID
	    	if (id.length() != 36) {
	    		return false;
	    	}
	    	
	    	try {
	    		//throws exception if not valid
	    		UUID uuid = UUID.fromString(id);
	    	}catch(Exception e) {
	    		return false;
	    	}
	    	
	    	return true;
	    }
	    
	    public boolean validateRequestCreateCompensation(Compensation compensation) {
	    		    	
	    	if (compensation.getEmployeeId() == null  || compensation.getEmployeeId().isBlank()
	    			|| compensation.getEffectiveDate() == null || compensation.getEffectiveDate().isBlank()
	    			|| compensation.getSalary() == 0) {
	    		return false;
	    	}
	    	
	    	if(!isStringUUID(compensation.getEmployeeId())) {
	    		return false;
	    	}
	    	
	    	return true;
	    }
}
