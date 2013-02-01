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
package com.db4o.internal.callbacks;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.query.*;

public class NullCallbacks implements Callbacks {

	@Override
	public void queryOnFinished(Transaction transaction, Query query) {
	}

	@Override
	public void queryOnStarted(Transaction transaction, Query query) {
	}

	@Override
	public boolean objectCanNew(Transaction transaction, Object obj) {
		return true;
	}

	@Override
	public boolean objectCanActivate(Transaction transaction, Object obj) {
		return true;
	}
	
	@Override
	public boolean objectCanUpdate(Transaction transaction, ObjectInfo objectInfo) {
		return true;
	}
	
	@Override
	public boolean objectCanDelete(Transaction transaction, ObjectInfo objectInfo) {
		return true;
	}
	
	@Override
	public boolean objectCanDeactivate(Transaction transaction, ObjectInfo objectInfo) {
		return true;
	}
	
	@Override
	public void objectOnNew(Transaction transaction, ObjectInfo obj) {
	}
	
	@Override
	public void objectOnActivate(Transaction transaction, ObjectInfo obj) {
	}

	@Override
	public void objectOnUpdate(Transaction transaction, ObjectInfo obj) {
	}

	@Override
	public void objectOnDelete(Transaction transaction, ObjectInfo obj) {
	}

	@Override
	public void objectOnDeactivate(Transaction transaction, ObjectInfo obj) {	
	}

	@Override
	public void objectOnInstantiate(Transaction transaction, ObjectInfo obj) {
	}

	@Override
	public void commitOnStarted(Transaction transaction, CallbackObjectInfoCollections objectInfoCollections) {
	}
	
	@Override
	public void commitOnCompleted(Transaction transaction, CallbackObjectInfoCollections objectInfoCollections, boolean isOwnCommit) {
	}

	@Override
	public boolean caresAboutCommitting() {
		return false;
	}

	@Override
	public boolean caresAboutCommitted() {
		return false;
	}

	@Override
	public void classOnRegistered(ClassMetadata clazz) {
	}

    @Override
	public boolean caresAboutDeleting() {
        return false;
    }

    @Override
	public boolean caresAboutDeleted() {
        return false;
    }

	@Override
	public void closeOnStarted(ObjectContainer container) {
	}

	@Override
	public void openOnFinished(ObjectContainer container) {
	}
}
