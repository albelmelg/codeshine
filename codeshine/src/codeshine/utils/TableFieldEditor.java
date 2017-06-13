/*******************************************************************************
 * Copyright notice                                                            *
 *                                                                             *
 * Copyright (c) 2005-2006 Feed'n Read Development Team                             *
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


import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import codeshine.preferences.IPreferenceConstants;


/**
 * Una implementacion de FieldEditor que soporta la selección de datos tabulares.
 * @author <a href="mailto:Sebastian.Machhausen@gmail.com">Sebastian Machhausen</a>
 */
public class TableFieldEditor extends FieldEditor implements Accessible{

    // The Table used to present the selectable tabular data
    private Table table;
    
    //The TableViewer used as controller
    private TableViewer viewer;
    
    // The content provider used to query the table data
    private IStructuredContentProvider contentProvider;
    
    /*The label provider used to convert domain objects to ui specific
      textual representations*/
     private ITableLabelProvider labelProvider;
     
   // The input or model object that holds the data of the TableViewer
     private Object input;
    
    // The column headers to display in the Table
    private String[] columnHeaders;
    
    // The Table sorter 
    @SuppressWarnings("deprecation")
	private TableViewerSorter sorter;

	//The handler used to sort the underlying IStructuredContentProvider</code>
    private TableViewerSorterHandler sorterHandler;
    

    /*The index of the column to store/retrieve the value for. If set to -1 the
	 * complete row represented as domain object is stored/retrieved. This is
	 * done by calling toString() on the respective domain object.*/
     private int selectionColumn;
    
    //The last selected value of the Table
    private Object oldValue;
    
    
    /**
     * Crea una nueva instancia de TableFieldEditor
     * 
     * @param name El nombre con el que trabaja el FielEditor
     * @param labelText El text label del FieldEditor
     * @param parent El 'padre' que tiene el control del FieldEditor
     * @param contentProvider El IStructuredContentProvider usado para
     * consultar los valores de la tabla.
     *
     * @param labelProvider El ITableLabelProvider usado para
     * convertir objetos de dominio a ui representaciones textuales especificas.
     * @param columnHeaders Un array de objetos String que representa
     * la cabecera de las columnas.
     * @param input El objeto de entrada o modelos que contiene los datos para
     * este FieldEditor
     */
    public TableFieldEditor(final String name, final String labelText, final Composite parent,
            IStructuredContentProvider contentProvider,
            ITableLabelProvider labelProvider, String[] columnHeaders, Object input) {        
    	this.contentProvider = contentProvider;
        this.labelProvider = labelProvider;
        this.columnHeaders = columnHeaders;
        this.input = input;
        this.init(name, labelText);
        this.createControl(parent);
    } 
    
