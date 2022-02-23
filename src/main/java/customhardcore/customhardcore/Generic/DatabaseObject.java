package customhardcore.customhardcore.Generic;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Objects that extend this will be used by ConnectionManager to create tables, and subsequently columns
 * in the database
 **/

@SuppressWarnings("unused")
public abstract class DatabaseObject {

    /**
     * This function will return the transient fields, the fields you don't want to be turned into columns
     **/
    public abstract List<Field> getIgnoredFields();

}
