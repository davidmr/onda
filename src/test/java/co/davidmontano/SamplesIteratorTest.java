package co.davidmontano;


import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
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

    @Test
    public void shouldIterateSamples_1Channel_16bits() {
        byte[] inputArray = {
                1, 0, //sample 1
                2, 0, //sample 2
                0, 3, //sample 3
                0, 4, //sample 4
                5, 0 //sample 5
        };
        InputStream input = new ByteArrayInputStream(inputArray);
        SamplesIterator iterator = new SamplesIterator(input, 1, 16, 5);

        Sample sample = null;

        sample = iterator.next();
        assertThat(sample.getAmplitude(0), is(1));

        sample = iterator.next();
        assertThat(sample.getAmplitude(0), is(2));

        sample = iterator.next();
        assertThat(sample.getAmplitude(0), is(768));

        sample = iterator.next();
        assertThat(sample.getAmplitude(0), is(1024));

        sample = iterator.next();
        assertThat(sample.getAmplitude(0), is(5));
    }

    @Test
    public void shouldIterateSamples_1Channel_32bits() {
        byte[] inputArray = {
                1, 0, 0, 0, //sample 1
                2, 0, 0, 0, //sample 2
                0, 3, 0, 1, //sample 3
                0, 4, 0, 1, //sample 4
                5, 0, 0, 0 //sample 5
        };
        InputStream input = new ByteArrayInputStream(inputArray);
        SamplesIterator iterator = new SamplesIterator(input, 1, 32, 5);

        Sample sample = null;

        sample = iterator.next();
        assertThat(sample.getAmplitude(0), is(1));

        sample = iterator.next();
        assertThat(sample.getAmplitude(0), is(2));

        sample = iterator.next();
        assertThat(sample.getAmplitude(0), is(16777984)); // 0, 3, 0, 1 becomes 3 << 8 & 1 << 24 = 1 0000 0000 0000 0011 0000 0000 = 16777984

        sample = iterator.next();
        assertThat(sample.getAmplitude(0), is(16778240));

        sample = iterator.next();
        assertThat(sample.getAmplitude(0), is(5));
    }

    @Test
    public void shouldIterateSamples_2Channel_16bits() {
        byte[] inputArray = {
                1, 0, 2, 0, //sample 1
                0, 3, 0, 4, //sample 2
                5, 0, 6, 0, //sample 3
                0, 7, 0, 8, //sample 4
                9, 10, 11, 12 // sample 5
        };
        InputStream input = new ByteArrayInputStream(inputArray);
        SamplesIterator iterator = new SamplesIterator(input, 2, 16, 5);

        Sample sample = null;

        sample = iterator.next();
        assertThat(sample.getAmplitude(0), is(1));
        assertThat(sample.getAmplitude(1), is(2));

        sample = iterator.next();
        assertThat(sample.getAmplitude(0), is(768)); // 0, 3 becomes 3 << 8 = 11 0000 0000 = 768
        assertThat(sample.getAmplitude(1), is(1024));

        sample = iterator.next();
        assertThat(sample.getAmplitude(0), is(5));
        assertThat(sample.getAmplitude(1), is(6));

        sample = iterator.next();
        assertThat(sample.getAmplitude(0), is(1792));
        assertThat(sample.getAmplitude(1), is(2048));

        sample = iterator.next();
        assertThat(sample.getAmplitude(0), is(2569));
        assertThat(sample.getAmplitude(1), is(3083));
    }

}
