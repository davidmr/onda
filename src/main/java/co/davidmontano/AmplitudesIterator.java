package co.davidmontano;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Copyright 2015 David Montaño
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, softwar
 *  distributed under the License is distributed on an "AS IS" BASIS
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied
 *  See the License for the specific language governing permissions an
 *  limitations under the License.
 */
public class AmplitudesIterator implements Iterator<Short> {

    private final InputStream input;
    private final int bytePerSample;
    private final long numSamples;
    private int position;

    protected AmplitudesIterator(InputStream input, int bytesPerSample, long numSamples) {
        this.bytePerSample = bytesPerSample;
        this.numSamples = numSamples;
        this.input = input;
    }

    @Override
    public boolean hasNext() {
        return position < numSamples;
    }

    @Override
    public Short next() {
        if (hasNext()) {
            short amplitude = 0;
            for (int byteNumber = 0; byteNumber < bytePerSample; byteNumber++) {
                // little endian
                byte data = getNextByte(input);
                amplitude |= (short) ((data & 0xFF) << (byteNumber * 8));
            }
            position++;
            return amplitude;
        } else {
            throw new IllegalArgumentException("End of stream reached. Position: " + String.valueOf(position) + " Samples: " + String.valueOf(numSamples));
        }

    }

    public byte getNextByte(InputStream input){
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

    public Short moveToSample(int sample) throws IOException {
        int diff = (sample - position);
        IOUtils.skipFully(input, diff * bytePerSample);
        position = sample;
        return next();
    }
}
