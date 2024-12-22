import javax.swing.*;
import java.awt.*;

public class FileTransferApp extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public FileTransferApp() {
        setTitle("FILEZILLA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new AuthenticationPanel(), "AuthPanel");
        mainPanel.add(new ApplicationPanel(), "AppPanel");

        add(mainPanel);
        setLocationRelativeTo(null);
        
        cardLayout.show(mainPanel, "AuthPanel");
    }

    private class AuthenticationPanel extends JPanel {
        private JTextField usernameField;
        private JPasswordField passwordField;

        public AuthenticationPanel() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel usernameLabel = new JLabel("Nom d'utilisateur:");
            usernameField = new JTextField(20);

            JLabel passwordLabel = new JLabel("Mot de passe:");
            passwordField = new JPasswordField(20);

            JButton loginButton = new JButton("Se connecter");
            loginButton.addActionListener(e -> authenticate());

            gbc.gridx = 0;
            gbc.gridy = 0;
            add(usernameLabel, gbc);

            gbc.gridx = 1;
            add(usernameField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            add(passwordLabel, gbc);

            gbc.gridx = 1;
            add(passwordField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            add(loginButton, gbc);
        }

        private void authenticate() {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if ("admin".equals(username) && "password123".equals(password)) {
                cardLayout.show(mainPanel, "AppPanel");
            } else {
                JOptionPane.showMessageDialog(this, "Nom d'utilisateur ou mot de passe incorrect.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class ApplicationPanel extends JPanel {
        public ApplicationPanel() {
            setLayout(new BorderLayout());
            JTextField filePathTextField = new JTextField("FILEZILLA");
            add(filePathTextField, BorderLayout.NORTH);

            JPanel clientPanel = new ClientPanel();
            JPanel receiverPanel = new ReceiverPanel();

            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, clientPanel, receiverPanel);
            splitPane.setDividerLocation(450);
            splitPane.setResizeWeight(0.5);

            add(splitPane, BorderLayout.CENTER);
        }
    }

    private static class ClientPanel extends JPanel {
        public ClientPanel() {
            setLayout(new BorderLayout());
            FileClientSwing clientSwing = new FileClientSwing();
            clientSwing.setVisible(true);
            add(clientSwing.getContentPane(), BorderLayout.CENTER);
        }
    }

    private static class ReceiverPanel extends JPanel {
        public ReceiverPanel() {
            setLayout(new BorderLayout());
            FileReceiverSwing receiverSwing = new FileReceiverSwing();
            receiverSwing.setVisible(true);
            add(receiverSwing.getContentPane(), BorderLayout.CENTER);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FileTransferApp app = new FileTransferApp();
            app.setVisible(true);
        });
    }
}
