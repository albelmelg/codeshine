package codeshine.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
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

import codeshine.Activator;
import codeshine.preferences.IPreferenceConstants;




public class CodeControl implements MouseListener{
	protected final StyledText styledText;
	private int STLastOffset;
	private Color highlightColor;
	private Color backgroundColor;
	private Color foregroundColor;
	
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
		    			//System.out.println("Capturado activador de sonido");
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

	protected void updateText(String text)  {
		styledText.update();
		styledText.insert(text+"\n");	
	}
	
	protected void recognitionIatros() throws IOException, InterruptedException{
		
		System.out.println("DENTRO DEL RECONOCEDOR");
		File archivo;
		
		//Espero a que termine y lo vuelco por pantalla una vez leido el fichero.txt
		String linea="sentencia no reconocida";

				Thread.sleep(10000);
				styledText.update();
				archivo = new File ("/tmp/cops.out");
				FileReader fr = null;
				fr = new FileReader (archivo);
				BufferedReader br = new BufferedReader(fr);
				linea = br.readLine();
				styledText.insert(linea+"\n");

		System.out.println("DENTRO DEL RECONOCEDOR2au");
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

