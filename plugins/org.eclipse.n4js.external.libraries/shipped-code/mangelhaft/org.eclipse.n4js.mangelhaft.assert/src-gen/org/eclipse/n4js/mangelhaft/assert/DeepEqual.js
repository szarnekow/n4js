// Generated by N4JS transpiler; for copyright see original N4JS source file.

(function(System) {
	'use strict';
	System.register([], function($n4Export) {
		var deepEqual, deepEqualRec;
		deepEqual = function deepEqual(actual, expected, ignorePrototype) {
			const shouldIgnorePrototype = (ignorePrototype == undefined) ? true : ignorePrototype;
			return deepEqualRec(actual, expected, new WeakMap(), shouldIgnorePrototype);
		};
		$n4Export('deepEqual', deepEqual);
		deepEqualRec = function deepEqualRec(actual, expected, visited, ignorePrototype) {
			if (!($instanceof(actual, Object)) || !($instanceof(expected, Object))) {
				return actual === expected;
			}
			const actualObj = actual;
			const expectedObj = expected;
			if (!ignorePrototype) {
				const actualPrototype = Object.getPrototypeOf(actualObj);
				const expectedPrototype = Object.getPrototypeOf(expectedObj);
				if (actualPrototype !== expectedPrototype) {
					return false;
				}
			}
			const actualProperties = Object.keys(actualObj);
			const expectedProperties = Object.keys(expectedObj);
			if (actualProperties.length != expectedProperties.length) {
				return false;
			}
			actualProperties.sort();
			expectedProperties.sort();
			for(var i = 0;i < actualProperties.length;i++) {
				if (actualProperties[i] !== expectedProperties[i]) {
					return false;
				}
			}
			const elem = visited.get(actualObj);
			if (elem) {
				return elem == expectedObj;
			}
			visited.set(actualObj, expectedObj);
			for(i = 0;i < actualProperties.length;i++) {
				const isEqual = deepEqualRec(actualObj[actualProperties[i]], expectedObj[actualProperties[i]], visited, ignorePrototype);
				if (!isEqual) {
					return false;
				}
			}
			visited.delete(actualObj);
			return true;
		};
		return {
			setters: [],
			execute: function() {}
		};
	});
})(typeof module !== 'undefined' && module.exports ? require('n4js-node').System(require, module) : System);
//# sourceMappingURL=DeepEqual.map