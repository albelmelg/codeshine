
package codeshine.preferences;

import java.io.File;
import java.io.InvalidObjectException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import codeshine.Activator;
import codeshine.utils.ITableContentProvider;
import codeshine.utils.TableFieldEditor;
import codeshine.utils.TokenList;
import codeshine.utils.TokensLabelProvider;
import org.apache.log4j.Logger;
/**
 * Guarda todos los campos 
 * que has introducido en las preferencias 
 * para poder guardarlo como perfil predeterminado
 * 
 */

public class CodeProfilesPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {
	
	ITableContentProvider tcProvider = new ProfileConfigProvider();
	ITableLabelProvider tlProvider = new TokensLabelProvider();
	String[] headers = {"Token", "Replace by", "Description"};
	TableViewer viewer;
	TableFieldEditor tableFE;
	FileFieldEditor fileFE;
	TokenList attributes;
	File file;
	boolean done;
	static Logger logger = Logger.getLogger(CodeProfilesPreferencePage.class);

	/** Constructor por defecto
	 * se instancia el grid
	 * */
	public CodeProfilesPreferencePage() {

		super(GRID);
		final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(preferenceStore);
	}

	/**
	 *  Se instancian los campos 
	 */

	protected void createFieldEditors() {
			
		final String[] extensions = {"*.xml"};
		
		try{
			attributes = new TokenList(Activator.getDefault().getPreferenceStore().getString(IPreferenceConstants.PROFILEPATH));
		}catch (InvalidObjectException e){e.printStackTrace();}
			
		logger.info("preferences constructor: " + attributes.length());
		fileFE = new FileFieldEditor(IPreferenceConstants.PROFILEPATH, "&Load from file", getFieldEditorParent());
		tableFE = new TableFieldEditor(IPreferenceConstants.PROFILECONTENT, "&Edit profile",
				getFieldEditorParent(), tcProvider, tlProvider, headers, attributes);
		tableFE.setSortingEnabled(true);
		
		fileFE.setFileExtensions(extensions);
		fileFE.setChangeButtonText("&Open");
		fileFE.setEmptyStringAllowed(true);
		
		
			
		addField(tableFE);
		addField(fileFE);
		
		final ModifyListener modifyListener = new ModifyListener(){
			/** 
			 * Se guardan los valores introducidos en los campos
			 * @param PropertyChangeEvent event  
			 * */
			public void modifyText(final ModifyEvent evento){
				if (fileFE.isValid()){
					String value = fileFE.getStringValue();
					file = new File(value);
					if ((file.exists()) && (file.getName().endsWith("xml"))){
						boolean answer = MessageDialog.openQuestion(getFieldEditorParent().getShell(),
								"newProfile", "Overrides current profile with selected file?");
						if (answer){
							TokenList newTokenList = null;
							try{
								newTokenList = new TokenList(file);
							}catch (Exception exp){exp.printStackTrace();}
							logger.info("*_elementos: " + newTokenList.length());
							logger.info("El archivo existe y me mola");
							attributes = newTokenList;
							logger.info("#elem: " + attributes.length());
							tableFE.setInput(attributes);
						}
					}
					else{
						logger.info("El archivo no existe");
					}
				}
			}
		};
		((StringFieldEditor)fileFE).getTextControl(getFieldEditorParent()).addModifyListener(modifyListener);		

		final IPropertyChangeListener prefListener = 
					new IPropertyChangeListener(){
					public void propertyChange(PropertyChangeEvent event) {
						logger.info("####" + event.getProperty().toString());}
				};
				
		fileFE.setPropertyChangeListener(prefListener);
		tableFE.setPropertyChangeListener(prefListener);
		
		final IPropertyChangeListener profPrefListener = new IPropertyChangeListener(){
			public void propertyChange(final PropertyChangeEvent event){
				logger.info("Property changed: " + event.getProperty());
				logger.info("New Value: " + event.getNewValue());
				logger.info("Old Value: " + event.getOldValue());
			}
		};
		
		tableFE.setPropertyChangeListener(profPrefListener);

	}

	/** Inicializa la pagina de preferencias para el espacio de trabajo (workbench)
	 * @param workbench El espacio de trabajo
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(final IWorkbench workbench) {

		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}
	/**
	 * Carga los valores por defecto.
	 */
	protected void performDefaults(){
		tableFE.loadDefault();	
		fileFE.loadDefault();
	}
	
	/**
	 * Este metodo se usa para guardar el perfil
	 */
	
	public void performApply(){
		logger.info("Entering performApply method");
		final File newFile = new File(fileFE.getStringValue()); 
		if (!done){
			done=true;
			if (newFile.exists()){
				attributes.toXML(fileFE.getStringValue());
				Activator.tts.setProfile(attributes);
				logger.info(attributes.toTrie().toString(" "));
				logger.info("CodeProfilesPP: " + attributes.toString());
				MessageDialog.openInformation(getFieldEditorParent().getShell(), "success", "Profile save successfully");
				this.setValid(true);
			}
			else{
				FileDialog fdialog = new FileDialog(new Shell());
				fdialog.setText("Save as...");
				String selectedFile = fdialog.open();
				MessageDialog.openInformation(getFieldEditorParent().getShell(), "success", "Profile save successfully");
				fileFE.setStringValue(selectedFile);
				attributes.toXML(selectedFile);
				fileFE.setFocus();
				this.setValid(true);
			}
		}
		
	}	
	/** Notifica que se ha pulsado el boton OK 
	 * @return True o False - True para que se pulse el boton o False si se debe cancelar la accion
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk(){
		this.performApply();
		fileFE.store();
		tableFE.store();
		return super.performOk();
	}

}
