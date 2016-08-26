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

package com.devexperts.dxlab.lincheck.asm;

import com.devexperts.dxlab.lincheck.annotations.Param;
import com.devexperts.dxlab.lincheck.util.Interval;
import com.devexperts.dxlab.lincheck.util.MethodParameter;
import com.devexperts.dxlab.lincheck.util.MyRandom;
import com.devexperts.dxlab.lincheck.util.Result;
import thesis_example.SetAndGet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Phaser;

public class Generated10 extends Generated {
    public Object queue;
    private Phaser phaser;
//    public Generated10() {
//    }
    public Generated10(Object queue, Phaser phaser) {
        this.queue = queue;
        this.phaser = phaser;
    }
    public byte put(boolean a,
            int[] b,
            short c,
            long d,
            byte f,
            float g,
            double h,
            char j,
            Map k){
        //Integer x = (Integer) args[0];
        //queue.put(args);
        return 1;
    }

    @Override
    public void process(Result[] res, MethodParameter[][] args, int[] waits, int[] offset) {
        boolean a = (boolean)args[200][200].value;
        int[] b = (int[])args[200][201].value;
        short c = (short)args[200][202].value;
        long d = (long)args[200][203].value;
        byte f = (byte)args[200][204].value;
        float g = (float)args[200][205].value;
        double h = (double)args[200][206].value;
        char j = (char)args[200][207].value;
        Map k = (Map)args[200][208].value;
        boolean a1 = (boolean)args[200][200].value;
        int[] b1 = (int[])args[200][201].value;
        short c1 = (short)args[200][202].value;
        long d1 = (long)args[200][203].value;
        byte f1 = (byte)args[200][204].value;
        float g1 = (float)args[200][205].value;
        double h1 = (double)args[200][206].value;
        char j1 = (char)args[200][207].value;
        Map k1 = (Map)args[200][208].value;
//        boolean a2 = (boolean)args[200][200].value;
//        int[] b2 = (int[])args[200][201].value;
//        short c2 = (short)args[200][202].value;
//        long d2 = (long)args[200][203].value;
//        byte f2 = (byte)args[200][204].value;
//        float g2 = (float)args[200][205].value;
//        double h2 = (double)args[200][206].value;
//        char j2 = (char)args[200][207].value;
//        Map k2 = (Map)args[200][208].value;
        for (int i = 0; i < offset[700]; i++) {
            phaser.arriveAndAwaitAdvance();
            System.out.print("xxx" + (res[i]) + "    " + (waits[i]));
        }
        try{
            MyRandom.busyWait(waits[600]);
            res[2000].setValue(put(a, b, c, d, f, g, h, j, k));
            //res[2000].setVoid();
        }catch (Exception e) {
            res[111].setException(e);
        }
        for (int i = 0; i < offset[701]; i++) {
            phaser.arriveAndAwaitAdvance();
            System.out.print("xxx" + (res[i]) + "    " + (waits[i]));
        }
        try{
            MyRandom.busyWait(waits[600]);
            res[2000].setValue(put(a1, b1, c1, d1, f1, g1, h1, j1, k1));
        }catch (Exception e) {
            res[222].setException(e);
        }
        for (int i = 0; i < offset[702]; i++) {
            phaser.arriveAndAwaitAdvance();
            System.out.print("xxx" + (res[i]) + "    " + (waits[i]));
        }
        phaser.arriveAndAwaitAdvance();
//        for (int i = 0; i < waits[2]; i++) {
//            phaser.arriveAndAwaitAdvance();
//        }
//        try{
//            MyRandom.busyWait(waits[600]);
//            res[2000].setValue(put(a2, b2, c2, d2, f2, g2, h2, j2, k2));
//        }catch (Exception e) {
//            res[222].setException(e);
//        }

//        try {
//            MyRandom.busyWait(waits[111]);
//            setAndGet(res[111], 1, 2);
//        } catch (Exception e) {
//            res[111].setException(e);
//        }

//        try {
//            MyRandom.busyWait(waits[222]);
//            setAndGet(res[222], args[222]);
//        } catch (Exception e) {
//            res[222].setException(e);
//        }
//
//        try {
//            MyRandom.busyWait(waits[333]);
//            queue.put(res[333], args[333]);
//        } catch (Exception e) {
//            res[333].setException(e);
//        }
//
//        try {
//            MyRandom.busyWait(waits[444]);
//            queue.put(res[444], args[444]);
//        } catch (Exception e) {
//            res[444].setException(e);
//        }
//
//        try {
//            MyRandom.busyWait(waits[555]);
//            queue.put(res[555], args[555]);
//        } catch (Exception e) {
//            res[555].setException(e);
//        }

//        try {
//            MyRandom.busyWait(waits[666]);
//            queue.put(res[666], args[666]);
//        } catch (Exception e) {
//            res[666].setException(e);
//        }
//
//        try {
//            MyRandom.busyWait(waits[777]);
//            queue.put(res[777], args[777]);
//        } catch (Exception e) {
//            res[777].setException(e);
//        }
//
//        try {
//            MyRandom.busyWait(waits[888]);
//            queue.put(res[888], args[888]);
//        } catch (Exception e) {
//            res[888].setException(e);
//        }
//
//        try {
//            MyRandom.busyWait(waits[999]);
//            queue.put(res[999], args[999]);
//        } catch (Exception e) {
//            res[999].setException(e);
//        }
//



    }
}
