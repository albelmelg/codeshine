package codeshine;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import codeshine.preferences.IPreferenceConstants;
import codeshine.speech.TtsClass;
/**
 * La clase Activator class controla el ciclo de vida de plugin
 * @author Carlos D. Hinarejos y Natividad Prieto
 * @version 1
 */
public class Activator extends AbstractUIPlugin {

	/** The plug-in ID*/
	
	public static final String PLUGIN_ID = "codeShine";
	
	/** La instancia compartida*/
	private static  Activator plugin;
	/**Objeto de la clase TtsClass*/
	public static TtsClass tts;
	
	/**
	 * Constructor
	 */
	public Activator() {
		plugin = this;
	}

	/**
	 * Se inicia la comunicación para que el plugin pueda realizar sus actividades.
	 * Throws java.lang.exception
	 * @param context es un objeto que se usa para poder interactuar con el Framework
	 */
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		tts = new TtsClass();
		Activator.getDefault().getPreferenceStore().setDefault(IPreferenceConstants.SOUND_EVENTS, false);
	}

	/**
	 * Detiene la ejecución del bundle y a ejecución del plugin debe detenerse.
	 *  Throws java.lang.exception
	 * @param conext es un objeto que se usa para poder interactuar con el Framework
	 */
	public void stop(final BundleContext context)throws Exception {
		super.stop(context);
		tts.deallocate();
			
	}

	/**
	 * Devuelve el objeto compartido
	 *
	 * @return el objeto compartido
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Crea y devuelve un nuevo ImageDescriptor para un
	 *  archivo de imagen ubicado dentro del plugin especificado.
	 *
	 * @param El path del plugin
	 * @return Un ImageDescriptor, o null si no se ha encontrado ninguna imagen.
	 */
	public static ImageDescriptor getImageDescriptor(final String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
