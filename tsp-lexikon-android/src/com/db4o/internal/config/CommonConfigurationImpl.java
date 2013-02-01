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
import com.db4o.config.encoding.*;
import com.db4o.diagnostic.*;
import com.db4o.internal.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;

public class CommonConfigurationImpl implements CommonConfiguration {

	private final Config4Impl _config;

	public CommonConfigurationImpl(Config4Impl config) {
		_config = config;
	}

	@Override
	public void activationDepth(int depth) {
		_config.activationDepth(depth);
	}

	@Override
	public int activationDepth() {
		return _config.activationDepth();
	}

	@Override
	public void add(ConfigurationItem configurationItem) {
		_config.add(configurationItem);
	}
	
	@Override
	public void addAlias(Alias alias) {
		_config.addAlias(alias);
	}

	@Override
	public void removeAlias(Alias alias) {
		_config.removeAlias(alias);
	}

	@Override
	public void allowVersionUpdates(boolean flag) {
		_config.allowVersionUpdates(flag);
	}

	@Override
	public void automaticShutDown(boolean flag) {
		_config.automaticShutDown(flag);
	}

	@Override
	public void bTreeNodeSize(int size) {
		_config.bTreeNodeSize(size);
	}

	@Override
	public void callbacks(boolean flag) {
		_config.callbacks(flag);
	}

	public void callbackMode(CallBackMode mode) {
		_config.callbackMode(mode);
	}

	@Override
	public void callConstructors(boolean flag) {
		_config.callConstructors(flag);
	}

	@Override
	public void detectSchemaChanges(boolean flag) {
		_config.detectSchemaChanges(flag);
	}

	@Override
	public DiagnosticConfiguration diagnostic() {
		return _config.diagnostic();
	}

	@Override
	public void exceptionsOnNotStorable(boolean flag) {
		_config.exceptionsOnNotStorable(flag);
	}

	@Override
	public void internStrings(boolean flag) {
		_config.internStrings(flag);
	}

	@Override
	public void markTransient(String attributeName) {
		_config.markTransient(attributeName);
	}

	@Override
	public void messageLevel(int level) {
		_config.messageLevel(level);
	}

	@Override
	public ObjectClass objectClass(Object clazz) {
		return _config.objectClass(clazz);
	}

	@Override
	public void optimizeNativeQueries(boolean optimizeNQ) {
		_config.optimizeNativeQueries(optimizeNQ);
	}

	@Override
	public boolean optimizeNativeQueries() {
		return _config.optimizeNativeQueries();
	}

	@Override
	public QueryConfiguration queries() {
		return _config.queries();
	}

	@Override
	public void reflectWith(Reflector reflector) {
		_config.reflectWith(reflector);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void outStream(PrintStream outStream) {
		_config.setOut(outStream);
	}

	@Override
	public void stringEncoding(StringEncoding encoding) {
		_config.stringEncoding(encoding);
	}

	@Override
	public void testConstructors(boolean flag) {
		_config.testConstructors(flag);
	}

	@Override
	public void updateDepth(int depth) {
		_config.updateDepth(depth);
	}

	@Override
	public void weakReferences(boolean flag) {
		_config.weakReferences(flag);
	}

	@Override
	public void weakReferenceCollectionInterval(int milliseconds) {
		_config.weakReferenceCollectionInterval(milliseconds);
	}

	@Override
	public void registerTypeHandler(TypeHandlerPredicate predicate, TypeHandler4 typeHandler) {
		_config.registerTypeHandler(predicate, typeHandler);
	}

	@Override
	public EnvironmentConfiguration environment() {
		return new EnvironmentConfiguration() {
			@Override
			public void add(Object service) {
				_config.environmentContributions().add(service);
			}
		};
	}

	@Override
	public void nameProvider(NameProvider provider) {
		_config.nameProvider(provider);
	}

	@Override
	public void maxStackDepth(int maxStackDepth) {
		_config.maxStackDepth(maxStackDepth);
	}

	@Override
	public int maxStackDepth() {
		return _config.maxStackDepth();
	}

}
