package cinema.gui;

import cinema.model.*;
import cinema.service.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainFrame extends JFrame {
    private final MovieService movieService = new MovieService();
    private final RoomService roomService = new RoomService();
    private final ScreeningService screeningService = new ScreeningService();
    private final PurchaseService purchaseService = new PurchaseService();
    private final UserService userService = new UserService();

    private final JPanel content = new JPanel(new BorderLayout());
    private JPanel navigation;
    private Person currentUser;
    private Movie selectedMovieForNewScreening;

    private final Color background = new Color(238, 246, 252);
    private final Color surface = new Color(232, 240, 247);
    private final Color cardColor = Color.WHITE;
    private final Color blue = new Color(45, 111, 222);
    private final Color dark = new Color(16, 43, 70);
    private final Color greenBackground = new Color(241, 250, 245);
    private final Color pinkBackground = new Color(251, 247, 252);

    private final Font titleFont = new Font("Arial", Font.BOLD, 22);
    private final Font sectionFont = new Font("Arial", Font.BOLD, 15);
    private final Font normalFont = new Font("Arial", Font.PLAIN, 12);
    private final Font smallFont = new Font("Arial", Font.PLAIN, 11);

    public MainFrame() {
        setTitle("Cinema Management System");
        setSize(1220, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        content.setBackground(background);
        add(content, BorderLayout.CENTER);
        startPendingPurchaseChecker();
        showLoginPanel();
    }

    private void startPendingPurchaseChecker() {
        Timer timer = new Timer(600000, e -> purchaseService.cancelExpiredPendingPurchases());
        timer.setInitialDelay(600000);
        timer.start();
    }

    private void showLoginPanel() {
        removeNavigation();
        JPanel page = new JPanel(new GridBagLayout());
        page.setBackground(background);

        JPanel loginCard = card("Login to Cinema Management System");
        loginCard.setPreferredSize(new Dimension(560, 210));

        List<Person> users = userService.getAllUsers();
        JComboBox<Person> userBox = new JComboBox<>(users.toArray(new Person[0]));
        userBox.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value == null ? "" : value + " - " + getRoleName(value));
            label.setOpaque(true);
            label.setBorder(new EmptyBorder(4, 8, 4, 8));
            label.setBackground(isSelected ? new Color(216, 232, 255) : Color.WHITE);
            label.setForeground(dark);
            return label;
        });
        userBox.setPreferredSize(new Dimension(360, 34));

        JButton loginButton = primaryButton("Login");
        loginButton.setPreferredSize(new Dimension(360, 34));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(cardColor);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 8, 10, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.gridy = 0;
        form.add(new JLabel("User:"), c);
        c.gridx = 1;
        form.add(userBox, c);
        c.gridx = 1; c.gridy = 1;
        form.add(loginButton, c);

        loginCard.add(form, BorderLayout.CENTER);
        page.add(loginCard);
        loginButton.addActionListener(e -> {
            currentUser = (Person) userBox.getSelectedItem();
            buildNavigation();
            showStartPanelForUser();
        });
        setContent(page);
    }

    private void buildNavigation() {
        removeNavigation();
        navigation = new JPanel(new BorderLayout());
        navigation.setBackground(Color.WHITE);
        navigation.setBorder(new EmptyBorder(14, 26, 12, 26));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        left.setBackground(Color.WHITE);
        JLabel logo = new JLabel("", SwingConstants.CENTER);
        logo.setOpaque(true);
        logo.setBackground(blue);
        logo.setPreferredSize(new Dimension(42, 42));
        left.add(logo);

        if (currentUser instanceof ScreeningEmployee) {
            left.add(navButton("Add screening", () -> showAddScreeningPanel()));
            left.add(navButton("Movies", () -> showMoviesPanel()));
            left.add(navButton("Screenings", () -> showScreeningsPanel()));
            left.add(navButton("Rooms", () -> showRoomsPanel()));
        } else if (currentUser instanceof Cashier) {
            left.add(navButton("Purchases", () -> showPurchasesPanel()));
            left.add(navButton("Movies", () -> showMoviesPanel()));
            left.add(navButton("Screenings", () -> showScreeningsPanel()));
        } else if (currentUser instanceof Customer) {
            left.add(navButton("Movies", () -> showMoviesPanel()));
            left.add(navButton("Screenings", () -> showScreeningsPanel()));
            left.add(navButton("My purchases", () -> showPurchasesPanel()));
        } else if (currentUser instanceof Employee) {
            left.add(navButton("Profile", () -> showUserProfilePanel()));
            left.add(navButton("Movies", () -> showMoviesPanel()));
        }
        left.add(navButton("Profile", () -> showUserProfilePanel()));

        JPanel userPanel = new JPanel(new BorderLayout(8, 0));
        userPanel.setBackground(Color.WHITE);
        JLabel avatar = new JLabel("", SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(52, 44));
        avatar.setBorder(new LineBorder(new Color(150, 175, 205)));
        JLabel userText = new JLabel("<html><b>" + currentUser + "</b><br><span style='font-size:9px;'>" + getRoleName(currentUser) + "</span></html>");
        JButton logout = smallButton("Logout");
        logout.addActionListener(e -> showLoginPanel());
        userPanel.add(avatar, BorderLayout.WEST);
        userPanel.add(userText, BorderLayout.CENTER);
        userPanel.add(logout, BorderLayout.EAST);

        navigation.add(left, BorderLayout.WEST);
        navigation.add(userPanel, BorderLayout.EAST);
        add(navigation, BorderLayout.NORTH);
        revalidate();
        repaint();
    }

    private void removeNavigation() {
        if (navigation != null) {
            remove(navigation);
            navigation = null;
            revalidate();
            repaint();
        }
    }

    private void showStartPanelForUser() {
        if (currentUser instanceof Cashier) showPurchasesPanel();
        else if (currentUser instanceof Employee && !(currentUser instanceof ScreeningEmployee)) showUserProfilePanel();
        else showMoviesPanel();
    }

    private JButton navButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(222, 236, 255));
        button.setForeground(dark);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(128, 38));
        button.addActionListener(e -> action.run());
        return button;
    }

    private JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(blue);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        return button;
    }

    private JButton darkButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(dark);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 11));
        return button;
    }

    private JButton smallButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 11));
        return button;
    }

    private JPanel page(String title) {
        JPanel outer = new JPanel(new BorderLayout(12, 12));
        outer.setBackground(background);
        outer.setBorder(new EmptyBorder(16, 28, 24, 28));
        JLabel label = new JLabel(title);
        label.setFont(titleFont);
        label.setForeground(dark);
        outer.add(label, BorderLayout.NORTH);

        JPanel canvas = new JPanel(new BorderLayout(16, 16));
        canvas.setBackground(surface);
        canvas.setBorder(new EmptyBorder(26, 26, 26, 26));
        outer.add(canvas, BorderLayout.CENTER);
        return outer;
    }

    private JPanel innerOf(JPanel page) {
        return (JPanel) page.getComponent(1);
    }

    private JPanel card(String title) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(cardColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(214, 226, 238)),
                new EmptyBorder(14, 14, 14, 14)));
        if (title != null && !title.isBlank()) {
            JLabel label = new JLabel(title);
            label.setForeground(dark);
            label.setFont(sectionFont);
            panel.add(label, BorderLayout.NORTH);
        }
        return panel;
    }

    private JPanel infoCard(String title, String body, Color bg) {
        JPanel panel = card(title);
        panel.setBackground(bg);
        JTextArea area = textArea(bg);
        area.setText(body);
        panel.add(area, BorderLayout.CENTER);
        return panel;
    }

    private void setContent(JPanel panel) {
        content.removeAll();
        content.add(panel, BorderLayout.CENTER);
        content.revalidate();
        content.repaint();
    }

    private JPanel wrapInScroll(JPanel panel) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(surface);
        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getViewport().setBackground(surface);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel cardGridPanel() {
        JPanel grid = new JPanel(new GridLayout(0, 4, 18, 18));
        grid.setBackground(surface);
        return grid;
    }

    private void showAccessDenied(String message) {
        JPanel page = page("Access denied");
        innerOf(page).add(infoCard("Not available for current role", message, new Color(255, 245, 245)), BorderLayout.CENTER);
        setContent(page);
    }

    private void showMoviesPanel() {
        JPanel page = page("Browse Movies");
        JPanel main = innerOf(page);
        JPanel grid = cardGridPanel();

        if (currentUser instanceof ScreeningEmployee) {
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            actions.setBackground(surface);
            JButton addMovie = primaryButton("Add movie");
            addMovie.setPreferredSize(new Dimension(160, 34));
            addMovie.addActionListener(e -> showMovieFormDialog(null));
            actions.add(addMovie);
            main.add(actions, BorderLayout.NORTH);
        }

        List<Movie> movies = movieService.getMovies();
        for (Movie movie : movies) {
            JPanel movieCard = movieCard(movie, true);
            grid.add(movieCard);
        }
        main.add(wrapInScroll(grid), BorderLayout.CENTER);
        setContent(page);
    }

    private JPanel movieCard(Movie movie, boolean withButton) {
        JPanel movieCard = card(movie.getTitle());
        movieCard.setPreferredSize(new Dimension(280, 190));
        JTextArea body = textArea(cardColor);
        body.setText(movieShortInfo(movie));
        movieCard.add(body, BorderLayout.CENTER);
        if (withButton) {
            JButton check = darkButton("Check");
            check.setPreferredSize(new Dimension(210, 30));
            check.addActionListener(e -> showMovieDetailsPanel(movie.getId()));
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            buttonPanel.setBackground(cardColor);
            buttonPanel.add(check);
            movieCard.add(buttonPanel, BorderLayout.SOUTH);
        }
        return movieCard;
    }

    private void showMovieDetailsPanel(Integer movieId) {
        Movie movie = movieService.getMovieById(movieId);
        selectedMovieForNewScreening = movie;
        JPanel page = page("Movie details");
        JPanel main = innerOf(page);
        main.setLayout(new BorderLayout(26, 16));

        JPanel movieInfo = infoCard("Movie", movieLongInfo(movie), pinkBackground);
        movieInfo.setPreferredSize(new Dimension(280, 260));
        if (currentUser instanceof ScreeningEmployee) {
            JButton assign = primaryButton("Assign new screening");
            assign.setPreferredSize(new Dimension(190, 32));
            assign.addActionListener(e -> showAddScreeningPanel());
            JButton edit = darkButton("Edit movie");
            edit.setPreferredSize(new Dimension(130, 32));
            edit.addActionListener(e -> showMovieFormDialog(movie));
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
            p.setBackground(pinkBackground);
            p.add(assign);
            p.add(edit);
            movieInfo.add(p, BorderLayout.SOUTH);
        }

        JPanel screenings = card("Screenings");
        screenings.setPreferredSize(new Dimension(720, 300));
        JPanel screeningGrid = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        screeningGrid.setBackground(cardColor);
        for (Screening s : movie.getScreenings()) {
            JPanel sc = screeningSmallCard(s, currentUser instanceof Customer);
            sc.setPreferredSize(new Dimension(310, 130));
            screeningGrid.add(sc);
        }
        screenings.add(wrapWhiteScroll(screeningGrid), BorderLayout.CENTER);

        main.add(movieInfo, BorderLayout.WEST);
        main.add(screenings, BorderLayout.CENTER);
        setContent(page);
    }

    private JScrollPane wrapWhiteScroll(JPanel panel) {
        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(cardColor);
        return scroll;
    }

    private void showRoomsPanel() {
        if (!(currentUser instanceof ScreeningEmployee)) {
            showAccessDenied("Rooms and cinema schedule management are available only for Screening Employee.");
            return;
        }
        JPanel page = page("Browse Rooms");
        JPanel main = innerOf(page);
        JPanel grid = cardGridPanel();
        for (Room room : roomService.getRooms()) {
            JPanel roomCard = roomCard(room);
            roomCard.setPreferredSize(new Dimension(250, 170));
            JButton check = darkButton("Check");
            check.setPreferredSize(new Dimension(145, 30));
            check.addActionListener(e -> showRoomDetailsPanel(room.getId()));
            JPanel bp = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            bp.setBackground(greenBackground);
            bp.add(check);
            roomCard.add(bp, BorderLayout.SOUTH);
            grid.add(roomCard);
        }
        main.add(wrapInScroll(grid), BorderLayout.CENTER);
        setContent(page);
    }

    private void showRoomDetailsPanel(Integer roomId) {
        Room room = roomService.getRoomById(roomId);
        JPanel page = page("Room details");
        JPanel main = innerOf(page);
        main.setLayout(new BorderLayout(26, 16));
        JPanel roomPanel = roomCard(room);
        roomPanel.setPreferredSize(new Dimension(260, 220));
        main.add(roomPanel, BorderLayout.WEST);

        JPanel screenings = card("Screenings");
        JPanel grid = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        grid.setBackground(cardColor);
        for (Screening s : room.getScreenings()) {
            JPanel sc = screeningSmallCard(s, false);
            sc.setPreferredSize(new Dimension(320, 130));
            grid.add(sc);
        }
        screenings.add(wrapWhiteScroll(grid), BorderLayout.CENTER);
        main.add(screenings, BorderLayout.CENTER);
        setContent(page);
    }

    private void showScreeningsPanel() {
        JPanel page = page("Browse Screenings");
        JPanel main = innerOf(page);
        JPanel grid = cardGridPanel();
        for (Screening screening : screeningService.getScreenings()) {
            JPanel screeningCard = card(screening.getStartDateTime().toLocalDate().toString());
            screeningCard.setPreferredSize(new Dimension(280, 170));
            JTextArea body = textArea(cardColor);
            body.setText(screeningShortInfo(screening));
            JButton check = darkButton("Check");
            check.setPreferredSize(new Dimension(145, 30));
            check.addActionListener(e -> showScreeningDetailsPanel(screening.getId()));
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            buttonPanel.setBackground(cardColor);
            buttonPanel.add(check);
            screeningCard.add(body, BorderLayout.CENTER);
            screeningCard.add(buttonPanel, BorderLayout.SOUTH);
            grid.add(screeningCard);
        }
        main.add(wrapInScroll(grid), BorderLayout.CENTER);
        setContent(page);
    }

    private void showScreeningDetailsPanel(Integer screeningId) {
        Screening s = screeningService.getScreeningById(screeningId);
        JPanel page = page("Screening details");
        JPanel main = innerOf(page);
        main.setLayout(new GridLayout(1, 3, 26, 18));
        JPanel screening = infoCard("Screening", formatScreening(s), cardColor);
        if (currentUser instanceof Customer) {
            JButton buy = primaryButton("Buy ticket");
            buy.setPreferredSize(new Dimension(180, 32));
            buy.addActionListener(e -> buyTicket(s.getId()));
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
            p.setBackground(cardColor);
            p.add(buy);
            screening.add(p, BorderLayout.SOUTH);
        }
        main.add(screening);
        main.add(roomCard(s.getRoom()));
        main.add(infoCard("Of movie", movieLongInfo(s.getMovie()), pinkBackground));
        setContent(page);
    }

    private void showAddScreeningPanel() {
        if (!(currentUser instanceof ScreeningEmployee)) {
            showAccessDenied("Only Screening Employee can add a new screening.");
            return;
        }

        JPanel page = page("Assign New Screening");
        JPanel main = innerOf(page);
        main.setLayout(new BorderLayout(26, 18));

        JComboBox<Movie> movieBox = new JComboBox<>(movieService.getMovies().toArray(new Movie[0]));
        if (selectedMovieForNewScreening != null) selectMovieInBox(movieBox, selectedMovieForNewScreening.getId());
        JComboBox<Room> roomBox = new JComboBox<>(roomService.getRooms().toArray(new Room[0]));
        JComboBox<LocalDate> dateBox = new JComboBox<>();
        for (int i = 0; i < 7; i++) dateBox.addItem(LocalDate.now().plusDays(i));
        JTextField timeField = new JTextField("12:00");
        JTextField priceField = new JTextField("45");
        Dimension inputSize = new Dimension(240, 32);
        movieBox.setPreferredSize(inputSize);
        roomBox.setPreferredSize(inputSize);
        dateBox.setPreferredSize(inputSize);
        timeField.setPreferredSize(inputSize);
        priceField.setPreferredSize(inputSize);
        JTextArea occupied = textArea(cardColor);
        JLabel message = new JLabel(" ", SwingConstants.CENTER);

        JPanel leftColumn = new JPanel(new GridLayout(2, 1, 0, 14));
        leftColumn.setBackground(surface);
        leftColumn.setPreferredSize(new Dimension(255, 500));
        JPanel movieInfo = infoCard("Movie", "", pinkBackground);
        JPanel roomInfo = infoCard("Room", "", greenBackground);
        leftColumn.add(movieInfo);
        leftColumn.add(roomInfo);

        JPanel form = card("New screening");
        form.setPreferredSize(new Dimension(310, 360));
        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBackground(cardColor);
        GridBagConstraints fc = new GridBagConstraints();
        fc.gridx = 0;
        fc.weightx = 1;
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.insets = new Insets(3, 0, 3, 0);
        fc.gridy = 0; fields.add(new JLabel("Movie"), fc);
        fc.gridy = 1; fields.add(movieBox, fc);
        fc.gridy = 2; fields.add(new JLabel("Room"), fc);
        fc.gridy = 3; fields.add(roomBox, fc);
        fc.gridy = 4; fields.add(new JLabel("Date"), fc);
        fc.gridy = 5; fields.add(dateBox, fc);
        fc.gridy = 6; fields.add(new JLabel("Starting time"), fc);
        fc.gridy = 7; fields.add(timeField, fc);
        fc.gridy = 8; fields.add(new JLabel("Base ticket price"), fc);
        fc.gridy = 9; fields.add(priceField, fc);
        JButton create = primaryButton("Create screening");
        create.setPreferredSize(new Dimension(210, 34));
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bp.setBackground(cardColor);
        bp.add(create);
        form.add(fields, BorderLayout.CENTER);
        form.add(bp, BorderLayout.SOUTH);

        JPanel right = card("Occupied time slots");
        right.setPreferredSize(new Dimension(310, 360));
        right.add(new JScrollPane(occupied), BorderLayout.CENTER);

        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 28, 20));
        center.setBackground(surface);
        center.add(form);
        center.add(right);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(surface);
        message.setOpaque(true);
        message.setBackground(surface);
        message.setBorder(new EmptyBorder(8, 8, 8, 8));
        bottom.add(message, BorderLayout.CENTER);

        main.add(leftColumn, BorderLayout.WEST);
        main.add(center, BorderLayout.CENTER);
        main.add(bottom, BorderLayout.SOUTH);

        Runnable refresh = () -> {
            Movie m = (Movie) movieBox.getSelectedItem();
            Room r = (Room) roomBox.getSelectedItem();
            setInfoCardText(movieInfo, m == null ? "" : movieLongInfo(m));
            setInfoCardText(roomInfo, r == null ? "" : roomLongInfo(r));
            updateOccupiedTimes(roomBox, dateBox, occupied);
        };
        movieBox.addActionListener(e -> refresh.run());
        roomBox.addActionListener(e -> refresh.run());
        dateBox.addActionListener(e -> refresh.run());

        create.addActionListener(e -> {
            try {
                Movie movie = (Movie) movieBox.getSelectedItem();
                Room room = (Room) roomBox.getSelectedItem();
                LocalDate date = (LocalDate) dateBox.getSelectedItem();
                LocalTime time = LocalTime.parse(timeField.getText().trim());
                BigDecimal price = new BigDecimal(priceField.getText().trim());
                LocalDateTime start = LocalDateTime.of(date, time);
                Screening created = screeningService.createScreening(movie.getId(), room.getId(), start, price);
                showScreeningCreatedPanel(created.getId());
            } catch (Exception ex) {
                message.setForeground(new Color(150, 35, 35));
                message.setBackground(new Color(255, 235, 235));
                message.setText("Error: " + ex.getMessage());
            }
        });

        refresh.run();
        setContent(page);
    }

    private void showMovieFormDialog(Movie movie) {
        if (!(currentUser instanceof ScreeningEmployee)) return;
        boolean editMode = movie != null;
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        JTextField titleField = new JTextField(editMode ? movie.getTitle() : "New Movie");
        JTextField releaseField = new JTextField(editMode ? movie.getReleasedDate().toString() : LocalDate.now().toString());
        JTextField durationField = new JTextField(editMode ? String.valueOf(movie.getDuration().toMinutes()) : "120");
        JTextField minimumAgeField = new JTextField(editMode ? String.valueOf(getMinimumAge(movie)) : "12");
        JTextField genreField = new JTextField(editMode && !movie.getGenres().isEmpty() ? movie.getGenres().get(0).getName() : "Action");
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"AdultMovie", "ChildrenMovie"});
        if (editMode && movie instanceof ChildrenMovie) typeBox.setSelectedItem("ChildrenMovie");
        typeBox.setEnabled(!editMode);
        panel.add(new JLabel("Type:")); panel.add(typeBox);
        panel.add(new JLabel("Title:")); panel.add(titleField);
        panel.add(new JLabel("Released date yyyy-MM-dd:")); panel.add(releaseField);
        panel.add(new JLabel("Duration minutes:")); panel.add(durationField);
        panel.add(new JLabel("Minimum age:")); panel.add(minimumAgeField);
        panel.add(new JLabel("Genre:")); panel.add(genreField);

        int result = JOptionPane.showConfirmDialog(this, panel, editMode ? "Edit movie" : "Add movie", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;
        try {
            String title = titleField.getText().trim();
            LocalDate released = LocalDate.parse(releaseField.getText().trim());
            Integer duration = Integer.parseInt(durationField.getText().trim());
            Integer minimumAge = Integer.parseInt(minimumAgeField.getText().trim());
            String genre = genreField.getText().trim();
            if (editMode) {
                movieService.updateMovie(movie.getId(), title, released, duration, minimumAge, genre);
                showMovieDetailsPanel(movie.getId());
            } else {
                movieService.createMovie((String) typeBox.getSelectedItem(), title, released, duration, minimumAge, genre);
                showMoviesPanel();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void setInfoCardText(JPanel card, String text) {
        Component center = ((BorderLayout) card.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (center instanceof JTextArea area) area.setText(text);
    }

    private void showScreeningCreatedPanel(Integer screeningId) {
        Screening s = screeningService.getScreeningById(screeningId);
        JPanel page = page("Screening created successfully");
        JPanel main = innerOf(page);
        main.setLayout(new BorderLayout(18, 18));
        JLabel success = new JLabel("Success: the screening has been added and is now visible in the schedule.", SwingConstants.CENTER);
        success.setOpaque(true);
        success.setBackground(new Color(230, 250, 230));
        success.setBorder(new EmptyBorder(14, 14, 14, 14));
        success.setForeground(new Color(0, 110, 40));
        main.add(success, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 3, 26, 18));
        cards.setBackground(surface);
        cards.add(infoCard("Screening", formatScreening(s), cardColor));
        cards.add(roomCard(s.getRoom()));
        cards.add(infoCard(s.getMovie().getTitle(), movieLongInfo(s.getMovie()), pinkBackground));
        main.add(cards, BorderLayout.CENTER);
        setContent(page);
    }

    private void showPurchasesPanel() {
        if (!(currentUser instanceof Customer) && !(currentUser instanceof Cashier)) {
            showAccessDenied("Purchases are available for Customer and Cashier roles.");
            return;
        }
        JPanel page = page(currentUser instanceof Customer ? "My purchases" : "Customer purchases");
        JPanel main = innerOf(page);
        main.setLayout(new BorderLayout(18, 18));

        List<Purchase> purchases = currentUser instanceof Customer c
                ? purchaseService.getPurchasesForCustomer(c.getId())
                : purchaseService.getPurchases();

        DefaultListModel<Purchase> model = new DefaultListModel<>();
        purchases.forEach(model::addElement);
        JList<Purchase> list = new JList<>(model);
        JTextArea details = textArea(cardColor);
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && list.getSelectedValue() != null) details.setText(formatPurchase(list.getSelectedValue()));
        });

        JPanel left = card(currentUser instanceof Customer ? "Purchases made by current customer" : "All purchases");
        left.setPreferredSize(new Dimension(360, 300));
        left.add(new JScrollPane(list), BorderLayout.CENTER);
        JPanel right = card("Purchase details");
        right.add(new JScrollPane(details), BorderLayout.CENTER);

        if (currentUser instanceof Cashier) {
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
            actions.setBackground(cardColor);
            JButton confirm = primaryButton("Confirm");
            JButton cancel = primaryButton("Cancel");
            JButton archive = primaryButton("Archive");
            actions.add(confirm); actions.add(cancel); actions.add(archive);
            confirm.addActionListener(e -> changePurchaseStatus(list, PaymentStatus.CONFIRMED));
            cancel.addActionListener(e -> changePurchaseStatus(list, PaymentStatus.CANCELLED));
            archive.addActionListener(e -> changePurchaseStatus(list, PaymentStatus.ARCHIVED));
            right.add(actions, BorderLayout.SOUTH);
        } else if (currentUser instanceof Customer) {
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
            actions.setBackground(cardColor);
            JButton cancel = primaryButton("Cancel purchase");
            cancel.addActionListener(e -> changePurchaseStatus(list, PaymentStatus.CANCELLED));
            actions.add(cancel);
            right.add(actions, BorderLayout.SOUTH);
        }

        main.add(left, BorderLayout.WEST);
        main.add(right, BorderLayout.CENTER);
        setContent(page);
    }

    private void showUserProfilePanel() {
        JPanel page = page("User profile");
        JPanel main = innerOf(page);
        main.setLayout(new BorderLayout(18, 18));
        String text;
        if (currentUser instanceof Customer c) text = formatCustomer(userService.getCustomerById(c.getId()));
        else if (currentUser instanceof Employee e) text = formatEmployee(userService.getEmployeeById(e.getId()));
        else text = formatPerson(currentUser);
        JPanel profile = infoCard(currentUser + " - " + getRoleName(currentUser), text, cardColor);
        profile.setPreferredSize(new Dimension(520, 380));
        main.add(profile, BorderLayout.WEST);
        setContent(page);
    }

    private void buyTicket(Integer screeningId) {
        if (!(currentUser instanceof Customer customer)) {
            JOptionPane.showMessageDialog(this, "Only Customer can buy tickets.");
            return;
        }
        Screening screening = screeningService.getScreeningById(screeningId);
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        JTextField seatField = new JTextField("1");
        JComboBox<PaymentType> paymentBox = new JComboBox<>(PaymentType.values());
        JComboBox<Coupon> couponBox = new JComboBox<>();
        couponBox.addItem(null);
        for (Coupon coupon : purchaseService.getAvailableCoupons()) couponBox.addItem(coupon);
        couponBox.setRenderer((list, value, index, isSelected, cellHasFocus) -> new JLabel(value == null ? "No coupon" : value.toString()));
        panel.add(new JLabel("Screening:")); panel.add(new JLabel(screening.getMovie().getTitle()));
        panel.add(new JLabel("Room:")); panel.add(new JLabel(screening.getRoom().toString()));
        panel.add(new JLabel("Seat number:")); panel.add(seatField);
        panel.add(new JLabel("Payment type:")); panel.add(paymentBox);
        panel.add(new JLabel("Coupon:")); panel.add(couponBox);
        int answer = JOptionPane.showConfirmDialog(this, panel, "Make ticket purchase", JOptionPane.OK_CANCEL_OPTION);
        if (answer != JOptionPane.OK_OPTION) return;
        try {
            int seat = Integer.parseInt(seatField.getText().trim());
            PaymentType type = (PaymentType) paymentBox.getSelectedItem();
            Coupon coupon = (Coupon) couponBox.getSelectedItem();
            Integer couponId = coupon == null ? null : coupon.getId();
            Purchase purchase = purchaseService.createPurchaseForScreening(customer.getId(), screeningId, seat, type, couponId);
            int payment = JOptionPane.showConfirmDialog(this,
                    "Purchase created with status PENDING. Simulate successful payment now?",
                    "Finalize purchase", JOptionPane.YES_NO_OPTION);
            if (payment == JOptionPane.YES_OPTION) purchaseService.finalizePurchase(purchase.getId());
            JOptionPane.showMessageDialog(this, "Purchase saved. You can see it in My purchases.");
            showPurchasesPanel();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void changePurchaseStatus(JList<Purchase> list, PaymentStatus status) {
        Purchase selected = list.getSelectedValue();
        if (selected == null) return;
        if (status == PaymentStatus.CONFIRMED) purchaseService.finalizePurchase(selected.getId());
        if (status == PaymentStatus.CANCELLED) purchaseService.cancelPurchase(selected.getId());
        if (status == PaymentStatus.ARCHIVED) purchaseService.archivePurchase(selected.getId());
        showPurchasesPanel();
    }

    private void updateOccupiedTimes(JComboBox<Room> roomBox, JComboBox<LocalDate> dateBox, JTextArea occupied) {
        Room room = (Room) roomBox.getSelectedItem();
        LocalDate date = (LocalDate) dateBox.getSelectedItem();
        if (room == null || date == null) return;
        List<Screening> screenings = screeningService.getScreeningsInRoomByDate(room, date);
        StringBuilder sb = new StringBuilder();
        if (screenings.isEmpty()) sb.append("No screenings planned.");
        for (Screening s : screenings) {
            sb.append(s.getStartDateTime().toLocalTime()).append(" - ")
                    .append(s.getEndDateTime().toLocalTime()).append(" | ")
                    .append(s.getMovie().getTitle()).append("\n");
        }
        occupied.setText(sb.toString());
    }

    private JPanel screeningSmallCard(Screening s, boolean showBuyButton) {
        JPanel p = card(s.getStartDateTime().toLocalDate().toString());
        JTextArea text = textArea(cardColor);
        text.setText(screeningShortInfo(s));
        p.add(text, BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        buttons.setBackground(cardColor);
        JButton info = darkButton("Details");
        info.setPreferredSize(new Dimension(110, 28));
        info.addActionListener(e -> showScreeningDetailsPanel(s.getId()));
        buttons.add(info);
        if (showBuyButton) {
            JButton buy = primaryButton("Buy ticket");
            buy.setPreferredSize(new Dimension(110, 28));
            buy.addActionListener(e -> buyTicket(s.getId()));
            buttons.add(buy);
        }
        p.add(buttons, BorderLayout.SOUTH);
        return p;
    }

    private JPanel roomCard(Room room) {
        return infoCard("Room " + room.getRoomNumber(), roomLongInfo(room), greenBackground);
    }

    private JTextArea textArea(Color bg) {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(normalFont);
        area.setBackground(bg);
        area.setBorder(null);
        return area;
    }

    private void selectMovieInBox(JComboBox<Movie> box, Integer movieId) {
        for (int i = 0; i < box.getItemCount(); i++) {
            if (box.getItemAt(i).getId().equals(movieId)) {
                box.setSelectedIndex(i);
                return;
            }
        }
    }

    private String movieShortInfo(Movie movie) {
        return "Released: " + movie.getReleasedDate() + "\n" +
                "Genre: " + movie.getGenres() + "\n" +
                "Duration: " + movie.getDuration().toMinutes() + " minutes\n" +
                "Min age: " + getMinimumAge(movie);
    }

    private String movieLongInfo(Movie movie) {
        String extra = "";
        if (movie instanceof AdultMovie adult) extra = "Alcohol allowed: " + adult.getAlcoholsAllowed();
        if (movie instanceof ChildrenMovie child) extra = "Snacks allowed: " + child.getSnacksAllowed();
        return "Title: " + movie.getTitle() + "\n" +
                "Released: " + movie.getReleasedDate() + "\n" +
                "Genre: " + movie.getGenres() + "\n" +
                "Duration: " + movie.getDuration().toMinutes() + " minutes\n" +
                "Min age: " + getMinimumAge(movie) + "\n" + extra;
    }

    private String roomLongInfo(Room room) {
        String extra = "";
        if (room instanceof Room2D r) extra = "Projection: " + r.getProjectionType();
        if (room instanceof IMAX r) extra = "Screen: " + r.getScreenHeight() + " x " + r.getScreenWidth();
        if (room instanceof Dolby r) extra = "Vibrating seats: " + r.getVibratingSeats();
        return "Type: " + room.getRoomType() + "\n" +
                "Seats: " + room.getNumberOfSeats() + "\n" +
                "Seats / row: " + room.getSeatPerRow() + "\n" + extra;
    }

    private String screeningShortInfo(Screening s) {
        return "Time: " + s.getStartDateTime().toLocalTime() + " - " + s.getEndDateTime().toLocalTime() + "\n" +
                "Movie: " + s.getMovie().getTitle() + "\n" +
                "Room: " + s.getRoom().getRoomNumber() + "\n" +
                "Base ticket price: " + s.getDefaultTicketPrice() + " PLN";
    }

    private String formatScreening(Screening s) {
        return "Date: " + s.getStartDateTime().toLocalDate() + "\n" +
                "Time: " + s.getStartDateTime().toLocalTime() + " - " + s.getEndDateTime().toLocalTime() + "\n" +
                "Movie: " + s.getMovie().getTitle() + "\n" +
                "Room: " + s.getRoom() + "\n" +
                "Basic ticket price: " + s.getDefaultTicketPrice() + " PLN";
    }

    private String formatPerson(Person person) {
        return "Person ID: " + person.getId() + "\n" +
                "Name: " + person.getName() + "\n" +
                "Surname: " + person.getSurname() + "\n" +
                "Phone number: " + person.getPhoneNumber() + "\n" +
                "Date of birth: " + person.getDateOfBirth() + "\n" +
                "Email: " + person.getEmail() + "\n" +
                "Role: " + getRoleName(person);
    }

    private String formatCustomer(Customer customer) {
        StringBuilder sb = new StringBuilder(formatPerson(customer));
        sb.append("\nCustomer since: ").append(customer.getCustomerSince()).append("\n\nPurchases:\n");
        if (customer.getPurchases().isEmpty()) sb.append("No purchases.");
        for (Purchase p : customer.getPurchases()) sb.append("- ").append(p.getPurchaseNumber()).append(" | ").append(p.getPaymentStatus()).append(" | ").append(p.getTotalPrice()).append(" PLN\n");
        return sb.toString();
    }

    private String formatEmployee(Employee employee) {
        StringBuilder sb = new StringBuilder(formatPerson(employee));
        sb.append("\nBase salary: ").append(employee.getBaseSalary()).append(" PLN");
        sb.append("\nCurrent salary: ").append(employee.getCurrentSalary()).append(" PLN");
        sb.append("\nStart date: ").append(employee.getStartDate());
        if (employee instanceof ScreeningEmployee se) sb.append("\nEquipment training completed: ").append(se.getEquipmentTrainingCompleted());
        if (employee instanceof Cashier cashier) sb.append("\nLanguages spoken: ").append(cashier.getLanguagesSpoken());
        if (employee.getFulltime() != null) sb.append("\nEmployment form: Fulltime, weekly hours: ").append(employee.getFulltime().getWeeklyHours());
        if (employee.getParttime() != null) sb.append("\nEmployment form: Parttime, shift: ").append(employee.getParttime().getShift());
        return sb.toString();
    }

    private String formatPurchase(Purchase p) {
        StringBuilder sb = new StringBuilder();
        sb.append("Purchase number: ").append(p.getPurchaseNumber()).append("\n");
        sb.append("Customer: ").append(p.getCustomer()).append("\n");
        sb.append("Payment type: ").append(p.getPaymentType()).append("\n");
        sb.append("Payment status: ").append(p.getPaymentStatus()).append("\n");
        sb.append("Last modification: ").append(p.getLastModification().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("\n");
        sb.append("Total price: ").append(p.getTotalPrice()).append(" PLN\n\nTickets:\n");
        for (Ticket t : p.getTickets()) {
            sb.append("- Seat ").append(t.getSeatNumber()).append(" | ")
                    .append(t.getScreening().getMovie().getTitle()).append(" | ")
                    .append(t.getScreening().getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                    .append(" | Room ").append(t.getScreening().getRoom().getRoomNumber()).append("\n");
        }
        return sb.toString();
    }

    private Integer getMinimumAge(Movie movie) {
        if (movie instanceof AdultMovie adult) return adult.getMinimumAge();
        if (movie instanceof ChildrenMovie child) return child.getMinimumAge();
        return 0;
    }

    private String getRoleName(Person person) {
        if (person instanceof ScreeningEmployee) return "Screening Employee";
        if (person instanceof Cashier) return "Cashier";
        if (person instanceof Customer) return "Customer";
        if (person instanceof Employee) return "Employee";
        return "Person";
    }
}
