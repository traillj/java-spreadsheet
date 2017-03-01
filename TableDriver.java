/*
 * Java Spreadsheet
 * Author: traillj
 */

/**
 * Driver for the table application.
 * The system allows for table data to be easily presented,
 * searched, edited, and saved. Restricts deleting rows
 * adding columns and editing column names.
 */
public class TableDriver {
    
    /** Path to the CSV file. */
    private static final String FILEPATH = "inventory.csv";
    /** The delimiter used by the CSV file. */
    private static final String DELIM = ",";
    
    public static void main(String[] args) {
        
        TableUI window = new TableUI(FILEPATH, DELIM);
        window.setVisible(true);
    }
}
