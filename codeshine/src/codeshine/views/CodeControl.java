package codeshine.views;

import java.awt.Cursor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.sun.speech.freetts.audio.AudioPlayer;
import com.sun.speech.freetts.audio.NullAudioPlayer;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;

import codeshine.Activator;
import codeshine.preferences.IPreferenceConstants;

import com.sun.speech.freetts.audio.AudioPlayer;
import com.sun.speech.freetts.audio.NullAudioPlayer;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;
import javax.sound.sampled.AudioSystem;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;




public class CodeControl implements MouseListener{
	protected final StyledText styledText;
	private int STLastOffset;
	private Color highlightColor;
	private Color backgroundColor;
	private Color foregroundColor;
	//TODO:get color from preferences store
	
	public CodeControl(StyledText st) {
		styledText = st;

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		RGB fontColor = PreferenceConverter.getColor(store,IPreferenceConstants.FONT_COLOR);
		RGB backColor = PreferenceConverter.getColor(store, IPreferenceConstants.BACK_COLOR);
		RGB highColor = PreferenceConverter.getColor(store, IPreferenceConstants.HIGHTLIGHT);
		Font font = new Font(styledText.getDisplay(), PreferenceConverter.getFontData(store, IPreferenceConstants.FONT_TYPE));
		
		this.highlightColor = new Color(styledText.getDisplay(),highColor);
		this.backgroundColor = new Color(styledText.getDisplay(), backColor);
		this.foregroundColor = new Color(styledText.getDisplay(), fontColor);
		
		styledText.setFont(font);
		styledText.setBackground(backgroundColor);
		styledText.setForeground(foregroundColor);
		this.setHighLight(highlightColor);
		
		styledText.addMouseListener(this);
		
		styledText.addKeyListener(new KeyAdapter() {
		      public void keyPressed(KeyEvent e){
	
		    	  updatePosition(styledText.getCaretOffset());
		       	  switch (e.keyCode){
		    	  case (int)SWT.KEYPAD_ADD://Ctrl +
		    	   	if ((e.stateMask & SWT.CTRL) != 0)
		    	   		increaseFontSize();
		    	  		break;
		    	  case (int)SWT.KEYPAD_SUBTRACT://Ctrl -
		    		if ((e.stateMask & SWT.CTRL) != 0)
		    			decreaseFontSize();
		    	  		break;
		    	  case (int)116://Ctrl + t
		    		if ((e.stateMask & SWT.CTRL) != 0){
		    			System.out.println("Capturado activador de sonido");
		    			speaker();}
		    			break;
		    	  case (int)112://Ctrl + p
		    		  if ((e.stateMask & SWT.CTRL) != 0){
		    			launchPreferences();
		    		  }
		    		  break;
		    	  case (int)102://Ctrl + f
		    		  if ((e.stateMask & SWT.CTRL) != 0){
		    			launchFind();
		    		  }
		    	  	  break;
		    	  default:
		    		System.out.print("");
		    	  }
		      }
		    });
		// Adding move and resize listeners
		styledText.addControlListener(new ControlListener(){
			//TODO: Capture scroll event
			public void controlMoved(ControlEvent e){
				System.out.println("Capturado evento controlMoved");
			}
			public void controlResized(ControlEvent e) {
				System.out.println("Capturado evento controlResized");
			}
		});
		
		// TODO Auto-generated constructor stub
	}
	public Display getDisplay(){
		return styledText.getDisplay();
	}
	public void setBackground(Color backgroundColor){
		this.backgroundColor = backgroundColor;
		this.styledText.setBackground(backgroundColor);
		this.styledText.redraw();
	}
	public void setForeground(Color foregroundColor){
		this.styledText.setForeground(foregroundColor);
	}
	public void setHighLight(Color hightlight){
		this.highlightColor = hightlight;
		this.styledText.setLineBackground(styledText.getCaretOffset(), 1, this.highlightColor);
		this.styledText.redraw();
		//		this.updatePosition(styledText.getCaretOffset());
	}
	public void setFont(Font newFont){
		this.styledText.setFont(newFont);
		this.redraw();
	}
	public void redraw(){
		this.styledText.redraw();
	}
	
