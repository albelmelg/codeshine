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

//import org.eclipse.ui.internal.presentations.UpdatingActionContributionItem;
import codeshine.preferences.IPreferenceConstants;


/**
 * <p>
 * A <code>FieldEditor</code> implementation that supports the selection of
 * tabular data.
 * </p>
 *
 * @author <a href="mailto:Sebastian.Machhausen@gmail.com">Sebastian Machhausen</a>
 */
public class TableFieldEditor extends FieldEditor implements Accessible{

    
    /**
	 * <p>
	 * The <code>Table</code> used to present the selectable tabular data
	 * </p>
	 */
    private Table table;
    
    /** <p> The <code>TableViewer</code> used as controller </p> */
    private TableViewer viewer;
    
    /** <p> The content provider used to query the table data </p> */
    private IStructuredContentProvider contentProvider;
    
    /** <p>
     * The label provider used to convert domain objects to ui specific
     * textual representations.
     * </p>
     */
    private ITableLabelProvider labelProvider;
    
    /**
	 * <p>
	 * The input or model object that holds the data of the
	 * <code>TableViewer</code>
	 * </p>
	 */
    private Object input;
    
    /** <p> The column headers to display in the <code>Table</code> </p> */
    private String[] columnHeaders;
    
    /** <o> The <code>Table</code> sorter </p> */
    private TableViewerSorter sorter;
    
    /**
	 * <p>
	 * The handler used to sort the underlying
	 * <code>IStructuredContentProvider</code>
	 * </p>
	 */
	private TableViewerSorterHandler sorterHandler;
    
    /**
	 * <p>
	 * The index of the column to store/retrieve the value for. If set to -1 the
	 * complete row represented as domain object is stored/retrieved. This is
	 * done by calling toString() on the respective domain object.
	 * </p>
	 */
    private int selectionColumn;
    
