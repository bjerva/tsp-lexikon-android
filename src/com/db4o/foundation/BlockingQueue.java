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


/**
 * @exclude
 */
public class BlockingQueue<T> implements BlockingQueue4<T> {
    
	protected NonblockingQueue<T> _queue = new NonblockingQueue<T>();

	protected Lock4 _lock = new Lock4();
	
	protected boolean _stopped;

	@Override
	public void add(final T obj) {
		if(obj == null){
			throw new IllegalArgumentException();
		}
		_lock.run(new Closure4<Void>() {
			@Override
			public Void run() {
				_queue.add(obj);
				_lock.awake();
				return null;
			}
		});
	}

	@Override
	public boolean hasNext() {
		return _lock.run(new Closure4<Boolean>() {
			@Override
			public Boolean run() {
				return _queue.hasNext();
			}
		});
	}

	@Override
	public Iterator4<T> iterator() {
		return _lock.run(new Closure4<Iterator4<T>>() {
			@Override
			public Iterator4<T> run() {
				return _queue.iterator();
			}
		});
	}

	@Override
	public T next(final long timeout) throws BlockingQueueStoppedException {
		return _lock.run(new Closure4<T>() {
			@Override
			public T run() {
				return unsafeWaitForNext(timeout) ? unsafeNext() : null;
			}
		});
	}
	
	@Override
	public int drainTo(final Collection4<T> target) {
		return _lock.run(new Closure4<Integer>() {
			@Override
			public Integer run() {
				unsafeWaitForNext();
				int i = 0;
				while(hasNext()) {
					i++;
					target.add(unsafeNext());
				}
				return i;
			}
		});
	}

	public boolean waitForNext(final long timeout) throws BlockingQueueStoppedException {
		 return _lock.run(new Closure4<Boolean>() {
			@Override
			public Boolean run() {
				return unsafeWaitForNext(timeout);
			}
		});
	}

	@Override
	public T next() throws BlockingQueueStoppedException {
		return _lock.run(new Closure4<T>() {
			@Override
			public T run() {
				unsafeWaitForNext();
				return unsafeNext();
			}
		});
	}
	
	@Override
	public void stop(){
		_lock.run(new Closure4<Void>() {
			@Override
			public Void run() {
				_stopped = true;
				_lock.awake();
				return null;
			}
		});
	}

	@Override
	public T nextMatching(final Predicate4<T> condition) {
		return _lock.run(new Closure4<T>() {
			@Override
			public T run() {
				return _queue.nextMatching(condition);
			}
		});
	}
	
	public void waitForNext() throws BlockingQueueStoppedException {
		_lock.run(new Closure4<Boolean>() {
			@Override
			public Boolean run() {
				unsafeWaitForNext();
				return null;
			}
		});
	}

	protected void unsafeWaitForNext() throws BlockingQueueStoppedException {
		unsafeWaitForNext(Long.MAX_VALUE);
	}

	protected boolean unsafeWaitForNext(final long timeout) throws BlockingQueueStoppedException {
		long timeLeft = timeout;
		long now = System.currentTimeMillis();
		while (timeLeft > 0) {
			if (_queue.hasNext()) {
				return true;
			}
			if(_stopped) {
				throw new BlockingQueueStoppedException();
			}
			_lock.snooze(timeLeft);
			long l = now;
			now = System.currentTimeMillis();
			timeLeft -= now-l;
		}
		return false;
	}

	private T unsafeNext() {
		return _queue.next();
	}
}
