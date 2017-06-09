
package codeshine.preferences;
/**
 * Esta clase sirve para definir la ventana de las preferencias sobre la letra del código.
 * 
 */

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

	/**Color del texto*/
	ColorFieldEditor textColourFE;
	/**Color del fondo*/
	ColorFieldEditor backgroundColourFE;
	/**Color del resaltado */
	ColorFieldEditor hightlightFE;
	/** Tipo de fuente*/
	FontFieldEditor fontFE;
	/** */
	BooleanFieldEditor customProfileFE;
	/** */
	Label fontLabel;

	/** Constructor por defecto
	 * se instancia el grid
	 * */
	
	public CodeAppearancePreferencePage() {
		super(GRID);
	final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(preferenceStore);
	}
	
	/** Constructor, creará un objeto de tipo Field Editor Preference Page
	 * @param String title, ImageDescriptor image, int style
	 * */
		
	public CodeAppearancePreferencePage(String title, final ImageDescriptor image,final int style) {
		super(title, image, style);
	}

	/** Inicializa a página de preferencias para el espacio de trabajo (workbench)
	 * @param Iworkbench workbench - el espacio de trabajo
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(final IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());

	}
	/** Notifica que se ha pulsado el botón cancelar de la página
	 * @return false - Para poder cancelaro
	 * @see org.eclipse.jface.preference.IPreferencePage#performCancel()
	 */

	public boolean performCancel() {

		return false;
	}

	/** Notifica que se ha pulsado el botón OK 
	 * @return True o False - True para que se pulse el botón o False si se debe cancelar la acción
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		textColourFE.store();
		backgroundColourFE.store();
		fontFE.store();
		hightlightFE.store();
		return super.performOk();
	}

	/**
	 * Carga los valores por defecto del color del texto, del fondo...
	 * 
	 */

	protected void performDefaults(){
		textColourFE.loadDefault();
		backgroundColourFE.loadDefault();
		fontFE.loadDefault();
		hightlightFE.loadDefault();
		
	}
	/**
	 *  Se instancian los campos para la edición de la fuente, del fondo y del resaltado
	 */

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
		
		final IPropertyChangeListener preferenceListener = new IPropertyChangeListener(){
			
			/** 
			 * Se guardan los valores introducidos en los campos
			 * @param PropertyChangeEvent event  
			 * */
			public void propertyChange(final PropertyChangeEvent event){
				if (event.getProperty().equals(IPreferenceConstants.P_BOOLEAN)){
					boolean bool = Activator.getDefault().getPreferenceStore().getBoolean(IPreferenceConstants.P_BOOLEAN); 
					textColourFE.setEnabled(bool, getFieldEditorParent());
					backgroundColourFE.setEnabled(bool, getFieldEditorParent());
					hightlightFE.setEnabled(bool, getFieldEditorParent());
					}
				}
		};
		
		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(preferenceListener);
		
		
	}
	
}
