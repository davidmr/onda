package co.davidmontano.onda.core;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;

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
public class FileWaveTest {

    @Test
    public void shouldLoadCanonicalHeaderFor_1Channel_441khz_16bps() throws IOException {
        FileWave wave = new FileWave("src/test/resources/1channel_441khz_16bps.wav");
        assertThat(wave.getChannels(), is(1));
        assertThat(wave.getSampleRate(), is(44100));
        assertThat(wave.getBitsPerSample(), is(16));
    }

    @Test
    public void shouldLoadCanonicalHeaderFor_2Channel_441khz_16bps() throws IOException {
        FileWave wave = new FileWave("src/test/resources/2channel_441khz_16bps.wav");
        assertThat(wave.getChannels(), is(2));
        assertThat(wave.getSampleRate(), is(44100));
        assertThat(wave.getBitsPerSample(), is(16));
    }

    @Test
    public void shouldTrimWhenDurationLongerThanRequested() throws IOException {
        FileWave trimmed = new FileWave("src/test/resources/1channel_441khz_16bps.wav").trim(0.1);
        int bytesRead = IOUtils.read(trimmed.data(), new byte[4410]);
        assertThat(trimmed.getChannels(), is(1));
        assertThat(trimmed.getSampleRate(), is(44100));
        assertThat(trimmed.getBitsPerSample(), is(16));
        assertThat(trimmed.getLength(), closeTo(0.1, 0.001));
        assertThat(bytesRead, is(4410));
    }
}
