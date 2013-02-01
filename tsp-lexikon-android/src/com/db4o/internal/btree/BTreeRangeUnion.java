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
package com.db4o.internal.btree;

import com.db4o.foundation.*;
import com.db4o.internal.btree.algebra.*;

public class BTreeRangeUnion implements BTreeRange {

	private final BTreeRangeSingle[] _ranges;

	public BTreeRangeUnion(BTreeRangeSingle[] ranges) {		
		this(toSortedCollection(ranges));
	}

	public BTreeRangeUnion(SortedCollection4 sorted) {
		if (null == sorted) {
			throw new ArgumentNullException();
		}
		_ranges = toArray(sorted);
	}
	
    @Override
	public void accept(BTreeRangeVisitor visitor) {
    	visitor.visit(this);
    }
	
	@Override
	public boolean isEmpty() {
		for (int i = 0; i < _ranges.length; i++) {
			if (!_ranges[i].isEmpty()) {
				return false;
			}
		}
		return true;
	}

	private static SortedCollection4 toSortedCollection(BTreeRangeSingle[] ranges) {
		if (null == ranges) {
			throw new ArgumentNullException();
		}
		SortedCollection4 collection = new SortedCollection4(BTreeRangeSingle.COMPARISON);
		for (int i = 0; i < ranges.length; i++) {
			BTreeRangeSingle range = ranges[i];
			if (!range.isEmpty()) {
				collection.add(range);
			}
		}		
		return collection;
	}

	private static BTreeRangeSingle[] toArray(SortedCollection4 collection) {
		return (BTreeRangeSingle[]) collection.toArray(new BTreeRangeSingle[collection.size()]);
	}

	@Override
	public BTreeRange extendToFirst() {
		throw new NotImplementedException();
	}

	@Override
	public BTreeRange extendToLast() {
		throw new NotImplementedException();
	}

	@Override
	public BTreeRange extendToLastOf(BTreeRange upperRange) {
		throw new NotImplementedException();
	}

	@Override
	public BTreeRange greater() {
		throw new NotImplementedException();
	}

	@Override
	public BTreeRange intersect(BTreeRange range) {
		if (null == range) {
			throw new ArgumentNullException();
		}
		return new BTreeRangeUnionIntersect(this).dispatch(range);
	}
	
	@Override
	public Iterator4 pointers() {
		return Iterators.concat(Iterators.map(_ranges, new Function4() {
			@Override
			public Object apply(Object range) {
				return ((BTreeRange)range).pointers();
			}
		}));
	}

	@Override
	public Iterator4 keys() {
		return Iterators.concat(Iterators.map(_ranges, new Function4() {
			@Override
			public Object apply(Object range) {
				return ((BTreeRange)range).keys();
			}
		}));
	}
	
	@Override
	public int size() {
		int size = 0;
		for (int i = 0; i < _ranges.length; i++) {
			size += _ranges[i].size();
		}
		return size;
	}

	@Override
	public BTreeRange smaller() {
		throw new NotImplementedException();
	}

	@Override
	public BTreeRange union(BTreeRange other) {
		if (null == other) {
			throw new ArgumentNullException();
		}
		return new BTreeRangeUnionUnion(this).dispatch(other);
	}

	public Iterator4 ranges() {
		return new ArrayIterator4(_ranges);
	}

	@Override
	public BTreePointer lastPointer() {
		throw new NotImplementedException();
	}
}
