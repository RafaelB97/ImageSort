package imagesort.file;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.file.FileSystemDirectory;
import lombok.val;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ImageFile {
    final private Path file;
    final private Path target;
    final private Metadata metadata;
    final private String extension;
    final private Calendar calendar;

    public ImageFile(Path file) throws ImageProcessingException, IOException {
        System.out.println(file);
        this.file = file;
        this.target = file.getParent();
        this.extension = setType(file.toString());
        this.metadata = ImageMetadataReader.readMetadata(this.file.toFile());
        this.calendar = setDate();
    }

    public ImageFile(Path file, Path target) throws ImageProcessingException, IOException {
        System.out.println(file);
        this.file = file;
        this.target = target;
        this.extension = setType(file.toString());
        this.metadata = ImageMetadataReader.readMetadata(this.file.toFile());
        this.calendar = setDate();
    }

    private String setType(String file) {
        int i = file.lastIndexOf('.');
        return file.substring(i + 1).toLowerCase();
    }

    private Calendar setDate() {
        Directory directory = this.metadata.getFirstDirectoryOfType(FileSystemDirectory.class);
        System.out.println(directory);

        if (directory == null) {
            System.out.println("Directory not found");
            return null;
        }

        System.out.println(this.extension);
        Date date = directory.getDate(FileSystemDirectory.TAG_FILE_MODIFIED_DATE);

        if (date == null) {
            System.out.println("Tag not found");
            return null;
        }

        val cal = Calendar.getInstance();
        cal.setTime(date);
        System.out.println(cal.get(Calendar.DATE) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR));
        return cal;
    }

    public void move() throws IOException {
        System.out.println(this.file.getFileName());
        val date = String.format("%02d", this.calendar.get(Calendar.DATE));
        val month = String.format("%02d", this.calendar.get(Calendar.MONTH) + 1);
        String monthName = Month.of(this.calendar.get(Calendar.MONTH) + 1)
                .getDisplayName(
                        TextStyle.FULL,
                        Locale.ENGLISH
//                        new Locale("es", "MX")
                );
        val year = Integer.toString(this.calendar.get(Calendar.YEAR));
        val newPath = Path.of(this.target.toString(), year, month+"-"+monthName, date);
        Files.createDirectories(newPath);
        val newFile = Path.of(newPath.toString(), this.file.getFileName().toString());
        System.out.println(newFile);
        Files.move(this.file, newFile);
//        Files.copy(this.file, newFile);
    }

    public void printAllMetadata() {
        for (Directory directory : this.metadata.getDirectories()) {
            System.out.println("--------------------------------");
            System.out.println(directory + " | " + directory.getClass());
            for (Tag tag : directory.getTags()) {
                System.out.println(tag.getTagType() + " | " + tag);
            }
        }
        System.out.println("+++++++++++++++++++++++++++++++++");
    }
}
