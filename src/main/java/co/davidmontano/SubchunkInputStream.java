package co.davidmontano;

import co.davidmontano.exception.CannotReadSubchunkException;
import org.apache.commons.io.IOUtils;

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
public class SubchunkInputStream extends InputStream {

	private final Subchunk subchunk;

	private final InputStream input;

	private long bytesRead;

	// IMPORTANT: InputStream must be at the beginning
	public SubchunkInputStream(Subchunk subchunk, InputStream input) {
		try {
			this.subchunk = subchunk;
			this.input = input;
			IOUtils.skipFully(this.input, subchunk.getOffset());
		} catch (IOException e) {
			throw new CannotReadSubchunkException(e);
		}
	}

	@Override
	public int read() throws IOException {
		if (bytesRead <= subchunk.getSubchunkSize()) {
			int read = input.read();
			if(read >= 0){
				bytesRead++;
				return read;
			} else {
				return read;
			}
		} else {
			return -1;
		}
	}

}
