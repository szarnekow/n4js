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
package org.eclipse.n4js.hlc.tests;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.eclipse.n4js.hlc.base.N4jscBase;
import org.eclipse.n4js.hlc.helper.N4CliHelper;
import org.junit.Test;

/**
 */
public class N4jscSingleFileCompileTest extends AbstractN4jscTest {

	/***/
	public N4jscSingleFileCompileTest() {
		super("fixture");
	}

	/**
	 * Test help command
	 */
	@Test
	public void testHelp() throws Exception {
		logFile();

		Process p = createAndStartProcess("--help");

		int exitCode = p.waitFor();

		assertEquals("successful termination", 0, exitCode);
	}

	/**
	 * Compile a single file.
	 *
	 * @throws Exception
	 *             not expected.
	 */
	@Test
	public void testSingleFileCompile() throws Exception {
		logFile();

		Process p = createAndStartProcess("-t", "singleFile", WSP + "/" + "PSingle/src/a/A.n4js");

		int exitCode = p.waitFor();

		assertEquals("successful termination", 0, exitCode);
	}

	/**
	 * Compile & Run whole project.
	 *
	 * @throws Exception
	 *             in Error cases
	 */
	@Test
	public void testCompileAllAndRunWithNodejsPlugin() throws Exception {
		logFile();

		// -rw run with
		// -r run : file to run
		Process p = createAndStartProcess("-t", "allprojects", "-pl",
				WSP, "-rw",
				"nodejs", "-r",
				WSP + "/" + "P1/src/A.n4js");

		int exitCode = p.waitFor();

		assertEquals("successful termination", 0, exitCode);

		// check end of output.
		N4CliHelper.assertEndOfOutputExpectedToContain(N4jscBase.MARKER_RUNNER_OUPTUT, "Arrghtutut§", outputLogFile);

	}

	/**
	 * Compile & Run Tests for IDE-1510
	 *
	 * @throws Exception
	 *             in Error cases
	 */
	@Test
	public void testApiImplStub_CompileAndRunWithNodejsPlugin() throws Exception {
		logFile();

		Process p = createAndStartProcess( // ----
				"-pl", WSP + "/" + "IDE-1510_Incomplete_API_Implementation", // ----
				"-rw", "nodejs", // ----
				"-r", WSP + "/"
						+ "IDE-1510_Incomplete_API_Implementation/one.x.impl/src/AT_IDE-1510_Missing_Method.n4js", // ----
				"-t", "allprojects", // ----
				"IDE-1510_Incomplete_API_Implementation/one.api",
				"IDE-1510_Incomplete_API_Implementation/one.x.impl");

		int exitCode = p.waitFor();

		assertEquals("successful termination", 0, exitCode);

		// check end of output.
		N4CliHelper.assertEndOfOutputExpectedToContain(N4jscBase.MARKER_RUNNER_OUPTUT,
				"Hello from Implementation one.x.impl::p.A.n4js !", outputLogFile);
		N4CliHelper.assertEndOfOutputExpectedToContain(N4jscBase.MARKER_RUNNER_OUPTUT,
				"N4ApiNotImplementedError: API for method A.missing_n not implemented yet.", outputLogFile);
		N4CliHelper.assertEndOfOutputExpectedToContain(N4jscBase.MARKER_RUNNER_OUPTUT,
				"N4ApiNotImplementedError: API for method A.missing_someIA not implemented yet.", outputLogFile);
	}

	/**
	 * List available plugins.
	 *
	 * @throws Exception
	 *             in Error cases
	 */
	@Test
	public void testListAllRunnersPlugins_expecting_NODEJS() throws Exception {
		logFile();

		// -rw run with
		// -lr list runners.
		Process p = createAndStartProcess("-lr");

		int exitCode = p.waitFor();

		assertEquals("successful termination", 0, exitCode);

		N4CliHelper.assertContainsString("NODEJS", outputLogFile);
	}

	/**
	 * Just a negative test, capturing correct test-behavior
	 */
	@Test
	public void testListAllRunnersPlugins_notexpecting_XXX() throws Exception {
		logFile();

		// -rw run with
		// -lr list runners.
		Process p = createAndStartProcess("-lr");

		int exitCode = p.waitFor();

		assertEquals("successful termination", 0, exitCode);

		N4CliHelper.assertNotContainsString("XXX", outputLogFile);
	}

	/**
	 * Trying to run an uncompiled module: should result in a failure
	 *
	 * @throws IOException
	 *             if process causes problems.
	 * @throws InterruptedException
	 *             waiting for external process is interrupted.
	 */
	@Test()
	public void test_Run_Not_Compiled_A_WithNodeRunner() throws IOException, InterruptedException {
		logFile();

		// Process is running from TARGET-Folder.
		String proot = WSP;

		// Project
		String projectP1 = "P1";
		String pathToP1 = proot + "/" + projectP1;

		// absolute src filename
		String fileA = pathToP1 + "/src/A.n4js";

		Process p = createAndStartProcess("-pl", proot,
				"-t", "dontcompile",
				"-rw", "nodejs",
				"-r", fileA,
				"-v", "--debug");

		int exitCode = p.waitFor();

		// check the expected exit code of 7:
		assertEquals("Exit with wrong exitcode.", N4jscBase.EXITCODE_RUNNER_STOPPED_WITH_ERROR, exitCode);

	}
}
