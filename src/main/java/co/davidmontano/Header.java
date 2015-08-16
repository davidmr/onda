package co.davidmontano;

import co.davidmontano.exception.BitrateNotSupportedException;
import co.davidmontano.exception.NotAudioException;
import co.davidmontano.exception.NotPCMAudioFormatException;
import co.davidmontano.exception.SubchunkNotFoundException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

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
public class Header {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String RIFF_HEADER = "RIFF";
	private static final String WAVE_HEADER = "WAVE";
	private static final String FMT_HEADER_ID = "fmt ";
	private static final String DATA_HEADER_ID = "data";
	private static final int AUDIO_FORMAT_PCM = 1;

	private int audioFormat; // unsigned 2 bytes, little endian
	private int channels; // unsigned 2 bytes, little endian
	private long sampleRate; // unsigned 4 bytes, little endian
	private long byteRate; // unsigned 4 bytes, little endian
	private int blockAlign; // unsigned 2 bytes, little endian
	private int bitsPerSample; // unsigned 2 bytes, little endian
	private final int bytePerSample;
	private final long totalSamples;
	private final double length; // length in seconds
	private final long dataChunkSize;
	private final long totalSize;
	private final Set<Subchunk> subchunks;

	public Header(String location) throws IOException {
		File file = new File(location);
		totalSize = FileUtils.sizeOf(file);

		subchunks = new HashSet<Subchunk>();
		readSubchunks(new FileInputStream(file));

		Subchunk fmt = findSubchunk(FMT_HEADER_ID);
		byte[] fmtData = IOUtils.toByteArray(fmt.subchunkData(new FileInputStream(file)));
		readFmtHeader(fmtData);

		Subchunk data = findSubchunk(DATA_HEADER_ID);
		dataChunkSize = data.getSubchunkSize();

		length = byteRate > 0 ? (dataChunkSize / byteRate) : 0;
		bytePerSample = bitsPerSample / 8;
		totalSamples = dataChunkSize / bytePerSample;

		checkAcceptableWavProperties();
		logger.debug("Header parsed: {}", this);
	}

	// IMPORTANT: InputStream must be at the beginning
	private void readSubchunks(InputStream inputStream) throws IOException {

		// 4 bytes of chunkId, 4 bytes of chunkSize and 4 bytes for format
		byte[] chunkBuffer = new byte[12];
		IOUtils.readFully(inputStream, chunkBuffer);
		// read chunk
		int chunkPointer = 0;
		// @formatter:off
		String chunkId = new String(new byte[] { 
				chunkBuffer[chunkPointer++], 
				chunkBuffer[chunkPointer++], 
				chunkBuffer[chunkPointer++], 
				chunkBuffer[chunkPointer++]
		});
		// little endian
		long chunkSize = 
				(long) (chunkBuffer[chunkPointer++] & 0xff) | 
				(long) (chunkBuffer[chunkPointer++] & 0xff) << 8 | 
				(long) (chunkBuffer[chunkPointer++] & 0xff) << 16 | 
				(long) (chunkBuffer[chunkPointer++] & 0xff << 24);
		String format = new String(new byte[] { 
				chunkBuffer[chunkPointer++], 
				chunkBuffer[chunkPointer++], 
				chunkBuffer[chunkPointer++], 
				chunkBuffer[chunkPointer++]
		});
		// @formatter:on
		checkChunkAndFormat(chunkId, format);

		parseSubchunk(inputStream, chunkPointer);
	}

	private Subchunk findSubchunk(String subchunkId) {
		for (Subchunk subchunk : subchunks) {
			if (subchunk.isSubchunk(subchunkId)) {
				return subchunk;
			}
		}
		throw new SubchunkNotFoundException(subchunkId);
	}

