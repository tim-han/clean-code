package chapter14.after;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static chapter14.after.ArgsException.ErrorCode.MISSING_STRING;

public class StringArrayArgumentMarshaler implements ArgumentMarshaler {
    private String[] stringArrayValue = new String[100];

    @Override
    public void set(Iterator<String> currentArgument) throws ArgsException {
        String parameter = null;
        try {
            parameter = currentArgument.next();
            stringArrayValue = parameter.split(" ");
        } catch (NoSuchElementException e) {
            throw new ArgsException(MISSING_STRING);
        }
    }

    public static String[] getValue(ArgumentMarshaler am) {
        if (am instanceof StringArrayArgumentMarshaler)
            return ((StringArrayArgumentMarshaler) am).stringArrayValue;
        else
            return new String[100];
    }
}
