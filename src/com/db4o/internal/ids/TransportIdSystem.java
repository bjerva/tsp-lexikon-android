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
package com.db4o.internal.ids;

import com.db4o.internal.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public final class TransportIdSystem implements TransactionalIdSystem {
	
	private final LocalObjectContainer _container;
	
	public TransportIdSystem(LocalObjectContainer localObjectContainer) {
		_container = localObjectContainer;
	}
	
	@Override
	public int newId(SlotChangeFactory slotChangeFactory) {
		return _container.allocatePointerSlot();
	}
	
	@Override
	public void notifySlotCreated(int id, Slot slot,
			SlotChangeFactory slotChangeFactory) {
		writePointer(id, slot);
	}

	private void writePointer(int id, Slot slot) {
		_container.writePointer(id, slot);
	}
	
	@Override
	public void notifySlotUpdated(int id, Slot slot, SlotChangeFactory slotChangeFactory) {
		writePointer(id, slot);
	}
	
	@Override
	public void notifySlotDeleted(int id, SlotChangeFactory slotChangeFactory) {
		writePointer(id, Slot.ZERO);
	}
	
	@Override
	public void commit(FreespaceCommitter accumulator) {
		// don't do anything
	}
	
	@Override
	public Slot currentSlot(int id) {
		return committedSlot(id); 
	}
	
	@Override
	public void collectCallBackInfo(CallbackInfoCollector collector) {
		// do nothing
	}
	
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Slot committedSlot(int id) {
		return _container.readPointerSlot(id);
	}

	@Override
	public boolean isDeleted(int id) {
		return false;
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public int prefetchID() {
		return 0;
	}

	@Override
	public void prefetchedIDConsumed(int id) {
		
	}

	@Override
	public void rollback() {
		
	}

	@Override
	public void close() {
		
	}

	@Override
	public void accumulateFreeSlots(FreespaceCommitter freespaceCommitter, boolean forFreespace) {
		
	}

}
