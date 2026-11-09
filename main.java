
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;

public class main extends JFrame {

    // =====================================
    // GLOBAL VARIABLES
    // =====================================

    private ArrayList<DocumentRequest> requests = new ArrayList<>();
    private ArrayList<Admin> admins = new ArrayList<>();

    private final String REQUEST_FILE = "requests.txt";
    private final String ADMIN_FILE = "admins.txt";

    private int nextQueueNumber = 1;
    private int nextReferenceNumber = 1;

    private JPanel mainPanel;
    private DefaultListModel<DocumentRequest> listModel;
    private JList<DocumentRequest> requestList;

    private JLabel adminMessage;

    // =====================================
    // CONSTRUCTOR
    // =====================================

    public main() {

        setTitle("SertifiKAP - Barangay Document Request and Queue System");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel();

        add(mainPanel);

        loadRequests();
        loadAdmins();

        showMainMenu();
    }

    // =====================================
    // MAIN MENU
    // =====================================

    private void showMainMenu() {

        mainPanel.removeAll();
        mainPanel.setLayout(new GridBagLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JLabel title = new JLabel("SertifiKAP");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(new Color(20, 70, 120));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel(
            "Barangay Document Request and Queue System"
        );
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel welcome = new JLabel("Welcome!");
        welcome.setFont(new Font("Arial", Font.BOLD, 22));
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton residentButton = new JButton("Resident");
        JButton adminButton = new JButton("Admin Log In");

        residentButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        adminButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        residentButton.setMaximumSize(new Dimension(250, 45));
        adminButton.setMaximumSize(new Dimension(250, 45));

        panel.add(title);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(45));
        panel.add(welcome);
        panel.add(Box.createVerticalStrut(25));
        panel.add(residentButton);
        panel.add(Box.createVerticalStrut(15));
        panel.add(adminButton);

        mainPanel.add(panel);

        residentButton.addActionListener(e -> showResidentPage());
        adminButton.addActionListener(e -> showAdminLogin());

        refreshScreen();
    }

    // =====================================
    // RESIDENT PAGE
    // =====================================

    private void showResidentPage() {

        mainPanel.removeAll();
        mainPanel.setLayout(new GridBagLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Resident Page");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel description = new JLabel(
            "Select what you want to do."
        );
        description.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton requestButton = new JButton("Request Document");
        JButton statusButton = new JButton("Check Request Status");
        JButton backButton = new JButton("Back to Main Menu");

        requestButton.setMaximumSize(new Dimension(280, 45));
        statusButton.setMaximumSize(new Dimension(280, 45));
        backButton.setMaximumSize(new Dimension(280, 45));

        requestButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        panel.add(description);
        panel.add(Box.createVerticalStrut(35));
        panel.add(requestButton);
        panel.add(Box.createVerticalStrut(15));
        panel.add(statusButton);
        panel.add(Box.createVerticalStrut(15));
        panel.add(backButton);

        mainPanel.add(panel);

        requestButton.addActionListener(e -> showRequestForm());
        statusButton.addActionListener(e -> showStatusForm());
        backButton.addActionListener(e -> showMainMenu());

        refreshScreen();
    }

    // =====================================
    // REQUEST DOCUMENT FORM
    // =====================================

    private void showRequestForm() {

        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout(15, 15));

        JLabel title = new JLabel("Request a Barangay Document");
        title.setFont(new Font("Arial", Font.BOLD, 25));

        title.setBorder(
            BorderFactory.createEmptyBorder(15, 20, 5, 20)
        );

        JPanel form = new JPanel(new GridLayout(0, 2, 12, 12));

        form.setBorder(
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        );

        JTextField nameField = new JTextField();
        JTextField birthdateField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField residencyField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField purposeField = new JTextField();

        JLabel ageLabel = new JLabel("Automatically computed");

        String[] documents = {
            "Barangay Clearance",
            "Certificate of Indigency",
            "Certificate of Residency"
        };

        JComboBox<String> documentBox =
            new JComboBox<>(documents);

