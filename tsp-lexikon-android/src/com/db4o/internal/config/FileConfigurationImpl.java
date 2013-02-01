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
package com.db4o.internal.config;

import java.io.*;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.io.*;

class FileConfigurationImpl implements FileConfiguration {

	private final Config4Impl _config;

	public FileConfigurationImpl(Config4Impl config) {
		_config = config;
	}

	@Override
	public void blockSize(int bytes) {
		_config.blockSize(bytes);
	}

	@Override
	public void databaseGrowthSize(int bytes) {
		_config.databaseGrowthSize(bytes);
	}

	@Override
	public void disableCommitRecovery() {
		_config.disableCommitRecovery();
	}

	@Override
	public FreespaceConfiguration freespace() {
		return _config.freespace();
	}

	@Override
	public void generateUUIDs(ConfigScope setting) {
		_config.generateUUIDs(setting);
	}

	@Override
	@Deprecated
	public void generateVersionNumbers(ConfigScope setting) {
		_config.generateVersionNumbers(setting);
	}

	@Override
	public void generateCommitTimestamps(boolean setting) {
		_config.generateCommitTimestamps(setting);
	}

	@Override
	public void storage(Storage factory) throws GlobalOnlyConfigException {
		_config.storage(factory);
	}

	@Override
	public Storage storage() {
		return _config.storage();
	}

	@Override
	public void lockDatabaseFile(boolean flag) {
		_config.lockDatabaseFile(flag);
	}

	@Override
	public void reserveStorageSpace(long byteCount) throws DatabaseReadOnlyException, NotSupportedException {
		_config.reserveStorageSpace(byteCount);
	}

	@Override
	public void blobPath(String path) throws IOException {
		_config.setBlobPath(path);
	}
	
	@Override
	public void readOnly(boolean flag) {
		_config.readOnly(flag);
	}

	@Override
	public void recoveryMode(boolean flag) {
		_config.recoveryMode(flag);
	}

	@Override
	public void asynchronousSync(boolean flag) {
		_config.asynchronousSync(flag);
		
	}
}
