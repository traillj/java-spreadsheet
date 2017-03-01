/*
 * Java Spreadsheet
 * Author: traillj
 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * A simplified model for table data. Reads the data from a CSV file.
 * Allows rows to be added and edited, but not the number and name of
 * columns. Controls whether the save button is enabled. Provides
 * functions to filter based on text and save the data as a CSV file.
 */
public class SimpleTableModel {
    
    /** The model for table data. */
    private DefaultTableModel model;
    
    /** The button that saves the data. */
    private JButton saveButton;
    
    /**
     * Constructs a SimpleTableModel object.
     * @param filepath path to the CSV file
     * @param delim the delimiter used by the CSV file
     * @param saveButton the button that saves the data
     * @throws FileNotFoundException thrown if the file cannot be found
     * @throws IOException thrown if the file cannot be opened
     */
    public SimpleTableModel(String filepath, String delim, JButton saveButton)
            throws FileNotFoundException, IOException {
        
        this.saveButton = saveButton;
        saveButton.setEnabled(false);
        
        model = new DefaultTableModel();
        openCsv(filepath, delim);
        model.addTableModelListener(new ModelListener());
    }
    
    /**
     * Opens a CSV file and reads the data into the model.
     * @param filepath path to the CSV file
     * @param delim the delimiter used by the CSV file
     * @throws FileNotFoundException thrown if file file cannot be found
     * @throws IOException thrown if the file cannot be opened
     */
    private void openCsv(String filepath, String delim)
            throws FileNotFoundException, IOException {
        Scanner file = new Scanner(new FileInputStream(filepath));
        
        if (file.hasNext()) {
            String line = file.nextLine();
            String[] headings = line.split(delim);
            for (String heading : headings) {
                model.addColumn(heading);
            }
        }
        
        while (file.hasNext()) {
            String line = file.nextLine();
            String[] columns = line.split(delim);
            model.addRow(columns);
        }
    }
    
    /**
     * Creates a Swing table.
     * @return a JTable
     */
    public JTable createTable() {
        return new JTable(model);
    }
    
    /**
     * Sets a filter on the table. Only rows that contain the
     * regular expression in the text field are displayed. 
     * @param table a Swing table
     * @param textField a Swing text field
     * @return the table with a row filter
     */
    public JTable setFilter(JTable table, JTextField textField) {
        
        RowFilter<DefaultTableModel, Object> rf = null;
        // Don't update if invalid expression
        try {
            rf = RowFilter.regexFilter(textField.getText());
        } catch (java.util.regex.PatternSyntaxException e) {
            return table;
        }
        
        TableRowSorter<DefaultTableModel> sorter =
                new TableRowSorter<DefaultTableModel>(model);
        sorter.setRowFilter(rf);
        table.setRowSorter(sorter);
        return table;
    }
    
    /**
     * Adds a blank row to the end of the model.
     */
    public void addEmptyRow() {
        String[] emptyRow = new String[model.getColumnCount()];
        model.addRow(emptyRow);
    }
    
    
    /**
     * Saves the model to a CSV. Disables the save button until the model
     * is next changed.
     * @param filepath path to the CSV file
     * @param delim the delimiter to be used by the CSV file
     * @throws FileNotFoundException thrown if the file cannot be created
     */
    public void saveModel(String filepath, String delim)
            throws FileNotFoundException {
        
        PrintWriter outputStream = null;
        outputStream = new PrintWriter(new FileOutputStream(filepath));
        
        outputStream.println(getCsv(delim));
        outputStream.close();
        
        saveButton.setEnabled(false);
    }
    
    /**
     * Creates a string in CSV format with the specified delimiter.
     * @param delim the delimiter
     * @return a string in CSV format
     */
    private String getCsv(String delim) {
        StringBuilder csv = new StringBuilder(getColumnNames(delim));
        String newline = System.getProperty("line.separator");
        int numRows = model.getRowCount();
        
        for (int i = 0; i < numRows; i++) {
            csv.append(newline + getRow(delim, i));
        }
        return csv.toString();
    }
    
    /**
     * Gets the column names, separated by the specified delimiter.
     * @param delim the delimiter
     * @return the column names
     */
    private StringBuilder getColumnNames(String delim) {
        StringBuilder columnNames =
                new StringBuilder(model.getColumnName(0));
        int numColumns = model.getColumnCount();
        
        for (int i = 1; i < numColumns; i++) {
            columnNames.append(delim + model.getColumnName(i));
        }
        return columnNames;
    }
    
    /**
     * Gets a row of the model, delimited as specified.
     * @param delim the delimiter
     * @param index the row to return
     * @return row at the index
     */
    private StringBuilder getRow(String delim, int index) {
        StringBuilder row =
                new StringBuilder(getStringAt(index, 0));
        
        int numColumns = model.getColumnCount();
        for (int i = 1; i < numColumns; i++) {
            row.append(delim + getStringAt(index, i));
        }
        return row;
    }
    
    /**
     * Gets the string at the specified row and column in the model.
     * @param row row index in the model
     * @param column column index in the model
     * @return string at position
     */
    private String getStringAt(int row, int column) {
        if (model.getValueAt(row, column) == null) {
            return "";
        }
        return model.getValueAt(row, column).toString();
    }
    
    
    /**
     * Listens for changes to the model. Enables the save button
     * when a change occurs.
     */
    private class ModelListener implements TableModelListener {
        @Override
        public void tableChanged(TableModelEvent e) {
            saveButton.setEnabled(true);
        }
    }
}
