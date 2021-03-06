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
package org.eclipse.n4js.hlc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/*** Basic tests for N4jsc,like checking command line options or simple compile. */

@RunWith(Parameterized.class)
public class N4jscArgumentOrderTest extends AbstractN4jscTest {

	final static String currentPath = new File(".").getAbsolutePath() + "/src/test/resources/N4jscArgumentOrderTest/";

	// @formatter:off
	final static String expectedOrder = "" +
			"DEBUG: 1. Project Project 'PA'  type=RUNTIME_LIBRARY  used by [Project 'PA'  type=RUNTIME_LIBRARY , Project 'PB'  type=LIBRARY , Project 'PC'  type=RUNTIME_LIBRARY ]\n" +
			"DEBUG: 2. Project Project 'PB'  type=LIBRARY  used by [Project 'PB'  type=LIBRARY ]\n" +
			"DEBUG: 3. Project Project 'PC'  type=RUNTIME_LIBRARY  used by [Project 'PC'  type=RUNTIME_LIBRARY ]\n" +
			"DEBUG: 4. Project Project 'PD'  type=RUNTIME_LIBRARY  used by [Project 'PD'  type=RUNTIME_LIBRARY ]\n" +
			"DEBUG: 5. Project Project 'PE'  type=RUNTIME_LIBRARY  used by [Project 'PE'  type=RUNTIME_LIBRARY ]\n" +
			"DEBUG: 6. Project Project 'PF'  type=RUNTIME_LIBRARY  used by [Project 'PF'  type=RUNTIME_LIBRARY ]\n" +
			"DEBUG: 7. Project Project 'PG'  type=RUNTIME_LIBRARY  used by [Project 'PG'  type=RUNTIME_LIBRARY ]\n";

	final static String[] args = {
			"--testCatalogFile " + currentPath + "build/test-catalog.json",
			"--targetPlatformInstallLocation " + currentPath + "test-catalog.json",
			"--targetPlatformFile " + currentPath + "build/npm",
			"--targetPlatformSkipInstall",
			"-t projects",
			currentPath + "PA",
			currentPath + "PB",
			currentPath + "PC",
			currentPath + "PD",
			currentPath + "PE",
			currentPath + "PF",
			currentPath + "PG",
			"--debug"
	};

	final static int[][] shuffleOrders = {
			{0,1,2,3,4,5,6,7,8,9,10,11,12},
			{12,11,10,9,8,7,6,5,4,3,2,1,0},
			{0,1,2,3,4,5,7,8,9,10,11,12,6},
			{0,12,1,11,2,10,3,9,4,8,5,7,6},
			};
	// @formatter:on

	@SuppressWarnings("javadoc")
	@Parameter
	public int[] shuffleOrder;

	/**
	 * Returns test data.
	 */
	@Parameters
	public static Collection<int[]> shuffleOrders() {
		return Arrays.asList(shuffleOrders);
	}

	/**
	 * normal compile all test without flag "--keepCompiling"
	 */
	@Test
	public void testDifferentArgumentOrder() {
		System.out.println(logMethodname());
		System.out.println("just for reference base-path is: " + new File(".").getAbsolutePath());

		String[] shuffledArgs = shuffleArgs(shuffleOrder);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		PrintStream old = System.out;
		System.setOut(ps);

		try {
			new N4jsc().doMain(shuffledArgs);

			Reader reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
			BufferedReader bufferedReader = new BufferedReader(reader);
			String line = null;
			String actualOrder = "";
			while ((line = bufferedReader.readLine()) != null) {
				if (line.contains(". Project Project '")) {
					old.println(line);
					actualOrder += line + "\n";
				}
			}

			assertEquals(expectedOrder, actualOrder);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			fail();
		} finally {
			System.out.flush();
			System.setOut(old);
		}
	}

	/**
	 * Shuffles the arguments based on the the given order. Also splits arguments at space characters.
	 */
	private String[] shuffleArgs(int[] shufflOrder) {
		List<String> shuffledArgs = new LinkedList<>();
		for (int i = 0; i < shufflOrder.length; i++) {
			String argLine = args[shufflOrder[i]];
			String[] splitArgLine = argLine.split(" ");
			for (String sal : splitArgLine) {
				shuffledArgs.add(sal);
			}
		}

		return shuffledArgs.toArray(new String[shuffledArgs.size()]);
	}

}
