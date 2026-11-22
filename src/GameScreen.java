import javax.swing.JPanel;


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.ArrayList;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.util.Set;
import java.util.HashSet;

public class GameScreen extends JPanel implements MouseMotionListener, MouseListener {
    private static final long serialVersionUID = 1L;
    private static final Color institutionalWhite = new Color(248, 248, 248);
    private long startTime = Life.frameTime;
    private float opacity = 0;

    private short state = 0;
    //0 = red interlude
    //1 = red play
    //2 = blue interlude
    //3 = blue play
    //4 = seeding/growing
    //5 = reproducing
    //6 = grow/oof

    private long nextStateChange = Life.frameTime + 5000;

    private Board board;

    private Button[][] game = new Button[Board.SIZEX][Board.SIZEY];
    private ArrayList<TextBox> textBoxes = new ArrayList<TextBox>();

    private TextBox red;
    private Button[] confs;
    private TextBox blue;

    private TextBox gen;

    private Button timer;

    
    private Set<Animation> anims = new HashSet<Animation>();
    private Set<Animation> animsToRemove = new HashSet<Animation>();

    private int[] pops;

    private int mX = -1, mY = -1;
    private int selX = -1, selY = -1;
    private int[] Xs = {-1, -1};
    private int[] Ys = {-1, -1};

    private boolean bot = false;
    private Thread botThread = null;
    public GameScreen(boolean bot) {
        this.bot = bot;

        for (int x = 0; x < game.length; x ++) {
            for (int y = 0; y < game[x].length; y ++) {
                game[x][y] = new Button("", new UDim2(1.0f/game.length * x, 1.0f/game[x].length * y, 0, 0), new UDim2(1.0f/game.length, 1.0f/game[x].length, 0, 0), institutionalWhite, Assets.fonts.get("NotoEmoji-VariableFont_wght.ttf"));
                
                final int X = x;
                final int Y = y;
                game[x][y].setOnClickEvent(new ClickEvent() {
                    @Override
                    public void mouseDown() {}
                    @Override
                    public void mouseUp() {
                        if (state <= 3) {
                            if (bot && state >= 2) {
                                return;
                            }
                            if (board.isValidSeed(state <= 1, X, Y)) {
                                setSelection(X, Y);
                                Xs[state/2] = selX;
                                Ys[state/2] = selY;
                            }
                        }
                    }
                });
            }
        }

        board = new Board();
        pops = board.countPopulations();

        red = new TextBox(Board.p1Names[Life.p1SkinIndex] + " Population: " + pops[0], new UDim2(0.025f, 0, 0, 0), new UDim2(0.2f, 0.1f, 0, 0), Color.RED,  Assets.fonts.get("MultiemilyRegular-DRrR.ttf"));
        textBoxes.add(red);

        confs = new Button[2];

        confs[0] = new Button("Continue Without Seeding", new UDim2(0.025f, 0.1f, 0, 0), new UDim2(0.2f, 0.05f, 0, 0), Color.RED,  Assets.fonts.get("MultiemilyRegular-DRrR.ttf"));
        

        blue = new TextBox(Board.p2Names[Life.p2SkinIndex] + " Population: " + pops[1], new UDim2(0.775f, 0.8f, 0, 0), new UDim2(0.2f, 0.1f, 0, 0), Color.BLUE,  Assets.fonts.get("MultiemilyRegular-DRrR.ttf"));
        textBoxes.add(blue);

        confs[1] = new Button("Continue Without Seeding", new UDim2(0.775f, 0.7f, 0, 0), new UDim2(0.2f, 0.05f, 0, 0), Color.BLUE,  Assets.fonts.get("MultiemilyRegular-DRrR.ttf"));

        timer = new Button("15.0", new UDim2(0.25f, 0.03125f, 0, 0), new UDim2(0.5f, 0.046875f, 0, 0), Color.RED, Assets.fonts.get("MultiemilyRegular-DRrR.ttf"));

        gen = new TextBox("Generation 0", new UDim2(0.25f, 0.890625f, 0, 0), new UDim2(0.5f, 0.046875f, 0, 0), Color.BLACK, Assets.fonts.get("MultiemilyRegular-DRrR.ttf"));
        textBoxes.add(gen);

        for (Button btn : confs) {
            btn.setOnClickEvent(new ClickEvent() {

                @Override
                public void mouseDown() {
                    
                }

                @Override
                public void mouseUp() {
                    if (state == 1 || state == 3) {
                        nextStateChange = Life.frameTime;
                    }
                }
                
            });
        }
        updateBoard();

        addMouseMotionListener(this);
        addMouseListener(this);
    }

