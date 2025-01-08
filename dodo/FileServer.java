import java.io.*;
import java.net.*;
import java.net.*;

public class FileServer {
    private static final String DIRECTORY_PATH = Outils.repertoire_partage;
    private static final int PORT = Outils.PORT_SWING;

    public static void main(String[] args) {
        File directory = new File(DIRECTORY_PATH);
        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("Le répertoire spécifié n'existe pas ou n'est pas un dossier : " + DIRECTORY_PATH);
            return;
        }

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serveur en écoute sur le port " + PORT);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     DataInputStream input = new DataInputStream(clientSocket.getInputStream());
                     DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream())) {

                    System.out.println("Client connecté depuis : " + clientSocket.getInetAddress());

                    String command = input.readUTF();
                    if (command.equals("list")) {
                        sendFileList(output, directory);
                    } else if (command.startsWith("delete ")) {
                        String fileName = command.substring(7);
                        deleteFile(output, fileName, directory);
                    } else {
                        sendFile(output, command, directory);
                    }
                } catch (IOException e) {
                    System.err.println("Erreur avec un client : " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du démarrage du serveur : " + e.getMessage());
        }
    }

    /**
     * Envoie la liste des fichiers disponibles au client.
     */
    private static void sendFileList(DataOutputStream output, File directory) throws IOException {
        File[] files = directory.listFiles();
        if (files != null && files.length > 0) {
            output.writeUTF("OK"); // Confirmer que la liste peut être envoyée
            for (File file : files) {
                if (file.isFile()) {
                    output.writeUTF(file.getName());
                }
            }
            output.writeUTF(""); 
        } else {
            output.writeUTF("Aucun fichier disponible.");
        }
    }

    
    private static void deleteFile(DataOutputStream output, String fileName, File directory) throws IOException {
        File fileToDelete = new File(directory, fileName);
        if (fileToDelete.exists() && fileToDelete.isFile()) {
            if (fileToDelete.delete()) {
                output.writeUTF("Fichier supprimé avec succès : " + fileName);
                System.out.println("Fichier supprimé : " + fileName);
            } else {
                output.writeUTF("Erreur : impossible de supprimer le fichier.");
                System.err.println("Erreur lors de la suppression du fichier : " + fileName);
            }
        } else {
            output.writeUTF("Fichier introuvable : " + fileName);
            System.err.println("Fichier demandé pour suppression non trouvé : " + fileName);
        }
    }


    private static void sendFile(DataOutputStream output, String fileName, File directory) throws IOException {
        File requestedFile = new File(directory, fileName);
        if (requestedFile.exists() && requestedFile.isFile()) {
            output.writeUTF("OK"); 

            try (FileInputStream fileInput = new FileInputStream(requestedFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInput.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }
            System.out.println("Fichier envoyé : " + requestedFile.getName());
        } else {
            output.writeUTF("Fichier introuvable.");
            System.err.println("Fichier demandé non trouvé : " + fileName);
        }
    }
}
