package com.devexperts.dxlab.lincheck.report;

/*
 * #%L
 * core
 * %%
 * Copyright (C) 2015 - 2017 Devexperts, LLC
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

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
