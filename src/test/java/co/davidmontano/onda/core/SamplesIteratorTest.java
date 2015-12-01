package co.davidmontano.onda.core;


import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

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
public class SamplesIteratorTest {

    private static final double DELTA = 0.000000001;

    private static final double MAX_AMPLITUDE_16BITS = 65535;

    private static final double MAX_AMPLITUDE_32BITS = 4294967295L;

    @Test
    public void shouldIterateSamples_1Channel_16bits() {

        byte[] inputArray = {
                0, (byte) 255, //sample 1: 65280
                (byte) 255, 0, //sample 2: 255
                (byte) 255, (byte) 255, //sample 3: 65535
                0, 0, //sample 4: 0
                10, 10 //sample 5: 2570
        };
        SamplesIterator iterator = new SamplesIterator(new TestWave(inputArray, 1, 2, 5));

        Sample sample = null;

        sample = iterator.next();
        assertThat(sample.getAmplitude(0), closeTo(65280 / MAX_AMPLITUDE_16BITS, DELTA));

        sample = iterator.next();
        assertThat(sample.getAmplitude(0), closeTo(255 / MAX_AMPLITUDE_16BITS, DELTA));

        sample = iterator.next();
        assertThat(sample.getAmplitude(0), closeTo(1, DELTA));

        sample = iterator.next();
        assertThat(sample.getAmplitude(0), closeTo(0, DELTA));

        sample = iterator.next();
        assertThat(sample.getAmplitude(0), closeTo(2570 / MAX_AMPLITUDE_16BITS, DELTA));
    }

    @Test
    public void shouldIterateSamples_1Channel_32bits() {
        byte[] inputArray = {
                (byte) 255, 0, 0, 0, //sample 1: 255
                100, (byte) 255, 0, 0, //sample 2: 65380
                0, 3, 0, 1, //sample 3: 0, 3, 0, 1 becomes 3 << 8 & 1 << 24 = 1 0000 0000 0000 0011 0000 0000 = 16777984
                (byte) 255, (byte) 255, (byte) 255, (byte) 255, //sample 4: 4294967295L
                0, 0, 0, 0 //sample 5: 0
        };
        InputStream input = new ByteArrayInputStream(inputArray);
        SamplesIterator iterator = new SamplesIterator(new TestWave(inputArray, 1, 4, 5));

        Sample sample = null;

        sample = iterator.next();
        assertThat(sample.getAmplitude(0), closeTo(255 / MAX_AMPLITUDE_32BITS, DELTA));

        sample = iterator.next();
        assertThat(sample.getAmplitude(0), closeTo(65380 / MAX_AMPLITUDE_32BITS, DELTA));

        sample = iterator.next();
        assertThat(sample.getAmplitude(0), closeTo(16777984 / MAX_AMPLITUDE_32BITS, DELTA));

        sample = iterator.next();
        assertThat(sample.getAmplitude(0), closeTo(1, DELTA));

        sample = iterator.next();
        assertThat(sample.getAmplitude(0), closeTo(0, DELTA));
    }

    @Test
    public void shouldIterateSamples_2Channel_16bits() {
        byte[] inputArray = {
                0, (byte) 255, (byte) 255, 0, //sample 1: {65280, 255}
                0, 10, 0, 20, //sample 2: {2560, 5120}
                5, 0, 6, 0, //sample 3: {5, 6}
                0, 7, 0, 8 //sample 4: {1792, 2048}
        };
        InputStream input = new ByteArrayInputStream(inputArray);
        SamplesIterator iterator = new SamplesIterator(new TestWave(inputArray, 2, 2, 5));

        Sample sample = null;

        sample = iterator.next();
        assertThat(sample.getAmplitude(0), closeTo(65280 / MAX_AMPLITUDE_16BITS, DELTA));
        assertThat(sample.getAmplitude(1), closeTo(255 / MAX_AMPLITUDE_16BITS, DELTA));

        sample = iterator.next();
        assertThat(sample.getAmplitude(0), closeTo(2560 / MAX_AMPLITUDE_16BITS, DELTA));
        assertThat(sample.getAmplitude(1), closeTo(5120 / MAX_AMPLITUDE_16BITS, DELTA));

        sample = iterator.next();
        assertThat(sample.getAmplitude(0), closeTo(5 / MAX_AMPLITUDE_16BITS, DELTA));
        assertThat(sample.getAmplitude(1), closeTo(6 / MAX_AMPLITUDE_16BITS, DELTA));

        sample = iterator.next();
        assertThat(sample.getAmplitude(0), closeTo(1792 / MAX_AMPLITUDE_16BITS, DELTA));
        assertThat(sample.getAmplitude(1), closeTo(2048 / MAX_AMPLITUDE_16BITS, DELTA));

    }

}
