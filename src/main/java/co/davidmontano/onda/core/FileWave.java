package co.davidmontano.onda.core;

import co.davidmontano.onda.core.exception.BitrateNotSupportedException;
import co.davidmontano.onda.core.exception.CannotReadWaveFileException;
import co.davidmontano.onda.core.exception.NotPCMAudioFormatException;
import co.davidmontano.onda.core.exception.SubchunkNotFoundException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Set;

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
public class FileWave implements Wave, Constants {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String location;
    private final int bytePerSample;
    private final long totalSamples;
    private final double length; // length in seconds
    private final long dataChunkSize;
    private final long totalSize;
    private final Set<Subchunk> subchunks;
    private int audioFormat; // unsigned 2 bytes, little endian
    private int channels; // unsigned 2 bytes, little endian
    private long sampleRate; // unsigned 4 bytes, little endian
    private long byteRate; // unsigned 4 bytes, little endian
    private int blockAlign; // unsigned 2 bytes, little endian
    private int bitsPerSample; // unsigned 2 bytes, little endian

    public FileWave(String location) throws IOException {
        this.location = location;

        File file = new File(location);
        totalSize = FileUtils.sizeOf(file);

        subchunks = new ChunkReader().read(waveInputStream(), totalSize);
//        subchunks = new HashSet<Subchunk>();
//        readSubchunks(waveInputStream());

        Subchunk fmt = findSubchunk(FMT_HEADER_ID);
        byte[] fmtData = IOUtils.toByteArray(fmt.subchunkData(waveInputStream()));
        readFmtHeader(fmtData);

        Subchunk data = findSubchunk(DATA_HEADER_ID);
        dataChunkSize = data.getSubchunkSize();

        length = byteRate > 0 ? (dataChunkSize / byteRate) : 0;
        bytePerSample = bitsPerSample / 8;
        totalSamples = dataChunkSize / bytePerSample;

        checkAcceptableWavProperties();
        logger.debug("FileWave parsed: {}", this);
    }

    private InputStream waveInputStream() {
        try {
            return new FileInputStream(location);
        } catch (FileNotFoundException e) {
            throw new CannotReadWaveFileException(e);
        }
    }

    private Subchunk findSubchunk(String subchunkId) {
        for (Subchunk subchunk : subchunks) {
            if (subchunk.isSubchunk(subchunkId)) {
                return subchunk;
            }
        }
        throw new SubchunkNotFoundException(subchunkId);
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


    private void checkAcceptableWavProperties() {
        if (audioFormat != AUDIO_FORMAT_PCM) {
            throw new NotPCMAudioFormatException("Invalid audio format {" + audioFormat + "}");
        }
        if (bitsPerSample != 8 && bitsPerSample != 16 && bitsPerSample != 32) {
            throw new BitrateNotSupportedException("Bitrate not supported: " + bitsPerSample + ". Use either 8, 16, 32");
        }
    }

    @Override
    public InputStream data() {
        InputStream input = waveInputStream();
        Subchunk dataSubchunk = findSubchunk("data");
        return dataSubchunk.subchunkData(input);
    }

    public long getTotalSamplesForTime(double seconds) {
        return (long) Math.min(totalSamples, sampleRate * seconds);
    }

    @Override
    public long getTotalSamples() {
        return totalSamples;
    }

    @Override
    public int getBytePerSample() {
        return bytePerSample;
    }

    public int getAudioFormat() {
        return audioFormat;
    }

    @Override
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
        return "FileWave{" +
                "audioFormat=" + audioFormat +
                ", channels=" + channels +
                ", sampleRate=" + sampleRate +
                ", byteRate=" + byteRate +
                ", blockAlign=" + blockAlign +
                ", bitsPerSample=" + bitsPerSample +
                ", bytePerSample=" + bytePerSample +
                ", totalSamples=" + totalSamples +
                ", length=" + length +
                ", dataChunkSize=" + dataChunkSize +
                ", totalSize=" + totalSize +
                ", subchunks=" + subchunks +
                '}';
    }

}
