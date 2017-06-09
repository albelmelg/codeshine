package codeshine.utils;
import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.helpers.DefaultHandler;

import codeshine.preferences.IPreferenceConstants;

public class TokenList extends DefaultHandler{
	
	String[][] configData = {{"/**", "Javadoc string", "Cadena de documentación"},
		{"//", "single line comment","Comentario a una linea"},
			{"/*", "multiline comment", "Comentario de varias lineas"},
			{"*/", "end of comment", "fin de comentario"}};

	final List list = new ArrayList();
	final Hashtable table = new Hashtable();
	
	public static void main(String[] argv){
			TokenList tl = null;
			try{
				tl = new TokenList("/git/codeshine/codeshine/src/codeshine/speech");
			}catch (InvalidObjectException e){e.printStackTrace();}
		
	}

	public TokenList() {
		for (int i=0; i<configData.length; i++){

			
			Token t = new Token(configData[i][0],
									configData[i][1],
									configData[i][2]);
			list.add(t);
			table.put(configData[i][0], t);
		}
	}
	/**
	 * 
	 * 	 
	 * @param configFile
	 */
	public TokenList(File fInput) throws InvalidObjectException{
		DefaultHandler handler = new XMLHandler(this);
        // Use the default (non-validating) parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            // Set up output stream
        	 // Parse the input
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(fInput, handler);
            
        }catch (Throwable t) {}
        this.addToken(new Token(IPreferenceConstants.SPECIAL,"...","..."));
	}
	public TokenList(String configFile) throws InvalidObjectException{
		this(new File(configFile));
	}
	
	public Object[] toArray(){

		return this.table.values().toArray();
	}

	public void addToken(Token t){
		System.out.println("#elementos: " + this.length());
		if (table.containsKey(t.getValue())){
			System.out.println("Actualizando elemento existente");

			table.put(t.getValue(), t);

		}
		else{
			System.out.println("Añadido nuevo elemento");
			table.put(t.getValue(),t);
		}
		System.out.println("NumElementos: " + this.table.size());
	}
	public void removeToken(String key){
		if (!key.equals(IPreferenceConstants.SPECIAL))
			this.table.remove(key);
	}
	public String toString(){
		String value = "";

		Enumeration elems = table.elements();
		while (elems.hasMoreElements())
			value += elems.nextElement().toString();
		return value;
	}
	public int length(){
		return table.size();

	}
	public String toXML(String filepath){
		this.table.remove(IPreferenceConstants.SPECIAL);
		XMLWriter writer = null;
		try{
			writer = new XMLWriter(this.table.elements(), filepath);}
		catch (IOException IOExcept){IOExcept.printStackTrace();}
		writer.writeFile();
		return null;
	}
	public Trie toTrie(){
		Trie ret = new Trie();

		Enumeration elems = table.elements();
		while (elems.hasMoreElements())
			ret.addToken(elems.nextElement());

		return ret;
	}
	public Enumeration getElements(){
		return this.table.elements();	
	}
	public Hashtable getCollection(){
		return this.table;
	}
	public boolean equals(Object obj){
		boolean ret = true;
		try{
			if (((TokenList)obj).length() != this.length())
				ret = false;
			Hashtable enumCmp = (Hashtable)((TokenList)obj).getCollection();
			Enumeration keys = this.table.keys();
			while (keys.hasMoreElements() && ret){
				Object key = keys.nextElement();
				ret = ret && enumCmp.containsKey(key) && enumCmp.get(key).equals(this.table.get(key));
			}
		}catch (ClassCastException e){ret = false;}
		return ret;
	}
	
		
}