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

/**
 * Extension generica de ViewerSorte para Viewer
 * las instancias usan implementaciones de ITableContentProvider .
 * @deprecated El extends ViewerSorter ya no está en uso
 * 
 * @author <a href="mailto:smachhau@users.sourceforge.net">Sebastian Machhausen</a>
 */
@SuppressWarnings("deprecation")
public class TableViewerSorter extends ViewerSorter {


    /* The column that the sorting is done by */
    private int sortingColumn;
    
    /*     
	 * true indicates ascending (default), false
	 * descending sort order
	 * 
	 */
    private boolean ascending = true;
    
    /*The Viewer that the sorting is done for */
    private Viewer viewer;
    
    /*
	 * The ITableContentProvider used to query the underlying
	 * model
	 */
    private ITableContentProvider contentProvider;


    /**
	 * Crea una nueva instancia de TableViewerSorter enlazada a un especifico 
	 * Viewer
	 * @param viewer El Viewer que se enlaza al 
	 * TableViewerSorter
	 * @param contentProvider Contenido proporcionado
	 * @deprecated 
	 */
    public TableViewerSorter(Viewer viewer,
			ITableContentProvider contentProvider) {
        this.viewer = viewer;
        this.contentProvider = contentProvider;
    } // end constructor TableViewerSorter(Viewer, ITableContentProvider)


    /**
     * 
     * Obtiene el indice de la columna para el cual se ha hecho
     * una clasificacion
     * 
     * @return El indice de la columna para el que se ha hecho la clasificacion
     * 
     * @see #getSortingColumn()
     */
    public int getSortingColumn() {
        return (this.sortingColumn);
    } // end method getSortingColumn()


    /**
     * 
     * Establece el índice de columna por el cual se va a realizar la clasificacion.

     * @param columnIndex El indice de la columna por el cual se va a hacer la clasificacion
     * 
     * @see #getSortingColumn()
     */
    public void setSortingColumn(int columnIndex) {
        this.sortingColumn = columnIndex;
    } // end method setSortingColumn(int)


    /**
     * 
     * Devuelve el tipo de ordenacion, True indica ascendente,
     * False, descendente
     * @return True para ascendente, False para descendente
     * 
     * @see #setAscending(boolean)
     */
    public boolean isAscending() {
        return (this.ascending);
    } // end method isAscending()


    /**
     * Establece el tipo de ordenacion que se va a usar
     * True indica ascendente y Falso descendente
     *
     * @param ascending True para ascendente, False para descendente
     * 
     * @see #isAscending()
     */
    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    } // end method setAscending(boolean)


    /**
	Ordena los datos del modelo subyacente y actualiza los datos asociados del
     * TableViewer para reflejar la nueva clasificación.
     */
    public void sort() {
        this.viewer.refresh();
    } // end method sort()


    /**
	 * Devuelve un numero negativo, cero o positivo dependiendo de sobre si el
	 * Primer elemento es menor que, igual o mayor que el segundo elemento.
	 * 
	 * @param viewer El Viewer
	 * @param e1 El primer elemento
	 * @param e2 El segundo elemento
	 * 
	 * @return Un numero negativo si el primer elemento es menor que el segundo;
	 *  0 si el primer y el segundo elemento son iguales; y un numero positivo si el segundo
	 *  elemento es mayor al primero.
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