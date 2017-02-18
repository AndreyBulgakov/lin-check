/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2003-2012, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

package java_concurrent_hash_trie_map.collection.concurrent;

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



import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;



abstract class CNodeBase<K, V> extends MainNode<K, V> {
    
    public static final AtomicIntegerFieldUpdater<CNodeBase> updater = AtomicIntegerFieldUpdater.newUpdater(CNodeBase.class, "csize");
    
    public volatile int csize = -1;
    
    public boolean CAS_SIZE(int oldval, int nval) {
    return updater.compareAndSet(this, oldval, nval);
    }
    
    public void WRITE_SIZE(int nval) {
    updater.set(this, nval);
    }
    
    public int READ_SIZE() {
    return updater.get(this);
    }
    
}