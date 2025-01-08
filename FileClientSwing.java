import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileClientSwing extends JFrame {
    private JButton browseButton;
    private JButton sendButton;
    private JTextField filePathTextField;
    private JLabel statusLabel;
    private File selectedFile;

    public File getSelectedFile() {
        return selectedFile;
    }

    public FileClientSwing() {
        setLayout(new FlowLayout());

        filePathTextField = new JTextField(30);
        filePathTextField.setEditable(false);

        browseButton = new JButton("Sélectionner le fichier");
        browseButton.addActionListener(new BrowseButtonListener());

        sendButton = new JButton("Envoyer le fichier");
        sendButton.setEnabled(false);
        sendButton.addActionListener(new SendButtonListener());

        statusLabel = new JLabel("Sélectionnez un fichier pour commencer.");

        add(filePathTextField);
        add(browseButton);
        add(sendButton);
        add(statusLabel);
        setLocationRelativeTo(null);
        setTitle("Client de Transfert de Fichier");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 150);
    }

    private class BrowseButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Sélectionner un fichier");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Tous les fichiers", "*.*"));

            int result = fileChooser.showOpenDialog(FileClientSwing.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                filePathTextField.setText(selectedFile.getAbsolutePath());
                sendButton.setEnabled(true); 
            }
        }
    }

    private class SendButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (selectedFile != null) {
                sendButton.setEnabled(false);
                statusLabel.setText("Envoi du fichier en cours...");

                new Thread(new FileSender(selectedFile)).start();
            }
        }
    }

    private class FileSender implements Runnable {
        private File file;

        public FileSender(File file) {
            this.file = file;
        }

        public void run() {
            try (Socket socket = new Socket(Outils.CLIENT_HOST_swing, Outils.CLIENT_PORT_swing); 
                 OutputStream outputStream = socket.getOutputStream();
                 FileInputStream fileInputStream = new FileInputStream(file)) {

                PrintWriter writer = new PrintWriter(outputStream, true);
                writer.println(file.getName());  

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Fichier envoyé avec succès.");
                    sendButton.setEnabled(true);
                });
            } catch (IOException ex) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Erreur lors de l'envoi du fichier.");
                    sendButton.setEnabled(true);
                });
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FileClientSwing client = new FileClientSwing();
            client.setVisible(true);
        });
    }
}
