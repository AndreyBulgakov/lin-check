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

import sun.awt.Mutex;

import java.util.ArrayList;

/**
 * Created by alexander on 10.02.17.
 */
public class Vector {

    protected int size;
    protected int length;
    protected ArrayList<Integer> data;
    Mutex mutex;

    public Vector(int length, int[] datas) {
        mutex = new Mutex();
        this.length = length;
        data = new ArrayList<>(length);
        for (int i : datas) {
            data.add(i);
        }
        this.size = datas.length;
    }

    public int getSize() {
        return size;
    }

    public int getLength() {
        return length;
    }

    public int addAll(Vector v) {
        int numElem = v.size + this.size;
        mutex.lock();
        if (numElem > length) {
            this.length = numElem * 2;
        }
        data.addAll(v.data);
        this.size = numElem;
        mutex.unlock();
        return size;
    }
}