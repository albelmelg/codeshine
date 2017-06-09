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


import org.eclipse.swt.SWT;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import codeshine.utils.TableViewerSorter;


/**
 * Permite la facilidad para las instancias de Table usando un
 * arbitrario TableViewerSorter para ordenar.
 * 
 * @author <a href="mailto:smachhau@users.sourceforge.net">Sebastian Machhausen</a>
 */
public class TableViewerSorterHandler extends SelectionAdapter {

	//The Table that the TableViewerSorter is bound to
	private Table table;

	//The TableViewerSorter a usar para ordenar
	 private TableViewerSorter sorter;

	
	/**
	 * Crea una nueva instancia de TableViewerSorterHandler y lo enlaza
	 * en la Table especificada usando el TableViewerSorter que se le pasa
	 * para ordenar el modelo de elementos
	 * 
	 * @param table La Table que lo enlaza al
	 * TableViewerSorterHandler
	 * @param clasifica el TableViewerSorter para usarlo para ordenar
	 * el modelo de elementos
	 */
	public TableViewerSorterHandler(Table table, TableViewerSorter sorter) {
		this.table = table;
		this.sorter = sorter;
		this.registerColumns();
	} // end constructor TableViewerSorterHandler(Table, TableViewerSorter)
	
	
	/**
	 * Coloca el TableViewerSorterHandler 
	 */
	public void dispose() {
		this.unregisterColumns();
	} // end method dispose()
	
	
	/**
	 * Maneja el SelectionEvent que se dispara cuando la columna de clasificacion
	 * y/o ordena al Table cambiar.
	 * La clasificacion del modelo subyacente se usa para selecionar la columna a ordenar
	 * El orden está reservado, por ejemplo, de ascendente a descendente o al contrario.
	 * 
	 * @param event El evento SelectionEvent que se dispara
	 */
	public void widgetSelected(SelectionEvent event) {		
		int columnIndex = this.table.indexOf((TableColumn) event.widget);
		this.sort(columnIndex);
	} // end method widgetSelected(SelectionEvent)


	/**
	 * 
	 * Ordena el modelo subyacente por la columna que se le pasa.
	 * La manera de ordenarlo esta al reves
	 * 
	 * 
	 * @param columnIndex int the index of the column to sort
	 */
	public void sort(int columnIndex) {
		this.sort(columnIndex, !this.sorter.isAscending());
	} // end method sort(int)


	/**
	 *
	 * Ordena el modelo subyacente por la especifica columnIndex
	 * 
	 * @param columnIndex El numero de la columna a ordenar.
	 * @param True para ascendente, False para descendente.
	 */
	public void sort(int columnIndex, boolean ascending) {
		this.sorter.setSortingColumn(columnIndex);
		this.sorter.setAscending(ascending);
		this.sorter.sort();

		TableColumn column = this.table.getColumn(columnIndex);
		this.table.setSortColumn(column);
		this.table.setSortDirection(sorter.isAscending() ? SWT.UP : SWT.DOWN);
	} // end method sort(int, boolean)
	
	
	/**
	 * Registra todas las TableColumns a ordenar en la cabecera de un solo click
	 * Cada click en el mismo TableColumns invierte el orden.
	 *
	 */
	private void registerColumns() {
		TableColumn[] columns = this.table.getColumns();
		for (int i = 0; i < columns.length; i++) {
			columns[i].addSelectionListener(this);
		}
	} // end method registerColumns()
	
	
	/**
	 *
	 * Anula el registro de todas las TableColumns de este
	 * TableViewerSorterHandler
	 * 
	 */
	private void unregisterColumns() {
		TableColumn[] columns = this.table.getColumns();
		for (int i = 0; i < columns.length; i++) {
			columns[i].removeSelectionListener(this);
		}
	} // end method unregisterColumns()
	
	
} // end class TableViewerSortHandler