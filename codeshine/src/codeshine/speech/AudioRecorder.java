/*
 *	AudioRecorder.java
 *
 *	This file is part of jsresources.org
 */

/*
 * Copyright (c) 1999 - 2003 by Matthias Pfisterer
 * Modified for specific iATROS use by CD Martínez Hinarejos (2012)
 * for the COPS project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package codeshine.speech;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;
import org.apache.log4j.Logger;

// IDEA: recording format vs. storage format; possible conversion?
/**	Clase que permite grabar audio y convertirlo en un archivo
	Este programa abre dos lineas: una para grabar
	y otra para reproducir. En un bucle infinito lee de las líneas
	de grabar y las escribe en la de reproducir.

*/
public class AudioRecorder
{
	static Logger logger = Logger.getLogger(AudioRecorder.class);
  private static final SupportedFormat[] SUPPORTED_FORMATS =
  {
     new SupportedFormat("s8",     AudioFormat.Encoding.PCM_SIGNED,    8, true),
     new SupportedFormat("u8",     AudioFormat.Encoding.PCM_UNSIGNED,  8, true),
     new SupportedFormat("s16_le", AudioFormat.Encoding.PCM_SIGNED,   16, false),
     new SupportedFormat("s16_be", AudioFormat.Encoding.PCM_SIGNED,   16, true),
     new SupportedFormat("u16_le", AudioFormat.Encoding.PCM_UNSIGNED, 16, false),
     new SupportedFormat("u16_be", AudioFormat.Encoding.PCM_UNSIGNED, 16, true),
     new SupportedFormat("s24_le", AudioFormat.Encoding.PCM_SIGNED,   24, false),
     new SupportedFormat("s24_be", AudioFormat.Encoding.PCM_SIGNED,   24, true),
     new SupportedFormat("u24_le", AudioFormat.Encoding.PCM_UNSIGNED, 24, false),
     new SupportedFormat("u24_be", AudioFormat.Encoding.PCM_UNSIGNED, 24, true),
     new SupportedFormat("s32_le", AudioFormat.Encoding.PCM_SIGNED,   32, false),
     new SupportedFormat("s32_be", AudioFormat.Encoding.PCM_SIGNED,   32, true),
     new SupportedFormat("u32_le", AudioFormat.Encoding.PCM_UNSIGNED, 32, false),
     new SupportedFormat("u32_be", AudioFormat.Encoding.PCM_UNSIGNED, 32, true),
  };

  private static final String               DEFAULT_FORMAT = "s16_le";
  private static final int                  DEFAULT_CHANNELS = 1;
  private static final float                DEFAULT_RATE = 16000.0F;
  private static final AudioFileFormat.Type DEFAULT_TARGET_TYPE = AudioFileFormat.Type.WAVE;

  /** Atributos para grabar el sonido */
  private String strMixerName;
  private int    nInternalBufferSize;
  private String strFormat;
  private int    nChannels;
  private float  fRate;
  private String strPath;
  private String strExtension;
  private String strFilename;
  private AudioFileFormat.Type targetType;

  /** Atributos para el archivo de sonido */
  private File    outputSignalFile;
  private int     nOutputFormatIndex;
  private int     nBitsPerSample;
  private boolean bBigEndian;
  private int     nFrameSize;
  private AudioFormat          audioFormat;
  private AudioFormat.Encoding encoding;

  /** Atributos para grabar */
  private TargetDataLine targetDataLine;
  private Recorder recorder;

  /** iAtros atributos */
  private String iatrosCeps;
  private String iatrosOff;
  private File outFile;
  private Scanner outFileScanner;
  private Process iatros;

  /** Conversion de  wav a raw */
  private String convert;

