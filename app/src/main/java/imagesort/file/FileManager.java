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

    public Optional<Calendar> getDate(File image) throws ImageProcessingException, IOException {
        Metadata metadata = ImageMetadataReader.readMetadata(image);
        val directory = metadata.getFirstDirectoryOfType(FileSystemDirectory.class);
        System.out.println(directory);

        if (directory == null) {
            System.out.println("Directory not found");
            return Optional.empty();
        }

        val type  = getFileType(image.getName());
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
}
