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

/*
|<---            this code is formatted to fit into 80 columns             --->|
*/

/*
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
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

// IDEA: recording format vs. storage format; possible conversion?
/**	<titleabbrev>AudioRecorder</titleabbrev>
	<title>Recording to an audio file (advanced version)</title>

	<formalpara><title>Purpose</title>
	<para>
	This program opens two lines: one for recording and one
	for playback. In an infinite loop, it reads data from
	the recording line and writes them to the playback line.
	</para></formalpara>

	<formalpara><title>Usage</title>
	<para>
	<synopsis>java AudioRecorder</synopsis>
	</para></formalpara>

	<formalpara><title>Bugs, limitations</title>
	<para>
	There is no way to stop the program besides brute force
	(ctrl-C). There is no way to set the audio quality.
	</para></formalpara>
*/
public class AudioRecorder
{
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

  /** Attributes for sound recording */
  private String strMixerName;
  private int    nInternalBufferSize;
  private String strFormat;
  private int    nChannels;
  private float  fRate;
  private String strPath;
  private String strExtension;
  private String strFilename;
  private AudioFileFormat.Type targetType;

  /** Attributes for sound file */
  private File    outputSignalFile;
  private int     nOutputFormatIndex;
  private int     nBitsPerSample;
  private boolean bBigEndian;
  private int     nFrameSize;
  private AudioFormat          audioFormat;
  private AudioFormat.Encoding encoding;

  /** Recorder attributes */
  private TargetDataLine targetDataLine;
  private Recorder recorder;

  /** iAtros attributes */
  private String iatrosCeps;
  private String iatrosOff;
  private File outFile;
  private Scanner outFileScanner;
  private Process iatros;

  /** Convertion wav to raw */
  private String convert;

  public AudioRecorder() {
    // Sound recording
    strMixerName = null;
    nInternalBufferSize = AudioSystem.NOT_SPECIFIED;
    strFormat = DEFAULT_FORMAT;
    nChannels = DEFAULT_CHANNELS;
    fRate = DEFAULT_RATE;
    strExtension = null;
    strPath="/Documentos/TFG/tfg/iatros_cops/";
    strFilename = strPath+"cops.wav";
    targetType = DEFAULT_TARGET_TYPE;
    System.out.println("Hasta aquí me ejecuto 1");
    // Sound file
    outputSignalFile = null;
    nOutputFormatIndex = 2;          // For s16_le
    encoding = SUPPORTED_FORMATS[nOutputFormatIndex].getEncoding();;
    nBitsPerSample = SUPPORTED_FORMATS[nOutputFormatIndex].getSampleSize();
    bBigEndian = SUPPORTED_FORMATS[nOutputFormatIndex].getBigEndian();
    nFrameSize = (nBitsPerSample / 8) * nChannels;
    audioFormat = new AudioFormat(encoding, fRate, nBitsPerSample, nChannels, nFrameSize, fRate, bBigEndian);

    // Recorder
    targetDataLine = null;
    recorder = null;

    // iAtros
    iatrosCeps=strPath+"raw2CC";
    iatrosOff=strPath+"iatros-run";
    outFile=new File("/tmp/cops.out");
    iatros = null;

    // Convertion from wav to raw
 
    convert=strPath+"wav2raw";
    // Start iAtros recogniser
    try {
      iatros = Runtime.getRuntime().exec("/bin/bash "+iatrosOff);
    } catch (IOException e) {
      System.exit(-1);
    }

  }

  public void startRecording() {
    targetDataLine = AudioCommon.getTargetDataLine( strMixerName, audioFormat, nInternalBufferSize);
    if (targetDataLine == null) { out("can't get TargetDataLine, exiting."); System.exit(1); }
    outputSignalFile = new File(strFilename);
    recorder = new DirectRecorder( targetDataLine, targetType, outputSignalFile);
    recorder.start();
  }

  public void stopRecording() {
    recorder.stopRecording();
  }

