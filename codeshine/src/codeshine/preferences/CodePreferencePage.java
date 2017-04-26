package codeshine.preferences;

import org.eclipse.jdt.internal.ui.preferences.formatter.ProfileManager.CustomProfile;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.preference.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import codeshine.speech.TtsClass;
import codeshine.Activator;


/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class CodePreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	
	private BooleanFieldEditor customProfile;
	private RadioGroupFieldEditor profiles;
	private BooleanFieldEditor soundEvents;
	
	public CodePreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("CodeShine preferences page");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		
		/*		
		profiles = new RadioGroupFieldEditor(
				IPreferenceConstants.PROFILE, "Default user profile", 1, 
				new String[][] {{"&Low Vision", "high_contrast"}, {"&Color deficiency", "color_def"},
						{"&Both", "both_def"}}, getFieldEditorParent(), true);
	
		customProfile = new BooleanFieldEditor(
				IPreferenceConstants.P_BOOLEAN,
				"C&ustom config",
				getFieldEditorParent());*/
		soundEvents = new BooleanFieldEditor(
				IPreferenceConstants.SOUND_EVENTS, "&Notify editor changes",
				getFieldEditorParent()); 
		
//		profiles.setEnabled(!Activator.getDefault().getPreferenceStore().getBoolean(IPreferenceConstants.P_BOOLEAN), getFieldEditorParent());
		
		soundEvents.loadDefault();
		
//		addField(profiles);
//		addField(customProfile);
		addField(soundEvents);
		
		Activator.getDefault().getPreferenceStore().setDefault(IPreferenceConstants.SOUND_EVENTS, false);
		
/*		IPropertyChangeListener preferenceListener = new IPropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent event){
				if (event.getProperty().equals(IPreferenceConstants.P_BOOLEAN)){
					boolean custom = customProfile.getBooleanValue(); 
					profiles.setEnabled(!custom, getFieldEditorParent());
					System.out.println("boolean_ preference");
				}
				
			}
		};*/
//		customProfile.setPropertyChangeListener(preferenceListener);
	
//		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(preferenceListener);
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		
		
//		
	}
	public void performDefaults(){
		profiles.loadDefault();
		customProfile.loadDefault();
		soundEvents.loadDefault();
	}
}