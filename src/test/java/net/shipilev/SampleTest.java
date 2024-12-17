package net.shipilev;

import org.junit.Test;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class SampleTest {

    @Test
    public void runJMH() throws RunnerException {
        PrintWriter pw = new PrintWriter(System.err, true);

        {
            long time1 = System.nanoTime();
            Options opts = new OptionsBuilder()
                    .include("SampleJMHBench")
                    .verbosity(VerboseMode.EXTRA)
                    .jvmArgs("-Xlog:all", "-Xlog:async")
                    .build();
            new Runner(opts).runSingle();
            long time2 = System.nanoTime();
            pw.println("JMH run finished in " + TimeUnit.NANOSECONDS.toMillis(time2 - time1) + " ms");
        }
    }

    @Test
    public void test() throws IOException {
        PrintWriter pw = new PrintWriter(System.err, true);

        {
            long time1 = System.nanoTime();
            Runtime.getRuntime().exec(new String[]{"echo", "1"});
            long time2 = System.nanoTime();
            pw.println("Exec finished in " + TimeUnit.NANOSECONDS.toMillis(time2 - time1) + " ms");
        }

        {
            long time1 = System.nanoTime();
            pw.println(getListenAddress());
            long time2 = System.nanoTime();
            pw.println("Get Listen address finished in " + TimeUnit.NANOSECONDS.toMillis(time2 - time1) + " ms");
        }

        {
            long time1 = System.nanoTime();
            pw.println(InetAddress.getAllByName("127.0.0.1"));
            long time2 = System.nanoTime();
            pw.println("getAllByName finished in " + TimeUnit.NANOSECONDS.toMillis(time2 - time1) + " ms");
        }
    }

    private InetAddress getListenAddress() {
        // Try to use user-provided override first.
        String addr = System.getProperty("jmh.link.address");
        if (addr != null) {
            try {
                return InetAddress.getByName(addr);
            } catch (UnknownHostException e) {
                // override failed, notify user
                throw new IllegalStateException("Can not initialize binary link.", e);
            }
        }

        // Auto-detection should try to use JDK 7+ method first, it is more reliable.
        try {
            Method m = InetAddress.class.getMethod("getLoopbackAddress");
            return (InetAddress) m.invoke(null);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            // shun
        }

        // Otherwise open up the special loopback.
        //   (It can only fail for the obscure reason)
        try {
            return InetAddress.getByAddress(new byte[] {127, 0, 0, 1});
        } catch (UnknownHostException e) {
            // shun
        }

        // Last resort. Open the local host: this resolves
        // the machine name, and not reliable on mis-configured
        // hosts, but there is nothing else we can try.
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new IllegalStateException("Can not find the address to bind to.", e);
        }
    }


}