	protected void speaker(){
		System.out.println("evento de habla capturado");
		String text = styledText.getSelectionText();
		/**
		 * If no selected text. play current line.
		 */
		if (text.length()==0){
			int line = styledText.getLineAtOffset(styledText.getCaretOffset());
			int offset = styledText.getOffsetAtLine(line);
			int endOffset = styledText.getOffsetAtLine(line+1);
			text = styledText.getText(offset, endOffset-2);

		}
// TODO: Hacer más transparente el proceso

// Incluir la carga de perfiles y el preproceso dentro del sintetizador.
		System.out.println("original text: " + text);
		System.out.println("original lenght: " + text.length());
		
		try {
			File file = new File("/tmp/sinte.wav");
			EscribirEnFichero1 voz = new EscribirEnFichero1("kevin16");
			System.out.println("Hola "+file.getName());
			//voz.toFile(file.getName(), text);
			voz.toFile("/tmp/sinte.wav", text);
			voz.close();
			
			//Reproducimos de fichero:
	    	java.applet.AudioClip clip =java.applet.Applet.newAudioClip(new java.net.URL("file:/tmp/sinte.wav"));
	    	clip.play();
	    	
	   
		} catch (java.net.MalformedURLException e) {
			// TODO Auto-generated catch block
			System.out.println("Estoy escribiendo en el fichero");
			e.printStackTrace();
		}
		//Activator.tts.setText(text);
		//Activator.tts.speak();	
 catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	protected void increaseFontSize(){
		Font current_font = styledText.getFont();
	  	FontData[] fd = current_font.getFontData();
	  	int fontHeight = fd[0].getHeight();
	  	fd[0].setHeight(fontHeight + 2);
	  	styledText.setFont(new Font(styledText.getDisplay(),fd[0]));
	}
	protected void decreaseFontSize(){
		Font current_font = styledText.getFont();
	  	FontData[] fd = current_font.getFontData();
	  	int fontHeight = fd[0].getHeight();
	  	fd[0].setHeight(fontHeight - 2);
	  	styledText.setFont(new Font(styledText.getDisplay(),fd[0]));
	 }
	
	
	/*Reconocedor de voz*//*
	protected void recognitionIatros2() throws IOException, InterruptedException{
		styledText.update();
		File archivo = new File ("/home/beowulf/fichero.txt");
		archivo = new File ("/home/beowulf/fichero.txt");
		FileReader fr = null;
	    fr = new FileReader (archivo);
	    BufferedReader br = new BufferedReader(fr);
		String linea = br.readLine();
		styledText.insert(linea+"\n");
		
	}
	*/
	protected void updateText(String text)  {
		styledText.update();
		styledText.insert(text+"\n");	
	}
	
	protected void recognitionIatros() throws IOException, InterruptedException{
		
		System.out.println("DENTRO DEL RECONOCEDOR");
		/* directorio/ejecutable es el path del ejecutable y un nombre */ 
		  /*
			   String command = "/bin/bash -c terminal";
			   try {

			   Process p = Runtime.getRuntime().exec ("sudo terminal");
			   //Process p = Runtime.getRuntime().exec ("env LD_LIBRARY_PATH=/home/beowulf/Dropbox/proyecto_cops/iatros-v1.0/build/lib /home/beowulf/Dropbox/proyecto_cops/iatros-v1.0/build/bin/iatros-speech-online -c /home/beowulf/Dropbox/proyecto_cops/eutransMiriam/conf/fsm2_conCHAUMEAmpliadoNumero.cnf -p /home/beowulf/Dropbox/proyecto_cops/eutransMiriam/conf/preprocessiAtrosOnline.cnf");
			/* Se prepara un bufferedReader para poder leer la salida más comodamente. */ 
		/*	
			InputStream is = p.getInputStream(); 
            BufferedReader br = new BufferedReader (new InputStreamReader (is)); 
            String aux = br.readLine();
            
            while (aux!=null){ 
            	System.out.println("DENTRO DEL RECONOCEDOR while");
                System.out.println (aux); 
                aux = br.readLine(); 
            }
            
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		*/
		// 	/home/beowulf/Dropbox/proyecto_cops/iatros-v1.0/build/bin/iatros-speech-online
		String command = "sudo /home/beowulf/Dropbox/proyecto_cops/iatros-v1.0/build/bin/iatros-speech-online";
		String command1 = "sudo /etc/init.d/networking restart";
		String command2 [] = {"gnome-terminal","-e","env LD_LIBRARY_PATH=/home/beowulf/proyecto_cops/iatros-v1.0/build/lib /home/beowulf/proyecto_cops/iatros-v1.0/build/bin/iatros-speech-online -c /home/beowulf/proyecto_cops/eutransMiriam/conf/fsm_final.cnf -p /home/beowulf/proyecto_cops/eutransMiriam/conf/preprocessiAtrosOnline.cnf"};
		//String command2 [] = {"gnome-terminal","-e","telnet "+"google.com"};
		
		//Borro el archivo antes de escribir
		//File archivo = new File ("/home/beowulf/fichero.txt");
		File archivo;
		//archivo.delete();
		
		//Ejecuto el comando de gnome terminal
		Runtime runtime = Runtime.getRuntime();
		// Process process = runtime.exec(command2);
		
		
		//Espero a que termine y lo vuelco por pantalla una vez leido el fichero.txt
		String linea="sentencia no reconocida";
				
				//process.waitFor();
				Thread.sleep(10000);
				
				//System.out.println("WOLAAAAAAAAAAAA");
				styledText.update();
				archivo = new File ("/tmp/cops.out");
				FileReader fr = null;
				fr = new FileReader (archivo);
				BufferedReader br = new BufferedReader(fr);
				linea = br.readLine();
				styledText.insert(linea+"\n");
				
				

		System.out.println("DENTRO DEL RECONOCEDOR2au");
		
		//int line = styledText.getLineAtOffset(styledText.getCaretOffset());
		//int offset = styledText.getOffsetAtLine(line);
		//int endOffset = styledText.getOffsetAtLine(line+1);
		//styledText.setCaretOffset(offset);
		//styledText.setBounds(0, 10, 100, 100);
		
		//styledText.setText("PROBANDO");//Text(offset, endOffset-2);
//		System.out.println("ProbandoOOWQOWOWOQWOQWQWOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
	//	StyleContext context = new StyleContext();
	   // StyledDocument document = new DefaultStyledDocument(context);
/*	    try {
			document.insertString(5,"funciona",null);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//insertString(document.getLength(), "Hello www.java2s.com", attributes);
		*/
		//styledText.setCaretOffset(5);
		//int offset=styledText.getCaretOffset();
		//styledText.setCaretOffset(20);
		//int line=styledText.getLineAtOffset(offset);
		
		//Point point= new Point(0,0);
		//point =styledText.getLocation();

		//int off=styledText.getOffsetAtLocation(new Point(3, 3));


		
		
	/*	
		StyledText sv = getSourceViewer().getTextWidget() ;
		int offset = sv.getCaretOffset() ;
		
		
		sv.setSelection(0, offset) ;
		ignore = true ; //to ensure the textChanged doesn't get executed a
		
		
		sv.insert("WOLAAAAAAAAASDDASDAD") ;
		ignore = false ;
		
		sv.setCaretOffset(0); 
*/
		
		
		//Font current_font = styledText.getFont();
	  	//FontData[] fd = current_font.getFontData();
	  	//int fontHeight = fd[0].getHeight();
	  	//fd[0].setHeight(fontHeight - 2);
	  	//styledText.setFont(new Font(styledText.getDisplay(),fd[0]));
	 }
	
	
	public void launchPreferences(){
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		ActionFactory.IWorkbenchAction openPreferencesAction = ActionFactory.PREFERENCES.create(win);
		openPreferencesAction.setText("menu action");
		openPreferencesAction.setToolTipText("show preferences menu");
		openPreferencesAction.setImageDescriptor(Activator.getImageDescriptor("icons/preferences-system.gif"));
		openPreferencesAction.run();
	}
	public void launchFind(){
		System.out.println("__");
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		ActionFactory.IWorkbenchAction openFindAction = ActionFactory.FIND.create(win);
		openFindAction.setText("Find");
		openFindAction.setToolTipText("Find");
		openFindAction.setImageDescriptor(Activator.getImageDescriptor("icons/preferences-system.gif"));
		openFindAction.run();
	}
	protected void updatePosition(int offset){
		
	  	  IWorkbench wb = PlatformUI.getWorkbench();
		  IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		  IWorkbenchPage page = win.getActivePage();
		  
		  AbstractTextEditor abseditor = (AbstractTextEditor) page.getActiveEditor();
		  try{
			  styledText.setLineBackground(styledText.getLineAtOffset(STLastOffset), 1 ,styledText.getBackground());
			  styledText.setLineBackground(styledText.getLineAtOffset(offset), 1, highlightColor);
			  styledText.redraw();
			  STLastOffset = offset;
		  }catch (Exception documentException){documentException.printStackTrace();}
		  
		  styledText.setCaretOffset(offset);
		  styledText.showSelection();
		  abseditor.setHighlightRange(offset, 5, true);

		}
	public void mouseDown(MouseEvent e) {
		// TODO Auto-generated method stub
		if (e.button ==1){
			updatePosition(styledText.getOffsetAtLocation(new Point(e.x, e.y)));
		}
		System.out.println("Mouse event: " + e.toString());
	}
	public void mouseDoubleClick(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Mouse event: " + e.toString());
	}
	public void mouseUp(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Mouse event: " + e.toString());
	}
	

	
}

