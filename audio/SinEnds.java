package audio;

public class SinEnds implements EndsTreatment{

    private static final float pi2 = (float) (Math.PI/2);
    private int width, halfWidth;

    private float k;

    public SinEnds(int halfWidth) {
        this.halfWidth = halfWidth;
        width = 2*halfWidth;
        k = (float) (Math.PI/width);
    }


    @Override
    public int getHalfWidth() {
        return halfWidth;
    }

    @Override
    public void treat(float[] line) {
        int np = line.length - width;
        for (int i = 0; i < width; i++) {
            float sin = (float) ((Math.sin(k*i - pi2) + 1.0f)/2);
            line[i] *= sin;
            line[i + np] *= 1.0f - sin;
        }
    }
}
