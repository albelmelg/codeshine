package codeshine.speech;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.Voice;
public class Ejemplo {
	
public Ejemplo() {
	
}

public static void main(String[] args) {
		try{
			Voice voice = VoiceManager.getInstance().getVoice("kevin16");
			voice.allocate();
			voice.speak("Hello world!");
			voice.deallocate();
		}catch (Exception n){
			n.printStackTrace();
			System.exit(1);}
		}
}
