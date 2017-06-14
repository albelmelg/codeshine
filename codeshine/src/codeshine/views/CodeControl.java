package codeshine.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;
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

/**
 *Clase que implementa los metodos necesarios para el funcionamiento de la vista
 */
public class CodeControl implements MouseListener{
	protected final StyledText styledText;
	private int STLastOffset;
	private Color highlightColor;
	private Color backgroundColor;
	private Color foregroundColor;
	static Logger logger = Logger.getLogger(CodeControl.class);
	
	/**
	 * Constructor
	 * @param st Es un atributo de tipo StyledText que permite definir la fuente, color del fondo entre otros.
	 */
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
			
			/**
			 * Metodo que registra cuando se ha pulsado una tecla.
			 */
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
		    		logger.info("");
		    	  }
		      }
		    });
		// Adding move and resize listeners
		styledText.addControlListener(new ControlListener(){
			/**
			 * Metodo que indica cuando se mueve la ventana
			 */
			public void controlMoved(ControlEvent e){
				logger.info("Capturado evento controlMoved");
			}
			/**
			 * Metodo que indica cuando se redimensiona la ventana
			 */
			public void controlResized(ControlEvent e) {
				logger.info("Capturado evento controlResized");
			}
		});

	}
	/**
	 * Devuelve el display (pantalla) a la que esta asociado
	 * @return El display
	 */
	public Display getDisplay(){
		return styledText.getDisplay();
	}
	/**
	 * Establece un color de fondo
	 * @param backgroundColor El nuevo color de fondo
	 */
	
	public void setBackground(Color backgroundColor){
		this.backgroundColor = backgroundColor;
		this.styledText.setBackground(backgroundColor);
		this.styledText.redraw();
	}
	/**
	 * Establece un color para el texto
	 * @param foregroundColor El nuevo color para el texto
	 */
	public void setForeground(Color foregroundColor){
		this.styledText.setForeground(foregroundColor);
	}
	/**
	 * Establece un nuevo color para el resaltado "Color hightlight"
	 * @param hightlight El nuevo color para el resaltado
	 */
	public void setHighLight(Color hightlight){
		this.highlightColor = hightlight;
		this.styledText.setLineBackground(styledText.getCaretOffset(), 1, this.highlightColor);
		this.styledText.redraw();

	}
	/**
	 * Establece una nueva fuente
	 * @param newFont
	 */
	public void setFont(Font newFont){
		this.styledText.setFont(newFont);
		this.redraw();
	}
	/**
	 * 
	 */
	public void redraw(){
		this.styledText.redraw();
	}
	
	protected void speaker(){
		logger.info("evento de habla capturado");
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
			voz.toFile("/tmp/sinte.wav", text);
			voz.close();
			
			//Reproducimos de fichero:
	    	java.applet.AudioClip clip =java.applet.Applet.newAudioClip(new java.net.URL("file:/tmp/sinte.wav"));
	    	clip.play();
	    	
	   
		} catch (java.net.MalformedURLException e) {
			System.out.println("Estoy escribiendo en el fichero");
			e.printStackTrace();
		}
	
 catch (Exception e) {

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
		
		logger.info("DENTRO DEL RECONOCEDOR");
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

				logger.info("DENTRO DEL RECONOCEDOR2au");
	 }
	
	/**
	 * Metodo que ejecuta las preferencias del menu action
	 */
	public void launchPreferences(){
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		ActionFactory.IWorkbenchAction openPreferencesAction = ActionFactory.PREFERENCES.create(win);
		openPreferencesAction.setText("menu action");
		openPreferencesAction.setToolTipText("show preferences menu");
		openPreferencesAction.setImageDescriptor(Activator.getImageDescriptor("icons/preferences-system.gif"));
		openPreferencesAction.run();
	}
	
	/**
	 * Metodo que ejecuta la opcion "Buscar"
	 */
	public void launchFind(){
		logger.info("__");
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
	/**
	 * Metodo que informa de un evento de que el raton se ha movido hacia abajo
	 */
	public void mouseDown(MouseEvent e) {

		if (e.button ==1){
			updatePosition(styledText.getOffsetAtLocation(new Point(e.x, e.y)));
		}
		logger.info("Mouse event: " + e.toString());
	}
	/**
	 * Metodo que informa de un evento de doble click en el raton
	 */
	public void mouseDoubleClick(MouseEvent e) {

		logger.info("Mouse event: " + e.toString());
	}
	/**
	 * Metodo que informa de un evento de que el raton se ha movido hacia arriba
	 */
	public void mouseUp(MouseEvent e) {

		logger.info("Mouse event: " + e.toString());
	}
	

	
}

