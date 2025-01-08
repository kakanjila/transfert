import java.io.*;
import java.net.*;
import java.net.*;

public class FileReceiverServer {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java FileReceiverServer <port>");
            return;
        }

        int serverPort = Integer.parseInt(args[0]);
        String partFileName = "part" + serverPort + ".txt"; 

        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            System.out.println("Serveur en écoute sur le port " + serverPort + "...");
            try (Socket socket = serverSocket.accept();
                 InputStream inputStream = socket.getInputStream();
                 FileOutputStream fileOutputStream = new FileOutputStream(partFileName)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
                System.out.println("Partie reçue et enregistrée sous " + partFileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