        addField(form, "Full Name:", nameField);
        addField(form, "Birthdate (YYYY-MM-DD):", birthdateField);
        addField(form, "Age:", ageLabel);
        addField(form, "Address:", addressField);
        addField(form, "Years of Residency:", residencyField);
        addField(form, "Contact Number:", contactField);
        addField(form, "Purpose of Request:", purposeField);
        addField(form, "Document Type:", documentBox);

        birthdateField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                try {
                    int age = calculateAge(birthdateField.getText());
                    ageLabel.setText(age + " years old");
                } catch (Exception ex) {
                    ageLabel.setText("Invalid birthdate");
                }
            }
        });

        JButton submitButton = new JButton("Submit Request");
        JButton backButton = new JButton("Back to Resident Page");

        JPanel buttons = new JPanel();
        buttons.add(backButton);
        buttons.add(submitButton);

        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(form), BorderLayout.CENTER);
        mainPanel.add(buttons, BorderLayout.SOUTH);

        submitButton.addActionListener(e -> {

            try {

                String name = nameField.getText().trim();
                String birthdate = birthdateField.getText().trim();
                String address = addressField.getText().trim();
                String contact = contactField.getText().trim();
                String purpose = purposeField.getText().trim();

                if (
                    name.isEmpty() ||
                    birthdate.isEmpty() ||
                    address.isEmpty() ||
                    residencyField.getText().trim().isEmpty() ||
                    contact.isEmpty() ||
                    purpose.isEmpty()
                ) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Please complete all fields."
                    );
                    return;
                }

                int age = calculateAge(birthdate);

                int years = Integer.parseInt(
                    residencyField.getText().trim()
                );

                if (years < 0 || years > age) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Please enter valid years of residency."
                    );
                    return;
                }

                if (!contact.matches("[0-9+() -]{7,20}")) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Please enter a valid contact number."
                    );
                    return;
                }

                String reference = String.format(
                    "SK-%d-%04d",
                    LocalDate.now().getYear(),
                    nextReferenceNumber
                );

                int queue = nextQueueNumber;

                DocumentRequest request = new DocumentRequest(
                    reference,
                    name,
                    birthdate,
                    age,
                    address,
                    years,
                    contact,
                    purpose,
                    documentBox.getSelectedItem().toString(),
                    queue
                );

                requests.add(request);

                nextQueueNumber++;
                nextReferenceNumber++;

                saveRequests();

                int position = getQueuePosition(request);
                int pending = getPendingCount();

                JOptionPane.showMessageDialog(
                    this,
                    "REQUEST SUBMITTED SUCCESSFULLY!\n\n" +
                    "Reference Number: " + reference +
                    "\nQueue Number: " + queue +
                    "\nQueue Position: " + position +
                    "\nTotal Pending Requests: " + pending +
                    "\n\nPLEASE SAVE YOUR REFERENCE NUMBER." +
                    "\nRETURN TO THE RESIDENT PAGE AND CHECK YOUR STATUS."
                );

                showResidentPage();

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                    this,
                    "Years of residency must be a valid number."
                );

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                    this,
                    "Invalid birthdate. Please use YYYY-MM-DD."
                );
            }
        });

        backButton.addActionListener(e -> showResidentPage());

        refreshScreen();
    }

    // =====================================
    // CALCULATE AGE
    // =====================================

    private int calculateAge(String birthdate) {

        LocalDate date = LocalDate.parse(birthdate);

        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                "Birthdate cannot be in the future."
            );
        }

        return Period.between(
            date,
            LocalDate.now()
        ).getYears();
    }

    // =====================================
    // CHECK REQUEST STATUS
    // =====================================

    private void showStatusForm() {

        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout(15, 15));

        JLabel title = new JLabel("Check Request Status");
        title.setFont(new Font("Arial", Font.BOLD, 25));

        title.setBorder(
            BorderFactory.createEmptyBorder(15, 20, 5, 20)
        );

        JPanel form = new JPanel(new GridLayout(0, 2, 12, 12));

        form.setBorder(
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        );

        JTextField nameField = new JTextField();
        JTextField referenceField = new JTextField();

        addField(form, "Resident Name:", nameField);
        addField(form, "Reference Number:", referenceField);

        JButton searchButton = new JButton("Search Request");
        JButton backButton = new JButton("Back to Resident Page");

        JPanel buttons = new JPanel();

        buttons.add(backButton);
        buttons.add(searchButton);

        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(form, BorderLayout.CENTER);
        mainPanel.add(buttons, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> {

            String name = nameField.getText().trim();
            String reference = referenceField.getText().trim();

            if (name.isEmpty() || reference.isEmpty()) {

                JOptionPane.showMessageDialog(
                    this,
                    "Please enter your name and reference number."
                );

                return;
            }

            boolean found = false;

            for (DocumentRequest r : requests) {

                if (
                    r.getFullName().equalsIgnoreCase(name) &&
                    r.getReferenceNumber().equalsIgnoreCase(reference)
                ) {

                    String message =
                        "REQUEST DETAILS\n\n" +
                        "Name: " + r.getFullName() +
                        "\nDocument: " + r.getDocumentType() +
                        "\nReference Number: " + r.getReferenceNumber() +
                        "\nQueue Number: " + r.getQueueNumber() +
                        "\nStatus: " + r.getStatus();

                    if (r.getStatus().equals("Rejected")) {
                        message += "\nRejection Reason: " +
                            r.getRejectionReason();
                    }

                    JOptionPane.showMessageDialog(
                        this,
                        message,
                        "Request Information",
                        JOptionPane.INFORMATION_MESSAGE
                    );

                    found = true;
                    break;
                }
            }

            if (!found) {
                JOptionPane.showMessageDialog(
                    this,
                    "Request not found. Please check your information."
                );
            }
        });

        backButton.addActionListener(e -> showResidentPage());

        refreshScreen();
    }

    // =====================================
    // ADMIN LOGIN
    // =====================================

    private void showAdminLogin() {

        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());

        JLabel title = new JLabel("Admin Log In");
        title.setFont(new Font("Arial", Font.BOLD, 25));

        title.setBorder(
            BorderFactory.createEmptyBorder(15, 20, 5, 20)
        );

        JPanel form = new JPanel(new GridLayout(0, 2, 12, 12));

        form.setBorder(
            BorderFactory.createEmptyBorder(40, 70, 40, 70)
        );

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        addField(form, "Username:", usernameField);
        addField(form, "Password:", passwordField);

        JButton loginButton = new JButton("Log In");
        JButton signupButton = new JButton("Sign Up");
        JButton backButton = new JButton("Back to Main Menu");

        JPanel buttons = new JPanel();

        buttons.add(backButton);
        buttons.add(signupButton);
        buttons.add(loginButton);

        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(form, BorderLayout.CENTER);
        mainPanel.add(buttons, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> {

            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            for (Admin a : admins) {

                if (
                    a.getUsername().equals(username) &&
                    a.getPassword().equals(password)
                ) {

                    showAdminDashboard();
                    return;
                }
            }

            JOptionPane.showMessageDialog(
                this,
                "Incorrect username or password."
            );
        });

        signupButton.addActionListener(e -> showAdminSignup());

        backButton.addActionListener(e -> showMainMenu());

        refreshScreen();
    }

    // =====================================
    // ADMIN SIGN UP
    // =====================================

    private void showAdminSignup() {

        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());

        JLabel title = new JLabel("Admin Sign Up");
        title.setFont(new Font("Arial", Font.BOLD, 25));

        title.setBorder(
            BorderFactory.createEmptyBorder(15, 20, 5, 20)
        );

        JPanel form = new JPanel(new GridLayout(0, 2, 12, 12));

        form.setBorder(
            BorderFactory.createEmptyBorder(40, 70, 40, 70)
        );

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmField = new JPasswordField();

        addField(form, "New Username:", usernameField);
        addField(form, "Password:", passwordField);
        addField(form, "Confirm Password:", confirmField);

        JButton createButton = new JButton("Create Account");
        JButton backButton = new JButton("Back to Login");

        JPanel buttons = new JPanel();

        buttons.add(backButton);
        buttons.add(createButton);

        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(form, BorderLayout.CENTER);
        mainPanel.add(buttons, BorderLayout.SOUTH);

        createButton.addActionListener(e -> {

            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirm = new String(confirmField.getPassword());

            if (
                username.isEmpty() ||
                password.isEmpty() ||
                confirm.isEmpty()
            ) {

                JOptionPane.showMessageDialog(
                    this,
                    "Please complete all fields."
                );

                return;
            }

            if (password.length() < 6) {

                JOptionPane.showMessageDialog(
                    this,
                    "Password must have at least 6 characters."
                );

                return;
            }

            if (!password.equals(confirm)) {

                JOptionPane.showMessageDialog(
                    this,
                    "Passwords do not match."
                );

                return;
            }

            for (Admin a : admins) {

                if (a.getUsername().equalsIgnoreCase(username)) {

                    JOptionPane.showMessageDialog(
                        this,
                        "Username already exists."
                    );

                    return;
                }
            }

            Admin admin = new Admin(username, password);

            admins.add(admin);

            saveAdmins();

            JOptionPane.showMessageDialog(
                this,
                "Admin account created successfully!"
            );

            showAdminLogin();
        });

        backButton.addActionListener(e -> showAdminLogin());

        refreshScreen();
    }

    // =====================================
    // ADMIN DASHBOARD
    // =====================================

    private void showAdminDashboard() {

        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 25));

        title.setBorder(
            BorderFactory.createEmptyBorder(15, 20, 5, 20)
        );

        adminMessage = new JLabel("Requests are arranged using FCFS.");

        listModel = new DefaultListModel<>();

        requestList = new JList<>(listModel);

        requestList.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scrollPane = new JScrollPane(requestList);

        scrollPane.setBorder(
            BorderFactory.createTitledBorder("Resident Requests")
        );

        JButton approveButton = new JButton("Approve / Start Review");
        JButton readyButton = new JButton("Mark Ready to Pick Up");
        JButton rejectButton = new JButton("Reject Request");
        JButton refreshButton = new JButton("Refresh");
        JButton logoutButton = new JButton("Log Out");

        JPanel buttons = new JPanel();

        buttons.add(refreshButton);
        buttons.add(approveButton);
        buttons.add(readyButton);
        buttons.add(rejectButton);
        buttons.add(logoutButton);

        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());

        bottom.add(adminMessage, BorderLayout.NORTH);
        bottom.add(buttons, BorderLayout.SOUTH);

        mainPanel.add(bottom, BorderLayout.SOUTH);

        approveButton.addActionListener(e -> approveRequest());
        readyButton.addActionListener(e -> markReady());
        rejectButton.addActionListener(e -> rejectRequest());
        refreshButton.addActionListener(e -> refreshRequestList());

        logoutButton.addActionListener(e -> showMainMenu());

        refreshRequestList();

        refreshScreen();
    }

    // =====================================
    // REFRESH REQUEST LIST
    // =====================================

    private void refreshRequestList() {

        listModel.clear();

        // Display requests in their submission order.
        for (DocumentRequest r : requests) {
            listModel.addElement(r);
        }

        DocumentRequest first = getNextRequest();

        if (first == null) {

            adminMessage.setText("No Pending Requests.");

        } else {

            adminMessage.setText(
                "Next FCFS Request: " +
                first.getReferenceNumber() +
                " - " +
                first.getFullName()
            );
        }
    }

    // =====================================
    // FIND NEXT REQUEST USING FCFS
    // =====================================

    private DocumentRequest getNextRequest() {

        for (DocumentRequest r : requests) {

            if (
                r.getStatus().equals("Pending") ||
                r.getStatus().equals("Under Review")
            ) {
                return r;
            }
        }

        return null;
    }

    // =====================================
    // APPROVE REQUEST
    // =====================================

    private void approveRequest() {

        DocumentRequest selected =
            requestList.getSelectedValue();

        if (selected == null) {

            JOptionPane.showMessageDialog(
                this,
                "Please select a request."
            );

            return;
        }

        DocumentRequest first = getNextRequest();

        if (first == null || first != selected) {

            JOptionPane.showMessageDialog(
                this,
                "Please process the earliest active request first (FCFS)."
            );

            return;
        }

        if (!selected.getStatus().equals("Pending")) {

            JOptionPane.showMessageDialog(
                this,
                "This request is not pending."
            );

            return;
        }

        selected.setStatus("Under Review");

        saveRequests();

        refreshRequestList();

        JOptionPane.showMessageDialog(
            this,
            "Request approved.\nStatus: Under Review\n\n" +
            "Prepare and print the requested document."
        );
    }

    // =====================================
    // MARK READY TO PICK UP
    // =====================================

    private void markReady() {

        DocumentRequest selected =
            requestList.getSelectedValue();

        if (selected == null) {

            JOptionPane.showMessageDialog(
                this,
                "Please select a request."
            );

            return;
        }

        DocumentRequest first = getNextRequest();

        if (first == null || first != selected) {

            JOptionPane.showMessageDialog(
                this,
                "Please process the earliest active request first."
            );

            return;
        }

        if (!selected.getStatus().equals("Under Review")) {

            JOptionPane.showMessageDialog(
                this,
                "The request must be Under Review first."
            );

            return;
        }

        selected.setStatus("Ready to Pick Up");

        saveRequests();

        refreshRequestList();

        JOptionPane.showMessageDialog(
            this,
            "Document is now Ready to Pick Up!"
        );
    }

    // =====================================
    // REJECT REQUEST
    // =====================================

    private void rejectRequest() {

        DocumentRequest selected =
            requestList.getSelectedValue();

        if (selected == null) {

            JOptionPane.showMessageDialog(
                this,
                "Please select a request."
            );

            return;
        }

        DocumentRequest first = getNextRequest();

        if (first == null || first != selected) {

            JOptionPane.showMessageDialog(
                this,
                "Please process the earliest active request first."
            );

            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to reject this request?",
            "Confirm Rejection",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String reason = JOptionPane.showInputDialog(
            this,
            "Enter the reason for rejection:"
        );

        if (reason == null) {
            return;
        }

        if (reason.trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                this,
                "Rejection reason is required."
            );

            return;
        }

        selected.setStatus("Rejected");
        selected.setRejectionReason(reason.trim());

        saveRequests();

        refreshRequestList();

        JOptionPane.showMessageDialog(
            this,
            "Request rejected successfully."
        );
    }

    // =====================================
    // QUEUE POSITION
    // =====================================

    private int getQueuePosition(DocumentRequest target) {

        int position = 0;

        for (DocumentRequest r : requests) {

            if (
                r.getStatus().equals("Pending") ||
                r.getStatus().equals("Under Review")
            ) {

                position++;

                if (r == target) {
                    return position;
                }
            }
        }

        return 0;
    }

    private int getPendingCount() {

        int count = 0;

        for (DocumentRequest r : requests) {

            if (r.getStatus().equals("Pending")) {
                count++;
            }
        }

        return count;
    }

    // =====================================
    // FILE HANDLING: SAVE REQUESTS
    // =====================================

    private void saveRequests() {

        try {

            PrintWriter writer = new PrintWriter(
                new FileWriter(REQUEST_FILE)
            );

            for (DocumentRequest r : requests) {

                writer.println(
                    clean(r.getReferenceNumber()) + "|" +
                    clean(r.getFullName()) + "|" +
                    clean(r.getBirthdate()) + "|" +
                    r.getAge() + "|" +
                    clean(r.getAddress()) + "|" +
                    r.getYearsOfResidency() + "|" +
                    clean(r.getContactNumber()) + "|" +
                    clean(r.getPurpose()) + "|" +
                    clean(r.getDocumentType()) + "|" +
                    r.getQueueNumber() + "|" +
                    clean(r.getStatus()) + "|" +
                    clean(r.getRejectionReason())
                );
            }

            writer.close();

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                this,
                "Error saving requests: " + e.getMessage()
            );
        }
    }

    // =====================================
    // FILE HANDLING: LOAD REQUESTS
    // =====================================

    private void loadRequests() {

        try {

            File file = new File(REQUEST_FILE);

            if (!file.exists()) {
                return;
            }

            BufferedReader reader = new BufferedReader(
                new FileReader(file)
            );

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split("\\|", -1);

                if (data.length == 12) {

                    DocumentRequest r = new DocumentRequest(
                        data[0],
                        data[1],
                        data[2],
                        Integer.parseInt(data[3]),
                        data[4],
                        Integer.parseInt(data[5]),
                        data[6],
                        data[7],
                        data[8],
                        Integer.parseInt(data[9])
                    );

                    r.setStatus(data[10]);
                    r.setRejectionReason(data[11]);

                    requests.add(r);

                    if (r.getQueueNumber() >= nextQueueNumber) {
                        nextQueueNumber = r.getQueueNumber() + 1;
                    }

                    try {
                        int refNumber = Integer.parseInt(
                            r.getReferenceNumber().substring(8)
                        );

                        if (refNumber >= nextReferenceNumber) {
                            nextReferenceNumber = refNumber + 1;
                        }

                    } catch (Exception ex) {
                        // Keep loading the remaining records.
                    }
                }
            }

            reader.close();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                this,
                "Error loading requests: " + e.getMessage()
            );
        }
    }

    // =====================================
    // FILE HANDLING: SAVE ADMINS
    // =====================================

    private void saveAdmins() {

        try {

            PrintWriter writer = new PrintWriter(
                new FileWriter(ADMIN_FILE)
            );

            for (Admin a : admins) {

                writer.println(
                    clean(a.getUsername()) + "|" +
                    clean(a.getPassword())
                );
            }

            writer.close();

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                this,
                "Error saving admin accounts."
            );
        }
    }

    // =====================================
    // FILE HANDLING: LOAD ADMINS
    // =====================================

    private void loadAdmins() {

        try {

            File file = new File(ADMIN_FILE);

            if (!file.exists()) {
                return;
            }

            BufferedReader reader = new BufferedReader(
                new FileReader(file)
            );

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split("\\|", -1);

                if (data.length == 2) {

                    Admin a = new Admin(
                        data[0],
                        data[1]
                    );

                    admins.add(a);
                }
            }

            reader.close();

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                this,
                "Error loading admin accounts."
            );
        }
    }

    // =====================================
    // HELPER METHODS
    // =====================================

    private void addField(
        JPanel panel,
        String label,
        JComponent component
    ) {

        panel.add(new JLabel(label));
        panel.add(component);
    }

    private String clean(String text) {

        return text.replace("|", "/")
                   .replace("\n", " ")
                   .replace("\r", " ");
    }

    private void refreshScreen() {

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // =====================================
    // MAIN METHOD
    // =====================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            main app = new main();

            app.setVisible(true);
        });
    }
}

