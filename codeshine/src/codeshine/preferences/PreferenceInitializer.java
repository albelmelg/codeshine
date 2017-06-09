package codeshine.preferences;


import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import codeshine.Activator;

/**
 * Clase que se usa para incializar 
 * los valores por defecto de las preferencias.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/**
	 * Se inicializan los valores por defecto 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(IPreferenceConstants.P_BOOLEAN, true);
		store.setDefault(IPreferenceConstants.P_CHOICE, "choice2");
		store.setDefault(IPreferenceConstants.P_STRING,
				"Default value");
		store.setDefault(IPreferenceConstants.BACK_COLOR,"10,10,10");
		store.setDefault(IPreferenceConstants.FONT_COLOR,"255,215,0");
		
	}

}
