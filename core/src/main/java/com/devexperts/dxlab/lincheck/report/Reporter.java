package com.devexperts.dxlab.lincheck.report;

import com.devexperts.dxlab.lincheck.Utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static java.nio.file.StandardOpenOption.APPEND;

/**
 * Write result of test to csv File, which contains following columns
 * <ul>
 *     <li>TestName</li>
 *     <li>StrategyName</li>
 *     <li>MaxIterations</li>
 *     <li>MaxInvocations</li>
 *     <li>ThreadConfig</li>
 *     <li>Iterations</li>
 *     <li>Invocations</li>
 *     <li>Time</li>
 *     <li>Result</li>
 * </ul>
 * See {@link TestReport} for describing parameters
 */
public class Reporter implements Closeable {

    // Columns for CSV report
    private static final List<String> columns = Arrays.asList("TestName", "StrategyName", "MaxIterations", "MaxInvocations",
            "ThreadConfig", "Iterations", "Invocations", "Time", "Result");
    private PrintStream out; // null if reports shouldn't be written

    public Reporter(String filename) throws IOException {
        if (filename == null || filename.isEmpty()) // do not write reports
            throw new IllegalArgumentException();
        Path p = Paths.get( System.getProperty("user.dir"), filename);
        if (Files.exists(p)) {
            out = new PrintStream(Files.newOutputStream(p, APPEND));
        } else {
            Utils.createMissingDirectories(p);
            out = new PrintStream(Files.newOutputStream(p));
            String header = columns.toString().replace("[", "").replace("]", "").replace(", ", ",");
            out.println(header);
        }
    }

    @Override
    public void close() throws IOException {
        if (out != null)
            out.close();
    }

    /**
     * Write this report to specified output.
     *
     * @param report report to be written
     */
    public void report(TestReport report) {
        if (out == null)
            return;
        String serializedReport = report.getTestName() + "," + report.getStrategyName() + "," +
                report.getMaxIterations() + "," + report.getMaxInvocations() + ",\"" +
                report.getThreadConfig() + "\"," + report.getIterations() + "," + report.getInvocations() + "," +
                report.getTime() + "," + report.getResult();
        out.println(serializedReport);
    }
}
