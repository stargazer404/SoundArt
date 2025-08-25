package audio;

import javax.sound.sampled.AudioFormat;

public abstract class AudioImagePanel {

    private float[] samples;
    private AudioFormat format;

    public AudioImagePanel(int length, AudioFormat format) {
        samples = new float[length];
        this.format = format;
    }

    public int getLength() {
        return samples.length;
    }

    public AudioFormat getAudioFormat() {
        return format;
    }

    public float getSample(int n) {
        return samples[n];
    }

    public void setSample(int n, float sample) {
        samples[n] = sample;
    }

    public abstract byte[] getAudioData();
}