  public void restoreRecording() {
    // Restore state for new recording
    targetDataLine = null;
    outputSignalFile.delete();
    outputSignalFile = null;
    recorder = null;
  }

  public String performRecognition() {
    boolean ok=true;
    String result="";

    // Convert wav to raw
     out("Converting wav to raw...");
    try {
      Process conv = Runtime.getRuntime().exec("/bin/bash "+convert);
      conv.waitFor();
    } catch (Exception e) {
      System.exit(-1);
    }
    out("Done!");

     //Convert raw to CC
     out("Converting raw to CC...");
    try {
      Process iatrosEnc = Runtime.getRuntime().exec("/bin/bash "+iatrosCeps);
      iatrosEnc.waitFor();
    } catch (Exception e) {
      System.exit(-1);
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

  public void terminateRecogniser() {
    // Terminate iATROS
    try {
      iatros.destroy();
      iatros.waitFor();
      // Process iatrosEnc = Runtime.getRuntime().exec("/bin/bash -c "+iatrosCeps);
      Process iatrosEnc = Runtime.getRuntime().exec("/bin/bash "+iatrosCeps);
      iatrosEnc.waitFor();
    } catch(Exception e) {}
  }

  private static void out(String strMessage) {
    System.out.println(strMessage);
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

///////////// inner classes ////////////////////

  private static class SupportedFormat {
    /** The name of the format.  */
    private String m_strName;
    /** The encoding of the format.  */
    private AudioFormat.Encoding m_encoding;
    /** The sample size of the format.  This value is in bits for a single sample
        (not for a frame).  */
    private int m_nSampleSize;
    /** The endianess of the format. */
    private boolean m_bBigEndian;
    // sample size is in bits

    /** Construct a new supported format.
        @param strName the name of the format.
        @param encoding the encoding of the format.
        @param nSampleSize the sample size of the format, in bits.
        @param bBigEndian the endianess of the format.
    */
    public SupportedFormat(String strName, AudioFormat.Encoding encoding, int nSampleSize, boolean bBigEndian) {
      m_strName = strName;
      m_encoding = encoding;
      m_nSampleSize = nSampleSize;
    }

    /** Returns the name of the format.  */
    //public String getName() { return m_strName; }

    /** Returns the encoding of the format.  */
    public AudioFormat.Encoding getEncoding() { return m_encoding; }

    /** Returns the sample size of the format.  This value is in bits.  */
    public int getSampleSize() { return m_nSampleSize; }

    /** Returns the endianess of the format.  */
    public boolean getBigEndian() { return m_bBigEndian; }
  }


  public interface Recorder {
    public void start();
    public void stopRecording();
  }


  public class AbstractRecorder extends Thread implements Recorder {
    protected TargetDataLine        m_line;
    protected AudioFileFormat.Type  m_targetType;
    protected File                  m_file;
    protected boolean               m_bRecording;

    public AbstractRecorder(TargetDataLine line, AudioFileFormat.Type targetType, File file) {
      m_line = line;
      m_targetType = targetType;
      m_file = file;
    }

    /**     Starts the recording. To accomplish this, (i) the line is started and (ii) the
      *      thread is started.
      */
    public void start() {
      m_line.start();
      super.start();
    }

    public void stopRecording() {
      // for recording, the line needs to be stopped before draining (especially if you're still
      // reading from it)
      m_line.stop();
      m_line.drain();
      m_line.close();
      m_bRecording = false;
      System.out.println("Me ejecuto");
    }
  }

  public class DirectRecorder extends AbstractRecorder {
    private AudioInputStream m_audioInputStream;

    public DirectRecorder(TargetDataLine line, AudioFileFormat.Type targetType, File file) {
      super(line, targetType, file);
      m_audioInputStream = new AudioInputStream(line);
    }

    public void run() {
      try {
        AudioSystem.write( m_audioInputStream, m_targetType, m_file);
      } catch (IOException e) {

      }
    }
  }

}

