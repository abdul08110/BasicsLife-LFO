package com.hasbro.basicslife_lfo.pojo;

import org.json.JSONArray;

public class Employee {
    private String firstName;
    private String empCode;
    private JSONArray fullDetails;

    public Employee(String firstName, String empCode, JSONArray fullDetails) {
        this.firstName = firstName;
        this.empCode = empCode;
        this.fullDetails = fullDetails;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getEmpCode() {
        return empCode;
    }

    public JSONArray getFullDetails() {
        return fullDetails;
    }
}
