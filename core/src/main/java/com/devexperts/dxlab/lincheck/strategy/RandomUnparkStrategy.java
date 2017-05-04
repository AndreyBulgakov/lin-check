package com.devexperts.dxlab.lincheck.strategy;

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

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.Suspendable;
import com.devexperts.dxlab.lincheck.ExecutionsStrandPool;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardOpenOption.APPEND;


public class RandomUnparkStrategy implements Strategy {
    public static int bug;
    volatile PrintStream logger = getLogger();
    ExecutionsStrandPool pool;

    public RandomUnparkStrategy(ExecutionsStrandPool pool) {
        this.pool = pool;
    }

    public RandomUnparkStrategy(Driver driver) {

    }

    @Suspendable
    @Override
    public void startOfThread() {
    }

    @Suspendable
    @Override
    public void onSharedVariableRead(int location) {
        onSharedVariableAccess(location);
    }

    @Suspendable
    @Override
    public void onSharedVariableWrite(int location) {
        onSharedVariableAccess(location);
    }

    @Suspendable
    private void onSharedVariableAccess(int location) {
        try {
            if (Fiber.isCurrentFiber()) {
                Fiber.park(100, TimeUnit.NANOSECONDS);
            }
            bug = 0;
        } catch (Exception suspendExecution) {
            throw new AssertionError(suspendExecution);
        }
    }

    @Suspendable
    @Override
    public void endOfThread() {
        if (Fiber.isCurrentFiber()) {
        }
        bug = 0;
    }

    public static PrintStream getLogger() {
        try {
            Path p = Paths.get(System.getProperty("user.dir"), "log");
            if (Files.exists(p)) {
                return new PrintStream(Files.newOutputStream(p, APPEND));
            } else {
                return new PrintStream(Files.newOutputStream(p));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
