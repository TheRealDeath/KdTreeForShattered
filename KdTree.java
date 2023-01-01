package Binary_Trees;

import java.util.Arrays;


/**
 * @author Gerardo Vega gv418239@g.risd.org
 * @version 1.5
 * @since 18.0.2
 * <p>
 * An implementation of a K-D Tree for fast Nearest Neighbor Queries
 * In future editons, the tree will contain range search
 * @see <a href="https://en.wikipedia.org/wiki/K-d_tree">K-D Tree (Wikipedia)</a>
 * <br>
 */
public class KdTree {

    private int k; // number of dimensions
    private Node root;
    private double distance; //Best Distance
    private Node mostBest; //Best Node
    private int q; //Keeps track of queries made

    public KdTree(int k) {
        this.k = k;
    }
    //internal class for storage of extra variables such as strings and such
    private class Node {
        double[] points;
        Node left, right;

        public Node(double[] arr) {
            points = arr;
        }
        public double euclidDistance(Node temp) {
            double s = 0;
            for(int i = 0;i<temp.points.length;i++) {
                s += Math.pow(temp.points[i]-points[i],2);
            }
            return s;
        }
        @Override
        public String toString() {
            return Arrays.toString(points);
        }
    }
    private Node createNode(double[] temp) {
        return new Node(temp);
    }
    /**
    * @author Gerardo Vega gv418239@g.risd.org
    * @version 1.5
    * @since 18.0.2
    * <p>
    * inserts nodes according to a k value comparasion 
    * <br>
    */
    public void insert(double[] temp) {
        root = insertRec(root,temp,0);
    }
    private Node insertRec(Node root, double[] temp, int depth) {
        if(root == null) return createNode(temp);
        int cd = depth % k;
        if(temp[cd] <= root.points[cd]) root.left = insertRec(root.left,temp,depth+1);
        else root.right = insertRec(root.right,temp,depth+1);
        return root;
    }
    /**
    * @author Gerardo Vega gv418239@g.risd.org
    * @version 1.5
    * @since 18.0.2
    * <p>
    * An implementation of the Nearest Neighbor algorithm(NN)
    * <p>
    * Done by:
    * <p>
    *   recursing down the tree to the nearest leaf to the insertion point of the given points
    * <p>
    *   ascending up the tree, marking the distance between the current node and the given points
    * <p>
    *   If the distance is less than the current best, descend the tree down the opposite path already descended 
    * <br>
    */
    public Node nearestNeighbor(double[] points) {
        q = 0;
        distance = Integer.MAX_VALUE;
        Node player = new Node(points);
        nearestNeighborRec(root,player,0);
        if(distance(root.points,player) < distance) {mostBest = root;}
        q++;
        System.out.println(q);
        return mostBest;
    }
    private double distance(double[] points, Node points2) {
        double s = 0;
        for(int i = 0;i<points.length;i++) {
            s += Math.pow(points[i]-points2.points[i],2);
        }
        return s;
    }
    private Node nearestNeighborRec(Node root,Node temp, int depth) {
        if(root != null && root.right == null && root.left == null) return root;
        else if(root != null) {
            q++;
            int cd = depth % k;
            Node best;
            double dx = root.points[cd]-temp.points[cd];
            boolean b = dx > 0;
            if(b) best = nearestNeighborRec(root.left, temp, depth+1);
            else best = nearestNeighborRec(root.right, temp, depth+1);
            double tempDouble = best.euclidDistance(temp);
            if(tempDouble < distance) {
                distance = tempDouble;
                mostBest = best;
            }
            double hyper = dx * dx;
            if(hyper < distance) nearestNeighborRec(dx > 0 ?root.right : root.left, temp, depth+1);
            return best;
        }
        double[] tempArr = new double[k];
        Arrays.fill(tempArr,-1e8);
        Node temp1 = new Node(tempArr);
        return temp1;
    }

    /**
    * @author Gerardo Vega gv418239@g.risd.org
    * @version 1.5
    * @since 18.0.2
    * <p>
    * prints the tree in an inorder fashion
    * <br>
    */
    private static String s;
    @Override
    public String toString() {
        s = "";
		inOrder(root);
        return s;
	}

	private String inOrder(Node current) {
		if(current != null) {
            return (inOrder(current.left) +"\n"+current+"\n"+inOrder(current.right)).trim();
		}
        return "";
	}
    //helper methods
    public Node findMin(Node root, int d) {
        return findMinRec(root,d,0);
    }
    private Node findMinRec(Node root, int d, int depth) {
        if(root == null) return null;
        int cd = depth % k;
        if(cd == d) {
            if(root.left == null) return root;
            return findMinRec(root, d, depth+1);
        }
        return minNode(root,findMinRec(root.left,d,depth+1),findMinRec(root, d, depth+1),d);
    }
    private Node minNode(Node x, Node y, Node z, int d) {
        Node res = x;
        if(y != null && y.points[d] < res.points[d]) res = y;
        if(z != null && z.points[d] < res.points[d]) res = z;
        return res;
    }
    //idk what this even does :)
    public Node deleteNode(double[] temp) {
        return deleteNodeRec(root, temp,0);
    }
    private Node deleteNodeRec(Node root, double[] points, int depth) {
        if(root == null) return null;
        int cd = depth % k;
        if(arePointsSame(root.points,points)) {
            if(root.right != null) {
                Node min = findMin(root.right,cd);
                copyPoint(root.points,min.points);
                root.right = deleteNodeRec(root.right, min.points, depth+1);
            }
            else if(root.left != null) {
                Node min = findMin(root.left,cd);
                copyPoint(root.points, min.points);
                root.left = deleteNodeRec(root.left,min.points,depth+1);
            }
            else {
                root = null;
                return null;
            }
            return root;
        }
        if(points[cd] < root.points[cd])
            root.left = deleteNodeRec(root.left, points, depth+1);
        else root.right = deleteNodeRec(root.right, points, depth+1);
        return root;
    }
    private void copyPoint(double[] p1, double[] p2) {
        for(int i = 0;i<k;i++) p1[i] = p2[i];
    }
    private boolean arePointsSame(double p1[], double p2[]) {
        for(int i = 0;i<k;i++) 
            if(p1[i] != p2[i])
                return false;
        return true;
    }
}