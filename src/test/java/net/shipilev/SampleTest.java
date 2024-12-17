package net.shipilev;

import org.junit.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

public class SampleTest {

    @Test
    public void test() throws IOException {
        PrintWriter pw = new PrintWriter(System.err, true);

        long time1 = System.nanoTime();
        Runtime.getRuntime().exec(new String[]{"echo", "1"});
        long time2 = System.nanoTime();


        pw.println("Finished in " + TimeUnit.NANOSECONDS.toMillis(time2 - time1) + " ms");
    }

}
