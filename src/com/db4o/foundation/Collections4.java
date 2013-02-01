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
package com.db4o.foundation;

public class Collections4 {

	public static Sequence4 unmodifiableList(Sequence4 orig) {
		return new UnmodifiableSequence4(orig);
	}
	
	@decaf.Ignore(except=decaf.Platform.JDK11)
	public static void sort(Sequence4 sequence, final Comparison4 comparator) {
		final Object[] array = sequence.toArray();
		Arrays4.sort(array, comparator);
		sequence.clear();
		for (Object o : array) {
			sequence.add(o);
		}
	}

	private static class UnmodifiableSequence4 implements Sequence4 {

		private Sequence4 _sequence; 
		
		public UnmodifiableSequence4(Sequence4 sequence) {
			_sequence = sequence;
		}		

		@Override
		public boolean add(Object element) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void addAll(Iterable4 iterable){
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isEmpty() {
			return _sequence.isEmpty();
		}

		@Override
		public Iterator4 iterator() {
			return _sequence.iterator();
		}

		@Override
		public Object get(int index) {
			return _sequence.get(index);
		}

		@Override
		public int size() {
			return _sequence.size();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object obj) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean contains(Object obj) {
			return _sequence.contains(obj);
		}
		
		@Override
		public boolean containsAll(Iterable4 iter) {
			return _sequence.containsAll(iter);
		}


		@Override
		public Object[] toArray() {
			return _sequence.toArray();
		}
		
		@Override
		public Object[] toArray(Object[] array) {
			return _sequence.toArray(array);
		}
	}
}
