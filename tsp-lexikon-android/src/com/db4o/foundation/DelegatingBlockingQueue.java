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


public class DelegatingBlockingQueue<T> implements BlockingQueue4<T> {
	
	private BlockingQueue4<T> queue;
	
	@Override
	public T next(long timeout) throws BlockingQueueStoppedException {
		return queue.next(timeout);
	}

	@Override
	public T next() {
		return queue.next();
	}

	@Override
	public void add(T obj) {
		queue.add(obj);
	}

	@Override
	public boolean hasNext() {
		return queue.hasNext();
	}

	@Override
	public T nextMatching(Predicate4<T> condition) {
		return queue.nextMatching(condition);
	}

	@Override
	public Iterator4 iterator() {
		return queue.iterator();
	}

	public DelegatingBlockingQueue(BlockingQueue4<T> queue) {
		this.queue = queue;
	}

	@Override
	public void stop() {
		queue.stop();
	}

	@Override
	public int drainTo(Collection4<T> list) {
		return queue.drainTo(list);
	}

}
