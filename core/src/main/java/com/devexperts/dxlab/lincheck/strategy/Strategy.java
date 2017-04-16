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


/**
 * Each strategy should implement this interface and handle shared variable reading and writing.
 */
public interface Strategy {

    /**
     * Execute implemented strategy on read operations
     * @param location location id
     */
    void onSharedVariableRead(int location);

    /**
     * Execute implemented strategy on write operations
     *
     * @param location location id
     */
    void onSharedVariableWrite(int location);


    /**
     * Execute implemented strategy at end of thread
     */
    default void endOfThread() {
    }
}
