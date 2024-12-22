import java.io.*;
import java.net.*;

public class MainServer {
    public static void main(String[] args) {
        int mainServerPort = Outils.mainServerPort; 
        String[] serverAddresses = {Outils.serverAddresses, Outils.serverAddresses,Outils.serverAddresses};  
        int[] serverPorts =Outils.SERVER_PORTS ; 
        try (ServerSocket serverSocket = new ServerSocket(mainServerPort)) {
            System.out.println("Serveur principal en attente de connexion...");
            try (Socket clientSocket = serverSocket.accept();
                InputStream inputStream = clientSocket.getInputStream()) {
                BufferedReader reader= new BufferedReader(new InputStreamReader(inputStream));
                String outputFileName = reader.readLine(); 
                System.out.println("Nom du fichier reçu : " + outputFileName);
                byte[] fileData = inputStream.readAllBytes();
                System.out.println("Fichier reçu, taille: " + fileData.length);
                int partSize = fileData.length / 3;
                byte[] part1 = new byte[partSize];
                byte[] part2 = new byte[partSize];
                byte[] part3 = new byte[fileData.length - 2 * partSize]; 

                System.arraycopy(fileData, 0, part1, 0, partSize); 
                System.arraycopy(fileData, partSize, part2, 0, partSize); 
                System.arraycopy(fileData, 2 * partSize, part3, 0, part3.length);

                System.out.println("Fichier divisé en 3 parties et prêt à être envoyé.");

                sendFilePart(serverAddresses[0], serverPorts[0], part1, "part1_" + outputFileName);
                sendFilePart(serverAddresses[1], serverPorts[1], part2, "part2_" + outputFileName);
                sendFilePart(serverAddresses[2], serverPorts[2], part3, "part3_" + outputFileName);

                System.out.println("Fichier divisé et envoyé aux serveurs secondaires.");

                FileAssembler.reassembleFile(outputFileName);  

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendFilePart(String serverAddress, int serverPort, byte[] partData, String partName) {
        try (Socket socket = new Socket(serverAddress, serverPort);
            OutputStream outputStream = socket.getOutputStream()) {
            System.out.println("Envoi de " + partName + " à " + serverAddress + ":" + serverPort);
            outputStream.write(partData);
            System.out.println(partName + " envoyé avec succès.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
