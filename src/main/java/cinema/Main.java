package cinema;

import cinema.gui.MainFrame;
import cinema.service.DataSeeder;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        new DataSeeder().seed();
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
