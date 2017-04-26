/**
 * 
 */
package codeshine.preferences;

import org.eclipse.swt.widgets.Button;

import java.awt.image.TileObserver;
import java.util.logging.*;
import java.io.*;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.FontFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.dialogs.MessageDialog;

//import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.link.LinkedModeUI.ExitFlags;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.CellEditor.LayoutData;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.*;


import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import sun.util.logging.resources.*;//resources.logging;


import codeshine.Activator;
import codeshine.speech.TtsClass;
import codeshine.utils.*;


public class CodeProfilesPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {
	
	ITableContentProvider tcProvider = new ProfileConfigProvider();
	ITableLabelProvider tlProvider = new TokensLabelProvider();
	String[] headers = {"Token", "Replace by", "Description"};
	TableViewer viewer;
	TableFieldEditor tableFE;
	FileFieldEditor fileFE;
	TokenList attributes = null;
	File f;
	boolean done = false;
	/**
	 * @param style
	 */
	public CodeProfilesPreferencePage() {
//		TODO Auto-generated constructor stub
		super(GRID);
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(preferenceStore);
	}

	/**
	 * @param title
	 * @param style
	 */
/*	public CodeProfilesPreferencePage(String title, int style) {
		super(title, style);
		// TODO Auto-generated constructor stub
	}
*/
	protected void createFieldEditors() {
			
		String[] extensions = {"*.xml"};
		
		try{
//			TODO: Fix this absolute path
//			attributes = new TokenList("/home/agonzalez/workspace/codeshine/src/codeshine/speech/profile.xml");
			attributes = new TokenList(Activator.getDefault().getPreferenceStore().getString(IPreferenceConstants.PROFILEPATH));
		}catch (InvalidObjectException e){e.printStackTrace();}
		
		
	
		System.out.println("preferences constructor: " + attributes.length());
		fileFE = new FileFieldEditor(IPreferenceConstants.PROFILEPATH, "&Load from file", getFieldEditorParent());
		tableFE = new TableFieldEditor(IPreferenceConstants.PROFILECONTENT, "&Edit profile",
				getFieldEditorParent(), tcProvider, tlProvider, headers, attributes);
		tableFE.setSortingEnabled(true);
		
		fileFE.setFileExtensions(extensions);
		fileFE.setChangeButtonText("&Open");
		fileFE.setEmptyStringAllowed(true);
		
		
			
		addField(tableFE);
		addField(fileFE);
		
		ModifyListener modifyListener = new ModifyListener(){
			public void modifyText(ModifyEvent e){
				if (fileFE.isValid()){
					String value = fileFE.getStringValue();
					f = new File(value);
					if ((f.exists()) && (f.getName().endsWith("xml"))){
						boolean answer = MessageDialog.openQuestion(getFieldEditorParent().getShell(),
								"newProfile", "Overrides current profile with selected file?");
						if (answer){
							TokenList newTokenList = null;
							try{
								newTokenList = new TokenList(f);
							}catch (Exception exp){exp.printStackTrace();}
							System.out.println("*_elementos: " + newTokenList.length());
							System.out.println("El archivo existe y me mola");
							attributes = newTokenList;
							System.out.println("#elem: " + attributes.length());
							tableFE.setInput(attributes);
						}
					}
					else{
						System.out.println("El archivo no existe");
					}
				}
			}
		};
		((StringFieldEditor)fileFE).getTextControl(getFieldEditorParent()).addModifyListener(modifyListener);		

		IPropertyChangeListener preferenceListener = 
					new IPropertyChangeListener(){
					public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event) {
						System.out.println("####" + event.getProperty().toString());}
				};
				
		fileFE.setPropertyChangeListener(preferenceListener);
		tableFE.setPropertyChangeListener(preferenceListener);
		
		IPropertyChangeListener profilesPrefListener = new IPropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent event){
				System.out.println("Property changed: " + event.getProperty());
				System.out.println("New Value: " + event.getNewValue());
				System.out.println("Old Value: " + event.getOldValue());
			}
		};
		
		tableFE.setPropertyChangeListener(profilesPrefListener);
//		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(profilesPrefListener);
		
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}
	protected void performDefaults(){
		tableFE.loadDefault();	
		fileFE.loadDefault();
	}
	
	public void performApply(){
		System.out.println("Entering performApply method");
		File newFile = new File(fileFE.getStringValue()); 
		if (!done){
			done=true;
			if (newFile.exists()){
				attributes.toXML(fileFE.getStringValue());
				Activator.tts.setProfile(attributes);
				System.out.println(attributes.toTrie().toString(" "));
				System.out.println("CodeProfilesPP: " + attributes.toString());
				MessageDialog.openInformation(getFieldEditorParent().getShell(), "success", "Profile save successfully");
				this.setValid(true);
			}
			else{
				FileDialog fd = new FileDialog(new Shell());
				fd.setText("Save as...");
				String selectedFile = fd.open();
				MessageDialog.openInformation(getFieldEditorParent().getShell(), "success", "Profile save successfully");
				fileFE.setStringValue(selectedFile);
				attributes.toXML(selectedFile);
				fileFE.setFocus();
				this.setValid(true);
			}
		}
		
	}	
	public boolean performOk(){
		this.performApply();
		fileFE.store();
		tableFE.store();
		return super.performOk();
	}

}
