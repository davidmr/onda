package co.davidmontano.onda.core;

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
public class Subchunk {

	private final String subchunkId;

	private final long offset;

	private final long subchunkSize;

	public Subchunk(String subchunkId, long offset, long subchunkSize) {
		this.subchunkId = subchunkId;
		this.offset = offset;
		this.subchunkSize = subchunkSize;
	}

	public String getSubchunkId() {
		return subchunkId;
	}

	public long getOffset() {
		return offset;
	}

	public long getSubchunkSize() {
		return subchunkSize;
	}

	public boolean isSubchunk(String id) {
		return subchunkId.equals(id);
	}

	// IMPORTANT: InputStream must be at the beginning
	public SubchunkInputStream subchunkData(InputStream input) {
		return new SubchunkInputStream(this, input);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((subchunkId == null) ? 0 : subchunkId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Subchunk other = (Subchunk) obj;
		if (subchunkId == null) {
			if (other.subchunkId != null)
				return false;
		} else if (!subchunkId.equals(other.subchunkId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Subchunk [subchunkId=" + subchunkId + ", offset=" + offset + ", subchunkSize=" + subchunkSize + "]";
	}

}
