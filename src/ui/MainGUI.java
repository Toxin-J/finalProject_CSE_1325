package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

import car.Car;
import car.CarInfoManager;
import car.Make;
import car.VehicleStatus;
import customer.Customer;
import customer.PurchaseType;

/**
 * Main window for the dealership app.
 *
 * Layout:
 *   top row    : [Add] [Remove] [Customer]
 *   left panel : filters (type, year, sold/rent, mileage slider)
 *   center     : car table  MAKE | TYPE | YEAR | MILEAGE | SOLD
 *   bottom     : message bar (success / error feedback)
 *
 * Requirements covered: FR-1 add car, FR-2 remove car, FR-3 sell/rent/lease (status only,
 * until PaymentInfo is written), FR-4 view details, FR-5 update car, FR-6 filter,
 * FR-10 add customer, FR-11 remove customer.
 */
public class MainGUI extends JFrame {
    private static final int MAX_MILEAGE = 200_000;
    private static final String ANY = "Any";

    private final CarInfoManager cm;

    private final CarTableModel tableModel = new CarTableModel();
    private final JTable carTable = new JTable(tableModel);

    private final JComboBox<Object> typeFilter = new JComboBox<>();
    private final JComboBox<Object> yearFilter = new JComboBox<>();
    private final JComboBox<Object> statusFilter = new JComboBox<>();
    private final JSlider mileageSlider = new JSlider(0, MAX_MILEAGE, MAX_MILEAGE);
    private final JLabel mileageLabel = new JLabel();

    private final JLabel messageBar = new JLabel(" ");

    public MainGUI(CarInfoManager cm) {
        super("Car Dealership Manager");
        this.cm = cm;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(12, 12, 12, 12));