	private void parseSubchunk(InputStream inputStream, long bytesRead) throws IOException {
		if (bytesRead < totalSize) {
			int subchunkPointer = 0;
			// 4 for subchunkId, 4 for subchunkSize
			byte[] subchunkHeaderBuffer = new byte[8];
			IOUtils.readFully(inputStream, subchunkHeaderBuffer);
			// @formatter:off
			String subChunkId = new String(new byte[] {
					subchunkHeaderBuffer[subchunkPointer++],
					subchunkHeaderBuffer[subchunkPointer++],
					subchunkHeaderBuffer[subchunkPointer++],
					subchunkHeaderBuffer[subchunkPointer++]
			});
			long subChunkSize = 
					(long) (subchunkHeaderBuffer[subchunkPointer++] & 0xff) |
					(long) (subchunkHeaderBuffer[subchunkPointer++] & 0xff) << 8 |
					(long) (subchunkHeaderBuffer[subchunkPointer++] & 0xff) << 16 |
					(long) (subchunkHeaderBuffer[subchunkPointer++] & 0xff) << 24;
			// @formatter:on
			bytesRead += subchunkPointer;

			// Avoid moving beyond the end of the file
			// subChunkSize might be corrupt (bigger than size of the actual file)
			subChunkSize = Math.min(totalSize - bytesRead, subChunkSize);

			subchunks.add(new Subchunk(subChunkId, bytesRead, subChunkSize));

			IOUtils.skipFully(inputStream, subChunkSize);
			bytesRead += subChunkSize;
			bytesRead += skipPadding(inputStream, subChunkSize);

			parseSubchunk(inputStream, bytesRead);
		}
	}

	private int skipPadding(InputStream input, long subChunkSize) throws IOException {
		if (subChunkSize % 2 != 0) {
			IOUtils.skipFully(input, 1);
			return 1;
		} else {
			return 0;
		}
	}

	private void readFmtHeader(byte[] headerBuffer) {
		int pointer = 0;
		audioFormat = (int) ((headerBuffer[pointer++] & 0xff) | (headerBuffer[pointer++] & 0xff) << 8);
		channels = (int) ((headerBuffer[pointer++] & 0xff) | (headerBuffer[pointer++] & 0xff) << 8);
		sampleRate = (long) (headerBuffer[pointer++] & 0xff) | (long) (headerBuffer[pointer++] & 0xff) << 8 | (long) (headerBuffer[pointer++] & 0xff) << 16
				| (long) (headerBuffer[pointer++] & 0xff) << 24;
		byteRate = (long) (headerBuffer[pointer++] & 0xff) | (long) (headerBuffer[pointer++] & 0xff) << 8 | (long) (headerBuffer[pointer++] & 0xff) << 16
				| (long) (headerBuffer[pointer++] & 0xff) << 24;
		blockAlign = (int) ((headerBuffer[pointer++] & 0xff) | (headerBuffer[pointer++] & 0xff) << 8);
		bitsPerSample = (int) ((headerBuffer[pointer++] & 0xff) | (headerBuffer[pointer++] & 0xff) << 8);
	}

	private void checkChunkAndFormat(String chunkId, String format) {
		if (!chunkId.toUpperCase().equals(RIFF_HEADER) || !format.toUpperCase().equals(WAVE_HEADER)) {
			throw new NotAudioException("Header: Unsupported header format. (" + chunkId + "|" + format + ")");
		}
	}

	private void checkAcceptableWavProperties() {
		if (audioFormat != AUDIO_FORMAT_PCM) {
			throw new NotPCMAudioFormatException("Invalid audio format {" + audioFormat + "}");
		}
		if (bitsPerSample != 8 && bitsPerSample != 16) {
			throw new BitrateNotSupportedException("Bitrate not supported: " + bitsPerSample + ". Use either 8 o 16");
		}
	}

	public InputStream data(InputStream input) throws IOException {
		Subchunk dataSubchunk = findSubchunk("data");
		return dataSubchunk.subchunkData(input);
	}

	public long getTotalSamplesForTime(double seconds) {
		return (long) Math.min(totalSamples, sampleRate * seconds);
	}

	public long getTotalSamples() {
		return totalSamples;
	}

	public int getBytePerSample() {
		return bytePerSample;
	}

	public int getAudioFormat() {
		return audioFormat;
	}

	public int getChannels() {
		return channels;
	}

	public int getSampleRate() {
		return (int) sampleRate;
	}

	public int getByteRate() {
		return (int) byteRate;
	}

	public int getBlockAlign() {
		return blockAlign;
	}

	public int getBitsPerSample() {
		return bitsPerSample;
	}

	public double getLength() {
		return length;
	}

	@Override
	public String toString() {
		return "Header [audioFormat=" + audioFormat + ", channels=" + channels + ", sampleRate=" + sampleRate + ", byteRate=" + byteRate + ", blockAlign=" + blockAlign + ", bitsPerSample="
				+ bitsPerSample + ", bytePerSample=" + bytePerSample + ", totalSamples=" + totalSamples + ", length=" + length + ", dataChunkSize=" + dataChunkSize + ", totalSize=" + totalSize
				+ ", subchunks=" + subchunks + "]";
	}

}