  public AudioRecorder() {
    // Grabación del sonido
    strMixerName = null;
    nInternalBufferSize = AudioSystem.NOT_SPECIFIED;
    strFormat = DEFAULT_FORMAT;
    nChannels = DEFAULT_CHANNELS;
    fRate = DEFAULT_RATE;
    strExtension = null;
    strPath="/Documentos/TFG/tfg/iatros_cops/";
    strFilename = strPath+"cops.wav";
    targetType = DEFAULT_TARGET_TYPE;
    // Archivo de sonido
    outputSignalFile = null;
    nOutputFormatIndex = 2;          // For s16_le
    encoding = SUPPORTED_FORMATS[nOutputFormatIndex].getEncoding();;
    nBitsPerSample = SUPPORTED_FORMATS[nOutputFormatIndex].getSampleSize();
    bBigEndian = SUPPORTED_FORMATS[nOutputFormatIndex].getBigEndian();
    nFrameSize = (nBitsPerSample / 8) * nChannels;
    audioFormat = new AudioFormat(encoding, fRate, nBitsPerSample, nChannels, nFrameSize, fRate, bBigEndian);

    // Grabador
    targetDataLine = null;
    recorder = null;

    // iAtros
    iatrosCeps=strPath+"raw2CC";
    iatrosOff=strPath+"iatros-run";
    outFile=new File("/tmp/cops.out");
    iatros = null;

    // Conversión de wav a raw
 
    convert=strPath+"wav2raw";
    // Se ejecuta el reconocedor iAtros 
    try {
      iatros = Runtime.getRuntime().exec("/bin/bash "+iatrosOff);
    } catch (IOException e) {
    	Runtime.getRuntime().exit(-1);
    }

  }

  /**
   * Empieza la grabacion
   */
  public void startRecording() {
    targetDataLine = AudioCommon.getTargetDataLine( strMixerName, audioFormat, nInternalBufferSize);
    if (targetDataLine == null) { out("can't get TargetDataLine, exiting."); System.exit(1); }
    outputSignalFile = new File(strFilename);
    recorder = new DirectRecorder( targetDataLine, targetType, outputSignalFile);
    recorder.start();
  }
/**
 * Detiene la grabación
 */
  public void stopRecording() {
    recorder.stopRecording();
  }
/**
 * Antes de empezar una nueva grabacion se resetea el estado
 */
  public void restoreRecording() {
    // Restore state for new recording
    targetDataLine = null;
    outputSignalFile.delete();
    outputSignalFile = null;
    recorder = null;
  }
/**
 * Empieza el reconociemiento de voz, el audio que graba
 *  pasa por diversas conversiones hasta que se convierte en un String
 * @return Una cadena String donde se refleja lo que le hemos dicho al reconocedor
 * @throws Exception Captura cualquier excepcion
 */
  public String performRecognition() {
    boolean ok=true;
    String result="";

    // Convert wav to raw
     out("Converting wav to raw...");
    try {
      Process conv = Runtime.getRuntime().exec("/bin/bash "+convert);
      conv.waitFor();
    } catch (Exception e) {
    	Runtime.getRuntime().exit(-1);
    }
    out("Done!");

     //Convert raw to CC
     out("Converting raw to CC...");
    try {
      Process iatrosEnc = Runtime.getRuntime().exec("/bin/bash "+iatrosCeps);
      iatrosEnc.waitFor();
    } catch (Exception e) {
    	Runtime.getRuntime().exit(-1);
    }
     out("Done!");

    // Wait for output recognition
    out("Waiting for output...");
    while (!outFile.exists());
     out("Done!");
    do {
      ok=true;
      try {
        outFileScanner=new Scanner(outFile);
        result=processOutput(outFileScanner.nextLine());
        outFile.delete();
      } catch (Exception e) { ok=false; }
    } while (!ok);

    // Return recognition
    return result;

  }
/**
 * Cuando hemos terminado de hablar, pulsamos el boton
 * otra vez y se ejecuta este metodo, el de terminar de ejectarse el reconocedor.
 */
  public void terminateRecogniser() {
    // Terminar iATROS
    try {
      iatros.destroy();
      iatros.waitFor();
      Process iatrosEnc = Runtime.getRuntime().exec("/bin/bash "+iatrosCeps);
      iatrosEnc.waitFor();
    } catch(Exception e) {Runtime.getRuntime().exit(-1);}
  }

  private static void out(String strMessage) {
    logger.info(strMessage);
  }

