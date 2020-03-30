import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KdTree {
    private static class Node {

        Node left;
        Node right;
        Point2D val;
        public boolean equals(Point2D p){
            return val.equals(p);
        }
        public double x() {
            return val.x();
        }

        public double y() {
            return val.y();
        }

        public Node(Point2D val) {
            this.val = val;
        }

        public void drawLine(boolean isHorisontal, RectHV boundary) {
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.01);
            StdDraw.point(x(), y());
            StdDraw.setPenRadius();
            if (!boundary.contains(val)) {throw new IllegalArgumentException("Point " + val.toString() + " not in " + boundary.toString());}
            if (isHorisontal) {
                StdDraw.setPenColor(Color.BLUE);
                StdDraw.line(boundary.xmin(), y(), boundary.xmax(), y());
            } else {
                StdDraw.setPenColor(Color.RED);
                StdDraw.line(x(), boundary.ymin(), x(), boundary.ymax());
            }
        }

        public double dist(Point2D p) {
            return Math.pow(x() - p.x(), 2) + Math.pow(y() - p.y(), 2);
        }
    }

    Node root;
    int size;

    public KdTree() {

    }

    public boolean isEmpty() {
        return root == null;
    }                      // is the set empty?

    public               int size() {
        return size;
    }                         // number of points in the set

    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("null");
        System.out.println("Inserting " + p.toString() + " for root " + (root == null ? "empty " : root.val.toString()));
        root = insertRec(root, p, 0);
        System.out.println("**************************");
    }

    private Node insertRec(Node n, Point2D p, int level) {
        if (n == null) {
            size++;
            System.out.println("node " + p.toString() + " added. Size: " + size);
            return new Node(p);
        }
        if (n.equals(p)) {
            System.out.println("node p " + p.toString() + " equal to current");
            return n;
        }

        if   (level % 2 == 0 && p.x() < n.x()
                || (level % 2 == 1 && p.y() < n.y())) {
            System.out.println("go left on level " + level);
            n.left = insertRec(n.left, p, level + 1);
        }
        else if ((level % 2 == 0 && p.x() >= n.x())
                || (level % 2 == 1 && p.y() >= n.y())) {
            System.out.println("go right on level " + level);
            n.right = insertRec(n.right, p, level + 1);
        }
        else {
            System.out.println("unexpected on level " + level);
        }
        return n;
    }

    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("null");
        return getPosition(root, p, 0) != null;
    }

    public Node getRoot() {
        return root;
    }
    private Node getPosition(Node n, Point2D p, int level) {
        if (n == null) return null;
        if (n.equals(p)) return n;
        if   (level % 2 == 0 && p.x() < n.x()
          || (level % 2 == 1 && p.y() < n.y()))
            return getPosition(n.left, p, level + 1);
        else if ((level % 2 == 0 && p.x() >= n.x())
              || (level % 2 == 1 && p.y() >= n.y()))
            return getPosition(n.right, p, level + 1);
        else return n;
    }

    public void draw(){
        drawRec(root, false, new RectHV(0,0, 1, 1));
    }

    private void drawRec(Node n, boolean isHorisontal, RectHV boundary){
        if (n == null) return;
        n.drawLine(isHorisontal, boundary);
        drawRec(n.left, !isHorisontal, getBoundaryRect(boundary, n, isHorisontal, true));
        drawRec(n.right, !isHorisontal, getBoundaryRect(boundary, n, isHorisontal, false));
    }

    private RectHV getBoundaryRect(RectHV boundary, Node n, boolean isHorisontal, boolean toTheLeft){
        if (toTheLeft) {
            return isHorisontal ?
                   new RectHV(boundary.xmin(), boundary.ymin(), boundary.xmax(), n.y())
                                : new RectHV(boundary.xmin(), boundary.ymin(), n.x(), boundary.ymax());
        } else {
            return isHorisontal ?
                   new RectHV(boundary.xmin(), n.y(), boundary.xmax(), boundary.ymax())
                                : new RectHV(n.x(), boundary.ymin(), boundary.xmax(), boundary.ymax());
        }
    }

    public Iterable<Point2D> range(RectHV rect){
        if (rect == null) throw new IllegalArgumentException("null");
        List<Point2D> res = new ArrayList<>();
        rangeRec(rect, root, res, true);
        return res;
    }

    private void rangeRec(RectHV rect, Node n, List<Point2D> res, boolean checkX) {
        if (n == null) {
            System.out.println("oooppps");
            return;
        }
        System.out.println("check node " + n.val.toString() + " in rect " + rect);
        if (rect.contains(n.val)) res.add(n.val);

        if (checkX && rect.xmax() >= n.x()) {
            System.out.println("x-right");
            rangeRec(rect, n.right, res, !checkX); }
        if (checkX && rect.xmin() < n.x()) {
            System.out.println("x-left");
            rangeRec(rect, n.left, res, !checkX);
        }
        if (!checkX && rect.ymax() >= n.y()) {
            System.out.println("y-right");
            rangeRec(rect, n.right, res, !checkX);
        }
        if (!checkX && rect.ymin() < n.y()) {
            System.out.println("y-left");
            rangeRec(rect, n.left, res, !checkX);
        }
    }

    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("null");
        return nearestRec(p, root, true, new Nearest(root, root.dist(p)), new RectHV(0,0,1,1)).minNode.val;
    }            // a nearest neighbor in the set to point p; null if the set is empty

    private Nearest nearestRec(Point2D p, Node n, boolean checkX, Nearest nearest, RectHV boundary) {
        if (n == null) return nearest;
        double dist = n.dist(p);
        if (dist < nearest.minDist) {
            nearest.minDist = dist;
            nearest.minNode = n;
        }

        if (nearest.minDist < boundary.distanceTo(p)) {
            return nearest;
        }

        if (checkX) {
            if (p.x() < n.x()) {
                nearest = nearestRec(p, n.left, !checkX, nearest, getBoundaryRect(boundary, n, !checkX, true));
                nearest = nearestRec(p, n.right, !checkX, nearest, getBoundaryRect(boundary, n, !checkX, false));
            }
            else {
                nearest = nearestRec(p, n.right, !checkX, nearest, getBoundaryRect(boundary, n, !checkX, false));
                nearest = nearestRec(p, n.left, !checkX, nearest, getBoundaryRect(boundary, n, !checkX, true));
            }
        }
        else {
            if (p.y() < n.y()) {
                nearest = nearestRec(p, n.left, !checkX, nearest, getBoundaryRect(boundary, n, !checkX, true));
                nearest = nearestRec(p, n.right, !checkX, nearest, getBoundaryRect(boundary, n, !checkX, false));
            }
            else {
                nearest = nearestRec(p, n.right, !checkX, nearest, getBoundaryRect(boundary, n, !checkX, false));
                nearest = nearestRec(p, n.left, !checkX, nearest, getBoundaryRect(boundary, n, !checkX, true));
            }
        }
        return nearest;
    }

    private static class Nearest {
        Node minNode;
        double minDist;

        public Nearest(Node minNode, double minDist) {
            this.minDist = minDist;
            this.minNode = minNode;
        }
    }

    public static void main(String[] args) {
        KdTree tr = new KdTree();
        System.out.println("Empty true" + tr.isEmpty());
        System.out.println("size 0=" + tr.size());
        tr.insert(new Point2D(1, 1));
        System.out.println("true=" + tr.contains(new Point2D(1, 1)));
        System.out.println("false=" + tr.contains(new Point2D(0, 0)));
        System.out.println("Empty false=" + tr.isEmpty());
        System.out.println("size 1=" + tr.size());

        tr.insert(new Point2D(1, 1));
        System.out.println("size 1=" + tr.size());

        tr.insert(new Point2D(.5, .5));
        System.out.println("size 2=" + tr.size());
        System.out.println("true=" + tr.contains(new Point2D(1, 1)));
        System.out.println("true=" + tr.contains(new Point2D(0.5, 0.5)));
        System.out.println("false=" + tr.contains(new Point2D(0, 0)));

        RectHV rect = new RectHV(-0.052734375, 0.2578125, 0.580078125, 0.419921875);
        In in = new In("circle10.txt");
        KdTree kdtree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
        }
        Iterator<Point2D> it = kdtree.range(rect).iterator();
        while (it.hasNext()){
            System.out.println(it.next());
        }

    }
}
