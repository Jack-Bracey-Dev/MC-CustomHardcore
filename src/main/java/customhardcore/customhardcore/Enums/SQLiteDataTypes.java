package customhardcore.customhardcore.Enums;

import customhardcore.customhardcore.Helpers.Logger;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public enum SQLiteDataTypes {

    /** The accepted values need to be lower case **/
    INTEGER("INTEGER", Arrays.asList("int", "integer")),
    STRING("TEXT", Collections.singletonList("string")),
    BLOB("BLOB", Collections.emptyList()),
    DOUBLE("REAL", Arrays.asList("double", "bigint", "biginteger")),
    NUMBER("NUMERIC", Arrays.asList("boolean", "date", "datetime", "timestamp"));

    private String returnedValue;
    private List<String> acceptedValues;

    SQLiteDataTypes(String returnedValue, List<String> acceptedValues) {
        this.returnedValue = returnedValue;
        this.acceptedValues = acceptedValues;
    }

    public String getReturnedValue() {
        return returnedValue;
    }

    public void setReturnedValue(String returnedValue) {
        this.returnedValue = returnedValue;
    }

    public List<String> getAcceptedValues() {
        return acceptedValues;
    }

    public void setAcceptedValues(List<String> acceptedValues) {
        this.acceptedValues = acceptedValues;
    }

    public static String getSqlLiteDataTypeFromField(Field field) {
        String currentField = field.getType().getSimpleName();
        Optional<SQLiteDataTypes> optionalType = Arrays.stream(SQLiteDataTypes.values())
                .filter(t -> t.getAcceptedValues().contains(currentField.toLowerCase()))
                .findFirst();
        if (!optionalType.isPresent()) {
            Logger.error(SQLiteDataTypes.class, String.format("failed to find type for %s", currentField));
            return null;
        }
        return optionalType.get().toString();
    }
}
