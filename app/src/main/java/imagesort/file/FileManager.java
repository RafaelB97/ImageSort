package imagesort.file;

import com.drew.imaging.ImageProcessingException;
import lombok.val;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class FileManager {
    final private Path source;
    final private List<ImageFile> paths;

    public FileManager(String path) throws IOException {
        this.source = Path.of(path);
        this.paths = getFiles();
    }

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

    private List<ImageFile> getFiles() throws IOException {
        Stream<Path> files = Files.walk(this.source);
        try (files) {
            return files.filter(Files::isRegularFile)
                    .filter(this::isValid)
                    .map(file -> {
                        try {
                            return new ImageFile(file, source);
                        } catch (ImageProcessingException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        }
    }

    public void printAllImagesAllMetadata() {
        paths.forEach(ImageFile::printAllMetadata);
    }

    public void moveAllImages() throws IOException {
        for (ImageFile imageFile : paths) {
            imageFile.move();
        }
    }
}
