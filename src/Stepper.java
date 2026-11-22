import java.awt.Graphics;

public interface Stepper {
    void step(Graphics g, float percentage); // percentage is a value between 0 and 1 indicating the progress of the step

}
