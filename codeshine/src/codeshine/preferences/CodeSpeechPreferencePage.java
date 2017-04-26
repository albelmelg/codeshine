/**
 * 
 */
package codeshine.preferences;


import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.ScaleFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

//import codeshine.speech.*;
import codeshine.Activator;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

/**
 * @author tivi@us.es
 *
 */
public class CodeSpeechPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {
	
//	RadioGroupFieldEditor voiceChoice;
	StringFieldEditor trainingText;
	ScaleFieldEditor pitch;
//	FileFieldEditor fileChooser;
	

	/**
	 * @param style
	 */
	public CodeSpeechPreferencePage() {
		super(GRID);
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(preferenceStore);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	protected void createFieldEditors() {
	
		pitch = new ScaleFieldEditor(IPreferenceConstants.PITCH, "&Pitch selector", getFieldEditorParent(), 100, 190, 5, 10);
		trainingText = new StringFieldEditor("testing", "&Training area", getFieldEditorParent());
		trainingText.setStringValue("Test how it sounds here");
		trainingText.fillIntoGrid(getFieldEditorParent(), 2);
		
		Button testVoice = new Button(getFieldEditorParent(), SWT.NONE);
		testVoice.setText("&Speak");
		GridData gd = new GridData(SWT.FILL, SWT.RIGHT, true, true);
		gd.horizontalSpan = 3;
		gd.grabExcessHorizontalSpace = true;
		testVoice.setLayoutData(gd);
	
		Listener listener = new Listener(){
			public void handleEvent(Event event){
				
				pitch.store();
				Activator.tts.setText(trainingText.getStringValue());
				Activator.tts.speak();
				System.out.println(trainingText.getStringValue());
				System.out.println("Increment: " + pitch.getPageIncrement());
			}
		};
		testVoice.addListener(SWT.Selection, listener);
		
		addField(pitch);
		addField(trainingText);
		
		pitch.getScaleControl().addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {				
				
			}
			public void widgetSelected(SelectionEvent e) {
//				System.out.println("codeSpeechPrefs_" + e.toString());
				System.out.println("codeSpeechPrefs_ " + pitch.getScaleControl().getSelection());
			}
		});
		IPropertyChangeListener preferenceListener = 
			new IPropertyChangeListener(){
			public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event) {
//				System.out.println("codeSpeechPrefs_" + event.getProperty().toString());
				System.out.println("codeSpeechPrefs_value:  " + Activator.getDefault().
						getPreferenceStore().
						getInt(IPreferenceConstants.PITCH));
			}
		};
//		voiceChoice.setPropertyChangeListener(preferenceListener);
		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(preferenceListener);

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());

	}
	public boolean performOK(){
		pitch.store();
		trainingText.store();
		return super.performOk();
	}
	public boolean performCancel() {
		// TODO Auto-generated method stub
		
		return super.performCancel();
	}
	

}
