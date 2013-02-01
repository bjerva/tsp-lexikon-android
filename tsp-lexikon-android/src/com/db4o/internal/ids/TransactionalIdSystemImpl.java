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

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public class TransactionalIdSystemImpl implements TransactionalIdSystem {
	
	private IdSlotChanges _slotChanges;

	private TransactionalIdSystemImpl _parentIdSystem;
	
	private final Closure4<IdSystem> _globalIdSystem;
	
	public TransactionalIdSystemImpl(Closure4<FreespaceManager> freespaceManager, Closure4<IdSystem> globalIdSystem, TransactionalIdSystemImpl parentIdSystem){
		_globalIdSystem = globalIdSystem;
		_slotChanges = new IdSlotChanges(this, freespaceManager);
		_parentIdSystem = parentIdSystem;
	}
	
	@Override
	public void collectCallBackInfo(final CallbackInfoCollector collector) {
		if(! _slotChanges.isDirty()){
			return;
		}
		_slotChanges.traverseSlotChanges(new Visitor4<SlotChange>() {
			@Override
			public void visit(SlotChange slotChange) {
				int id = slotChange._key;
				if (slotChange.isDeleted()) {
					if(! slotChange.isNew()){
						collector.deleted(id);
					}
				} else if (slotChange.isNew()) {
					collector.added(id);
				} else {
					collector.updated(id);
				}
			}
		});
	}

	@Override
	public boolean isDirty() {
		return _slotChanges.isDirty();
	}

	@Override
	public void commit(final FreespaceCommitter freespaceCommitter) {
		Visitable<SlotChange> slotChangeVisitable = new Visitable<SlotChange>() {
			@Override
			public void accept(Visitor4<SlotChange> visitor) {
				traverseSlotChanges(visitor);
			}
		};
		freespaceCommitter.transactionalIdSystem(this);
		accumulateFreeSlots(freespaceCommitter, false);
		globalIdSystem().commit(slotChangeVisitable, freespaceCommitter);
	}
	

	@Override
	public void accumulateFreeSlots(FreespaceCommitter accumulator, boolean forFreespace) {
		_slotChanges.accumulateFreeSlots(accumulator, forFreespace, isSystemIdSystem());
		if(_parentIdSystem != null){
			_parentIdSystem.accumulateFreeSlots(accumulator, forFreespace);
		}
	}
	
	private boolean isSystemIdSystem() {
		return _parentIdSystem == null;
	}

	public void completeInterruptedTransaction(int transactionId1, int transactionId2) {
		globalIdSystem().completeInterruptedTransaction(transactionId1, transactionId2);
	}

	@Override
	public Slot committedSlot(int id) {
        if (id == 0) {
            return null;
        }
		return globalIdSystem().committedSlot(id);
	}

	@Override
	public Slot currentSlot(int id) {
        Slot slot = modifiedSlot(id);
        if(slot != null){
        	return slot;
        }
        return committedSlot(id);
	}

	public Slot modifiedSlot(int id) {
		if (id == 0) {
            return null;
        }
        SlotChange change = _slotChanges.findSlotChange(id);
        if (change != null) {
            if(change.slotModified()){
                return change.newSlot();
            }
        }
        return modifiedSlotInParentIdSystem(id); 
	}

	public final Slot modifiedSlotInParentIdSystem(int id) {
		if(_parentIdSystem == null){
			return null;
		}
        return _parentIdSystem.modifiedSlot(id);
	}

	@Override
	public void rollback() {
		_slotChanges.rollback();
	}

	@Override
	public void clear() {
		_slotChanges.clear();
	}

	@Override
	public boolean isDeleted(int id) {
		return _slotChanges.isDeleted(id);
	}

	@Override
	public void notifySlotUpdated(int id, Slot slot, SlotChangeFactory slotChangeFactory) {
		_slotChanges.notifySlotUpdated(id, slot, slotChangeFactory);
	}

	private void traverseSlotChanges(Visitor4<SlotChange> visitor){
		if(_parentIdSystem != null){
			_parentIdSystem.traverseSlotChanges(visitor);
		}
		_slotChanges.traverseSlotChanges(visitor);
	}
    
	@Override
	public int newId(SlotChangeFactory slotChangeFactory) {
		int id = acquireId();
        _slotChanges.produceSlotChange(id, slotChangeFactory).notifySlotCreated(null);
		return id;
	}

	private int acquireId() {
		return globalIdSystem().newId();
	}

	@Override
	public int prefetchID() {
		int id = acquireId();
		_slotChanges.addPrefetchedID(id);
		return id;
	}

	@Override
	public void prefetchedIDConsumed(int id) {
		_slotChanges.prefetchedIDConsumed(id);
	}

	@Override
	public void notifySlotCreated(int id, Slot slot, SlotChangeFactory slotChangeFactory) {
		_slotChanges.notifySlotCreated(id, slot, slotChangeFactory);
	}
	
	@Override
	public void notifySlotDeleted(int id, SlotChangeFactory slotChangeFactory) {
		_slotChanges.notifySlotDeleted(id, slotChangeFactory);
	}

	private IdSystem globalIdSystem() {
		return _globalIdSystem.run();
	}

	@Override
	public void close() {
		_slotChanges.freePrefetchedIDs(globalIdSystem());
	}

}
