package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class ClientDriverGUI extends JFrame {

    private BasicClient client;
    private ClientService service;
    private boolean isAdmin = false;
    private String currentUsername = null;
    private List<String> movies;
    private List<Auditorium> auditoriumList;

    // ui components
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // helper method to create styled buttons
    private JButton createStyledButton(String text, Color bgColor, int width, int height) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setPreferredSize(new Dimension(width, height));
        btn.setBackground(bgColor);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        return btn;
    }

    public ClientDriverGUI() {
        // initialize client and service
        client = new BasicClient();
        service = new ClientService(client);
        service.connectToServer();

        // setup main frame
        setTitle("Movie Ticket Booking System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // create card layout for switching between screens
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // add screens
        mainPanel.add(createLoginScreen(), "LOGIN");

        add(mainPanel);
        setVisible(true);
    }

    // login/register screen
    private JPanel createLoginScreen() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 240, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // title
        JLabel titleLabel = new JLabel("Movie Ticket Booking");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(50, 50, 100));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // login button
        JButton loginBtn = createStyledButton("Login", new Color(70, 130, 180), 200, 50);
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        loginBtn.addActionListener(e -> showLoginDialog());
        panel.add(loginBtn, gbc);

        // register button
        JButton registerBtn = createStyledButton("Register", new Color(60, 179, 113), 200, 50);
        gbc.gridy = 3;
        registerBtn.addActionListener(e -> showRegisterDialog());
        panel.add(registerBtn, gbc);

        return panel;
    }

    // login dialog
    private void showLoginDialog() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JCheckBox adminCheckBox = new JCheckBox();

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.BLACK);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.BLACK);
        JLabel adminLabel = new JLabel("Admin Login:");
        adminLabel.setForeground(Color.BLACK);

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(adminLabel);
        panel.add(adminCheckBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Login",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            boolean isAdminInput = adminCheckBox.isSelected();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            performLogin(username, password, isAdminInput);
        }
    }

    private void performLogin(String username, String password, boolean isAdminInput) {
        service.login(username, password, isAdminInput);
        ServerResponse resp = service.receiveResponse();

        if (resp != null && resp.getPayload() instanceof ServerPayload payload) {
            if (payload.getSuccess()) {
                currentUsername = username;
                JOptionPane.showMessageDialog(this, "Login successful!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                checkAdminAndLoadData();
            } else {
                JOptionPane.showMessageDialog(this, "Login failed: " + payload.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Server did not respond.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // register dialog
    private void showRegisterDialog() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JCheckBox adminCheckBox = new JCheckBox();

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.BLACK);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.BLACK);
        JLabel adminLabel = new JLabel("Register as Admin:");
        adminLabel.setForeground(Color.BLACK);

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(adminLabel);
        panel.add(adminCheckBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Register",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            boolean isAdminInput = adminCheckBox.isSelected();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            performRegistration(username, password, isAdminInput);
        }
    }

    private void performRegistration(String username, String password, boolean isAdminInput) {
        service.register(username, password, isAdminInput);
        ServerResponse resp = service.receiveResponse();

        if (resp != null && resp.getPayload() instanceof ServerPayload payload) {
            if (payload.getSuccess()) {
                JOptionPane.showMessageDialog(this, "Registration successful! Please log in.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed: " + payload.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Server did not respond.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // check admin status and load data
    private void checkAdminAndLoadData() {
        service.isAdmin();
        ServerResponse response = service.receiveResponse();

        if (response != null && response.getPayload() instanceof ServerPayload payload) {
            isAdmin = payload.getSuccess();
        }

        // Fetch movies and auditoriums
        service.requestMovies();
        ServerResponse resp = service.receiveResponse();

        movies = Arrays.asList("Madagascar", "Godzilla", "Cars", "Toy Story"); // fallback
        if (resp != null && resp.getPayload() instanceof MovieListPayload mlp) {
            movies = mlp.getNames();
        }

        resp = service.receiveResponse();
        if (resp != null && resp.getPayload() instanceof AvailabilityPayload load) {
            auditoriumList = load.getInfo();
        }

        // Navigate to appropriate screen
        if (isAdmin) {
            showAdminScreen();
        } else {
            showMovieSelectionScreen();
        }
    }

    // movie selection screen
    private void showMovieSelectionScreen() {
        JPanel moviePanel = new JPanel(new BorderLayout(10, 10));
        moviePanel.setBackground(new Color(240, 240, 245));
        moviePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 240, 245));
        JLabel headerLabel = new JLabel("Now Showing - Select Your Movie");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(50, 50, 100));
        headerPanel.add(headerLabel, BorderLayout.WEST);

        JLabel userLabel = new JLabel("Welcome, " + currentUsername);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        headerPanel.add(userLabel, BorderLayout.EAST);

        moviePanel.add(headerPanel, BorderLayout.NORTH);

        // movie buttons
        JPanel moviesGrid = new JPanel(new GridLayout(0, 2, 15, 15));
        moviesGrid.setBackground(new Color(240, 240, 245));

        Set<String> uniqueMovies = new TreeSet<>(movies);
        for (String movie : uniqueMovies) {
            JButton movieBtn = createMovieButton(movie);
            moviesGrid.add(movieBtn);
        }

        JScrollPane scrollPane = new JScrollPane(moviesGrid);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        moviePanel.add(scrollPane, BorderLayout.CENTER);

        // logout button
        JButton logoutBtn = createStyledButton("Logout", new Color(220, 53, 69), 100, 40);
        logoutBtn.addActionListener(e -> logout());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(new Color(240, 240, 245));
        bottomPanel.add(logoutBtn);
        moviePanel.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(moviePanel, "MOVIES");
        cardLayout.show(mainPanel, "MOVIES");
    }

    private JButton createMovieButton(String movie) {
        JButton btn = createStyledButton("<html><center>" + movie + "</center></html>",
                new Color(70, 130, 180), 300, 100);

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(100, 149, 237));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(70, 130, 180));
            }
        });

        btn.addActionListener(e -> selectShowingTime(movie));
        return btn;
    }

    // showing time selection
    private void selectShowingTime(String movie) {
        List<Auditorium> showingsForMovie = new ArrayList<>();
        for (Auditorium a : auditoriumList) {
            if (a.getMovie().equals(movie)) {
                showingsForMovie.add(a);
            }
        }

        if (showingsForMovie.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No showings available for this movie.",
                    "No Showings", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] showingOptions = new String[showingsForMovie.size()];
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm a");

        for (int i = 0; i < showingsForMovie.size(); i++) {
            showingOptions[i] = showingsForMovie.get(i).getShowingTime().format(fmt);
        }

        String selected = (String) JOptionPane.showInputDialog(this,
                "Select showing time for " + movie + ":",
                "Choose Showing Time",
                JOptionPane.QUESTION_MESSAGE,
                null,
                showingOptions,
                showingOptions[0]);

        if (selected != null) {
            int index = Arrays.asList(showingOptions).indexOf(selected);
            Auditorium chosenAuditorium = showingsForMovie.get(index);
            showSeatSelection(chosenAuditorium, movie);
        }
    }

    // seat selection screen
    private void showSeatSelection(Auditorium auditorium, String movieName) {
        JDialog seatDialog = new JDialog(this, "Select Your Seat - " + movieName, true);
        seatDialog.setSize(700, 600);
        seatDialog.setLocationRelativeTo(this);
        seatDialog.setLayout(new BorderLayout(10, 10));

        // header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Select Your Seat");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        legendPanel.setBackground(Color.WHITE);

        JButton availableBtn = new JButton("Available");
        availableBtn.setBackground(new Color(144, 238, 144));
        availableBtn.setEnabled(false);

        JButton takenBtn = new JButton("Taken");
        takenBtn.setBackground(new Color(255, 99, 71));
        takenBtn.setEnabled(false);

        legendPanel.add(availableBtn);
        legendPanel.add(takenBtn);
        headerPanel.add(legendPanel, BorderLayout.EAST);

        seatDialog.add(headerPanel, BorderLayout.NORTH);

        // screen indicator
        JPanel screenPanel = new JPanel();
        screenPanel.setBackground(Color.WHITE);
        JLabel screenLabel = new JLabel("SCREEN");
        screenLabel.setFont(new Font("Arial", Font.BOLD, 18));
        screenLabel.setForeground(new Color(70, 130, 180));
        screenPanel.add(screenLabel);
        seatDialog.add(screenPanel, BorderLayout.SOUTH);

        // seat grid
        String[][] seating = auditorium.getSeats();
        JPanel seatPanel = new JPanel(new GridLayout(seating.length, seating[0].length, 5, 5));
        seatPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        seatPanel.setBackground(Color.WHITE);

        for (int r = 0; r < seating.length; r++) {
            for (int c = 0; c < seating[r].length; c++) {
                final int row = r;
                final int col = c;
                boolean isEmpty = seating[r][c].equals("empty");

                JButton seatBtn = new JButton((r + 1) + "-" + (c + 1));
                seatBtn.setFont(new Font("Arial", Font.BOLD, 12));
                seatBtn.setPreferredSize(new Dimension(60, 60));

                if (isEmpty) {
                    seatBtn.setBackground(new Color(144, 238, 144));
                    seatBtn.setForeground(Color.BLACK);
                    seatBtn.addActionListener(e -> {
                        int confirm = JOptionPane.showConfirmDialog(seatDialog,
                                "Reserve seat " + (row + 1) + "-" + (col + 1) + "?",
                                "Confirm Reservation",
                                JOptionPane.YES_NO_OPTION);

                        if (confirm == JOptionPane.YES_OPTION) {
                            reserveSeat(row + 1, col + 1, auditorium, seatDialog);
                        }
                    });
                } else {
                    seatBtn.setBackground(new Color(255, 99, 71));
                    seatBtn.setForeground(Color.BLACK);
                    seatBtn.setEnabled(false);
                }

                seatBtn.setFocusPainted(false);
                seatPanel.add(seatBtn);
            }
        }

        JScrollPane scrollPane = new JScrollPane(seatPanel);
        seatDialog.add(scrollPane, BorderLayout.CENTER);

        // back button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton backBtn = createStyledButton("Back to Movies", new Color(108, 117, 125), 150, 40);
        backBtn.addActionListener(e -> seatDialog.dispose());
        bottomPanel.add(backBtn);
        seatDialog.add(bottomPanel, BorderLayout.NORTH);

        seatDialog.setVisible(true);
    }

    // reserve seat
    private void reserveSeat(int row, int col, Auditorium auditorium, JDialog dialog) {
        service.reserveSeat(row, col, currentUsername, auditorium);
        ServerResponse resp = service.receiveResponse();

        if (resp != null && resp.getPayload() instanceof ServerPayload payload) {
            if (payload.getSuccess()) {
                auditorium.setReservation(currentUsername, row - 1, col - 1);
                JOptionPane.showMessageDialog(dialog,
                        "Seat " + row + "-" + col + " reserved successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();

                int another = JOptionPane.showConfirmDialog(this,
                        "Make another reservation?",
                        "Continue?",
                        JOptionPane.YES_NO_OPTION);

                if (another == JOptionPane.NO_OPTION) {
                    logout();
                }
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Reservation failed: " + payload.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(dialog,
                    "Server did not respond.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // admin screen
    private void showAdminScreen() {
        JPanel adminPanel = new JPanel(new BorderLayout(10, 10));
        adminPanel.setBackground(new Color(240, 240, 245));
        adminPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("Admin Panel - " + currentUsername);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(139, 0, 0));
        adminPanel.add(headerLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        centerPanel.setBackground(new Color(240, 240, 245));

        JButton viewReservationsBtn = createAdminButton("View All Reservations", new Color(70, 130, 180));
        JButton addMovieBtn = createAdminButton("Add Movie", new Color(60, 179, 113));
        JButton removeMovieBtn = createAdminButton("Remove Movie", new Color(220, 53, 69));
        JButton addShowingBtn = createAdminButton("Add Showing", new Color(255, 193, 7));
        JButton statsBtn = createAdminButton("View Statistics", new Color(108, 117, 125));
        JButton logoutAdminBtn = createAdminButton("Logout", new Color(220, 53, 69));

        viewReservationsBtn.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Admin functionality - View Reservations\n(Requires AdminInterface implementation)",
                "Admin Feature", JOptionPane.INFORMATION_MESSAGE));

        addMovieBtn.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Admin functionality - Add Movie\n(Requires AdminInterface implementation)",
                "Admin Feature", JOptionPane.INFORMATION_MESSAGE));

        removeMovieBtn.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Admin functionality - Remove Movie\n(Requires AdminInterface implementation)",
                "Admin Feature", JOptionPane.INFORMATION_MESSAGE));

        addShowingBtn.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Admin functionality - Add Showing\n(Requires AdminInterface implementation)",
                "Admin Feature", JOptionPane.INFORMATION_MESSAGE));

        statsBtn.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Admin functionality - View Statistics\n(Requires AdminInterface implementation)",
                "Admin Feature", JOptionPane.INFORMATION_MESSAGE));

        logoutAdminBtn.addActionListener(e -> logout());

        centerPanel.add(viewReservationsBtn);
        centerPanel.add(addMovieBtn);
        centerPanel.add(removeMovieBtn);
        centerPanel.add(addShowingBtn);
        centerPanel.add(statsBtn);
        centerPanel.add(logoutAdminBtn);

        adminPanel.add(centerPanel, BorderLayout.CENTER);

        mainPanel.add(adminPanel, "ADMIN");
        cardLayout.show(mainPanel, "ADMIN");
    }

    private JButton createAdminButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setPreferredSize(new Dimension(250, 80));
        btn.setBackground(color);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        return btn;
    }

    // logout
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            isAdmin = false;
            currentUsername = null;
            mainPanel.removeAll();
            mainPanel.add(createLoginScreen(), "LOGIN");
            cardLayout.show(mainPanel, "LOGIN");
            JOptionPane.showMessageDialog(this,
                    "Thank you!",
                    "Goodbye", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ClientDriverGUI();
        });
    }
}