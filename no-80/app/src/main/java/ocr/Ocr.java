package ocr;


import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.util.concurrent.Callable;

@Command(name = "ocr", mixinStandardHelpOptions = true, version = "ocr 1.0", description = "This challenge is to build your own Optical Character Recognition")
public class Ocr implements Callable<Result> {

    public static int _verbose = 0;

    @Option(names = "-v", description = "verbose model level 1")
    boolean verbose = false;
    @Option(names = "-vv", description = "verbose model level 2")
    boolean verbose2 = false;

    @Option(names = "-s", description = "show image after processing - default is true")
    boolean showImage = true;
    @Option(names = "-r", description = "show raster image after processing - default is false")
    boolean showRasterImage = false;
    @Option(names = "-c", description = "show contours after processing - default is true")
    boolean showContours = true;

    @Option(names = "-w", description = "width of the image to process, default is 0 (no scaling)")
    int width = 0;

    @Option(names = "-f", description = "file to process", required = true)
    String filePathName = null;

    public static void main(String[] args) {
        var ocr = new Ocr();
        var cmd = new CommandLine(ocr);
        var exitCode = cmd.execute(args);
        Result result = cmd.getExecutionResult();
        if (result != null && result.toString() != null) {
            System.exit(exitCode);
        }
    }

    public static boolean verbose() {
        return _verbose >= 1;
    }

    @SuppressWarnings("unused")
    public static boolean verbose2() {
        return _verbose >= 2;
    }

    @Override
    public Result call() throws Exception {
        if (this.verbose) _verbose = 1;
        if (this.verbose2) _verbose = 2;
        new OcrDetector(this.width, showImage, showRasterImage, showContours)
                .detectText(this.filePathName);
        return new Result();
    }

}