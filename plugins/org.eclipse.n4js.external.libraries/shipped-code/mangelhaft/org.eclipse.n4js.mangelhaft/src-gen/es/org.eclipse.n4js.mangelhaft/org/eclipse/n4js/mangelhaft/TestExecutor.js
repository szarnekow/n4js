// Generated by N4JS transpiler; for copyright see original N4JS source file.

(function(System) {
	'use strict';
	System.register([
		'org.eclipse.n4js.mangelhaft.assert/org/eclipse/n4js/mangelhaft/assert/AssertionError',
		'org.eclipse.n4js.mangelhaft/org/eclipse/n4js/mangelhaft/types/ResultGroup',
		'org.eclipse.n4js.mangelhaft/org/eclipse/n4js/mangelhaft/types/ResultGroups',
		'org.eclipse.n4js.mangelhaft/org/eclipse/n4js/mangelhaft/types/TestResult',
		'org.eclipse.n4js.mangelhaft/org/eclipse/n4js/mangelhaft/types/TestSpy',
		'org.eclipse.n4js.mangelhaft.assert/org/eclipse/n4js/mangelhaft/precondition/PreconditionNotMet'
	], function($n4Export) {
		var AssertionError, ResultGroup, ResultGroups, TestResult, TestSpy, PreconditionNotMet, TestExecutor;
		TestExecutor = function TestExecutor(spy) {
			this.spy = undefined;
			this.spy = spy;
		};
		$n4Export('TestExecutor', TestExecutor);
		return {
			setters: [
				function($_import_org_u002eeclipse_u002en4js_u002emangelhaft_u002eassert_org_u002feclipse_u002fn4js_u002fmangelhaft_u002fassert_u002fAssertionError) {
					AssertionError = $_import_org_u002eeclipse_u002en4js_u002emangelhaft_u002eassert_org_u002feclipse_u002fn4js_u002fmangelhaft_u002fassert_u002fAssertionError.AssertionError;
				},
				function($_import_org_u002eeclipse_u002en4js_u002emangelhaft_org_u002feclipse_u002fn4js_u002fmangelhaft_u002ftypes_u002fResultGroup) {
					ResultGroup = $_import_org_u002eeclipse_u002en4js_u002emangelhaft_org_u002feclipse_u002fn4js_u002fmangelhaft_u002ftypes_u002fResultGroup.ResultGroup;
				},
				function($_import_org_u002eeclipse_u002en4js_u002emangelhaft_org_u002feclipse_u002fn4js_u002fmangelhaft_u002ftypes_u002fResultGroups) {
					ResultGroups = $_import_org_u002eeclipse_u002en4js_u002emangelhaft_org_u002feclipse_u002fn4js_u002fmangelhaft_u002ftypes_u002fResultGroups.ResultGroups;
				},
				function($_import_org_u002eeclipse_u002en4js_u002emangelhaft_org_u002feclipse_u002fn4js_u002fmangelhaft_u002ftypes_u002fTestResult) {
					TestResult = $_import_org_u002eeclipse_u002en4js_u002emangelhaft_org_u002feclipse_u002fn4js_u002fmangelhaft_u002ftypes_u002fTestResult.TestResult;
				},
				function($_import_org_u002eeclipse_u002en4js_u002emangelhaft_org_u002feclipse_u002fn4js_u002fmangelhaft_u002ftypes_u002fTestSpy) {
					TestSpy = $_import_org_u002eeclipse_u002en4js_u002emangelhaft_org_u002feclipse_u002fn4js_u002fmangelhaft_u002ftypes_u002fTestSpy.TestSpy;
				},
				function($_import_org_u002eeclipse_u002en4js_u002emangelhaft_u002eassert_org_u002feclipse_u002fn4js_u002fmangelhaft_u002fprecondition_u002fPreconditionNotMet) {
					PreconditionNotMet = $_import_org_u002eeclipse_u002en4js_u002emangelhaft_u002eassert_org_u002feclipse_u002fn4js_u002fmangelhaft_u002fprecondition_u002fPreconditionNotMet.PreconditionNotMet;
				}
			],
			execute: function() {
				$makeClass(TestExecutor, N4Object, [], {
					handleFixme: {
						value: function handleFixme___n4(testObject, testRes) {
							if (testObject.fixme) {
								if (testRes.testStatus === 'PASSED') {
									testRes.testStatus = 'FAILED';
									testRes.message = "Test marked with @Fixme annotation but was successful. Issue blocking test has probably been fixed. Try removing annotation.";
									if (testObject.fixmeReason != null) {
										testRes.message += " (reason was '" + testObject.fixmeReason + "')";
									}
									testRes.trace = [
										String(testRes)
									];
								} else if (testRes.testStatus === 'FAILED' || testRes.testStatus === 'ERROR') {
									testRes.testStatus = 'SKIPPED_FIXME';
									testRes.message = testObject.fixmeReason;
									testRes.actual = testRes.expected = testRes.trace = null;
								}
							}
							return testRes;
						}
					},
					callAll: {
						value: function callAll___n4(instrumentedTest, testMethodDescriptors) {
							return $spawn(function *() {
								let results = [];
								var runTest = function runTest(testMethodDescriptor) {
									return $spawn(function *() {
										let timeoutId, testResult;
										function doPromise(resolve, reject) {
											return $spawn(function *() {
												let res;
												timeoutId = setTimeout(function() {
													reject(new Error("Test object " + testMethodDescriptor.name + " timed out after " + testMethodDescriptor.timeout + " milliseconds"));
												}, testMethodDescriptor.timeout);
												try {
													res = (yield Promise.resolve(testMethodDescriptor.value.call(instrumentedTest.testObject)));
												} catch(error) {
													reject(error);
												} finally {
													clearTimeout(timeoutId);
												}
												resolve(res);
											}.apply(this, arguments));
										}
										testResult = (yield new Promise(doPromise));
										return testResult;
									}.apply(this, arguments));
								};
								if (testMethodDescriptors) {
									results = ((yield Promise.all(testMethodDescriptors.map(runTest))));
								}
								return results;
							}.apply(this, arguments));
						}
					},
					getAncestorTestMethods: {
						value: function getAncestorTestMethods___n4(iTest, testMethodName) {
							let testMethods = [], nodeTestMethods, node = iTest;
							;
							while(node.parent) {
								node = node.parent;
							}
							do {
								let nodeObj = node;
								nodeTestMethods = nodeObj[testMethodName];
								if (nodeTestMethods && nodeTestMethods.length) {
									testMethods = testMethods.concat(nodeTestMethods);
								}
							} while(node = node.child);
							return testMethods;
						}
					},
					runTestAsync: {
						value: function runTestAsync___n4(instrumentedTest) {
							return $spawn(function *() {
								return this.runTestsAsync([
									instrumentedTest
								]);
							}.apply(this, arguments));
						}
					},
					runGroup: {
						value: function runGroup___n4(iTest) {
							return $spawn(function *() {
								let rg, testObject, testRes, testResults = [], beforeAlls = this.getAncestorTestMethods(iTest, "beforeAlls"), befores = this.getAncestorTestMethods(iTest, "befores"), afters = this.getAncestorTestMethods(iTest, "afters").reverse(), afterAlls = this.getAncestorTestMethods(iTest, "afterAlls").reverse(), numTests, ii, start, end;
								;
								(yield this.spy.groupStarted.dispatch([
									iTest
								]));
								if (iTest.error) {
									testResults = (yield this.errorTests(iTest, iTest.error));
								} else {
									try {
										(yield this.callAll(iTest, beforeAlls));
										numTests = iTest.tests.length;
										for(ii = 0;ii < numTests;++ii) {
											testObject = iTest.tests[ii];
											try {
												(yield this.spy.testStarted.dispatch([
													iTest,
													testObject
												]));
												start = new Date().getTime();
												if (testObject.ignore) {
													testRes = new TestResult({
														testStatus: 'SKIPPED_IGNORE',
														message: testObject.ignoreReason,
														description: testObject.name
													});
												} else {
													try {
														(yield this.callAll(iTest, befores));
														(yield this.callAll(iTest, [
															testObject
														]));
														testRes = new TestResult({
															testStatus: 'PASSED',
															description: testObject.name
														});
													} finally {
														(yield this.callAll(iTest, afters));
													}
												}
												end = new Date().getTime();
											} catch(er) {
												let err = er;
												end = new Date().getTime();
												testRes = TestExecutor.generateFailureTestResult(err, testObject.name);
											}
											testRes.elapsedTime = end - start;
											testRes = this.handleFixme(testObject, testRes);
											(yield this.spy.testFinished.dispatch([
												iTest,
												testObject,
												testRes,
												(function() {
													return $spawn(function *() {
														let allTests = iTest.tests;
														;
														iTest.tests = [
															testObject
														];
														try {
															(yield this.runTestsAsync([
																iTest
															]));
														} finally {
															iTest.tests = allTests;
														}
													}.apply(this, arguments));
												}).bind(this)
											]));
											testResults.push(testRes);
										}
										(yield this.callAll(iTest, afterAlls));
									} catch(error) {
										let results = (yield this.errorTests(iTest, error));
										testResults = testResults.concat(results);
									}
								}
								rg = new ResultGroup(testResults, ("" + iTest.name + ""));
								(yield this.spy.groupFinished.dispatch([
									iTest,
									rg
								]));
								return rg;
							}.apply(this, arguments));
						}
					},
					runTestsAsync: {
						value: function runTestsAsync___n4(instrumentedTests) {
							return $spawn(function *() {
								let results = [];
								for(let test of instrumentedTests) {
									if (test) {
										if (test.hasParameterizedTests) {
											let pResults = [];
											(yield this.spy.parameterizedGroupsStarted.dispatch([
												test
											]));
											for(let ptest of test.parameterizedTests) {
												let testRes = (yield this.runGroup(ptest));
												pResults.push(testRes);
												results.push(testRes);
											}
											(yield this.spy.parameterizedGroupsFinished.dispatch([
												new ResultGroups(pResults)
											]));
										} else {
											let testRes = (yield this.runGroup(test));
											results.push(testRes);
										}
									}
								}
								let resultGroups = new ResultGroups(results);
								return resultGroups;
							}.apply(this, arguments));
						}
					},
					errorTests: {
						value: function errorTests___n4(instrumentedTest, error) {
							return $spawn(function *() {
								let len = instrumentedTest.tests.length, testResult, testResults = [], test, ii;
								;
								for(ii = 0;ii < len;++ii) {
									test = instrumentedTest.tests[ii];
									(yield this.spy.testStarted.dispatch([
										instrumentedTest,
										test
									]));
									testResult = TestExecutor.generateFailureTestResult(error, test.name);
									testResult.elapsedTime = 0;
									(yield this.spy.testFinished.dispatch([
										instrumentedTest,
										test,
										testResult
									]));
									testResults.push(testResult);
								}
								return testResults;
							}.apply(this, arguments));
						}
					},
					spy: {
						value: undefined,
						writable: true
					}
				}, {
					getStringFromAbiguous: {
						value: function getStringFromAbiguous___n4(thing) {
							let str;
							if (thing === null) {
								str = "null";
							} else if (typeof thing === "object") {
								str = Object.prototype.hasOwnProperty.call(thing, "toString") ? "" + thing : "prototypeless object";
							} else {
								str = "" + thing;
							}
							return str;
						}
					},
					generateFailureTestResult: {
						value: function generateFailureTestResult___n4(ex, description) {
							if (!ex) {
								ex = new Error("Unknown error: " + description);
							} else if (typeof ex === "string") {
								ex = new Error(ex);
							}
							let e = ex instanceof AssertionError ? ex : ex, reason = e.toString(), tr, status, trace;
							;
							if (reason.charAt(0) === "[") {
								reason = e.name ? ("" + e.name + " : " + description + "") : description;
							}
							if (ex instanceof AssertionError) {
								status = 'FAILED';
							} else if (ex instanceof PreconditionNotMet) {
								status = 'SKIPPED_PRECONDITION';
							} else if (ex instanceof N4ApiNotImplementedError) {
								status = 'SKIPPED_NOT_IMPLEMENTED';
							} else {
								status = 'ERROR';
							}
							if (e.message) {
								reason = String(e);
							}
							if (e.stack) {
								if (typeof e.stack === "string") {
									trace = (e.stack).split("\n");
								} else if (Array.isArray(e.stack)) {
									trace = e.stack;
								} else {
									trace = [
										(e.stack).toString()
									];
								}
								trace = trace.map(function(line) {
									return line.trim();
								});
							}
							tr = new TestResult({
								testStatus: status,
								message: reason,
								trace: trace,
								description: description,
								expected: e.hasOwnProperty("expected") ? this.getStringFromAbiguous(e.expected) : undefined,
								actual: e.hasOwnProperty("actual") ? this.getStringFromAbiguous(e.actual) : undefined
							});
							return tr;
						}
					}
				}, function(instanceProto, staticProto) {
					var metaClass = new N4Class({
						name: 'TestExecutor',
						origin: 'org.eclipse.n4js.mangelhaft',
						fqn: 'org.eclipse.n4js.mangelhaft.TestExecutor.TestExecutor',
						n4superType: N4Object.n4type,
						allImplementedInterfaces: [],
						ownedMembers: [
							new N4DataField({
								name: 'spy',
								isStatic: false,
								annotations: [
									new N4Annotation({
										name: 'Inject',
										details: []
									})
								]
							}),
							new N4Method({
								name: 'constructor',
								isStatic: false,
								jsFunction: instanceProto['constructor'],
								annotations: []
							}),
							new N4Method({
								name: 'getStringFromAbiguous',
								isStatic: true,
								jsFunction: staticProto['getStringFromAbiguous'],
								annotations: []
							}),
							new N4Method({
								name: 'generateFailureTestResult',
								isStatic: true,
								jsFunction: staticProto['generateFailureTestResult'],
								annotations: []
							}),
							new N4Method({
								name: 'handleFixme',
								isStatic: false,
								jsFunction: instanceProto['handleFixme'],
								annotations: []
							}),
							new N4Method({
								name: 'callAll',
								isStatic: false,
								jsFunction: instanceProto['callAll'],
								annotations: []
							}),
							new N4Method({
								name: 'getAncestorTestMethods',
								isStatic: false,
								jsFunction: instanceProto['getAncestorTestMethods'],
								annotations: []
							}),
							new N4Method({
								name: 'runTestAsync',
								isStatic: false,
								jsFunction: instanceProto['runTestAsync'],
								annotations: []
							}),
							new N4Method({
								name: 'runGroup',
								isStatic: false,
								jsFunction: instanceProto['runGroup'],
								annotations: []
							}),
							new N4Method({
								name: 'runTestsAsync',
								isStatic: false,
								jsFunction: instanceProto['runTestsAsync'],
								annotations: []
							}),
							new N4Method({
								name: 'errorTests',
								isStatic: false,
								jsFunction: instanceProto['errorTests'],
								annotations: []
							})
						],
						consumedMembers: [],
						annotations: []
					});
					return metaClass;
				});
				Object.defineProperty(TestExecutor, '$di', {
					value: {
						fieldsInjectedTypes: [
							{
								name: 'spy',
								type: TestSpy
							}
						]
					}
				});
			}
		};
	});
})(typeof module !== 'undefined' && module.exports ? require('n4js-node/index').System(require, module) : System);
//# sourceMappingURL=TestExecutor.map
