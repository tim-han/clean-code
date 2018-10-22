package chapter14.before;

import chapter14.after.ArgsException;

import java.text.ParseException;
import java.util.*;

public class Args {
    private String schema;
    private String[] args;
    private boolean valid = true;
    private Set<Character> unexpectedArguments = new TreeSet<>();
    private Map<Character, ArgumentMarshaler> marshalers = new HashMap<>();
    private Set<Character> argsFound = new HashSet<>();
    private int currentArgument;
    private char errorArgumnetId = '\0';
    private String errorParameter = "TILT";
    private ErrorCode errorCode = ErrorCode.OK;


    enum ErrorCode {
        OK, MISSNIG_STRING, MISSING_INTEGER, INVALID_INTEGER, UNEXPECTED_ARGUMENT
    }

    public Args(String schema, String[] args) throws ParseException, ArgsException {
        this.schema = schema;
        this.args = args;
        valid = parse();
    }

    private boolean parse() throws ParseException {
        if (schema.length() == 0 && args.length == 0)
            return true;
        parseSchema();
        try {
            parseArguments();
        } catch (ArgsException e) {

        }
        return valid;
    }

    private boolean parseSchema() throws ParseException {
        for (String element : schema.split(",")) {
            if (element.length() > 0) {
                String trimmedElement = element.trim();
                parseSchemaElement(trimmedElement);
            }
        }
        return true;
    }

    private void parseSchemaElement(String element) throws ParseException {
        char elementId = element.charAt(0);
        String elementTail = element.substring(1);
        validateSchemaElementId(elementId);
        if (isBooleanSchemaElement(elementTail))
            marshalers.put(elementId, new BooleanArgumentMarshaler());
        else if (isStringSchemaElement(elementTail))
            marshalers.put(elementId, new StringArgumentMarshaler());
        else if (isIntSchemaElement(elementTail))
            marshalers.put(elementId, new IntegerArgumentMarshaler());
        else {
            throw new ParseException(String.format(
                    "ArgumentL %c has invalid format: %s.", elementId, elementTail), 0
            );
        }
    }

    private void validateSchemaElementId(char elementId) throws ParseException {
        if (!Character.isLetter(elementId)) {
            throw new ParseException("Bad character: " + elementId + "in Args format: " + schema, 0);
        }
    }

    private boolean isStringSchemaElement(String elementTail) {
        return elementTail.equals("*");
    }

    private boolean isBooleanSchemaElement(String elementTail) {
        return elementTail.length() == 0;
    }

    private boolean isIntSchemaElement(String elementTail) {
        return elementTail.equals("#");
    }

    private boolean parseArguments() throws ArgsException {
        for (currentArgument = 0; currentArgument < args.length; currentArgument++) {
            parseArgument(args[currentArgument]);
        }
        return true;
    }

    private void parseArgument(String arg) throws ArgsException {
        if (arg.startsWith("-"))
            parseElements(arg);

    }

    private void parseElements(String arg) throws ArgsException {
        for (int i = 1; i < arg.length(); i++)
            parseElement(arg.charAt(i));
    }

    private void parseElement(char argChar) throws ArgsException {
        if (setArgument(argChar))
            argsFound.add(argChar);
        else {
            valid = false;
            errorCode = ErrorCode.UNEXPECTED_ARGUMENT;
            unexpectedArguments.add(argChar);
        }
    }

    private boolean setArgument(char argChar) throws ArgsException {
        ArgumentMarshaler m = marshalers.get(argChar);

        try {
            if (m instanceof BooleanArgumentMarshaler)
                setBooleanArg(m);
            else if (m instanceof StringArgumentMarshaler)
                setStringArg(m);
            else if (m instanceof IntegerArgumentMarshaler)
                setIntArg(m);
            else
                return false;
        } catch (ArgsException e) {
            valid = false;
            errorArgumnetId = argChar;
            throw e;
        }

        return true;
    }

    private void setIntArg(ArgumentMarshaler m) throws ArgsException {
        currentArgument++;
        String parameter = null;
        try {
            parameter = args[currentArgument];
            m.set(parameter);
        } catch (ArrayIndexOutOfBoundsException e) {
            errorCode = ErrorCode.MISSING_INTEGER;
            throw new ArgsException();
        } catch (ArgsException e) {
            errorParameter = parameter;
            errorCode = ErrorCode.MISSING_INTEGER;
            throw e;
        }
    }

    private void setStringArg(ArgumentMarshaler m) throws ArgsException {
        currentArgument++;
        try {
            m.set(args[currentArgument]);
        } catch (ArrayIndexOutOfBoundsException e) {
            errorCode = ErrorCode.MISSNIG_STRING;
            throw new ArgsException();
        }
    }

    private void setBooleanArg(ArgumentMarshaler m) {
        try {
            m.set("true");
        } catch (ArgsException e) {
        }
    }

    public int cardinality() {
        return argsFound.size();
    }

    public String usage() {
        if (schema.length() > 0)
            return "-[" + schema + "]";
        else
            return "";
    }

    public String errorMessage() throws Exception {
        if (unexpectedArguments.size() > 0) {
            return unexpectedArgumentMessage();
        } else
            switch (errorCode) {
                case OK:
                    throw new Exception("TILT: Should not get here.");
                case UNEXPECTED_ARGUMENT:
                    return unexpectedArgumentMessage();
                case MISSNIG_STRING:
                    return String.format("Could not find string parameter for -%c.", errorArgumnetId);
                case MISSING_INTEGER:
                    return String.format("Could not find integer parameter for -%c.", errorArgumnetId);
            }

        return "";
    }

    private String unexpectedArgumentMessage() {
        StringBuffer message = new StringBuffer("Argument(s) -");
        for (char c : unexpectedArguments) {
            message.append(c);
        }
        message.append(" unexpected.");

        return message.toString();
    }


    public boolean getBoolean(char arg) {
        ArgumentMarshaler am = marshalers.get(arg);
        boolean b = false;
        try {
            b = am != null && (Boolean) am.get();
        } catch (ClassCastException e) {
            b = false;
        }
        return b;
    }

    public String getString(char arg) {
        ArgumentMarshaler am = marshalers.get(arg);
        try {
            return am == null ? "" : (String) am.get();
        } catch (ClassCastException e) {
            return "";
        }
    }

    public int getInt(char arg) {
        ArgumentMarshaler am = marshalers.get(arg);
        try {
            return am == null ? 0 : (Integer) am.get();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean has(char arg) {
        return argsFound.contains(arg);
    }

    public boolean isValid() {
        return valid;
    }

    private abstract class ArgumentMarshaler {
        public abstract void set(String s) throws ArgsException;

        public abstract Object get();
    }

    private class BooleanArgumentMarshaler extends ArgumentMarshaler {
        private boolean booleanValue = false;

        public void set(String s) {
            booleanValue = true;
        }

        public Object get() {
            return booleanValue;
        }
    }

    private class StringArgumentMarshaler extends ArgumentMarshaler {
        private String stringVale;

        @Override
        public void set(String s) {
            stringVale = s;
        }

        @Override
        public Object get() {
            return stringVale;
        }
    }

    private class IntegerArgumentMarshaler extends ArgumentMarshaler {
        private int intValue = 0;

        @Override
        public void set(String s) throws ArgsException {
            try {
                intValue = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                throw new ArgsException();
            }
        }

        @Override
        public Object get() {
            return intValue;
        }
    }
}




