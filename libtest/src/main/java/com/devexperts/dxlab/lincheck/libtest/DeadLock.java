package com.devexperts.dxlab.lincheck.libtest;

/*
 * #%L
 * libtest
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
 * Created by alexander on 09.02.17.
 */
public class DeadLock {
    static Object lock1;
    static Object lock2;
    static int x;

    public DeadLock(){
        lock1 = new Object();
        lock2 = new Object();
        x = 0;
    }

    public int parent() {
        synchronized(lock1) {
            synchronized(lock2) {
                return x++;
            }
        }
    }

    public int child()
    {
        synchronized(lock2) {
            synchronized(lock1) {
                return x++;
            }
        }
    }

    public int inc(){
        synchronized (this) {
            return x++;
        }
    }

    public int dec(){
        synchronized (this) {
            return x--;
        }
    }

    public int getX(){
        return x;
    }

}
