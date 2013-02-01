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
package com.db4o.internal.slots;

/**
 * @exclude
 */
public class SlotChangeFactory {
	
	private SlotChangeFactory(){
		
	}
	
	public SlotChange newInstance(int id){
		return new SlotChange(id);
	}
	
	public static final SlotChangeFactory USER_OBJECTS = new SlotChangeFactory();
	
	public static final SlotChangeFactory SYSTEM_OBJECTS = new SlotChangeFactory(){
		@Override
		public SlotChange newInstance(int id) {
			return new SystemSlotChange(id);
		};
	};
	
	public static final SlotChangeFactory ID_SYSTEM = new SlotChangeFactory(){
		@Override
		public SlotChange newInstance(int id) {
			return new IdSystemSlotChange(id);
		};
	};
	
	public static final SlotChangeFactory FREE_SPACE = new SlotChangeFactory(){
		@Override
		public SlotChange newInstance(int id) {
			return new FreespaceSlotChange(id);
		};
	};
	
}
