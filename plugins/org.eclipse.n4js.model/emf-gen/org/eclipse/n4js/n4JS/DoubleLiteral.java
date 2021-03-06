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
package org.eclipse.n4js.n4JS;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Double Literal</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see org.eclipse.n4js.n4JS.N4JSPackage#getDoubleLiteral()
 * @model
 * @generated
 */
public interface DoubleLiteral extends NumericLiteral {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model unique="false"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='<%java.math.BigDecimal%> _value = this.getValue();\nreturn _value.doubleValue();'"
	 * @generated
	 */
	double toDouble();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation" unique="false"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='<%java.math.BigDecimal%> _value = this.getValue();\nboolean _tripleEquals = (_value == null);\nif (_tripleEquals)\n{\n\treturn null;\n}\n<%java.math.BigDecimal%> _value_1 = this.getValue();\nreturn _value_1.toString();'"
	 * @generated
	 */
	String getValueAsString();

} // DoubleLiteral
