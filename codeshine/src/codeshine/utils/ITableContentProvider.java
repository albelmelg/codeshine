/*******************************************************************************
 * Copyright notice                                                            *
 *                                                                             *
 * Copyright (c) 2005-2006 Feed'n Read Development Team                        *
 * http://sourceforge.net/fnr                                                  *
 *                                                                             *
 * All rights reserved.                                                        *
 *                                                                             *
 * This program and the accompanying materials are made available under the    *
 * terms of the Common Public License v1.0 which accompanies this distribution,*
 * and is available at                                                         *
 * http://www.eclipse.org/legal/cpl-v10.html                                   *
 *                                                                             *
 * A copy is found in the file cpl-v10.html and important notices to the       *
 * license from the team is found in the textfile LICENSE.txt distributed      *
 * in this package.                                                            *
 *                                                                             *
 * This copyright notice MUST APPEAR in all copies of the file.                *
 *                                                                             *
 * Contributors:                                                               *
 *    Feed'n Read - initial API and implementation                             *
 *                  (smachhau@users.sourceforge.net)                           *
 *******************************************************************************/
package codeshine.utils;

import org.eclipse.jface.viewers.IStructuredContentProvider;


/**
 * Interfaz que se sirve para dar un soporte generico a JFace.
 * @author <a href="mailto:smachhau@users.sourceforge.net">Sebastian Machhausen</a>
 * 
 */
public interface ITableContentProvider extends IStructuredContentProvider {

	
	/**
     * Devuelve el valor de la columna para un objeto especificado
     * en el indice de la columna que se le pasa.
     * 
     * @param element - El elemento del modelo para el cual consultar el valor de la columna
     * @param columnIndex el indice de la columna
     * 
     * @return el valor del elemento en ese columnIndex
     * 
     */
    public Object getColumnValue(Object element, int columnIndex);
    
    
} 