    /** <p> The last selected value of the <code>Table</code> </p> */
    private Object oldValue;
    
    
    /**
     * <p>
     * Creates a new <code>TableFieldEditor</code> instance.
     * </p>
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     * @param contentProvider the <code>IStructuredContentProvider</code> used
     * to query the table data
     * @param labelProvider the <code>ITableLabelProvider</code> used to
     * convert domain objects to ui specific textual representations
     * @param columnHeaders an array of <code>String</code> objects representing
     * the column headers
     * @param input the input or model object which holds the data for
     * this <code>TableFieldEditor</code>
     */
    public TableFieldEditor(String name, String labelText, Composite parent,
            IStructuredContentProvider contentProvider,
            ITableLabelProvider labelProvider, String[] columnHeaders, Object input) {        
    	this.contentProvider = contentProvider;
        this.labelProvider = labelProvider;
        this.columnHeaders = columnHeaders;
        this.input = input;
        this.init(name, labelText);
        this.createControl(parent);
//        System.out.println("#elements before: " + ((TokenList)this.input).length());
    } // end constructor TableFieldEditor(String, String, Composite, IStructuredContentProvider, ITableLabelProvider, String[], Object)
    
    
    /**
     * </p>
     * Returns the number of controls in this 
     * <code>TableFieldEditor</code>. Returns <code>1</code> as the
     * <code>Table</code> is the only control.
     * </p>
     * 
     * @return <code>1</code>
     * @see org.eclipse.jface.preference.FieldEditor#getNumberOfControls()
     */
    public int getNumberOfControls() {       
        return (1);
    } // end method getNumberOfControls()
    
    
    /**
     * <p>
     * Sets the selection column to the specified <code>columnIndex</code>.
     * The index represents the column whose value is stored/retrieved in this
     * <code>TableFieldEditor</code>. If set to -1 the complete row
     * represented as domain object is stored/retrieved. This is done by calling
     * {@link #toString()} on the respective domain object.
     * </p>
     * 
     * @param columnIndex the column whose value is stored/retrieved in this
     * <code>TableFieldEditor</code>
     * 
     * @see #getSelectionColumn()
     */
    public void setSelectionColumn(int columnIndex) {
        this.selectionColumn = columnIndex;
    } // end method setSelectionColumn(int)
    
    
    /**
     * <p>
     * Gets the selection column which represents the index of the column whose
     * value is stored/retrieved in this <code>TableFieldEditor</code>. A
     * value of <code>-1</code> means that the complete row represented as
     * domain object is stored/retrieved. This is done by calling
     * {@link #toString()} on the respective domain object.
     * </p>
     * 
     * @return the column whose value is stored/retrieved in this
     * <code>TableFieldEditor</code>
     * 
     * @see #setSelectionColumn(int)
     */
    public int getSelectionColumn() {
        return (this.selectionColumn);
    } // end method getSelectionColumn()
    
    
    /**
     * <p>
     * Gets the currently selected value of this <code>TableFieldEditor</code>.
     * The value returned by this method depends on the selection column 
     * set up as returned by {@link #getSelectionColumn()}. If the selection
     * column is set to <code>-1</code> the complete row represented as
     * domain object is returned by calling {@link #toString()} on it. 
     * Otherwise the respective column value is queried and returned using
     * the <code>ITableLabelProvider</code> bound to this
     * <code>TableFieldEditor</code>.
     * </p>
     * 
     * @return the currently selected value or an empty <code>String</code> 
     * if no selection 
     * 
     * @see #setSelectionColumn(int)
     * @see #getSelectionColumn()
     */
    public String getSelection() {
        IStructuredSelection selection = 
            (IStructuredSelection) this.viewer.getSelection();
        if (selection.isEmpty()) {
            /* Empty selection */        
            return (StringUtils.EMPTY_STRING);
        } else if (this.selectionColumn == -1) {
            /* Row selection */
            return (selection.getFirstElement().toString());
        } else {
            /* Column selection */
            return (this.labelProvider.getColumnText(selection
                    .getFirstElement(), this.selectionColumn));
        }
    } // end method getSelection()
    
    
    /**
	 * <p>
	 * Enables or disables the sorting of the <code>Table</code> depending on
	 * the specified <code>enabled</code> status.</br> The
	 * <code>IStructuredContentProvider</code> used in this
	 * <code>TableFieldEditor</code> has to implement the
	 * {@link net.sourceforge.java.util.gui.jface.viewers.ITableContentProvider}
	 * interface to enable sorting.
	 * </p>
	 * 
	 * @param enabled <code>true</code> to enable sorting; <code>false</code>
	 * to disable
	 * 
	 * @see #isSortingEnabled()
	 */
    public void setSortingEnabled(boolean enabled) {
    	if (this.contentProvider instanceof ITableContentProvider) {
    		if (enabled) {
	    		this.sorter = new TableViewerSorter(this.viewer,
	    					(ITableContentProvider) this.contentProvider);
	    		this.sorterHandler = 
	    			new TableViewerSorterHandler(this.table, this.sorter);
	    		this.viewer.setSorter(sorter);
    		} else {
    			this.sorter = null;
    			this.sorterHandler = null;
    			this.viewer.setSorter(null);    			
    		}
    	}
    } // end method setSortingEnabled(boolean)
    
    
    /**
     * <p>
     * Returns true if <code>Table</code> sorting is enabled;
     * <code>false</code> otherwise.
     * </p>
     * 
     * @return <code>true</code> if sorting is enabled; <code>false</code>
     * otherwise
     * 
     * @see #setSortingEnabled(boolean)
     */
    public boolean isSortingEnabled() {
    	return (this.sorterHandler != null);
    } // end method isSortingEnabled()
    
    
    /**
	 * <p>
	 * Sorts the <code>Table</code> by the specified <code>columnIndex</code>
	 * in the specified sort order. If sorting is disabled this method does
	 * nothing.
	 * </p>
	 * 
	 * @param columnIndex the index of the column to sort by
	 * @param ascending <code>true</code> to sort in ascending,
	 * <code>false</code> to sort in descending order
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
    } // end method sort(int, boolean)
    
    
    /**
	 * <p>
	 * Gets the index of the column by which sorting is done. If sorting is
	 * disabled <i>-1</i> is returned.
	 * </p>
	 * 
	 * @return the index of the column upon which sorting is done; <i>-1</i> if
	 * sorting is disabled
	 * 
	 * @see #isSortAscending()
	 * @see #sort(int, boolean)
	 */
    public int getSortingColumn() {
    	if (this.isSortingEnabled()) {
    		return (this.sorter.getSortingColumn());
    	} else {
    		return (-1);
    	}
    } // end method getSortingColumn()
    
    
    /**
	 * <p>
	 * Returns <code>true</code> if sorting is done in ascending order;
	 * <code>false</code> if done in descending order or sorting is disabled;
	 * </p>
	 * 
	 * @return <code>true</code> if sorting is done in ascending order;
	 * <code>false</code> if done in descending order or sorting is disabled;
	 * 
	 * @see #getSortingColumn()
	 * @see #sort(int, boolean)
	 */
    public boolean isSortAscending() {
    	if (this.isSortingEnabled()) {
    		return (this.sorter.isAscending());
    	} else {
    		return (false);
    	}
    } // end method isSortAscending()
    

