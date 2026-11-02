package main;

import javax.swing.SwingUtilities;

public class Main {

	public static void main(String[] args) {
        // Swing utilities safely starts the UI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
