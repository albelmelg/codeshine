/*
 *	AudioCommon.java
 *
 *	This file is part of jsresources.org
 * Copyright (c) 1999 - 2001 by Matthias Pfisterer
 */

/**
 * 
 */

package codeshine.speech;


import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;



/** Metodos comunes para ejemplos de muestras de audio.
 */
public class AudioCommon
{
	private static boolean		debug = false;
	static Logger logger = Logger.getLogger(AudioCommon.class);

	/**
	 * Modifica el nombre de debug
	 * @param bDebug boolean
	 */
	public static void setDebug(final boolean bDebug)
	{
		debug = bDebug;
	}
	/**
	 * Lista los destinos compatibles
	 */
	public static void listSupportedTargetTypes()
	{
		String	strMessage = "Supported target types:";
		final AudioFileFormat.Type[]	aTypes = AudioSystem.getAudioFileTypes();
		for (int i = 0; i < aTypes.length; i++)
		{
			strMessage += " " + aTypes[i].getExtension();
		}
		out(strMessage);
	}

	/**	
	 * Metodo que intenta obtener un tipo de archivo
	 * de audio para la extension que le pasamos.
	 * Examina todos los tipos de archivo disponibles
	 * y si la extension que le pasamos coincide la devuelve.
	 * 
	 * @param strExtension La extensión
	 * @return un objeto que coinicide con la extensión que le pasamos
	 * @return null si no coincide nada
	*/
	public static AudioFileFormat.Type findTargetType(final String strExtension)
	{
		AudioFileFormat.Type[]	aTypes = AudioSystem.getAudioFileTypes();
		for (int i = 0; i < aTypes.length; i++)
		{
			if (aTypes[i].getExtension().equals(strExtension))
			{
				return aTypes[i];
			}
		}
		return null;
	}
	/**
	 * Crea una lista con los mixers que hay,
	 * si no hay ninguno, sale de la ejecucion.
	 */
	public static void listMixersAndExit()
	{
		out("Available Mixers:");
		final Mixer.Info[] aInfos = AudioSystem.getMixerInfo();
		for (int i = 0; i < aInfos.length; i++)
		{
			out(aInfos[i].getName());
		}
		if (aInfos.length == 0)
		{
			out("[No mixers available]");
		}
		Runtime.getRuntime().exit(0);
	}



	/** Lista los mixers.
		Solo los mixers que soportan TargetDataLines or SourceDataLines
		se añaden a la lista dependiendo del valor de bPlayback.
		Si no encuentra nada sale de la ejecución. 
		@param bPlayback True o False
	**/
	public static void listMixersAndExit(final boolean bPlayback)
	{
		out("Available Mixers:");
		final Mixer.Info[]	aInfos = AudioSystem.getMixerInfo();
		for (int i = 0; i < aInfos.length; i++)
		{
			final Mixer mixer = AudioSystem.getMixer(aInfos[i]);
			final Line.Info lineInfo = new Line.Info(bPlayback ?
											   SourceDataLine.class :
											   TargetDataLine.class);
			if (mixer.isLineSupported(lineInfo))
			{
				out(aInfos[i].getName());
			}
		}
		if (aInfos.length == 0)
		{
			out("[No mixers available]");
		}
		Runtime.getRuntime().exit(0);
	}



	/**
	 * Este método trata de devolver un Mixer.
	 * Se busca en un array un mixer que coincida
	 * con el strMixerName que se le pasa
	 * @param strMixerName - nombre del mixer a buscar
	 * @return el objeto Mixer si lo encuentra, null si no.
	*/
	public static Mixer.Info getMixerInfo(final String strMixerName)
	{
		final Mixer.Info[]	aInfos = AudioSystem.getMixerInfo();
		for (int i = 0; i < aInfos.length; i++)
		{
			if (aInfos[i].getName().equals(strMixerName))
			{
				return aInfos[i];
			}
		}
		return null;
	}
	/**
	 * Método que devuelve un TargetDataLine.
	 * Cómo esto es complicado, se tiene que contruir un objeto Info
	 * que especifique las propiedades que buscamos en la linea.
	 * Primero se especifica que tipo de línea queremos: 
	 * SourceDataline (para  reproducir), TargetDataLine (para grabar),
	 * Clip (repite la reproducción).
	 * En este caso queremos hacer una captura normal, por lo que pedimos
	 * un TargetDataLine.
	 * Después, tenemos que pasar un objeto AudioFormat,
	 * para que la Linea sepa el formato de dato que le pasaremos.
	 * Java Sound establecerá un tamaño del buffer por defecto.
	 * 
	 * @param strMixerName Define el nombre del Mixer
	 * @param audioFormat Objeto de tipo AudioFormat
	 * @param nBufferSize Tamaño del buffer
	 * @return null o el objeto TargetDataLine
	 */
	public static TargetDataLine getTargetDataLine(String strMixerName,
							final AudioFormat audioFormat,
							final int nBufferSize)
	{
	     BasicConfigurator.configure();
		TargetDataLine	targetDataLine = null;
		DataLine.Info	info = new DataLine.Info(TargetDataLine.class,
							 audioFormat, nBufferSize);
		try
		{
			if (strMixerName != null)
			{
				final Mixer.Info	mixerInfo = getMixerInfo(strMixerName);
				if (mixerInfo == null)
				{
					out("AudioCommon.getTargetDataLine(): mixer not found: " + strMixerName);
					return null;
				}
				final Mixer	mixer = AudioSystem.getMixer(mixerInfo);
				targetDataLine = (TargetDataLine) mixer.getLine(info);
			}
			else
			{
				if (debug) { out("AudioCommon.getTargetDataLine(): using default mixer"); }
				targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
			}

			/*
			 *	La línea está, pero aún no está lista para recibir
			 *	datos del audio. Tenemos que abrirla.
			 */
			if (debug) { out("AudioCommon.getTargetDataLine(): opening line..."); }
			targetDataLine.open(audioFormat, nBufferSize);
			if (debug) { out("AudioCommon.getTargetDataLine(): opened line"); }
		}
		catch (LineUnavailableException e)
		{
			if (debug) { logger.error("Error", e); }
		}
		catch (Exception e)
		{
			if (debug) {logger.error("Error", e); }
		}
			if (debug) { out("AudioCommon.getTargetDataLine(): returning line: " + targetDataLine); }
		return targetDataLine;
	}


	/** Verifica si la codificació es PCM
	 * @param encoding La codificacion
	 * @return True o False
	 */
	public static boolean isPcm(AudioFormat.Encoding encoding)
	{
		return encoding.equals(AudioFormat.Encoding.PCM_SIGNED)
			|| encoding.equals(AudioFormat.Encoding.PCM_UNSIGNED);
	}
	
	/** Método que envía un mensaje
	 * @param strMessage - El mensaje
	 **/
	
	private static void out(String strMessage)
	{
		logger.info(strMessage);
	}

}

