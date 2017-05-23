package codeshine.preferences;

import org.eclipse.swt.graphics.Color;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import codeshine.preferences.*;

import codeshine.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(IPreferenceConstants.P_BOOLEAN, true);
		store.setDefault(IPreferenceConstants.P_CHOICE, "choice2");
		store.setDefault(IPreferenceConstants.P_STRING,
				"Default value");
		store.setDefault(IPreferenceConstants.BACK_COLOR,"10,10,10");
		store.setDefault(IPreferenceConstants.FONT_COLOR,"255,215,0");
		
	}

}
