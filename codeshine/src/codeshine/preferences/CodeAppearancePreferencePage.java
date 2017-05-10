/**
 * 
 */
package codeshine.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FontFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import codeshine.Activator;

public class CodeAppearancePreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	ColorFieldEditor textColourFE;
	ColorFieldEditor backgroundColourFE;
	ColorFieldEditor hightlightFE;
	FontFieldEditor fontFE;
	BooleanFieldEditor customProfileFE;
	Label fontLabel;
	
	public CodeAppearancePreferencePage() {
		super(GRID);
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(preferenceStore);
	}
		
	public CodeAppearancePreferencePage(String title, ImageDescriptor image, int style) {
		super(title, image, style);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());

	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferencePage#performCancel()
	 */

	public boolean performCancel() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		textColourFE.store();
		backgroundColourFE.store();
		fontFE.store();
		hightlightFE.store();
		return super.performOk();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferencePage#setContainer(org.eclipse.jface.preference.IPreferencePageContainer)
	 */

	protected void performDefaults(){
		textColourFE.loadDefault();
		backgroundColourFE.loadDefault();
		fontFE.loadDefault();
		hightlightFE.loadDefault();
		
	}

	protected void createFieldEditors() {	
		fontFE = new FontFieldEditor(IPreferenceConstants.FONT_TYPE, "&Font", getFieldEditorParent());
		textColourFE = new ColorFieldEditor(IPreferenceConstants.FONT_COLOR,
				"&Text colour ", getFieldEditorParent() );
		textColourFE.setEnabled(true, getFieldEditorParent());
		textColourFE.fillIntoGrid(getFieldEditorParent(), 2);
		backgroundColourFE = new ColorFieldEditor(IPreferenceConstants.BACK_COLOR,
				"&Background color", getFieldEditorParent());
		backgroundColourFE.setEnabled(true, getFieldEditorParent());
		backgroundColourFE.fillIntoGrid(getFieldEditorParent(), 2);
		hightlightFE = new ColorFieldEditor(IPreferenceConstants.HIGHTLIGHT,
				"&Highlight color", getFieldEditorParent());
		hightlightFE.fillIntoGrid(getFieldEditorParent(), 2);
		hightlightFE.setEnabled(true, getFieldEditorParent());


		addField(fontFE);
		addField(textColourFE);
		addField(backgroundColourFE);
		addField(hightlightFE);
		
		IPropertyChangeListener preferenceListener = new IPropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent event){
				if (event.getProperty().equals(IPreferenceConstants.P_BOOLEAN)){
					//System.out.println("boolean preference new value: " + event.getNewValue());
					boolean b = Activator.getDefault().getPreferenceStore().getBoolean(IPreferenceConstants.P_BOOLEAN); 
					textColourFE.setEnabled(b, getFieldEditorParent());
					backgroundColourFE.setEnabled(b, getFieldEditorParent());
					hightlightFE.setEnabled(b, getFieldEditorParent());
					}
				}
		};
		
		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(preferenceListener);
		
		
	}
	
}
