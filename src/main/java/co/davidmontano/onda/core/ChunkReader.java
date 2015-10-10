package co.davidmontano.onda.core;

import co.davidmontano.onda.core.exception.NotAudioException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Copyright 2015 David Montaño
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, softwar
 * distributed under the License is distributed on an "AS IS" BASIS
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied
 * See the License for the specific language governing permissions an
 * limitations under the License.
 */
public class ChunkReader implements Constants {

    private final Set<Subchunk> subchunks;

    public ChunkReader() {
        subchunks = new HashSet<Subchunk>();
    }

    public Set<Subchunk> read(InputStream inputStream, long totalSize) throws IOException {
        // 4 bytes of chunkId, 4 bytes of chunkSize and 4 bytes for format
        byte[] chunkBuffer = new byte[12];
        IOUtils.readFully(inputStream, chunkBuffer);
        // read chunk
        int chunkPointer = 0;
        // @formatter:off
        String chunkId = new String(new byte[]{
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
        String format = new String(new byte[]{
                chunkBuffer[chunkPointer++],
                chunkBuffer[chunkPointer++],
                chunkBuffer[chunkPointer++],
                chunkBuffer[chunkPointer++]
        });
        // @formatter:on

        checkChunkAndFormat(chunkId, format);

        parseSubchunk(inputStream, chunkPointer, totalSize);

        return Collections.unmodifiableSet(subchunks);
    }



    private void parseSubchunk(InputStream inputStream, long bytesRead, long totalSize) throws IOException {
        if (bytesRead < totalSize) {
            int subchunkPointer = 0;
            // 4 for subchunkId, 4 for subchunkSize
            byte[] subchunkHeaderBuffer = new byte[8];
            IOUtils.readFully(inputStream, subchunkHeaderBuffer);
            // @formatter:off
            String subChunkId = new String(new byte[]{
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

            parseSubchunk(inputStream, bytesRead, totalSize);
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

    private void checkChunkAndFormat(String chunkId, String format) {
        if (!chunkId.toUpperCase().equals(RIFF_HEADER) || !format.toUpperCase().equals(WAVE_HEADER)) {
            throw new NotAudioException("Header: Unsupported header format. (" + chunkId + "|" + format + ")");
        }
    }

}