  private static String processOutput(String src) {
    String dst;
    dst=src.replaceAll("<sil>","");
    dst=dst.replaceAll("<s>","");
    dst=dst.replaceAll("</s>","");
    dst=dst.replaceAll("  *"," ");
    dst=dst.replaceAll(" \\. ",".");
    dst=dst.replaceAll("obr","[");
    dst=dst.replaceAll("cbr","]");
    dst=dst.replaceAll("quo","\"");
    dst=dst.replaceAll("squo","'");
    dst=dst.replaceAll("spc"," ");
    dst=dst.replaceAll(" #STR#","");   // To make strings with united symbols ("hello" instead of " h e l l o ")
    dst=dst.replaceAll(" #ID#","");   // To make identifiers with united symbols (args instead of a r g s)
    dst=dst.replaceAll(" #NUM#","");   // To make numbers with united symbols (103 instead of 1 0 3)
    dst=dst.replaceAll(" #CHAR#","");   // To make char with united symbols ('a' instead of ' a ')
    return dst;
  }

///////////// Clases interiores (inner class)  ////////////////////

  private static class SupportedFormat {
    /** Nombre del formato.  */
    private String m_strName;
    /** El encoding del formato.  */
    private AudioFormat.Encoding m_encoding;
    /** Muestra de tamaño. Este valor es en bits para una muestra simple.  */
    private int m_nSampleSize;
    /** El endianess del formato. */
    private boolean m_bBigEndian;


    /** Contruye un nuevo formato que sea soportado.
        @param strName - El nombre de formato.
        @param encoding - El encoding del formato.
        @param nSampleSize - El tamaño de la muestra en bits.
        @param bBigEndian - El endianess del formato.
    */
    public SupportedFormat(String strName, AudioFormat.Encoding encoding, int nSampleSize, boolean bBigEndian) {
      m_strName = strName;
      m_encoding = encoding;
      m_nSampleSize = nSampleSize;
    }

    /** Devuelve el encoding del formato.  */
    public AudioFormat.Encoding getEncoding() { return m_encoding; }

    /** Devuelve el tamaño de la muestra en bits  */
    public int getSampleSize() { return m_nSampleSize; }

    /** Devuelve el endianess del formato */
    public boolean getBigEndian() { return m_bBigEndian; }
  }

  /**
   * Interfaz para la grabación
   */

  public interface Recorder {
	  /**
	   * Empezar la grabación
	   */
    public void start();
    /**
     * Detener la grabación
     */
    public void stopRecording();
  }


  public class AbstractRecorder extends Thread implements Recorder {
    protected TargetDataLine        mline;
    protected AudioFileFormat.Type  mtargetType;
    protected File                  mfile;
    protected boolean               mbRecording;

    public AbstractRecorder(TargetDataLine line, AudioFileFormat.Type targetType, File file) {
      mline = line;
      mtargetType = targetType;
      mfile = file;
    }

    /**     Empieza la grabación. Para realizar esto (i) la línea empieza (ii)
     * 		se ejecuta el thread.
      */
    public void start() {
      mline.start();
      super.start();
    }
    /**
     * Detiene la grabacion. La linea debe pararse despues de que se quede vacia
     * (con el método drain())
     */
    public void stopRecording() {
      mline.stop();
      mline.drain();
      mline.close();
      mbRecording = false;
    }
  }
  
  /**
   * Clase que sirve para poder hacer la grabacion en directo
   *
   */

  public class DirectRecorder extends AbstractRecorder {
    private AudioInputStream maudioInputStream;
    /**
     * Constructor
     * @param line La línea
     * @param targetType Objeto que tiene el tipo de archivo
     * @param file El archivo final
     */
    public DirectRecorder(TargetDataLine line, AudioFileFormat.Type targetType, File file) {
      super(line, targetType, file);
      maudioInputStream = new AudioInputStream(line);
    }
    /**
     * Escribe una secuencia de bytes que representan un archivo de audio
     * del tipo de archivo especificado (mTargetType) en el archivo externo proporcionado (mfile)
     */
    public void run() {
      try {
        AudioSystem.write( maudioInputStream, mtargetType, mfile);
      } catch (IOException e) {

      }
    }
  }

}

