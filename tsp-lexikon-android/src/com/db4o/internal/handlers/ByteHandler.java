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
package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;

public final class ByteHandler extends PrimitiveHandler {

    static final int LENGTH = 1 + Const4.ADDED_LENGTH;
	
	private static final Byte DEFAULTVALUE = new Byte((byte)0);
	
    @Override
	public Object coerce(ReflectClass claxx, Object obj) {
    	return Coercion4.toByte(obj);
    }

	@Override
	public Object defaultValue(){
		return DEFAULTVALUE;
	}
	
	@Override
	public int linkLength(){
		return LENGTH;
	}

	@Override
	public Class primitiveJavaClass(){
		return byte.class;
	}
	
	@Override
	Object read1(ByteArrayBuffer a_bytes){
		if (Deploy.debug){
			a_bytes.readBegin(Const4.YAPBYTE);
		}
		byte ret = a_bytes.readByte();
		if (Deploy.debug){
			a_bytes.readEnd();
		}
		return new Byte(ret);
	}
	
	@Override
	public void write(Object a_object, ByteArrayBuffer a_bytes){
		if(Deploy.debug){
			a_bytes.writeBegin(Const4.YAPBYTE);
		}
		a_bytes.writeByte(((Byte)a_object).byteValue());
		if(Deploy.debug){
			a_bytes.writeEnd();
		}
	}
					
    @Override
	public Object read(ReadContext context) {
        if (Deploy.debug) {
            Debug4.readBegin(context, Const4.YAPBYTE);
        }
        
        byte byteValue = context.readByte();
        
        if (Deploy.debug) {
            Debug4.readEnd(context);
        }
        
        return new Byte(byteValue);
    }

    @Override
	public void write(WriteContext context, Object obj) {
        if (Deploy.debug) {
            Debug4.writeBegin(context, Const4.YAPBYTE);
        }
        
        context.writeByte(((Byte)obj).byteValue());
        
        if (Deploy.debug) {
            Debug4.writeEnd(context);
        }
    }
    
    @Override
	public PreparedComparison internalPrepareComparison(Object source) {
    	final byte sourceByte = ((Byte)source).byteValue();
    	return new PreparedComparison() {
			@Override
			public int compareTo(Object target) {
				if(target == null){
					return 1;
				}
				byte targetByte = ((Byte)target).byteValue();
				return sourceByte == targetByte ? 0 : (sourceByte < targetByte ? - 1 : 1); 
			}
		};
    }

}
