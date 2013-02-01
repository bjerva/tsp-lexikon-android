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
package com.db4o.internal.freespace;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public class BlockAwareFreespaceManager implements FreespaceManager {
	
	private final FreespaceManager _delegate;
	
	private final BlockConverter _blockConverter;

	public BlockAwareFreespaceManager(FreespaceManager delegate_, BlockConverter blockConverter) {
		_delegate = delegate_;
		_blockConverter = blockConverter;
	}

	@Override
	public Slot allocateSlot(int length) {
		Slot slot = _delegate.allocateSlot(_blockConverter.bytesToBlocks(length));
		if(slot == null){
			return null;
		}
		return _blockConverter.toNonBlockedLength(slot);
	}

	@Override
	public Slot allocateSafeSlot(int length) {
		Slot slot = _delegate.allocateSafeSlot(_blockConverter.bytesToBlocks(length));
		if(slot == null){
			return null;
		}
		return _blockConverter.toNonBlockedLength(slot);
	}

	@Override
	public void beginCommit() {
		_delegate.beginCommit();
	}

	@Override
	public void commit() {
		_delegate.commit();
	}

	@Override
	public void endCommit() {
		_delegate.endCommit();
	}

	@Override
	public void free(Slot slot) {
		_delegate.free(_blockConverter.toBlockedLength(slot));
	}

	@Override
	public void freeSelf() {
		_delegate.freeSelf();
	}

	@Override
	public void freeSafeSlot(Slot slot) {
		_delegate.freeSafeSlot(_blockConverter.toBlockedLength(slot));
		
	}

	@Override
	public void listener(FreespaceListener listener) {
		_delegate.listener(listener);
	}

	@Override
	public void migrateTo(FreespaceManager fm) {
		throw new IllegalStateException();
	}

	@Override
	public int slotCount() {
		return _delegate.slotCount();
	}

	@Override
	public void start(int id) {
		throw new IllegalStateException();
	}

	@Override
	public byte systemType() {
		return _delegate.systemType();
	}

	@Override
	public int totalFreespace() {
		return _blockConverter.blocksToBytes(_delegate.totalFreespace());
	}

	@Override
	public void traverse(final Visitor4<Slot> visitor) {
		_delegate.traverse(new Visitor4<Slot>() {
			@Override
			public void visit(Slot slot) {
				visitor.visit(_blockConverter.toNonBlockedLength(slot));
			}
		});
	}

	@Override
	public void write(LocalObjectContainer container) {
		_delegate.write(container);
	}

	@Override
	public void slotFreed(Slot slot) {
		_delegate.slotFreed(slot);
	}

	@Override
	public boolean isStarted() {
		return _delegate.isStarted();
	}

	@Override
	public Slot allocateTransactionLogSlot(int length) {
		Slot slot = _delegate.allocateTransactionLogSlot(_blockConverter.bytesToBlocks(length));
		if(slot == null){
			return null;
		}
		return _blockConverter.toNonBlockedLength(slot);
	}

	@Override
	public void read(LocalObjectContainer container, Slot slot) {
		throw new IllegalStateException();
	}

}

