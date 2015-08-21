package co.davidmontano;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SubchunkInputStreamTest {

    @Test
    public void shouldReturnAWindowOfOriginalInput() throws IOException {
        byte[] data = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0};

        SubchunkInputStream input = new SubchunkInputStream(new Subchunk("id", 2, 5), new ByteArrayInputStream(data));
        byte[] result = new byte[5];
        int bytesRead = input.read(result);

        byte[] expected = {3, 4, 5, 6, 7};
        assertThat(bytesRead, is(5));
        assertTrue(Arrays.equals(result, expected));
    }

}
