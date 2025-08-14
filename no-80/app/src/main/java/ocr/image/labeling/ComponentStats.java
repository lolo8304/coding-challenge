package ocr.image.labeling;

public class ComponentStats {
    int label, minX=Integer.MAX_VALUE, minY=Integer.MAX_VALUE, maxX=Integer.MIN_VALUE, maxY=Integer.MIN_VALUE, n=0;
    long sumX=0, sumY=0, sumXX=0, sumYY=0, sumXY=0; // for orientation (optional)
    ComponentStats(int label){ this.label = label; }
    void add(int x, int y){
        if (x<minX) minX=x; if (y<minY) minY=y;
        if (x>maxX) maxX=x; if (y>maxY) maxY=y;
        n++; sumX+=x; sumY+=y; sumXX+= (long)x*x; sumYY+= (long)y*y; sumXY+= (long)x*y;
    }
}
