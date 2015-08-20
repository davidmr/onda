package co.davidmontano;


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
public class Sample {

    private final int[] amplitudes;

    public Sample(int[] amplitudes) {
        this.amplitudes = amplitudes;
    }

    public int getAmplitude(int channel) {
        if (channel >= amplitudes.length) {
            throw new IllegalArgumentException("Channel " + channel + " not found");
        }
        return amplitudes[channel];
    }
}