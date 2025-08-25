package audio;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class AudioPainter {
    private AudioImagePanel panel;
    private EndsTreatment ends;
    private float sampleRate, sampMils;
    private float gain = 1000.0f;

    public AudioPainter(AudioImagePanel panel) {
        this.panel = panel;
        sampleRate = panel.getAudioFormat().getSampleRate();
        sampMils = sampleRate/1000.0f;
        ends = new SinEnds(1000);
    }

    public void setEnd(EndsTreatment end) {
        this.ends = end;
    }

    public void setGain(float gain) {
        if (gain > 0.0f) {
            this.gain = gain;
        }
    }


    public void drawLine(float t1, float v1, float t2, float v2) {
        float nt1, nv1, nt2, nv2;
        if (t1 < t2) {
            nt1 = t1;
            nv1 = v1;
            nt2 = t2;
            nv2 = v2;
        } else {
            nt1 = t2;
            nv1 = v2;
            nt2 = t1;
            nv2 = v1;
        }
        int half = ends.getHalfWidth();
        int l = (int) ((nt2 - nt1)*sampMils);
        float[] buf = new float[l + 2*half];
        float f1 = genVertical(buf, 0, half, nv1, 0.0f);
        float f2 = genLine(buf, half, l, nv1, nv2, f1);
        genVertical(buf, l + half, half, nv2, f2);
        ends.treat(buf);
        loadBuffer(buf, (int) (nt1*sampMils) - half);
    }

    public void drawPolyline(Point2D.Float[] linePoints) {
        Point2D.Float pbuf = linePoints[0];
        ArrayList<Point2D.Float> line = new ArrayList<>();
        line.addFirst(pbuf);
        boolean up = linePoints[1].x > pbuf.x;
        for (int i = 1; i < linePoints.length; i++) {
            Point2D.Float p = linePoints[i];
            boolean tec = p.x > pbuf.x;
            if (tec ^ up) {
                genFragmentLine(line.toArray(new Point2D.Float[0]));
                line.clear();
                line.add(pbuf);
                up = tec;
            }
            if (tec) {
                line.add(p);
            } else {
                line.addFirst(p);
            }
            pbuf = p;
        }
        genFragmentLine(line.toArray(new Point2D.Float[0]));
    }


    private void genFragmentLine(Point2D.Float[] points) {
        int n = points.length;
        if (n == 2) {
            drawLine(points[0].x, points[0].y, points[1].x, points[1].y);
        } else {
            int half = ends.getHalfWidth();
            int l = (int) ((points[n - 1].x - points[0].x)*sampMils);
            float[] buf = new float[l + 2*half];
            Point2D.Float pbuf = points[0];
            int offset = 0, length = half;
            float f = genVertical(buf, offset, length, pbuf.y, 0.0f);
            for (int i = 1; i < n; i++) {
                Point2D.Float p = points[i];
                offset += length;
                length = (int) ((p.x - pbuf.x)*sampMils);
                f = genLine(buf, offset, length, pbuf.y, p.y, f);
                pbuf = p;
            }
            genVertical(buf, offset + length, half, pbuf.y, f);
            ends.treat(buf);
            loadBuffer(buf, (int) (points[0].x*sampMils) - half);
        }
    }

    private float genVertical(float[] buffer, int n0, int length, float freq, float f0) {
        float m = (float) (2*Math.PI*freq/sampleRate);
        float f = f0;
        for (int i = n0; i < n0 + length; i++) {
            buffer[i] = (float) (gain*Math.sin(f));
            f += m;
        }
        return calRemainder(f);
    }


    private float genLine(float[] buffer, int n1, int length, float v1, float v2, float f0) {
        float f = f0;
        float k = (v2 - v1)/length;
        float df = (float) (2*Math.PI/sampleRate);
        for (int i = 0; i < length; i++) {
            buffer[i + n1] = (float) (gain*Math.sin(f));
            float v = k*i + v1;
            f += df*v;
        }
        return calRemainder(f);
    }

    private void loadBuffer(float[] buffer, int offset) {
        int l = buffer.length;
        for (int i = 0; i < l; i++) {
            int f = i + offset;
            panel.setSample(f, panel.getSample(f) + buffer[i]);
        }
    }

    private float calRemainder(float num) {
        float h = (float) (num/(2*Math.PI));
        return (float) (2*Math.PI*(h - Math.floor(h)));
    }
}
