package co.davidmontano;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
public class Wave {

	private final String filename;

	private final Header header;

	public Wave(String filename) throws IOException {
		this.filename = filename;
		this.header = new Header(filename);
	}

	public SamplesIterator amplitudes() throws IOException {
		InputStream data = header.data(new BufferedInputStream(new FileInputStream(filename)));
		return new SamplesIterator(data, 1, header.getBitsPerSample(), header.getTotalSamples());
	}

	public SamplesIterator amplitudes(double seconds) throws IOException {
		InputStream data = header.data(new BufferedInputStream(new FileInputStream(filename)));
        return new SamplesIterator(data, 1, header.getBitsPerSample(), header.getTotalSamplesForTime(seconds));
	}

	public double getSecondsLength() {
		return header.getLength();
	}

	public int getChannels() {
		return header.getChannels();
	}

	public int getBitsPerSample() {
		return header.getBitsPerSample();
	}

	public int getSampleRate() {
		return header.getSampleRate();
	}

	@Override
	public String toString() {
		return "Wave [filename=" + filename + ", header=" + header + "]";
	}

}
