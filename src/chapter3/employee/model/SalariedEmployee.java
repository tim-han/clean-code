package chapter3.employee.model;

import chapter3.employee.EmployeeRecord;
import chapter3.employee.Money;

/**
 * @author tim.han
 */
public class SalariedEmployee extends Employee {

    public SalariedEmployee(EmployeeRecord r) {
        super();
    }

    @Override
    public boolean isPayday() {
        return false;
    }

    @Override
    public Money calculatePay() {
        return null;
    }

    @Override
    public void deliverPay(Money pay) {

    }
}
