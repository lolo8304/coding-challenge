package qr;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Mask {

    private static Map<Quality, Map<Integer, Mask>> MASKS = new HashMap<>();
    private final Quality quality;
    private final int maskNo;

    private final String informationBits;

    public Mask(Quality quality, int maskNo, String informationBits) {
        this.quality = quality;
        this.maskNo = maskNo;
        this.informationBits = informationBits;
    }

    static {
        initFormatInformation();
    }

    public static Mask get(Quality quality, int maskNo) {
        return MASKS.get(quality).get(maskNo);
    }

    public String informationBits() {
        return this.informationBits;
    }
    private static void initFormatInformation() {

        var stream = Mask.class.getResourceAsStream("/mask-format-information.txt");
        if (stream != null) {
            var reader = new BufferedReader(new InputStreamReader(stream));
            var lines = reader.lines().toList();
            // remove header
            lines = lines.subList(1, lines.size());
            for (int i = 0; i < lines.size(); i++) {
                var line = lines.get(i);
                var splitted = line.split("\t");
                var quality = Quality.valueOf(splitted[0].trim());
                var mask = Integer.parseInt(splitted[1].trim());
                var information = splitted[2].trim();
                var maskMap = MASKS.get(quality);
                if (maskMap == null) {
                    maskMap = new HashMap<>();
                    MASKS.put(quality, maskMap);
                }
                maskMap.put(mask, new Mask(quality, mask, information));
            }
        }

    }
}
