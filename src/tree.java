import org.w3c.dom.css.Rect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class tree {
    static boolean anim = false;
    static int currentIdx = 0, time = 0, idx = 0;
    public static void main(String[] args) {
        JFrame frame = new JFrame("tree");
        SGT sgt = new SGT(1920, 1080, 120);
        frame.add(sgt);
        frame.pack();
        frame.requestFocus();

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        LoadNodesAndEdges(1800, 1000);
        LoadDFS(0);
        LoadPaths();
        Rectangle bwd = new Rectangle(10, 10, 150, 75),
                fwd = new Rectangle(170, 10, 150, 75),
                ani = new Rectangle(330, 10, 150, 75),
                reset = new Rectangle(490, 10, 150, 75),
                pause = new Rectangle(650, 10, 150, 75);
        sgt.SGTKeyInput(new SGTKeyInput() {

        });
        sgt.SGTMouseInput(new SGTMouseInput() {
            @Override
            public void mousePressed(MouseEvent e) {
                int mousex = e.getX()-10, mousey = e.getY()-30;
                if(fwd.contains(mousex, mousey) && idx < dfs.size()-1) idx++;
                if(bwd.contains(mousex, mousey) && idx > 0){
                    visited.removeLast();
                    idx--;
                }
                if(pause.contains(mousex, mousey)) anim = false;
                if(ani.contains(mousex, mousey)) anim = true;
                if(reset.contains(mousex, mousey)) idx = 0;
                System.out.println(idx);
            }
        });

        sgt.SGTUpdate(()->{
            if(anim){
                time++;
                if(time%60==0) idx++;
                if(idx > dfs.size()-2) anim = false;
            }

        });
        sgt.SGTPaintComponent((g)->{
            g.setColor(Color.black);
            g.fillRect(0, 0, sgt.getScreenWidth(), sgt.getScreenHeight());
            g.setColor(Color.darkGray);
            g.fillRect(fwd.x, fwd.y, fwd.width, fwd.height);
            g.fillRect(bwd.x, bwd.y, bwd.width, bwd.height);
            g.fillRect(ani.x, ani.y, ani.width, ani.height);
            g.fillRect(reset.x, reset.y, reset.width, reset.height);
            g.fillRect(pause.x, pause.y, pause.width, pause.height);
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("fwd", fwd.x+15, fwd.y+fwd.height/2);
            g.drawString("bwd", bwd.x+15, bwd.y+bwd.height/2);
            g.drawString("ani", ani.x+15, ani.y+ani.height/2);
            g.drawString("reset", reset.x+15, reset.y+reset.height/2);
            g.drawString("pause", pause.x+15, pause.y+pause.height/2);
            paintTree(g);
            g.setColor(Color.yellow);
            for(int i = 0 ; i < currentIdx; i++){
                Rectangle rect = nodes.get(dfs.get(i));
                g.fillOval(rect.x, rect.y, rect.width, rect.height);

            }


        });

        sgt.startThread();
    }
    static ArrayList<Integer> visited = new ArrayList<>();
    static HashMap<Integer, ArrayList<Integer>> paths = new HashMap<>();
    static ArrayList<Rectangle> nodes = new ArrayList<>();
    static ArrayList<int[]> edges = new ArrayList<>();
    public static void LoadNodesAndEdges(int width, int height){
        //level 1
        nodes.add(new Rectangle(width/2, height/5, 100, 100));
        //level 2
        nodes.add(new Rectangle(width/3, 2*height/5, 100, 100));
        nodes.add(new Rectangle(2*width/3, 2*height/5, 100, 100));
        //level 3
        nodes.add(new Rectangle(width/5, 3*height/5, 100, 100));
        nodes.add(new Rectangle(2*width/5, 3*height/5, 100, 100));
        nodes.add(new Rectangle(3*width/5, 3*height/5, 100, 100));
        nodes.add(new Rectangle(4*width/5, 3*height/5, 100, 100));
        //level 4
        nodes.add(new Rectangle(width/9, 4*height/5, 100, 100));
        nodes.add(new Rectangle(2*width/9, 4*height/5, 100, 100));
        nodes.add(new Rectangle(3*width/9, 4*height/5, 100, 100));
        nodes.add(new Rectangle(4*width/9, 4*height/5, 100, 100));
        nodes.add(new Rectangle(5*width/9, 4*height/5, 100, 100));
        nodes.add(new Rectangle(6*width/9, 4*height/5, 100, 100));
        nodes.add(new Rectangle(7*width/9, 4*height/5, 100, 100));
        nodes.add(new Rectangle(8*width/9, 4*height/5, 100, 100));

        edges.add(new int[]{0, 1});
        edges.add(new int[]{0, 2});
        edges.add(new int[]{1, 3});
        edges.add(new int[]{1, 4});
        edges.add(new int[]{2, 5});
        edges.add(new int[]{2, 6});
        edges.add(new int[]{3, 7});
        edges.add(new int[]{3, 8});
        edges.add(new int[]{4, 9});
        edges.add(new int[]{4, 10});
        edges.add(new int[]{5, 11});
        edges.add(new int[]{5, 12});
        edges.add(new int[]{6, 13});
        edges.add(new int[]{6, 14});
    }
    static ArrayList<Integer> dfs = new ArrayList<>();
    public static void LoadDFS(int node){
        dfs.add(node);
        for (int[] edge : edges) {
            if (edge[0] == node && !dfs.contains(edge[1])) {
                LoadDFS(edge[1]);
            }
        }
    }

    public static void LoadPaths() {
        HashMap<Integer, ArrayList<Integer>> nodeConnections = new HashMap<>();
        for (int[] edge : edges) {
            nodeConnections.putIfAbsent(edge[0], new ArrayList<>());
            nodeConnections.get(edge[0]).add(edge[1]);
        }
        int headNode = 0;
        paths.put(headNode, new ArrayList<>(List.of(headNode)));
        helper(headNode, nodeConnections);
    }

    public static void helper(int node, HashMap<Integer, ArrayList<Integer>> nodeConnections) {
        if (!nodeConnections.containsKey(node)) {
            return;
        }
        for (int connectedNode : nodeConnections.get(node)) {
            ArrayList<Integer> currentPath = new ArrayList<>(paths.get(node));
            currentPath.add(connectedNode);
            paths.put(connectedNode, currentPath);
            helper(connectedNode, nodeConnections);
        }
    }

    public static void paintTree(Graphics g){
        g.setFont(new Font("Arial", Font.BOLD, 64));
        for(int i = 0 ; i < edges.size(); i++){
            g.setColor(Color.white);
            g.drawLine(nodes.get(edges.get(i)[0]).x+50, nodes.get(edges.get(i)[0]).y+50, nodes.get(edges.get(i)[1]).x+50, nodes.get(edges.get(i)[1]).y+50);
        }
        g.setColor(Color.red);
        for(int i = 0 ; i < nodes.size(); i++) g.fillOval(nodes.get(i).x, nodes.get(i).y, nodes.get(i).width, nodes.get(i).height);
        g.setColor(Color.yellow);
        for(int i = 0 ; i < idx; i++) g.fillOval(nodes.get(dfs.get(i)).x, nodes.get(dfs.get(i)).y, nodes.get(dfs.get(i)).width, nodes.get(dfs.get(i)).height);
        g.setColor(Color.blue);
        for(int i = 0 ; i < paths.get(dfs.get(idx)).size(); i++){
            int pathNode = paths.get(dfs.get(idx)).get(i);
            g.fillOval(nodes.get(pathNode).x, nodes.get(pathNode).y, nodes.get(pathNode).width, nodes.get(pathNode).height);
        }
        g.fillOval(nodes.get(dfs.get(idx)).x, nodes.get(dfs.get(idx)).y, nodes.get(dfs.get(idx)).width, nodes.get(dfs.get(idx)).height);
        visited.add(dfs.get(idx));
        for(int i = 0 ; i < nodes.size(); i++) {
            g.setColor(Color.cyan);
            g.drawString(""+(i+1), nodes.get(i).x+25, nodes.get(i).y+75);
        }
    }
}
