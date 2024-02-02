package imagesort.file;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.file.FileSystemDirectory;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Stream;

public class FileManager {

    public Boolean endsWith(String file, String ext) {
        return file.toLowerCase().endsWith(ext);
    }

    public Boolean isValid(Path file) {
        val name = file.toString();
        return endsWith(name, ".png")
                || endsWith(name, ".jpg")
                || endsWith(name, ".jpeg")
                || endsWith(name, ".mp4");
    }

    public String getFileType (String file) {
        int i = file.lastIndexOf('.');
        return file.substring(i + 1).toLowerCase();
    }

    public List<Path> getFiles(String path) throws IOException {
        Stream<Path> files = Files.walk(Paths.get(path));
        try (files) {
            return files.filter(Files::isRegularFile)
                    .filter(this::isValid)
                    .toList();
        }
    }

    public static void readImageMetadata(File image) throws ImageProcessingException, IOException {
        Metadata metadata = ImageMetadataReader.readMetadata(image);
        for (Directory directory : metadata.getDirectories()) {
            System.out.println("--------------------------------");
            System.out.println(directory + " | " + directory.getClass());
            for (Tag tag : directory.getTags()) {
                System.out.println(tag.getTagType() + " | " + tag);
            }
        }
        System.out.println("+++++++++++++++++++++++++++++++++");
    }

    public Optional<Calendar> getDate(Path image) throws ImageProcessingException, IOException {
        Metadata metadata = ImageMetadataReader.readMetadata(image.toFile());
        val directory = metadata.getFirstDirectoryOfType(FileSystemDirectory.class);
        System.out.println(directory);

        if (directory == null) {
            System.out.println("Directory not found");
            return Optional.empty();
        }

        val type  = getFileType(image.toString());
        System.out.println(type);
        val date = directory.getDate(FileSystemDirectory.TAG_FILE_MODIFIED_DATE);

        if (date == null) {
            System.out.println("Tag not found");
            return Optional.empty();
        }

        val cal = Calendar.getInstance();
        cal.setTime(date);
        System.out.println(cal.get(Calendar.DATE) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR));
        return Optional.of(cal);
    }

    public void moveFile(Path file, Calendar cal) throws IOException {
        System.out.println(file.getFileName());
        val date = String.format("%02d", cal.get(Calendar.DATE));
        val month = String.format("%02d", cal.get(Calendar.MONTH));
        val monthName = Month.of(cal.get(Calendar.MONTH))
                .getDisplayName(
                        TextStyle.FULL,
                        Locale.ENGLISH
                );
        val year = Integer.toString(cal.get(Calendar.YEAR));
        val newPath = Path.of(file.getParent().toString(), year, month+"-"+monthName, date);
        Files.createDirectories(newPath);
        val newFile = Path.of(newPath.toString(), file.getFileName().toString());
        System.out.println(newFile);
        Files.copy(file, newFile);
    }
}