    /**
     * Devuelve el número de controles que tiene el TableFieldEditor
     * Devuelve 1 si Table es el único que tiene el control.
     * @see org.eclipse.jface.preference.FieldEditor#getNumberOfControls()
     */
    public int getNumberOfControls() {       
        return 1;
    } 
    
    
    /**
     * Establece la columna que se selecciona en el columnIndex especificado 
     * El índice representa la columna cuyo valor se almacena / recupera en este
     * TableFieldEditor. 
     * @param columnIndex La columna cuyo valor se almacena/recupera en el
     * TableFieldEditor
     * 
     * @see #getSelectionColumn()
     */
    public void setSelectionColumn(int columnIndex) {
        this.selectionColumn = columnIndex;
    }
    
    
    /**
     * Devuelve la seleccion de la columna que se representa en el indice cuyo
     * valor se almacena/recupera en este TableFieldEditor.
     * El valor -1 significa que la columna del objeto del dominio está 
     * almacenada /recuperada. Esto se logra llamando a
     * {@link #toString()} en el respectivo objeto de dominio.
     * 
     * @return la columna cuyo valor es almacenado / recuperado en este
     * TableField
     * 
     * @see #setSelectionColumn(int)
     */
    public int getSelectionColumn() {
        return this.selectionColumn;
    }
    
    
    /**
     * Devuelve el valor seleccionado del TableFieldEditor
     * El valor retornado en este metodo depende de la columna seleccionada
     * devuelta por {@link #getSelectionColumn()}. Si la columna seleccionada
     * devuelve -1 la fila completa representada por los objetos de dominio
     * es devuelta llamando a {@link #toString()} 
     * De lo contrario el valor de la columna respectiva se consulta y se devuelve
     * usando el ITableLabelProvider vinculado al TableFieldEditor.

     * @return El valor actualmente seleccionado o un String vacio si no hay seleccion 
     * 
     * @see #setSelectionColumn(int)
     * @see #getSelectionColumn()
     */
    public String getSelection() {
        IStructuredSelection selection = 
            (IStructuredSelection) this.viewer.getSelection();
        if (selection.isEmpty()) {
            /* Empty selection */        
            return StringUtils.EMPTY_STRING;
        } else if (this.selectionColumn == -1) {
            /* Row selection */
            return selection.getFirstElement().toString();
        } else {
            /* Column selection */
            return this.labelProvider.getColumnText(selection
                    .getFirstElement(), this.selectionColumn);
        }
    }
    
    
    /**
	 * Habilita o deshabilita la ordenacion de tabla dependiendo del status
	 * El IStructuredContentProvider utilizado en este
	 * TableFieldEditor tiene que implementar la interfaz para 
	 * habilitar la clasificacion
	 * 
	 * @param enabled True para habilitar la clasificacion; Falso para deshabilitarlo

	 * @see #isSortingEnabled()
	 */
    @SuppressWarnings("deprecation")
	public void setSortingEnabled(boolean enabled) {
    	if (this.contentProvider instanceof ITableContentProvider) {
    		if (enabled) {
	    		this.sorter = new TableViewerSorter(this.viewer,
	    					(ITableContentProvider) this.contentProvider);
	    		this.sorterHandler = 
	    			new TableViewerSorterHandler(this.table, this.sorter);
	    	
    		} else {
    			this.sorter = null;
    			this.sorterHandler = null;
    			 			
    		}
    	}
    } 
    /**
     * Devuelve True si la clasificación está habilitada y False en caso contrario
     * @return True si la clasificacion esta habilitada y False en caso contrario
     * 
     * @see #setSortingEnabled(boolean)
     */
    public boolean isSortingEnabled() {
    	return this.sorterHandler != null;
    } 
    
    /**
	 * Ordena la Table en el valor especificado en columIndex en un
	 * orden especifico. Si la clasificacion esta deshabilitada este metodo no hace nada
	 * 
	 * @param columnIndex El indice de la columna a ordenar
	 * @param ascending True para ordenarlo en ascendente
	 *  o false para que sea en descendente.
	 * 
	 * @see #setSortingEnabled(boolean)
	 * @see #isSortingEnabled()
	 * @see #getSortingColumn()
	 * @see #isSortAscending()
	 */
    public void sort(int columnIndex, boolean ascending) {
    	if (this.isSortingEnabled()) {
    		this.sorterHandler.sort(columnIndex, ascending);
    	}
    } 
    
    
    /**
	 * Devuelve el valor de la columna que se va a ordenar.
	 * Si la ordenación está deshabilitada se devueve -1
	 * 
	 * @return El indice de la columna que se ha ordenado; O -1 
	 * si está desactivada la clasificacion
	 * 
	 * @see #isSortAscending()
	 * @see #sort(int, boolean)
	 */
    @SuppressWarnings("deprecation")
	public int getSortingColumn() {
    	if (this.isSortingEnabled()) {
    		return this.sorter.getSortingColumn();
    	} else {
    		return -1;
    	}
    } 
    
    
    /**
	 * Devuelve True si se ha ordenado de forma ascendente
	 * Falso si se ha ordenado de forma descendente o la clasificacion
	 * está deshabilitada

	 * @return True si se ha ordenado de forma ascendente
	 * False si se ha ordenado de forma descendente o la clasificacion
	 * esta deshabilitada
	 * 
	 * @see #getSortingColumn()
	 * @see #sort(int, boolean)
	 */
    @SuppressWarnings("deprecation")
	public boolean isSortAscending() {
    	if (this.isSortingEnabled()) {
    		return this.sorter.isAscending();
    	} else {
    		return false;
    	}
    } 
    

