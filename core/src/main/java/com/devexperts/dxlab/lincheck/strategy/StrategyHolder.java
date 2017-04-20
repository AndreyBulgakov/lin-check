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
import com.devexperts.dxlab.lincheck.LinCheckThread;
import com.devexperts.dxlab.lincheck.Result;

import java.util.ArrayList;

/**
 * Current strategy container
 */
public abstract class StrategyHolder {
    private static Strategy currentStrategy;
    public static ArrayList<LinCheckThread> threads = new ArrayList<>();

    public static void setCurrentStrategy(Strategy curentStrategy) {
        StrategyHolder.currentStrategy = curentStrategy;
    }

    public final static ArrayList<Fiber<Result[]>> fibers = new ArrayList<>();

    @SuppressWarnings("unused") // invoked from transformed code
    public static Strategy getCurrentStrategy() {
        return currentStrategy;
    }

}
