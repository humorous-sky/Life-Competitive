import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Set;

public class Button extends TextBox {
    private boolean mouseOver = false;
    private boolean down = false;

    private Animation a = null;

    private Color og;
    private Color darker;

    private Color bg;

    private UDim2 smallPosition;
    private UDim2 smallSize;

    private UDim2 bigPosition;
    private UDim2 bigSize;

    private ClickEvent onClick = null;

    public Button(String text, UDim2 position, UDim2 size, Color color, Font font) {
        super(text, position, size, color, font);
        
        og = color;
        darker = color.darker();

        smallSize = size;
        smallPosition = position;

        bigSize = size.multiply(1.15f);
        bigPosition = position.subtract(bigSize.subtract(size).divide(2));
        bg = Color.WHITE;
    }

    public void setOnClickEvent(ClickEvent onClick) {
        this.onClick = onClick;
    }

    public void setPosition(UDim2 position) {
        super.setPosition(position);
        smallPosition = position;
        bigPosition = position.subtract(bigSize.subtract(smallSize).divide(2));
    }

    public void setSize(UDim2 size) {
        super.setSize(size);
        smallSize = size;
        bigSize = size.multiply(1.15f);
        bigPosition = smallPosition.subtract(bigSize.subtract(smallSize).divide(2));
    }

    public void render(Graphics g, int parentX, int parentY, int parentWidth, int parentHeight, Set<Animation> anims) {
       int x = super.getPosition().getX(parentWidth) + parentX;
        int y = super.getPosition().getY(parentHeight) + parentY;
        int width = super.getSize().getX(parentWidth);
        int height = super.getSize().getY(parentHeight);

        int smallX = smallPosition.getX(parentWidth) + parentX;
        int smallY = smallPosition.getY(parentHeight) + parentY;
        int smallWidth = smallSize.getX(parentWidth);
        int smallHeight = smallSize.getY(parentHeight);

        if (Life.mouseX >= smallX && Life.mouseX <= smallX + smallWidth && Life.mouseY >= smallY && Life.mouseY <= smallY + smallHeight) {
            
            if (!mouseOver) {
                anims.remove(a);

                a = new Animation(0.1f, (Graphics g2d, float percentage) -> {
                             
                    UDim2 newPosition = smallPosition.interpolate(bigPosition, percentage);
                    UDim2 newSize = smallSize.interpolate(bigSize, percentage);
                    super.setPosition(newPosition);
                    super.setSize(newSize);
                });

                anims.add(a);

            }
            
            mouseOver = true;
        } else {
            if (mouseOver) {
               anims.remove(a);

                a = new Animation(0.1f, (Graphics g2d, float percentage) -> {                   
                  UDim2 newPosition = bigPosition.interpolate(smallPosition, percentage);
                  UDim2 newSize = bigSize.interpolate(smallSize, percentage);
                  super.setPosition(newPosition);
                  super.setSize(newSize);        
                });

                anims.add(a);
            }
            mouseOver = false;
        }

        int arcWidth = Math.min(width, height) / 3;
        int arcHeight = Math.min(width, height) / 3;
        
        int opacity = g.getColor().getAlpha();

        g.setColor(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), opacity/3 * 2));
        g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);

        if (mouseOver && Life.mousePressed) {
            super.setColor(new Color(darker.getRed(), darker.getGreen(), darker.getBlue(), opacity));
            if (!down) {
                if (onClick != null) {
                    onClick.mouseDown();
                }
                down = true;
            }

        } else {
           super.setColor(new Color(og.getRed(), og.getGreen(), og.getBlue(), opacity));
           if (down) {         
                if (onClick != null) {
                    onClick.mouseUp();
                }
                down = false;
           }
        }
        g.setColor(super.getColor());

        ((Graphics2D) g).setStroke(new java.awt.BasicStroke(3));
        g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
       super.render(g, parentX, parentY, parentWidth, parentHeight, anims);
    }
    public void setBackgroundColor(Color c) {
        this.bg = c;
    }
    public void setColor(Color c) {
        super.setColor(c);
        og = c;
        darker = c.darker();
    }
    public Color getColor() {
        return og;
    }
}
