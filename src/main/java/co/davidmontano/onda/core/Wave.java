package co.davidmontano.onda.core;


import java.io.InputStream;

public interface Wave {

    InputStream data();

    int getChannels();

    int getBytePerSample();

    long getTotalSamples();

    Wave trim(double seconds);
}
