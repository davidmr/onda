package co.davidmontano.onda.core;


import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class TestWave implements Wave {

    private final byte[] bytes;

    private final int channels;

    private final int bytePerSample;

    private final long totalSamples;

    public TestWave(byte[] bytes, int channels, int bytePerSample, long totalSamples) {
        this.bytes = bytes;
        this.channels = channels;
        this.bytePerSample = bytePerSample;
        this.totalSamples = totalSamples;
    }

    @Override
    public InputStream data() {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public int getChannels() {
        return channels;
    }

    @Override
    public int getBytePerSample() {
        return bytePerSample;
    }

    @Override
    public long getTotalSamples() {
        return totalSamples;
    }

}
