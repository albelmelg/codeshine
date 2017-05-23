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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import codeshine.utils.StringUtils;

/**
 * <p>
 * Generic <code>ViewerSorter</code> extension for <code>Viewer</code>
 * instances using <code>ITableContentProvider</code> implementations.
 * </p>
 * 
 * @author <a href="mailto:smachhau@users.sourceforge.net">Sebastian Machhausen</a>
 */
public class TableViewerSorter extends ViewerSorter {


    /** <p> The column that the sorting is done by </p> */
    private int sortingColumn;
    
    /**
     * <p>
	 * <code>true</code> indicates ascending (default), <code>false</code>
	 * descending sort order
	 * </p>
	 */
    private boolean ascending = true;
    
    /**<p> The <code>Viewer</code> that the sorting is done for </p> */
    private Viewer viewer;
    
    /**
	 * <p>
	 * The <code>ITableContentProvider</code> used to query the underlying
	 * model
	 * </p>
	 */
    private ITableContentProvider contentProvider;


    /**
	 * <p>
	 * Creates a new <code>TableViewerSorter</code> instance linked to the
	 * specified <code>Viewer</code>.
	 * </p>
	 * 
	 * @param viewer the <code>Viewer</code> to link this
	 * <code>TableViewerSorter</code> to
	 */
    public TableViewerSorter(Viewer viewer,
			ITableContentProvider contentProvider) {
        this.viewer = viewer;
        this.contentProvider = contentProvider;
    } // end constructor TableViewerSorter(Viewer, ITableContentProvider)


    /**
     * <p>
     * Gets the column index by which the sorting is done.
     * </p>
     * 
     * @return the column index by which the sorting is done
     * 
     * @see #getSortingColumn()
     */
    public int getSortingColumn() {
        return (this.sortingColumn);
    } // end method getSortingColumn()


    /**
     * <p>
     * Sets the column index by which the sorting is to be done.
     * </p>
     * 
     * @param columnIndex the column index by which the sorting is to be done
     * 
     * @see #getSortingColumn()
     */
    public void setSortingColumn(int columnIndex) {
        this.sortingColumn = columnIndex;
    } // end method setSortingColumn(int)


    /**
     * <p>
     * Gets the sort order; <code>true<Code> indicates ascending,
     * <code>false</code> descending sort order.
     * </p>
     *
     * @return <code>true<Code> for ascending, <code>false</code> for descending
     * sort order
     * 
     * @see #setAscending(boolean)
     */
    public boolean isAscending() {
        return (this.ascending);
    } // end method isAscending()


    /**
     * <p>
     * Sets the sort order to be used; <code>true<Code> indicates ascending,
     * <code>false</code> descending sort order.
     * </p>
     *
     * @param ascending <code>true<Code> for ascending, <code>false</code> for
     * descending sort order
     * 
     * @see #isAscending()
     */
    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    } // end method setAscending(boolean)


    /**
     * <p>
     * Sorts the underlying model data and refreshes the associated
     * <code>TableViewer</code> to reflect the new sorting.
     * </p>
     */
    public void sort() {
        this.viewer.refresh();
    } // end method sort()


    /**
	 * <p>
	 * Returns a negative, zero, or positive number depending on whether the
	 * first element is less than, equal to, or greater than the second element.
	 * </p>
	 * 
	 * @param viewer the viewer
	 * @param e1 the first element
	 * @param e2 the second element
	 * 
	 * @return a negative number if the first element is less than the second
	 * element; the value <code>0</code> if the first element is equal to the
	 * second element; and a positive number if the first element is greater
	 * than the second element
	 */
    public int compare(Viewer viewer, Object e1, Object e2) {
    	/* Evaluate element categories first */
    	int category1 = this.category(e1);
        int category2 = this.category(e2);
        if (category1 != category2) {
			return (category1 - category2);
		}
        
        /* Get the value of the first argument for the current sorting column
         * and prevent null values. */
        Object value1 = this.contentProvider.getColumnValue(e1, this
				.getSortingColumn());
              
        /* Get the value of the second argument for the current sorting column
         * and prevent null values. */
        Object value2 = this.contentProvider.getColumnValue(e2, this
                .getSortingColumn());        
                
        if (value1 instanceof String && value2 instanceof String) {
            /* Prevent null values */
            if (value1 == null) {
                value1 = StringUtils.EMPTY_STRING;
            }
            if (value2 == null) {
                value2 = StringUtils.EMPTY_STRING;
            }
            
            /* Compare two String objects with the internal Collator */
            return (this.isAscending()
        			? this.collator.compare(value1, value2)
        			: (-this.collator.compare(value1, value2)));
        } else {                        
            if (value1 == null && value2 == null) {
                /* Consider both values to be equal. */
                return (0);
            } else if (value1 != null && value2 == null) {         
                /* Always consider value1 as the non null value greater
                 * than value2 as the null value. The sort order is 
                 * ignored in this case. */                       
                return (-1);
            } else if (value1 == null && value2 != null) {         
                /* Always consider value2 as the non null value greater
                 * than value1 as the null value. The sort order is 
                 * ignored in this case. */                      
                return (1);
            } else if (value1 instanceof Comparable
					&& value2 instanceof Comparable) {
                /* Compare value1 and value2 based on the Comparable 
                 * compareTo(Object) method. */
                return (this.isAscending()                
                        ? ((Comparable) value1).compareTo(value2)           
                        : -((Comparable) value1).compareTo(value2));                        
            } else {
            	/* Convert both Objects to String objects and make use of 
            	 * the internal Collator */
            	return (this.isAscending()
            			? this.collator.compare(value1, value2)
            			: (-this.collator.compare(value1, value2)));		
            }
        }
    } // end method compare(Viewer, Object, Object)

    
} // end class TableViewerSorter