    /**
	 * <p>
	 * Sets the width of the column at the specified <code>columnIndex</code>
	 * to the given <code>width</code>. If no <code>TableColumn</code>
	 * exists at the specified <code>columnIndex</code> the method does
	 * nothing.
	 * </p>
	 * 
	 * @param columnIndex the index of the column to set the width for
	 * @param width the width of the column in pixel
	 * 
	 * @see #getColumnWidth(int)
	 */
    public void setColumnWidth(int columnIndex, int width) {
    	if (columnIndex >= 0 && columnIndex < this.columnHeaders.length) {
    		TableColumn column = this.table.getColumn(columnIndex);
    		column.setWidth(width);
    	}
    } // end method setColumnWidth(int, int)
    
    
    /**
	 * <p>
	 * Gets the width in pixel of the column at the specified
	 * <code>columnIndex</code>. If no <code>TableColumn</code> exists at
	 * the specified <code>columnIndex</code> the method returns <i>0</i>.
	 * </p>
	 * 
	 * @param columnIndex the index of the column to get the width for
	 * 
	 * @return the column width
	 * 
	 * @see #setColumnWidth(int, int)
	 */
    public int getColumnWidth(int columnIndex) {
    	if (columnIndex >= 0 && columnIndex < this.columnHeaders.length) {
    		TableColumn column = this.table.getColumn(columnIndex);
    		return (column.getWidth());
    	} else {
    		return (0);
    	}
    } // end method getColumnWidth(int)
    
    
    /**
     * <p>
     * Adjusts the horizontal span of this <code>TableFieldEditor</code>'s basic
     * controls. The number of columnHeaders will always be equal to or greater than
     * the value returned by this editor's <code>getNumberOfControls</code>
     * method.
     * </p>
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


    /**
     * <p>
     * Fills this <code>TableFieldEditor</code>'s basic controls into the
     * given parent.
     * </p>
     * 
     * @param parent the composite used as a parent for the basic controls; the
     * parent's layout must be a <code>GridLayout</code>
     * @param numColumns the number of columnHeaders
     * 
     * @see org.eclipse.jface.preference.FieldEditor#doFillIntoGrid(org.eclipse.swt.widgets.Composite,
     * int)
     */
    protected void doFillIntoGrid(Composite parent, int numColumns) {
        this.getLabelControl(parent);
        
        this.table = new Table(parent, SWT.FULL_SELECTION | SWT.BORDER
                | SWT.V_SCROLL | SWT.H_SCROLL);       
        this.table.setHeaderVisible(true);
        this.table.setLinesVisible(false);
        this.table.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {               
                valueChanged();          
            } // end method widgetDDefaultSelected(SelectionEvent)
            
            public void widgetSelected(SelectionEvent e) {
                valueChanged();
            } // end method widgetSelected(SelectionEvent)
        });        
        
        this.initializeColumns();
        this.initializeViewer();
        
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns - 1;
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        this.table.setLayoutData(gd);
        this.table.setLinesVisible(true);

//Tivi's hack that makes tableFE editable.
        
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
            	  if ((old.equals(IPreferenceConstants.SPECIAL)) && (row.getText(0).equals(""))){
            		  Token t = new Token(IPreferenceConstants.SPECIAL, row.getText(1), row.getText(2));
            		  tl.addToken(t);
	              }
            	  else{
            		  if (tl.getCollection().containsKey(old)){
            			  System.out.println("TFE: removed " + old);
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
            TableItem row = (selection.length == 0) ? table.getItem(table.getTopIndex()) : selection[0];
            table.showItem(row);
            cursor.setSelection(row, 0);
            cursor.setVisible(true);
            cursor.setFocus();
            selectionColumn = cursor.getColumn();
          }
        });
        
        
    } // end method doFillIntoGrid()


    /**
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


    /**
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


    /**
     * <p>
     * Stores the preference value from this
     * <code>TableFieldEditor</code> into the preference store.
     * </p>
     * 
     * @see org.eclipse.jface.preference.FieldEditor#doStore()
     */
    protected void doStore() {   
    	this.valueChanged();
        this.getPreferenceStore().setValue(this.getPreferenceName(),
                this.getSelection());        
    } // end method doStore()

    
    /**
     * <p>
     * Informs this field editor's listener, if it has one, about a change to
     * the value (<code>VALUE</code> property) provided that the old and new
     * values are different. This hook is <em>not</em> called when the value is
     * initialized (or reset to the default value) from the preference store.
     * </p>
     */
    protected void valueChanged() {
    	
    	System.out.println("Invocado ValueChanged");
    	this.setPresentsDefaultValue(false);
    	
    	
        IStructuredSelection selection = 
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
    
    
    /**
     * <p>
     * Initializes this <code>TableFieldEditor</code>'s
     * <code>TableViewer</code>.
     * </p>
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
    
    
//    New method added by tivi
    
    public void setInput(Object newInput){
    	this.input = newInput;
    	this.initializeViewer();
    }
    public Object getInput(){
    	return this.input;
    }
    
    /**
     * <p>
     * Initializes the table columnHeaders by setting their widths and adjusting their
     * settings.
     * </p>
     */
    private void initializeColumns() {
        TableColumn column;
        for (int i = 0; i < this.columnHeaders.length; i++) {
            column = new TableColumn(this.table, SWT.LEFT);           
            column.setText(this.columnHeaders[i]);  
            column.setToolTipText(this.columnHeaders[i]);
        }
    } // end method initializeColumns()
    
    
    /**
     * </p>
     * Sets the selection of this <code>TableFieldEditor</code> to
     * the row or element matching the specified <code>selectionStr</code>.
     * </p>
     * 
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


	public AccessibleContext getAccessibleContext() {
		return null;
	}
    
    
} // end class TableFieldEditor