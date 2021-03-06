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
package org.eclipse.n4js.tests.typesbuilder.extensions

import com.google.inject.Inject
import org.eclipse.n4js.ts.types.TModule
import java.util.List
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.resource.IResourceDescriptions
import org.eclipse.xtext.resource.impl.ResourceDescriptionsProvider
import org.eclipse.xtext.xbase.lib.Pair

import static org.junit.Assert.*

/**
 */
class TypesAssertionsExtensions {

	@Inject
	private ResourceDescriptionsProvider resourceDescriptionsProvider;

	@Inject
	private extension TypeEqualityAssertionsExtension

	def assertAllExportedElementAreOnIndex(String phase, IResourceDescriptions resourceDescriptions, List<? extends Pair<? extends Class<? extends EObject>, String>> expectedExportedTypeToNamePairs) {
		val eoDescs = resourceDescriptions.allResourceDescriptions.filter[URI.fileExtension == "n4js"].map[exportedObjects].flatten
		val expectedTestContentCount = expectedExportedTypeToNamePairs.size // type roots
		assertEquals(phase + ": count of elements are marked as exported should equal to EResourceDescriptions", expectedTestContentCount, eoDescs.size)
		assertExpectedTypes(phase, eoDescs, expectedExportedTypeToNamePairs)
		eoDescs
	}

	def assertIndexHasBeenFilled(String phase, Resource testResource) {
		assertTrue(phase + ": " + testResource.errors.toString, testResource.errors.isEmpty)
		val resourceDescriptions = resourceDescriptionsProvider.getResourceDescriptions(testResource);
		assertFalse(phase + ": Test that the index has been filled", resourceDescriptions.allResourceDescriptions.empty)
		resourceDescriptions
	}

	def assertExpectedTypesCount(String phase, Resource testResource, int expectedTypesCount) {
		assertEquals(phase + ": test content count", 2, testResource.contents.size)
		val exportedScript = testResource.contents.last as TModule
		assertEquals(phase + ": Should have the expected count of types", expectedTypesCount, exportedScript.topLevelTypes.size + exportedScript.variables.size)
	}
}
