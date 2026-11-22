import java.awt.Graphics;



public class Animation {
    private Stepper stepper;
    private long startTime;
    private long endTime;
    public Animation(float seconds, Stepper stepper) {
        this.stepper = stepper;
        startTime = Life.frameNanoTime;
        endTime = startTime + (long) (seconds * 1e9);
    }

    public void render(Graphics g) {
        if (Life.frameNanoTime >= endTime) {
            stepper.step(g, 1.0f);
            return;
        }
        float percentage = (float) (Life.frameNanoTime - startTime) / (float) (endTime - startTime);
        stepper.step(g, percentage);
    }

    public boolean isFinished() {
        return Life.frameNanoTime >= endTime;
    }
}
