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
package com.db4o.internal.query;

import java.util.*;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.query.result.*;
import com.db4o.query.*;

/**
 * @exclude
 * @sharpen.ignore 
 */
@decaf.IgnoreExtends(decaf.Platform.JDK11)
public class ObjectSetFacade extends AbstractList implements ExtObjectSet {
    
    public final StatefulQueryResult _delegate;
    
    public ObjectSetFacade(QueryResult queryResult){
        _delegate = new StatefulQueryResult(queryResult);
    }
    
	public void sort(QueryComparator cmp) {
		_delegate.sort(cmp);
	}
	
	@Override
	@decaf.ReplaceFirst(value="return _delegate.iterator();", platform=decaf.Platform.JDK11)
    public Iterator iterator() {
    	return new JDKIterator();
    }
	
	@decaf.Ignore(decaf.Platform.JDK11)
	class JDKIterator extends Iterable4Adaptor implements Iterator {
		public JDKIterator() {
			super(_delegate);
		}
		
		@Override
		protected boolean moveNext() {
			synchronized (_delegate.lock()) {
				return super.moveNext();
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
    
    @Override
	public long[] getIDs() {
        return _delegate.getIDs();
    }

    @Override
	public ExtObjectSet ext() {
        return this;
    }

    @Override
	public boolean hasNext() {
        return _delegate.hasNext();
    }

    @Override
	public Object next() {
        return _delegate.next();
    }

    @Override
	public void reset() {
        _delegate.reset();
    }

    @Override
	public int size() {
        return _delegate.size();
    }
    
    /**
     */
    @Override
	@decaf.Ignore(decaf.Platform.JDK11)
    public boolean contains(Object obj) {
        return indexOf(obj) >= 0;
    }

    @Override
	public Object get(int index) {
        return _delegate.get(index);
    }

    /**
     */
    @Override
	@decaf.Ignore(decaf.Platform.JDK11)
    public int indexOf(Object obj) {
    	return _delegate.indexOf(obj);
    }
    
    /**
     */
    @Override
	@decaf.Ignore(decaf.Platform.JDK11)
    public int lastIndexOf(Object obj) {
        return indexOf(obj);
    }
    
    /**
     */
    @decaf.Ignore(decaf.Platform.JDK11)
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
