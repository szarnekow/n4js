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
package org.eclipse.n4js.runner.tests

import org.eclipse.n4js.N4JSUiInjectorProvider
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import org.eclipse.core.resources.IProject
import org.eclipse.core.resources.IWorkspace
import org.eclipse.core.runtime.CoreException
import org.eclipse.core.runtime.OperationCanceledException
import org.eclipse.core.runtime.Platform
import org.junit.BeforeClass

import static com.google.common.base.Throwables.propagate
import static org.eclipse.n4js.projectModel.IN4JSProject.N4MF_MANIFEST
import static org.apache.log4j.Logger.getLogger
import static org.eclipse.core.resources.IContainer.INCLUDE_HIDDEN
import static org.eclipse.core.resources.ResourcesPlugin.FAMILY_AUTO_BUILD
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace
import static org.eclipse.core.runtime.Platform.isRunning
import static org.eclipse.core.runtime.jobs.Job.getJobManager
import static org.eclipse.emf.common.util.URI.createPlatformResourceURI
import static org.junit.Assert.*

import static extension org.eclipse.xtext.junit4.ui.util.IResourcesSetupUtil.*

/**
 * Class for testing the the runtime environment resolution for the N4 runners in standalone JUnit mode.
 * <p>
 * <b>NOTE:&nbsp;</b>This test class is responsible to execute exactly the same tests as defined in its
 * {@link RuntimeEnvironmentResolutionTest super class} but this implementation uses the {@link IWorkspace Eclipse workspace}
 * so it requires a running {@link Platform platform}.
 */
class RuntimeEnvironmentResolutionPluginUITest extends RuntimeEnvironmentResolutionTest {

	static val LOGGER = getLogger(RuntimeEnvironmentResolutionPluginUITest)
	static val NATURE_ID = "org.eclipse.xtext.ui.shared.xtextNature"; //$NON-NLS-1$

	@BeforeClass
	public static def beforeClass() {
		assertTrue('Platform is not running. These tests should be executed as JUnit Plug-in Test.', isRunning)
	}

	@Override
	override createInjector() {
		new N4JSUiInjectorProvider().injector;
	}

	@Override
	override before() {
		super.before()
		clearWorkspace()
	}

	@Override
	override protected createProjectWithManifest(String projectId, String manifestContent) {
		val project = workspace.root.getProject(projectId)
		assertFalse(project.exists)
		try {
			project.create(null)
		} catch (CoreException e) {
			LOGGER.error('Error while creating project in the workspace.', e)
		}
		assertTrue(project.exists)

		if (!project.open) {
			try {
				project.open(null)
			} catch (CoreException e) {
				LOGGER.error('Error while trying to opening project.', e)
			}
		}

		try {
			project.addNature(NATURE_ID)
		} catch (CoreException e) {
			LOGGER.error('Error while trying to adding Xtext nature to the project.', e)
		}

		val manifest = project.getFile(N4MF_MANIFEST)
		assertFalse(manifest.exists)

		var is = null as InputStream
		var exc = null as Exception
		try {
			is = new ByteArrayInputStream(manifestContent.bytes)
			manifest.create(is, true, null)
		} catch (Exception e) {
			LOGGER.error('''Error while creating manifest file for project: '«projectId»'.''')
			throw propagate(e)
		} finally {
			if (null !== is) {
				try {
					is.close
				} catch (IOException e) {
					exc = e
					try {
						is.close
					} catch (IOException e2) {
						e.addSuppressed(e2)
						// Intentionally ignored.
					}
				}
			}
		}
		if (null !== exc) {
			throw new RuntimeException('Error while closing the stream for the N4JS manifest file.', exc)
		}

		assertTrue(manifest.exists)
		waitForAutoBuild
		createPlatformResourceURI(project.fullPath.toString, true);
	}

	private def waitForAutoBuild() {
		var interrupted = false;
		do {
			try {
				getJobManager().join(FAMILY_AUTO_BUILD, null);
				interrupted = false;
			} catch (OperationCanceledException e) {
				LOGGER.warn('Operation was canceled while waiting for auto build job.', e)
			} catch (InterruptedException e) {
				interrupted = true;
			}
		} while (interrupted);
	}

	private def clearWorkspace() {
		workspace.root.projects.forEach[tryDelete]
		workspace.root.getProjects(INCLUDE_HIDDEN).forEach[tryDelete]
	}

	private def tryDelete(IProject it) {
		try {
			delete(true, true, null)
		} catch (CoreException e) {
			LOGGER.error('Error while cleaning workspace content.', e)
			throw propagate(e)
		}
	}

}
