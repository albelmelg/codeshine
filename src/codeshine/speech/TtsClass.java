package codeshine.speech;

import java.io.*;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import java.lang.Thread;
import org.eclipse.jface.util.IPropertyChangeListener;

import codeshine.*;
import codeshine.preferences.IPreferenceConstants;
import codeshine.utils.TokenList;
import codeshine.utils.Token;
import codeshine.utils.Trie;
import org.eclipse.jface.util.PropertyChangeEvent;

public class TtsClass extends Thread{
	String voiceName;
	Voice helloVoice;
	VoiceManager voiceManager;
	String text;
	TokenList profile;
	static int tag = 0;
	static int initPos = 0;
	static int currPos = 0;
	static Token token = null;
	/**
	 * Constructor
	 *
	 */
    public TtsClass(){
    	System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory" +
    			",com.sun.speech.freetts.en.us.cmu_time_awb.AlanVoiceDirectory");
    	this.voiceName = "kevin16";
        System.out.println("Using voice: " + voiceName);
        try{
          this.voiceManager = VoiceManager.getInstance();
        }catch (Exception e){e.printStackTrace();}
        System.out.println("Total of available voices: " + this.voiceManager.getVoices().length);
        this.helloVoice = voiceManager.getVoice(voiceName);
        this.helloVoice.allocate();
        // TODO: Load default profile from preferencesStore.
        System.out.println("Loading default data from: " + Activator.getDefault().getPreferenceStore().getString(IPreferenceConstants.PROFILEPATH));
        this.loadFile(Activator.getDefault().getPreferenceStore().getString(IPreferenceConstants.PROFILEPATH));
        
        IPropertyChangeListener propertyChangeListener = new IPropertyChangeListener(){
         	public void propertyChange(PropertyChangeEvent event){
         		if (event.getProperty().equals(IPreferenceConstants.PROFILEPATH)){
         			System.out.println("TTS: New data stored");
         			loadFile(event.getNewValue().toString());
         		}
         	}
         };
       /** Añadimos nuestro listener al pool de preferencias del plugin.
          De este modo nos notificará cualquier cambio que se produzca y
          podremos actuar en consecuencia
        */
         Activator.getDefault().getPreferenceStore().addPropertyChangeListener(propertyChangeListener);
    }
    /**
     * List all available voices
     *
     */
	private void listAllVoices() {
        System.out.println();
        System.out.println("All voices available:");        
        Voice[] voices = this.voiceManager.getVoices();
        for (int i = 0; i < voices.length; i++) {
            System.out.println("    " + voices[i].getName()
                               + " (" + voices[i].getDomain() + " domain)");
        }
    }
    private String[] getAllVoices() {
    	VoiceManager voiceManager = VoiceManager.getInstance();
    	Voice[] voices = voiceManager.getVoices();
    	String[] voicesNames = new String[voices.length];
    	for (int i = 0; i < voices.length; i++) {
    		voicesNames[i] = voices[i].getName();
    	}
    	return voicesNames;
    }
    private String getText(){
    	return this.text;
    }
    public void setText(String input){
    	this.text = input;
    	System.out.println("SetText__");
//    	this.replaceExpr(profile);

    }
    public void deallocate(){
    	this.helloVoice.deallocate();
    }
    public void speak() {

    	this.replaceExpr(profile);
    	Thread work1 = new Thread(this, "ttsThread");
    	work1.start();
    	System.out.println(currentThread().getName());
    	work1.destroy();


      }
    public void speak(boolean preproccessor){
    	if (!preproccessor){
    		System.out.println("no process");
	    	Thread work1 = new Thread(this, "ttsThread");
	    	work1.start();
    	}
    	else
    		this.speak();
    }
    public void setProfile(TokenList profile){
    	this.profile = profile;
    }
    public void run(){
    	 System.out.println("Voices #:" + voiceManager.getVoices().length);
     	 float pitchValue = Activator.getDefault().getPreferenceStore().getFloat(IPreferenceConstants.PITCH);
     	 System.out.println("TTsClass_pitchValue: " + pitchValue);
     	 helloVoice.setPitch(pitchValue);
     	 helloVoice.speak(this.text);
     	 System.out.println("Despues de Run y speak");
    }
    private void loadFile(String path){
    	
    	File fd = new File(path);
    	try{
    		this.profile = new TokenList(fd);
    	}catch (IOException e){e.printStackTrace();}
//    	System.out.println(this.profile.toString());

    
    }
    private void replaceExpr(TokenList profile){
    	
    	Trie trie = profile.toTrie();
    	System.out.println(trie.toString(" "));
    	System.out.println("_________________");
    	System.out.println(this.text);
    	String result = trackString(this.text, trie);
    	this.text = result;
    	System.out.println("result: " + result);
    }
    private static String trackString(String input, Trie trie){
		
		String ret="";
		char[] chars = input.toCharArray();
    	currPos = 0;
		Token t = null;
		while (currPos < input.length()){
			t = trie.trackRecursive(chars, currPos);
			if (t != null){
				ret += t.getReplacement();
				currPos += t.getValue().length();
				System.out.println("Reemplazo ->" + t.getReplacement());
			}
			else if (currPos<input.length()){
				ret += chars[currPos];
				currPos++;
			}
		}
		return ret;			
		}
    
}