/**
 * Liam Wild
 * CEN 3024 - Software Development 1
 * July 15, 2025
 * CheckoutGUI.java
 *
 * The CheckoutGUI class provides a Java Swing graphical user interface
 * for managing gear checkout records. It connects to a user-supplied
 * SQLite database via the CheckoutManager class and allows users to:
 * - Add new records
 * - Delete existing records
 * - Update existing records
 * - Mark records as returned
 * - List overdue gear
 *
 * The GUI layout is organized using layout managers for readability and spacing.
 * It uses a JTable to display data and validates user input before database operations.
 */

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class CheckoutGUI {
    private JFrame frame;
    private JTextField nameField, gearField, checkoutField, dueField, returnedField;
    private DefaultTableModel tableModel;
    private JTable table;
    private CheckoutManager manager;

    /**
     * method: main
     * purpose: Entry point for launching the GUI application.
     * parameters: args - command-line arguments (not used)
     * return: void
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CheckoutGUI().createAndShowGUI());
    }

    /**
     * method: createAndShowGUI
     * purpose: Prompts the user for a database path, initializes the GUI frame,
     *          and displays the main window layout.
     * parameters: none
     * return: void
     */
    private void createAndShowGUI() {
        String dbPath = JOptionPane.showInputDialog(null, "Enter path to SQLite .db file:", "Database Path", JOptionPane.QUESTION_MESSAGE);
        if (dbPath == null || dbPath.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No database selected. Program will exit.");
            System.exit(0);
        }

        manager = new CheckoutManager(dbPath.trim());

        frame = new JFrame("Audio Gear Checkout System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(950, 600);
        frame.setLayout(new BorderLayout(10, 10));

        addFormPanel();
        addTablePanel();
        addButtonPanel();

        refreshTable();
        frame.setVisible(true);
    }

    /**
     * method: addFormPanel
     * purpose: Creates and adds labeled text fields to collect checkout information from the user.
     * parameters: none
     * return: void
     */
    private void addFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        formPanel.setBorder(BorderFactory.createTitledBorder("Checkout Information"));

        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(15);
        formPanel.add(nameField, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Gear:"), gbc);
        gbc.gridx = 3;
        gearField = new JTextField(15);
        formPanel.add(gearField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Checkout Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        checkoutField = new JTextField(15);
        formPanel.add(checkoutField, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Due Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 3;
        dueField = new JTextField(15);
        formPanel.add(dueField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Returned (Yes/No):"), gbc);
        gbc.gridx = 1;
        returnedField = new JTextField(10);
        formPanel.add(returnedField, gbc);

        frame.add(formPanel, BorderLayout.NORTH);
    }

    /**
     * method: addTablePanel
     * purpose: Adds a JTable inside a scroll pane
     * to the center of the frame to display all checkout records.
     * parameters: none
     * return: void
     */
    private void addTablePanel() {
        tableModel = new DefaultTableModel(new String[]{"Name", "Gear", "Checkout Date", "Due Date", "Returned"}, 0);
        table = new JTable(tableModel) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Checkout Records"));
        frame.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * method: addButtonPanel
     * purpose: Creates all functional buttons (Add, Delete, Update, Overdue, Mark Returned),
     * defines their behavior, and adds them to the bottom of the GUI.
     * parameters: none
     * return: void
     */
    private void addButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton addButton = new JButton("Add");
        JButton deleteButton = new JButton("DELETE");
        JButton updateButton = new JButton("Update");
        JButton overdueButton = new JButton("Check Overdue Gear");
        JButton markReturnedButton = new JButton("Mark as Returned");

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(overdueButton);
        buttonPanel.add(markReturnedButton);

        // ADD
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String gear = gearField.getText().trim();
            String checkout = checkoutField.getText().trim();
            String due = dueField.getText().trim();
            String returned = returnedField.getText().trim();

            if (name.isEmpty() || gear.isEmpty() || checkout.isEmpty() || due.isEmpty() || returned.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
                return;
            }

            if (!checkout.matches("\\d{4}-\\d{2}-\\d{2}") || !due.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(frame, "Dates must be in YYYY-MM-DD format.");
                return;
            }

            if (!returned.equalsIgnoreCase("Yes") && !returned.equalsIgnoreCase("No")) {
                JOptionPane.showMessageDialog(frame, "Returned field must be 'Yes' or 'No'.");
                return;
            }

            manager.addRecord(name, gear, checkout, due, returned);
            refreshTable();
            clearForm();
        });

        // DELETE
        deleteButton.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected == -1) {
                JOptionPane.showMessageDialog(frame, "Select a row to delete.");
                return;
            }

            String name = (String) tableModel.getValueAt(selected, 0);
            String gear = (String) tableModel.getValueAt(selected, 1);
            String checkout = (String) tableModel.getValueAt(selected, 2);

            manager.deleteRecord(name, gear, checkout);
            refreshTable();
            JOptionPane.showMessageDialog(frame, "Record deleted.");
        });

        // UPDATE
        updateButton.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected == -1) {
                JOptionPane.showMessageDialog(frame, "Select a record to update.");
                return;
            }

            String originalName = (String) tableModel.getValueAt(selected, 0);
            String originalGear = (String) tableModel.getValueAt(selected, 1);
            String originalCheckout = (String) tableModel.getValueAt(selected, 2);

            String newName = nameField.getText().trim();
            String newGear = gearField.getText().trim();
            String newCheckout = checkoutField.getText().trim();
            String newDue = dueField.getText().trim();
            String newReturned = returnedField.getText().trim();

            if (newName.isEmpty() || newGear.isEmpty() || newCheckout.isEmpty() || newDue.isEmpty() || newReturned.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in all fields to update.");
                return;
            }

            if (!newCheckout.matches("\\d{4}-\\d{2}-\\d{2}") || !newDue.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(frame, "Dates must be in YYYY-MM-DD format.");
                return;
            }

            if (!newReturned.equalsIgnoreCase("Yes") && !newReturned.equalsIgnoreCase("No")) {
                JOptionPane.showMessageDialog(frame, "Returned field must be 'Yes' or 'No'.");
                return;
            }

            manager.deleteRecord(originalName, originalGear, originalCheckout);
            manager.addRecord(newName, newGear, newCheckout, newDue, newReturned);

            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(frame, "Record updated.");
        });

        // OVERDUE
        overdueButton.addActionListener(e -> {
            String today = LocalDate.now().toString();
            String result = manager.listOverdueGear(today);
            JOptionPane.showMessageDialog(frame, result);
        });

        // MARK RETURNED
        markReturnedButton.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected == -1) {
                JOptionPane.showMessageDialog(frame, "Select a row to update.");
                return;
            }

            String name = (String) tableModel.getValueAt(selected, 0);
            String gear = (String) tableModel.getValueAt(selected, 1);
            String checkout = (String) tableModel.getValueAt(selected, 2);

            manager.updateReturnStatus(name, gear, checkout, "Yes");
            refreshTable();
            JOptionPane.showMessageDialog(frame, "Record marked as returned.");
        });

        frame.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * method: refreshTable
     * purpose: Reloads data from the database and updates the JTable display.
     * parameters: none
     * return: void
     */
    private void refreshTable() {
        tableModel.setRowCount(0);
        ArrayList<String[]> records = manager.loadAllRecords();
        for (String[] row : records) {
            tableModel.addRow(row);
        }
    }

    // No Javadoc needed for clearForm (simple helper)
    private void clearForm() {
        nameField.setText("");
        gearField.setText("");
        checkoutField.setText("");
        dueField.setText("");
        returnedField.setText("");
    }
}
