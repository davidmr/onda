package co.davidmontano.onda.core;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Copyright 2015 David Montaño
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, softwar
 * distributed under the License is distributed on an "AS IS" BASIS
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied
 * See the License for the specific language governing permissions an
 * limitations under the License.
 */
public class SamplesIterator implements Iterator<Sample> {

    private final InputStream input;
    private final int bytePerSample;
    private int channels;
    private int bitsPerSample;
    private final long numSamples;
    private int position;

    public SamplesIterator(Wave wave) {
        this.input = wave.data();
        this.channels = wave.getChannels();
        this.bytePerSample = wave.getBytePerSample();
        this.numSamples = wave.getTotalSamples();
    }

    @Override
    public boolean hasNext() {
        return position < numSamples;
    }

    @Override
    public Sample next() {
        if (hasNext()) {
            Sample sample = getNextSample();
            sampleRead();
            return sample;
        } else {
            throw new IllegalArgumentException("End of stream reached. Position: " + position + " Samples: " + numSamples);
        }

    }

    private void sampleRead() {
        position++;
    }

    private Sample getNextSample() {
        int[] amplitudes = new int[channels];
        for (int i = 0; i < channels; i++) {
            amplitudes[i] = getNextAmplitude();
        }
        return new Sample(amplitudes);
    }


    private int getNextAmplitude() {
        int amplitude = 0;
        for (int byteNumber = 0; byteNumber < bytePerSample; byteNumber++) {
            // little endian
            byte data = getNextByte(input);
            amplitude |= (int) ((data & 0xFF) << (byteNumber * 8));
        }
        return amplitude;
    }

    public byte getNextByte(InputStream input) {
        try {
            return (byte) input.read();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public Sample moveToSample(int sample) throws IOException {
        int diff = (sample - position);
        IOUtils.skipFully(input, diff * bytePerSample * channels);
        position = sample;
        return next();
    }
}
