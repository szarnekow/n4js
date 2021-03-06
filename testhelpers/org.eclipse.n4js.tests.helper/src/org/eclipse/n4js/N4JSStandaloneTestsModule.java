/**
 * Copyright (c) 2017 NumberFour AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   NumberFour AG - Initial API and implementation
 */
package org.eclipse.n4js;

import org.eclipse.n4js.N4JSInjectorProvider.BaseTestModule;
import org.eclipse.n4js.n4JS.Script;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.junit4.util.ResourceHelper;
import org.eclipse.xtext.resource.containers.IAllContainersState;
import org.eclipse.xtext.service.SingletonBinding;
import org.eclipse.xtext.validation.IDiagnosticConverter;
import org.xpect.setup.XpectGuiceModule;

/**
 * A Guice module that is used when running standalone tests.
 *
 * In case of Xpect tests, the Xpect runner will use this module to override the default runtime module bindings of the
 * language in use.
 *
 */
@XpectGuiceModule
public class N4JSStandaloneTestsModule extends BaseTestModule {
	/** */
	public Class<? extends IDiagnosticConverter> bindDiagnosticConverter() {
		return ExceptionAwareDiagnosticConverter.class;
	}

	/** */
	public Class<? extends N4JSParseHelper> bindN4JSParseHelper() {
		return SmokeTestWriter.class;
	}

	/** */
	@SingletonBinding
	public Class<? extends ResourceHelper> bindResourceHelper() {
		return ResourceHelper.class;
	}

	/** */
	public Class<? extends ParseHelper<Script>> bindParseHelperScript() {
		return SmokeTestWriter.class;
	}

	/**
	 * Bind a custom IAllContainerState in a testing context. See {@link N4JSTestsAllContainerState} for an explanation.
	 */
	public Class<? extends IAllContainersState.Provider> bindAllContainerState() {
		return N4JSTestsAllContainerState.Provider.class;
	}
}
