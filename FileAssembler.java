import java.io.*;
import java.nio.file.*;

public class FileAssembler {

    public static void reassembleFile(String outputFileName) {
        String absoluteDirectoryPath = Outils.absoluteDirectoryPath;
        File directory = new File(absoluteDirectoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
            System.out.println("Répertoire '" + absoluteDirectoryPath + "' créé.");
        }

        String outputFilePath = absoluteDirectoryPath + File.separator + outputFileName;

        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFilePath))) {
            String[] partFileNames = { "part23456.txt", "part34567.txt", "part45678.txt" };
            for (String partFileName : partFileNames) {
                byte[] partData = Files.readAllBytes(Paths.get(partFileName));
                outputStream.write(partData);
                System.out.println("Ajout de " + partFileName + " au fichier final.");
            }

            System.out.println("Fichier reconstitué avec succès sous " + outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

  
}
