package chapter3.employee.model;

import chapter3.employee.Money;

/**
 * @author tim.han
 */
public abstract class Employee {
    public abstract boolean isPayday();
    public abstract Money calculatePay();
    public abstract void deliverPay(Money pay);
}
