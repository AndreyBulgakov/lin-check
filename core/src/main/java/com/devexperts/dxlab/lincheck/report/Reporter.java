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

public class Reporter implements Closeable {

    public static final List<String> list = Arrays.asList("TestName", "MaxIteraions", "MaxInvocations", "ThreadConfig",
        "Iterations", "Invocations", "Time", "Result");
    private PrintStream out;

    public Reporter(String filename) throws IOException {
        Path p = Paths.get(filename);
        if (Files.exists(p)) {
            out = new PrintStream(Files.newOutputStream(p, APPEND));
        } else {
            Utils.createMissingDirectories(p);
            out = new PrintStream(Files.newOutputStream(p));
        }
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

    public void report(TestReport report) {
        // TODO print it to out
    }
}
