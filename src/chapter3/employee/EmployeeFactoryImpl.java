package chapter3.employee;

import chapter3.employee.model.CommissionedEmployee;
import chapter3.employee.model.Employee;
import chapter3.employee.model.HourlyEmployee;
import chapter3.employee.model.SalariedEmployee;

/**
 * @author tim.han
 */
public class EmployeeFactoryImpl implements EmployeeFactory {

    @Override
    public Employee makeEmployee(EmployeeRecord r) throws InvalidEmployeeType {
        switch (r.type) {
            case "COMMISSIONED":
                return new CommissionedEmployee(r);
            case "HOURLY":
                return new HourlyEmployee(r);
            case "SALRAIED":
                return new SalariedEmployee(r);
            default:
                throw new InvalidEmployeeType(r.type);
        }
    }
}
