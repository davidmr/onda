package co.davidmontano.onda.image;

import co.davidmontano.onda.core.Sample;
import co.davidmontano.onda.core.SamplesIterator;
import co.davidmontano.onda.core.Wave;
import org.apache.commons.math3.stat.StatUtils;

import java.awt.image.BufferedImage;

public class DrawWave {

    private static final int DEFAULT_SAMPLE_PER_POINT = 200;

    private final Wave wave;

    public DrawWave(Wave wave) {
        this.wave = wave;
    }

    public BufferedImage[] draw(int width, int height) {
        SamplesIterator samples = new SamplesIterator(wave);

        int samplePerPoint = DEFAULT_SAMPLE_PER_POINT;
        if (wave.getTotalSamples() < width * samplePerPoint) {
            samplePerPoint = 1;
        }

        float blocksOffset = wave.getTotalSamples() / (width * samplePerPoint);

        int currentAmplitude = 0;
        double[][] amplitudesPerPoint = new double[wave.getChannels()][samplePerPoint];

        for (float i = 0; i < wave.getTotalSamples(); i += blocksOffset) {
            int amplitudePos = (int) Math.floor(i);
            Sample sample = samples.moveToSample(amplitudePos);
            for (int channel = 0; channel < wave.getChannels(); channel++) {
                amplitudesPerPoint[channel][currentAmplitude] = sample.getAmplitude(channel);
            }
            if (currentAmplitude >= samplePerPoint) {
                for (int channel = 0; channel < wave.getChannels(); channel++) {
                    double median = StatUtils.percentile(amplitudesPerPoint[channel], 50);
                    System.out.println(Math.round(median * height));
                }
                currentAmplitude = 0;
            }
            currentAmplitude++;
        }

        return null;

    }
}
