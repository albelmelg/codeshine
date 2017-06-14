package codeshine.views;

/**
*
* FreeTTS
* requiere en el CLASSPATH: cmu_time_awb.jar, cmu_us_kal.jar, cmudict04.jar, cmulex.jar, cmutimelex.jar,
* en_us.jar, freets.jar, jsapi.jar
*
* @version 0.0.0.2
* @since JDK 1.8 / Eclipse Neon 3.0
*/
import com.sun.speech.freetts.audio.AudioPlayer;
import com.sun.speech.freetts.audio.NullAudioPlayer;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;
import javax.sound.sampled.AudioSystem;

import org.apache.log4j.Logger;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

/**
 *Convierte ficheros de texto en voz
 */
public class EscribirEnFichero1
{
	static Logger logger = Logger.getLogger(EscribirEnFichero1.class);
	Voice voice=null;
	
	/**
	 * Constructor
	 * @param voiceName La voz en la que se quiere que se hable.
	 * @throws Exception no se especifica que tipo de excepcion puede saltar
	 */
	public EscribirEnFichero1(String voiceName) throws Exception
	{
	VoiceManager voiceManager = VoiceManager.getInstance();
	this.voice = voiceManager.getVoice(voiceName);
	if (this.voice == null)
	{
		logger.info("La lista de voces disponibles es:");
		listAllVoices();
		throw new Exception("No se encuentra la voz llamada: "+voiceName+". Por favor selecciona una voz diferente.");
	}
		this.voice.allocate();
	}
	
	/**
	 * Reproduce el texto que se le pasa
	 * @param text El texto que queremos que se reproduzca
	 * @throws Exception La excepcion no especificada que salta
	 */
	public void speak(String text) throws Exception
	{
		this.voice.speak(text);
	}
	/**
	 * Graba un fichero de audio
	 * @param filename El nombre del fichero
	 * @param text El texto que queremos grabar
	 * @throws Exception La excepcion no especificada que puede saltar
	 */
	public void toFile(String filename,String text) throws Exception
	{
		
		javax.sound.sampled.AudioFileFormat.Type type = getAudioType(filename);
		AudioPlayer audioPlayer = null;
		if(audioPlayer == null)
		audioPlayer = new NullAudioPlayer();
		audioPlayer = new SingleFileAudioPlayer(getBasename(filename), type);
		logger.info("audioPlayer "+audioPlayer);
		this.voice.setAudioPlayer(audioPlayer);
		this.voice.speak(text);
		audioPlayer.close();
	}
	/**
	 * Cancela la ejecucion del procesamiento de la voz
	 * @throws Exception La excepcion no especificada que puede saltar
	 */
	public void close() throws Exception
	{
		this.voice.deallocate();
	}
	/**
	 * Lista todas las voces que hay disponibles
	 */
	public static void listAllVoices()
	{

		logger.info("All voices available:");
		VoiceManager voiceManager = VoiceManager.getInstance();
		logger.info("voiceManager:"+voiceManager);
		Voice[] voices = voiceManager.getVoices();
			for (int i = 0; i < voices.length; i++) {
				logger.info(" " + voices[i].getName()
							+ " (" + voices[i].getDomain() + " domain)");
			}
	}
	/**
	 * Devuelve el tipo de archivo de audio que es un file (como wav o au)
	 * @param file El archivo del cual queremos saber el tipo de archivo
	 * @return El tipo de audio del file que se le pasa
	 * @see javax.sound.sampled.AudioFileFormat.Type
	 */
	public static javax.sound.sampled.AudioFileFormat.Type getAudioType(String file)
	{
		javax.sound.sampled.AudioFileFormat.Type types[] = AudioSystem.getAudioFileTypes();
		String extension = getExtension(file);
			for(int i = 0; i < types.length; i++)
				if(types[i].getExtension().equals(extension))
					return types[i];
			return null;
	}
	/**
	 * Devuelve la extension del nombre del fichero que se le pasa
	 * @param path El nombre del fichero completo (nombre + .extension)
	 * @return Devuelve un substring con la extension del fichero
	 */
	public static String getExtension(String path)
	{
		int index = path.lastIndexOf(".");
			if(index == -1)
				return null;
			else
				return path.substring(index + 1);
	}
	/**
	 * Devuelve el nombre del path que se le pasa
	 * @param path El nombre del fichero completo con la extension
	 * @return Devuelve un substring que esta entre la primera letra del path y el .
	 */
	public static String getBasename(String path)
	{
		int index = path.lastIndexOf(".");
			if(index == -1)
				return path;
			else
				return path.substring(0, index);
	}
}
//end of class SimpleTTS
