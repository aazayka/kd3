import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayList;
import java.util.List;

public class PointSET extends SET<Point2D> {
    public void insert(Point2D p){
        add(p);
    }

    public void draw() {
        for (Point2D p: this
             ) {
            StdDraw.point(p.x(), p.y());
        }
    }                         // draw all points to standard draw

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("null arg");
        List<Point2D> res = new ArrayList<>();
        for (Point2D p: this) {
            if (rect.contains(p)) res.add(p);
        }
        return res;
    }             // all points that are inside the rectangle (or on the boundary)

    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("null arg");
        double minDist = Double.MAX_VALUE;
        Point2D res = null;
        for (Point2D curr: this) {
            double dist = p.distanceSquaredTo(curr);
            if (dist < minDist) {
                minDist = dist;
                res = curr;
            }
        }
        return res;
    }


    public static void main(String[] args) {
        PointSET ps = new PointSET();
        System.out.println("0=" + ps.size());
        ps.add(new Point2D(1,1));
        ps.add(new Point2D(1,1));
        System.out.println("1=" + ps.size());
        ps.add(new Point2D(0, 0));
        System.out.println("(0,0) = " + ps.nearest(new Point2D(0.3, 0)));
        ps.add(new Point2D(0.3, 1.0));
        System.out.println("0.3, 1");
        for (Point2D p : ps.range(new RectHV(0.1, 0.1, 0.4, 1))) {
            System.out.println(p);
        }
        System.out.println("true = " + ps.contains(new Point2D(1, 1)));
    }                 // unit testing of the methods (optional)
}