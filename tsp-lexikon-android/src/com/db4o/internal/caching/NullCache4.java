/* This file is part of the db4o object database http://www.db4o.com

Copyright (C) 2004 - 2011  Versant Corporation http://www.versant.com

db4o is free software; you can redistribute it and/or modify it under
the terms of version 3 of the GNU General Public License as published
by the Free Software Foundation.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program.  If not, see http://www.gnu.org/licenses/. */
package com.db4o.internal.caching;

import java.util.*;

import com.db4o.foundation.*;

/**
 * @exclude
 */
public class NullCache4 <K,V> implements Cache4<K, V>{

	@Override
	public V produce(K key, Function4<K, V> producer, Procedure4<V> onDiscard) {
		return producer.apply(key);
	}

	@Override
	public Iterator<V> iterator() {
		return Iterators.platformIterator(Iterators.EMPTY_ITERATOR);
	}
	
}
