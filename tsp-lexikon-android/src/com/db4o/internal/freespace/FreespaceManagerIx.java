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
 * Old freespacemanager, before version 7.0.
 * If it is still in use freespace is dropped.
 * {@link BTreeFreespaceManager} should be used instead.
 */
public class FreespaceManagerIx extends AbstractFreespaceManager{
    
    
	public FreespaceManagerIx(int discardLimit, int remainderSizeLimit) {
		super(null, discardLimit, 0);
	}

	@Override
	public Slot allocateSafeSlot(int length) {
		return null;
	}
	
	@Override
	public void freeSafeSlot(Slot slot) {
		// do nothing
	}
    
    @Override
	public void beginCommit() {
    	
    }
    
    @Override
	public void endCommit() {
    	
    }
    
    @Override
	public int slotCount() {
    	throw new IllegalStateException();
    }
    
    @Override
	public void free(Slot slot) {
    	// Should no longer be used: Should not happen.
    }
    
    @Override
	public void freeSelf() {
    	// do nothing, freespace is dropped.
    }
    
    @Override
	public Slot allocateSlot(int length) {
        // implementation is no longer present, no freespace returned.
    	return null;
	}

    @Override
	public void migrateTo(FreespaceManager fm) {
    	// do nothing, freespace is dropped.
    }
    
	@Override
	public void traverse(final Visitor4 visitor) {
    	throw new IllegalStateException();
	}
    
    @Override
	public void start(int id) {
    	
    }
    
    @Override
	public byte systemType() {
        return FM_IX;
    }

    @Override
	public void write(LocalObjectContainer container) {
    	
    }

	@Override
	public void commit() {
		
	}
	
	@Override
	public void listener(FreespaceListener listener) {
		
	}

	@Override
	public boolean isStarted() {
		return false;
	}

	@Override
	public Slot allocateTransactionLogSlot(int length) {
		return null;
	}

	@Override
	public void read(LocalObjectContainer container, Slot slot) {
		
	}


}
