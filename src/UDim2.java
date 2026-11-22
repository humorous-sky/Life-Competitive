public class UDim2 {
    private float scaleX;
    private float scaleY;
    private float offsetX;
    private float offsetY;

    public UDim2(float scaleX, float scaleY, float offsetX, float offsetY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public float getScaleX() {
        return scaleX;
    }
    public float getScaleY() {
        return scaleY;
    }
    public float getOffsetX() {
        return offsetX;
    }
    public float getOffsetY() {
        return offsetY;
    }
    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }
    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }
    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }
    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public int getX(int parentWidth) {
        return (int) (scaleX * parentWidth + offsetX);
    }
    public int getY(int parentHeight) {
        return (int) (scaleY * parentHeight + offsetY);
    }

    public UDim2 add(UDim2 other) {
        return new UDim2(scaleX + other.scaleX, scaleY + other.scaleY, offsetX + other.offsetX, offsetY + other.offsetY);
    }
    public UDim2 subtract(UDim2 other) {
        return new UDim2(scaleX - other.scaleX, scaleY - other.scaleY, offsetX - other.offsetX, offsetY - other.offsetY);
    }
    public UDim2 multiply(float scalar) {
        return new UDim2(scaleX * scalar, scaleY * scalar, offsetX * scalar, offsetY * scalar);
    }
    public UDim2 divide(float scalar) {
        return new UDim2(scaleX / scalar, scaleY / scalar, offsetX / scalar, offsetY / scalar);
    }
    public UDim2 interpolate(UDim2 other, float t) {
        return new UDim2(scaleX + (other.scaleX - scaleX) * t, scaleY + (other.scaleY - scaleY) * t, offsetX + (other.offsetX - offsetX) * t, offsetY + (other.offsetY - offsetY) * t);
    }

}
