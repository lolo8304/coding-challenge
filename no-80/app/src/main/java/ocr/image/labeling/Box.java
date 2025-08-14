package ocr.image.labeling;

public class Box {
    int label, x, y, w, h;
    double cx, cy;     // centroid
    double angleRad;   // orientation (principal axis), radians
    Box(int label, int x, int y, int w, int h, double cx, double cy, double angleRad){
        this.label=label; this.x=x; this.y=y; this.w=w; this.h=h; this.cx=cx; this.cy=cy; this.angleRad=angleRad;
    }
}