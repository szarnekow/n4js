/**
 * Copyright (c) 2016 NumberFour AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   NumberFour AG - Initial API and implementation
 */
package org.eclipse.n4js.tests.projectModel;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;
import org.junit.runner.RunWith;

import com.google.inject.Inject;

import org.eclipse.n4js.N4JSUiInjectorProvider;
import org.eclipse.n4js.internal.InternalN4JSWorkspace;
import org.eclipse.n4js.ui.internal.EclipseBasedN4JSWorkspace;

/**
 */
@RunWith(XtextRunner.class)
@InjectWith(N4JSUiInjectorProvider.class)
public class EclipseBasedInternalWorkspacePluginTest extends AbstractInternalWorkspaceTest {

	@Inject
	private IWorkspaceRoot workspace;

	@Inject
	private IResourceSetProvider resourceSetProvider;

	private EclipseBasedN4JSWorkspace testMe;

	@Override
	protected AbstractProjectModelSetup createSetup() {
		return new EclipseBasedProjectModelSetup(this, workspace);
	}

	@Override
	public void setUp() {
		testMe = new EclipseBasedN4JSWorkspace(workspace, resourceSetProvider);
		super.setUp();
	}

	@Override
	public void tearDown() {
		super.tearDown();
		testMe = null;
	}

	@Override
	protected InternalN4JSWorkspace getWorkspace() {
		return testMe;
	}

}
