/*
 * Java Spreadsheet
 * Author: traillj
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * The UI for the table system.
 */
public class TableUI extends JFrame {
    
    /** The width of the window. */
    private static final int WIDTH = 800;
    /** The height of the window. */
    private static final int HEIGHT = 600;
    /** The name of the window. */
    private static final String TITLE = "Table System";
    
    /** Path to the CSV file. */
    private String filepath;
    /** The delimiter used by the CSV file. */
    private String delim;
    
    /** The text used to filter the table. */
    private JTextField textField;
    
    /** The button to add a row to the table. */
    private JButton addButton;
    /** The button to save the table to a CSV file. */
    private JButton saveButton;
    
    /** The table to display the data. */
    private JTable table;
    /** The model to contain the data. */
    private SimpleTableModel tableModel;
    
    /**
     * Constructs a TableUI object.
     * @param filepath path to the CSV file
     * @param delim the delimiter used by the CSV file
     */
    public TableUI(String filepath, String delim) {
        this.filepath = filepath;
        this.delim = delim;
        display();
    }
    
    /**
     * Displays the Swing components on the window.
     */
    private void display() {
        setSize(WIDTH, HEIGHT);
        setTitle(TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        textField = new JTextField();
        textField.getDocument().addDocumentListener(new TextFieldListener());
        add(textField, BorderLayout.NORTH);
        
        displayButtons();
        displayTable();
    }
    
    /**
     * Adds the buttons to the bottom of the window.
     */
    private void displayButtons() {
        JPanel buttonPanel = new JPanel();
        
        addButton = new JButton("Add");
        addButton.addActionListener(new AddListener());
        buttonPanel.add(addButton);
        
        saveButton = new JButton("Save");
        saveButton.addActionListener(new SaveListener());
        buttonPanel.add(saveButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Adds the table with a scroll pane to the centre of the window.
     * Initialises the model. Assumes a save button and text field for
     * filtering the results exist.
     */
    private void displayTable() {
        try {
            tableModel = new SimpleTableModel(filepath, delim, saveButton);
        } catch (FileNotFoundException e) {
            System.err.println("File not found");
        } catch (IOException e) {
            System.err.println("IO Exception on file");
        }
        
        table = tableModel.createTable();
        table = tableModel.setFilter(table, textField);
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(250, 80));
        add(scroll, BorderLayout.CENTER);
    }
    
    
    /**
     * Adds an empty row to the model.
     */
    private class AddListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            tableModel.addEmptyRow();
        }
    }
    
    /**
     * Overwrites the CSV file with the model's current data.
     */
    private class SaveListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                tableModel.saveModel(filepath, delim);
            } catch (FileNotFoundException ex) {
                System.err.println("Cannot save file");
            }
        }
    }
    
    /**
     * Updates the table filter when the text field changes.
     */
    private class TextFieldListener implements DocumentListener {
        @Override
        public void changedUpdate(DocumentEvent e) {
            table = tableModel.setFilter(table, textField);
        }
        
        @Override
        public void insertUpdate(DocumentEvent e) {
            changedUpdate(e);
        }
        
        @Override
        public void removeUpdate(DocumentEvent e) {
            changedUpdate(e);
        }
    }
}
