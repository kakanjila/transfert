import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.ArrayList;


public class FileReceiverSwing extends JFrame {
    private JButton refreshButton;
    private JButton downloadButton;
    private JButton deleteButton;
    private JList<String> fileList;
    private DefaultListModel<String> listModel;
    private JLabel statusLabel;
    private final String serverIp = Outils.SERVER_IP_SWING;
    private final int serverPort = Outils.PORT_SWING;

    public FileReceiverSwing() {
        setTitle("Client de Fichiers");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(fileList);

        refreshButton = new JButton("Rafraîchir");
        downloadButton = new JButton("Télécharger");
        deleteButton = new JButton("Supprimer");
        downloadButton.setEnabled(false);
        deleteButton.setEnabled(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(downloadButton);
        buttonPanel.add(deleteButton);

        statusLabel = new JLabel("Statut : prêt.");

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(statusLabel, BorderLayout.NORTH);

        refreshButton.addActionListener(e -> loadFileList());
        downloadButton.addActionListener(e -> downloadSelectedFile());
        deleteButton.addActionListener(e -> deleteSelectedFile());
        fileList.addListSelectionListener(e -> {
            boolean fileSelected = fileList.getSelectedValue() != null;
            downloadButton.setEnabled(fileSelected);
            deleteButton.setEnabled(fileSelected);
        });

        setSize(500, 350);
        setLocationRelativeTo(null);
    }

    private void loadFileList() {
        listModel.clear();
        statusLabel.setText("Chargement des fichiers...");
        try (Socket socket = new Socket(serverIp, serverPort);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

            output.writeUTF("list");
            String response = input.readUTF();

            if (response.equals("OK")) {
                ArrayList<String> files = new ArrayList<>();
                while (true) {
                    String fileName = input.readUTF();
                    if (fileName == null || fileName.isEmpty()) break; 
                    files.add(fileName);
                }
                if (!files.isEmpty()) {
                    files.forEach(listModel::addElement);
                    statusLabel.setText("Fichiers chargés : " + files.size());
                } else {
                    statusLabel.setText("Aucun fichier disponible.");
                }
            } else {
                statusLabel.setText(response);
            }
        } catch (IOException ex) {
            statusLabel.setText("Erreur : Impossible de charger les fichiers.");
            ex.printStackTrace();
        }
    }

    private void downloadSelectedFile() {
        String selectedFile = fileList.getSelectedValue();
        if (selectedFile == null) {
            statusLabel.setText("Aucun fichier sélectionné.");
            return;
        }

        statusLabel.setText("Téléchargement en cours : " + selectedFile);

        try (Socket socket = new Socket(serverIp, serverPort);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

            output.writeUTF(selectedFile);
            String response = input.readUTF();

            if (response.equals("OK")) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Enregistrer le fichier sous");
                fileChooser.setSelectedFile(new File(selectedFile));

                int userChoice = fileChooser.showSaveDialog(this);
                if (userChoice == JFileChooser.APPROVE_OPTION) {
                    File destinationFile = fileChooser.getSelectedFile();

                    try (FileOutputStream fileOutput = new FileOutputStream(destinationFile)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = input.read(buffer)) != -1) {
                            fileOutput.write(buffer, 0, bytesRead);
                        }
                        statusLabel.setText("Fichier téléchargé : " + destinationFile.getAbsolutePath());
                    }
                } else {
                    statusLabel.setText("Téléchargement annulé.");
                }
            } else {
                statusLabel.setText("Erreur : " + response);
            }
        } catch (IOException ex) {
            statusLabel.setText("Erreur lors du téléchargement.");
            ex.printStackTrace();
        }
    }

    private void deleteSelectedFile() {
        String selectedFile = fileList.getSelectedValue();
        if (selectedFile == null) {
            statusLabel.setText("Aucun fichier sélectionné.");
            return;
        }

        statusLabel.setText("Suppression en cours : " + selectedFile);

        try (Socket socket = new Socket(serverIp, serverPort);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

            output.writeUTF("delete " + selectedFile);
            String response = input.readUTF();

            if (response.startsWith("Fichier supprimé")) {
                listModel.removeElement(selectedFile);
                statusLabel.setText(response);
            } else {
                statusLabel.setText("Erreur : " + response);
            }
        } catch (IOException ex) {
            statusLabel.setText("Erreur lors de la suppression.");
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FileReceiverSwing client = new FileReceiverSwing();
            client.setVisible(true);
        });
    }
}
