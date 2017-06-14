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
import org.apache.log4j.Logger;
/**
 * La principal funcionalidad de esta clase es convertir el texto a voz
 * TextToSpeech
 */
public class TtsClass extends Thread{
	
	String voiceName;
	Voice helloVoice;
	VoiceManager voiceManager;
	String text;
	TokenList profile;
	static int tag;
	static int initPos;
	static int currPos;
	static Token token;
	static Logger logger = Logger.getLogger(TtsClass.class);
	/**
	 * Constructor
	 *
	 */
    public TtsClass(){
    	System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory" +
    			",com.sun.speech.freetts.en.us.cmu_time_awb.AlanVoiceDirectory");
    	this.voiceName = "kevin16";
    	logger.info("Using voice: " + voiceName);
        try{
          this.voiceManager = VoiceManager.getInstance();
        }catch (Exception e){e.printStackTrace();}
        logger.info("Total of available voices: " + this.voiceManager.getVoices().length);
        this.helloVoice = voiceManager.getVoice(voiceName);
        this.helloVoice.allocate();
        logger.info("Loading default data from: " + Activator.getDefault().getPreferenceStore().getString(IPreferenceConstants.PROFILEPATH));
        this.loadFile(Activator.getDefault().getPreferenceStore().getString(IPreferenceConstants.PROFILEPATH));
        
        final IPropertyChangeListener propertyChangeListener = new IPropertyChangeListener(){
         	public void propertyChange(final PropertyChangeEvent event){
         		if (event.getProperty().equals(IPreferenceConstants.PROFILEPATH)){
         			logger.info("TTS: New data stored");
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
     * Informa con un logger del string que se le pasa.
     * @param input Una cadena string
     */
    
    public void setText(String input){
    	this.text = input;
    	logger.info("SetText__");
    }
    /**
     * Cancela la ejecucion del procesamiento de la voz
     * @see com.sun.speech.freetts.Voice
     */
    public void deallocate(){
    	this.helloVoice.deallocate();
    }
    /**
     * Ejecuta un hilo para poder hablar.
     */
    public void speak() {

    	this.replaceExpr(profile);
    	final Thread work1 = new Thread(this, "ttsThread");
    	work1.start();
    	logger.info(currentThread().getName());
      }
    /**
     * Determina si debe empezar la ejecución de habla.
     * @param preproccessor si es F ejecutará un hilo. Si es T llamará al método speak()
     */
    public void speak(boolean preproccessor){
    	if (!preproccessor){
    		logger.info("no process");
	    	final Thread work1 = new Thread(this, "ttsThread");
	    	work1.start();
    	}
    	else
    		this.speak();
    }
    /**
     * Se modifica el parametro de tipo TokenList profile
     * @param profile - El perfil
     */
    public void setProfile(TokenList profile){
    	this.profile = profile;
    }
    /**
     * Se ejecutan los hilos.
     */
    public void run(){
    	 logger.info("Voices #:" + voiceManager.getVoices().length);
     	 final float pitchValue = Activator.getDefault().getPreferenceStore().getFloat(IPreferenceConstants.PITCH);
     	 logger.info("TTsClass_pitchValue: " + pitchValue);
     	 helloVoice.setPitch(pitchValue);
     	 helloVoice.speak(this.text);
     	logger.info("Despues de Run y speak");
    }
    private void loadFile(String path){
    	
    	final File file = new File(path);
    	try{
    		this.profile = new TokenList(file);
    	}catch (IOException e){e.printStackTrace();}
    
    }
    private void replaceExpr(TokenList profile){
    	
    	final Trie trie = profile.toTrie();
    	logger.info(trie.toString(" "));
    	logger.info("_________________");
    	logger.info(this.text);
    	final String result = trackString(this.text, trie);
    	this.text = result;
    	logger.info("result: " + result);
    }
    private static String trackString(String input, Trie trie){
		
		String ret="";
		char[] chars = input.toCharArray();
    	currPos = 0;
		Token token = null;
		while (currPos < input.length()){
			token = trie.trackRecursive(chars, currPos);
			if (token != null){
				ret += token.getReplacement();
				currPos += token.getValue().length();
				logger.info("Reemplazo ->" + token.getReplacement());
			}
			else if (currPos<input.length()){
				ret += chars[currPos];
				currPos++;
			}
		}
		return ret;			
		}
    
}