        add(buildTopButtons(), BorderLayout.NORTH);
        add(buildFilterPanel(), BorderLayout.WEST);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildMessageBar(), BorderLayout.SOUTH);

        refreshYearChoices();
        applyFilters();

        setSize(950, 580);
        setLocationRelativeTo(null);
    }

    // ---------------- layout ----------------

    private JPanel buildTopButtons() {
        JButton addButton = new JButton("Add");
        JButton removeButton = new JButton("Remove");
        JButton customerButton = new JButton("Customer");

        addButton.addActionListener(e -> openAddCarForm());
        removeButton.addActionListener(e -> confirmRemoveCar());
        customerButton.addActionListener(e -> openCustomerWindow());

        JPanel panel = new JPanel(new GridLayout(1, 3, 16, 0));
        for (JButton b : new JButton[] {addButton, removeButton, customerButton}) {
            b.setFont(b.getFont().deriveFont(Font.BOLD, 15f));
            b.setPreferredSize(new Dimension(160, 48));
            panel.add(b);
        }
        return panel;
    }

    private JPanel buildFilterPanel() {
        typeFilter.addItem(ANY);
        for (car.Type t : car.Type.values()) {
            typeFilter.addItem(t);
        }
        statusFilter.addItem(ANY);
        for (VehicleStatus s : VehicleStatus.values()) {
            statusFilter.addItem(s);
        }

        mileageSlider.setMajorTickSpacing(50_000);
        mileageSlider.setPaintTicks(true);
        updateMileageLabel();

        typeFilter.addActionListener(e -> applyFilters());
        yearFilter.addActionListener(e -> applyFilters());
        statusFilter.addActionListener(e -> applyFilters());
        mileageSlider.addChangeListener(e -> {
            updateMileageLabel();
            applyFilters();
        });

        JButton resetButton = new JButton("Reset filters");
        resetButton.addActionListener(e -> resetFilters());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 2), "Filter",
                TitledBorder.CENTER, TitledBorder.TOP));
        panel.setPreferredSize(new Dimension(190, 0));

        addLabeled(panel, "Type", typeFilter);
        addLabeled(panel, "Year", yearFilter);
        addLabeled(panel, "Sold / Rent", statusFilter);
        addLabeled(panel, "Mileage", mileageSlider);
        mileageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(mileageLabel);
        panel.add(Box.createVerticalStrut(12));
        resetButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(resetButton);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private void addLabeled(JPanel panel, String text, JComponent field) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getPreferredSize().height));
        panel.add(label);
        panel.add(field);
        panel.add(Box.createVerticalStrut(10));
    }

    private JComponent buildTablePanel() {
        carTable.setRowHeight(24);
        carTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        carTable.setAutoCreateRowSorter(true);
        carTable.getTableHeader().setFont(carTable.getTableHeader().getFont().deriveFont(Font.BOLD));
        carTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showCarDetails(getSelectedCar());
                }
            }
        });
        JScrollPane scroll = new JScrollPane(carTable);
        scroll.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        return scroll;
    }

    private JComponent buildMessageBar() {
        messageBar.setBorder(new EmptyBorder(6, 4, 6, 4));
        return messageBar;
    }

    // ---------------- filters (FR-6) ----------------

    private void applyFilters() {
        Object t = typeFilter.getSelectedItem();
        Object y = yearFilter.getSelectedItem();
        Object s = statusFilter.getSelectedItem();
        car.Type type = (t instanceof car.Type) ? (car.Type) t : null;
        int year = (y instanceof Integer) ? (Integer) y : -1;
        VehicleStatus status = (s instanceof VehicleStatus) ? (VehicleStatus) s : null;

        int maxMileage = mileageSlider.getValue() >= MAX_MILEAGE ? -1 : mileageSlider.getValue();

        // The backend does the filtering; the GUI only collects the choices and shows the results.
        displayCarResults(cm.filterCar(null, type, status, -1, year, maxMileage));
    }

    private void resetFilters() {
        typeFilter.setSelectedItem(ANY);
        yearFilter.setSelectedItem(ANY);
        statusFilter.setSelectedItem(ANY);
        mileageSlider.setValue(MAX_MILEAGE);
        applyFilters();
    }

    /** Rebuilds the year dropdown from the years of the cars in the fleet. */
    private void refreshYearChoices() {
        Object current = yearFilter.getSelectedItem();
        TreeSet<Integer> years = new TreeSet<>();
        for (Car c : cm.getCars()) {
            years.add(c.getYear());
        }
        yearFilter.removeAllItems();
        yearFilter.addItem(ANY);
        for (Integer yr : years.descendingSet()) {
            yearFilter.addItem(yr);
        }
        yearFilter.setSelectedItem(current != null && years.contains(current) ? current : ANY);
    }

    private void updateMileageLabel() {
        int v = mileageSlider.getValue();
        mileageLabel.setText(v >= MAX_MILEAGE ? "Any mileage" : String.format("Up to %,d mi", v));
    }

    private void displayCarResults(List<Car> cars) {
        tableModel.setCars(cars);
        messageBar.setForeground(Color.DARK_GRAY);
        messageBar.setText(cars.size() + " of " + cm.getCars().size() + " cars shown");
    }

    private Car getSelectedCar() {
        int viewRow = carTable.getSelectedRow();
        if (viewRow < 0) {
            return null;
        }
        return tableModel.getCarAt(carTable.convertRowIndexToModel(viewRow));
    }

    private void refreshFleet() {
        refreshYearChoices();
        applyFilters();
    }

    // ---------------- car actions (FR-1, FR-2, FR-4, FR-5) ----------------

    /** Shows the car form. Returns a new Car, or null if cancelled or invalid. */
    private Car showCarForm(String title, Car existing) {
        JComboBox<Make> makeBox = new JComboBox<>(Make.values());
        JComboBox<car.Type> typeBox = new JComboBox<>(car.Type.values());
        JTextField yearField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField mileageField = new JTextField();
        JComboBox<VehicleStatus> statusBox = new JComboBox<>(VehicleStatus.values());

        if (existing != null) {
            makeBox.setSelectedItem(existing.getMake());
            typeBox.setSelectedItem(existing.getType());
            yearField.setText(String.valueOf(existing.getYear()));
            priceField.setText(String.valueOf(existing.getPrice()));
            mileageField.setText(String.valueOf(existing.getMileage()));
            statusBox.setSelectedItem(existing.getStatus());
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Make"));
        form.add(makeBox);
        form.add(new JLabel("Type"));
        form.add(typeBox);
        form.add(new JLabel("Year"));
        form.add(yearField);
        form.add(new JLabel("Price ($)"));
        form.add(priceField);
        form.add(new JLabel("Mileage"));
        form.add(mileageField);
        if (existing != null) {
            form.add(new JLabel("Status"));
            form.add(statusBox);
        }

        int result = JOptionPane.showConfirmDialog(this, form, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }
        try {
            int year = Integer.parseInt(yearField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            int mileage = Integer.parseInt(mileageField.getText().trim());
            if (year < 1900 || year > 2100 || price < 0 || mileage < 0) {
                showError("Year must be 1900-2100, and price and mileage can't be negative.");
                return null;
            }
            VehicleStatus status = existing == null
                    ? VehicleStatus.AVAILABLE : (VehicleStatus) statusBox.getSelectedItem();
            return new Car((Make) makeBox.getSelectedItem(), (car.Type) typeBox.getSelectedItem(),
                    year, price, mileage, status);
        } catch (NumberFormatException ex) {
            showError("Year, price and mileage must be numbers.");
            return null;
        }
    }

    private void openAddCarForm() {
        Car car = showCarForm("Add Car", null);
        if (car != null) {
            cm.addCar(car);
            refreshFleet();
            showSuccess("Added " + car.getYear() + " " + car.getMake() + " " + car.getType() + ".");
        }
    }

    private void confirmRemoveCar() {
        Car car = getSelectedCar();
        if (car == null) {
            showError("Select a car in the list first.");
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this, "Remove " + car + "?",
                "Remove Car", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            cm.removeCar(car);
            refreshFleet();
            showSuccess("Removed " + car.getYear() + " " + car.getMake() + " " + car.getType() + ".");
        }
    }

    private void showCarDetails(Car car) {
        if (car == null) {
            return;
        }
        String details = String.format("Make: %s%nType: %s%nYear: %d%nPrice: $%,.2f%nMileage: %,d%nStatus: %s",
                car.getMake(), car.getType(), car.getYear(), car.getPrice(), car.getMileage(), car.getStatus());
        Object[] options = {"Edit", "Close"};
        int choice = JOptionPane.showOptionDialog(this, details, "Car Details",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[1]);
        if (choice == 0) {
            Car updated = showCarForm("Edit Car", car);
            if (updated != null && cm.updateCar(car, updated)) {
                refreshFleet();
                showSuccess("Updated " + updated.getYear() + " " + updated.getMake() + " " + updated.getType() + ".");
            }
        }
    }

    // ---------------- customers (FR-3, FR-10, FR-11) ----------------

    private void openCustomerWindow() {
        JDialog dialog = new JDialog(this, "Customers", true);
        DefaultListModel<Customer> listModel = new DefaultListModel<>();
        for (Customer c : cm.getCustomers()) {
            listModel.addElement(c);
        }
        JList<Customer> list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList<?> l, Object value, int index,
                    boolean selected, boolean focus) {
                String text = (value instanceof Customer) ? ((Customer) value).getName() : String.valueOf(value);
                return super.getListCellRendererComponent(l, text, index, selected, focus);
            }
        });

        JTextArea infoArea = new JTextArea(6, 28);
        infoArea.setEditable(false);
        list.addListSelectionListener(e -> {
            Customer c = list.getSelectedValue();
            infoArea.setText(c == null ? "" : c.toString());
        });

        JButton addBtn = new JButton("Add Customer");
        JButton removeBtn = new JButton("Remove Customer");
        JButton transactionBtn = new JButton("Sell / Rent / Lease Selected Car");

        addBtn.addActionListener(e -> {
            Customer c = showCustomerForm(dialog);
            if (c != null) {
                cm.addCustomer(c);
                listModel.addElement(c);
                showSuccess("Added customer " + c.getName() + ".");
            }
        });

        removeBtn.addActionListener(e -> {
            Customer c = list.getSelectedValue();
            if (c == null) {
                return;
            }
            if (JOptionPane.showConfirmDialog(dialog, "Remove " + c.getName() + "?", "Remove Customer",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION && cm.removeCustomer(c)) {
                listModel.removeElement(c);
                showSuccess("Removed customer " + c.getName() + ".");
            }
        });

        transactionBtn.addActionListener(e -> recordTransaction(dialog, list.getSelectedValue()));

        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(addBtn);
        buttons.add(removeBtn);
        buttons.add(transactionBtn);

        JPanel center = new JPanel(new GridLayout(1, 2, 10, 0));
        center.add(new JScrollPane(list));
        center.add(new JScrollPane(infoArea));

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        root.add(center, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setSize(720, 360);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private Customer showCustomerForm(Component parent) {
        JTextField name = new JTextField();
        JTextField phone = new JTextField();
        JTextField email = new JTextField();
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Name"));
        form.add(name);
        form.add(new JLabel("Phone (digits only)"));
        form.add(phone);
        form.add(new JLabel("Email"));
        form.add(email);
        if (JOptionPane.showConfirmDialog(parent, form, "Add Customer",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return null;
        }
        if (name.getText().trim().isEmpty()) {
            showError("Name can't be empty.");
            return null;
        }
        String digits = phone.getText().replaceAll("[^0-9]", "");
        String phoneNumber = String.valueOf(digits);
        if (phoneNumber.length() == 10) {
        	return new Customer(name.getText().trim(), phoneNumber, email.getText().trim());
        }
        else {
            showError("That phone number doesn't fit in Customer's int phoneNumber field.");
            return null;
        }
    }

    /** FR-3: marks the selected car sold/rented/leased for the selected customer. */
    private void recordTransaction(Component parent, Customer customer) {
        Car car = getSelectedCar();
        if (customer == null || car == null) {
            JOptionPane.showMessageDialog(parent,
                    "Pick a customer here and a car in the main list first.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        Object how = JOptionPane.showInputDialog(parent, "Sale, rental or lease?", "Record Transaction",
                JOptionPane.QUESTION_MESSAGE, null, PurchaseType.values(), PurchaseType.SALE);
        if (how == null) {
            return;
        }
        if (!cm.recordTransaction(car, customer, (PurchaseType) how)) {
            JOptionPane.showMessageDialog(parent, "That car is already " + car.getStatus() + ".",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        applyFilters();
        showSuccess(car.getYear() + " " + car.getMake() + " " + car.getType() + " -> "
                + customer.getName() + " (" + how + ").");
    }

    // ---------------- feedback ----------------

    private void showError(String message) {
        messageBar.setForeground(new Color(180, 30, 30));
        messageBar.setText(message);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        messageBar.setForeground(new Color(20, 120, 40));
        messageBar.setText(message);
    }

    // ---------------- table model ----------------

    private static class CarTableModel extends AbstractTableModel {
        private final String[] columns = {"Make", "Type", "Year", "Mileage", "Sold"};
        private List<Car> cars = new ArrayList<>();

        void setCars(List<Car> cars) {
            this.cars = new ArrayList<>(cars);
            fireTableDataChanged();
        }

        Car getCarAt(int row) {
            return cars.get(row);
        }

        @Override
        public int getRowCount() {
            return cars.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }
        

        @Override
        public String getColumnName(int col) {
            return columns[col];
        }

        @Override
        public Class<?> getColumnClass(int col) {
            return (col == 2 || col == 3) ? Integer.class : String.class;
        }

        @Override
        public Object getValueAt(int row, int col) {
            Car c = cars.get(row);
            switch (col) {
                case 0:
                    return c.getMake().toString();
                case 1:
                    return c.getType().toString();
                case 2:
                    return c.getYear();
                case 3:
                    return c.getMileage();
                default:
                    return c.getStatus().toString();
            }
        }
    }

    // ---------------- entry point ----------------

    public static void main(String[] args) {
        CarInfoManager cm = new CarInfoManager();
        // Sample cars for testing. Remove once real data loading exists.
        cm.addCar(new Car(Make.TOYOTA, car.Type.SEDAN, 2021, 24500, 32000, VehicleStatus.AVAILABLE));
        cm.addCar(new Car(Make.FORD, car.Type.TRUCK, 2019, 31000, 58000, VehicleStatus.AVAILABLE));
        cm.addCar(new Car(Make.HONDA, car.Type.SUV, 2022, 28900, 15000, VehicleStatus.AVAILABLE));
        cm.addCar(new Car(Make.CHEVROLET, car.Type.COUPE, 2018, 22000, 47000, VehicleStatus.SOLD));
        cm.addCar(new Car(Make.HONDA, car.Type.MINIVAN, 2020, 26500, 41000, VehicleStatus.RENTED));

        SwingUtilities.invokeLater(() -> new MainGUI(cm).setVisible(true));
    }
}