package codeshine.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import codeshine.Activator;


/**
 * Esta clase representa la página de preferencias 
 * que contribuye al dialogo de Preferencias.
 * La página se usa solo para modificar las preferencias
 */

public class CodePreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	
	private BooleanFieldEditor customProfile;
	private RadioGroupFieldEditor profiles;
	private BooleanFieldEditor soundEvents;
	/**
	 * Constructor por defecto
	 */
	
	public CodePreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("CodeShine preferences page");
	}
	
	/**
	 * Se crean los field editors. 
	 * Cada Field Editor sabe como guardarse y borrarse a el mismo.
	 */
	public void createFieldEditors() {

		soundEvents = new BooleanFieldEditor(
				IPreferenceConstants.SOUND_EVENTS, "&Notify editor changes",
				getFieldEditorParent()); 
		soundEvents.loadDefault();
		addField(soundEvents);
		Activator.getDefault().getPreferenceStore().setDefault(IPreferenceConstants.SOUND_EVENTS, false);
		};


	/** 
	 * Inicializa a página de preferencias para el espacio de trabajo (workbench)
	 * @param Iworkbench workbench - el espacio de trabajo
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		
	}
	

	/**
	 * Carga los valores por defecto.
	 * 
	 */
	public void performDefaults(){
		profiles.loadDefault();
		customProfile.loadDefault();
		soundEvents.loadDefault();
	}
}