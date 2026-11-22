import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.Set;

public class TextBox {
    private String text;
    private UDim2 position;
    private UDim2 size;
    private Font font;
    private Color color;
    private int maxFontSize;

    public TextBox(String text, UDim2 position, UDim2 size, Color color, Font font) {
        this.text = text;
        this.position = position;
        this.size = size;
        this.color = color;
        this.font = font;
        this.maxFontSize = 1000;
        if (this.font == null) {
            this.font = new Font("Arial", Font.PLAIN, 12);
            System.out.println("Font is null, defaulting to Arial");
        }
    }

    public String getText() {
        return text;
    }
    public UDim2 getPosition() {
        return position;
    }
    public UDim2 getSize() {
        return size;
    }
    public Color getColor() {
        return color;
    }
    public Font getFont() {
        return font;
    }
    public int getMaxFontSize() {
        return maxFontSize;
    }

    public void setText(String text) {
        this.text = text;
    }
    public void setPosition(UDim2 position) {
        this.position = position;
    }
    public void setSize(UDim2 size) {
        this.size = size;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    public void setFont(Font font) {
        this.font = font;
    }
    public void setMaxFontSize(int maxFontSize) {
        this.maxFontSize = maxFontSize;
    }

    public void render(Graphics g, int parentX, int parentY, int parentWidth, int parentHeight, Set<Animation> anims) {
        
        int x = position.getX(parentWidth) + parentX;
        int y = position.getY(parentHeight) + parentY;
        int width = size.getX(parentWidth);
        int height = size.getY(parentHeight);
        
        g.setColor(color);
        
        //g.drawRect(x, y, width, height);
        
        int optimalFontSize = getOptimalFontSize(g, text, width, height);

        g.setFont(font.deriveFont((float) optimalFontSize));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        int textX = x + (width - textWidth) / 2;
        int textY = y + (height + textHeight) / 2 - fm.getDescent()/2;
        
        g.drawString(text, textX, textY);
    }
    public void render(Graphics g, int parentWidth, int parentHeight, Set<Animation> anims) {
        render(g, 0, 0, parentWidth, parentHeight, anims);
    }
    private int getOptimalFontSize(Graphics g, String text, int maxWidth, int maxHeight) { // Binary search for the optimal font size
        // Very fast algorithm and very accurate
        int high = maxFontSize;
        int low = 0;
        FontMetrics fm;
        while (low < high) {
            int mid = (low + high + 1) / 2;
            g.setFont(font.deriveFont((float) mid));
            fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();
            if (textWidth <= maxWidth && textHeight <= maxHeight) {
                low = mid;
            } else {
                high = mid - 1;
            }
        }
        return low;
    }
}
