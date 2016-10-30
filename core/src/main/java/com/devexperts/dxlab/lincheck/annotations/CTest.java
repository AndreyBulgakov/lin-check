/*
 *  Lincheck - Linearizability checker
 *  Copyright (C) 2015 Devexperts LLC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.devexperts.dxlab.lincheck.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation shows number of iteration, number of threads and number of methods in thread
 * <ul>
 *     <li><b>iterations</b> - number of iteration</li>
 *     <li><b>actorsPerThread</b> - number of elements in brackets shows number of threads</li>
 *     <li><b>actorsPerThread </b> - value in quotes shows range of the number of methods in threads</li>
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(CTest.CTests.class)
public @interface CTest {
    int iterations();

    String[] actorsPerThread();

    /**
     * Holder annotation for {@link CTest}.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @interface CTests {
        CTest[] value();
    }
}
