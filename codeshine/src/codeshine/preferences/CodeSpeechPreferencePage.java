/**
 * 
 */
package codeshine.preferences;

import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.ScaleFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.apache.log4j.Logger;

import codeshine.Activator;


/**
 * Esta clase sirve para definir la ventana de las preferencias sobre el speech.
 */
public class CodeSpeechPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	 StringFieldEditor trainingText;
	 ScaleFieldEditor pitch;
	private static Logger logger = Logger.getLogger(CodeSpeechPreferencePage.class);

	/**Constructor por defecto donde
	 * se instancia el grid
	 * */
	public CodeSpeechPreferencePage() {
		super(GRID);
		final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(preferenceStore);
	}

	/**
	 * Se instancian los campos del trainingtext, pitch y testvoice
	 */
	protected void createFieldEditors() {
	
		pitch = new ScaleFieldEditor(IPreferenceConstants.PITCH, "&Pitch selector", getFieldEditorParent(), 100, 190, 5, 10);
		trainingText = new StringFieldEditor("testing", "&Training area", getFieldEditorParent());
		trainingText.setStringValue("Test how it sounds here");
		trainingText.fillIntoGrid(getFieldEditorParent(), 2);
		
		final Button testVoice = new Button(getFieldEditorParent(), SWT.NONE);
		testVoice.setText("&Speak");
		final GridData gridata = new GridData(SWT.FILL, SWT.RIGHT, true, true);
		gridata.horizontalSpan = 3;
		gridata.grabExcessHorizontalSpace = true;
		testVoice.setLayoutData(gridata);
	
		final Listener listener = new Listener(){
			public void handleEvent(final Event event){
				
				pitch.store();
				Activator.tts.setText(trainingText.getStringValue());
				Activator.tts.speak();
				logger.info(trainingText.getStringValue());
				logger.info("Increment: " + pitch.getPageIncrement());
			}
		};
		testVoice.addListener(SWT.Selection, listener);
		
		addField(pitch);
		addField(trainingText);
		
		pitch.getScaleControl().addSelectionListener(new SelectionListener(){
			/**
			 * Recoge el evento que nos dice cual es el windget seleccionado
			 */
			public void widgetDefaultSelected(final SelectionEvent event) {				
				
			}
			/**
			 * Recoge el evento que tiene el botón seleccionado e informa.
			 */
			public void widgetSelected(final SelectionEvent event) {

				logger.info("codeSpeechPrefs_ " + pitch.getScaleControl().getSelection());
			}
		});
		final IPropertyChangeListener prefListener = 
			new IPropertyChangeListener(){
			/**
			 * Notifica con un logger que se han cambiado las preferencias del speech
			 */
			public void propertyChange(final PropertyChangeEvent event) {
				logger.info("codeSpeechPrefs_value:  " + Activator.getDefault().
						getPreferenceStore().
						getInt(IPreferenceConstants.PITCH));
			}
		};
		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(prefListener);

	}

	/** Inicializa a página de preferencias para el espacio de trabajo (workbench)
	 * @param workbench - el espacio de trabajo
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(final IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());

	}
	
	/** Notifica que se ha pulsado el botón OK 
	 * @return True o False - True para que se pulse el botón o False si se debe cancelar la acción
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	
	public boolean performOK(){
		pitch.store();
		trainingText.store();
		return super.performOk();
	}
	
	/** Notifica que se ha pulsado el botón cancelar de la página
	 * @return false - Para poder cancelaro
	 * @see org.eclipse.jface.preference.IPreferencePage#performCancel()
	 */
	public boolean performCancel() {
		
		return super.performCancel();
	}
	

}