    @Override 
    public void paint(Graphics g) {
        opacity = (float) Math.tanh((System.currentTimeMillis() - startTime)/1000.0 - 3.5)/2 + 0.5f;
        g.setColor(new Color(255, 255, 255, (int) (255 * opacity)));
        g.fillRect(0, 0, (int) Life.width, (int) Life.height);

        if (Life.frameTime > nextStateChange) {
            state ++;
            state %= 6;
            switch (state) {
                case 0: case 2:
                    updateBoard();//Sync just in case
                    if (board.getGeneration() >= 60 || pops[0] == 0 || pops[1] == 0) {
                        System.out.println(pops[0] + " " + pops[1]);
                        System.exit(0);
                    }
                    if (state == 2) {
                        Xs[0] = selX;
                        Ys[0] = selY;

                        if (bot && botThread == null) {
                            botThread = new Thread(() -> {
                                int[] botMove = board.getBestMoveAndScore(false, 2);
                                if (state == 2 || state == 3) {
                                    selX = botMove[0];
                                    selY = botMove[1];
                                    state = 3;
                                    nextStateChange = Life.frameTime;
                                }
                                botThread = null;
                            });
                            botThread.start();
                        }
                    }
                    setSelection(-1, -1);
                    nextStateChange = Life.frameTime + 1500;
                    
                    break;
                case 1: case 3: 
                    timer.setColor(state == 1 ? Color.RED : Color.BLUE);
                    nextStateChange = Life.frameTime + 10000;
                    break;
                case 4:
                    Xs[1] = selX;
                    Ys[1] = selY;
                    setSelection(-1, -1);
                    if (Xs[0] == Xs[1] && Ys[0] == Ys[1]) {
                        if (Xs[0] < 0 || Xs[0] >= game.length || Ys[0] < 0 || Ys[0] >= game[Xs[0]].length) {
                            nextStateChange = Life.frameTime;
                            break;
                        }

                        final Button btn = game[Xs[0]][Ys[0]];
                        btn.setText(Board.invalid);
                        anims.add(new Animation(1.5f, (Graphics g2d, float percentage) -> {
                            percentage = -2 * Math.abs(percentage - 0.5f) + 1; 
                            btn.setMaxFontSize((int) (100 * percentage));
                            btn.setBackgroundColor(interpColor(Color.WHITE, Color.BLACK, percentage));
                        }));
                        
                        nextStateChange = Life.frameTime + 1500;
                        break;
                    }
                    for (int i = 0; i < 2; i ++) {
                        if (Xs[i] < 0 || Xs[i] >= game.length || Ys[i] < 0 || Ys[i] >= game[Xs[i]].length) {
                            continue;
                        }
                        makeAnim(i == 0, true, Xs[i], Ys[i]);
                        board.seed(i == 0, Xs[i], Ys[i]);
                    }
                    pops = board.countPopulations();
                    red.setText(Board.p1Names[Life.p1SkinIndex] + " Population: " + pops[0]);
                    blue.setText(Board.p2Names[Life.p2SkinIndex] + " Population: " + pops[1]);
                    nextStateChange = Life.frameTime + 1500;
                    break;
                case 5:
                    board.computeNextHalfRound();
                    gen.setText("Generation " + board.getGeneration());
                    Board.Cell[][] b = board.getBoard();

                    for (int x = 0; x < game.length && x < b.length; x ++) {
                        for (int y = 0; y < game[x].length && y < b[x].length; y ++) {
                            switch(b[x][y]) {
                                case BLUEawakening:
                                    makeAnim(false, true, x, y);
                                    break;
                                case REDawakening:
                                    makeAnim(true, true, x, y);
                                    break;
                                case BLUEdying:
                                    makeAnim(false, false, x, y);
                                    break;
                                case REDdying:
                                    makeAnim(true, false, x, y);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    board.updateCells();
                    pops = board.countPopulations();
                    red.setText(Board.p1Names[Life.p1SkinIndex] + " Population: " + pops[0]);
                    blue.setText(Board.p2Names[Life.p2SkinIndex] + " Population: " + pops[1]);
                    nextStateChange = Life.frameTime + 1500;
                    break;
                    
            }
        }
        if (state == 1 || state == 3) {
            timer.setText(String.format("%.2g", (nextStateChange - Life.frameTime)/1000.0));
            timer.setSize(new UDim2((nextStateChange - Life.frameTime)/20000.0f, 0.046875f, 0, 0));
            timer.setPosition(new UDim2(0.5f - (nextStateChange - Life.frameTime)/40000.0f, 0.03125f, 0, 0));
            timer.render(g, (int) Life.width, (int) Life.height, anims);
            
            if (state != 3 || !bot) {
                confs[state/2].render(g, (int) Life.width, (int) Life.height, anims);
            }
        }

        paintBoard(g, Life.width/4, Life.height/8 - Life.height/64, Life.width/2, 3 * Life.height/4);

       for (TextBox textBox : textBoxes) {
            Color c = textBox.getColor();
            textBox.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (255 * opacity)));
            textBox.render(g, (int) Life.width, (int) Life.height, anims);
        }

        for (Animation anim : anims) {
            anim.render(g);
            if (anim.isFinished()) {
                animsToRemove.add(anim);
            }
        }

        for (Animation anim : animsToRemove) {
            anims.remove(anim);
        }
        animsToRemove.clear();

    }

    public void paintBoard(Graphics g, double X, double Y, double width, double height) {

        double scale = Math.min(width/Board.SIZEX, height/Board.SIZEY);

        double xSize = scale * Board.SIZEX;
        double ySize = scale * Board.SIZEY;

        X += (width - xSize)/2;
        Y += (height - ySize)/2;

        mX = (int) ((Life.mouseX - X)/scale);
        mY = (int) ((Life.mouseY - Y)/scale);
        
        g.setColor(new Color(64, 64, 64, (int) (255 * opacity)));
        ((Graphics2D) g).setStroke(new java.awt.BasicStroke(3));
        g.drawRoundRect((int) X-5, (int) Y-5, (int) xSize+10, (int) ySize+10, 15, 15);


        for (int x = 0; x < game.length; x ++) {
            for (int y = 0; y < game[x].length; y ++) {
                if (x != mX || y != mY || x != selX || x != selY) {
                    game[x][y].render(g, (int) X, (int) Y, (int) xSize, (int) ySize, anims);
                }
            }
        }

        if (mX >= 0 && mX < game.length && mY >= 0 && mY < game[mX].length) {
            game[mX][mY].render(g, (int) X, (int) Y, (int) xSize, (int) ySize, anims);
        }
        if (selX >= 0 && selX < game.length && selY >= 0 && selY < game[selX].length) {
            game[selX][selY].render(g, (int) X, (int) Y, (int) xSize, (int) ySize, anims);
        }
    }
    public void updateBoard(){
        for (int x = 0; x < game.length; x ++) {
            for (int y = 0; y < game[x].length; y ++) {
                if (board.getBoard()[x][y] == Board.Cell.RED) {
                    game[x][y].setBackgroundColor(Color.RED);
                    game[x][y].setText(Board.p1Skins[Life.p1SkinIndex]);
                    game[x][y].setMaxFontSize(100);
                } else if (board.getBoard()[x][y] == Board.Cell.BLUE) {
                    game[x][y].setBackgroundColor(Color.BLUE);
                    game[x][y].setText(Board.p2Skins[Life.p2SkinIndex]);
                    game[x][y].setMaxFontSize(100);
                } else {
                    game[x][y].setBackgroundColor(Color.WHITE);
                    game[x][y].setText("");
                    game[x][y].setMaxFontSize(0);
                }
            }
        }
    }
    public void setSelection(int x, int y) {
        if (selX >= 0 && selX < game.length && selY >= 0 && selY < game[selX].length) {
            game[selX][selY].setColor(institutionalWhite);
        }
        
        if (x >= 0 && x < game.length && y >= 0 && y < game[y].length && (x != selX || y != selY)) {
            selX = x;
            selY = y;
            game[selX][selY].setColor(new Color(0, 128, 0));
            for (Button btn : confs) {
                btn.setText("Continue");
            }
        } else {
            selX = -1;
            selY = -1;
            for (Button btn : confs) {
                btn.setText("Continue Without Seeding");
            }
        }
        
    }
    public void makeAnim(boolean red, boolean birth, int x, int y) {
        final Color s = birth ? Color.WHITE : (red ? Color.RED : Color.BLUE);
        final Color c = birth ? (red ? Color.RED : Color.BLUE) : Color.WHITE;
        final Button btn = game[x][y];
        btn.setText(red ? Board.p1Skins[Life.p1SkinIndex] : Board.p2Skins[Life.p2SkinIndex]);
        int font = btn.getMaxFontSize();
        btn.setMaxFontSize(birth ? 0 : font);

        anims.add(new Animation(1f, (Graphics g2d, float percentage) -> {
            btn.setMaxFontSize(birth ? ((int) (percentage * 100)) : ((int) (font - font * percentage)));
            btn.setBackgroundColor(interpColor(s, c, percentage));
        }));
    }


    public Color interpColor(Color c1, Color c2, float percentage) {
        int rDiff = c2.getRed() - c1.getRed();
        int gDiff = c2.getGreen() - c1.getGreen();
        int bDiff = c2.getBlue() - c1.getBlue();

        return new Color((int) (rDiff * percentage + c1.getRed()), (int) (gDiff * percentage + c1.getGreen()), (int) (bDiff * percentage + c1.getBlue()));
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (opacity < 0.99f) {return;}
        mouseMoved(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (opacity < 0.99f) {return;}
        Life.mouseX = e.getX();
        Life.mouseY = e.getY();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (opacity < 0.99f) {return;}
        Life.mouseX = e.getX();
        Life.mouseY = e.getY();
     }

    @Override
    public void mousePressed(MouseEvent e) {
        if (opacity < 0.99f) {return;}
        mouseMoved(e);
        Life.enqueue(true);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseMoved(e);
        Life.enqueue(false);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (opacity < 0.99f) {return;}
        Life.mouseX = e.getX();
        Life.mouseY = e.getY();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        Life.mouseX = -1;
        Life.mouseY = -1;
    }
}
