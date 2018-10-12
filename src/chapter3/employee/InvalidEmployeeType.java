package chapter3.employee;

/**
 * @author tim.han
 */
public class InvalidEmployeeType extends Exception {
    private String type;

    public InvalidEmployeeType(String type) {
        this.type = type;
    }
}
