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

import org.apache.log4j.Logger;
import org.xml.sax.helpers.DefaultHandler;

import codeshine.preferences.IPreferenceConstants;

/**
 * Clase que permite el funcionamiento de objetos de tipo Token 
 * para manejar eventos de tipo DefaultHandler en listas
 *
 */

public class TokenList extends DefaultHandler{
	private static Logger logger = Logger.getLogger(TokenList.class);
	String[][] configData = {{"/**", "Javadoc string", "Cadena de documentación"},
		{"//", "single line comment","Comentario a una linea"},
			{"/*", "multiline comment", "Comentario de varias lineas"},
			{"*/", "end of comment", "fin de comentario"}};

	final List<Token> list = new ArrayList<Token>();
	final Hashtable<String, Token> table = new Hashtable<String, Token>();
	/**
	 * Crea una lista tokenlist
	 * @param argv Los argumentos que recibe el método
	 */
	public static void main(String[] argv){
			TokenList tl = null;
			try{
				tl = new TokenList("/git/codeshine/codeshine/src/codeshine/speech");
			}catch (InvalidObjectException e){e.printStackTrace();}
		
	}
	/**
	 * Contructor de listas de tipo Token
	 */

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
	 *  Analiza el contenido del archivo especificado fInput
	 * como XML utilizando el archivo org.xml.sax.helpers.DefaultHandler.
	 * 
	 * @param fInput Un objeto de tipo File
	 * @throws InvalidObjectException
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
	/**
	 * Crea un objeto de tipo File dado un pathfile
	 * @param configFile el pathfile
	 * @throws InvalidObjectException
	 */
	
	public TokenList(String configFile) throws InvalidObjectException{
		this(new File(configFile));
	}
	/**
	 * Devuelve un array de objetos
	 * @return Un array de objetos en el mismo orden en el que estaba
	 */
	public Object[] toArray(){

		return this.table.values().toArray();
	}
	/**
	 * Añade un token a la lista
	 * @param t el token que se añade a la lista
	 */
	public void addToken(Token t){
		logger.info("#elementos: " + this.length());
		if (table.containsKey(t.getValue())){
			logger.info("Actualizando elemento existente");
			table.put(t.getValue(), t);
		}
		else{
			logger.info("Añadido nuevo elemento");
			table.put(t.getValue(),t);
		}
		logger.info("NumElementos: " + this.table.size());
	}
	/**
	 * Elimina el token que se especifica en el String key
	 * @param key Especifica el token a eliminar
	 */
	public void removeToken(String key){
		if (!key.equals(IPreferenceConstants.SPECIAL))
			this.table.remove(key);
	}
	/**
	 * Devuelve un string literal del objeto
	 */
	public String toString(){
		String value = "";
		Enumeration<Token> elems = table.elements();
		while (elems.hasMoreElements())
			value += elems.nextElement().toString();
		return value;
	}
	/**
	 * Devuelve la longitud de la TokenList
	 * @return Un int que indica la longitud de la TokenList
	 */
	public int length(){
		return table.size();

	}
	/**
	 * Convierte el String que se le pasa a formato XML
	 * @param filepath el string que queremos convertir
	 * @return Un String en formato XML
	 */
	
	public String toXML(String filepath){
		this.table.remove(IPreferenceConstants.SPECIAL);
		XMLWriter writer = null;
		try{
			writer = new XMLWriter(this.table.elements(), filepath);}
		catch (IOException IOExcept){IOExcept.printStackTrace();}
		writer.writeFile();
		return null;
	}
	/**
	 * 
	 * @return
	 */
	public Trie toTrie(){
		Trie ret = new Trie();
		Enumeration<Token> elems = table.elements();
		while (elems.hasMoreElements())
			ret.addToken(elems.nextElement());
		return ret;
	}
	/**
	 * Indica el numero de elementos en un Hashtable
	 * @return Devuelve un int con el numero de elementos en el hastable
	 */
	public Enumeration<Token> getElements(){
		return this.table.elements();	
	}
	/**
	 * Devuelve una coleccion de objetos de tipo HashTable
	 * @return La coleccion de objetos Hashatble
	 */
	public Hashtable<String, Token> getCollection(){
		return this.table;
	}
	/**
	 * Establece si los objetos Key del Hashtable son iguales
	 * @return True si los objetos Key del Hashtable son iguales o False en sentido contrario
	 */
	public boolean equals(Object obj){
		boolean ret = true;
		try{
			if (((TokenList)obj).length() != this.length())
				ret = false;
			Hashtable<String, Token> enumCmp = (Hashtable<String, Token>)((TokenList)obj).getCollection();
			Enumeration<String> keys = this.table.keys();
			while (keys.hasMoreElements() && ret){
				Object key = keys.nextElement();
				ret = ret && enumCmp.containsKey(key) && enumCmp.get(key).equals(this.table.get(key));
			}
		}catch (ClassCastException e){ret = false;}
		return ret;
	}
	
		
}