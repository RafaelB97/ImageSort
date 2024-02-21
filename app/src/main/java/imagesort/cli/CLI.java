package imagesort.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.nio.file.Path;

@Command(
        name = "imageSort",
        mixinStandardHelpOptions = true,
        version = "imageSort 0.1",
        description = "This is the description."
)
public class CLI implements Runnable {
    @Parameters(
            index = "0",
            description = "The directory that will be scan to search images"
    )
    private Path sourcePath;
    @Override
    public void run() {
        System.out.println(sourcePath);
        System.out.println("Hello World");
    }
}
