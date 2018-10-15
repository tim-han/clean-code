package chapter10.afterrefactoring;

import java.io.PrintStream;

/**
 * @author tim.han
 */
public class RowColumnPagePrint {
    private int rowsPerPage;
    private int columnsPerPage;
    private int numbersPerPage;
    private String pageHeader;
    private PrintStream printStream;

    public RowColumnPagePrint(int rowsPerPage, int columnsPerPage, String pageHeader) {
        this.rowsPerPage = rowsPerPage;
        this.columnsPerPage = columnsPerPage;
        this.pageHeader = pageHeader;
        numbersPerPage = rowsPerPage * columnsPerPage;
        printStream = System.out;
    }

}