    /**
	 * Establece el ancho de la columna especificada en el columnIndex.
	 * Si no existe una TableColumn en la columnIndez el metodo no hace nada
	 * 
	 * @param columnIndex el indice de la columna
	 * @param width la anchura en pixeles
	 * 
	 * @see #getColumnWidth(int)
	 */
    public void setColumnWidth(int columnIndex, int width) {
    	if (columnIndex >= 0 && columnIndex < this.columnHeaders.length) {
    		TableColumn column = this.table.getColumn(columnIndex);
    		column.setWidth(width);
    	}
    } 
    
    
    /**
	 * Devuelve la anchura en pixeles de la columna que se especifica en columnIndex
	 * Si no encuentra una TableColum en la columnIndex
	 * el metodo devuelve 0
	 * 
	 * @param columnIndex El indice de la columna de la que se quiere saber la anchura
	 * 
	 * @return La anchura de la columna
	 * 
	 * @see #setColumnWidth(int, int)
	 */
    public int getColumnWidth(int columnIndex) {
    	if (columnIndex >= 0 && columnIndex < this.columnHeaders.length) {
    		TableColumn column = this.table.getColumn(columnIndex);
    		return column.getWidth();
    	} else {
    		return 0;
    	}
    } 
    
    
    /*
     * Adjusts the horizontal span of this TableFieldEditor's basic
     * controls. The number of columnHeaders will always be equal to or greater than
     * the value returned by this editor's getNumberOfControls
     * method.
     *  
     * @param numColumns the number of columnHeaders
     * 
     * @see org.eclipse.jface.preference.FieldEditor#adjustForNumColumns(int)
     */
    protected void adjustForNumColumns(int numColumns) {
        GridData gd = (GridData) this.table.getLayoutData();
        gd.horizontalSpan = numColumns - 1;       
        gd.grabExcessHorizontalSpace = gd.horizontalSpan <= 1;
    } // end method adjustForNumColumns(int)


