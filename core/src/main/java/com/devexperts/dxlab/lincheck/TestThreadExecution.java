package com.devexperts.dxlab.lincheck;

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

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.strands.SuspendableCallable;

import java.util.concurrent.Phaser;

/**
 * Instance of this class represents the test execution for ONE thread. Several instances should be ran in parallel.
 * All implementations of this class should be generated via {@link TestThreadExecutionGenerator}.
 *
 * <p> This class should be public for having access from generated classes.
 */
//Callable<Result[]>
public abstract class TestThreadExecution implements SuspendableCallable<Result[]> {

    // The following fields are assigned in TestThreadExecutionGenerator
    protected Phaser phaser;
    protected Object testInstance;
    protected Object[] objArgs;
    protected int[] waits;

    // It is better to return List<Result>,
    // but such implementation requires to have a synthetic
    // method to support generics and the byte-code generation
    // is more out-prone as well. If you need to use
    // List<Result>, see Arrays.asList(..) method.
    public abstract Result[] run() throws SuspendExecution, InterruptedException;
    @Suspendable
    public Result[] call() {
        try {
//            System.out.println(SuspendableHelper.isInstrumented(getClass()));
            return run();
        } catch (SuspendExecution | InterruptedException suspendExecution) {
            suspendExecution.printStackTrace();
            throw new IllegalStateException(suspendExecution);
        }
    }
}