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
package com.db4o.internal.query.result;

import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.query.processor.*;
import com.db4o.query.*;


/**
 * @exclude
 */
public class HybridQueryResult extends AbstractQueryResult {
	
	private AbstractQueryResult _delegate;
	
	public HybridQueryResult(Transaction transaction, QueryEvaluationMode mode) {
		super(transaction);
		_delegate = forMode(transaction, mode);
	}
	
	private static AbstractQueryResult forMode(Transaction transaction, QueryEvaluationMode mode){
		if(mode == QueryEvaluationMode.LAZY){
			return new LazyQueryResult(transaction); 
		}
		if(mode == QueryEvaluationMode.SNAPSHOT){
			return new SnapShotQueryResult(transaction); 
		}
		return new IdListQueryResult(transaction);
	}

	@Override
	public Object get(int index) {
		_delegate = _delegate.supportElementAccess();
		return _delegate.get(index);
	}
	
	@Override
	public int getId(int index) {
		_delegate = _delegate.supportElementAccess();
		return _delegate.getId(index);
	}

	@Override
	public int indexOf(int id) {
		_delegate = _delegate.supportElementAccess();
		return _delegate.indexOf(id);
	}

	@Override
	public IntIterator4 iterateIDs() {
		return _delegate.iterateIDs();
	}
	
	@Override
	public Iterator4 iterator() {
		return _delegate.iterator();
	}

	@Override
	public void loadFromClassIndex(ClassMetadata clazz) {
		_delegate.loadFromClassIndex(clazz);
	}

	@Override
	public void loadFromClassIndexes(ClassMetadataIterator iterator) {
		_delegate.loadFromClassIndexes(iterator);
	}

	@Override
	public void loadFromIdReader(Iterator4 reader) {
		_delegate.loadFromIdReader(reader);
	}

	@Override
	public void loadFromQuery(QQuery query) {
		if(query.requiresSort()){
			_delegate = new IdListQueryResult(transaction());
		}
		_delegate.loadFromQuery(query);
	}

	@Override
	public int size() {
		_delegate = _delegate.supportSize();
		return _delegate.size();
	}

	@Override
	public void sort(QueryComparator cmp) {
		_delegate = _delegate.supportSort();
		_delegate.sort(cmp);
	}
	
	@Override
	public void sortIds(IntComparator cmp) {
		_delegate = _delegate.supportSort();
		_delegate.sortIds(cmp);
	}
}