    /*
     * 
     * Fills this TableFieldEditor's basic controls into the
     * given parent.
     * 
     * 
     * @param parent the composite used as a parent for the basic controls; the
     * parent's layout must be a <code>GridLayout</code>
     * @param numColumns the number of columnHeaders
     * 
     * @see org.eclipse.jface.preference.FieldEditor#doFillIntoGrid(org.eclipse.swt.widgets.Composite,
     * int)
     */
    protected void doFillIntoGrid(final Composite parent, final int numColumns) {
        this.getLabelControl(parent);
        
        this.table = new Table(parent, SWT.FULL_SELECTION | SWT.BORDER
                | SWT.V_SCROLL | SWT.H_SCROLL);       
        this.table.setHeaderVisible(true);
        this.table.setLinesVisible(false);
        this.table.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent event) {               
                valueChanged();          
            } 
            public void widgetSelected(SelectionEvent event) {
                valueChanged();
            } 
        });        
        
        this.initializeColumns();
        this.initializeViewer();
        
        final GridData gd = new GridData();
        gd.horizontalSpan = numColumns - 1;
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        this.table.setLayoutData(gd);
        this.table.setLinesVisible(true);     
        final TableCursor cursor = new TableCursor(table, SWT.NONE);
        final ControlEditor editor = new ControlEditor(cursor);
        editor.grabHorizontal = true;
        editor.grabVertical = true;
       
        cursor.addSelectionListener(new SelectionAdapter() {
          // when the TableEditor is over a cell, select the corresponding row in
          // the table
          public void widgetSelected(SelectionEvent e) {
            table.setSelection(new TableItem[] { cursor.getRow() });
            selectionColumn = cursor.getColumn();
          }

          // when the user hits "ENTER" in the TableCursor, pop up a text editor so
          // that
          // they can change the text of the cell
          public void widgetDefaultSelected(SelectionEvent e) {
            final Text text = new Text(cursor, SWT.NONE);
            TableItem row = cursor.getRow();
            int column = cursor.getColumn();
            selectionColumn = column;
            if (row.getText(column).equals(IPreferenceConstants.SPECIAL))
            	text.setText("");
            else
            	text.setText(row.getText(column));
            text.addKeyListener(new KeyAdapter() {
              public void keyPressed(KeyEvent e) {
                // close the text editor and copy the data over
                // when the user hits "ENTER"
                if (e.character == SWT.CR) {
                  TableItem row = cursor.getRow();
                  int column = cursor.getColumn();
                  String oldText = row.getText(column);
                  row.setText(column, text.getText());
                  updateInput(column, row, oldText);
                  text.dispose();
                  selectionColumn = cursor.getColumn();
                  cursor.setFocus();
                }
               }
//              Chapú para actualizar el objeto input.
              public void updateInput(int column, TableItem row, String old){
            	  TokenList tl = (TokenList)input;
            	  if (old.equals(IPreferenceConstants.SPECIAL) && row.getText(0).equals("")){
            		  Token t = new Token(IPreferenceConstants.SPECIAL, row.getText(1), row.getText(2));
            		  tl.addToken(t);
	              }
            	  else{
            		  if (tl.getCollection().containsKey(old)){
            			  tl.removeToken(old);
            		  }
            		  if (!row.getText(0).equals("")){
            			  Token t = new Token(row.getText(0), row.getText(1), row.getText(2));
            			  tl.addToken(t);
            		  }
            	  }
            	  System.out.println("TableFieldEditor: " + tl.toString());
            	  viewer.setInput(tl);
              }
            });
            // close the text editor when the user tabs away
            text.addFocusListener(new FocusAdapter() {
              public void focusLost(FocusEvent e) {
                text.dispose();
                selectionColumn = cursor.getColumn();
              }
            });
            editor.setEditor(text);
            text.setFocus();
            
           }
        });
        // Hide the TableCursor when the user hits the "CTRL" or "SHIFT" key.
        // This alows the user to select multiple items in the table.
        cursor.addKeyListener(new KeyAdapter() {
          public void keyPressed(KeyEvent e) {
            if (e.keyCode == SWT.CTRL || e.keyCode == SWT.SHIFT || (e.stateMask & SWT.CONTROL) != 0
                || (e.stateMask & SWT.SHIFT) != 0) {
              cursor.setVisible(false);
            }
          }
        });
        
        // Show the TableCursor when the user releases the "SHIFT" or "CTRL" key.
        // This signals the end of the multiple selection task.
        table.addKeyListener(new KeyAdapter() {
          public void keyReleased(KeyEvent e) {
            if (e.keyCode == SWT.CONTROL && (e.stateMask & SWT.SHIFT) != 0)
              return;
            if (e.keyCode == SWT.SHIFT && (e.stateMask & SWT.CONTROL) != 0)
              return;
            if (e.keyCode != SWT.CONTROL && (e.stateMask & SWT.CONTROL) != 0)
              return;
            if (e.keyCode != SWT.SHIFT && (e.stateMask & SWT.SHIFT) != 0)
              return;

            TableItem[] selection = table.getSelection();
            final TableItem row = (selection.length == 0) ? table.getItem(table.getTopIndex()) : selection[0];
            table.showItem(row);
            cursor.setSelection(row, 0);
            cursor.setVisible(true);
            cursor.setFocus();
            selectionColumn = cursor.getColumn();
          }
        });
        
        
    } // end method doFillIntoGrid()


    /*
     * <p>
     * Initializes this <code>TableFieldEditor</code> with the
     * preference value from the preference store. 
     * </p>
     * 
     * @see org.eclipse.jface.preference.FieldEditor#doLoad()
     */
    protected void doLoad() {   
        String value = this.getPreferenceStore().getString(
                this.getPreferenceName());        
        this.setSelection(value);
        this.oldValue = value; 
    } // end method doLoad()


    /*
     * <p>
     * Initializes this <code>TableFieldEditor</code> with the
     * default preference value from the preference store.
     * </p>
     * 
     * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
     */
    protected void doLoadDefault() {   
        String defaultValue = this.getPreferenceStore().getDefaultString(
                this.getPreferenceName());
        this.setSelection(defaultValue);
        this.valueChanged();
    } // end method doLoadDefault()


    /*
     * 
     * Stores the preference value from this
     * <code>TableFieldEditor</code> into the preference store.
     * 
     * 
     * @see org.eclipse.jface.preference.FieldEditor#doStore()
     */
    protected void doStore() {   
    	this.valueChanged();
        this.getPreferenceStore().setValue(this.getPreferenceName(),
                this.getSelection());        
    } // end method doStore()

    
    /*
     * 
     * Informs this field editor's listener, if it has one, about a change to
     * the value (<code>VALUE</code> property) provided that the old and new
     * values are different. This hook is <em>not</em> called when the value is
     * initialized (or reset to the default value) from the preference store.
     * 
     */
    protected void valueChanged() {
    	
    	System.out.println("Invocado ValueChanged");
    	this.setPresentsDefaultValue(false);
    	
    	
        final IStructuredSelection selection = 
            (IStructuredSelection) this.viewer.getSelection();
        
        String newValue;
//        System.out.println("NewVAlue: " + newValue);
        if (selection.isEmpty()) {
            newValue = StringUtils.EMPTY_STRING;
        } else if (this.selectionColumn == -1) {
            newValue = selection.getFirstElement().toString();
        } else {
            newValue = this.labelProvider.getColumnText(selection
                    .getFirstElement(), this.selectionColumn);
        }
        
        if (newValue.equals(oldValue)) {
            this.fireValueChanged(VALUE, oldValue, newValue);
            oldValue = newValue;
        }
    } // end method valueChanged()
    
    
    /*
     * Initializes thisTableFieldEditor's
     * TableVieweR.
     * 
     */
    private void initializeViewer() {
        this.viewer = new TableViewer(this.table);
        this.viewer.setContentProvider(this.contentProvider);
        this.viewer.setLabelProvider(this.labelProvider);
                
        this.viewer.setColumnProperties(this.columnHeaders);
        
        this.viewer.setInput(this.input);
        
        /* Pack all columnHeaders */
        TableColumn column;
        for (int i = 0; i < this.columnHeaders.length; i++) {
            column = this.table.getColumn(i);    
            column.pack();
        }
    } // end method initializeViewer()

   /**
    *  Inicializa el input
    * @param newInput el nuevo objeto input
    */
    public void setInput(final Object newInput){
    	this.input = newInput;
    	this.initializeViewer();
    }
    /**
     * Devuelve el input
     * @return el input
     */
    public Object getInput(){
    	return this.input;
    }
    
    
     /* Initializes the table columnHeaders by setting their widths and adjusting their
     * settings.
     *
     */
    private void initializeColumns() {
        TableColumn column;
        for (int i = 0; i < this.columnHeaders.length; i++) {
            column = new TableColumn(this.table, SWT.LEFT);           
            column.setText(this.columnHeaders[i]);  
            column.setToolTipText(this.columnHeaders[i]);
        }
    } // end method initializeColumns()
    
    
    /*Sets the selection of this TableFieldEditor to
     * the row or element matching the specified selectionStr. 
     * @param selectionStr the <code>String</code> that identifies the
     * row or element to select
     */
    private void setSelection(String selectionStr) {
        if (this.viewer != null) {
            Object[] items = this.contentProvider.getElements(this.viewer
                    .getInput());
            boolean selected = false;
            if (this.selectionColumn == -1) {
                for (int i = 0; i < items.length && !selected; i++) {
                    if (selectionStr.equals(items[i].toString())) {
                        StructuredSelection selection = new StructuredSelection(
                                items[i]);
                        this.viewer.setSelection(selection);
                        selected = true;
                    }
                }
            } else {
                for (int i = 0; i < items.length && !selected; i++) {
                    if (selectionStr.equals(this.labelProvider.getColumnText(
                            items[i], this.selectionColumn))) {
                        StructuredSelection selection = new StructuredSelection(
                                items[i]);
                        this.viewer.setSelection(selection);
                        selected = true;
                    }
                }
            }
        }
    } // end method setSelection(String)

    /**
     * @return Siempre Null
     */
    
	public AccessibleContext getAccessibleContext() {
		return null;
	}
    
    
} 