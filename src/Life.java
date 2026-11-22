import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;


public class Life {
    static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    static double width = screenSize.getWidth();
    static double height = screenSize.getHeight();
    //sets up fps calculation 
    static int frames = 0;
    static long frameNanoTime = System.nanoTime();
    static long frameTime = System.currentTimeMillis();
    static long lastUpdate = frameNanoTime;
    static long lastSound = frameTime;
    static long nextBGM = frameTime;
    static long startTime = frameTime;
    private static long lastNavigate = frameTime - 1000;
    static double fps = 15.0;
    static BufferedImage img = null;
    static JPanel s;// = new Screen(1, 26, new Basic(Screen.X(500), Screen.Y(880)));
    static JPanel queue;
    static final String version = "1.0.0";
    static final String title = "Life: Competitive";

    static int mouseX = -1;
    static int mouseY = -1;
    static boolean mousePressed = false;

    static int p1SkinIndex = (int) (Math.random() * Board.p1Skins.length);
    static int p2SkinIndex = (int) (Math.random() * Board.p2Skins.length);

    private static Queue<Boolean> evtQueue = new LinkedList<>();

    //initalizes the frame
    static JFrame frame = new JFrame();
    
    public static void main(String[] args) {
        Assets.loadSounds();
        Assets.loadImages();
        Assets.loadFonts();
    	s = new StartScreen();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocation(0, 0);
        frame.setName(title + " " + version);
        frame.setTitle(title);
        img = Assets.images.get("icon.png");
        if (img != null) {
        	frame.setIconImage(img);
        }

        frame.setVisible(true);
        //initalizes the screen
        frame.add(s);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        //main game loop
        while (true) { 
        	width = frame.getWidth();
        	height = frame.getHeight();
            frameNanoTime = System.nanoTime();
            frameTime = System.currentTimeMillis();

              //Updates fps every 166 milliseconds or 166000000 nanoseconds
              if (frameNanoTime >= lastUpdate + 166000000L) {
                      //Calculates fps
                      fps = frames/((frameNanoTime - lastUpdate)/1000000000.0);
                      //Resets update cooldown
                      lastUpdate = frameNanoTime; 
                      //Resets frame counter
                      frames = 0;
              }
              if (frameTime >= lastSound + 100) {
                  //calculates fps
                  Assets.playAll();
                  lastSound = Life.frameTime; 
              }

              if (frameTime >= nextBGM) {
                  Assets.playSound("Edvard-grieg-morning-mood.wav", 2);
                  nextBGM = Life.frameTime + 1000 * 60 * 3 + 50 * 1000;
              }

              if (!evtQueue.isEmpty()) {
                mousePressed = evtQueue.poll();
              } else {
                mousePressed = false;
              }
              //paints the screen
              s.repaint();
              frames ++;
              
              try {	  
            	Thread.sleep(33);
              } catch (InterruptedException ignored) {

              } 

              if (queue != null) {
            	  frame.getContentPane().removeAll();
            	  frame.add(queue);
            	  //updates screen
                  frame.setVisible(true);
            	  Life.s = queue;
            	  queue = null;
              }

         }
    }
    public static void enqueue(boolean clicked) {
        if (evtQueue.size() < 10) {
            evtQueue.offer(clicked);
        }
    }
    public static void navigate(JPanel from, JPanel to) {
    	if (System.currentTimeMillis() - 1000 >= lastNavigate) {
    		lastNavigate = System.currentTimeMillis();
    		from.removeAll();
            try {
                from.removeMouseListener((MouseListener) from);
                from.removeMouseMotionListener((MouseMotionListener) from);
            } catch (Exception ignored) {

            }
            
    		queue = queue == null ? to : queue;
    		// screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    	    // width = screenSize.getWidth();
    	    // height = screenSize.getHeight();
    	}
    }
}