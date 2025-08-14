package ocr.image.labeling;

import java.util.*;

public class BoundingBoxes {

    private final Map<Integer, Box> boxes;
    private final Labels labels;

    public BoundingBoxes(Labels labels) {
        this.labels = labels;
        this.boxes = new HashMap<>();
    }

    public void add(int label, Box box) {
        if (box != null) {
            this.boxes.put(label, box);
        }
    }

    public BoundingBoxes detectBoundingBoxes(){
        var label = this.labels.getLabels();
        int H = label.length, W = label[0].length;
        var m = new HashMap<Integer, ComponentStats>();
        for (int y=0; y<H; y++){
            for (int x=0; x<W; x++){
                int L = label[y][x];
                if (L >= 2){
                    m.computeIfAbsent(L, ComponentStats::new).add(x,y);
                }
            }
        }
        for (var s : m.values()){
            double meanX = (double)s.sumX / s.n;
            double meanY = (double)s.sumY / s.n;
            double cxx = (double)s.sumXX / s.n - meanX*meanX;
            double cyy = (double)s.sumYY / s.n - meanY*meanY;
            double cxy = (double)s.sumXY / s.n - meanX*meanY;
            // principal orientation (major axis)
            double angle = 0.5 * Math.atan2(2.0*cxy, cxx - cyy); // radians
            int x = s.minX, y = s.minY, w = s.maxX - s.minX + 1, h = s.maxY - s.minY + 1;
            this.boxes.put(s.label, new Box(s.label, x, y, w, h, meanX, meanY, angle));
        }
        return this;
    }


    public List<OrientedWord> orderIntoLinesAndWords(){
        // 1) collect and estimate global theta (median angle)
        var bs = new ArrayList<>(boxes.values());
        bs.sort(Comparator
                .comparingDouble(b -> b.cy)
        );
        double theta = bs.get(bs.size()/2).angleRad;

        double c = Math.cos(theta), s = Math.sin(theta);

        // 2) deskew and collect
        List<OrientedChar> chars = new ArrayList<>();
        for (Box b : bs){
            var oc = new OrientedChar();
            oc.label = b.label; oc.cx=b.cx; oc.cy=b.cy; oc.angle=b.angleRad;
            oc.x=b.x; oc.y=b.y; oc.w=b.w; oc.h=b.h;
            oc.xp =  c*b.cx + s*b.cy;
            oc.yp = -s*b.cx + c*b.cy;
            chars.add(oc);
        }

        // 3) simple line clustering by yp
        chars.sort(Comparator.comparingDouble(o -> o.yp));
        List<List<OrientedChar>> lines = new ArrayList<>();
        double lineThresh = medianHeight(chars) * 0.5; // tweak
        List<OrientedChar> current = new ArrayList<>();
        for (OrientedChar oc : chars){
            if (current.isEmpty() || Math.abs(oc.yp - current.get(current.size()-1).yp) <= lineThresh){
                current.add(oc);
            } else {
                lines.add(new ArrayList<>(current));
                current.clear();
                current.add(oc);
            }
        }
        if (!current.isEmpty()) lines.add(current);

        // 4) per line: sort by xp and optionally split into words by gap
        for (List<OrientedChar> line : lines){
            line.sort(Comparator.comparingDouble(o -> o.xp));
            // optional: compute gaps and split; omitted for brevity
        }
        return lines.stream().map(OrientedWord::new).toList();
    }

    private double medianHeight(List<OrientedChar> cs) {
        double[] hs = cs.stream().mapToDouble(c -> c.h).sorted().toArray();
        int n = hs.length;
        return (n%2==1) ? hs[n/2] : 0.5*(hs[n/2-1]+hs[n/2]);
    }

}
