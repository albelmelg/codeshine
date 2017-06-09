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
 * <p>
 * Enables sorting facility for <code>Table</code> instances using an
 * arbitrary <code>TableViewerSorter</code> for sorting.
 * </p>
 * 
 * @author <a href="mailto:smachhau@users.sourceforge.net">Sebastian Machhausen</a>
 */
public class TableViewerSorterHandler extends SelectionAdapter {

	/**
	 * <p>
	 * The <code>Table</code> that the <code>TableViewerSorter</code> is
	 * bound to
	 * </p>
	 */
	private Table table;
	
	/**
	 * <p>
	 * The <code>TableViewerSorter</code> to use for sorting
	 * </p>
	 */
	private TableViewerSorter sorter;

	
	/**
	 * <p>
	 * Creates a new <code>TableViewerSorterHandler</code> instance and
	 * binds it to the specified <code>Table</code> using the given
	 * <code>TableViewerSorter</code> to sort the model elements.
	 * </p>
	 * 
	 * @param table the <code>Table</code> to bind this
	 * <code>TableViewerSorterHandler</code> to
	 * @param sorter the <code>TableViewerSorter</code> to use to
	 * sort the model elements
	 */
	public TableViewerSorterHandler(Table table, TableViewerSorter sorter) {
		this.table = table;
		this.sorter = sorter;
		this.registerColumns();
	} // end constructor TableViewerSorterHandler(Table, TableViewerSorter)
	
	
	/**
	 * <p> Disposes this <code>TableViewerSorterHandler</code>.	 
	 */
	public void dispose() {
		this.unregisterColumns();
	} // end method dispose()
	
	
	/**
	 * <p>
	 * Handles the <code>SelectionEvent</code> being triggered when the
	 * sorting column and/or order of the <code>Table</code> changes. The
	 * sorting of the underlying model is done using the selected column to sort
	 * by. The sort direction is reversed, i.e. from ascending to descending and
	 * reverse.
	 * </p>
	 * 
	 * @param event Event the <code>SelectionEvent</code> triggered
	 */
	public void widgetSelected(SelectionEvent event) {		
		int columnIndex = this.table.indexOf((TableColumn) event.widget);
		this.sort(columnIndex);
	} // end method widgetSelected(SelectionEvent)


	/**
	 * <p>
	 * Sorts the underlying model by the specified column. The sort
	 * direction is reversed.
	 * </p>
	 * 
	 * @param columnIndex int the index of the column to sort
	 */
	public void sort(int columnIndex) {
		this.sort(columnIndex, !this.sorter.isAscending());
	} // end method sort(int)


	/**
	 * <p>
	 * Sorts the underlying model by the specified <code>columnIndex</code>.
	 * </p>
	 * 
	 * @param columnIndex int the index of the column to sort
	 * @param ascending <code>true</code> for ascending, <code>false</code>
	 * for descending sort order
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
	 * <p>
	 * Registers all <code>TableColumns</code> to sort on header single mouse
	 * click. Each single mouse click on the same
	 * <code> <code>TableColumns</code>
	 * reverses the sort order.
	 * </p>
	 */
	private void registerColumns() {
		TableColumn[] columns = this.table.getColumns();
		for (int i = 0; i < columns.length; i++) {
			columns[i].addSelectionListener(this);
		}
	} // end method registerColumns()
	
	
	/**
	 * <p>
	 * Unregisters all <code>TableColumns</code> from this
	 * <code>TableViewerSorterHandler</code>.
	 * </p>
	 */
	private void unregisterColumns() {
		TableColumn[] columns = this.table.getColumns();
		for (int i = 0; i < columns.length; i++) {
			columns[i].removeSelectionListener(this);
		}
	} // end method unregisterColumns()
	
	
} // end class TableViewerSortHandler