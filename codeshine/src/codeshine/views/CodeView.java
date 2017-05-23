package codeshine.views;

import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import codeshine.Activator;
import codeshine.preferences.IPreferenceConstants;
import codeshine.speech.AudioRecorder;


public class CodeView extends ViewPart implements ISelectionListener {
	private SourceViewer viewer;
	private Action speakAction;
	private Action increaseAction;
	private Action decreaseAction;
	private Action recogAction;
	private ActionFactory.IWorkbenchAction openPreferencesAction;

	private CodeControl codeControl;
	private Color backgroundColor;
	private Color foregroundColor;
	private Color highlightColor;

	private AudioRecorder ar;
	private boolean recogActive;

	
	IPropertyChangeListener preferenceListener = 
		new IPropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent event) {
			if (event
					.getProperty()
					.equals(IPreferenceConstants.BACK_COLOR)){
				
				backgroundColor = new Color(codeControl.getDisplay()
						,PreferenceConverter.getColor(Activator.getDefault().getPreferenceStore()
						,IPreferenceConstants.BACK_COLOR));
				System.out.println("Bkground color: " + backgroundColor.toString());
				codeControl.setBackground(backgroundColor);

			}
			if (event
					.getProperty()
					.equals(IPreferenceConstants.FONT_COLOR)){
				foregroundColor = new Color(codeControl.getDisplay(),
						PreferenceConverter.getColor(Activator.getDefault().getPreferenceStore()
						,IPreferenceConstants.FONT_COLOR));
				codeControl.setForeground(foregroundColor);}
			if (event.getProperty().equals(IPreferenceConstants.HIGHTLIGHT)){
				System.out.println("Cambio en preferencias");
				highlightColor = new Color(codeControl.getDisplay(),
						PreferenceConverter.getColor(Activator.getDefault().getPreferenceStore(),
								IPreferenceConstants.HIGHTLIGHT));
				System.out.println(highlightColor.toString());
				codeControl.setHighLight(highlightColor); 
			}
			if (event
					.getProperty()
					.equals(IPreferenceConstants.FONT_TYPE)){
				Font newFont = new Font(codeControl.getDisplay(),
						PreferenceConverter.getFontData(Activator.getDefault().getPreferenceStore()
						, IPreferenceConstants.FONT_TYPE));
				codeControl.setFont(newFont);}	
			else{
				System.out.println("codeview_" + event.getProperty());
			}
		}
	};
	/**
	 * The constructor.
	 */
	public CodeView() {
	}
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
	
		viewer = new SourceViewer(parent, new VerticalRuler(1), SWT.SCROLL_PAGE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.setInput(getViewSite());
		
		viewer.setDocument(new Document());
		viewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			  public void selectionChanged(SelectionChangedEvent event)
			  {
				  int offset = ((ITextSelection)event.getSelection()).getOffset();
				  System.out.println("Offset: " + offset);
				  
			  }
			});
		makeActions();
		hookContextMenu();
		fillTextWidget();
		contributeToActionBars();
	}
	public void fillTextWidget(){
		IWorkbenchPage page = getSite().getPage();
		page.addSelectionListener(this);
		codeControl = new CodeControl(viewer.getTextWidget());
	}
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {

		if (selection instanceof org.eclipse.jface.viewers.TreeSelection){
			String[] selected = selection.toString().split("/");
			this.notify(selected[selected.length-1]);
		}
		
		if (part instanceof PackageExplorerPart){
			System.out.println("Selection: " + selection.toString());	
			System.out.println("contentDescription: " + ((PackageExplorerPart)part).getContentDescription());
		}
		else if (part instanceof AbstractTextEditor){
			System.out.println(part.getClass().toString());
			ITextEditor editor = (ITextEditor)part;

			IDocumentProvider dp = editor.getDocumentProvider();
			editor.getSelectionProvider().addSelectionChangedListener(new ISelectionChangedListener()
			{
				  public void selectionChanged(SelectionChangedEvent event){
					  int offset = ((ITextSelection)event.getSelection()).getOffset();
					  //int length = ((ITextSelection)event.getSelection()).getLength();
					  codeControl.updatePosition(offset);
				  }
				});
			Document doc2 = (Document)dp.getDocument(editor.getEditorInput());
			ITextSelection select = (ITextSelection)editor.getSelectionProvider().getSelection();
			viewer.setDocument(doc2);
			viewer.setData(getTitle(), "holaaaaa");
			codeControl.updatePosition(select.getOffset());
			System.out.println("Imprimidoooooooo "+select.getOffset());
			System.out.println("Selección cambiada...evento capturado");
		}
		else{
//			System.out.println("debug: " + part.getTitle());
			System.out.println("debug_class: " + part.getClass());
		}
		
	}
	private void notify(String text){
		boolean sEvents = false;
		try{	
			sEvents = Activator.getDefault().getPreferenceStore().getBoolean(IPreferenceConstants.SOUND_EVENTS);
		}catch (Exception e){e.printStackTrace();}
		if (sEvents){
			System.out.println("notifing___" + text);
			Activator.tts.setText(text);
			Activator.tts.speak(false);
		}
	}
	public void setFocus() {	
		viewer.getControl().setFocus();
		
	}
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		Menu menu = menuMgr.createContextMenu(viewer.getTextWidget());
		viewer.getTextWidget().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}
	/*private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Sample View",
			message);
	}*/
	/**
	 * Passing the focus request to the viewer's control.
	 */
	
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}
	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(increaseAction);
		manager.add(new Separator());
		manager.add(decreaseAction);
		manager.add(new Separator());
		manager.add(recogAction);
		manager.add(new Separator());
		manager.add(speakAction);
		manager.add(openPreferencesAction);
	}
	/*private void fillContextMenu(IMenuManager manager) {
		manager.add(increaseAction);
		manager.add(decreaseAction);
		manager.add(recogAction);
		manager.add(new Separator());
		manager.add(speakAction);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	*/
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(new Separator());
		manager.add(increaseAction);
		manager.add(new Separator());
		manager.add(decreaseAction);
		manager.add(new Separator());
		manager.add(recogAction);
		manager.add(new Separator());
		manager.add(speakAction);
		manager.add(openPreferencesAction);
		
	}

	private void makeActions() {
		increaseAction = new Action() {
			public void run() {
				codeControl.increaseFontSize();
			}
		};
		increaseAction.setText("Increase font size");
		increaseAction.setToolTipText("Increase font size");
		increaseAction.setImageDescriptor(Activator.getImageDescriptor("icons/font_increase_icon.gif"));
		decreaseAction = new Action() {
			public void run() {
				codeControl.decreaseFontSize();
			}
		};
		decreaseAction.setText("Decrease font size");
		decreaseAction.setToolTipText("Decrease font size");
		decreaseAction.setImageDescriptor(Activator.getImageDescriptor("icons/font_decrease_icon.gif"));
		speakAction = new Action(){
			public void run() {
				codeControl.speaker();				
			}
		};
		
		/*Reconocimiento de voz COPS*/
		recogAction = new Action() {
			public void run() {
					if (recogActive) {
						System.out.println("Stopping recording");        
						recogAction.setImageDescriptor(Activator.getImageDescriptor("icons/micro.gif"));
						ar.stopRecording();
						System.out.println("Performing recognition");
				        String r=ar.performRecognition();
						//String r="Testing...";
				        System.out.println(r);
				        ar.restoreRecording();
				        System.out.println("Restoring recogniser");
				        codeControl.updateText(r);
						recogActive=false;
					}
					else {
						System.out.println("Starting recording");
						ar.startRecording();
						recogAction.setImageDescriptor(Activator.getImageDescriptor("/icons/micro2.gif"));
						recogActive=true;
					}

			}
		};
		recogAction.setText("Reconocer voz");
		recogAction.setToolTipText("Reconocer voz");
		recogAction.setImageDescriptor(Activator.getImageDescriptor("icons/micro.gif"));
		
		
		speakAction = new Action(){
			public void run() {
				codeControl.speaker();				
			}
		};
		speakAction.setText("Speak selection");
		speakAction.setToolTipText("Speak selected text");
		speakAction.setImageDescriptor(Activator.getImageDescriptor("icons/speech.gif"));
		
		
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		
		openPreferencesAction = ActionFactory.PREFERENCES.create(win);
		
		openPreferencesAction.setText("menu action");
		openPreferencesAction.setToolTipText("show preferences menu");
		openPreferencesAction.setImageDescriptor(Activator.getImageDescriptor("icons/preferences-system.gif"));
	
		
	}
	public void init(IViewSite site) throws PartInitException{
		super.init(site);
		this.setPartName("COPS plugin view");
		this.setContentDescription("COPS description");
		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(this.preferenceListener);
		ar=new AudioRecorder();
		recogActive=false;
	}
	public void dispose() {
		getSite().getPage().removeSelectionListener(this);
		ar.stopRecording();
		ar.terminateRecogniser();
	}
}