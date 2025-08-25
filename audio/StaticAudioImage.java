package audio;

import javax.sound.sampled.AudioFormat;

public class StaticAudioImage extends AudioImagePanel{

    public StaticAudioImage(float duration, float sampleRate) {
        super((int) (duration*sampleRate/1000.0f), new AudioFormat(sampleRate, 16, 1, true, false));
    }
    @Override
    public byte[] getAudioData() {
        int n = super.getLength();
        byte[] data = new byte[2*n];
        for (int i = 0; i < n; i++) {
            int p = 2*i;
            short samp = (short) super.getSample(i);
            data[p] = (byte) samp;
            data[p + 1] = (byte) (samp >> 8);
        }
        return data;
    }
}
