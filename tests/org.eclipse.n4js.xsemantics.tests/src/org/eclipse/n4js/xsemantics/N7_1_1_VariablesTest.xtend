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
package org.eclipse.n4js.xsemantics

import com.google.inject.Inject
import org.eclipse.n4js.N4JSInjectorProvider
import org.eclipse.n4js.n4JS.VariableStatement
import org.eclipse.n4js.typesystem.RuleEnvironmentExtensions
import org.eclipse.n4js.validation.JavaScriptVariant
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.*
import org.eclipse.xtext.diagnostics.Severity

/**
 * @see N7_3_2_ExportStatementOfVariablesTest
 */
@RunWith(XtextRunner)
@InjectWith(N4JSInjectorProvider)
class N7_1_1_VariablesTest extends AbstractTypesystemTest {

	@Inject
	extension ValidationTestHelper

	def assertVarDeclarationType(String expectedTypeAsString, String varDeclaration) {
		val scriptSrc = '''var «varDeclaration»;''';
		val script = createScript(JavaScriptVariant.n4js, scriptSrc);
		val issues = script.validate();
		assertEquals("Bogus test, expected no validation errors in: " + scriptSrc, 0, issues.filter[it.severity!=Severity.WARNING].size)
		val varDecl = (script.scriptElements.head as VariableStatement).varDecl.head
		assertNotNull("Bogus test, variable declaration not found", varDecl);

		val G = RuleEnvironmentExtensions.newRuleEnvironment(script);
		var typeOfVariableResult = ts.type(G, varDecl)
		assertNull("Cannot compute type of "+varDeclaration+":",typeOfVariableResult.ruleFailedException)
		assertNotNull("Cannot compute type of "+varDeclaration+":",typeOfVariableResult.value)
		assertEquals(expectedTypeAsString, typeOfVariableResult.value.typeRefAsString)
	}


	@Test
	def void testTypeAnyVars() {
		assertVarDeclarationType("any", '''a: any''');
		assertVarDeclarationType("any", '''any: any''');
		assertVarDeclarationType("any", '''a''');
		assertVarDeclarationType("any", '''a: any = "hello"''');
		assertVarDeclarationType("any", '''a: any = 1''');
		assertVarDeclarationType("any", '''a: any = null''');
		assertVarDeclarationType("any", '''a: any = undefined''');

	}

	@Test
	def void testTypeDeclaredVars() {
		assertVarDeclarationType("string", '''s: string''');
		assertVarDeclarationType("string", '''s: string = "Hello"''');
		assertVarDeclarationType("string", '''s: string = null''');
		assertVarDeclarationType("string", '''s: string = undefined''');
		assertVarDeclarationType("union{number,string}", '''s: union{number,string}''');
		assertVarDeclarationType("union{number,string}", '''s: union{number,string} = "Hello"''');
		assertVarDeclarationType("union{number,string}", '''s: union{number,string} = 42''');
	}

	@Test
	def void testTypeInferredToAnyVars() {
		assertVarDeclarationType("any", '''a''');
		assertVarDeclarationType("any", '''a = null''');
		assertVarDeclarationType("any", '''a = undefined''');
	}

	@Test
	def void testTypeInferredVars() {
		assertVarDeclarationType("string", '''s = "Hello"''');
		assertVarDeclarationType("int", '''i = 42''');
		assertVarDeclarationType("union{int,string}", '''u = (true)?"Hello":42''');
		assertVarDeclarationType("union{int,string}", '''u = "Hello" || 42''');
	}
}
