// Generated by N4JS transpiler; for copyright see original N4JS source file.

(function(System) {
	'use strict';
	System.register([
		'org.eclipse.n4js.mangelhaft.assert/org/eclipse/n4js/mangelhaft/assert/Assert',
		'org.eclipse.n4js.mangelhaft.assert/org/eclipse/n4js/mangelhaft/precondition/PreconditionNotMet'
	], function($n4Export) {
		var Assert, PreconditionNotMet, Precondition;
		Precondition = function Precondition() {
			Assert.prototype.constructor.call(this);
		};
		$n4Export('Precondition', Precondition);
		return {
			setters: [
				function($_import_org_u002eeclipse_u002en4js_u002emangelhaft_u002eassert_org_u002feclipse_u002fn4js_u002fmangelhaft_u002fassert_u002fAssert) {
					Assert = $_import_org_u002eeclipse_u002en4js_u002emangelhaft_u002eassert_org_u002feclipse_u002fn4js_u002fmangelhaft_u002fassert_u002fAssert.Assert;
				},
				function($_import_org_u002eeclipse_u002en4js_u002emangelhaft_u002eassert_org_u002feclipse_u002fn4js_u002fmangelhaft_u002fprecondition_u002fPreconditionNotMet) {
					PreconditionNotMet = $_import_org_u002eeclipse_u002en4js_u002emangelhaft_u002eassert_org_u002feclipse_u002fn4js_u002fmangelhaft_u002fprecondition_u002fPreconditionNotMet.PreconditionNotMet;
				}
			],
			execute: function() {
				$makeClass(Precondition, Assert, [], {}, {
					fail_: {
						value: function fail____n4(actual, expected, message, operator, stackStartFunction) {
							let error = new PreconditionNotMet(message);
							throw error;
						}
					}
				}, function(instanceProto, staticProto) {
					var metaClass = new N4Class({
						name: 'Precondition',
						origin: 'org.eclipse.n4js.mangelhaft.assert',
						fqn: 'org.eclipse.n4js.mangelhaft.precondition.Precondition.Precondition',
						n4superType: Assert.n4type,
						allImplementedInterfaces: [],
						ownedMembers: [
							new N4Method({
								name: 'fail_',
								isStatic: true,
								jsFunction: staticProto['fail_'],
								annotations: []
							})
						],
						consumedMembers: [],
						annotations: []
					});
					return metaClass;
				});
				Object.defineProperty(Precondition, '$di', {
					value: {
						superType: Assert,
						fieldsInjectedTypes: []
					}
				});
			}
		};
	});
})(typeof module !== 'undefined' && module.exports ? require('n4js-node/index').System(require, module) : System);
//# sourceMappingURL=Precondition.map
