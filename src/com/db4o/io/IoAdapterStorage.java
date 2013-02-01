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
package com.db4o.io;

import static com.db4o.foundation.Environments.*;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.foundation.*;
/**
 * @exclude
 */
@SuppressWarnings("deprecation")
public class IoAdapterStorage implements Storage {
	
	private final IoAdapter _io;

	public IoAdapterStorage(IoAdapter io) {
		_io = io;
	}

	@Override
	public boolean exists(String uri) {
		return _io.exists(uri);
	}

	@Override
	public Bin open(BinConfiguration config) throws Db4oIOException {
		final IoAdapterBin bin = new IoAdapterBin(_io.open(config.uri(), config.lockFile(), config.initialLength(), config.readOnly()));
		my(BlockSize.class).register(bin);
		return bin;
	}
	
	static  class IoAdapterBin implements Bin, Listener4<Integer>{

		private final IoAdapter _io;

		public IoAdapterBin(IoAdapter io) {
			_io = io;
	    }
		
		@Override
		public void close() {
			_io.close();
		}
		
		@Override
		public long length() {
			return _io.getLength();
		}
		
		@Override
		public int read(long position, byte[] buffer, int bytesToRead) {
			_io.seek(position);
			return _io.read(buffer, bytesToRead);
		}
		
		@Override
		public void sync() {
			_io.sync();
		}
		
		@Override
		public int syncRead(long position, byte[] bytes, int bytesToRead) {
			return read(position, bytes, bytesToRead);
		}
		
		@Override
		public void write(long position, byte[] bytes, int bytesToWrite) {
			_io.seek(position);
			_io.write(bytes, bytesToWrite);
		}
		
		public void blockSize(int blockSize) {
			_io.blockSize(blockSize);
		}

		@Override
		public void onEvent(Integer event) {
			blockSize(event);
		}
		
		@Override
		public void sync(Runnable runnable) {
			sync();
			runnable.run();
			sync();
		}

	}

	@Override
	public void delete(String uri) throws IOException {
		_io.delete(uri);
	}

	@Override
	public void rename(String oldUri, String newUri) throws IOException {
		throw new NotImplementedException();
	}
}
