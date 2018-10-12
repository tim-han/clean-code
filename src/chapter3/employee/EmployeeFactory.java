package chapter3.employee;

import chapter3.employee.model.Employee;

/**
 * @author tim.han
 */
public interface EmployeeFactory {
    Employee makeEmployee(EmployeeRecord r) throws InvalidEmployeeType;
}
