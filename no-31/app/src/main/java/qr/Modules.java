package qr;

import java.util.List;

public class Modules {
    public final Version version;
    public final List<Rect> finderPatterns;
    public final List<Rect> alignmentPatterns;
    public final List<Rect> timingPatterns;
    public final Point2d darkModule;
    public final Region reserveFormatInformation;
    public final List<Rect> reserveVersionInformation;
    public final List<Rect> separatorPatterns;

    public Modules(Version version, List<Rect> finderPatterns, List<Rect> separatorPatterns, List<Rect> alignmentPatterns, List<Rect> timingPatterns, Point2d darkModule, Region reserveFormatInformation, List<Rect> reserveVersionInformation) {
        this.version = version;
        this.finderPatterns = finderPatterns;
        this.separatorPatterns = separatorPatterns;
        this.alignmentPatterns = alignmentPatterns;
        this.timingPatterns = timingPatterns;
        this.darkModule = darkModule;
        this.reserveFormatInformation = reserveFormatInformation;
        this.reserveVersionInformation = reserveVersionInformation;
    }

    public Rect versionSize() {
        return new Rect(0, 0, this.version.qrSize(), this.version.qrSize());
    }
}
