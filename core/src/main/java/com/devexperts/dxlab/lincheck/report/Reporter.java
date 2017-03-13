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

// TODO javadoc (purpose, output format)
public class Reporter implements Closeable {

    // Columns for CSV report
    public static final List<String> columns = Arrays.asList("TestName", "StrategyName", "MaxIterations", "MaxInvocations",
            "ThreadConfig", "Iterations", "Invocations", "Time", "Result");
    private PrintStream out; // null if reports shouldn't be written

    public Reporter(String filename) throws IOException {
        if (filename == null) // do not write reports
            return;
        Path p = Paths.get(filename);
        if (Files.exists(p)) {
            out = new PrintStream(Files.newOutputStream(p, APPEND));
        } else {
            Utils.createMissingDirectories(p);
            out = new PrintStream(Files.newOutputStream(p));
            out.println(columns);
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
        // TODO Do not use toString method for printing report
        out.println(report);
    }
}
