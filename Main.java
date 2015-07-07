package triview3dsnake;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
public class Main {
    static Window w;
    public static void main(String[] args) throws Exception {
        Snake s = new Snake();
        w = new Window(s);
        w.popUp("Note that all views are not a complete view. You see a slice of the world. The slice you see is based off of the location of the head of the snake.");
        while(true){
            s.animate();
            w.render();
            Thread.sleep(100);
        }
    }
}
class Snake {
    int startingSize = 15;
    int size = startingSize;
    public static int selectedView = 1;
    static Target t;
    public static Location[][][] map = new Location[40][40][40];
    Location startingLocation = new Location(20, 20, 20);
    ArrayList<Directions> directionQueue = new ArrayList();
    public static ArrayList<SnakeSegment> snakeSegments = new ArrayList();
    private void initialize(){
        for(int i = 0; i < startingSize; i++)
            snakeSegments.add(new SnakeSegment(startingLocation));
        for(int x = 0; x < 40; x++){
            for(int y = 0; y < 40; y++){
                for(int z = 0; z < 40; z++)
                    map[x][y][z] = new Location(x, y, z);
            }
        }
        directionQueue.add(Directions.UP);
        t = new Target();
    }
    public void addDir(Directions dir){
        Directions prev = directionQueue.get(directionQueue.size() - 1);
        switch(dir){
            case IN:
                if(prev != Directions.IN && prev != Directions.OUT)
                    directionQueue.add(directionQueue.size(), dir);
                break;
            case OUT:
                if(prev != Directions.IN && prev != Directions.OUT)
                    directionQueue.add(directionQueue.size(), dir);
                break;
            case UP:
                if(prev != Directions.UP && prev != Directions.DOWN)
                    directionQueue.add(directionQueue.size(), dir);
                break;
            case DOWN:
                if(prev != Directions.UP && prev != Directions.DOWN)
                    directionQueue.add(directionQueue.size(), dir);
                break;
            case LEFT:
                if(prev != Directions.LEFT && prev != Directions.RIGHT)
                    directionQueue.add(directionQueue.size(), dir);
                break;
            case RIGHT:
                if(prev != Directions.LEFT && prev != Directions.RIGHT)
                    directionQueue.add(directionQueue.size(), dir);
                break;
        }
    }
    public Snake(){
        initialize();
    }
    public void onDown(KeyEvent e){
        char key = e.getKeyChar();
        if(key == 'w')
            switch(selectedView){
                case 1:
                    addDir(Directions.UP);
                    break;
                case 2:
                    addDir(Directions.UP);
                    break;
                case 3:
                    addDir(Directions.IN);
                    break;
            }
        if(key == 's')
            switch(selectedView){
                case 1:
                    addDir(Directions.DOWN);
                    break;
                case 2:
                    addDir(Directions.DOWN);
                    break;
                case 3:
                    addDir(Directions.OUT);
                    break;
            }
        if(key == 'a')
            switch(selectedView){
                case 1:
                    addDir(Directions.LEFT);
                    break;
                case 2:
                    addDir(Directions.OUT);
                    break;
                case 3:
                    addDir(Directions.LEFT);
                    break;
            }
        if(key == 'd')
            switch(selectedView){
                case 1:
                    addDir(Directions.RIGHT);
                    break;
                case 2:
                    addDir(Directions.IN);
                    break;
                case 3:
                    addDir(Directions.RIGHT);
                    break;
            }
        if(e.getKeyCode() == KeyEvent.VK_LEFT)
            selectedView--;
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
            selectedView++;
        selectedView += 3;
        selectedView %= 3;
        if(selectedView == 0)
            selectedView = 3;
    }
    private void gameOver(){
        Main.w.popUp("Game over. You lose with a score of " + (size - startingSize)/5 + ".");
        directionQueue.clear();
        snakeSegments.clear();
        size = startingSize;
        initialize();
    }
    public void animate(){
        if(directionQueue.size() > 1)
            directionQueue.remove(0);
        if(map[(snakeSegments.get(0).x + directionQueue.get(0).xChange + 40) % 40]
              [(snakeSegments.get(0).y + directionQueue.get(0).yChange + 40) % 40]
              [(snakeSegments.get(0).z + directionQueue.get(0).zChange + 40) % 40].sOccupied){
            gameOver();
            return;
        }
        if(map[(snakeSegments.get(0).x + directionQueue.get(0).xChange + 40) % 40]
              [(snakeSegments.get(0).y + directionQueue.get(0).yChange + 40) % 40]
              [(snakeSegments.get(0).z + directionQueue.get(0).zChange + 40) % 40].tOccupied){
            t = new Target();
            size += 5;
            map[(snakeSegments.get(0).x + directionQueue.get(0).xChange + 40) % 40]
               [(snakeSegments.get(0).y + directionQueue.get(0).yChange + 40) % 40]
               [(snakeSegments.get(0).z + directionQueue.get(0).zChange + 40) % 40].tOccupied = false;
        }
        snakeSegments.add(0, new SnakeSegment(snakeSegments.get(0).x + directionQueue.get(0).xChange,
                                              snakeSegments.get(0).y + directionQueue.get(0).yChange,
                                              snakeSegments.get(0).z + directionQueue.get(0).zChange));
        map[snakeSegments.get(0).x][snakeSegments.get(0).y][snakeSegments.get(0).z].sOccupied = true;
        
        if(snakeSegments.size() > size){
            SnakeSegment SegToRem = snakeSegments.get(snakeSegments.size() - 1);
            map[SegToRem.x][SegToRem.y][SegToRem.z].sOccupied = false;
            snakeSegments.remove(snakeSegments.size() - 1);
        }
    }
}
class SnakeSegment {
    int x, y, z;
    private void wrapAround(){
        x += 40;
        y += 40;
        z += 40;
        x %= 40;
        y %= 40;
        z %= 40;
    }
    public SnakeSegment(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
        wrapAround();
    }
    public SnakeSegment(Location l){
        this(l.x, l.y, l.z);
    }
    @Override public String toString(){
        return "(" + x + "," + y + "," + z + ")";
    }
}
class Target {
    int x, y, z;
    public Target(){
        boolean resolved = false;
        while(!resolved){
            x = (int) Math.floor(Math.random() * 40);
            y = (int) Math.floor(Math.random() * 40);
            z = (int) Math.floor(Math.random() * 40);
            resolved = !Snake.map[x][y][z].sOccupied;
        }
        Snake.map[x][y][z].tOccupied = true;
    }
    @Override public String toString(){
        return "(" + x + "," + y + "," + z + ")";
    }
}
class ViewPoint {
    Axis side1, side2;
    public ViewPoint(Axis side1, Axis side2){
        this.side1 = side1;
        this.side2 = side2;
    }
    public BufferedImage generate(){
        BufferedImage bi = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        SnakeSegment head = Snake.snakeSegments.get(0);
        g.setColor(Color.yellow);
        g.fillRect(0, 0, 400, 400);
        if(side1 == Axis.x)
            for(int x = 0; x < 40; x++)
                for(int y = 0; y < 40; y++){
                    Location loc = Snake.map[x][y][head.z];
                    if(loc.sOccupied)
                        g.setColor(Color.green);
                    else if(loc.tOccupied)
                        g.setColor(Color.red);
                    else
                        g.setColor(Color.white);
                    g.fillRect(x * 10, y * 10, 10, 10);
                }
        if(side1 == Axis.y)
            for(int y = 0; y < 40; y++)
                for(int z = 0; z < 40; z++){
                    Location loc = Snake.map[head.x][y][z];
                    if(loc.sOccupied)
                        g.setColor(Color.green);
                    else if(loc.tOccupied)
                        g.setColor(Color.red);
                    else
                        g.setColor(Color.white);
                    g.fillRect(z * 10, y * 10, 10, 10);
                }
        if(side1 == Axis.z)
            for(int z = 0; z < 40; z++)
                for(int x = 0; x < 40; x++){
                    Location loc = Snake.map[x][head.y][z];
                    if(loc.sOccupied)
                        g.setColor(Color.green);
                    else if(loc.tOccupied)
                        g.setColor(Color.red);
                    else
                        g.setColor(Color.white);
                    g.fillRect(x * 10, (39-z) * 10, 10, 10);
                }
        return bi;
    }
}
class Location {
    int x, y, z;
    boolean sOccupied = false;
    boolean tOccupied = false;
    public Location(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
class WindowContent extends JPanel {
    public WindowContent(){
        super();
    }
    @Override public void paint(Graphics G){
        super.paint(G);
        ViewPoint xy = new ViewPoint(Axis.x, Axis.y),
                  yz = new ViewPoint(Axis.y, Axis.z),
                  zx = new ViewPoint(Axis.z, Axis.x);
        BufferedImage img1 = xy.generate();
        BufferedImage img2 = yz.generate();
        BufferedImage img3 = zx.generate();
        G.fillRect(0, 70, getWidth(), 410);
        G.setColor(Color.red);
        G.fillRect((Snake.selectedView - 1) * getWidth() / 3, 70, 410, 410);
        G.drawImage(img1, 5, 75, this);
        G.drawImage(img2, 5 + getWidth() / 3, 75, this);
        G.drawImage(img3, 5 + getWidth()/3*2, 75, this);
        G.setColor(Color.YELLOW);
        G.drawString("Front View", 150, 50);
        G.drawString("Side View", 560, 50);
        G.drawString("Top View", 970, 50);
        G.setColor(Color.green);
        G.drawString("Your coords " + Snake.snakeSegments.get(0), 150, 30);
        G.setColor(Color.red);
        G.drawString("Target coords " + Snake.t, 650, 30);
        
    }
    public void update(){
        revalidate();
        updateUI();
    }
    @Override public void setSize(int x, int y){
        setPreferredSize(new Dimension(x,y));
    }
}
class Window extends JFrame {
    int sizeX = 400;
    int sizeY = 405;
    WindowContent wc = new WindowContent();
    public Window(Snake s){
        super("Snake");
        wc.setSize(sizeX * 3 + 30, sizeY + 75);
        add(wc);
        pack();
        setDefaultCloseOperation(3);
        setVisible(true);
        setFocusable(true);
        addKeyListener(new KeyListener(){
            @Override public void keyPressed(KeyEvent e){
                s.onDown(e);
            }
            @Override public void keyReleased(KeyEvent e){}
            @Override public void keyTyped(KeyEvent e){}
        });
    }
    public void render(){
        wc.repaint();
        wc.update();
    }
    public void popUp(String str){
        JOptionPane.showMessageDialog(this, str);
    }
}
enum Axis {
    x,
    y,
    z;
}
enum Directions {
    UP    ( 0,-1, 0),
    DOWN  ( 0, 1, 0),
    IN    ( 0, 0, 1),
    OUT   ( 0, 0,-1),
    LEFT  (-1, 0, 0),
    RIGHT ( 1, 0, 0);
    public final int xChange;
    public final int yChange;
    public final int zChange;
    Directions(int x, int y, int z){
        this.xChange = x;
        this.yChange = y;
        this.zChange = z;
    }
    @Override public String toString(){
        return "("+ xChange + ", " + yChange + ", " + zChange + ")";
    }
}
