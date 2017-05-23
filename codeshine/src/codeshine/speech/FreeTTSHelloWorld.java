package codeshine.speech;
/**
 * Copyright 2003 Sun Microsystems, Inc.
 * 
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL 
 * WARRANTIES.
 */
import com.sun.speech.freetts.*;
//import com.sun.speech.freetts.Voice;
//import com.sun.speech.freetts.VoiceManager;
//import com.sun.speech.freetts.audio.JavaClipAudioPlayer;

/**
 * Simple program to demonstrate the use of the FreeTTS speech
 * synthesizer.  This simple program shows how to use FreeTTS
 * without requiring the Java Speech API (JSAPI).
 */
public class FreeTTSHelloWorld extends Thread{
	String voiceName;
	Voice helloVoice;
	VoiceManager voiceManager;
    /**
     * Example of how to list all the known voices.
     */
    public static void listAllVoices() {
        System.out.println();
        System.out.println("All voices available:");        
        VoiceManager voiceManager = VoiceManager.getInstance();
        Voice[] voices = voiceManager.getVoices();
        for (int i = 0; i < voices.length; i++) {
            System.out.println("    " + voices[i].getName()
                               + " (" + voices[i].getDomain() + " domain)");
        }
    }
    public void speak(){
    	try{
        this.helloVoice.allocate();
    	}catch(Exception e){e.printStackTrace();} 
         /* Synthesize speech.
          */
    	 helloVoice.setPitch(90);
    	 System.out.println("voiceInfo: " + this.voiceManager.getVoiceInfo());
    	 this.helloVoice.speak("It sounds so nice. "
                          + "I'm so glad to say hello to this world.");

         /* Clean up and leave.
          */
         this.helloVoice.deallocate();
      
    }
    public FreeTTSHelloWorld() {
    	
    	System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory,com.sun.speech.freetts.en.us.cmu_time_awb.AlanVoiceDirectory");
    	
    	//    	System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_time_awb.AlanVoiceDirectory");
        this.voiceName = "kevin16";
        System.out.println();
        System.out.println("Using voice: " + voiceName);
      
        try{
        this.voiceManager = VoiceManager.getInstance();
        }catch (Exception e){e.printStackTrace();}
        System.out.println(this.voiceManager.getVoices().length);
                    
        for (int i=0;i<voiceManager.getVoices().length;i++){
        	System.out.println(voiceManager.getVoices()[i].getName());
        }
        this.helloVoice = voiceManager.getVoice(voiceName);   	
    }
    public void run(){
    	this.speak();
    }
}
