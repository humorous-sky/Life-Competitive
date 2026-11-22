import javax.swing.JPanel;


import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.util.Set;
import java.util.HashSet;

public class StartScreen extends JPanel implements MouseMotionListener, MouseListener {
    private static final long serialVersionUID = 1L;
    private long startTime = Life.frameTime;
    private float opacity = 0;
    private ArrayList<TextBox> textBoxes = new ArrayList<TextBox>();
    private Set<Animation> anims = new HashSet<Animation>();
    private Set<Animation> animsToRemove = new HashSet<Animation>();
    public StartScreen() {
        TextBox title = new TextBox("Life: Competitive", new UDim2(0.05f, 0.15f, 0, 0), new UDim2(0.4f, 0.1f, 0, 0), Color.BLUE, Assets.fonts.get("MultiemilyRegular-DRrR.ttf"));
        textBoxes.add(title);
        TextBox subtitle = new TextBox("Based on Conway's Game of Life", new UDim2(0.05f, 0.25f, 0, 0), new UDim2(0.4f, 0.1f, 0, 0), Color.RED, Assets.fonts.get("MultiemilyRegular-DRrR.ttf"));
        textBoxes.add(subtitle);
        Button startButton = new Button("Play VS Bot", new UDim2(0.05f, 0.5f, 0, 0), new UDim2(0.4f, 0.1f, 0, 0), Color.BLUE, Assets.fonts.get("MultiemilyRegular-DRrR.ttf"));
        textBoxes.add(startButton);

        Button friendButton = new Button("Play VS Friend", new UDim2(0.05f, 0.65f, 0, 0), new UDim2(0.4f, 0.1f, 0, 0), Color.BLUE, Assets.fonts.get("MultiemilyRegular-DRrR.ttf"));
        textBoxes.add(friendButton);

        JPanel cur = this;

        startButton.setOnClickEvent(new ClickEvent() {
            @Override
            public void mouseDown() {
               
            }
            @Override
            public void mouseUp() {
                Life.navigate(cur, new GameScreen(true));
            }
        });

        friendButton.setOnClickEvent(new ClickEvent() {
            @Override
            public void mouseDown() {
               
            }
            @Override
            public void mouseUp() {
                Life.navigate(cur, new GameScreen(false));
            }
        });

        Button rulesButton = new Button("How 2 Play", new UDim2(0.05f, 0.8f, 0, 0), new UDim2(0.4f, 0.1f, 0, 0), Color.RED, Assets.fonts.get("MultiemilyRegular-DRrR.ttf"));
        textBoxes.add(rulesButton);

        Button creditsButton = new Button("Credits", new UDim2(0.764f, 0.85f, 0, 0), new UDim2(0.186f, 0.05f, 0, 0), Color.RED, Assets.fonts.get("MultiemilyRegular-DRrR.ttf"));
        textBoxes.add(creditsButton);
        
        Button p1Skin = new Button(Board.p1Skins[Life.p1SkinIndex], new UDim2(0.824f, 0.5f, 0, 0), new UDim2(0.066f, 0.1f, 0, 0), Color.WHITE, Assets.fonts.get("NotoEmoji-VariableFont_wght.ttf"));
        p1Skin.setBackgroundColor(Color.RED);
        textBoxes.add(p1Skin);

        TextBox p1SkinText = new TextBox("P1's Skin: " + Board.p1Names[Life.p1SkinIndex], new UDim2(0.764f, 0.436f, 0, 0), new UDim2(0.186f, 0.05f, 0, 0), Color.RED, Assets.fonts.get("MultiemilyRegular-DRrR.ttf"));
        textBoxes.add(p1SkinText);

        p1Skin.setOnClickEvent(new ClickEvent() {
            @Override
            public void mouseDown() {
                Life.p1SkinIndex = (int) (Math.random() * Board.p1Skins.length);
            }
            @Override
            public void mouseUp() {
                p1Skin.setText(Board.p1Skins[Life.p1SkinIndex]);
                p1SkinText.setText("P1's Skin: " + Board.p1Names[Life.p1SkinIndex]);
            }
        });

        Button p1Prev = new Button("Prev.", new UDim2(0.764f, 0.5f, 0, 0), new UDim2(0.05f, 0.1f, 0, 0), Color.RED, Assets.fonts.get("MultiemilyRegular-DRrR.ttf"));
        textBoxes.add(p1Prev);

        p1Prev.setOnClickEvent(new ClickEvent() {
            @Override
            public void mouseDown() {
                Life.p1SkinIndex += Board.p1Skins.length;
                Life.p1SkinIndex --;
                Life.p1SkinIndex %= Board.p1Skins.length;   
            }
            @Override
            public void mouseUp() {
                p1Skin.setText(Board.p1Skins[Life.p1SkinIndex]);
                p1SkinText.setText("P1's Skin: " + Board.p1Names[Life.p1SkinIndex]);
            }
        });

        Button p1Next = new Button("Next", new UDim2(0.9f, 0.5f, 0, 0), new UDim2(0.05f, 0.1f, 0, 0), Color.RED, Assets.fonts.get("MultiemilyRegular-DRrR.ttf"));
        textBoxes.add(p1Next);

        p1Next.setOnClickEvent(new ClickEvent() {
            @Override
            public void mouseDown() {
                Life.p1SkinIndex ++;
                Life.p1SkinIndex %= Board.p1Skins.length;
            }
            @Override
            public void mouseUp() {
                p1Skin.setText(Board.p1Skins[Life.p1SkinIndex]);
                p1SkinText.setText("P1's Skin: " + Board.p1Names[Life.p1SkinIndex]);
            }
        });

        Button p2Skin = new Button(Board.p2Skins[Life.p2SkinIndex], new UDim2(0.824f, 0.65f, 0, 0), new UDim2(0.066f, 0.1f, 0, 0), Color.WHITE, Assets.fonts.get("NotoEmoji-VariableFont_wght.ttf"));
        p2Skin.setBackgroundColor(Color.BLUE);
        textBoxes.add(p2Skin);

        TextBox p2SkinText = new TextBox("P2's Skin: " + Board.p2Names[Life.p2SkinIndex], new UDim2(0.764f, 0.764f, 0, 0), new UDim2(0.186f, 0.05f, 0, 0), Color.BLUE, Assets.fonts.get("MultiemilyRegular-DRrR.ttf"));
        textBoxes.add(p2SkinText);

        p2Skin.setOnClickEvent(new ClickEvent() {
            @Override
            public void mouseDown() {
                Life.p2SkinIndex = (int) (Math.random() * Board.p2Skins.length);
            }
            @Override
            public void mouseUp() {
                p2Skin.setText(Board.p2Skins[Life.p2SkinIndex]);
                p2SkinText.setText("P2's Skin: " + Board.p2Names[Life.p2SkinIndex]);
            }
        });

        Button p2Prev = new Button("Prev.", new UDim2(0.764f, 0.65f, 0, 0), new UDim2(0.05f, 0.1f, 0, 0), Color.BLUE, Assets.fonts.get("MultiemilyRegular-DRrR.ttf"));
        textBoxes.add(p2Prev);

        p2Prev.setOnClickEvent(new ClickEvent() {
            @Override
            public void mouseDown() {
                Life.p2SkinIndex += Board.p2Skins.length;
                Life.p2SkinIndex --;
                Life.p2SkinIndex %= Board.p2Skins.length;
            }
            @Override
            public void mouseUp() {
                p2Skin.setText(Board.p2Skins[Life.p2SkinIndex]);
                p2SkinText.setText("P2's Skin: " + Board.p2Names[Life.p2SkinIndex]);
            }
        });

        Button p2Next = new Button("Next", new UDim2(0.9f, 0.65f, 0, 0), new UDim2(0.05f, 0.1f, 0, 0), Color.BLUE, Assets.fonts.get("MultiemilyRegular-DRrR.ttf"));
        textBoxes.add(p2Next);

        p2Next.setOnClickEvent(new ClickEvent() {
            @Override
            public void mouseDown() {
                Life.p2SkinIndex ++;
                Life.p2SkinIndex %= Board.p2Skins.length;
            }
            @Override
            public void mouseUp() {
                p2Skin.setText(Board.p2Skins[Life.p2SkinIndex]);
                p2SkinText.setText("P2's Skin: " + Board.p2Names[Life.p2SkinIndex]);
            }
        });

        addMouseMotionListener(this);
        addMouseListener(this);
    }

    @Override 
    public void paint(Graphics g) {
        opacity = (float) Math.tanh((System.currentTimeMillis() - startTime)/1000.0 - 3.5)/2 + 0.5f;
        g.setColor(new Color(255, 255, 255, (int) (255 * opacity)));
        g.fillRect(0, 0, (int) Life.width, (int) Life.height);
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
        Life.enqueue(true);

    }

    @Override
    public void mouseReleased(MouseEvent e) {
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