// =========================================
// DOCUMENT REQUEST CLASS
// =========================================

class DocumentRequest {

    private String referenceNumber;
    private String fullName;
    private String birthdate;
    private String address;
    private String contactNumber;
    private String purpose;
    private String documentType;
    private String status;
    private String rejectionReason;

    private int age;
    private int yearsOfResidency;
    private int queueNumber;

    public DocumentRequest(
        String referenceNumber,
        String fullName,
        String birthdate,
        int age,
        String address,
        int yearsOfResidency,
        String contactNumber,
        String purpose,
        String documentType,
        int queueNumber
    ) {

        this.referenceNumber = referenceNumber;
        this.fullName = fullName;
        this.birthdate = birthdate;
        this.age = age;
        this.address = address;
        this.yearsOfResidency = yearsOfResidency;
        this.contactNumber = contactNumber;
        this.purpose = purpose;
        this.documentType = documentType;
        this.queueNumber = queueNumber;

        this.status = "Pending";
        this.rejectionReason = "";
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public int getAge() {
        return age;
    }

    public String getAddress() {
        return address;
    }

    public int getYearsOfResidency() {
        return yearsOfResidency;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getDocumentType() {
        return documentType;
    }

    public int getQueueNumber() {
        return queueNumber;
    }

    public String getStatus() {
        return status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRejectionReason(String reason) {
        this.rejectionReason = reason;
    }

    @Override
    public String toString() {

        return "Queue No. " + queueNumber +
               " | " + fullName +
               " | " + documentType +
               " | " + status;
    }
}

// =========================================
// ADMIN CLASS
// =========================================

class Admin {

    private String username;
    private String password;

    public Admin(String username, String password) {

        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
