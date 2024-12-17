import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.*;

public class graph {
    static ArrayList<Point> arr = new ArrayList<>();
    static boolean[][] walls = new boolean[10][10];
    static int idx = 0, blocksize = 0, time = 0, xoff = 10, yoff = 70;
    static Point cpos = new Point(5, 5);
    static boolean anim = false, getpos = false;

    public static void main(String[] args) {
        JFrame frame = new JFrame("graph");
        SGT sgt = new SGT(1920, 1080, 120);
        frame.add(sgt);
        frame.pack();
        frame.setBackground(Color.black);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        LoadArrays(cpos.x, cpos.y);

        Rectangle back = new Rectangle(10, 10, 100, 50),
                fwd = new Rectangle(120, 10, 100, 50),
                pause = new Rectangle(230, 10, 100, 50),
                bani = new Rectangle(340, 10, 100, 50),
                ani = new Rectangle(450, 10, 100, 50),
                pos = new Rectangle(560, 10, 100, 50);

        sgt.SGTKeyInput(new SGTKeyInput() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_RIGHT && idx < arr.size()) idx++;
                if (e.getKeyCode() == KeyEvent.VK_LEFT && idx > 0) idx--;
            }
        });

        sgt.SGTMouseInput(new SGTMouseInput() {
            @Override
            public void mousePressed(MouseEvent e) {
                int mousex = e.getX() - 10, mousey = e.getY() - 30;
                int gridX = (mousex - xoff) / blocksize;
                int gridY = (mousey - yoff) / blocksize;

                if (getpos) {
                    anim = false;
                    System.out.println(gridX + " " + gridY);
                    if (gridX >= 0 && gridX < 10 && gridY >= 0 && gridY < 10) {
                        LoadArrays(gridX, gridY);
                        cpos = new Point(gridX, gridY);
                        walls[gridX][gridY] = false;
                    } else {
                        LoadArrays(0, 0);
                    }
                    getpos = false;

                } else {
                    if (back.contains(mousex, mousey) && idx >= 1) idx--;
                    if (fwd.contains(mousex, mousey) && idx < arr.size()) idx++;
                    if (pause.contains(mousex, mousey)) anim = false;
                    if (ani.contains(mousex, mousey)) anim = true;
                    if (bani.contains(mousex, mousey)){
                        idx = 0;
                        walls = new boolean[10][10];
                    }
                    if (pos.contains(mousex, mousey)){
                        getpos = true;
                        idx = 0;
                    }
                    if (mousex > xoff && mousey > yoff && gridX >= 0 && gridX < 10 && gridY >= 0 && gridY < 10) {
                        if(gridX != cpos.x || gridY != cpos.y) {
                            walls[gridX][gridY] = !walls[gridX][gridY];
                            LoadArrays(cpos.x, cpos.y);
                        }
                    }
                }
            }
        });

        sgt.SGTUpdate(() -> {
            blocksize = Math.min(sgt.getWidth(), sgt.getHeight()) / 11;
            if (anim) {
                time++;
                if (time % 20 == 0) idx++;
                if (idx >= arr.size()) anim = false;
            }
        });

        sgt.SGTPaintComponent((g) -> {
            g.setColor(Color.black);
            g.fillRect(0, 0, sgt.getScreenWidth(), sgt.getScreenHeight());
            g.setColor(Color.red);
            g.fillRect(xoff, yoff, blocksize * 10, blocksize * 10);

            g.setColor(Color.darkGray);
            g.fillRect(back.x, back.y, back.width, back.height);
            g.fillRect(fwd.x, fwd.y, fwd.width, fwd.height);
            g.fillRect(pause.x, pause.y, pause.width, pause.height);
            g.fillRect(ani.x, ani.y, ani.width, ani.height);
            g.fillRect(bani.x, bani.y, bani.width, bani.height);
            g.fillRect(pos.x, pos.y, pos.width, pos.height);

            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("back", back.x + 10, back.y + back.height - 10);
            g.drawString("fwd", fwd.x + 10, fwd.y + fwd.height - 10);
            g.drawString("pause", pause.x+5, pause.y + pause.height-10);
            g.drawString("ani", ani.x + 10, ani.y + ani.height - 10);
            g.drawString("reset", bani.x + 5, bani.y + bani.height - 10);
            g.drawString("pos", pos.x + 10, pos.y + pos.height - 10);

            for (int i = 0; i < Math.min(idx, arr.size()); i++) {
                g.setColor(new Color(0, (int) ((double) i / idx * 255), (int) ((double) i / idx * 255)));
                g.fillRect(xoff + arr.get(i).x * blocksize, yoff + arr.get(i).y * blocksize, blocksize, blocksize);
            }

            g.setColor(Color.yellow);
            for (int i = 0; i < walls.length; i++) {
                for (int j = 0; j < walls[i].length; j++) {
                    if (walls[i][j]) {
                        g.fillRect(xoff + i * blocksize, yoff + j * blocksize, blocksize, blocksize);
                    }
                }
            }
            g.setColor(Color.CYAN);
            g.fillRect(xoff+cpos.x*blocksize, yoff+cpos.y*blocksize, blocksize, blocksize);
        });

        sgt.startThread();
    }

    static boolean[][] visited = new boolean[10][10];

    public static void LoadArrays(int x, int y) {
        arr.clear();
        for (int i = 0; i < visited.length; i++) {
            Arrays.fill(visited[i], false);
        }
        int[][] nums = new int[10][10];
        dfs(nums, x, y, 0);
    }

    private static void dfs(int[][] nums, int x, int y, int step) {
        if (x < 0 || x >= nums[0].length || y < 0 || y >= nums.length) return;
        if (walls[x][y]) return;
        if (visited[x][y]) return;
        visited[x][y] = true;
        arr.add(new Point(x, y));
        dfs(nums, x, y - 1, step + 1);
        dfs(nums, x + 1, y, step + 1);
        dfs(nums, x, y + 1, step + 1);
        dfs(nums, x - 1, y, step + 1);
    }
}