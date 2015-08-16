package co.davidmontano;


import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

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
public class AmplitudesIteatorTest {

    @Test
    public void shouldIterateAllSamples() throws IOException {
        Wave wave = new Wave("src/test/resources/1channel_441khz_16bps.wav");
        AmplitudesIterator amplitudes = wave.amplitudes();
        long counter = 0;
        while (amplitudes.hasNext()) {
            Short next =  amplitudes.next();
            counter++;
        }
        assertThat(counter, is(44267L));
    }

    @Test
    public void shouldIterateSampleCount() throws IOException {
        Wave wave = new Wave("src/test/resources/1channel_441khz_16bps.wav");
        AmplitudesIterator amplitudes = wave.amplitudes(0.2);
        long counter = 0;
        while (amplitudes.hasNext()) {
            Short next =  amplitudes.next();
            counter++;
        }
        assertThat(counter, is(8820L));
    }
}
